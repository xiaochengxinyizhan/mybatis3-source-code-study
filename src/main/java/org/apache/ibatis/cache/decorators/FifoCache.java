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

import java.util.Deque;
import java.util.LinkedList;

import org.apache.ibatis.cache.Cache;

/**
 * 先进先出缓存
 * FIFO (first in, first out) cache decorator.
 *
 * @author Clinton Begin
 */
public class FifoCache implements Cache {
  //缓存
  private final Cache delegate;
  //双向队列
  private final Deque<Object> keyList;
  //大小
  private int size;
  //构造函数，大小默认1024
  public FifoCache(Cache delegate) {
    this.delegate = delegate;
    this.keyList = new LinkedList<>();
    this.size = 1024;
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
  public void setSize(int size) {
    this.size = size;
  }
  //存放对象
  @Override
  public void putObject(Object key, Object value) {
    cycleKeyList(key);
    delegate.putObject(key, value);
  }
  //获取对象
  @Override
  public Object getObject(Object key) {
    return delegate.getObject(key);
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
    keyList.clear();
  }
  //循环回收
  private void cycleKeyList(Object key) {
    keyList.addLast(key);
    if (keyList.size() > size) {
      Object oldestKey = keyList.removeFirst();
      delegate.removeObject(oldestKey);
    }
  }

}
