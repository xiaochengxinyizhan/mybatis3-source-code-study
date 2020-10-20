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
package org.apache.ibatis.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.io.Resources;

/**
 * 类型别名注册器
 * @author Clinton Begin
 */
public class TypeAliasRegistry {
  //初始化类型别名Map容器
  private final Map<String, Class<?>> typeAliases = new HashMap<>();

  //类构造器
  public TypeAliasRegistry() {
    //字符串类型
    registerAlias("string", String.class);
    //字节类型
    registerAlias("byte", Byte.class);
    //长整型
    registerAlias("long", Long.class);
    //短整型
    registerAlias("short", Short.class);
    //整数型
    registerAlias("int", Integer.class);
    //整数型
    registerAlias("integer", Integer.class);
    //双精度
    registerAlias("double", Double.class);
    //单精度
    registerAlias("float", Float.class);
    //布尔
    registerAlias("boolean", Boolean.class);
    //字节数组
    registerAlias("byte[]", Byte[].class);
    //长整型数组
    registerAlias("long[]", Long[].class);
    //短整型数组
    registerAlias("short[]", Short[].class);
    //整数型数组
    registerAlias("int[]", Integer[].class);
    //整数型包装类数组
    registerAlias("integer[]", Integer[].class);
    //双精度数组
    registerAlias("double[]", Double[].class);
    //单精度数组
    registerAlias("float[]", Float[].class);
    //布尔类型数组
    registerAlias("boolean[]", Boolean[].class);

    //字节类型
    registerAlias("_byte", byte.class);
    //长整型类型
    registerAlias("_long", long.class);
    //短整型类型
    registerAlias("_short", short.class);
    //整数型类型
    registerAlias("_int", int.class);
    //包装类整数类型
    registerAlias("_integer", int.class);
    //双精度类型
    registerAlias("_double", double.class);
    //单精度类型
    registerAlias("_float", float.class);
    //布尔类型
    registerAlias("_boolean", boolean.class);

    //字节数组类型
    registerAlias("_byte[]", byte[].class);
    //长整型数组类型
    registerAlias("_long[]", long[].class);
    //短整型数组类型
    registerAlias("_short[]", short[].class);
    //整数型类型数组
    registerAlias("_int[]", int[].class);
    //包装类整数型数组
    registerAlias("_integer[]", int[].class);
    //双精度数组
    registerAlias("_double[]", double[].class);
    //单精度数组
    registerAlias("_float[]", float[].class);
    //布尔类型数组
    registerAlias("_boolean[]", boolean[].class);

    //日期类型
    registerAlias("date", Date.class);
    //金融计算bigdecimal
    registerAlias("decimal", BigDecimal.class);
    registerAlias("bigdecimal", BigDecimal.class);
    //大整型
    registerAlias("biginteger", BigInteger.class);
    //对象类型
    registerAlias("object", Object.class);

    //日期数组类型
    registerAlias("date[]", Date[].class);
    //bigdecimal类型数组
    registerAlias("decimal[]", BigDecimal[].class);
    registerAlias("bigdecimal[]", BigDecimal[].class);
    //biginteger数组
    registerAlias("biginteger[]", BigInteger[].class);
    //对象数组
    registerAlias("object[]", Object[].class);

    //map数据结构
    registerAlias("map", Map.class);
    //hashmap
    registerAlias("hashmap", HashMap.class);
    //list集合
    registerAlias("list", List.class);
    //数组集合
    registerAlias("arraylist", ArrayList.class);
    //collection集合
    registerAlias("collection", Collection.class);
    //迭代器
    registerAlias("iterator", Iterator.class);
    //返回结果集
    registerAlias("ResultSet", ResultSet.class);
  }

  @SuppressWarnings("unchecked")
  // throws class cast exception as well if types cannot be assigned
  //如果类型未注册则抛出类型转化异常
  public <T> Class<T> resolveAlias(String string) {
    try {
      //如果传入别名为空，则返回为空
      if (string == null) {
        return null;
      }
      // issue #748   https://github.com/mybatis/mybatis-3/issues/748
      String key = string.toLowerCase(Locale.ENGLISH);
      Class<T> value;
      //注册器是否包含该别名
      if (typeAliases.containsKey(key)) {
        //直接获取该别名处理器
        value = (Class<T>) typeAliases.get(key);
      } else {
        //根据资源器通过类名获取
        value = (Class<T>) Resources.classForName(string);
      }
      return value;
    } catch (ClassNotFoundException e) {
      throw new TypeException("Could not resolve type alias '" + string + "'.  Cause: " + e, e);
    }
  }

  /**
   * 注册包下的所有类
   * @param packageName
   */
  public void registerAliases(String packageName) {
    registerAliases(packageName, Object.class);
  }

  public void registerAliases(String packageName, Class<?> superType) {
    ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
    resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
    Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
    for (Class<?> type : typeSet) {
      // Ignore inner classes and interfaces (including package-info.java)
      // Skip also inner classes. See issue #6
      if (!type.isAnonymousClass() && !type.isInterface() && !type.isMemberClass()) {
        registerAlias(type);
      }
    }
  }

  /**
   * 注册别名
   * @param type
   */
  public void registerAlias(Class<?> type) {
    String alias = type.getSimpleName();
    Alias aliasAnnotation = type.getAnnotation(Alias.class);
    if (aliasAnnotation != null) {
      alias = aliasAnnotation.value();
    }
    registerAlias(alias, type);
  }

  /**
   * 注册别名，别名和指定的类
   * @param alias
   * @param value
   */
  public void registerAlias(String alias, Class<?> value) {
    if (alias == null) {
      throw new TypeException("The parameter alias cannot be null");
    }
    // issue #748
    String key = alias.toLowerCase(Locale.ENGLISH);
    if (typeAliases.containsKey(key) && typeAliases.get(key) != null && !typeAliases.get(key).equals(value)) {
      throw new TypeException("The alias '" + alias + "' is already mapped to the value '" + typeAliases.get(key).getName() + "'.");
    }
    typeAliases.put(key, value);
  }

  /**
   * 注册别名，别名和资源加载的类
   * @param alias
   * @param value
   */
  public void registerAlias(String alias, String value) {
    try {
      registerAlias(alias, Resources.classForName(value));
    } catch (ClassNotFoundException e) {
      throw new TypeException("Error registering type alias " + alias + " for " + value + ". Cause: " + e, e);
    }
  }

  /**
   * 获取只读权限的注册容器map
   * @since 3.2.2
   */
  public Map<String, Class<?>> getTypeAliases() {
    return Collections.unmodifiableMap(typeAliases);
  }

}
