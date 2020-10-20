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
package org.apache.ibatis.logging;

/**
 * 日志接口
 * @author Clinton Begin
 */
public interface Log {
  //是否开启debug模式
  boolean isDebugEnabled();
  //是否开启trace模式
  boolean isTraceEnabled();
  //输出错误信息
  void error(String s, Throwable e);
  //输出错误信息
  void error(String s);
  //输出debug信息
  void debug(String s);
  //输出trace信息
  void trace(String s);
  //输出war信息
  void warn(String s);

}
