/**
 *    Copyright 2009-2018 the original author or authors.
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
 * 已经废弃，使用SQL类代替
 * @deprecated Use the {@link SQL} Class
 *
 * @author Jeff Butler
 */
public class SqlBuilder {
  //线程本地变量
  private static final ThreadLocal<SQL> localSQL = new ThreadLocal<>();
  //静态代码块，调用Begin函数
  static {
    BEGIN();
  }
  //阻止外部实例化
  private SqlBuilder() {
    // Prevent Instantiation
  }
  //调用reset函数进行内容重置
  public static void BEGIN() {
    RESET();
  }
  //重置函数主要实现了线程本地变量的sql对象重新赋值
  public static void RESET() {
    localSQL.set(new SQL());
  }
  //Update更新某个表
  public static void UPDATE(String table) {
    sql().UPDATE(table);
  }
 //set某些字段
  public static void SET(String sets) {
    sql().SET(sets);
  }
  //将SQL对象封装字符串并重置
  public static String SQL() {
    try {
      return sql().toString();
    } finally {
        RESET();
    }
  }
  //INSERT_INTO 某个表
  public static void INSERT_INTO(String tableName) {
    sql().INSERT_INTO(tableName);
  }
  // values值
  public static void VALUES(String columns, String values) {
    sql().VALUES(columns, values);
  }
  //查询
  public static void SELECT(String columns) {
    sql().SELECT(columns);
  }
  //查询去重
  public static void SELECT_DISTINCT(String columns) {
    sql().SELECT_DISTINCT(columns);
  }
  //从表删除
  public static void DELETE_FROM(String table) {
    sql().DELETE_FROM(table);
  }
  //来源某个表
  public static void FROM(String table) {
    sql().FROM(table);
  }
  //连表
  public static void JOIN(String join) {
    sql().JOIN(join);
  }
  //内连
  public static void INNER_JOIN(String join) {
    sql().INNER_JOIN(join);
  }
  //左连
  public static void LEFT_OUTER_JOIN(String join) {
    sql().LEFT_OUTER_JOIN(join);
  }
  //右连
  public static void RIGHT_OUTER_JOIN(String join) {
    sql().RIGHT_OUTER_JOIN(join);
  }
  //外连
  public static void OUTER_JOIN(String join) {
    sql().OUTER_JOIN(join);
  }
  //where关键字
  public static void WHERE(String conditions) {
    sql().WHERE(conditions);
  }
  //or关键字
  public static void OR() {
    sql().OR();
  }
  //and关键字
  public static void AND() {
    sql().AND();
  }
  //group_by关键字
  public static void GROUP_BY(String columns) {
    sql().GROUP_BY(columns);
  }
  //having关键字
  public static void HAVING(String conditions) {
    sql().HAVING(conditions);
  }
  //order by关键字
  public static void ORDER_BY(String columns) {
    sql().ORDER_BY(columns);
  }
  //获取线程本地变量
  private static SQL sql() {
    return localSQL.get();
  }

}
