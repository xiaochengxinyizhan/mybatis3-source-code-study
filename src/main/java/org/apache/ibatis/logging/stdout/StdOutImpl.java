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
package org.apache.ibatis.logging.stdout;

import org.apache.ibatis.logging.Log;

/**标准输出实现类
 * @author Clinton Begin
 */
public class StdOutImpl implements Log {
  //空构造器
  public StdOutImpl(String clazz) {
    // Do Nothing
  }
 //是否开启debug
  @Override
  public boolean isDebugEnabled() {
    return true;
  }
  //是否开启trace
  @Override
  public boolean isTraceEnabled() {
    return true;
  }
  //输出错误信息，系统输出和栈溢出
  @Override
  public void error(String s, Throwable e) {
    System.err.println(s);
    e.printStackTrace(System.err);
  }
  //错误信息输出
  @Override
  public void error(String s) {
    System.err.println(s);
  }
  //输出debug
  @Override
  public void debug(String s) {
    System.out.println(s);
  }
  //输出trace
  @Override
  public void trace(String s) {
    System.out.println(s);
  }
  //输出warn
  @Override
  public void warn(String s) {
    System.out.println(s);
  }
}
