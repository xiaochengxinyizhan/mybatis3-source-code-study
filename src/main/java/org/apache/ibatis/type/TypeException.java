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
package org.apache.ibatis.type;

import org.apache.ibatis.exceptions.PersistenceException;

/**
 * 类型异常，之前在异常的包 解释过PersistenceException 继承 已经废弃的IbatisException 继承 RuntimeException
 * 所以从根本上讲继承了运行异常，但是统一外部异常父类PersistenceException
 * @author Clinton Begin
 */
public class TypeException extends PersistenceException {

  private static final long serialVersionUID = 8614420898975117130L;
  //空构造器
  public TypeException() {
    super();
  }
 //传递信息构造器
  public TypeException(String message) {
    super(message);
  }
 //传递信息和Throwable的构造器
  public TypeException(String message, Throwable cause) {
    super(message, cause);
  }
  //传递Throwable的构造器
  public TypeException(Throwable cause) {
    super(cause);
  }

}
