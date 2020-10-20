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
package org.apache.ibatis.jdbc;

/**
 * 已经废弃，使用SQL类替代
 * @deprecated Use the {@link SQL} Class
 *
 * @author Clinton Begin
 */
@Deprecated
public class SelectBuilder {
  //线程本地变量，为啥用ThreadLocal,是为了解决变量共享问题，可以参考9.2.1
  private static final ThreadLocal<SQL> localSQL = new ThreadLocal<>();
  //静态代码块，包含了BEGIN()，类加载的时候调用
  static {
    BEGIN();
  }
  //私有构造函数，防止被外部实例化
  private SelectBuilder() {
    // Prevent Instantiation
  }
  //调用重置函数
  public static void BEGIN() {
    RESET();
  }
  //重置函数调用了线程本地变量存放了SQL对象
  public static void RESET() {
    localSQL.set(new SQL());
  }
  //封装select关键字段
  public static void SELECT(String columns) {
    sql().SELECT(columns);
  }
 //封装SELECT_DISTINCT关键字段，没有依赖使用方，被select替代了
  public static void SELECT_DISTINCT(String columns) {
    sql().SELECT_DISTINCT(columns);
  }
  //封装FROM关键字段
  public static void FROM(String table) {
    sql().FROM(table);
  }
  //封装JOIN关键字段
  public static void JOIN(String join) {
    sql().JOIN(join);
  }
  //封装INNER_JOIN关键字段
  public static void INNER_JOIN(String join) {
    sql().INNER_JOIN(join);
  }
  //封装LEFT_OUTER_JOIN关键字段，没有依赖使用方
  public static void LEFT_OUTER_JOIN(String join) {
    sql().LEFT_OUTER_JOIN(join);
  }
  //封装RIGHT_OUTER_JOIN关键字段，没有依赖使用方
  public static void RIGHT_OUTER_JOIN(String join) {
    sql().RIGHT_OUTER_JOIN(join);
  }
  //封装OUTER_JOIN关键字段，没有依赖使用方
  public static void OUTER_JOIN(String join) {
    sql().OUTER_JOIN(join);
  }
  //封装WHERE关键字段
  public static void WHERE(String conditions) {
    sql().WHERE(conditions);
  }
  //封装OR关键字段
  public static void OR() {
    sql().OR();
  }
  //封装AND关键字段
  public static void AND() {
    sql().AND();
  }
  //封装GROUP_BY关键字段
  public static void GROUP_BY(String columns) {
    sql().GROUP_BY(columns);
  }
  //封装HAVING关键字段
  public static void HAVING(String conditions) {
    sql().HAVING(conditions);
  }
  //封装ORDER_BY关键字段
  public static void ORDER_BY(String columns) {
    sql().ORDER_BY(columns);
  }
  //将线程本地变量的SQL对象转化为string字符串，并重置线程本地变量设置为空SQL对象
  public static String SQL() {
    try {
      return sql().toString();
    } finally {
      RESET();
    }
  }
  //获取本地线程变量存储的SQL对象
  private static SQL sql() {
    return localSQL.get();
  }

}
