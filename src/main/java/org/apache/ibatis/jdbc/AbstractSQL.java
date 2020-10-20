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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 抽象SQL类
 * @author Clinton Begin
 * @author Jeff Butler
 * @author Adam Gent
 * @author Kazuki Shimizu
 */
public abstract class AbstractSQL<T> {
  //私有属性 And
  private static final String AND = ") \nAND (";
  //私有属性 or
  private static final String OR = ") \nOR (";
  //私有SQL声明
  private final SQLStatement sql = new SQLStatement();

  public abstract T getSelf();
  //更新函数
  public T UPDATE(String table) {
    sql().statementType = SQLStatement.StatementType.UPDATE;
    sql().tables.add(table);
    return getSelf();
  }
  //set字段
  public T SET(String sets) {
    sql().sets.add(sets);
    return getSelf();
  }

  /**
   * set字段
   * @since 3.4.2
   */
  public T SET(String... sets) {
    sql().sets.addAll(Arrays.asList(sets));
    return getSelf();
  }
  //insert函数
  public T INSERT_INTO(String tableName) {
    sql().statementType = SQLStatement.StatementType.INSERT;
    sql().tables.add(tableName);
    return getSelf();
  }
  //插入VALUES关键字
  public T VALUES(String columns, String values) {
    INTO_COLUMNS(columns);
    INTO_VALUES(values);
    return getSelf();
  }

  /**
   * 插入field列
   * @since 3.4.2
   */
  public T INTO_COLUMNS(String... columns) {
    sql().columns.addAll(Arrays.asList(columns));
    return getSelf();
  }

  /**
   * 插入字段
   * @since 3.4.2
   */
  public T INTO_VALUES(String... values) {
    List<String> list = sql().valuesList.get(sql().valuesList.size() - 1);
    Collections.addAll(list, values);
    return getSelf();
  }
  //查询SQL函数
  public T SELECT(String columns) {
    sql().statementType = SQLStatement.StatementType.SELECT;
    sql().select.add(columns);
    return getSelf();
  }

  /**
   * //查询SQL函数
   * @since 3.4.2
   */
  public T SELECT(String... columns) {
    sql().statementType = SQLStatement.StatementType.SELECT;
    sql().select.addAll(Arrays.asList(columns));
    return getSelf();
  }
  //去重字段
  public T SELECT_DISTINCT(String columns) {
    sql().distinct = true;
    SELECT(columns);
    return getSelf();
  }

  /**
   * 去重字段
   * @since 3.4.2
   */
  public T SELECT_DISTINCT(String... columns) {
    sql().distinct = true;
    SELECT(columns);
    return getSelf();
  }
  //删除表
  public T DELETE_FROM(String table) {
    sql().statementType = SQLStatement.StatementType.DELETE;
    sql().tables.add(table);
    return getSelf();
  }
  //from表追加
  public T FROM(String table) {
    sql().tables.add(table);
    return getSelf();
  }

  /**
   * from表追加
   * @since 3.4.2
   */
  public T FROM(String... tables) {
    sql().tables.addAll(Arrays.asList(tables));
    return getSelf();
  }
  //连表
  public T JOIN(String join) {
    sql().join.add(join);
    return getSelf();
  }

  /**
   * 连表
   * @since 3.4.2
   */
  public T JOIN(String... joins) {
    sql().join.addAll(Arrays.asList(joins));
    return getSelf();
  }
  //内连
  public T INNER_JOIN(String join) {
    sql().innerJoin.add(join);
    return getSelf();
  }

  /**
   * 内连表
   * @since 3.4.2
   */
  public T INNER_JOIN(String... joins) {
    sql().innerJoin.addAll(Arrays.asList(joins));
    return getSelf();
  }
  //左连表
  public T LEFT_OUTER_JOIN(String join) {
    sql().leftOuterJoin.add(join);
    return getSelf();
  }

  /**
   * 左连表
   * @since 3.4.2
   */
  public T LEFT_OUTER_JOIN(String... joins) {
    sql().leftOuterJoin.addAll(Arrays.asList(joins));
    return getSelf();
  }
  //右连表
  public T RIGHT_OUTER_JOIN(String join) {
    sql().rightOuterJoin.add(join);
    return getSelf();
  }

  /**
   * 右连表
   * @since 3.4.2
   */
  public T RIGHT_OUTER_JOIN(String... joins) {
    sql().rightOuterJoin.addAll(Arrays.asList(joins));
    return getSelf();
  }
  //外连表
  public T OUTER_JOIN(String join) {
    sql().outerJoin.add(join);
    return getSelf();
  }

  /**
   * 外连表
   * @since 3.4.2
   */
  public T OUTER_JOIN(String... joins) {
    sql().outerJoin.addAll(Arrays.asList(joins));
    return getSelf();
  }
   //追加where条件
  public T WHERE(String conditions) {
    sql().where.add(conditions);
    sql().lastList = sql().where;
    return getSelf();
  }

  /**
   * 追加where条件
   * @since 3.4.2
   */
  public T WHERE(String... conditions) {
    sql().where.addAll(Arrays.asList(conditions));
    sql().lastList = sql().where;
    return getSelf();
  }
  //追加or
  public T OR() {
    sql().lastList.add(OR);
    return getSelf();
  }
  //追加and
  public T AND() {
    sql().lastList.add(AND);
    return getSelf();
  }
  //给group by增加一列属性
  public T GROUP_BY(String columns) {
    sql().groupBy.add(columns);
    return getSelf();
  }

  /**
   * 给group by增加多列排序
   * @since 3.4.2
   */
  public T GROUP_BY(String... columns) {
    sql().groupBy.addAll(Arrays.asList(columns));
    return getSelf();
  }
  //给having增加单独条件属性
  public T HAVING(String conditions) {
    sql().having.add(conditions);
    sql().lastList = sql().having;
    return getSelf();
  }

  /**
   * 给having增加条件
   * @since 3.4.2
   */
  public T HAVING(String... conditions) {
    sql().having.addAll(Arrays.asList(conditions));
    sql().lastList = sql().having;
    return getSelf();
  }
  //给order by增加一列属性
  public T ORDER_BY(String columns) {
    sql().orderBy.add(columns);
    return getSelf();
  }

  /**
   * 给order by增加列字段属性
   * @since 3.4.2
   */
  public T ORDER_BY(String... columns) {
    sql().orderBy.addAll(Arrays.asList(columns));
    return getSelf();
  }

  /**
   * 给#{limit}赋值内容
   * Set the limit variable string(e.g. {@code "#{limit}"}).
   *
   * @param variable a limit variable string
   * @return a self instance
   * @see #OFFSET(String)
   * @since 3.5.2
   */
  public T LIMIT(String variable) {
    sql().limit = variable;
    sql().limitingRowsStrategy = SQLStatement.LimitingRowsStrategy.OFFSET_LIMIT;
    return getSelf();
  }

  /**
   * 设置limit值
   * Set the limit value.
   *
   * @param value an offset value
   * @return a self instance
   * @see #OFFSET(long)
   * @since 3.5.2
   */
  public T LIMIT(int value) {
    return LIMIT(String.valueOf(value));
  }

  /**
   * 给游标#{offset}赋值内容
   * Set the offset variable string(e.g. {@code "#{offset}"}).
   *
   * @param variable a offset variable string
   * @return a self instance
   * @see #LIMIT(String)
   * @since 3.5.2
   */
  public T OFFSET(String variable) {
    sql().offset = variable;
    sql().limitingRowsStrategy = SQLStatement.LimitingRowsStrategy.OFFSET_LIMIT;
    return getSelf();
  }

  /**
   * 设置游标值
   * Set the offset value.
   *
   * @param value an offset value
   * @return a self instance
   * @see #LIMIT(int)
   * @since 3.5.2
   */
  public T OFFSET(long value) {
    return OFFSET(String.valueOf(value));
  }

  /**
   * Set the fetch first rows variable string(e.g. {@code "#{fetchFirstRows}"}).
   * 给fetchFirstRows变量赋值
   * @param variable a fetch first rows variable string
   * @return a self instance
   * @see #OFFSET_ROWS(String)
   * @since 3.5.2
   */
  public T FETCH_FIRST_ROWS_ONLY(String variable) {
    sql().limit = variable;
    sql().limitingRowsStrategy = SQLStatement.LimitingRowsStrategy.ISO;
    return getSelf();
  }

  /**
   * 用于DB2数据库的分页查询返回值
   * Set the fetch first rows value.
   *
   * @param value a fetch first rows value
   * @return a self instance
   * @see #OFFSET_ROWS(long)
   * @since 3.5.2
   */
  public T FETCH_FIRST_ROWS_ONLY(int value) {
    return FETCH_FIRST_ROWS_ONLY(String.valueOf(value));
  }

  /**
   * 用于DB2数据库的分页下标查询
   * Set the offset rows variable string(e.g. {@code "#{offset}"}).
   *
   * @param variable a offset rows variable string
   * @return a self instance
   * @see #FETCH_FIRST_ROWS_ONLY(String)
   * @since 3.5.2
   */
  public T OFFSET_ROWS(String variable) {
    sql().offset = variable;
    sql().limitingRowsStrategy = SQLStatement.LimitingRowsStrategy.ISO;
    return getSelf();
  }

  /**
   * 设置行数查询的下标
   * Set the offset rows value.
   *
   * @param value an offset rows value
   * @return a self instance
   * @see #FETCH_FIRST_ROWS_ONLY(int)
   * @since 3.5.2
   */
  public T OFFSET_ROWS(long value) {
    return OFFSET_ROWS(String.valueOf(value));
  }

  /*
   * used to add a new inserted row while do multi-row insert.
   * 用于多行插入的时候，多添加一行SQL
   * @since 3.5.2
   */
  public T ADD_ROW() {
    sql().valuesList.add(new ArrayList<>());
    return getSelf();
  }

  private SQLStatement sql() {
    return sql;
  }
  //使用usingAppender函数返回原对象
  public <A extends Appendable> A usingAppender(A a) {
    sql().sql(a);
    return a;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sql().sql(sb);
    return sb.toString();
  }
  //静态内部安全可扩展类，
  private static class SafeAppendable {
    //来源jdk，并非线程安全类，由继承者或者实现者来保护接口的安全性
    private final Appendable a;
    private boolean empty = true;
    //实现了SafeAppendable创建时候，封装的内部为Appendable接口对象
    public SafeAppendable(Appendable a) {
      super();
      this.a = a;
    }
    //追加字符串
    public SafeAppendable append(CharSequence s) {
      try {
        if (empty && s.length() > 0) {
          empty = false;
        }
        a.append(s);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return this;
    }
   //判断是否为空对象
    public boolean isEmpty() {
      return empty;
    }

  }
  //静态内部私有类
  private static class SQLStatement {
    //定义内部枚举类声明关键字DML操作
    public enum StatementType {
      DELETE, INSERT, SELECT, UPDATE
    }
    //私有枚举类限制函数策略
    private enum LimitingRowsStrategy {
      //不需要分页或者适用于SQLServer数据库
      NOP {
        @Override
        protected void appendClause(SafeAppendable builder, String offset, String limit) {
          // NOP
        }
      },
     //处理DB2数据库
      ISO {
        @Override
        protected void appendClause(SafeAppendable builder, String offset, String limit) {
          if (offset != null) {
            builder.append(" OFFSET ").append(offset).append(" ROWS");
          }
          if (limit != null) {
            builder.append(" FETCH FIRST ").append(limit).append(" ROWS ONLY");
          }
        }
      },

      //处理mysql
      OFFSET_LIMIT {
        @Override
        protected void appendClause(SafeAppendable builder, String offset, String limit) {
          if (limit != null) {
            builder.append(" LIMIT ").append(limit);
          }
          if (offset != null) {
            builder.append(" OFFSET ").append(offset);
          }
        }
      };

      protected abstract void appendClause(SafeAppendable builder, String offset, String limit);

    }
    //DML关键字类型声明
    StatementType statementType;
    //set字段集合
    List<String> sets = new ArrayList<>();
    //select字段集合
    List<String> select = new ArrayList<>();
    //tables集合
    List<String> tables = new ArrayList<>();
    //join集合
    List<String> join = new ArrayList<>();
    //内连join集合
    List<String> innerJoin = new ArrayList<>();
    //外连join集合
    List<String> outerJoin = new ArrayList<>();
    //左连join集合
    List<String> leftOuterJoin = new ArrayList<>();
    //右连join集合
    List<String> rightOuterJoin = new ArrayList<>();
    //where集合
    List<String> where = new ArrayList<>();
    //having集合
    List<String> having = new ArrayList<>();
    //groupBy集合
    List<String> groupBy = new ArrayList<>();
    //orderBy集合
    List<String> orderBy = new ArrayList<>();
    //
    List<String> lastList = new ArrayList<>();
    //字段列集合
    List<String> columns = new ArrayList<>();
    //字段值集合
    List<List<String>> valuesList = new ArrayList<>();
    //区别boolean值
    boolean distinct;
    //下标
    String offset;
    //行数
    String limit;
    //限制行策略默认不限制
    LimitingRowsStrategy limitingRowsStrategy = LimitingRowsStrategy.NOP;
    //空构造函数初始化字段值
    public SQLStatement() {
      // Prevent Synthetic Access
      valuesList.add(new ArrayList<>());
    }
    //sql约定条款处理builder内容
    private void sqlClause(SafeAppendable builder, String keyword, List<String> parts, String open, String close,
                           String conjunction) {
      //如果关键字不为空
      if (!parts.isEmpty()) {
        //如果构建关键字不为空则追加换行
        if (!builder.isEmpty()) {
          builder.append("\n");
        }
        //构建器追加DML的关键字
        builder.append(keyword);
        //追加空格区分sql语句
        builder.append(" ");
        //追加左边符号
        builder.append(open);
        //封装关键字局部内容
        String last = "________";
        for (int i = 0, n = parts.size(); i < n; i++) {
          String part = parts.get(i);
          if (i > 0 && !part.equals(AND) && !part.equals(OR) && !last.equals(AND) && !last.equals(OR)) {
            builder.append(conjunction);
          }
          builder.append(part);
          last = part;
        }
        //追加右边括号
        builder.append(close);
      }
    }
    //selectSQL
    private String selectSQL(SafeAppendable builder) {
      //如果去重则select拼接去重逻辑
      if (distinct) {
        sqlClause(builder, "SELECT DISTINCT", select, "", "", ", ");
      } else {
        sqlClause(builder, "SELECT", select, "", "", ", ");
      }
      //from拼接
      sqlClause(builder, "FROM", tables, "", "", ", ");
      //连表拼接
      joins(builder);
      //where连接
      sqlClause(builder, "WHERE", where, "(", ")", " AND ");
      //group by连接
      sqlClause(builder, "GROUP BY", groupBy, "", "", ", ");
      //having 连接
      sqlClause(builder, "HAVING", having, "(", ")", " AND ");
      //order by连接
      sqlClause(builder, "ORDER BY", orderBy, "", "", ", ");
      //限制行数策略
      limitingRowsStrategy.appendClause(builder, offset, limit);
      //返回SQL拼接
      return builder.toString();
    }
    //封装连表查询的内容
    private void joins(SafeAppendable builder) {
      sqlClause(builder, "JOIN", join, "", "", "\nJOIN ");
      sqlClause(builder, "INNER JOIN", innerJoin, "", "", "\nINNER JOIN ");
      sqlClause(builder, "OUTER JOIN", outerJoin, "", "", "\nOUTER JOIN ");
      sqlClause(builder, "LEFT OUTER JOIN", leftOuterJoin, "", "", "\nLEFT OUTER JOIN ");
      sqlClause(builder, "RIGHT OUTER JOIN", rightOuterJoin, "", "", "\nRIGHT OUTER JOIN ");
    }
    //插入SQL语句的封装
    private String insertSQL(SafeAppendable builder) {
      sqlClause(builder, "INSERT INTO", tables, "", "", "");
      sqlClause(builder, "", columns, "(", ")", ", ");
      for (int i = 0; i < valuesList.size(); i++) {
        sqlClause(builder, i > 0 ? "," : "VALUES", valuesList.get(i), "(", ")", ", ");
      }
      return builder.toString();
    }
    //删除SQL语句的封装
    private String deleteSQL(SafeAppendable builder) {
      sqlClause(builder, "DELETE FROM", tables, "", "", "");
      sqlClause(builder, "WHERE", where, "(", ")", " AND ");
      limitingRowsStrategy.appendClause(builder, null, limit);
      return builder.toString();
    }
    //更新SQL语句的封装
    private String updateSQL(SafeAppendable builder) {
      sqlClause(builder, "UPDATE", tables, "", "", "");
      joins(builder);
      sqlClause(builder, "SET", sets, "", "", ", ");
      sqlClause(builder, "WHERE", where, "(", ")", " AND ");
      limitingRowsStrategy.appendClause(builder, null, limit);
      return builder.toString();
    }
    //将SQL对象转化为String的SQL语句
    public String sql(Appendable a) {
      SafeAppendable builder = new SafeAppendable(a);
      if (statementType == null) {
        return null;
      }

      String answer;

      switch (statementType) {
        case DELETE:
          answer = deleteSQL(builder);
          break;

        case INSERT:
          answer = insertSQL(builder);
          break;

        case SELECT:
          answer = selectSQL(builder);
          break;

        case UPDATE:
          answer = updateSQL(builder);
          break;

        default:
          answer = null;
      }

      return answer;
    }
  }
}
