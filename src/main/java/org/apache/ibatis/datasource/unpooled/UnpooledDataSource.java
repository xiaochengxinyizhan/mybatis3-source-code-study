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
package org.apache.ibatis.datasource.unpooled;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.ibatis.io.Resources;

/**
 * 未池化数据源
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public class UnpooledDataSource implements DataSource {
  //类加载驱动
  private ClassLoader driverClassLoader;
  //驱动属性文件
  private Properties driverProperties;
  //注册驱动容器
  private static Map<String, Driver> registeredDrivers = new ConcurrentHashMap<>();
  //驱动
  private String driver;
  //链接url
  private String url;
  //用户名
  private String username;
  //密码
  private String password;
  //是否自动提交
  private Boolean autoCommit;
  //默认事物级别
  private Integer defaultTransactionIsolationLevel;
  //默认网络超时
  private Integer defaultNetworkTimeout;

  //驱动管理获取驱动并放到注册驱动容器
  static {
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    while (drivers.hasMoreElements()) {
      Driver driver = drivers.nextElement();
      registeredDrivers.put(driver.getClass().getName(), driver);
    }
  }
  //空构造函数
  public UnpooledDataSource() {
  }
  //未池化的数据源封装
  public UnpooledDataSource(String driver, String url, String username, String password) {
    this.driver = driver;
    this.url = url;
    this.username = username;
    this.password = password;
  }
  //未池化的数据源 和驱动属性封装
  public UnpooledDataSource(String driver, String url, Properties driverProperties) {
    this.driver = driver;
    this.url = url;
    this.driverProperties = driverProperties;
  }
  //未池化的数据源，和类加载驱动封装
  public UnpooledDataSource(ClassLoader driverClassLoader, String driver, String url, String username, String password) {
    this.driverClassLoader = driverClassLoader;
    this.driver = driver;
    this.url = url;
    this.username = username;
    this.password = password;
  }
  //未池化的数据源，类加载驱动和驱动属性。
  public UnpooledDataSource(ClassLoader driverClassLoader, String driver, String url, Properties driverProperties) {
    this.driverClassLoader = driverClassLoader;
    this.driver = driver;
    this.url = url;
    this.driverProperties = driverProperties;
  }
  //获取链接
  @Override
  public Connection getConnection() throws SQLException {
    return doGetConnection(username, password);
  }
  //根据用户名和密码获取链接
  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return doGetConnection(username, password);
  }
 //设置登陆超时
  @Override
  public void setLoginTimeout(int loginTimeout) {
    DriverManager.setLoginTimeout(loginTimeout);
  }
 //获取登陆超时
  @Override
  public int getLoginTimeout() {
    return DriverManager.getLoginTimeout();
  }
 //设置日志输出器
  @Override
  public void setLogWriter(PrintWriter logWriter) {
    DriverManager.setLogWriter(logWriter);
  }
  //获取日志输出器
  @Override
  public PrintWriter getLogWriter() {
    return DriverManager.getLogWriter();
  }
  //获取类加载驱动
  public ClassLoader getDriverClassLoader() {
    return driverClassLoader;
  }
  //设置类加载驱动
  public void setDriverClassLoader(ClassLoader driverClassLoader) {
    this.driverClassLoader = driverClassLoader;
  }
  //获取驱动属性
  public Properties getDriverProperties() {
    return driverProperties;
  }
  //设置驱动属性
  public void setDriverProperties(Properties driverProperties) {
    this.driverProperties = driverProperties;
  }
  //获取驱动
  public synchronized String getDriver() {
    return driver;
  }
  //设置驱动
  public synchronized void setDriver(String driver) {
    this.driver = driver;
  }
  //获取URL
  public String getUrl() {
    return url;
  }
  //设置URL
  public void setUrl(String url) {
    this.url = url;
  }
  //获取用户名
  public String getUsername() {
    return username;
  }
  //设置用户名
  public void setUsername(String username) {
    this.username = username;
  }
  //获取密码
  public String getPassword() {
    return password;
  }
  //设置密码
  public void setPassword(String password) {
    this.password = password;
  }
  //是否自动提交
  public Boolean isAutoCommit() {
    return autoCommit;
  }
  //设置自动提交
  public void setAutoCommit(Boolean autoCommit) {
    this.autoCommit = autoCommit;
  }
  //获取默认事物隔离级别
  public Integer getDefaultTransactionIsolationLevel() {
    return defaultTransactionIsolationLevel;
  }
  //设置事物隔离级别
  public void setDefaultTransactionIsolationLevel(Integer defaultTransactionIsolationLevel) {
    this.defaultTransactionIsolationLevel = defaultTransactionIsolationLevel;
  }

  /**
   * 3。5。2获取默认的网络超时时间
   * @since 3.5.2
   */
  public Integer getDefaultNetworkTimeout() {
    return defaultNetworkTimeout;
  }

  /**
   * 设置默认的等待数据库完成操作的时间
   * Sets the default network timeout value to wait for the database operation to complete. See {@link Connection#setNetworkTimeout(java.util.concurrent.Executor, int)}
   *
   * @param defaultNetworkTimeout
   *          The time in milliseconds to wait for the database operation to complete.
   * @since 3.5.2
   */
  public void setDefaultNetworkTimeout(Integer defaultNetworkTimeout) {
    this.defaultNetworkTimeout = defaultNetworkTimeout;
  }
  //获取链接
  private Connection doGetConnection(String username, String password) throws SQLException {
    Properties props = new Properties();
    if (driverProperties != null) {
      props.putAll(driverProperties);
    }
    if (username != null) {
      props.setProperty("user", username);
    }
    if (password != null) {
      props.setProperty("password", password);
    }
    return doGetConnection(props);
  }
  //根据属性获取链接
  private Connection doGetConnection(Properties properties) throws SQLException {
    //初始化驱动
    initializeDriver();
    //获取驱动链接
    Connection connection = DriverManager.getConnection(url, properties);
    //配置链接
    configureConnection(connection);
    return connection;
  }
  //初始化驱动
  private synchronized void initializeDriver() throws SQLException {
    //注册驱动器是否包含驱动
    if (!registeredDrivers.containsKey(driver)) {
      Class<?> driverType;
      try {
        //jdbc的第一步骤 获取类加载驱动类型，比如mysql
        if (driverClassLoader != null) {
          driverType = Class.forName(driver, true, driverClassLoader);
        } else {
          //直接根据类名获取类加载驱动类型
          driverType = Resources.classForName(driver);
        }
        // DriverManager requires the driver to be loaded via the system ClassLoader.
        // http://www.kfu.com/~nsayer/Java/dyn-jdbc.html
        // 驱动管理器 要求驱动被代理系统的类加载器 加载
        Driver driverInstance = (Driver)driverType.getDeclaredConstructor().newInstance();
        //驱动管理器注册驱动
        DriverManager.registerDriver(new DriverProxy(driverInstance));
        registeredDrivers.put(driver, driverInstance);
      } catch (Exception e) {
        throw new SQLException("Error setting driver on UnpooledDataSource. Cause: " + e);
      }
    }
  }
  //配置链接，网络超时时间，是否自动提交，默认的事物隔离级别
  private void configureConnection(Connection conn) throws SQLException {
    if (defaultNetworkTimeout != null) {
      conn.setNetworkTimeout(Executors.newSingleThreadExecutor(), defaultNetworkTimeout);
    }
    if (autoCommit != null && autoCommit != conn.getAutoCommit()) {
      conn.setAutoCommit(autoCommit);
    }
    if (defaultTransactionIsolationLevel != null) {
      conn.setTransactionIsolation(defaultTransactionIsolationLevel);
    }
  }
  //驱动代理类
  private static class DriverProxy implements Driver {
    //驱动类
    private Driver driver;
   //初始化驱动类
    DriverProxy(Driver d) {
      this.driver = d;
    }
   //驱动类 加载url
    @Override
    public boolean acceptsURL(String u) throws SQLException {
      return this.driver.acceptsURL(u);
    }
   //驱动类链接用户url和属性文件
    @Override
    public Connection connect(String u, Properties p) throws SQLException {
      return this.driver.connect(u, p);
    }
    //获取主要的版本
    @Override
    public int getMajorVersion() {
      return this.driver.getMajorVersion();
    }
    //获取小版本
    @Override
    public int getMinorVersion() {
      return this.driver.getMinorVersion();
    }
   //根据url和属性文件获取驱动属性
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
      return this.driver.getPropertyInfo(u, p);
    }
    //jdbc是否符合
    @Override
    public boolean jdbcCompliant() {
      return this.driver.jdbcCompliant();
    }
    //获取父类日志器
    @Override
    public Logger getParentLogger() {
      return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }
  }

  /**
   * 不能封装
   * @param iface
   * @param <T>
   * @return
   * @throws SQLException
   */
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new SQLException(getClass().getName() + " is not a wrapper.");
  }

  /**
   * 是否封装
   * @param iface
   * @return
   * @throws SQLException
   */
  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }

  /**
   * 获取父类的日志器，要求jdk6+
   * @return
   */
  @Override
  public Logger getParentLogger() {
    // requires JDK version 1.6
    return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  }

}
