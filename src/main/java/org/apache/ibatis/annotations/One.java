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
package org.apache.ibatis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.ibatis.mapping.FetchType;

/**
 * 指定检索单个对象的嵌套会话的注解
 * The annotation that specify the nested statement for retrieving single object.
 *
 * @see Result
 * @see Results
 * @author Clinton Begin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface One {
  /**
   * 返回检索单个对象的会话ID
   * Returns the statement id that retrieves single object.
   *
   * @return the statement id
   */
  String select() default "";

  /**
   * 返回嵌套会话的拉取策略
   * Returns the fetch strategy for nested statement.
   *
   * @return the fetch strategy
   */
  FetchType fetchType() default FetchType.DEFAULT;

}
