/**
 *    Copyright 2009-2017 the original author or authors.
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

import java.util.Iterator;

/**
 * 属性标记器
 * @author Clinton Begin
 */
public class PropertyTokenizer implements Iterator<PropertyTokenizer> {
  //名称
  private String name;
  //索引名称
  private final String indexedName;
  //索引
  private String index;
  //子属性
  private final String children;
  //根据完整的全名称构造器
  public PropertyTokenizer(String fullname) {
    int delim = fullname.indexOf('.');
    if (delim > -1) {
      name = fullname.substring(0, delim);
      children = fullname.substring(delim + 1);
    } else {
      name = fullname;
      children = null;
    }
    indexedName = name;
    delim = name.indexOf('[');
    if (delim > -1) {
      index = name.substring(delim + 1, name.length() - 1);
      name = name.substring(0, delim);
    }
  }
  //获取名称
  public String getName() {
    return name;
  }
  //获取索引
  public String getIndex() {
    return index;
  }
  //获取索引名称
  public String getIndexedName() {
    return indexedName;
  }
  //获取子名称
  public String getChildren() {
    return children;
  }
  //是否还有下一个属性
  @Override
  public boolean hasNext() {
    return children != null;
  }
  //遍历下一个属性
  @Override
  public PropertyTokenizer next() {
    return new PropertyTokenizer(children);
  }
  //移除能力没有
  @Override
  public void remove() {
    throw new UnsupportedOperationException("Remove is not supported, as it has no meaning in the context of properties.");
  }
}
