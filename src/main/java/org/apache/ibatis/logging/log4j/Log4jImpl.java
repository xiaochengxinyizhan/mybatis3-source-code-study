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
package org.apache.ibatis.logging.log4j;

import org.apache.ibatis.logging.Log;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * log4j实现类
 * @author Eduardo Macarron
 */
public class Log4jImpl implements Log {

  private static final String FQCN = Log4jImpl.class.getName();
  //log4j.Logger
  private final Logger log;
  //实现类
  public Log4jImpl(String clazz) {
    log = Logger.getLogger(clazz);
  }
  //是否开启debug形式
  @Override
  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }
  //是否开启trace形式
  @Override
  public boolean isTraceEnabled() {
    return log.isTraceEnabled();
  }
  //输出错误信息
  @Override
  public void error(String s, Throwable e) {
    log.log(FQCN, Level.ERROR, s, e);
  }
  //输出错误信息
  @Override
  public void error(String s) {
    log.log(FQCN, Level.ERROR, s, null);
  }
  //输出debug信息
  @Override
  public void debug(String s) {
    log.log(FQCN, Level.DEBUG, s, null);
  }
  //输出trace信息
  @Override
  public void trace(String s) {
    log.log(FQCN, Level.TRACE, s, null);
  }
  //输出warn信息
  @Override
  public void warn(String s) {
    log.log(FQCN, Level.WARN, s, null);
  }

}
