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
package org.apache.ibatis.executor.keygen;

import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;

/**
 * 查询主键生成
 * @author Clinton Begin
 * @author Jeff Butler
 */
public class SelectKeyGenerator implements KeyGenerator {
  //查询key前缀
  public static final String SELECT_KEY_SUFFIX = "!selectKey";
  //是否在执行前
  private final boolean executeBefore;
  //主键会话
  private final MappedStatement keyStatement;
  //构造器函数
  public SelectKeyGenerator(MappedStatement keyStatement, boolean executeBefore) {
    this.executeBefore = executeBefore;
    this.keyStatement = keyStatement;
  }
  //在执行前生成
  @Override
  public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
    //是否在执行前执行判断
    if (executeBefore) {
      processGeneratedKeys(executor, ms, parameter);
    }
  }
  //在执行后生成
  @Override
  public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
    //是否在执行后生成
    if (!executeBefore) {
      processGeneratedKeys(executor, ms, parameter);
    }
  }
  //执行生成主键key
  private void processGeneratedKeys(Executor executor, MappedStatement ms, Object parameter) {
    try {
      //参数不为空，主键会话不为空，主键会话的属性不为空
      if (parameter != null && keyStatement != null && keyStatement.getKeyProperties() != null) {
        //获取会话主键属性
        String[] keyProperties = keyStatement.getKeyProperties();
        //获取全局配置
        final Configuration configuration = ms.getConfiguration();
        //配置获取元对象
        final MetaObject metaParam = configuration.newMetaObject(parameter);
        // Do not close keyExecutor.
        // The transaction will be closed by parent executor.
        // 不要关闭主键执行器--类型选择简单执行器SimPleExecutor
        Executor keyExecutor = configuration.newExecutor(executor.getTransaction(), ExecutorType.SIMPLE);
        //主键执行器执行查询
        List<Object> values = keyExecutor.query(keyStatement, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
        //没有返回数据
        if (values.size() == 0) {
          throw new ExecutorException("SelectKey returned no data.");
          //返回数据超过一条
        } else if (values.size() > 1) {
          throw new ExecutorException("SelectKey returned more than one value.");
        } else {
          //获取元对象结果
          MetaObject metaResult = configuration.newMetaObject(values.get(0));
          if (keyProperties.length == 1) {
            if (metaResult.hasGetter(keyProperties[0])) {
              //设置元参数结果值
              setValue(metaParam, keyProperties[0], metaResult.getValue(keyProperties[0]));
            } else {
              // no getter for the property - maybe just a single value object
              // so try that
              //咩有get方法的属性，可能只是一个值对象，可以尝试下面这种方式。
              setValue(metaParam, keyProperties[0], values.get(0));
            }
          } else {
            //处理多个属性
            handleMultipleProperties(keyProperties, metaParam, metaResult);
          }
        }
      }
    } catch (ExecutorException e) {
      throw e;
    } catch (Exception e) {
      throw new ExecutorException("Error selecting key or setting result to parameter object. Cause: " + e, e);
    }
  }
  //处理多个属性
  private void handleMultipleProperties(String[] keyProperties,
      MetaObject metaParam, MetaObject metaResult) {
    //会话获取列字段
    String[] keyColumns = keyStatement.getKeyColumns();
    //如果列字段没有不存在
    if (keyColumns == null || keyColumns.length == 0) {
      // no key columns specified, just use the property names
      //没有key列被指定，使用属性名字
      for (String keyProperty : keyProperties) {
        setValue(metaParam, keyProperty, metaResult.getValue(keyProperty));
      }
    } else {
      if (keyColumns.length != keyProperties.length) {
        throw new ExecutorException("If SelectKey has key columns, the number must match the number of key properties.");
      }
      //否则遍历列字段设置值。
      for (int i = 0; i < keyProperties.length; i++) {
        setValue(metaParam, keyProperties[i], metaResult.getValue(keyColumns[i]));
      }
    }
  }
  //设置值
  private void setValue(MetaObject metaParam, String property, Object value) {
    //如果元参数有set方法设置值
    if (metaParam.hasSetter(property)) {
      metaParam.setValue(property, value);
    } else {
      throw new ExecutorException("No setter found for the keyProperty '" + property + "' in " + metaParam.getOriginalObject().getClass().getName() + ".");
    }
  }
}
