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
 * 指定属性的映射定义注解
 * The annotation that specify a mapping definition for the property.
 *
 * @see Results
 * @author Clinton Begin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Results.class)
public @interface Result {
  /**
   * 返回是否ID列或者不是
   * Returns whether id column or not.
   *
   * @return {@code true} if id column; {@code false} if otherwise
   */
  boolean id() default false;

  /**
   * 返回映射到这个参数的列名
   * Return the column name(or column label) to map to this argument.
   *
   * @return the column name(or column label)
   */
  String column() default "";

  /**
   * 返回使用这个映射的属性名字
   * Returns the property name for applying this mapping.
   *
   * @return the property name
   */
  String property() default "";

  /**
   * 返回参数的Java类型
   * Return the java type for this argument.
   *
   * @return the java type
   */
  Class<?> javaType() default void.class;

  /**
   * 返回映射到这个参数的列的jdbc类型
   * Return the jdbc type for column that map to this argument.
   *
   * @return the jdbc type
   */
  JdbcType jdbcType() default JdbcType.UNDEFINED;

  /**
   * 返回检索一个列值从结果集的类型处理器
   * Returns the {@link TypeHandler} type for retrieving a column value from result set.
   *
   * @return the {@link TypeHandler} type
   */
  Class<? extends TypeHandler> typeHandler() default UnknownTypeHandler.class;

  /**
   * 返回单个关系的映射定义
   * Returns the mapping definition for single relationship.
   *
   * @return the mapping definition for single relationship
   */
  One one() default @One;

  /**
   * 返回集合关系的映射定义
   * Returns the mapping definition for collection relationship.
   *
   * @return the mapping definition for collection relationship
   */
  Many many() default @Many;
}
