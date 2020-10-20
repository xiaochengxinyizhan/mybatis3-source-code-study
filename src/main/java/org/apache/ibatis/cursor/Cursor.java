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
package org.apache.ibatis.cursor;

import java.io.Closeable;

/**
 * Cursor contract to handle fetching items lazily using an Iterator.
 * Cursors are a perfect fit to handle millions of items queries that would not normally fits in memory.
 * If you use collections in resultMaps then cursor SQL queries must be ordered (resultOrdered="true")
 * using the id columns of the resultMap.
 *
 * @author Guillaume Darmont / guillaume@dropinocean.com
 */
//游标继承Closeable接口，这个接口只有一个close方法需要实现，一般用于资源需要手动关闭情况，各种io流之类这里是结果集（ResultSet需要关闭，所以继承这个接口）
//  继承Iterable接口，游标对结果集（ResultSet）进行遍历功能，需要实现一个迭代器的功能。
public interface Cursor<T> extends Closeable, Iterable<T> {

  /**
   * 如果cursor已经开始从数据库拉去游标项则返回true
   * @return true if the cursor has started to fetch items from database.
   */
  boolean isOpen();

  /**
   *如果将匹配的查询数据已经全部返回，并消费完毕则返回true
   * @return true if the cursor is fully consumed and has returned all elements matching the query.
   */
  boolean isConsumed();

  /**
   * 获取当前游标项的下标，第一个游标项下标为0
   *  如果未检索到第一个游标项，则返回-1
   * Get the current item index. The first item has the index 0.
   * @return -1 if the first cursor item has not been retrieved. The index of the current item retrieved.
   */
  int getCurrentIndex();
}
