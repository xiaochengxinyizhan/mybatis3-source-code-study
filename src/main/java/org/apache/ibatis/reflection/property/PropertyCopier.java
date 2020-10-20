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
package org.apache.ibatis.reflection.property;

import java.lang.reflect.Field;

import org.apache.ibatis.reflection.Reflector;

/**
 * 属性拷贝者
 * @author Clinton Begin
 */
public final class PropertyCopier {
  //属性拷贝空构造器设置私有防止外部实例化
  private PropertyCopier() {
    // Prevent Instantiation of Static Class
  }
  //拷贝bean的属性，类型，原bean，目标bean
  public static void copyBeanProperties(Class<?> type, Object sourceBean, Object destinationBean) {
    Class<?> parent = type;
    while (parent != null) {
      //获取类型的声明属性
      final Field[] fields = parent.getDeclaredFields();
      //遍历属性
      for (Field field : fields) {
        try {
          try {
            //属性设置目标bean通过原bean
            field.set(destinationBean, field.get(sourceBean));
          } catch (IllegalAccessException e) {
            if (Reflector.canControlMemberAccessible()) {
              //如果非法访问则开启访问属性权限
              field.setAccessible(true);
              field.set(destinationBean, field.get(sourceBean));
            } else {
              throw e;
            }
          }
        } catch (Exception e) {
          // Nothing useful to do, will only fail on final fields, which will be ignored.
        }
      }
      //获取父级类，相当于是递归
      parent = parent.getSuperclass();
    }
  }

}
