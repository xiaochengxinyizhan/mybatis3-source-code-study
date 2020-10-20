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

/**
 * 这个注解是 类型鉴别器的条件映射定义
 * The annotation that conditional mapping definition for {@link TypeDiscriminator}.
 *
 * @see TypeDiscriminator
 * @see Result
 * @see Arg
 * @see Results
 * @see ConstructorArgs
 * @author Clinton Begin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Case {

  /**
   * 返回应用这个映射的条件值
   * Return the condition value to apply this mapping.
   *
   * @return the condition value
   */
  String value();

  /**
   * 返回使用这个映射创建对象的对象类型
   * Return the object type that create a object using this mapping.
   *
   * @return the object type
   */
  Class<?> type();

  /**
   * 返回属性的映射定义
   * Return mapping definitions for property.
   *
   * @return mapping definitions for property
   */
  Result[] results() default {};

  /**
   * 返回构造器的映射定义
   * Return mapping definitions for constructor.
   *
   * @return mapping definitions for constructor
   */
  Arg[] constructArgs() default {};

}
