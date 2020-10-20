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
package org.apache.ibatis.executor.result;

import org.apache.ibatis.exceptions.PersistenceException;

/**
 * 结果集映射异常
 * @author Ryan Lamore
 */
public class ResultMapException extends PersistenceException {
  private static final long serialVersionUID = 3270932060569707623L;
  //空构造函数
  public ResultMapException() {
  }
  //传递信息构造函数
  public ResultMapException(String message) {
    super(message);
  }
  //传递信息和throwable构造函数
  public ResultMapException(String message, Throwable cause) {
    super(message, cause);
  }
  //结果映射异常
  public ResultMapException(Throwable cause) {
    super(cause);
  }
}
