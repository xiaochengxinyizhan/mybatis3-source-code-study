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
package org.apache.ibatis.executor.result;

import org.apache.ibatis.session.ResultContext;

/**
 * 默认结果上下文
 * @author Clinton Begin
 */
public class DefaultResultContext<T> implements ResultContext<T> {
  //结果对象
  private T resultObject;
  //结果数量
  private int resultCount;
  //是否被停止
  private boolean stopped;
   //默认结果上下文
  public DefaultResultContext() {
    resultObject = null;
    resultCount = 0;
    stopped = false;
  }
  //获取结果对象
  @Override
  public T getResultObject() {
    return resultObject;
  }
  //获取结果数量
  @Override
  public int getResultCount() {
    return resultCount;
  }
  //是否被停止
  @Override
  public boolean isStopped() {
    return stopped;
  }
  //下一个结果对象
  public void nextResultObject(T resultObject) {
    resultCount++;
    this.resultObject = resultObject;
  }
  //停止
  @Override
  public void stop() {
    this.stopped = true;
  }

}
