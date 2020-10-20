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
package org.apache.ibatis.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.session.Configuration;

/**
 * 在已经执行一些动态内容之后，从{@link SqlSource} 获取实际的SQL。
 * SQL可能有占位符，并且参数映射有序 对于每个参数另外的信息（至少是从输入的对象读取值）
 * 可能也有另外的参数通过动态语言创建 比如循环或者绑定
 * An actual SQL String got from an {@link SqlSource} after having processed any dynamic content.
 * The SQL may have SQL placeholders "?" and an list (ordered) of an parameter mappings
 * with the additional information for each parameter (at least the property name of the input object to read
 * the value from).
 * <p>
 * Can also have additional parameters that are created by the dynamic language (for loops, bind...).
 *
 * @author Clinton Begin
 */
public class BoundSql {
  //sql
  private final String sql;
  //参数映射
  private final List<ParameterMapping> parameterMappings;
  //参数对象
  private final Object parameterObject;
  //附加参数
  private final Map<String, Object> additionalParameters;
  //元参数
  private final MetaObject metaParameters;
  //绑定SQL构造器
  public BoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings, Object parameterObject) {
    this.sql = sql;
    this.parameterMappings = parameterMappings;
    this.parameterObject = parameterObject;
    this.additionalParameters = new HashMap<>();
    this.metaParameters = configuration.newMetaObject(additionalParameters);
  }
  //获取SQL
  public String getSql() {
    return sql;
  }
 //获取参数映射
  public List<ParameterMapping> getParameterMappings() {
    return parameterMappings;
  }
  //获取参数对象
  public Object getParameterObject() {
    return parameterObject;
  }
  //是否有附加参数
  public boolean hasAdditionalParameter(String name) {
    String paramName = new PropertyTokenizer(name).getName();
    return additionalParameters.containsKey(paramName);
  }
  //设置附加参数--存放元参数信息
  public void setAdditionalParameter(String name, Object value) {
    metaParameters.setValue(name, value);
  }
  //获取附加参数
  public Object getAdditionalParameter(String name) {
    return metaParameters.getValue(name);
  }
}
