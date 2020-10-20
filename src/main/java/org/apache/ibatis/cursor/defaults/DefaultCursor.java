/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.cursor.defaults;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetWrapper;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * Cursor的默认实现类，非线程安全的类
 * This is the default implementation of a MyBatis Cursor.
 * This implementation is not thread safe.
 *
 * @author Guillaume Darmont / guillaume@dropinocean.com
 */
public class DefaultCursor<T> implements Cursor<T> {

  // ResultSetHandler stuff
  //数据集处理器
  private final DefaultResultSetHandler resultSetHandler;
  //数据集容器
  private final ResultMap resultMap;
  //数据集包装类
  private final ResultSetWrapper rsw;
  //行边界
  private final RowBounds rowBounds;
  //对象包装结果处理器
  protected final ObjectWrapperResultHandler<T> objectWrapperResultHandler = new ObjectWrapperResultHandler<>();
  //游标迭代器
  private final CursorIterator cursorIterator = new CursorIterator();
  //迭代器恢复标志
  private boolean iteratorRetrieved;
  //游标状态
  private CursorStatus status = CursorStatus.CREATED;
  //行边界初始默认值
  private int indexWithRowBound = -1;

  /**
   * 游标状态枚举值
   */
  private enum CursorStatus {

    /**
     * 新创建的游标，还未开始消费数据集
     * A freshly created cursor, database ResultSet consuming has not started.
     *
     */
    CREATED,
    /**
     * 当前在使用的游标，数据集消费已经开始
     * A cursor currently in use, database ResultSet consuming has started.
     */
    OPEN,
    /**
     * 关闭的游标，没有完全消费完毕
     * A closed cursor, not fully consumed.
     */
    CLOSED,
    /**
     * 完全消费完毕的游标，消费完的游标也是关闭状态
     * A fully consumed cursor, a consumed cursor is always closed.
     */
    CONSUMED
  }

  /**
   *   有参构造器
   * @param resultSetHandler
   * @param resultMap
   * @param rsw
   * @param rowBounds
   */
  public DefaultCursor(DefaultResultSetHandler resultSetHandler, ResultMap resultMap, ResultSetWrapper rsw, RowBounds rowBounds) {
    this.resultSetHandler = resultSetHandler;
    this.resultMap = resultMap;
    this.rsw = rsw;
    this.rowBounds = rowBounds;
  }

  @Override
  public boolean isOpen() {
    return status == CursorStatus.OPEN;
  }

  @Override
  public boolean isConsumed() {
    return status == CursorStatus.CONSUMED;
  }

  /**
   * 获取当前游标下标=行边界的出发点+当前游标迭代器的下标
   * @return
   */
  @Override
  public int getCurrentIndex() {
    return rowBounds.getOffset() + cursorIterator.iteratorIndex;
  }

  /**
   * 实现迭代器，同一个时刻只有一个迭代器，如果该游标已经被消费或者关闭则不再继续迭代数据。
   * @return
   */
  @Override
  public Iterator<T> iterator() {
    if (iteratorRetrieved) {
      throw new IllegalStateException("Cannot open more than one iterator on a Cursor");
    }
    if (isClosed()) {
      throw new IllegalStateException("A Cursor is already closed.");
    }
    iteratorRetrieved = true;
    return cursorIterator;
  }

  /**
   * 实现自动关闭父类的close方法，如果已经关闭，则返回，否则将数据集关闭，并将当前游标置为关闭状态
   */
  @Override
  public void close() {
    if (isClosed()) {
      return;
    }

    ResultSet rs = rsw.getResultSet();
    try {
      if (rs != null) {
        rs.close();
      }
    } catch (SQLException e) {
      // ignore
    } finally {
      status = CursorStatus.CLOSED;
    }
  }

  /**
   * 使用行边界来获取下一批数据，直接从数据库获取数据，并
   * @return
   */
  protected T fetchNextUsingRowBound() {
    T result = fetchNextObjectFromDatabase();
    while (objectWrapperResultHandler.fetched && indexWithRowBound < rowBounds.getOffset()) {
      result = fetchNextObjectFromDatabase();
    }
    return result;
  }

  /**
   * 从数据库拉取下一批对象
   * @return
   */
  protected T fetchNextObjectFromDatabase() {
    //如果游标已经关闭了，直接返回空对象
    if (isClosed()) {
      return null;
    }

    try {
      objectWrapperResultHandler.fetched = false;
      status = CursorStatus.OPEN;
      if (!rsw.getResultSet().isClosed()) {
        //结果集处理器处理行数据
        resultSetHandler.handleRowValues(rsw, resultMap, objectWrapperResultHandler, RowBounds.DEFAULT, null);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    //将对象包装器结果集处理器的结果赋值给next
    T next = objectWrapperResultHandler.result;
    //如果拉取数据成功，则行边界下标累计增加
    if (objectWrapperResultHandler.fetched) {
      indexWithRowBound++;
    }
    // No more object or limit reached
    //如果没有拉取到数据或者总条数已经拉取完毕，则游标状态关闭
    if (!objectWrapperResultHandler.fetched || getReadItemsCount() == rowBounds.getOffset() + rowBounds.getLimit()) {
      close();
      status = CursorStatus.CONSUMED;
    }
    //返回对象结果为空
    objectWrapperResultHandler.result = null;

    return next;
  }

  /**
   * 判断当前游标是否已经被关闭，如果游标状态是关闭或者消费完成则返回true，已经关闭
   * @return
   */
  private boolean isClosed() {
    return status == CursorStatus.CLOSED || status == CursorStatus.CONSUMED;
  }

  /**
   * 获取读的数据项条数
   * @return
   */
  private int getReadItemsCount() {
    return indexWithRowBound + 1;
  }

  /**
   * 对象包装类结果处理器，将结果器上下文数据返回，并标记数据拉取成功。
   * @param <T>
   */
  protected static class ObjectWrapperResultHandler<T> implements ResultHandler<T> {

    protected T result;
    protected boolean fetched;

    @Override
    public void handleResult(ResultContext<? extends T> context) {
      this.result = context.getResultObject();
      context.stop();
      fetched = true;
    }
  }

  /**
   * 游标迭代器
   */
  protected class CursorIterator implements Iterator<T> {

    /**
     * Holder for the next object to be returned.
     * 下一个要返回的对象
     */
    T object;

    /**
     * Index of objects returned using next(), and as such, visible to users.
     * 使用next()函数返回的值，用户见
     */
    int iteratorIndex = -1;

    /**
     * 重写hasNext函数，判断是否对象包装结果处理器已经拉取成功。，如果不成功，则调用fetchNextUsingRowBound()获取数据，否则返回true。
     * @return
     */
    @Override
    public boolean hasNext() {
      if (!objectWrapperResultHandler.fetched) {
        object = fetchNextUsingRowBound();
      }
      return objectWrapperResultHandler.fetched;
    }

    /**
     * 重写next函数，用hasNext（）函数返回的对象填充next
     * @return
     */
    @Override
    public T next() {
      // Fill next with object fetched from hasNext()
      //用hasNext（）函数返回的对象填充next
      T next = object;
      //如果对象包装类处理器已经拉取成功
      if (!objectWrapperResultHandler.fetched) {
        //未拉取成功则需要重新拉取进行next对象填充
        next = fetchNextUsingRowBound();
      }
      //如果拉取数据成功
      if (objectWrapperResultHandler.fetched) {
        //初始化数据，为下次遍历做准备
        objectWrapperResultHandler.fetched = false;
        object = null;
        iteratorIndex++;
        return next;
      }
      //如果没有拉取成功，则直接抛出异常
      throw new NoSuchElementException();
    }

    /**
     * 重写remove方法，不能从当前游标移除元素
     */
    @Override
    public void remove() {
      throw new UnsupportedOperationException("Cannot remove element from Cursor");
    }
  }
}
