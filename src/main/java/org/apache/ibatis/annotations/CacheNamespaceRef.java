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

/**
 * 引用缓存的注解
 * The annotation that reference a cache.
 * 如果你使用这个注解，应该通过{@link #value()}或者{@link #name()}属性指定。
 * <p>If you use this annotation, should be specified either {@link #value()} or {@link #name()} attribute.
 *
 * <p><br>
 * <b>How to use:</b>
 * <pre>
 * &#064;CacheNamespaceRef(UserMapper.class)
 * public interface AdminUserMapper {
 *   // ...
 * }
 * </pre>
 * @author Clinton Begin
 * @author Kazuki Shimizu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CacheNamespaceRef {

  /**
   * 返回引用缓存的命名空间类型（这个命名空间变成指定类型的名字）
   * Returns the namespace type to reference a cache (the namespace name become a FQCN of specified type).
   *
   * @return the namespace type to reference a cache
   */
  Class<?> value() default void.class;

  /**
   * 返回引用缓存的命名空间
   * Returns the namespace name to reference a cache.
   *
   * @return the namespace name
   * @since 3.4.2
   */
  String name() default "";
}
