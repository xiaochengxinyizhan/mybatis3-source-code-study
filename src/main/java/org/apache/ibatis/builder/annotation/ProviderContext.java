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
package org.apache.ibatis.builder.annotation;

import java.lang.reflect.Method;

/**
 * sql服务方法的上下文对象
 * The context object for sql provider method.
 *
 * @author Kazuki Shimizu
 * @since 3.4.5
 */
public final class ProviderContext {
  //mapper类型
  private final Class<?> mapperType;
  //mapper方法
  private final Method mapperMethod;
  //数据库ID
  private final String databaseId;

  /**
   * 构造函数
   * Constructor.
   *
   * @param mapperType A mapper interface type that specified provider
   * @param mapperMethod A mapper method that specified provider
   * @param databaseId A database id
   */
  ProviderContext(Class<?> mapperType, Method mapperMethod, String databaseId) {
    this.mapperType = mapperType;
    this.mapperMethod = mapperMethod;
    this.databaseId = databaseId;
  }

  /**
   * 获取指定服务的mapper接口
   * Get a mapper interface type that specified provider.
   *
   * @return A mapper interface type that specified provider
   */
  public Class<?> getMapperType() {
    return mapperType;
  }

  /**
   * 获取指定服务的mapper方法
   * Get a mapper method that specified provider.
   *
   * @return A mapper method that specified provider
   */
  public Method getMapperMethod() {
    return mapperMethod;
  }

  /**
   * 从数据库ID服务中获取数据库ID
   * Get a database id that provided from {@link org.apache.ibatis.mapping.DatabaseIdProvider}.
   *
   * @return A database id
   * @since 3.5.1
   */
  public String getDatabaseId() {
    return databaseId;
  }

}
