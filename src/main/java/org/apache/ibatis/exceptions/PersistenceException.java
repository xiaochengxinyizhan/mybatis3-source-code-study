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
package org.apache.ibatis.exceptions;

/**
 * 用于抑制编译废弃的IbatisException提醒
 * @author Clinton Begin
 */
@SuppressWarnings("deprecation")
public class PersistenceException extends IbatisException {

  private static final long serialVersionUID = -7537395265357977271L;
  //重载空构造函数
  public PersistenceException() {
    super();
  }
 //重载传递消息的构造函数
  public PersistenceException(String message) {
    super(message);
  }
  //重载传递消息和Throwable的构造函数
  public PersistenceException(String message, Throwable cause) {
    super(message, cause);
  }
  //重载Throwable的构造函数
  public PersistenceException(Throwable cause) {
    super(cause);
  }
}
