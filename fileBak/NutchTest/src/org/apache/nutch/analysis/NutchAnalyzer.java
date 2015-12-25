/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nutch.analysis;

// JDK imports
import java.io.Reader;

// Lucene imports
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

// Hadoop imports
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;

// Nutch imports
import org.apache.nutch.plugin.Pluggable;


/** 
 * Extension point for analysis.
 * All plugins found which implement this extension point are run
 * sequentially on the parse.
 *
 * @author J&eacute;r&ocirc;me Charron
 */

public abstract class NutchAnalyzer extends Analyzer
                                    implements Configurable, Pluggable {

  /** The name of the extension point. */
  final static String X_POINT_ID = NutchAnalyzer.class.getName();

  /** The current Configuration */
  protected Configuration conf = null;

  
  /**
   * Creates a TokenStream which tokenizes all the text in the provided Reader.
   * 
   * //。该方法是用于分析文本，其后的具体
		类在这个方法中实现了从文本中提取索引词组的策略、算法。
   */
  public abstract TokenStream tokenStream(String fieldName, Reader reader);


  /* ----------------------------- *
   * <implementation:Configurable> *
   * ----------------------------- */

  // Inherited Javadoc
  public void setConf(Configuration conf) {
    this.conf = conf;
  }

  // Inherited Javadoc
  public Configuration getConf() {
    return this.conf;
  }

  /* ------------------------------ *
   * </implementation:Configurable> *
   * ------------------------------ */

}
