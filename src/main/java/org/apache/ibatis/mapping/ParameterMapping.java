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

import java.sql.ResultSet;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * 参数映射
 * @author Clinton Begin
 */
public class ParameterMapping {
  //全局配置
  private Configuration configuration;
  //属性
  private String property;
  //参数风格
  private ParameterMode mode;
  //Java类型
  private Class<?> javaType = Object.class;
  //jdbc类型
  private JdbcType jdbcType;
  //数值级别
  private Integer numericScale;
  //类型处理器
  private TypeHandler<?> typeHandler;
  //结果集映射ID
  private String resultMapId;
  //jdbc类型名称
  private String jdbcTypeName;
  //正则表达
  private String expression;

  private ParameterMapping() {
  }
  //内部构建器
  public static class Builder {
    //参数映射
    private ParameterMapping parameterMapping = new ParameterMapping();
    //构建构造函数
    public Builder(Configuration configuration, String property, TypeHandler<?> typeHandler) {
      parameterMapping.configuration = configuration;
      parameterMapping.property = property;
      parameterMapping.typeHandler = typeHandler;
      parameterMapping.mode = ParameterMode.IN;
    }
    //构建构造函数
    public Builder(Configuration configuration, String property, Class<?> javaType) {
      parameterMapping.configuration = configuration;
      parameterMapping.property = property;
      parameterMapping.javaType = javaType;
      parameterMapping.mode = ParameterMode.IN;
    }
    //参数风格
    public Builder mode(ParameterMode mode) {
      parameterMapping.mode = mode;
      return this;
    }
    //Java类型
    public Builder javaType(Class<?> javaType) {
      parameterMapping.javaType = javaType;
      return this;
    }
    //jdbc类型
    public Builder jdbcType(JdbcType jdbcType) {
      parameterMapping.jdbcType = jdbcType;
      return this;
    }
    //数值级别
    public Builder numericScale(Integer numericScale) {
      parameterMapping.numericScale = numericScale;
      return this;
    }
    //结果映射ID
    public Builder resultMapId(String resultMapId) {
      parameterMapping.resultMapId = resultMapId;
      return this;
    }
    //类型处理器
    public Builder typeHandler(TypeHandler<?> typeHandler) {
      parameterMapping.typeHandler = typeHandler;
      return this;
    }
    //jdbc类型名字
    public Builder jdbcTypeName(String jdbcTypeName) {
      parameterMapping.jdbcTypeName = jdbcTypeName;
      return this;
    }
    //表达
    public Builder expression(String expression) {
      parameterMapping.expression = expression;
      return this;
    }
   //构建
    public ParameterMapping build() {
      //解析类型处理器
      resolveTypeHandler();
      //校验
      validate();
      return parameterMapping;
    }
    //校验
    private void validate() {
      //结果集是否是jdbc类型
      if (ResultSet.class.equals(parameterMapping.javaType)) {
        if (parameterMapping.resultMapId == null) {
          throw new IllegalStateException("Missing resultmap in property '"
              + parameterMapping.property + "'.  "
              + "Parameters of type java.sql.ResultSet require a resultmap.");
        }
      } else {
        if (parameterMapping.typeHandler == null) {
          throw new IllegalStateException("Type handler was null on parameter mapping for property '"
            + parameterMapping.property + "'. It was either not specified and/or could not be found for the javaType ("
            + parameterMapping.javaType.getName() + ") : jdbcType (" + parameterMapping.jdbcType + ") combination.");
        }
      }
    }
    //解析类型处理器
    private void resolveTypeHandler() {
      if (parameterMapping.typeHandler == null && parameterMapping.javaType != null) {
        Configuration configuration = parameterMapping.configuration;
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        parameterMapping.typeHandler = typeHandlerRegistry.getTypeHandler(parameterMapping.javaType, parameterMapping.jdbcType);
      }
    }

  }
  //获取属性
  public String getProperty() {
    return property;
  }

  /**
   * 回调会话的处理输出
   * Used for handling output of callable statements.
   * @return
   */
  public ParameterMode getMode() {
    return mode;
  }

  /**
   * 回调会话的处理输出
   * Used for handling output of callable statements.
   * @return
   */
  public Class<?> getJavaType() {
    return javaType;
  }

  /**
   * 未知类型处理器以防没有没有属性类型对应的处理器
   * Used in the UnknownTypeHandler in case there is no handler for the property type.
   * @return
   */
  public JdbcType getJdbcType() {
    return jdbcType;
  }

  /**
   * 用于回调会话的处理输出
   * Used for handling output of callable statements.
   * @return
   */
  public Integer getNumericScale() {
    return numericScale;
  }

  /**
   * 用于给预会话设置参数
   * Used when setting parameters to the PreparedStatement.
   * @return
   */
  public TypeHandler<?> getTypeHandler() {
    return typeHandler;
  }

  /**
   * 回调会话的处理输出
   * Used for handling output of callable statements.
   * @return
   */
  public String getResultMapId() {
    return resultMapId;
  }

  /**
   * 回调会话的处理输出
   * Used for handling output of callable statements.
   * @return
   */
  public String getJdbcTypeName() {
    return jdbcTypeName;
  }

  /**
   * 没有使用
   * Not used
   * @return
   */
  public String getExpression() {
    return expression;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ParameterMapping{");
    //sb.append("configuration=").append(configuration); // configuration doesn't have a useful .toString()
    sb.append("property='").append(property).append('\'');
    sb.append(", mode=").append(mode);
    sb.append(", javaType=").append(javaType);
    sb.append(", jdbcType=").append(jdbcType);
    sb.append(", numericScale=").append(numericScale);
    //sb.append(", typeHandler=").append(typeHandler); // typeHandler also doesn't have a useful .toString()
    sb.append(", resultMapId='").append(resultMapId).append('\'');
    sb.append(", jdbcTypeName='").append(jdbcTypeName).append('\'');
    sb.append(", expression='").append(expression).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
