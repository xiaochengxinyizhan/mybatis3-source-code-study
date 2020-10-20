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
 * 当@Select方法使用结果处理器的时候，这个注解可以被使用。那些方法一定有不需要返回的类型，因此这个注解可以用来告诉mybatis每行的对象应该是构建成什么样子
 * This annotation can be used when a @Select method is using a
 * ResultHandler.  Those methods must have void return type, so
 * this annotation can be used to tell MyBatis what kind of object
 * it should build for each row.
 *
 * <p><br>
 * <b>How to use:</b>
 * <pre>
 * public interface UserMapper {
 *   &#064;ResultType(User.class)
 *   &#064;Select("SELECT id, name FROM users WHERE name LIKE #{name} || '%' ORDER BY id")
 *   void collectByStartingWithName(String name, ResultHandler&lt;User&gt; handler);
 * }
 * </pre>
 * @since 3.2.0
 * @author Jeff Butler
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultType {
  /**
   * 返回返回类型
   * Returns the return type.
   *
   * @return the return type
   */
  Class<?> value();
}
