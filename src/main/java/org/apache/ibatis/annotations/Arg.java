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
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

/**
 * 这个注解是指定一个映射定义为构造参数
 * The annotation that specify a mapping definition for the constructor argument.
 *
 * @see ConstructorArgs
 * @author Clinton Begin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ConstructorArgs.class)
public @interface Arg {

  /**
   * 返回ID列 是或者不是
   * Returns whether id column or not.
   *
   * @return {@code true} if id column; {@code false} if otherwise
   */
  boolean id() default false;

  /**
   * 返回映射这个参数的列名或者列标签
   * Return the column name(or column label) to map to this argument.
   *
   * @return the column name(or column label)
   */
  String column() default "";

  /**
   * 返回这个参数的Java类型
   * Return the java type for this argument.
   *
   * @return the java type
   */
  Class<?> javaType() default void.class;

  /**
   * 返回映射这个参数列的jdbc类型
   * Return the jdbc type for column that map to this argument.
   *
   * @return the jdbc type
   */
  JdbcType jdbcType() default JdbcType.UNDEFINED;

  /**
   * 返回从结果集获取列值对应的类型处理器
   * Returns the {@link TypeHandler} type for retrieving a column value from result set.
   *
   * @return the {@link TypeHandler} type
   */
  Class<? extends TypeHandler> typeHandler() default UnknownTypeHandler.class;

  /**
   * 返回会话ID对于映射这个参数的获取对象
   * Return the statement id for retrieving a object that map to this argument.
   *
   * @return the statement id
   */
  String select() default "";

  /**
   * 返回结果 映射 ID 映射到这个参数的对象
   * Returns the result map id for mapping to a object that map to this argument.
   *
   * @return the result map id
   */
  String resultMap() default "";

  /**
   * 返回正在使用这个映射的 参数名字
   * Returns the parameter name for applying this mapping.
   *
   * @return the parameter name
   * @since 3.4.3
   */
  String name() default "";

  /**
   * 返回当使用 {@link #resultMap()}. 的列前缀
   * Returns the column prefix that use when applying {@link #resultMap()}.
   *
   * @return the column prefix
   * @since 3.5.0
   */
  String columnPrefix() default "";
}
