/**
 *    Copyright 2009-2015 the original author or authors.
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
package org.apache.ibatis.datasource.unpooled;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.DataSourceException;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * 未池化的数据源工厂
 * @author Clinton Begin
 */
public class UnpooledDataSourceFactory implements DataSourceFactory {
  //驱动属性的前缀
  private static final String DRIVER_PROPERTY_PREFIX = "driver.";
  //驱动属性前缀长度
  private static final int DRIVER_PROPERTY_PREFIX_LENGTH = DRIVER_PROPERTY_PREFIX.length();
  //数据源
  protected DataSource dataSource;
  //空构造函数初始化数据源
  public UnpooledDataSourceFactory() {
    this.dataSource = new UnpooledDataSource();
  }

  /**
   * 设置属性
   * @param properties
   */
  @Override
  public void setProperties(Properties properties) {
    //初始化驱动属性
    Properties driverProperties = new Properties();
    //系统元数据对象获取关于数据源的元对象--可以参考反射模块
    MetaObject metaDataSource = SystemMetaObject.forObject(dataSource);
    //遍历map的集合
    for (Object key : properties.keySet()) {
      //获取属性名称
      String propertyName = (String) key;
      //属性名称如果以驱动属性作为前缀
      if (propertyName.startsWith(DRIVER_PROPERTY_PREFIX)) {
        //获取对应的value值
        String value = properties.getProperty(propertyName);
        //驱动属性设置属性
        driverProperties.setProperty(propertyName.substring(DRIVER_PROPERTY_PREFIX_LENGTH), value);
      } else if (metaDataSource.hasSetter(propertyName)) {
        //如果是有setter封装
        String value = (String) properties.get(propertyName);
        //转化下对象进行封装
        Object convertedValue = convertValue(metaDataSource, propertyName, value);
        metaDataSource.setValue(propertyName, convertedValue);
      } else {
        throw new DataSourceException("Unknown DataSource property: " + propertyName);
      }
    }
    //如果驱动属性大小大于0，则设置该驱动属性给元数据源
    if (driverProperties.size() > 0) {
      metaDataSource.setValue("driverProperties", driverProperties);
    }
  }

  /**
   * 获取数据源
   * @return
   */
  @Override
  public DataSource getDataSource() {
    return dataSource;
  }

  /**
   * 封装value---根据不同的值返回
   * @param metaDataSource
   * @param propertyName
   * @param value
   * @return
   */
  private Object convertValue(MetaObject metaDataSource, String propertyName, String value) {
    Object convertedValue = value;
    Class<?> targetType = metaDataSource.getSetterType(propertyName);
    if (targetType == Integer.class || targetType == int.class) {
      convertedValue = Integer.valueOf(value);
    } else if (targetType == Long.class || targetType == long.class) {
      convertedValue = Long.valueOf(value);
    } else if (targetType == Boolean.class || targetType == boolean.class) {
      convertedValue = Boolean.valueOf(value);
    }
    return convertedValue;
  }

}
