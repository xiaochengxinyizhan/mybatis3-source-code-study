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
package org.apache.ibatis.datasource;

import org.apache.ibatis.exceptions.PersistenceException;

/**
 * 数据源异常，看到异常模块的知道PersistenceException其实封装的是运行异常
 * @author Clinton Begin
 */
public class DataSourceException extends PersistenceException {

  private static final long serialVersionUID = -5251396250407091334L;
  //空构造函数
  public DataSourceException() {
    super();
  }
  //传递信息的构造函数
  public DataSourceException(String message) {
    super(message);
  }
  //传递信息和Throwable的构造函数
  public DataSourceException(String message, Throwable cause) {
    super(message, cause);
  }
  //传递Throwable的构造函数
  public DataSourceException(Throwable cause) {
    super(cause);
  }

}
