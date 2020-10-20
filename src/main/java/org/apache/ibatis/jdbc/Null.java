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
package org.apache.ibatis.jdbc;

import org.apache.ibatis.type.BigDecimalTypeHandler;
import org.apache.ibatis.type.BlobTypeHandler;
import org.apache.ibatis.type.BooleanTypeHandler;
import org.apache.ibatis.type.ByteArrayTypeHandler;
import org.apache.ibatis.type.ByteTypeHandler;
import org.apache.ibatis.type.ClobTypeHandler;
import org.apache.ibatis.type.DateOnlyTypeHandler;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.DoubleTypeHandler;
import org.apache.ibatis.type.FloatTypeHandler;
import org.apache.ibatis.type.IntegerTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LongTypeHandler;
import org.apache.ibatis.type.ObjectTypeHandler;
import org.apache.ibatis.type.ShortTypeHandler;
import org.apache.ibatis.type.SqlDateTypeHandler;
import org.apache.ibatis.type.SqlTimeTypeHandler;
import org.apache.ibatis.type.SqlTimestampTypeHandler;
import org.apache.ibatis.type.StringTypeHandler;
import org.apache.ibatis.type.TimeOnlyTypeHandler;
import org.apache.ibatis.type.TypeHandler;

/**
 * 枚举类 Null值
 * @author Clinton Begin
 * @author Adam Gent
 */
public enum Null {
  //布尔类型
  BOOLEAN(new BooleanTypeHandler(), JdbcType.BOOLEAN),
  //自符类型
  //字节类型--tinyint
  BYTE(new ByteTypeHandler(), JdbcType.TINYINT),
  //字符类型--smallint
  SHORT(new ShortTypeHandler(), JdbcType.SMALLINT),
  //整数类型--integer
  INTEGER(new IntegerTypeHandler(), JdbcType.INTEGER),
  //长整数类型--bigint
  LONG(new LongTypeHandler(), JdbcType.BIGINT),
  //单精度类型--float
  FLOAT(new FloatTypeHandler(), JdbcType.FLOAT),
  //双精度类型--double
  DOUBLE(new DoubleTypeHandler(), JdbcType.DOUBLE),
  //商业计算精度
  BIGDECIMAL(new BigDecimalTypeHandler(), JdbcType.DECIMAL),

  //文本类型
  //字符串类型
  STRING(new StringTypeHandler(), JdbcType.VARCHAR),
  //字符串大对象--比如文件头等超过varchar信息的内容
  CLOB(new ClobTypeHandler(), JdbcType.CLOB),
  //char是定长字符串（1-255）
  //varchar是非定长字符串（1-32672）
  //Long Varchar也是非定长字符串（1-32700）
  //char是最简单的，系统分配固定长度的空间给它，容易被系统调整空间，性能比较高；
  //Varchar必须要存在于一个Page之内，当varchar数据很常的时候，会形成一条记录跨页的现象，成为Overflow Page现象，这在作Reorgchk的时候是用来判断是否需要Reorg的一个参数，一旦形成跨页存储，性能将降低；
  //LongVarChar在Varchar的长度扩大至32672的时候已经没有太大的意义了，每个Longvarchar在记录中只占24个字节，估计是作为一种索引，真正的内容存放在单独的Page中，这样就可以达到很大的长度。这样的方式致使性能更加降低。
  LONGVARCHAR(new ClobTypeHandler(), JdbcType.LONGVARCHAR),

  //字节数组
  BYTEARRAY(new ByteArrayTypeHandler(), JdbcType.LONGVARBINARY),
  //文本类型
  BLOB(new BlobTypeHandler(), JdbcType.BLOB),
  //长字符串二进制类型
  LONGVARBINARY(new BlobTypeHandler(), JdbcType.LONGVARBINARY),

  //对象类型
  OBJECT(new ObjectTypeHandler(), JdbcType.OTHER),
  //其他类型
  OTHER(new ObjectTypeHandler(), JdbcType.OTHER),
  //时间戳类型
  TIMESTAMP(new DateTypeHandler(), JdbcType.TIMESTAMP),
  //时间日期类型
  DATE(new DateOnlyTypeHandler(), JdbcType.DATE),
  //时间类型
  TIME(new TimeOnlyTypeHandler(), JdbcType.TIME),
  //SQL时间戳类型
  SQLTIMESTAMP(new SqlTimestampTypeHandler(), JdbcType.TIMESTAMP),
  //SQL的date类型
  SQLDATE(new SqlDateTypeHandler(), JdbcType.DATE),
  //SQL的时间类型
  SQLTIME(new SqlTimeTypeHandler(), JdbcType.TIME);

  //类型处理器
  private TypeHandler<?> typeHandler;
  //JDBC类型
  private JdbcType jdbcType;
  //有参构造函数
  Null(TypeHandler<?> typeHandler, JdbcType jdbcType) {
    this.typeHandler = typeHandler;
    this.jdbcType = jdbcType;
  }
  //获取类型处理器
  public TypeHandler<?> getTypeHandler() {
    return typeHandler;
  }
 //获取JDBC类型
  public JdbcType getJdbcType() {
    return jdbcType;
  }
}
