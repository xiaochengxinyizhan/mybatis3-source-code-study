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
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * 日志缓存
 * @author Clinton Begin
 */
public class LoggingCache implements Cache {
  //日志接口
  private final Log log;
  //缓存
  private final Cache delegate;
  //请求数
  protected int requests = 0;
  //命中数
  protected int hits = 0;
  //缓存构造函数
  public LoggingCache(Cache delegate) {
    this.delegate = delegate;
    this.log = LogFactory.getLog(getId());
  }
  //获取ID
  @Override
  public String getId() {
    return delegate.getId();
  }
  //获取大小
  @Override
  public int getSize() {
    return delegate.getSize();
  }
  //存放缓存
  @Override
  public void putObject(Object key, Object object) {
    delegate.putObject(key, object);
  }
  //获取缓存
  @Override
  public Object getObject(Object key) {
    requests++;
    final Object value = delegate.getObject(key);
    //如果值不为空，命中数+1
    if (value != null) {
      hits++;
    }
    if (log.isDebugEnabled()) {
      log.debug("Cache Hit Ratio [" + getId() + "]: " + getHitRatio());
    }
    return value;
  }
  //移除对象
  @Override
  public Object removeObject(Object key) {
    return delegate.removeObject(key);
  }
  //清除缓存
  @Override
  public void clear() {
    delegate.clear();
  }
  //hash值
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }
  //比较对象
  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }
  //获取命中比例
  private double getHitRatio() {
    return (double) hits / (double) requests;
  }

}
