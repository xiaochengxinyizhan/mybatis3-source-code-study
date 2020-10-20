/**
 *    Copyright 2009-2015 the original author or authors.
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
package org.apache.ibatis.reflection;

import org.apache.ibatis.exceptions.PersistenceException;

/**
 * 反射异常与之前的一样，封装了底层的runtime异常
 * @author Clinton Begin
 */
public class ReflectionException extends PersistenceException {

  private static final long serialVersionUID = 7642570221267566591L;
  //空构造
  public ReflectionException() {
    super();
  }
  //传递信息构造
  public ReflectionException(String message) {
    super(message);
  }
  //传递信息和throwable构造
  public ReflectionException(String message, Throwable cause) {
    super(message, cause);
  }
  //传递throwable构造
  public ReflectionException(Throwable cause) {
    super(cause);
  }

}
