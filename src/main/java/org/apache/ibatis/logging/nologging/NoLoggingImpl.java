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
package org.apache.ibatis.logging.nologging;

import org.apache.ibatis.logging.Log;

/**
 * 不需要日志实现
 * @author Clinton Begin
 */
public class NoLoggingImpl implements Log {
  //什么也不做
  public NoLoggingImpl(String clazz) {
    // Do Nothing
  }
  //是否开启debug
  @Override
  public boolean isDebugEnabled() {
    return false;
  }
  //是否开启trace
  @Override
  public boolean isTraceEnabled() {
    return false;
  }
  //是否输出错误信息 ，什么也不做
  @Override
  public void error(String s, Throwable e) {
    // Do Nothing
  }
  //是否输出错误信息 ，什么也不做
  @Override
  public void error(String s) {
    // Do Nothing
  }
  //是否输出debug信息 ，什么也不做
  @Override
  public void debug(String s) {
    // Do Nothing
  }
  //是否输出trace信息 ，什么也不做
  @Override
  public void trace(String s) {
    // Do Nothing
  }
  //是否输出warn信息 ，什么也不做
  @Override
  public void warn(String s) {
    // Do Nothing
  }

}
