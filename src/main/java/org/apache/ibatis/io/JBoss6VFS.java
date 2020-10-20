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
package org.apache.ibatis.io;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * JBoss 6提供的VFS API实现
 * A {@link VFS} implementation that works with the VFS API provided by JBoss 6.
 *
 * @author Ben Gunter
 */
public class JBoss6VFS extends VFS {
  //日志
  private static final Log log = LogFactory.getLog(JBoss6VFS.class);
  //一个类，它模拟JBoss VFS类的一个子集
  /** A class that mimics a tiny subset of the JBoss VirtualFile class. */
  static class VirtualFile {
    //虚拟文件
    static Class<?> VirtualFile;
    //获取路径参数相关，获取子集递归
    static Method getPathNameRelativeTo, getChildrenRecursively;
    //声明自身
    Object virtualFile;
    //构造函数
    VirtualFile(Object virtualFile) {
      this.virtualFile = virtualFile;
    }
    //根据虚拟文件获取路径参数
    String getPathNameRelativeTo(VirtualFile parent) {
      try {
        return invoke(getPathNameRelativeTo, virtualFile, parent.virtualFile);
      } catch (IOException e) {
        // This exception is not thrown by the called method
        log.error("This should not be possible. VirtualFile.getPathNameRelativeTo() threw IOException.");
        return null;
      }
    }
    //获取子集
    List<VirtualFile> getChildren() throws IOException {
      List<?> objects = invoke(getChildrenRecursively, virtualFile);
      List<VirtualFile> children = new ArrayList<>(objects.size());
      for (Object object : objects) {
        children.add(new VirtualFile(object));
      }
      return children;
    }
  }
  //一个类，它模拟JBoss VFS类的一个子集
  /** A class that mimics a tiny subset of the JBoss VFS class. */
  static class VFS {
    static Class<?> VFS;
    static Method getChild;

    private VFS() {
      // Prevent Instantiation
    }

    static VirtualFile getChild(URL url) throws IOException {
      Object o = invoke(getChild, VFS, url);
      return o == null ? null : new VirtualFile(o);
    }
  }

  /** Flag that indicates if this VFS is valid for the current environment. */
  private static Boolean valid;
  //找访问 JBoss 6 VFS 要求的 所有的类和方法
  /** Find all the classes and methods that are required to access the JBoss 6 VFS. */
  protected static synchronized void initialize() {
    if (valid == null) {
      // 假设有效，如果出错 将会之后返回
      // Assume valid. It will get flipped later if something goes wrong.
      valid = Boolean.TRUE;
      // 浏览 并且找到需要的类
      // Look up and verify required classes
      VFS.VFS = checkNotNull(getClass("org.jboss.vfs.VFS"));
      VirtualFile.VirtualFile = checkNotNull(getClass("org.jboss.vfs.VirtualFile"));
      //浏览并找到需要的类
      // Look up and verify required methods
      VFS.getChild = checkNotNull(getMethod(VFS.VFS, "getChild", URL.class));
      VirtualFile.getChildrenRecursively = checkNotNull(getMethod(VirtualFile.VirtualFile,
          "getChildrenRecursively"));
      VirtualFile.getPathNameRelativeTo = checkNotNull(getMethod(VirtualFile.VirtualFile,
          "getPathNameRelativeTo", VirtualFile.VirtualFile));
      //校验API没有被改变
      // Verify that the API has not changed
      checkReturnType(VFS.getChild, VirtualFile.VirtualFile);
      checkReturnType(VirtualFile.getChildrenRecursively, List.class);
      checkReturnType(VirtualFile.getPathNameRelativeTo, String.class);
    }
  }

  /**
   * 校验提供的对象引用不为空，如果为空，那么VFS在当前环境被标记为无效
   * Verifies that the provided object reference is null. If it is null, then this VFS is marked
   * as invalid for the current environment.
   *
   * @param object The object reference to check for null.
   */
  protected static <T> T checkNotNull(T object) {
    if (object == null) {
      setInvalid();
    }
    return object;
  }

  /**
   * 校验返回的方法的类型是期望的，如果不是，那么VFS在当前环境被标记为无效
   * Verifies that the return type of a method is what it is expected to be. If it is not, then
   * this VFS is marked as invalid for the current environment.
   *
   * @param method The method whose return type is to be checked.
   * @param expected A type to which the method's return type must be assignable.
   * @see Class#isAssignableFrom(Class)
   */
  protected static void checkReturnType(Method method, Class<?> expected) {
    if (method != null && !expected.isAssignableFrom(method.getReturnType())) {
      log.error("Method " + method.getClass().getName() + "." + method.getName()
          + "(..) should return " + expected.getName() + " but returns "
          + method.getReturnType().getName() + " instead.");
      setInvalid();
    }
  }
  //标记当前环境的VFS无效/
  /** Mark this {@link VFS} as invalid for the current environment. */
  protected static void setInvalid() {
    if (JBoss6VFS.valid == Boolean.TRUE) {
      log.debug("JBoss 6 VFS API is not available in this environment.");
      JBoss6VFS.valid = Boolean.FALSE;
    }
  }
  //静态代码块
  static {
    initialize();
  }
  //是否资源路径有效
  @Override
  public boolean isValid() {
    return valid;
  }
  //遍历URL的 路径
  @Override
  public List<String> list(URL url, String path) throws IOException {
    VirtualFile directory;
    directory = VFS.getChild(url);
    if (directory == null) {
      return Collections.emptyList();
    }

    if (!path.endsWith("/")) {
      path += "/";
    }

    List<VirtualFile> children = directory.getChildren();
    List<String> names = new ArrayList<>(children.size());
    for (VirtualFile vf : children) {
      names.add(path + vf.getPathNameRelativeTo(directory));
    }

    return names;
  }
}
