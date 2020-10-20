/**
 *    Copyright 2009-2017 the original author or authors.
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
package org.apache.ibatis.executor.loader;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.ibatis.executor.ExecutorException;

import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyCopier;
import org.apache.ibatis.reflection.property.PropertyNamer;

/**
 * 抽象增强反序列化代理
 * @author Clinton Begin
 */
public abstract class AbstractEnhancedDeserializationProxy {
  //最后的方法
  protected static final String FINALIZE_METHOD = "finalize";
  //写取代方法
  protected static final String WRITE_REPLACE_METHOD = "writeReplace";
  //类型
  private final Class<?> type;
  //未加载属性
  private final Map<String, ResultLoaderMap.LoadPair> unloadedProperties;
  //对象工厂
  private final ObjectFactory objectFactory;
  //构造参数类型
  private final List<Class<?>> constructorArgTypes;
  //构造参数
  private final List<Object> constructorArgs;
  //重新加载属性锁
  private final Object reloadingPropertyLock;
  //重新加载属性
  private boolean reloadingProperty;
  //构造函数
  protected AbstractEnhancedDeserializationProxy(Class<?> type, Map<String, ResultLoaderMap.LoadPair> unloadedProperties,
          ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
    this.type = type;
    this.unloadedProperties = unloadedProperties;
    this.objectFactory = objectFactory;
    this.constructorArgTypes = constructorArgTypes;
    this.constructorArgs = constructorArgs;
    this.reloadingPropertyLock = new Object();
    this.reloadingProperty = false;
  }
  //执行
  public final Object invoke(Object enhanced, Method method, Object[] args) throws Throwable {
    //获取方法名称
    final String methodName = method.getName();
    try {
      //判断是否是写取代方法
      if (WRITE_REPLACE_METHOD.equals(methodName)) {
        final Object original;
        //对象工厂创建该类型的对象
        if (constructorArgTypes.isEmpty()) {
          original = objectFactory.create(type);
        } else {
          //创建构造类型和构造参数的对象
          original = objectFactory.create(type, constructorArgTypes, constructorArgs);
        }
        //属性拷贝器拷贝
        PropertyCopier.copyBeanProperties(type, enhanced, original);
        //返回新的序列化状态持有器
        return this.newSerialStateHolder(original, unloadedProperties, objectFactory, constructorArgTypes, constructorArgs);
      } else {
        //是否重新加载属性锁 ，加锁
        synchronized (this.reloadingPropertyLock) {
          //属性名根据方法解析属性，属性获取属性key
          if (!FINALIZE_METHOD.equals(methodName) && PropertyNamer.isProperty(methodName) && !reloadingProperty) {
            final String property = PropertyNamer.methodToProperty(methodName);
            final String propertyKey = property.toUpperCase(Locale.ENGLISH);
            //未加载属性包含该属性
            if (unloadedProperties.containsKey(propertyKey)) {
              //从未加载属性移除该属性
              final ResultLoaderMap.LoadPair loadPair = unloadedProperties.remove(propertyKey);
              if (loadPair != null) {
                try {
                  //重新加载属性
                  reloadingProperty = true;
                  loadPair.load(enhanced);
                } finally {
                  //不需要重新加载属性
                  reloadingProperty = false;
                }
              } else {
                //我不确定如果这种场景会确实发生，或者仅仅测试中。我们有一个未读属性，但是没有加载到它。
                /* I'm not sure if this case can really happen or is just in tests -
                 * we have an unread property but no loadPair to load it. */
                throw new ExecutorException("An attempt has been made to read a not loaded lazy property '"
                        + property + "' of a disconnected object");
              }
            }
          }

          return enhanced;
        }
      }
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }
  }
 //抽象方法需要初始化序列化状态持有器
  protected abstract AbstractSerialStateHolder newSerialStateHolder(
          Object userBean,
          Map<String, ResultLoaderMap.LoadPair> unloadedProperties,
          ObjectFactory objectFactory,
          List<Class<?>> constructorArgTypes,
          List<Object> constructorArgs);

}
