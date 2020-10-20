/**
 *    Copyright 2009-2017 the original author or authors.
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
package org.apache.ibatis.transaction.managed;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;

/**
 * 事务允许容器管理整个事务生命周期，延迟链接获取 直到getConnection()函数被调用。忽略所有提交或者回滚请求。默认关闭链接，但是可以被配置默认不关闭链接
 * {@link Transaction} that lets the container manage the full lifecycle of the transaction.
 * Delays connection retrieval until getConnection() is called.
 * Ignores all commit or rollback requests.
 * By default, it closes the connection but can be configured not to do it.
 *
 * @author Clinton Begin
 *
 * @see ManagedTransactionFactory
 */
public class ManagedTransaction implements Transaction {
  //日志
  private static final Log log = LogFactory.getLog(ManagedTransaction.class);
  //数据源
  private DataSource dataSource;
  //事务隔离级别
  private TransactionIsolationLevel level;
  //链接
  private Connection connection;
  //是否关闭链接
  private final boolean closeConnection;
  //被管理事务构造器
  public ManagedTransaction(Connection connection, boolean closeConnection) {
    this.connection = connection;
    this.closeConnection = closeConnection;
  }
  //被管理事务构造器
  public ManagedTransaction(DataSource ds, TransactionIsolationLevel level, boolean closeConnection) {
    this.dataSource = ds;
    this.level = level;
    this.closeConnection = closeConnection;
  }
  //获取链接
  @Override
  public Connection getConnection() throws SQLException {
    if (this.connection == null) {
      openConnection();
    }
    return this.connection;
  }
  //提交操作，什么也没做
  @Override
  public void commit() throws SQLException {
    // Does nothing
  }
  //回滚操作，什么也没做
  @Override
  public void rollback() throws SQLException {
    // Does nothing
  }
  //关闭链接
  @Override
  public void close() throws SQLException {
    if (this.closeConnection && this.connection != null) {
      if (log.isDebugEnabled()) {
        log.debug("Closing JDBC Connection [" + this.connection + "]");
      }
      this.connection.close();
    }
  }
  //打开链接，数据源获取链接，事务隔离级别部位空 设置事务隔离级别
  protected void openConnection() throws SQLException {
    if (log.isDebugEnabled()) {
      log.debug("Opening JDBC Connection");
    }
    this.connection = this.dataSource.getConnection();
    if (this.level != null) {
      this.connection.setTransactionIsolation(this.level.getLevel());
    }
  }
  //获取超时时间，没有超时
  @Override
  public Integer getTimeout() throws SQLException {
    return null;
  }

}
