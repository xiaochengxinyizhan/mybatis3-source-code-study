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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * 第二级缓存事务
 * The 2nd level cache transactional buffer.
 * <p>
 *   这个类持有所有缓存对象 被添加到二级缓存的在session期间。实体对象被发送给缓存，当提交被带哦用或者撤销如果这个session被回滚。
 *   阻塞的缓存支持已经被添加的。因此任何的get方法将返回一个缓存错过，将被put跟着执行，依次任何的与key有关系的锁都会被释放。
 * This class holds all cache entries that are to be added to the 2nd level cache during a Session.
 * Entries are sent to the cache when commit is called or discarded if the Session is rolled back.
 * Blocking cache support has been added. Therefore any get() that returns a cache miss
 * will be followed by a put() so any lock associated with the key can be released.
 *
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public class TransactionalCache implements Cache {

  private static final Log log = LogFactory.getLog(TransactionalCache.class);
  //缓存
  private final Cache delegate;
  //清空或者提交
  private boolean clearOnCommit;
  //添加提交的实体对象
  private final Map<Object, Object> entriesToAddOnCommit;
  //在缓存被错过的实体
  private final Set<Object> entriesMissedInCache;
  //构造函数
  public TransactionalCache(Cache delegate) {
    this.delegate = delegate;
    this.clearOnCommit = false;
    this.entriesToAddOnCommit = new HashMap<>();
    this.entriesMissedInCache = new HashSet<>();
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
  //获取对象
  @Override
  public Object getObject(Object key) {
    // issue #116
    Object object = delegate.getObject(key);
    //错过的缓存对象被存放起来
    if (object == null) {
      entriesMissedInCache.add(key);
    }
    // issue #146
    if (clearOnCommit) {
      return null;
    } else {
      return object;
    }
  }
  //存放缓存
  @Override
  public void putObject(Object key, Object object) {
    entriesToAddOnCommit.put(key, object);
  }
  //移除缓存
  @Override
  public Object removeObject(Object key) {
    return null;
  }
  //清空
  @Override
  public void clear() {
    clearOnCommit = true;
    entriesToAddOnCommit.clear();
  }
  //提交
  public void commit() {
    if (clearOnCommit) {
      delegate.clear();
    }
    //刷新等待的实体
    flushPendingEntries();
    //重置事务
    reset();
  }
  //回滚事务
  public void rollback() {
    //未锁定的被错过的实体
    unlockMissedEntries();
    //重置事务
    reset();
  }
  //全部清空
  private void reset() {
    clearOnCommit = false;
    entriesToAddOnCommit.clear();
    entriesMissedInCache.clear();
  }
  //刷新等待的实体
  private void flushPendingEntries() {
    for (Map.Entry<Object, Object> entry : entriesToAddOnCommit.entrySet()) {
      delegate.putObject(entry.getKey(), entry.getValue());
    }
    //将错过的缓存对象，都存放缓存
    for (Object entry : entriesMissedInCache) {
      if (!entriesToAddOnCommit.containsKey(entry)) {
        delegate.putObject(entry, null);
      }
    }
  }
  //未锁定的错过的实体对象被移除缓存
  private void unlockMissedEntries() {
    for (Object entry : entriesMissedInCache) {
      try {
        delegate.removeObject(entry);
      } catch (Exception e) {
        log.warn("Unexpected exception while notifiying a rollback to the cache adapter."
            + "Consider upgrading your cache adapter to the latest version.  Cause: " + e);
      }
    }
  }

}
