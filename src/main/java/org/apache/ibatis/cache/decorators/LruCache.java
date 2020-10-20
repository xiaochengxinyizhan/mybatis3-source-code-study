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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.ibatis.cache.Cache;

/**
 * 最近使用缓存装饰
 * Lru (least recently used) cache decorator.
 *
 * @author Clinton Begin
 */
public class LruCache implements Cache {
  //缓存对象
  private final Cache delegate;
  //键值
  private Map<Object, Object> keyMap;
  //老的key对象
  private Object eldestKey;
  //构造函数设置默认1024
  public LruCache(Cache delegate) {
    this.delegate = delegate;
    setSize(1024);
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
  //设置大小
  public void setSize(final int size) {
    keyMap = new LinkedHashMap<Object, Object>(size, .75F, true) {
      private static final long serialVersionUID = 4267176411845948333L;

      @Override
      protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
        boolean tooBig = size() > size;
        if (tooBig) {
          eldestKey = eldest.getKey();
        }
        return tooBig;
      }
    };
  }
  //存放对象，循环移除key
  @Override
  public void putObject(Object key, Object value) {
    delegate.putObject(key, value);
    cycleKeyList(key);
  }
  //获取对象
  @Override
  public Object getObject(Object key) {
    //触发key+1
    keyMap.get(key); //touch
    return delegate.getObject(key);
  }
   //移除对象
  @Override
  public Object removeObject(Object key) {
    return delegate.removeObject(key);
  }
  //清空缓存和key值集合
  @Override
  public void clear() {
    delegate.clear();
    keyMap.clear();
  }
  //循环移除key值
  private void cycleKeyList(Object key) {
    keyMap.put(key, key);
    if (eldestKey != null) {
      delegate.removeObject(eldestKey);
      eldestKey = null;
    }
  }

}
