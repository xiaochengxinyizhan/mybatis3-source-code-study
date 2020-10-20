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
package org.apache.ibatis.reflection;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BeanWrapper;
import org.apache.ibatis.reflection.wrapper.CollectionWrapper;
import org.apache.ibatis.reflection.wrapper.MapWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

/**
 * 元对象
 * @author Clinton Begin
 */
public class MetaObject {
  //源对象
  private final Object originalObject;
  //对象包装器
  private final ObjectWrapper objectWrapper;
  //对象工厂
  private final ObjectFactory objectFactory;
  //对象包装工厂
  private final ObjectWrapperFactory objectWrapperFactory;
  //反射工厂
  private final ReflectorFactory reflectorFactory;
  //根据入参的构造器
  private MetaObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
    //源对象
    this.originalObject = object;
    //对象工厂
    this.objectFactory = objectFactory;
    //对象包装工厂
    this.objectWrapperFactory = objectWrapperFactory;
    //反射工厂
    this.reflectorFactory = reflectorFactory;
    //包装器赋值 map,collection,bean,object
    if (object instanceof ObjectWrapper) {
      this.objectWrapper = (ObjectWrapper) object;
    } else if (objectWrapperFactory.hasWrapperFor(object)) {
      this.objectWrapper = objectWrapperFactory.getWrapperFor(this, object);
    } else if (object instanceof Map) {
      this.objectWrapper = new MapWrapper(this, (Map) object);
    } else if (object instanceof Collection) {
      this.objectWrapper = new CollectionWrapper(this, (Collection) object);
    } else {
      this.objectWrapper = new BeanWrapper(this, object);
    }
  }
  //获取元对象
  public static MetaObject forObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
    if (object == null) {
      return SystemMetaObject.NULL_META_OBJECT;
    } else {
      return new MetaObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
    }
  }
  //获取对象工厂
  public ObjectFactory getObjectFactory() {
    return objectFactory;
  }
  //获取对象包装器工厂
  public ObjectWrapperFactory getObjectWrapperFactory() {
    return objectWrapperFactory;
  }
  //获取反射工厂
  public ReflectorFactory getReflectorFactory() {
    return reflectorFactory;
  }
  //获取源对象
  public Object getOriginalObject() {
    return originalObject;
  }
  //找到对象包装器的属性
  public String findProperty(String propName, boolean useCamelCaseMapping) {
    return objectWrapper.findProperty(propName, useCamelCaseMapping);
  }
  //获取get属性数组
  public String[] getGetterNames() {
    return objectWrapper.getGetterNames();
  }
  //获取set属性数组
  public String[] getSetterNames() {
    return objectWrapper.getSetterNames();
  }
  //获取set类型
  public Class<?> getSetterType(String name) {
    return objectWrapper.getSetterType(name);
  }
  //获取get类型
  public Class<?> getGetterType(String name) {
    return objectWrapper.getGetterType(name);
  }
  //是否有对应的set属性
  public boolean hasSetter(String name) {
    return objectWrapper.hasSetter(name);
  }
  //是否有对应的get属性
  public boolean hasGetter(String name) {
    return objectWrapper.hasGetter(name);
  }
  //根据属性获取值
  public Object getValue(String name) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
      if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
        return null;
      } else {
        return metaValue.getValue(prop.getChildren());
      }
    } else {
      return objectWrapper.get(prop);
    }
  }
  //给属性赋值
  public void setValue(String name, Object value) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
      if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
        if (value == null) {
          // don't instantiate child path if value is null
          return;
        } else {
          metaValue = objectWrapper.instantiatePropertyValue(name, prop, objectFactory);
        }
      }
      metaValue.setValue(prop.getChildren(), value);
    } else {
      objectWrapper.set(prop, value);
    }
  }
   //某个属性生成元对象
  public MetaObject metaObjectForProperty(String name) {
    Object value = getValue(name);
    return MetaObject.forObject(value, objectFactory, objectWrapperFactory, reflectorFactory);
  }
  //获取对象包装器
  public ObjectWrapper getObjectWrapper() {
    return objectWrapper;
  }
  //是否是集合
  public boolean isCollection() {
    return objectWrapper.isCollection();
  }
  //添加对象元素
  public void add(Object element) {
    objectWrapper.add(element);
  }
  //添加集合
  public <E> void addAll(List<E> list) {
    objectWrapper.addAll(list);
  }

}
