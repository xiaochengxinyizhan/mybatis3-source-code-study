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

import java.util.Map;

import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.session.Configuration;

/**
 * foreach的SQL节点
 * @author Clinton Begin
 */
public class ForEachSqlNode implements SqlNode {
  public static final String ITEM_PREFIX = "__frch_";
  //正则校验
  private final ExpressionEvaluator evaluator;
  //集合正则
  private final String collectionExpression;
  //内容
  private final SqlNode contents;
  //（
  private final String open;
  // ）
  private final String close;
  //分割符
  private final String separator;
  //项
  private final String item;
  //索引
  private final String index;
  //全局配置
  private final Configuration configuration;
  //构造函数
  public ForEachSqlNode(Configuration configuration, SqlNode contents, String collectionExpression, String index, String item, String open, String close, String separator) {
    this.evaluator = new ExpressionEvaluator();
    this.collectionExpression = collectionExpression;
    this.contents = contents;
    this.open = open;
    this.close = close;
    this.separator = separator;
    this.index = index;
    this.item = item;
    this.configuration = configuration;
  }
  //应用动态上下文
  @Override
  public boolean apply(DynamicContext context) {
    Map<String, Object> bindings = context.getBindings();
    //校验集合正则
    final Iterable<?> iterable = evaluator.evaluateIterable(collectionExpression, bindings);
    if (!iterable.iterator().hasNext()) {
      return true;
    }
    boolean first = true;
    //应用 （
    applyOpen(context);
    int i = 0;
    //遍历foreach循环
    for (Object o : iterable) {
      DynamicContext oldContext = context;
      if (first || separator == null) {
        context = new PrefixedContext(context, "");
      } else {
        context = new PrefixedContext(context, separator);
      }
      int uniqueNumber = context.getUniqueNumber();
      // Issue #709
      if (o instanceof Map.Entry) {
        @SuppressWarnings("unchecked")
        Map.Entry<Object, Object> mapEntry = (Map.Entry<Object, Object>) o;
        applyIndex(context, mapEntry.getKey(), uniqueNumber);
        applyItem(context, mapEntry.getValue(), uniqueNumber);
      } else {
        applyIndex(context, i, uniqueNumber);
        applyItem(context, o, uniqueNumber);
      }
      contents.apply(new FilteredDynamicContext(configuration, context, index, item, uniqueNumber));
      if (first) {
        first = !((PrefixedContext) context).isPrefixApplied();
      }
      context = oldContext;
      i++;
    }
    //应用 ）
    applyClose(context);
    //移除item
    context.getBindings().remove(item);
    //移除索引
    context.getBindings().remove(index);
    return true;
  }
  //应用索引
  private void applyIndex(DynamicContext context, Object o, int i) {
    if (index != null) {
      context.bind(index, o);
      context.bind(itemizeItem(index, i), o);
    }
  }
  //应用item
  private void applyItem(DynamicContext context, Object o, int i) {
    if (item != null) {
      context.bind(item, o);
      context.bind(itemizeItem(item, i), o);
    }
  }
   //上下文追加（
  private void applyOpen(DynamicContext context) {
    if (open != null) {
      context.appendSql(open);
    }
  }
  //上下文追加 ）
  private void applyClose(DynamicContext context) {
    if (close != null) {
      context.appendSql(close);
    }
  }
  //item
  private static String itemizeItem(String item, int i) {
    return ITEM_PREFIX + item + "_" + i;
  }
  //被过滤的动态上下文
  private static class FilteredDynamicContext extends DynamicContext {
    //动态上下文
    private final DynamicContext delegate;
    //索引
    private final int index;
    //item索引
    private final String itemIndex;
    //item
    private final String item;
    //构造函数
    public FilteredDynamicContext(Configuration configuration,DynamicContext delegate, String itemIndex, String item, int i) {
      super(configuration, null);
      this.delegate = delegate;
      this.index = i;
      this.itemIndex = itemIndex;
      this.item = item;
    }
   //获取绑定
    @Override
    public Map<String, Object> getBindings() {
      return delegate.getBindings();
    }
    //绑定
    @Override
    public void bind(String name, Object value) {
      delegate.bind(name, value);
    }
   //获取sql
    @Override
    public String getSql() {
      return delegate.getSql();
    }
    //追加sql
    @Override
    public void appendSql(String sql) {
      GenericTokenParser parser = new GenericTokenParser("#{", "}", content -> {
        String newContent = content.replaceFirst("^\\s*" + item + "(?![^.,:\\s])", itemizeItem(item, index));
        if (itemIndex != null && newContent.equals(content)) {
          newContent = content.replaceFirst("^\\s*" + itemIndex + "(?![^.,:\\s])", itemizeItem(itemIndex, index));
        }
        return "#{" + newContent + "}";
      });

      delegate.appendSql(parser.parse(sql));
    }
   //获取唯一的值
    @Override
    public int getUniqueNumber() {
      return delegate.getUniqueNumber();
    }

  }

  //前缀上下文
  private class PrefixedContext extends DynamicContext {
    //动态上下文
    private final DynamicContext delegate;
    //前缀
    private final String prefix;
    //前缀是否被应用
    private boolean prefixApplied;
   //前缀上下文
    public PrefixedContext(DynamicContext delegate, String prefix) {
      super(configuration, null);
      this.delegate = delegate;
      this.prefix = prefix;
      this.prefixApplied = false;
    }
    //是否前缀被应用
    public boolean isPrefixApplied() {
      return prefixApplied;
    }
    //获取绑定
    @Override
    public Map<String, Object> getBindings() {
      return delegate.getBindings();
    }
    //绑定名字和value
    @Override
    public void bind(String name, Object value) {
      delegate.bind(name, value);
    }
    //追加sql
    @Override
    public void appendSql(String sql) {
      if (!prefixApplied && sql != null && sql.trim().length() > 0) {
        delegate.appendSql(prefix);
        prefixApplied = true;
      }
      delegate.appendSql(sql);
    }
    //获取sql
    @Override
    public String getSql() {
      return delegate.getSql();
    }
    //获取唯一的数值
    @Override
    public int getUniqueNumber() {
      return delegate.getUniqueNumber();
    }
  }

}
