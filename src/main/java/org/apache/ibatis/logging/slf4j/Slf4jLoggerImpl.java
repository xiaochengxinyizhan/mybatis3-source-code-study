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
package org.apache.ibatis.logging.slf4j;

import org.apache.ibatis.logging.Log;
import org.slf4j.Logger;

/**
 * 日志实现类
 * @author Eduardo Macarron
 */
class Slf4jLoggerImpl implements Log {
   //slf4j.Logger
  private final Logger log;
  //日志实现类构造器
  public Slf4jLoggerImpl(Logger logger) {
    log = logger;
  }
  //是否开启debug
  @Override
  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }
  //是否开启trace
  @Override
  public boolean isTraceEnabled() {
    return log.isTraceEnabled();
  }
  //是否输出错误信息
  @Override
  public void error(String s, Throwable e) {
    log.error(s, e);
  }
  //输出错误信息
  @Override
  public void error(String s) {
    log.error(s);
  }
  //输出debug信息
  @Override
  public void debug(String s) {
    log.debug(s);
  }
  //输出trace信息
  @Override
  public void trace(String s) {
    log.trace(s);
  }
  //输出warn信息
  @Override
  public void warn(String s) {
    log.warn(s);
  }

}
