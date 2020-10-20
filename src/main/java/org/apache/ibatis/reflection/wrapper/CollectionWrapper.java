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
package org.apache.ibatis.reflection.wrapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

/**
 * 集合包装器
 * @author Clinton Begin
 */
public class CollectionWrapper implements ObjectWrapper {
  //集合对象
  private final Collection<Object> object;
  //集合包装器构造对象
  public CollectionWrapper(MetaObject metaObject, Collection<Object> object) {
    this.object = object;
  }
  //获取属性标记器的对象
  @Override
  public Object get(PropertyTokenizer prop) {
    throw new UnsupportedOperationException();
  }
  //设置属性标记器的值
  @Override
  public void set(PropertyTokenizer prop, Object value) {
    throw new UnsupportedOperationException();
  }
  //找某个属性
  @Override
  public String findProperty(String name, boolean useCamelCaseMapping) {
    throw new UnsupportedOperationException();
  }
  //获取get名称属性数组
  @Override
  public String[] getGetterNames() {
    throw new UnsupportedOperationException();
  }
  //获取set属性名称数组
  @Override
  public String[] getSetterNames() {
    throw new UnsupportedOperationException();
  }
  //根据名称获取set类型
  @Override
  public Class<?> getSetterType(String name) {
    throw new UnsupportedOperationException();
  }
  //根据名称获取get类型
  @Override
  public Class<?> getGetterType(String name) {
    throw new UnsupportedOperationException();
  }
  //是否该名称有set方法
  @Override
  public boolean hasSetter(String name) {
    throw new UnsupportedOperationException();
  }
  //是否该名称有get方法
  @Override
  public boolean hasGetter(String name) {
    throw new UnsupportedOperationException();
  }
  //实例话属性值，抛出不支持的异常
  @Override
  public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
    throw new UnsupportedOperationException();
  }
  //是否是集合
  @Override
  public boolean isCollection() {
    return true;
  }
  //添加元素
  @Override
  public void add(Object element) {
    object.add(element);
  }
  //添加集合
  @Override
  public <E> void addAll(List<E> element) {
    object.addAll(element);
  }

}
