/**
 *    Copyright 2009-2016 the original author or authors.
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
package org.apache.ibatis.datasource.jndi;

import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.ibatis.datasource.DataSourceException;
import org.apache.ibatis.datasource.DataSourceFactory;

/**
 * jndi数据源工厂
 * @author Clinton Begin
 */
public class JndiDataSourceFactory implements DataSourceFactory {
  //初始化上下文
  public static final String INITIAL_CONTEXT = "initial_context";
  //数据源
  public static final String DATA_SOURCE = "data_source";
  //环境前缀
  public static final String ENV_PREFIX = "env.";
  //数据源
  private DataSource dataSource;

  /**
   * 实现数据源工厂的设置属性方法
   * @param properties
   */
  @Override
  public void setProperties(Properties properties) {
    try {
      //初始化上下文对象
      InitialContext initCtx;
      //获取环境属性
      Properties env = getEnvProperties(properties);
      if (env == null) {
        //如果环境变量没设置则初始化默认的
        initCtx = new InitialContext();
      } else {
        //否则初始化环境变量下的上下文
        initCtx = new InitialContext(env);
      }
      //如果属性包含初始化上下文并且包含数据源
      if (properties.containsKey(INITIAL_CONTEXT)
          && properties.containsKey(DATA_SOURCE)) {
       // 上下文对象获取对应的上下文
        Context ctx = (Context) initCtx.lookup(properties.getProperty(INITIAL_CONTEXT));
        dataSource = (DataSource) ctx.lookup(properties.getProperty(DATA_SOURCE));
      } else if (properties.containsKey(DATA_SOURCE)) {
        //如果包含数据源，则直接获取
        dataSource = (DataSource) initCtx.lookup(properties.getProperty(DATA_SOURCE));
      }

    } catch (NamingException e) {
      throw new DataSourceException("There was an error configuring JndiDataSourceTransactionPool. Cause: " + e, e);
    }
  }

  /**
   * 获取数据源
   * @return
   */
  @Override
  public DataSource getDataSource() {
    return dataSource;
  }

  /**
   * 获取环境属性
   * @param allProps
   * @return
   */
  private static Properties getEnvProperties(Properties allProps) {
    //环境前缀
    final String PREFIX = ENV_PREFIX;
    //上下文属性对象
    Properties contextProperties = null;
    //遍历所有的k-v配置属性，如果key是以前缀为开始的，则将截断的属性作为key，并封装该环境变量下的所有属性值。
    for (Entry<Object, Object> entry : allProps.entrySet()) {
      String key = (String) entry.getKey();
      String value = (String) entry.getValue();
      if (key.startsWith(PREFIX)) {
        if (contextProperties == null) {
          contextProperties = new Properties();
        }
        contextProperties.put(key.substring(PREFIX.length()), value);
      }
    }
    return contextProperties;
  }

}
