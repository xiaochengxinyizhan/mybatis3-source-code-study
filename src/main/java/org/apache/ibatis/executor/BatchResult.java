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
package org.apache.ibatis.executor;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.mapping.MappedStatement;

/**
 * 批量结果
 * @author Jeff Butler
 */
public class BatchResult {
  //会话映射
  private final MappedStatement mappedStatement;
  //sql
  private final String sql;
  //参数对象集合
  private final List<Object> parameterObjects;
  //操作行数
  private int[] updateCounts;
  //构造器
  public BatchResult(MappedStatement mappedStatement, String sql) {
    super();
    this.mappedStatement = mappedStatement;
    this.sql = sql;
    this.parameterObjects = new ArrayList<>();
  }
 //构造器
  public BatchResult(MappedStatement mappedStatement, String sql, Object parameterObject) {
    this(mappedStatement, sql);
    addParameterObject(parameterObject);
  }
  //获取映射会话
  public MappedStatement getMappedStatement() {
    return mappedStatement;
  }
  //获取sql
  public String getSql() {
    return sql;
  }
  //获取参数对象
  @Deprecated
  public Object getParameterObject() {
    return parameterObjects.get(0);
  }
  //获取参数对象
  public List<Object> getParameterObjects() {
    return parameterObjects;
  }
  //获取更新行数
  public int[] getUpdateCounts() {
    return updateCounts;
  }
  //设置更行行数
  public void setUpdateCounts(int[] updateCounts) {
    this.updateCounts = updateCounts;
  }
  //添加参数对象
  public void addParameterObject(Object parameterObject) {
    this.parameterObjects.add(parameterObject);
  }

}
