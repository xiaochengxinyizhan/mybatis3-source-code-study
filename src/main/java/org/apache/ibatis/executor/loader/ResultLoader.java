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
package org.apache.ibatis.executor.loader;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.ResultExtractor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;

/**
 * 结果集加载器
 * @author Clinton Begin
 */
public class ResultLoader {
  //全局配置
  protected final Configuration configuration;
  //执行器
  protected final Executor executor;
  //映射会话
  protected final MappedStatement mappedStatement;
  //参数对象
  protected final Object parameterObject;
  //目标类型
  protected final Class<?> targetType;
  //对象工厂
  protected final ObjectFactory objectFactory;
  //缓存key
  protected final CacheKey cacheKey;
  //执行SQL
  protected final BoundSql boundSql;
  //结果提取器
  protected final ResultExtractor resultExtractor;
  //创建线程ID
  protected final long creatorThreadId;
  //是否被加载
  protected boolean loaded;
  //结果对象
  protected Object resultObject;
  //结果加载构造函数
  public ResultLoader(Configuration config, Executor executor, MappedStatement mappedStatement, Object parameterObject, Class<?> targetType, CacheKey cacheKey, BoundSql boundSql) {
    this.configuration = config;
    this.executor = executor;
    this.mappedStatement = mappedStatement;
    this.parameterObject = parameterObject;
    this.targetType = targetType;
    this.objectFactory = configuration.getObjectFactory();
    this.cacheKey = cacheKey;
    this.boundSql = boundSql;
    this.resultExtractor = new ResultExtractor(configuration, objectFactory);
    this.creatorThreadId = Thread.currentThread().getId();
  }
  //加载结果
  public Object loadResult() throws SQLException {
    List<Object> list = selectList();
    resultObject = resultExtractor.extractObjectFromList(list, targetType);
    return resultObject;
  }
  //查询集合
  private <E> List<E> selectList() throws SQLException {
    Executor localExecutor = executor;
    if (Thread.currentThread().getId() != this.creatorThreadId || localExecutor.isClosed()) {
      localExecutor = newExecutor();
    }
    try {
      //本地执行器查询
      return localExecutor.query(mappedStatement, parameterObject, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER, cacheKey, boundSql);
    } finally {
      //如果本地执行与执行器不一致，则回滚事务
      if (localExecutor != executor) {
        localExecutor.close(false);
      }
    }
  }
  //初始化执行器
  private Executor newExecutor() {
    //获取环境配置
    final Environment environment = configuration.getEnvironment();
    if (environment == null) {
      throw new ExecutorException("ResultLoader could not load lazily.  Environment was not configured.");
    }
    //获取数据源
    final DataSource ds = environment.getDataSource();
    if (ds == null) {
      throw new ExecutorException("ResultLoader could not load lazily.  DataSource was not configured.");
    }
    //获取事务工厂
    final TransactionFactory transactionFactory = environment.getTransactionFactory();
    //构造事务
    final Transaction tx = transactionFactory.newTransaction(ds, null, false);
    //全局配置构建执行器 执行本次事务
    return configuration.newExecutor(tx, ExecutorType.SIMPLE);
  }
  //是否结果为空
  public boolean wasNull() {
    return resultObject == null;
  }

}
