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
package org.apache.ibatis.cache.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;

/**
 * 持久缓存
 * @author Clinton Begin
 */
public class PerpetualCache implements Cache {
  //id
  private final String id;
  //缓存
  private final Map<Object, Object> cache = new HashMap<>();
  //构造函数
  public PerpetualCache(String id) {
    this.id = id;
  }
  //获取ID
  @Override
  public String getId() {
    return id;
  }
  //获取大小
  @Override
  public int getSize() {
    return cache.size();
  }
  //存放对象
  @Override
  public void putObject(Object key, Object value) {
    cache.put(key, value);
  }
  //获取对象
  @Override
  public Object getObject(Object key) {
    return cache.get(key);
  }
  //移除对象
  @Override
  public Object removeObject(Object key) {
    return cache.remove(key);
  }
  //清空缓存
  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public boolean equals(Object o) {
    if (getId() == null) {
      throw new CacheException("Cache instances require an ID.");
    }
    if (this == o) {
      return true;
    }
    if (!(o instanceof Cache)) {
      return false;
    }

    Cache otherCache = (Cache) o;
    return getId().equals(otherCache.getId());
  }

  @Override
  public int hashCode() {
    if (getId() == null) {
      throw new CacheException("Cache instances require an ID.");
    }
    return getId().hashCode();
  }

}
