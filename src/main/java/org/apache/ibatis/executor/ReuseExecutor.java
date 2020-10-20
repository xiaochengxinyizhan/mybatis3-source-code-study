/**
 *    Copyright 2009-2018 the original author or authors.
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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

/**
 * 重用执行器
 * @author Clinton Begin
 */
public class ReuseExecutor extends BaseExecutor {
  //会话容器
  private final Map<String, Statement> statementMap = new HashMap<>();
  //重用执行器构造函数
  public ReuseExecutor(Configuration configuration, Transaction transaction) {
    super(configuration, transaction);
  }
  //执行会话更新操作
  @Override
  public int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
    Configuration configuration = ms.getConfiguration();
    StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null, null);
    Statement stmt = prepareStatement(handler, ms.getStatementLog());
    return handler.update(stmt);
  }
  //执行查询操作
  @Override
  public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
    Configuration configuration = ms.getConfiguration();
    StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
    Statement stmt = prepareStatement(handler, ms.getStatementLog());
    return handler.query(stmt, resultHandler);
  }
  //执行查询游标集
  @Override
  protected <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql) throws SQLException {
    Configuration configuration = ms.getConfiguration();
    StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, null, boundSql);
    Statement stmt = prepareStatement(handler, ms.getStatementLog());
    return handler.queryCursor(stmt);
  }
  //做刷新会话操作
  @Override
  public List<BatchResult> doFlushStatements(boolean isRollback) {
    //关闭会话容器的所有会话
    for (Statement stmt : statementMap.values()) {
      closeStatement(stmt);
    }
    //清空会话容器
    statementMap.clear();
    //返回空集合
    return Collections.emptyList();
  }
  //预会话构造
  private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
    Statement stmt;
    //获取边界SQL
    BoundSql boundSql = handler.getBoundSql();
    String sql = boundSql.getSql();
    //判断是否有处理该SQL的会话
    if (hasStatementFor(sql)) {
      //获取会话
      stmt = getStatement(sql);
      //增加事务超时时间
      applyTransactionTimeout(stmt);
    } else {
      //获取会话日志链接
      Connection connection = getConnection(statementLog);
      //准备会话
      stmt = handler.prepare(connection, transaction.getTimeout());
      //本地存储会话
      putStatement(sql, stmt);
    }
    //处理器参数化 会话
    handler.parameterize(stmt);
    return stmt;
  }
  //sql是否有对应的会话
  private boolean hasStatementFor(String sql) {
    try {
      return statementMap.keySet().contains(sql) && !statementMap.get(sql).getConnection().isClosed();
    } catch (SQLException e) {
      return false;
    }
  }
  //会话容器获取会话
  private Statement getStatement(String s) {
    return statementMap.get(s);
  }
  //存放会话到会话容器
  private void putStatement(String sql, Statement stmt) {
    statementMap.put(sql, stmt);
  }

}
