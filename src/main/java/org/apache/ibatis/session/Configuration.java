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
package org.apache.ibatis.session;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiFunction;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.CacheRefResolver;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.builder.annotation.MethodResolver;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.decorators.FifoCache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.decorators.SoftCache;
import org.apache.ibatis.cache.decorators.WeakCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.datasource.jndi.JndiDataSourceFactory;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ReuseExecutor;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.executor.loader.cglib.CglibProxyFactory;
import org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl;
import org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl;
import org.apache.ibatis.logging.log4j.Log4jImpl;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.InterceptorChain;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.LanguageDriverRegistry;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * 全局配置--mybatis的总管家
 * @author Clinton Begin
 */
public class Configuration {
  //环境对象
  protected Environment environment;
  //是否开启边界
  protected boolean safeRowBoundsEnabled;
  //是否开启结果处理器
  protected boolean safeResultHandlerEnabled = true;
  //是否开启下划线映射到驼峰标示
  protected boolean mapUnderscoreToCamelCase;
  //是否懒加载
  protected boolean aggressiveLazyLoading;
  //是否开启多个结果集
  protected boolean multipleResultSetsEnabled = true;
  //是否启用生成主键策略
  protected boolean useGeneratedKeys;
  //是否使用列标签
  protected boolean useColumnLabel = true;
  //是否开启缓存
  protected boolean cacheEnabled = true;
  //是否开启对空值调用setter
  protected boolean callSettersOnNulls;
  //是否使用实际的参数名字
  protected boolean useActualParamName = true;
  //返回空行实例
  protected boolean returnInstanceForEmptyRow;
  //日志前缀
  protected String logPrefix;
  //日志实现
  protected Class<? extends Log> logImpl;
  //vfs实现
  protected Class<? extends VFS> vfsImpl;
  //本地缓存范围--默认session
  protected LocalCacheScope localCacheScope = LocalCacheScope.SESSION;
  //jdbc类型对null值的处理
  protected JdbcType jdbcTypeForNull = JdbcType.OTHER;
  //懒加载触发方法
  protected Set<String> lazyLoadTriggerMethods = new HashSet<>(Arrays.asList("equals", "clone", "hashCode", "toString"));
  //默认的会话超时
  protected Integer defaultStatementTimeout;
  //默认的拉取数量
  protected Integer defaultFetchSize;
  //默认的结果集类型
  protected ResultSetType defaultResultSetType;
  //默认的执行类型
  protected ExecutorType defaultExecutorType = ExecutorType.SIMPLE;
  //自动映射行为
  protected AutoMappingBehavior autoMappingBehavior = AutoMappingBehavior.PARTIAL;
  //自动映射未知列行为
  protected AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior = AutoMappingUnknownColumnBehavior.NONE;

  //变量
  protected Properties variables = new Properties();
  //反射工厂
  protected ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
  //对象工厂
  protected ObjectFactory objectFactory = new DefaultObjectFactory();
  //对象包装工厂
  protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();
  //是否启动懒加载
  protected boolean lazyLoadingEnabled = false;
  //代理工厂
  protected ProxyFactory proxyFactory = new JavassistProxyFactory(); // #224 Using internal Javassist instead of OGNL
  //数据库ID
  protected String databaseId;
  /**
   * 全局配置工厂类--通常用于创建加载反序列化未读属性的配置
   * Configuration factory class.
   * Used to create Configuration for loading deserialized unread properties.
   *
   * @see <a href='https://code.google.com/p/mybatis/issues/detail?id=300'>Issue 300 (google code)</a>
   */
  protected Class<?> configurationFactory;
  //mapper注册器
  protected final MapperRegistry mapperRegistry = new MapperRegistry(this);
  //拦截器链
  protected final InterceptorChain interceptorChain = new InterceptorChain();
  //类型处理器注册器
  protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry(this);
  //类型别名注册器
  protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();
  //语言驱动注册
  protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();
  //映射的会话
  protected final Map<String, MappedStatement> mappedStatements = new StrictMap<MappedStatement>("Mapped Statements collection")
      .conflictMessageProducer((savedValue, targetValue) ->
          ". please check " + savedValue.getResource() + " and " + targetValue.getResource());
  //缓存集合
  protected final Map<String, Cache> caches = new StrictMap<>("Caches collection");
  //映射结果集合
  protected final Map<String, ResultMap> resultMaps = new StrictMap<>("Result Maps collection");
  //参数映射集合
  protected final Map<String, ParameterMap> parameterMaps = new StrictMap<>("Parameter Maps collection");
  //主键生成集合
  protected final Map<String, KeyGenerator> keyGenerators = new StrictMap<>("Key Generators collection");
  //加载资源
  protected final Set<String> loadedResources = new HashSet<>();
  //sql片段
  protected final Map<String, XNode> sqlFragments = new StrictMap<>("XML fragments parsed from previous mappers");
  //不完整的会话
  protected final Collection<XMLStatementBuilder> incompleteStatements = new LinkedList<>();
  //不完整的缓存引用
  protected final Collection<CacheRefResolver> incompleteCacheRefs = new LinkedList<>();
  //不完整的结果映射集合
  protected final Collection<ResultMapResolver> incompleteResultMaps = new LinkedList<>();
  //不完整的方法集合
  protected final Collection<MethodResolver> incompleteMethods = new LinkedList<>();

  /*
  持有缓存引用的map，这个key是引用缓存绑定另外一个命名空间和实际缓存绑定的命名空间的值的命名空间
   * A map holds cache-ref relationship. The key is the namespace that
   * references a cache bound to another namespace and the value is the
   * namespace which the actual cache is bound to.
   */
  protected final Map<String, String> cacheRefMap = new HashMap<>();
  //构造函数
  public Configuration(Environment environment) {
    this();
    this.environment = environment;
  }
  //全局配置
  public Configuration() {
    //类型别名注册器
    typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
    typeAliasRegistry.registerAlias("MANAGED", ManagedTransactionFactory.class);
    //数据源工厂别名注册
    typeAliasRegistry.registerAlias("JNDI", JndiDataSourceFactory.class);
    typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
    typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
    //缓存类型别名注册
    typeAliasRegistry.registerAlias("PERPETUAL", PerpetualCache.class);
    typeAliasRegistry.registerAlias("FIFO", FifoCache.class);
    typeAliasRegistry.registerAlias("LRU", LruCache.class);
    typeAliasRegistry.registerAlias("SOFT", SoftCache.class);
    typeAliasRegistry.registerAlias("WEAK", WeakCache.class);
    //数据库ID别名注册
    typeAliasRegistry.registerAlias("DB_VENDOR", VendorDatabaseIdProvider.class);
    //语言驱动别名注册
    typeAliasRegistry.registerAlias("XML", XMLLanguageDriver.class);
    typeAliasRegistry.registerAlias("RAW", RawLanguageDriver.class);
    //日志实现类别名注册
    typeAliasRegistry.registerAlias("SLF4J", Slf4jImpl.class);
    typeAliasRegistry.registerAlias("COMMONS_LOGGING", JakartaCommonsLoggingImpl.class);
    typeAliasRegistry.registerAlias("LOG4J", Log4jImpl.class);
    typeAliasRegistry.registerAlias("LOG4J2", Log4j2Impl.class);
    typeAliasRegistry.registerAlias("JDK_LOGGING", Jdk14LoggingImpl.class);
    typeAliasRegistry.registerAlias("STDOUT_LOGGING", StdOutImpl.class);
    typeAliasRegistry.registerAlias("NO_LOGGING", NoLoggingImpl.class);
    //代理和字节码代理别名注册
    typeAliasRegistry.registerAlias("CGLIB", CglibProxyFactory.class);
    typeAliasRegistry.registerAlias("JAVASSIST", JavassistProxyFactory.class);
    //默认的驱动类别名注册
    languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
    languageRegistry.register(RawLanguageDriver.class);
  }
  //获取日志前缀
  public String getLogPrefix() {
    return logPrefix;
  }
  //设置日志前缀
  public void setLogPrefix(String logPrefix) {
    this.logPrefix = logPrefix;
  }
  //获取日志实现类
  public Class<? extends Log> getLogImpl() {
    return logImpl;
  }
  //设置日志实现类
  public void setLogImpl(Class<? extends Log> logImpl) {
    if (logImpl != null) {
      this.logImpl = logImpl;
      LogFactory.useCustomLogging(this.logImpl);
    }
  }
  //获取vfs实现类
  public Class<? extends VFS> getVfsImpl() {
    return this.vfsImpl;
  }
  //设置vfs实现类
  public void setVfsImpl(Class<? extends VFS> vfsImpl) {
    if (vfsImpl != null) {
      this.vfsImpl = vfsImpl;
      VFS.addImplClass(this.vfsImpl);
    }
  }
  //是否开启对空值调用setter
  public boolean isCallSettersOnNulls() {
    return callSettersOnNulls;
  }
  //赋值开启对空值调用setter
  public void setCallSettersOnNulls(boolean callSettersOnNulls) {
    this.callSettersOnNulls = callSettersOnNulls;
  }
  //是否使用真实的参数名字
  public boolean isUseActualParamName() {
    return useActualParamName;
  }
  //赋值使用真实的参数名字
  public void setUseActualParamName(boolean useActualParamName) {
    this.useActualParamName = useActualParamName;
  }
  //是否返回空行数据实例
  public boolean isReturnInstanceForEmptyRow() {
    return returnInstanceForEmptyRow;
  }
  //赋值返回空行数据实例
  public void setReturnInstanceForEmptyRow(boolean returnEmptyInstance) {
    this.returnInstanceForEmptyRow = returnEmptyInstance;
  }
  //获取数据库唯一ID
  public String getDatabaseId() {
    return databaseId;
  }
  //设置数据库ID
  public void setDatabaseId(String databaseId) {
    this.databaseId = databaseId;
  }
  //获取配置工厂类
  public Class<?> getConfigurationFactory() {
    return configurationFactory;
  }
  //设置全局配置工厂类
  public void setConfigurationFactory(Class<?> configurationFactory) {
    this.configurationFactory = configurationFactory;
  }
  //是否开启安全结果处理器
  public boolean isSafeResultHandlerEnabled() {
    return safeResultHandlerEnabled;
  }
  //赋值安全结果处理器
  public void setSafeResultHandlerEnabled(boolean safeResultHandlerEnabled) {
    this.safeResultHandlerEnabled = safeResultHandlerEnabled;
  }
  //是否开启安全行边界
  public boolean isSafeRowBoundsEnabled() {
    return safeRowBoundsEnabled;
  }
  //赋值行边界
  public void setSafeRowBoundsEnabled(boolean safeRowBoundsEnabled) {
    this.safeRowBoundsEnabled = safeRowBoundsEnabled;
  }
  //是否映射下划线为驼峰
  public boolean isMapUnderscoreToCamelCase() {
    return mapUnderscoreToCamelCase;
  }
  //赋值下划线为驼峰
  public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
    this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
  }
  //添加已经加载的资源
  public void addLoadedResource(String resource) {
    loadedResources.add(resource);
  }
  //是否资源被加载
  public boolean isResourceLoaded(String resource) {
    return loadedResources.contains(resource);
  }
  //获取环境变量
  public Environment getEnvironment() {
    return environment;
  }
  //设置环境变量
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }
  //自动映射行为
  public AutoMappingBehavior getAutoMappingBehavior() {
    return autoMappingBehavior;
  }
  //设置自动映射行为
  public void setAutoMappingBehavior(AutoMappingBehavior autoMappingBehavior) {
    this.autoMappingBehavior = autoMappingBehavior;
  }

  /**
   *获取自动映射未知列行为
   * @since 3.4.0
   */
  public AutoMappingUnknownColumnBehavior getAutoMappingUnknownColumnBehavior() {
    return autoMappingUnknownColumnBehavior;
  }

  /**设置自动映射未知列行为
   * @since 3.4.0
   */
  public void setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior) {
    this.autoMappingUnknownColumnBehavior = autoMappingUnknownColumnBehavior;
  }

  /**
   * 是否开启懒加载
   * @return
   */
  public boolean isLazyLoadingEnabled() {
    return lazyLoadingEnabled;
  }

  /**
   * 设置懒加载
   * @param lazyLoadingEnabled
   */
  public void setLazyLoadingEnabled(boolean lazyLoadingEnabled) {
    this.lazyLoadingEnabled = lazyLoadingEnabled;
  }

  /**
   * 获取代理工厂
   * @return
   */
  public ProxyFactory getProxyFactory() {
    return proxyFactory;
  }

  /**
   * 设置代理工厂
   * @param proxyFactory
   */
  public void setProxyFactory(ProxyFactory proxyFactory) {
    if (proxyFactory == null) {
      proxyFactory = new JavassistProxyFactory();
    }
    this.proxyFactory = proxyFactory;
  }

  /**
   * 是否是积极的延迟加载
   * @return
   */
  public boolean isAggressiveLazyLoading() {
    return aggressiveLazyLoading;
  }

  /**
   * 设置积极的延迟加载
   * @param aggressiveLazyLoading
   */
  public void setAggressiveLazyLoading(boolean aggressiveLazyLoading) {
    this.aggressiveLazyLoading = aggressiveLazyLoading;
  }

  /**
   * 是否开启多个结果集
   * @return
   */
  public boolean isMultipleResultSetsEnabled() {
    return multipleResultSetsEnabled;
  }

  /**
   * 设置开启多个结果集
   * @param multipleResultSetsEnabled
   */
  public void setMultipleResultSetsEnabled(boolean multipleResultSetsEnabled) {
    this.multipleResultSetsEnabled = multipleResultSetsEnabled;
  }

  /**
   * 获取懒加载触发方法
   * @return
   */
  public Set<String> getLazyLoadTriggerMethods() {
    return lazyLoadTriggerMethods;
  }

  /**
   * 设置懒加载触发方法
   * @param lazyLoadTriggerMethods
   */
  public void setLazyLoadTriggerMethods(Set<String> lazyLoadTriggerMethods) {
    this.lazyLoadTriggerMethods = lazyLoadTriggerMethods;
  }

  /**
   * 是否生成主键
   * @return
   */
  public boolean isUseGeneratedKeys() {
    return useGeneratedKeys;
  }

  /**
   * 设置生成主键
   * @param useGeneratedKeys
   */
  public void setUseGeneratedKeys(boolean useGeneratedKeys) {
    this.useGeneratedKeys = useGeneratedKeys;
  }

  /**
   * 获取默认的执行器类型
   * @return
   */
  public ExecutorType getDefaultExecutorType() {
    return defaultExecutorType;
  }

  /**
   * 设置默认的执行器类型
   * @param defaultExecutorType
   */
  public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
    this.defaultExecutorType = defaultExecutorType;
  }

  /**
   * 是否开启缓存
   * @return
   */
  public boolean isCacheEnabled() {
    return cacheEnabled;
  }

  /**
   * 设置开启缓存
   * @param cacheEnabled
   */
  public void setCacheEnabled(boolean cacheEnabled) {
    this.cacheEnabled = cacheEnabled;
  }

  /**
   * 获取默认会话超时时间
   * @return
   */
  public Integer getDefaultStatementTimeout() {
    return defaultStatementTimeout;
  }

  /**
   * 设置默认的会话超时时间
   * @param defaultStatementTimeout
   */
  public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
    this.defaultStatementTimeout = defaultStatementTimeout;
  }

  /**
   * 获取默认的拉取数量
   * @since 3.3.0
   */
  public Integer getDefaultFetchSize() {
    return defaultFetchSize;
  }

  /**
   * 设置默认的拉取数量
   * @since 3.3.0
   */
  public void setDefaultFetchSize(Integer defaultFetchSize) {
    this.defaultFetchSize = defaultFetchSize;
  }

  /**
   * 获取默认的结果集类型
   * @since 3.5.2
   */
  public ResultSetType getDefaultResultSetType() {
    return defaultResultSetType;
  }

  /**
   * 设置默认的结果集类型
   * @since 3.5.2
   */
  public void setDefaultResultSetType(ResultSetType defaultResultSetType) {
    this.defaultResultSetType = defaultResultSetType;
  }

  /**
   * 是否使用列标签
   * @return
   */
  public boolean isUseColumnLabel() {
    return useColumnLabel;
  }

  /**
   * 设置使用列标签
   * @param useColumnLabel
   */
  public void setUseColumnLabel(boolean useColumnLabel) {
    this.useColumnLabel = useColumnLabel;
  }

  /**
   * 获取本地缓存范围
   * @return
   */
  public LocalCacheScope getLocalCacheScope() {
    return localCacheScope;
  }

  /**
   * 设置本地缓存范围
   * @param localCacheScope
   */
  public void setLocalCacheScope(LocalCacheScope localCacheScope) {
    this.localCacheScope = localCacheScope;
  }

  /**
   * 获取jdbc类型针对null时候
   * @return
   */
  public JdbcType getJdbcTypeForNull() {
    return jdbcTypeForNull;
  }

  /**
   * 设置jdbc类型针对null时候
   * @param jdbcTypeForNull
   */
  public void setJdbcTypeForNull(JdbcType jdbcTypeForNull) {
    this.jdbcTypeForNull = jdbcTypeForNull;
  }

  /**
   * 获取变量
   * @return
   */
  public Properties getVariables() {
    return variables;
  }

  /**
   * 设置变量
   * @param variables
   */
  public void setVariables(Properties variables) {
    this.variables = variables;
  }

  /**
   * 获取类型处理器注册器
   * @return
   */
  public TypeHandlerRegistry getTypeHandlerRegistry() {
    return typeHandlerRegistry;
  }

  /**
   * 为{@link Enum}设置默认的{@link TypeHandler}类。
   * 默认的{@link TypeHandler}是{@link org.apache.ibatis.type.EnumTypeHandler}。
   *
   * Set a default {@link TypeHandler} class for {@link Enum}.
   * A default {@link TypeHandler} is {@link org.apache.ibatis.type.EnumTypeHandler}.
   * @param typeHandler a type handler class for {@link Enum}
   * @since 3.4.5
   */
  public void setDefaultEnumTypeHandler(Class<? extends TypeHandler> typeHandler) {
    if (typeHandler != null) {
      getTypeHandlerRegistry().setDefaultEnumTypeHandler(typeHandler);
    }
  }

  /**
   * 获取类型别名注册器
   * @return
   */
  public TypeAliasRegistry getTypeAliasRegistry() {
    return typeAliasRegistry;
  }

  /**
   * 获取mapper注册器
   * @since 3.2.2
   */
  public MapperRegistry getMapperRegistry() {
    return mapperRegistry;
  }

  /**
   * 获取反射工厂
   * @return
   */
  public ReflectorFactory getReflectorFactory() {
    return reflectorFactory;
  }

  /**
   * 设置反射工厂
   * @param reflectorFactory
   */
  public void setReflectorFactory(ReflectorFactory reflectorFactory) {
    this.reflectorFactory = reflectorFactory;
  }

  /**
   * 获取对象工厂
   * @return
   */
  public ObjectFactory getObjectFactory() {
    return objectFactory;
  }

  /**
   * 设置对象工厂
   * @param objectFactory
   */
  public void setObjectFactory(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  /**
   * 对象修饰工厂
   * @return
   */
  public ObjectWrapperFactory getObjectWrapperFactory() {
    return objectWrapperFactory;
  }

  /**
   * 设置对象修饰工厂
   * @param objectWrapperFactory
   */
  public void setObjectWrapperFactory(ObjectWrapperFactory objectWrapperFactory) {
    this.objectWrapperFactory = objectWrapperFactory;
  }

  /**
   * 获取拦截器
   * @since 3.2.2
   */
  public List<Interceptor> getInterceptors() {
    return interceptorChain.getInterceptors();
  }

  /**
   * 语言驱动注册器
   * @return
   */
  public LanguageDriverRegistry getLanguageRegistry() {
    return languageRegistry;
  }

  /**
   * 设置默认的校验语言
   * @param driver
   */
  public void setDefaultScriptingLanguage(Class<? extends LanguageDriver> driver) {
    if (driver == null) {
      driver = XMLLanguageDriver.class;
    }
    getLanguageRegistry().setDefaultDriverClass(driver);
  }

  /**
   * 获取默认的脚本语言实例
   * @return
   */
  public LanguageDriver getDefaultScriptingLanguageInstance() {
    return languageRegistry.getDefaultDriver();
  }

  /**
   * 获取语言驱动器
   * @since 3.5.1
   */
  public LanguageDriver getLanguageDriver(Class<? extends LanguageDriver> langClass) {
    if (langClass == null) {
      return languageRegistry.getDefaultDriver();
    }
    languageRegistry.register(langClass);
    return languageRegistry.getDriver(langClass);
  }

  /**
   * 不建议使用
   * @deprecated Use {@link #getDefaultScriptingLanguageInstance()}
   */
  @Deprecated
  public LanguageDriver getDefaultScriptingLanuageInstance() {
    return getDefaultScriptingLanguageInstance();
  }

  /**
   * 构建新的元对象
   * @param object
   * @return
   */
  public MetaObject newMetaObject(Object object) {
    return MetaObject.forObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
  }

  /**
   * 构建参数处理器
   * @param mappedStatement
   * @param parameterObject
   * @param boundSql
   * @return
   */
  public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
    ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundSql);
    parameterHandler = (ParameterHandler) interceptorChain.pluginAll(parameterHandler);
    return parameterHandler;
  }

  /**
   * 构建结果集处理器
   * @param executor
   * @param mappedStatement
   * @param rowBounds
   * @param parameterHandler
   * @param resultHandler
   * @param boundSql
   * @return
   */
  public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ParameterHandler parameterHandler,
      ResultHandler resultHandler, BoundSql boundSql) {
    ResultSetHandler resultSetHandler = new DefaultResultSetHandler(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
    resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
    return resultSetHandler;
  }

  /**
   * 构建会话处理器
   * @param executor
   * @param mappedStatement
   * @param parameterObject
   * @param rowBounds
   * @param resultHandler
   * @param boundSql
   * @return
   */
  public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
    StatementHandler statementHandler = new RoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
    statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
    return statementHandler;
  }

  /**
   * 构建执行器
   * @param transaction
   * @return
   */
  public Executor newExecutor(Transaction transaction) {
    return newExecutor(transaction, defaultExecutorType);
  }

  /**
   * 构建执行器
   * @param transaction
   * @param executorType
   * @return
   */
  public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
    executorType = executorType == null ? defaultExecutorType : executorType;
    executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
    Executor executor;
    if (ExecutorType.BATCH == executorType) {
      executor = new BatchExecutor(this, transaction);
    } else if (ExecutorType.REUSE == executorType) {
      executor = new ReuseExecutor(this, transaction);
    } else {
      executor = new SimpleExecutor(this, transaction);
    }
    if (cacheEnabled) {
      executor = new CachingExecutor(executor);
    }
    executor = (Executor) interceptorChain.pluginAll(executor);
    return executor;
  }

  /**
   * 添加主键生成策略
   * @param id
   * @param keyGenerator
   */
  public void addKeyGenerator(String id, KeyGenerator keyGenerator) {
    keyGenerators.put(id, keyGenerator);
  }

  /**
   * 获取生成主键名字集合
   * @return
   */
  public Collection<String> getKeyGeneratorNames() {
    return keyGenerators.keySet();
  }

  /**
   * 获取主键生成值
   * @return
   */
  public Collection<KeyGenerator> getKeyGenerators() {
    return keyGenerators.values();
  }

  /**
   * 获取主键生成值
   * @param id
   * @return
   */
  public KeyGenerator getKeyGenerator(String id) {
    return keyGenerators.get(id);
  }

  /**
   * 是否有主键生成
   * @param id
   * @return
   */
  public boolean hasKeyGenerator(String id) {
    return keyGenerators.containsKey(id);
  }

  /**
   * 添加缓存
   * @param cache
   */
  public void addCache(Cache cache) {
    caches.put(cache.getId(), cache);
  }

  /**
   * 获取缓存名字
   * @return
   */
  public Collection<String> getCacheNames() {
    return caches.keySet();
  }

  /**
   * 获取缓存集合
   * @return
   */
  public Collection<Cache> getCaches() {
    return caches.values();
  }

  /**
   * 获取指定缓存
   * @param id
   * @return
   */
  public Cache getCache(String id) {
    return caches.get(id);
  }

  /**
   * 有缓存
   * @param id
   * @return
   */
  public boolean hasCache(String id) {
    return caches.containsKey(id);
  }

  /**
   * 添加结果集映射
   * @param rm
   */
  public void addResultMap(ResultMap rm) {
    resultMaps.put(rm.getId(), rm);
    checkLocallyForDiscriminatedNestedResultMaps(rm);
    checkGloballyForDiscriminatedNestedResultMaps(rm);
  }

  /**
   * 获取结果集映射名字
   * @return
   */
  public Collection<String> getResultMapNames() {
    return resultMaps.keySet();
  }

  /**
   * 获取结果集映射
   * @return
   */
  public Collection<ResultMap> getResultMaps() {
    return resultMaps.values();
  }

  /**
   * 获取结果集映射
   * @param id
   * @return
   */
  public ResultMap getResultMap(String id) {
    return resultMaps.get(id);
  }

  /**
   * 是否有该结果
   * @param id
   * @return
   */
  public boolean hasResultMap(String id) {
    return resultMaps.containsKey(id);
  }

  /**
   * 添加参数映射
   * @param pm
   */
  public void addParameterMap(ParameterMap pm) {
    parameterMaps.put(pm.getId(), pm);
  }

  /**
   * 获取参数映射名字
   * @return
   */
  public Collection<String> getParameterMapNames() {
    return parameterMaps.keySet();
  }

  /**
   * 获取参数映射
   * @return
   */
  public Collection<ParameterMap> getParameterMaps() {
    return parameterMaps.values();
  }

  /**
   * 获取参数映射
   * @param id
   * @return
   */
  public ParameterMap getParameterMap(String id) {
    return parameterMaps.get(id);
  }

  /**
   * 有参数映射
   * @param id
   * @return
   */
  public boolean hasParameterMap(String id) {
    return parameterMaps.containsKey(id);
  }

  /**
   * 添加映射的会话
   * @param ms
   */
  public void addMappedStatement(MappedStatement ms) {
    mappedStatements.put(ms.getId(), ms);
  }

  /**
   * 获取映射的会话名字
   * @return
   */
  public Collection<String> getMappedStatementNames() {
    buildAllStatements();
    return mappedStatements.keySet();
  }

  /**
   * 获取映射的会话
   * @return
   */
  public Collection<MappedStatement> getMappedStatements() {
    buildAllStatements();
    return mappedStatements.values();
  }

  /**
   * 获取不完整的会话
   * @return
   */
  public Collection<XMLStatementBuilder> getIncompleteStatements() {
    return incompleteStatements;
  }

  /**
   * 添加不完整的会话
   * @param incompleteStatement
   */
  public void addIncompleteStatement(XMLStatementBuilder incompleteStatement) {
    incompleteStatements.add(incompleteStatement);
  }

  /**
   * 获取不完整的缓存引用
   * @return
   */
  public Collection<CacheRefResolver> getIncompleteCacheRefs() {
    return incompleteCacheRefs;
  }

  /**
   * 添加不完整的缓存引用
   * @param incompleteCacheRef
   */
  public void addIncompleteCacheRef(CacheRefResolver incompleteCacheRef) {
    incompleteCacheRefs.add(incompleteCacheRef);
  }

  /**
   * 获取不完整的结果映射
   * @return
   */
  public Collection<ResultMapResolver> getIncompleteResultMaps() {
    return incompleteResultMaps;
  }

  /**
   * 添加不完整的结果映射
   * @param resultMapResolver
   */
  public void addIncompleteResultMap(ResultMapResolver resultMapResolver) {
    incompleteResultMaps.add(resultMapResolver);
  }

  /**
   * 添加不完整的方法
   * @param builder
   */
  public void addIncompleteMethod(MethodResolver builder) {
    incompleteMethods.add(builder);
  }

  /**
   * 获取不完整的方法
   * @return
   */
  public Collection<MethodResolver> getIncompleteMethods() {
    return incompleteMethods;
  }

  /**
   * 获取映射的会话
   * @param id
   * @return
   */
  public MappedStatement getMappedStatement(String id) {
    return this.getMappedStatement(id, true);
  }

  /**
   * 获取映射的会话
   * @param id
   * @param validateIncompleteStatements
   * @return
   */
  public MappedStatement getMappedStatement(String id, boolean validateIncompleteStatements) {
    if (validateIncompleteStatements) {
      buildAllStatements();
    }
    return mappedStatements.get(id);
  }

  /**
   * 获取sql片段
   * @return
   */
  public Map<String, XNode> getSqlFragments() {
    return sqlFragments;
  }

  /**
   * 添加拦截器
   * @param interceptor
   */
  public void addInterceptor(Interceptor interceptor) {
    interceptorChain.addInterceptor(interceptor);
  }

  /**
   * 根据包名添加mapper
   * @param packageName
   * @param superType
   */
  public void addMappers(String packageName, Class<?> superType) {
    mapperRegistry.addMappers(packageName, superType);
  }

  /**
   * 根据包名添加mapper
   * @param packageName
   */
  public void addMappers(String packageName) {
    mapperRegistry.addMappers(packageName);
  }

  /**
   * 添加mapper
   * @param type
   * @param <T>
   */
  public <T> void addMapper(Class<T> type) {
    mapperRegistry.addMapper(type);
  }

  /**
   * 获取mapper
   * @param type
   * @param sqlSession
   * @param <T>
   * @return
   */
  public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    return mapperRegistry.getMapper(type, sqlSession);
  }

  /**
   * 是否有该mapper
   * @param type
   * @return
   */
  public boolean hasMapper(Class<?> type) {
    return mapperRegistry.hasMapper(type);
  }

  /**
   * 是否有会话
   * @param statementName
   * @return
   */
  public boolean hasStatement(String statementName) {
    return hasStatement(statementName, true);
  }

  /**
   * 是否有会话
   * @param statementName
   * @param validateIncompleteStatements
   * @return
   */
  public boolean hasStatement(String statementName, boolean validateIncompleteStatements) {
    if (validateIncompleteStatements) {
      buildAllStatements();
    }
    return mappedStatements.containsKey(statementName);
  }

  /**
   * 添加缓存引用
   * @param namespace
   * @param referencedNamespace
   */
  public void addCacheRef(String namespace, String referencedNamespace) {
    cacheRefMap.put(namespace, referencedNamespace);
  }

  /*
    解析在缓存中的所有的未进行的会话节点。当她提供快速会话校验的时候 被推荐使用调用这个方法一次所有的mapper被加载，
   * Parses all the unprocessed statement nodes in the cache. It is recommended
   * to call this method once all the mappers are added as it provides fail-fast
   * statement validation.
   */
  protected void buildAllStatements() {
    parsePendingResultMaps();
    if (!incompleteCacheRefs.isEmpty()) {
      synchronized (incompleteCacheRefs) {
        incompleteCacheRefs.removeIf(x -> x.resolveCacheRef() != null);
      }
    }
    if (!incompleteStatements.isEmpty()) {
      synchronized (incompleteStatements) {
        incompleteStatements.removeIf(x -> {
          x.parseStatementNode();
          return true;
        });
      }
    }
    if (!incompleteMethods.isEmpty()) {
      synchronized (incompleteMethods) {
        incompleteMethods.removeIf(x -> {
          x.resolve();
          return true;
        });
      }
    }
  }
  //解析等待的结果map集
  private void parsePendingResultMaps() {
    if (incompleteResultMaps.isEmpty()) {
      return;
    }
    synchronized (incompleteResultMaps) {
      boolean resolved;
      IncompleteElementException ex = null;
      do {
        resolved = false;
        Iterator<ResultMapResolver> iterator = incompleteResultMaps.iterator();
        while (iterator.hasNext()) {
          try {
            iterator.next().resolve();
            iterator.remove();
            resolved = true;
          } catch (IncompleteElementException e) {
            ex = e;
          }
        }
      } while (resolved);
      if (!incompleteResultMaps.isEmpty() && ex != null) {
        // At least one result map is unresolvable.
        throw ex;
      }
    }
  }

  /**
   * 提取命名空间从全指定的会话ID
   * Extracts namespace from fully qualified statement id.
   *
   * @param statementId
   * @return namespace or null when id does not contain period.
   */
  protected String extractNamespace(String statementId) {
    int lastPeriod = statementId.lastIndexOf('.');
    return lastPeriod > 0 ? statementId.substring(0, lastPeriod) : null;
  }
  // 慢，但一次性的时间花费。期待更好的方法
  // Slow but a one time cost. A better solution is welcome.
  protected void checkGloballyForDiscriminatedNestedResultMaps(ResultMap rm) {
    if (rm.hasNestedResultMaps()) {
      for (Map.Entry<String, ResultMap> entry : resultMaps.entrySet()) {
        Object value = entry.getValue();
        if (value instanceof ResultMap) {
          ResultMap entryResultMap = (ResultMap) value;
          if (!entryResultMap.hasNestedResultMaps() && entryResultMap.getDiscriminator() != null) {
            Collection<String> discriminatedResultMapNames = entryResultMap.getDiscriminator().getDiscriminatorMap().values();
            if (discriminatedResultMapNames.contains(rm.getId())) {
              entryResultMap.forceNestedResultMaps();
            }
          }
        }
      }
    }
  }
  // 慢，但一次性的时间花费。期待更好的方法
  // Slow but a one time cost. A better solution is welcome.
  protected void checkLocallyForDiscriminatedNestedResultMaps(ResultMap rm) {
    if (!rm.hasNestedResultMaps() && rm.getDiscriminator() != null) {
      for (Map.Entry<String, String> entry : rm.getDiscriminator().getDiscriminatorMap().entrySet()) {
        String discriminatedResultMapName = entry.getValue();
        if (hasResultMap(discriminatedResultMapName)) {
          ResultMap discriminatedResultMap = resultMaps.get(discriminatedResultMapName);
          if (discriminatedResultMap.hasNestedResultMaps()) {
            rm.forceNestedResultMaps();
            break;
          }
        }
      }
    }
  }
  //严格的map内部实现类
  protected static class StrictMap<V> extends HashMap<String, V> {

    private static final long serialVersionUID = -4950446264854982944L;
    private final String name;
    private BiFunction<V, V, String> conflictMessageProducer;
     //构造函数
    public StrictMap(String name, int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
      this.name = name;
    }
    //构造函数
    public StrictMap(String name, int initialCapacity) {
      super(initialCapacity);
      this.name = name;
    }
    //构造函数
    public StrictMap(String name) {
      super();
      this.name = name;
    }

    public StrictMap(String name, Map<String, ? extends V> m) {
      super(m);
      this.name = name;
    }

    /**
     * 分配一个函数 产生一个冲突错误信息当包含相同key和值
     * Assign a function for producing a conflict error message when contains value with the same key.
     * <p>
     * function arguments are 1st is saved value and 2nd is target value.
     * @param conflictMessageProducer A function for producing a conflict error message
     * @return a conflict error message
     * @since 3.5.0
     */
    public StrictMap<V> conflictMessageProducer(BiFunction<V, V, String> conflictMessageProducer) {
      this.conflictMessageProducer = conflictMessageProducer;
      return this;
    }
   //存放属性和对象
    @Override
    @SuppressWarnings("unchecked")
    public V put(String key, V value) {
      if (containsKey(key)) {
        throw new IllegalArgumentException(name + " already contains value for " + key
            + (conflictMessageProducer == null ? "" : conflictMessageProducer.apply(super.get(key), value)));
      }
      if (key.contains(".")) {
        final String shortKey = getShortName(key);
        if (super.get(shortKey) == null) {
          super.put(shortKey, value);
        } else {
          super.put(shortKey, (V) new Ambiguity(shortKey));
        }
      }
      return super.put(key, value);
    }
   //获取全局配置的属性对应的值
    @Override
    public V get(Object key) {
      V value = super.get(key);
      if (value == null) {
        throw new IllegalArgumentException(name + " does not contain value for " + key);
      }
      if (value instanceof Ambiguity) {
        throw new IllegalArgumentException(((Ambiguity) value).getSubject() + " is ambiguous in " + name
            + " (try using the full name including the namespace, or rename one of the entries)");
      }
      return value;
    }
  //内部语义不明的类
    protected static class Ambiguity {
      final private String subject;

      public Ambiguity(String subject) {
        this.subject = subject;
      }

      public String getSubject() {
        return subject;
      }
    }
    //获取短名称
    private String getShortName(String key) {
      final String[] keyParts = key.split("\\.");
      return keyParts[keyParts.length - 1];
    }
  }

}
