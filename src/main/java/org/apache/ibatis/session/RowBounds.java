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
package org.apache.ibatis.session;

/**
 * 行边界
 * @author Clinton Begin
 */
public class RowBounds {
  //没有行下标
  public static final int NO_ROW_OFFSET = 0;
  //最大限制行
  public static final int NO_ROW_LIMIT = Integer.MAX_VALUE;
  //默认的行对象
  public static final RowBounds DEFAULT = new RowBounds();
  //行下标
  private final int offset;
  //限制行数
  private final int limit;
  //构造函数
  public RowBounds() {
    this.offset = NO_ROW_OFFSET;
    this.limit = NO_ROW_LIMIT;
  }
  //构造函数
  public RowBounds(int offset, int limit) {
    this.offset = offset;
    this.limit = limit;
  }
  //获取页码
  public int getOffset() {
    return offset;
  }
  //获取每页数量
  public int getLimit() {
    return limit;
  }

}
