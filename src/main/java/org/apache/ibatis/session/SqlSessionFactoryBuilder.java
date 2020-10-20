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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

/**
 * 构建{@link SqlSession}实例
 * Builds {@link SqlSession} instances.
 *
 * @author Clinton Begin
 */
public class SqlSessionFactoryBuilder {
  //根据字符流构建会话工厂
  public SqlSessionFactory build(Reader reader) {
    return build(reader, null, null);
  }
  //根据字符流和环境构建会话工厂
  public SqlSessionFactory build(Reader reader, String environment) {
    return build(reader, environment, null);
  }
  //根据字符流和属性构建会话工厂
  public SqlSessionFactory build(Reader reader, Properties properties) {
    return build(reader, null, properties);
  }
  //根据字符流和环境和属性构建会话工厂
  public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
    try {
      //利用xml配置构建器专门解析MyBatis的配置文件
      XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);
      //调用重载函数，build入参数为parser.parse()解析后的configuration对象
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        reader.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }
  //根据字节流构建会话工厂
  public SqlSessionFactory build(InputStream inputStream) {
    return build(inputStream, null, null);
  }
  //根据字节流和环境构建会话工厂
  public SqlSessionFactory build(InputStream inputStream, String environment) {
    return build(inputStream, environment, null);
  }
  //根据字节流和属性文件构建会话工厂
  public SqlSessionFactory build(InputStream inputStream, Properties properties) {
    return build(inputStream, null, properties);
  }
  //构建会话工厂
  public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    try {
      //解析字节流和环境和属性文件
      XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        inputStream.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }
  //根据全局配置构建会话工厂，走默认的会话工厂
  public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config);
  }

}
