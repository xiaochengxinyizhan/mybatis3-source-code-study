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
package org.apache.ibatis.executor;

import java.sql.BatchUpdateException;
import java.util.List;

/**
 * 在任何嵌套批量执行期间，如果 <code>java.sql.BatchUpdateException</code>被捕获，则异常抛出
 * <code>java.sql.BatchUpdateException</code>是根造成的，所以出现该异常一定是没有执行成功。
 * This exception is thrown if a <code>java.sql.BatchUpdateException</code> is caught
 * during the execution of any nested batch.  The exception contains the
 * java.sql.BatchUpdateException that is the root cause, as well as
 * the results from any prior nested batch that executed successfully.
 *
 * @author Jeff Butler
 */
public class BatchExecutorException extends ExecutorException {
  //成功批量结果
  private static final long serialVersionUID = 154049229650533990L;
  private final List<BatchResult> successfulBatchResults;
  //批量更新异常
  private final BatchUpdateException batchUpdateException;
  //批量结果
  private final BatchResult batchResult;
  //构造器
  public BatchExecutorException(String message,
                                BatchUpdateException cause,
                                List<BatchResult> successfulBatchResults,
                                BatchResult batchResult) {
    super(message + " Cause: " + cause, cause);
    this.batchUpdateException = cause;
    this.successfulBatchResults = successfulBatchResults;
    this.batchResult = batchResult;
  }

  /**
   *返回嵌套执行失败的异常，异常包含行数用来帮助定位造成失败的问题。
   * Returns the BatchUpdateException that caused the nested executor
   * to fail.  That exception contains an array of row counts
   * that can be used to determine exactly which statement of the
   * executor caused the failure (or failures).
   *
   * @return the root BatchUpdateException
   */
  public BatchUpdateException getBatchUpdateException() {
    return batchUpdateException;
  }

  /**
   * 返回批量结果，在失败执行前，在每一个成功的子执行器是有一个对象的
   * Returns a list of BatchResult objects.  There will be one entry
   * in the list for each successful sub-executor executed before the failing
   * executor.
   *
   * @return the previously successful executor results (may be an empty list
   *         if no executor has executed successfully)
   */
  public List<BatchResult> getSuccessfulBatchResults() {
    return successfulBatchResults;
  }

  /**
   * 返回造成失败的sql会话
   * Returns the SQL statement that caused the failure
   * (not the parameterArray).
   *
   * @return the failing SQL string
   */
  public String getFailingSqlStatement() {
    return batchResult.getSql();
  }

  /**
   * 返回造成失败的会话ID
   * Returns the statement id of the statement that caused the failure.
   *
   * @return the statement id
   */
  public String getFailingStatementId() {
    return batchResult.getMappedStatement().getId();
  }
}
