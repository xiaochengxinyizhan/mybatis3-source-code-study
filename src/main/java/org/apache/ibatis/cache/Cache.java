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
package org.apache.ibatis.cache;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * 缓存服务的SPI
 * SPI for cache providers.
 * 为每个命名空间创建一个缓存实例
 * <p>
 * One instance of cache will be created for each namespace.
 *缓存实现必须有一个将缓存id作为字符串参数接收的构造函数
 * <p>
 * The cache implementation must have a constructor that receives the cache id as an String parameter.
 * Mybatis将传递命名空间作为ID给构造函数
 * <p>
 * MyBatis will pass the namespace as id to the constructor.
 *
 * <pre>
 * public MyCache(final String id) {
 *  if (id == null) {
 *    throw new IllegalArgumentException("Cache instances require an ID");
 *  }
 *  this.id = id;
 *  initialize();
 * }
 * </pre>
 *
 * @author Clinton Begin
 */

public interface Cache {

  /**
   * 返回缓存的ID
   * @return The identifier of this cache
   */
  String getId();

  /**
   * 存放对象
   * @param key Can be any object but usually it is a {@link CacheKey}
   * @param value The result of a select.
   */
  void putObject(Object key, Object value);

  /**
   * 获取缓存的对象
   * @param key The key
   * @return The object stored in the cache.
   */
  Object getObject(Object key);

  /**
   * 从3.3.0版本开始i，这个方法仅仅是在回滚在缓存丢失的一些旧值被调用。
   * 可以使先前放进key的一些阻塞缓存释放锁
   * 阻塞的缓存 当值为空 放一把锁，并且发布它 当值再次回来
   * 这种方式，其他线程将这种值作为有效值而不是等待击中数据库的
   * As of 3.3.0 this method is only called during a rollback
   * for any previous value that was missing in the cache.
   * This lets any blocking cache to release the lock that
   * may have previously put on the key.
   * A blocking cache puts a lock when a value is null
   * and releases it when the value is back again.
   * This way other threads will wait for the value to be
   * available instead of hitting the database.
   *
   *
   * @param key The key
   * @return Not used
   */
  Object removeObject(Object key);

  /**
   * 清除这个缓存实例
   * Clears this cache instance.
   */
  void clear();

  /**
   * 可选，这个方法不会被核心调用
   * Optional. This method is not called by the core.
   *
   * @return The number of elements stored in the cache (not its capacity).
   */
  int getSize();

  /**
   * 可选，从3.2.6版本开始，这个方法不会被核心调用
   * Optional. As of 3.2.6 this method is no longer called by the core.
   * <p>
   * Any locking needed by the cache must be provided internally by the cache provider.
   *
   * @return A ReadWriteLock
   */
  default ReadWriteLock getReadWriteLock() {
    return null;
  }

}
