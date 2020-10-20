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
package org.apache.ibatis.jdbc;

/**
 * 运行SQL异常
 * @author Clinton Begin
 */
public class RuntimeSqlException extends RuntimeException {

  private static final long serialVersionUID = 5224696788505678598L;
  //重载空构造函数
  public RuntimeSqlException() {
    super();
  }
  //重载传递信息的构造函数
  public RuntimeSqlException(String message) {
    super(message);
  }
  //重载传递信息和Throwable的构造函数
  public RuntimeSqlException(String message, Throwable cause) {
    super(message, cause);
  }
  //重载Throwable的构造函数
  public RuntimeSqlException(Throwable cause) {
    super(cause);
  }

}
