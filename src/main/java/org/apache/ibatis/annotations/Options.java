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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.StatementType;

/**
 * 指定定制默认行为的的可选项注解
 * The annotation that specify options for customizing default behaviors.
 *
 * <p><br>
 * <b>How to use:</b>
 * <pre>
 * public interface UserMapper {
 *   &#064;Option(useGeneratedKeys = true, keyProperty = "id")
 *   &#064;Insert("INSERT INTO users (name) VALUES(#{name})")
 *   boolean insert(User user);
 * }
 * </pre>
 * @author Clinton Begin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Options {
  /**
   * 刷新缓存的可选项。默认策略
   * The options for the {@link Options#flushCache()}.
   * The default is {@link FlushCachePolicy#DEFAULT}
   */
  enum FlushCachePolicy {
    //查询会话 默认false，插入，更新，删除 默认是true
    /** <code>false</code> for select statement; <code>true</code> for insert/update/delete statement. */
    DEFAULT,
    //无视会话类型 刷新缓存
    /** Flushes cache regardless of the statement type. */
    TRUE,
    //无视会话类型  不刷新缓存
    /** Does not flush cache regardless of the statement type. */
    FALSE
  }

  /**
   * 如果分配缓存返回是否使用二级缓存
   * Returns whether use the 2nd cache feature if assigned the cache.
   *
   * @return {@code true} if use; {@code false} if otherwise
   */
  boolean useCache() default true;

  /**返回二级缓存刷新策略
   * Returns the 2nd cache flush strategy.
   *
   * @return the 2nd cache flush strategy
   */
  FlushCachePolicy flushCache() default FlushCachePolicy.DEFAULT;

  /**
   * 返回结果集类型
   * Returns the result set type.
   *
   * @return the result set type
   */
  ResultSetType resultSetType() default ResultSetType.DEFAULT;

  /**
   * 返回会话类型
   * Return the statement type.
   *
   * @return the statement type
   */
  StatementType statementType() default StatementType.PREPARED;

  /**
   * 返回拉取数量
   * Returns the fetch size.
   *
   * @return the fetch size
   */
  int fetchSize() default -1;

  /**
   * 返回会话超时时间
   * Returns the statement timeout.
   * @return the statement timeout
   */
  int timeout() default -1;

  /**
   * 返回是否使用生成的key,要求：3.0版本jdbc
   * Returns whether use the generated keys feature supported by JDBC 3.0
   *
   * @return {@code true} if use; {@code false} if otherwise
   */
  boolean useGeneratedKeys() default false;

  /**
   * 返回持有key值的属性名字以逗号分割开
   * Returns property names that holds a key value.
   * <p>
   * If you specify multiple property, please separate using comma(',').
   * </p>
   *
   * @return property names that separate with comma(',')
   */
  String keyProperty() default "";

  /**
   * 返回检索key值的列名字
   * Returns column names that retrieves a key value.
   * <p>
   * If you specify multiple column, please separate using comma(',').
   * </p>
   *
   * @return column names that separate with comma(',')
   */
  String keyColumn() default "";

  /**
   * 返回结果集名字，如果多个 以逗号分开返回
   * Returns result set names.
   * <p>
   * If you specify multiple result set, please separate using comma(',').
   * </p>
   *
   * @return result set names that separate with comma(',')
   */
  String resultSets() default "";
}
