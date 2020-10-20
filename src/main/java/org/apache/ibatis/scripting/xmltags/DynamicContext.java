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
package org.apache.ibatis.scripting.xmltags;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import ognl.OgnlContext;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

/**
 * 动态上下文
 * @author Clinton Begin
 */
public class DynamicContext {
  //参数key
  public static final String PARAMETER_OBJECT_KEY = "_parameter";
  //数据库id key
  public static final String DATABASE_ID_KEY = "_databaseId";
  //Ognl运行器设置属性可访问
  static {
    OgnlRuntime.setPropertyAccessor(ContextMap.class, new ContextAccessor());
  }
  //绑定的上下文
  private final ContextMap bindings;
  //数组拼接器
  private final StringJoiner sqlBuilder = new StringJoiner(" ");
  //唯一的数值
  private int uniqueNumber = 0;
  //构造函数
  public DynamicContext(Configuration configuration, Object parameterObject) {
    if (parameterObject != null && !(parameterObject instanceof Map)) {
      MetaObject metaObject = configuration.newMetaObject(parameterObject);
      boolean existsTypeHandler = configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass());
      bindings = new ContextMap(metaObject, existsTypeHandler);
    } else {
      bindings = new ContextMap(null, false);
    }
    bindings.put(PARAMETER_OBJECT_KEY, parameterObject);
    bindings.put(DATABASE_ID_KEY, configuration.getDatabaseId());
  }
  //获取绑定的集合
  public Map<String, Object> getBindings() {
    return bindings;
  }
  //绑定名字和值
  public void bind(String name, Object value) {
    bindings.put(name, value);
  }
//追加SQL
  public void appendSql(String sql) {
    sqlBuilder.add(sql);
  }
  //获取SQL
  public String getSql() {
    return sqlBuilder.toString().trim();
  }
  //获取唯一的数值
  public int getUniqueNumber() {
    return uniqueNumber++;
  }
  //上下文集合
  static class ContextMap extends HashMap<String, Object> {
    private static final long serialVersionUID = 2977601501966151582L;
    //参数元对象
    private final MetaObject parameterMetaObject;
    //回滚参数对象
    private final boolean fallbackParameterObject;
   //构造函数
    public ContextMap(MetaObject parameterMetaObject, boolean fallbackParameterObject) {
      this.parameterMetaObject = parameterMetaObject;
      this.fallbackParameterObject = fallbackParameterObject;
    }
    //获取key对应的参数值
    @Override
    public Object get(Object key) {
      String strKey = (String) key;
      if (super.containsKey(strKey)) {
        return super.get(strKey);
      }

      if (parameterMetaObject == null) {
        return null;
      }

      if (fallbackParameterObject && !parameterMetaObject.hasGetter(strKey)) {
        return parameterMetaObject.getOriginalObject();
      } else {
        // issue #61 do not modify the context when reading
        return parameterMetaObject.getValue(strKey);
      }
    }
  }
  //上下文访问
  static class ContextAccessor implements PropertyAccessor {
    //从上下文获取属性对象
    @Override
    public Object getProperty(Map context, Object target, Object name) {
      Map map = (Map) target;

      Object result = map.get(name);
      if (map.containsKey(name) || result != null) {
        return result;
      }

      Object parameterObject = map.get(PARAMETER_OBJECT_KEY);
      if (parameterObject instanceof Map) {
        return ((Map)parameterObject).get(name);
      }

      return null;
    }
   //设置属性
    @Override
    public void setProperty(Map context, Object target, Object name, Object value) {
      Map<Object, Object> map = (Map<Object, Object>) target;
      map.put(name, value);
    }
   //获取源访问
    @Override
    public String getSourceAccessor(OgnlContext arg0, Object arg1, Object arg2) {
      return null;
    }
    //获取源set方法
    @Override
    public String getSourceSetter(OgnlContext arg0, Object arg1, Object arg2) {
      return null;
    }
  }
}
