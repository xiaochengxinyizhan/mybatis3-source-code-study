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

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 脚本管理器
 * @author Clinton Begin
 */
public class ScriptRunner {
  //行分割定义
  private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
  //默认封号定义
  private static final String DEFAULT_DELIMITER = ";";
  //默认分割符匹配
  private static final Pattern DELIMITER_PATTERN = Pattern.compile("^\\s*((--)|(//))?\\s*(//)?\\s*@DELIMITER\\s+([^\\s]+)", Pattern.CASE_INSENSITIVE);
  //JDBC连接
  private final Connection connection;
  //停止错误标识
  private boolean stopOnError;
  //抛出警告标识
  private boolean throwWarning;
  //自动提交标识
  private boolean autoCommit;
  //发送完整的脚本标识
  private boolean sendFullScript;
  //移除CRs标识
  private boolean removeCRs;
  //转义标识
  private boolean escapeProcessing = true;
  //系统输出器
  private PrintWriter logWriter = new PrintWriter(System.out);
  //系统error输出器
  private PrintWriter errorLogWriter = new PrintWriter(System.err);
  //默认的分割符
  private String delimiter = DEFAULT_DELIMITER;
  //全行分割标识
  private boolean fullLineDelimiter;
  //脚本管理器的构造函数，赋值connection链接
  public ScriptRunner(Connection connection) {
    this.connection = connection;
  }

  //下面是set方法
  public void setStopOnError(boolean stopOnError) {
    this.stopOnError = stopOnError;
  }

  public void setThrowWarning(boolean throwWarning) {
    this.throwWarning = throwWarning;
  }

  public void setAutoCommit(boolean autoCommit) {
    this.autoCommit = autoCommit;
  }

  public void setSendFullScript(boolean sendFullScript) {
    this.sendFullScript = sendFullScript;
  }

  public void setRemoveCRs(boolean removeCRs) {
    this.removeCRs = removeCRs;
  }

  /**
   * @since 3.1.1
   */
  public void setEscapeProcessing(boolean escapeProcessing) {
    this.escapeProcessing = escapeProcessing;
  }

  public void setLogWriter(PrintWriter logWriter) {
    this.logWriter = logWriter;
  }

  public void setErrorLogWriter(PrintWriter errorLogWriter) {
    this.errorLogWriter = errorLogWriter;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public void setFullLineDelimiter(boolean fullLineDelimiter) {
    this.fullLineDelimiter = fullLineDelimiter;
  }

  //运行脚本调用方为Test测试类
  public void runScript(Reader reader) {
    //设置自动提交
    setAutoCommit();

    try {
      //如果全部脚本
      if (sendFullScript) {
        //执行全部脚本
        executeFullScript(reader);
      } else {
        //按行执行脚本
        executeLineByLine(reader);
      }
    } finally {
      //回滚链接
      rollbackConnection();
    }
  }
//执行全量脚本
  private void executeFullScript(Reader reader) {
    //脚本构建器
    StringBuilder script = new StringBuilder();
    try {
      //获取读到的数据
      BufferedReader lineReader = new BufferedReader(reader);
      String line;
      while ((line = lineReader.readLine()) != null) {
        script.append(line);
        script.append(LINE_SEPARATOR);
      }
      //脚本转化为命令行
      String command = script.toString();
      //输出SQL命令
      println(command);
      //执行SQL语句
      executeStatement(command);
      //提交链接
      commitConnection();
    } catch (Exception e) {
      String message = "Error executing: " + script + ".  Cause: " + e;
      printlnError(message);
      throw new RuntimeSqlException(message, e);
    }
  }
  //按行执行
  private void executeLineByLine(Reader reader) {
    StringBuilder command = new StringBuilder();
    try {
      //获取io流数据
      BufferedReader lineReader = new BufferedReader(reader);
      String line;
      while ((line = lineReader.readLine()) != null) {
        //处理每行命令
        handleLine(command, line);
      }
      //提交链接
      commitConnection();
      //检查是否丢失行内容命令
      checkForMissingLineTerminator(command);
    } catch (Exception e) {
      String message = "Error executing: " + command + ".  Cause: " + e;
      printlnError(message);
      throw new RuntimeSqlException(message, e);
    }
  }

  /**
   * 废弃该方法，请通过这个类外面的connection关闭
   * @deprecated Since 3.5.4, this method is deprecated. Please close the {@link Connection} outside of this class.
   */
  @Deprecated
  public void closeConnection() {
    try {
      connection.close();
    } catch (Exception e) {
      // ignore
    }
  }
  //设置自动提交
  private void setAutoCommit() {
    try {
      if (autoCommit != connection.getAutoCommit()) {
        connection.setAutoCommit(autoCommit);
      }
    } catch (Throwable t) {
      throw new RuntimeSqlException("Could not set AutoCommit to " + autoCommit + ". Cause: " + t, t);
    }
  }
  //手动提交链接
  private void commitConnection() {
    try {
      if (!connection.getAutoCommit()) {
        connection.commit();
      }
    } catch (Throwable t) {
      throw new RuntimeSqlException("Could not commit transaction. Cause: " + t, t);
    }
  }
  //回滚链接
  private void rollbackConnection() {
    try {
      if (!connection.getAutoCommit()) {
        connection.rollback();
      }
    } catch (Throwable t) {
      // ignore
    }
  }
  //检查是否有中断的命令行
  private void checkForMissingLineTerminator(StringBuilder command) {
    if (command != null && command.toString().trim().length() > 0) {
      throw new RuntimeSqlException("Line missing end-of-line terminator (" + delimiter + ") => " + command);
    }
  }
  //处理每行命令
  private void handleLine(StringBuilder command, String line) throws SQLException {
    String trimmedLine = line.trim();
    if (lineIsComment(trimmedLine)) {
      Matcher matcher = DELIMITER_PATTERN.matcher(trimmedLine);
      if (matcher.find()) {
        delimiter = matcher.group(5);
      }
      println(trimmedLine);
    } else if (commandReadyToExecute(trimmedLine)) {
      command.append(line.substring(0, line.lastIndexOf(delimiter)));
      command.append(LINE_SEPARATOR);
      println(command);
      executeStatement(command.toString());
      command.setLength(0);
    } else if (trimmedLine.length() > 0) {
      command.append(line);
      command.append(LINE_SEPARATOR);
    }
  }
  //行是内容
  private boolean lineIsComment(String trimmedLine) {
    return trimmedLine.startsWith("//") || trimmedLine.startsWith("--");
  }
  //准备执行的命令行
  private boolean commandReadyToExecute(String trimmedLine) {
    // issue #561 remove anything after the delimiter
    return !fullLineDelimiter && trimmedLine.contains(delimiter) || fullLineDelimiter && trimmedLine.equals(delimiter);
  }
  //执行SQL会话
  private void executeStatement(String command) throws SQLException {
    //创建SQL会话
    Statement statement = connection.createStatement();
    try {
      //处理转义字符
      statement.setEscapeProcessing(escapeProcessing);
      String sql = command;
      //是否移除回车符
      if (removeCRs) {
        sql = sql.replaceAll("\r\n", "\n");
      }
      try {
        //执行SQL获取结果
        boolean hasResults = statement.execute(sql);
        while (!(!hasResults && statement.getUpdateCount() == -1)) {
          checkWarnings(statement);
          //输出结果
          printResults(statement, hasResults);
          //判断是否还有结果
          hasResults = statement.getMoreResults();
        }
      } catch (SQLWarning e) {
        throw e;
      } catch (SQLException e) {
        if (stopOnError) {
          throw e;
        } else {
          String message = "Error executing: " + command + ".  Cause: " + e;
          printlnError(message);
        }
      }
    } finally {
      try {
        //关闭会话
        statement.close();
      } catch (Exception ignored) {
        // Ignore to workaround a bug in some connection pools
        // (Does anyone know the details of the bug?)
      }
    }
  }
  //检查是否有语法警告
  private void checkWarnings(Statement statement) throws SQLException {
    if (!throwWarning) {
      return;
    }
    // In Oracle, CREATE PROCEDURE, FUNCTION, etc. returns warning
    // instead of throwing exception if there is compilation error.
    //在Oracle数据库中，CREATE PROCEDURE, FUNCTION等，如果编译错误会返回警告而不是抛出异常
    SQLWarning warning = statement.getWarnings();
    if (warning != null) {
      throw warning;
    }
  }
  //输出结果
  private void printResults(Statement statement, boolean hasResults) {
    //判断是否有数据
    if (!hasResults) {
      return;
    }
    //会话获取数据结果
    try (ResultSet rs = statement.getResultSet()) {
      ResultSetMetaData md = rs.getMetaData();
      int cols = md.getColumnCount();
      for (int i = 0; i < cols; i++) {
        String name = md.getColumnLabel(i + 1);
        print(name + "\t");
      }
      println("");
      while (rs.next()) {
        for (int i = 0; i < cols; i++) {
          String value = rs.getString(i + 1);
          print(value + "\t");
        }
        println("");
      }
    } catch (SQLException e) {
      printlnError("Error printing results: " + e.getMessage());
    }
  }
  //打印日志对象
  private void print(Object o) {
    if (logWriter != null) {
      logWriter.print(o);
      logWriter.flush();
    }
  }
 //换行打印日志对象
  private void println(Object o) {
    if (logWriter != null) {
      logWriter.println(o);
      logWriter.flush();
    }
  }
  //打印错误日志信息
  private void printlnError(Object o) {
    if (errorLogWriter != null) {
      errorLogWriter.println(o);
      errorLogWriter.flush();
    }
  }

}
