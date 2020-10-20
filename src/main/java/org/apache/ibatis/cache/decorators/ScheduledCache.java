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
package org.apache.ibatis.cache.decorators;

import org.apache.ibatis.cache.Cache;

import java.util.concurrent.TimeUnit;

/**
 * 定时缓存
 * @author Clinton Begin
 */
public class ScheduledCache implements Cache {
  //缓存
  private final Cache delegate;
  //清除间隔
  protected long clearInterval;
  //上次清理时间
  protected long lastClear;
  //构造函数
  public ScheduledCache(Cache delegate) {
    this.delegate = delegate;
    this.clearInterval = TimeUnit.HOURS.toMillis(1);
    this.lastClear = System.currentTimeMillis();
  }
  //设置清除间隔时间
  public void setClearInterval(long clearInterval) {
    this.clearInterval = clearInterval;
  }
  //获取id
  @Override
  public String getId() {
    return delegate.getId();
  }
  //获取大小
  @Override
  public int getSize() {
    clearWhenStale();
    return delegate.getSize();
  }
  //存放对象
  @Override
  public void putObject(Object key, Object object) {
    clearWhenStale();
    delegate.putObject(key, object);
  }
  //获取对象
  @Override
  public Object getObject(Object key) {
    return clearWhenStale() ? null : delegate.getObject(key);
  }
  //移除对象
  @Override
  public Object removeObject(Object key) {
    clearWhenStale();
    return delegate.removeObject(key);
  }
  //清空缓存
  @Override
  public void clear() {
    lastClear = System.currentTimeMillis();
    delegate.clear();
  }
  //hashcode
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }
  //比较对象
  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }
  //定时清理任务
  private boolean clearWhenStale() {
    if (System.currentTimeMillis() - lastClear > clearInterval) {
      clear();
      return true;
    }
    return false;
  }

}
