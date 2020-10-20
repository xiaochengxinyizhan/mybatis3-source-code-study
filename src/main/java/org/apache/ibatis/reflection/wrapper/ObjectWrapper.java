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
package org.apache.ibatis.reflection.wrapper;

import java.util.List;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

/**
 * 对象包装器
 * @author Clinton Begin
 */
public interface ObjectWrapper {
  //根据属性标记器获取对象
  Object get(PropertyTokenizer prop);
  //设置属性标记器的某个值
  void set(PropertyTokenizer prop, Object value);
  //根据名字和是否使用驼峰来找属性
  String findProperty(String name, boolean useCamelCaseMapping);
  //获取get属性数组
  String[] getGetterNames();
  //获取set属性数组
  String[] getSetterNames();
  //根据名称获取set类型
  Class<?> getSetterType(String name);
  //根据名称获取get类型
  Class<?> getGetterType(String name);
  //该名称是否有set方法
  boolean hasSetter(String name);
  //该名称是否有get方法
  boolean hasGetter(String name);
  //实例化元对象
  MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);
  //是否是集合
  boolean isCollection();
  //添加对象
  void add(Object element);
  //添加集合对象
  <E> void addAll(List<E> element);

}
