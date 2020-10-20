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
package org.apache.ibatis.executor;

/**
 * 错误上下文
 * @author Clinton Begin
 */
public class ErrorContext {
  //行分割符号
  private static final String LINE_SEPARATOR = System.getProperty("line.separator","\n");
  //线程本地变量
  private static final ThreadLocal<ErrorContext> LOCAL = new ThreadLocal<>();
  //已经存储的错误上下文
  private ErrorContext stored;
  //资源
  private String resource;
  //活动
  private String activity;
  //对象
  private String object;
  //消息
  private String message;
  //sql
  private String sql;
  //抛出异常
  private Throwable cause;
  //防止外部实例化
  private ErrorContext() {
  }
  //错误上下文实例话
  public static ErrorContext instance() {
    //从线程本地变量获取，保证每个线程不一样
    ErrorContext context = LOCAL.get();
    if (context == null) {
      context = new ErrorContext();
      LOCAL.set(context);
    }
    return context;
  }
  //存储本地的错误上下文
  public ErrorContext store() {
    ErrorContext newContext = new ErrorContext();
    newContext.stored = this;
    LOCAL.set(newContext);
    return LOCAL.get();
  }
  //重新调用
  public ErrorContext recall() {
    if (stored != null) {
      LOCAL.set(stored);
      stored = null;
    }
    return LOCAL.get();
  }
  //设置资源
  public ErrorContext resource(String resource) {
    this.resource = resource;
    return this;
  }
 //设置活动
  public ErrorContext activity(String activity) {
    this.activity = activity;
    return this;
  }
  //设置对象
  public ErrorContext object(String object) {
    this.object = object;
    return this;
  }
  //设置信息
  public ErrorContext message(String message) {
    this.message = message;
    return this;
  }
  //设置sql
  public ErrorContext sql(String sql) {
    this.sql = sql;
    return this;
  }
  //设置Throwable
  public ErrorContext cause(Throwable cause) {
    this.cause = cause;
    return this;
  }
  //重置错误上下文
  public ErrorContext reset() {
    resource = null;
    activity = null;
    object = null;
    message = null;
    sql = null;
    cause = null;
    LOCAL.remove();
    return this;
  }
  //实现错误日志编排
  @Override
  public String toString() {
    StringBuilder description = new StringBuilder();

    // message
    if (this.message != null) {
      description.append(LINE_SEPARATOR);
      description.append("### ");
      description.append(this.message);
    }

    // resource
    if (resource != null) {
      description.append(LINE_SEPARATOR);
      description.append("### The error may exist in ");
      description.append(resource);
    }

    // object
    if (object != null) {
      description.append(LINE_SEPARATOR);
      description.append("### The error may involve ");
      description.append(object);
    }

    // activity
    if (activity != null) {
      description.append(LINE_SEPARATOR);
      description.append("### The error occurred while ");
      description.append(activity);
    }

    // sql
    if (sql != null) {
      description.append(LINE_SEPARATOR);
      description.append("### SQL: ");
      description.append(sql.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim());
    }

    // cause
    if (cause != null) {
      description.append(LINE_SEPARATOR);
      description.append("### Cause: ");
      description.append(cause.toString());
    }

    return description.toString();
  }

}
