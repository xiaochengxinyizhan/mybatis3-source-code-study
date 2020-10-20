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
package org.apache.ibatis.session;

import java.sql.Connection;

/**
 * 从连接或数据源创建{@link SqlSession}
 * Creates an {@link SqlSession} out of a connection or a DataSource
 *
 * @author Clinton Begin
 */
public interface SqlSessionFactory {
  //打开会话
  SqlSession openSession();
  //打开会话是否自动提交
  SqlSession openSession(boolean autoCommit);
  //打开链接的session
  SqlSession openSession(Connection connection);
  //打开事务隔离级别的session
  SqlSession openSession(TransactionIsolationLevel level);
  //打开执行器类型的session
  SqlSession openSession(ExecutorType execType);
  //打开执行器类型，是否自动提交的session
  SqlSession openSession(ExecutorType execType, boolean autoCommit);
  //打开执行器和事务隔离级别机制的session
  SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level);
  //打开执行器，链接的session
  SqlSession openSession(ExecutorType execType, Connection connection);
  //获取全局配置
  Configuration getConfiguration();

}
