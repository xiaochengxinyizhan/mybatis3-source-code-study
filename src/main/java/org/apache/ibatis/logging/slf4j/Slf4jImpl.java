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
package org.apache.ibatis.logging.slf4j;

import org.apache.ibatis.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;

/**
 * Slf4j实现代理类
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public class Slf4jImpl implements Log {
//mybatis接口
  private Log log;

  public Slf4jImpl(String clazz) {
    Logger logger = LoggerFactory.getLogger(clazz);

    if (logger instanceof LocationAwareLogger) {
      try {
        // slf4j >= 1.6的签名
        // check for slf4j >= 1.6 method signature
        logger.getClass().getMethod("log", Marker.class, String.class, int.class, String.class, Object[].class, Throwable.class);
        log = new Slf4jLocationAwareLoggerImpl((LocationAwareLogger) logger);
        return;
      } catch (SecurityException | NoSuchMethodException e) {
        // fail-back to Slf4jLoggerImpl
      }
    }
    // 没有LocationAwareLogger或者版本小于1.6
    // Logger is not LocationAwareLogger or slf4j version < 1.6
    log = new Slf4jLoggerImpl(logger);
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
  //输出错误信息
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
