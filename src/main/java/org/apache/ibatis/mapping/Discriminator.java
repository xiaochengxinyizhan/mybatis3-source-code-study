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
package org.apache.ibatis.mapping;

import java.util.Collections;
import java.util.Map;

import org.apache.ibatis.session.Configuration;

/**
 * 鉴别器
 * @author Clinton Begin
 */
public class Discriminator {
  //结果映射
  private ResultMapping resultMapping;
  //鉴别器映射
  private Map<String, String> discriminatorMap;
  //空构造器
  Discriminator() {
  }
  //内部构建器
  public static class Builder {
    //鉴别器
    private Discriminator discriminator = new Discriminator();
     //构建鉴别器
    public Builder(Configuration configuration, ResultMapping resultMapping, Map<String, String> discriminatorMap) {
      discriminator.resultMapping = resultMapping;
      discriminator.discriminatorMap = discriminatorMap;
    }
    //并设置鉴别器映射为不可修改的map
    public Discriminator build() {
      assert discriminator.resultMapping != null;
      assert discriminator.discriminatorMap != null;
      assert !discriminator.discriminatorMap.isEmpty();
      //lock down map
      discriminator.discriminatorMap = Collections.unmodifiableMap(discriminator.discriminatorMap);
      return discriminator;
    }
  }
  //获取结果映射
  public ResultMapping getResultMapping() {
    return resultMapping;
  }
  //获取鉴别器映射
  public Map<String, String> getDiscriminatorMap() {
    return discriminatorMap;
  }
  //根据key获取鉴别器的value
  public String getMapIdFor(String s) {
    return discriminatorMap.get(s);
  }

}
