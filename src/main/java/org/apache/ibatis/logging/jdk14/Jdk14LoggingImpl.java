/**
 *    Copyright 2009-2017 the original author or authors.
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
package org.apache.ibatis.logging.jdk14;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.ibatis.logging.Log;

/**
 * jdk14日志实现类
 * @author Clinton Begin
 */
public class Jdk14LoggingImpl implements Log {

  private final Logger log;
  //构造器
  public Jdk14LoggingImpl(String clazz) {
    log = Logger.getLogger(clazz);
  }
  //是否开启debug形式
  @Override
  public boolean isDebugEnabled() {
    return log.isLoggable(Level.FINE);
  }
  //是否开启trace模式
  @Override
  public boolean isTraceEnabled() {
    return log.isLoggable(Level.FINER);
  }
  //输出错误信息
  @Override
  public void error(String s, Throwable e) {
    log.log(Level.SEVERE, s, e);
  }
  //输出错误信息
  @Override
  public void error(String s) {
    log.log(Level.SEVERE, s);
  }
  //输出debug信息
  @Override
  public void debug(String s) {
    log.log(Level.FINE, s);
  }
  //输出trace信息
  @Override
  public void trace(String s) {
    log.log(Level.FINER, s);
  }
  //输出warn信息
  @Override
  public void warn(String s) {
    log.log(Level.WARNING, s);
  }

}
