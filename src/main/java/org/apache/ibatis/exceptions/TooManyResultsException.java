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
 * 数据结果太多异常
 * @author Clinton Begin
 */
public class TooManyResultsException extends PersistenceException {
  //序列化版本号
  private static final long serialVersionUID = 8935197089745865786L;
  //空构造函数，暂时无人使用
  public TooManyResultsException() {
    super();
  }
  //重载构造函数，根据传递信息调用父类
  public TooManyResultsException(String message) {
    super(message);
  }
  //重载构造函数，根据传递信息和Throwable抛出异常调用父类
  public TooManyResultsException(String message, Throwable cause) {
    super(message, cause);
  }
  //重载构造函数，根据传递Throwable抛出异常信息调用父类
  public TooManyResultsException(Throwable cause) {
    super(cause);
  }
}
