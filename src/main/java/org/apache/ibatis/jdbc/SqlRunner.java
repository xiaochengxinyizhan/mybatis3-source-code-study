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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * SQL管理器
 * @author Clinton Begin
 */
public class SqlRunner {
  //不生成key
  public static final int NO_GENERATED_KEY = Integer.MIN_VALUE + 1001;
  //jdbc链接器
  private final Connection connection;
  //类型处理注册器
  private final TypeHandlerRegistry typeHandlerRegistry;
  //是否生成主键key
  private boolean useGeneratedKeySupport;
  //脚本管理器 构造函数 赋值链接器和类型处理注册器
  public SqlRunner(Connection connection) {
    this.connection = connection;
    this.typeHandlerRegistry = new TypeHandlerRegistry();
  }
  //设置是否生成主键key
  public void setUseGeneratedKeySupport(boolean useGeneratedKeySupport) {
    this.useGeneratedKeySupport = useGeneratedKeySupport;
  }

  /**
   * 执行Select查询会话并返回单条数据，如果返回多条抛出异常
   * Executes a SELECT statement that returns one row.
   *
   * @param sql  The SQL
   * @param args The arguments to be set on the statement.
   * @return The row expected.
   * @throws SQLException If less or more than one row is returned
   */
  public Map<String, Object> selectOne(String sql, Object... args) throws SQLException {
    List<Map<String, Object>> results = selectAll(sql, args);
    if (results.size() != 1) {
      throw new SQLException("Statement returned " + results.size() + " results where exactly one (1) was expected.");
    }
    return results.get(0);
  }

  /**
   * 执行select 查询并返回多行数据
   * Executes a SELECT statement that returns multiple rows.
   *
   * @param sql  The SQL
   * @param args The arguments to be set on the statement.
   * @return The list of rows expected.
   * @throws SQLException If statement preparation or execution fails
   */
  public List<Map<String, Object>> selectAll(String sql, Object... args) throws SQLException {
    //建立预会话
    PreparedStatement ps = connection.prepareStatement(sql);
    try {
      //设置参数
      setParameters(ps, args);
      //执行会话查询
      ResultSet rs = ps.executeQuery();
      //返回查询的结果集
      return getResults(rs);
    } finally {
      try {
        //会话关闭
        ps.close();
      } catch (SQLException e) {
        //ignore
      }
    }
  }

  /**
   * 执行insert插入会话
   * Executes an INSERT statement.
   *
   * @param sql  The SQL
   * @param args The arguments to be set on the statement.
   * @return The number of rows impacted or BATCHED_RESULTS if the statements are being batched.
   * @throws SQLException If statement preparation or execution fails
   */
  public int insert(String sql, Object... args) throws SQLException {
    PreparedStatement ps;
    //判断是否需要生成主键ID
    if (useGeneratedKeySupport) {
      ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    } else {
      ps = connection.prepareStatement(sql);
    }

    try {
      //设置参数
      setParameters(ps, args);
      //执行更新操作
      ps.executeUpdate();
      //如果支持生成主键ID
      if (useGeneratedKeySupport) {
        //获取生成的key
        List<Map<String, Object>> keys = getResults(ps.getGeneratedKeys());
        //如果只生成唯一的key
        if (keys.size() == 1) {
          //获取该值
          Map<String, Object> key = keys.get(0);
          //迭代该对象
          Iterator<Object> i = key.values().iterator();
          if (i.hasNext()) {
            Object genkey = i.next();
            if (genkey != null) {
              try {
                //返回该值
                return Integer.parseInt(genkey.toString());
              } catch (NumberFormatException e) {
                //ignore, no numeric key support
              }
            }
          }
        }
      }
      //返回不生成主键ID的定义值
      return NO_GENERATED_KEY;
    } finally {
      try {
        //关闭会话
        ps.close();
      } catch (SQLException e) {
        //ignore
      }
    }
  }

  /**
   * 执行更新会话
   * Executes an UPDATE statement.
   *
   * @param sql  The SQL
   * @param args The arguments to be set on the statement.
   * @return The number of rows impacted or BATCHED_RESULTS if the statements are being batched.
   * @throws SQLException If statement preparation or execution fails
   */
  public int update(String sql, Object... args) throws SQLException {
    //建立预会话
    PreparedStatement ps = connection.prepareStatement(sql);
    try {
      //设置参数
      setParameters(ps, args);
      //执行更新会话操作
      return ps.executeUpdate();
    } finally {
      try {
        //会话关闭
        ps.close();
      } catch (SQLException e) {
        //ignore
      }
    }
  }

  /**
   * 执行删除会话操作
   * Executes a DELETE statement.
   *
   * @param sql  The SQL
   * @param args The arguments to be set on the statement.
   * @return The number of rows impacted or BATCHED_RESULTS if the statements are being batched.
   * @throws SQLException If statement preparation or execution fails
   */
  public int delete(String sql, Object... args) throws SQLException {
    return update(sql, args);
  }

  /**
   * 执行作为JDBC的会话，方便DDL语句的执行
   * Executes any string as a JDBC Statement.
   * Good for DDL
   *
   * @param sql The SQL
   * @throws SQLException If statement preparation or execution fails
   */
  public void run(String sql) throws SQLException {
    //链接创建会话
    Statement stmt = connection.createStatement();
    try {
      //直接执行SQL
      stmt.execute(sql);
    } finally {
      try {
        //会话关闭
        stmt.close();
      } catch (SQLException e) {
        //ignore
      }
    }
  }

  /**
   * 废弃关闭该方法，可以使用connection类关闭
   *  @deprecated Since 3.5.4, this method is deprecated. Please close the {@link Connection} outside of this class.
   */
  @Deprecated
  public void closeConnection() {
    try {
      connection.close();
    } catch (SQLException e) {
      //ignore
    }
  }
  //设置参数
  private void setParameters(PreparedStatement ps, Object... args) throws SQLException {
    for (int i = 0, n = args.length; i < n; i++) {
      if (args[i] == null) {
        throw new SQLException("SqlRunner requires an instance of Null to represent typed null values for JDBC compatibility");
        //如果是封装的关键字类型，则用类型处理器处理
      } else if (args[i] instanceof Null) {
        ((Null) args[i]).getTypeHandler().setParameter(ps, i + 1, null, ((Null) args[i]).getJdbcType());
      } else {
        //通过类型处理注册器获取类型处理器，然后处理对应的参数
        TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(args[i].getClass());
        if (typeHandler == null) {
          throw new SQLException("SqlRunner could not find a TypeHandler instance for " + args[i].getClass());
        } else {
          typeHandler.setParameter(ps, i + 1, args[i], null);
        }
      }
    }
  }
  //获取返回的数据结果
  private List<Map<String, Object>> getResults(ResultSet rs) throws SQLException {
    try {
      //返回的结果集
      List<Map<String, Object>> list = new ArrayList<>();
      //列集合
      List<String> columns = new ArrayList<>();
      //类型处理器集合
      List<TypeHandler<?>> typeHandlers = new ArrayList<>();
      //获取元数据
      ResultSetMetaData rsmd = rs.getMetaData();
      //根据列数量进行列处理
      for (int i = 0, n = rsmd.getColumnCount(); i < n; i++) {
        columns.add(rsmd.getColumnLabel(i + 1));
        try {
          //获取类类型
          Class<?> type = Resources.classForName(rsmd.getColumnClassName(i + 1));
          //根据类型获取类型处理器
          TypeHandler<?> typeHandler = typeHandlerRegistry.getTypeHandler(type);
          //如果为空，则根据对象获取Object类型的处理器
          if (typeHandler == null) {
            typeHandler = typeHandlerRegistry.getTypeHandler(Object.class);
          }
          //存放于类型处理器的集合
          typeHandlers.add(typeHandler);
        } catch (Exception e) {
          typeHandlers.add(typeHandlerRegistry.getTypeHandler(Object.class));
        }
      }
      //循环处理结果数据
      while (rs.next()) {
        Map<String, Object> row = new HashMap<>();
        //获取每列数据
        for (int i = 0, n = columns.size(); i < n; i++) {
          String name = columns.get(i);
          //根据名称类型处理器获取处理器
          TypeHandler<?> handler = typeHandlers.get(i);
          //将列名和处理器获取的结果数据存放行内
          row.put(name.toUpperCase(Locale.ENGLISH), handler.getResult(rs, name));
        }
        //添加到返回结果内 行成key-value键值对应
        list.add(row);
      }
      return list;
    } finally {
      if (rs != null) {
        try {
          //会话关闭
          rs.close();
        } catch (Exception e) {
          // ignore
        }
      }
    }
  }

}
