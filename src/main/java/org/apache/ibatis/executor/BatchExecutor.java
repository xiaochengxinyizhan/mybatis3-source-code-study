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
package org.apache.ibatis.executor;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

/**
 * 批量执行器
 * @author Jeff Butler
 */
public class BatchExecutor extends BaseExecutor {
  //批量更新返回值
  public static final int BATCH_UPDATE_RETURN_VALUE = Integer.MIN_VALUE + 1002;
  //会话列表
  private final List<Statement> statementList = new ArrayList<>();
  //批量结果列表
  private final List<BatchResult> batchResultList = new ArrayList<>();
  //当前SQL
  private String currentSql;
  //当前会话
  private MappedStatement currentStatement;
  //批量执行器构造
  public BatchExecutor(Configuration configuration, Transaction transaction) {
    super(configuration, transaction);
  }
  //执行会话操作
  @Override
  public int doUpdate(MappedStatement ms, Object parameterObject) throws SQLException {
    //全局配置对象
    final Configuration configuration = ms.getConfiguration();
    //获取会话处理器
    final StatementHandler handler = configuration.newStatementHandler(this, ms, parameterObject, RowBounds.DEFAULT, null, null);
    //边界SQL
    final BoundSql boundSql = handler.getBoundSql();
    //获取SQL
    final String sql = boundSql.getSql();
    final Statement stmt;
    //如果当前SQL 并且会话是当前会话
    if (sql.equals(currentSql) && ms.equals(currentStatement)) {
      //缩减会话列表集合
      int last = statementList.size() - 1;
      //获取最新的会话
      stmt = statementList.get(last);
      //增加事务会话超时时间
      applyTransactionTimeout(stmt);
      //处理器将会话参数化
      handler.parameterize(stmt);//fix Issues 322
      BatchResult batchResult = batchResultList.get(last);
      //批量结果添加参数对象
      batchResult.addParameterObject(parameterObject);
    } else {
      //获取会话日志链接
      Connection connection = getConnection(ms.getStatementLog());
      //处理会话
      stmt = handler.prepare(connection, transaction.getTimeout());
      //处理会话参数
      handler.parameterize(stmt);    //fix Issues 322
      currentSql = sql;
      currentStatement = ms;
      statementList.add(stmt);
      batchResultList.add(new BatchResult(ms, sql, parameterObject));
    }
    //处理器处理会话
    handler.batch(stmt);
    return BATCH_UPDATE_RETURN_VALUE;
  }
  //执行查询
  @Override
  public <E> List<E> doQuery(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql)
      throws SQLException {
    Statement stmt = null;
    try {
      //刷新会话
      flushStatements();
      //获取全局配置
      Configuration configuration = ms.getConfiguration();
      //构造会话处理器
      StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameterObject, rowBounds, resultHandler, boundSql);
      //创建会话并执行会话查询
      Connection connection = getConnection(ms.getStatementLog());
      stmt = handler.prepare(connection, transaction.getTimeout());
      handler.parameterize(stmt);
      return handler.query(stmt, resultHandler);
    } finally {
      //关闭会话
      closeStatement(stmt);
    }
  }
  //执行查询游标集
  @Override
  protected <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql) throws SQLException {
    //刷新会话
    flushStatements();
    //获取全局配置
    Configuration configuration = ms.getConfiguration();
    //构造会话处理器
    StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, null, boundSql);
    //获取链接
    Connection connection = getConnection(ms.getStatementLog());
    //处理器解析链接获取会话
    Statement stmt = handler.prepare(connection, transaction.getTimeout());
    //处理器执行游标集的查询
    handler.parameterize(stmt);
    Cursor<E> cursor = handler.queryCursor(stmt);
    stmt.closeOnCompletion();
    return cursor;
  }
  //刷新会话
  @Override
  public List<BatchResult> doFlushStatements(boolean isRollback) throws SQLException {
    try {
      //判断是否回滚，回滚返回空结果
      List<BatchResult> results = new ArrayList<>();
      if (isRollback) {
        return Collections.emptyList();
      }
      //遍历会话集合
      for (int i = 0, n = statementList.size(); i < n; i++) {
        //获取会话
        Statement stmt = statementList.get(i);
        //声明会话超时时间
        applyTransactionTimeout(stmt);
        //批量结果获取当前的结果
        BatchResult batchResult = batchResultList.get(i);
        try {
          //设置更新行数
          batchResult.setUpdateCounts(stmt.executeBatch());
          //获取映射的会话
          MappedStatement ms = batchResult.getMappedStatement();
          //获取参数对象
          List<Object> parameterObjects = batchResult.getParameterObjects();
          //主键生成
          KeyGenerator keyGenerator = ms.getKeyGenerator();
           //有key生成
          if (Jdbc3KeyGenerator.class.equals(keyGenerator.getClass())) {
            Jdbc3KeyGenerator jdbc3KeyGenerator = (Jdbc3KeyGenerator) keyGenerator;
            jdbc3KeyGenerator.processBatch(ms, stmt, parameterObjects);
            //咩有key生成
          } else if (!NoKeyGenerator.class.equals(keyGenerator.getClass())) { //issue #141
            for (Object parameter : parameterObjects) {
              keyGenerator.processAfter(this, ms, stmt, parameter);
            }
          }
          // Close statement to close cursor #1109
          //关闭会话为了关闭游标集
          closeStatement(stmt);
        } catch (BatchUpdateException e) {
          StringBuilder message = new StringBuilder();
          message.append(batchResult.getMappedStatement().getId())
              .append(" (batch index #")
              .append(i + 1)
              .append(")")
              .append(" failed.");
          if (i > 0) {
            message.append(" ")
                .append(i)
                .append(" prior sub executor(s) completed successfully, but will be rolled back.");
          }
          throw new BatchExecutorException(message.toString(), e, results, batchResult);
        }
        //添加结果集
        results.add(batchResult);
      }
      //返回结果
      return results;
    } finally {
      //遍历所有会话并关闭会话和清空所有信息
      for (Statement stmt : statementList) {
        closeStatement(stmt);
      }
      currentSql = null;
      statementList.clear();
      batchResultList.clear();
    }
  }

}
