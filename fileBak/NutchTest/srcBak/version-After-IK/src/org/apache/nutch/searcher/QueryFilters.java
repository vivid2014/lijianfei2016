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

package org.apache.nutch.searcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.nutch.plugin.*;
import org.apache.nutch.searcher.Query.Clause;
import org.apache.nutch.util.ObjectCache;
import org.apache.hadoop.conf.Configuration;

import java.util.*;

import org.apache.lucene.search.BooleanQuery;

/** Creates and caches {@link QueryFilter} implementing plugins.  QueryFilter
 * implementations should define either the "fields" or "raw-fields" attributes
 * for any fields that they process, otherwise these will be ignored by the
 * query parser.  Raw fields are parsed as a single Query.Term, including
 * internal punctuation, while non-raw fields are parsed containing punctuation
 * are parsed as multi-token Query.Phrase's.
 */
public class QueryFilters {
  private static final Log LOG = LogFactory.getLog(QueryFilters.class);

  private QueryFilter[] queryFilters;//设个是什么时候赋予的数据？？
  private HashSet<String> FIELD_NAMES ;//设个是什么时候赋予的数据？？
  private HashSet<String> RAW_FIELD_NAMES;//设个是什么时候赋予的数据？？

  private static List<String> parseFieldNames(Extension extension,
                                           String attribute) {
    String fields = extension.getAttribute(attribute);
    if (fields == null) fields = "";
    return Arrays.asList(fields.split("[,\\s]"));
  }
//这个函数都做了些什么呢？
  public QueryFilters(Configuration conf) {
    ObjectCache objectCache = ObjectCache.get(conf);//从conf配置信息中获取的，一开始是null
    this.queryFilters = (QueryFilter[]) objectCache.getObject(QueryFilter.class
        .getName());//获取了查询过滤
    if (this.queryFilters == null) {
      try {
        ExtensionPoint point = PluginRepository.get(conf)
            .getExtensionPoint(QueryFilter.X_POINT_ID);
        if (point == null)
          throw new RuntimeException(QueryFilter.X_POINT_ID + " not found.");
        Extension[] extensions = point.getExtensions();
        FIELD_NAMES = new HashSet<String>();
        RAW_FIELD_NAMES = new HashSet<String>();
        QueryFilter[] filters = new QueryFilter[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
          Extension extension = extensions[i];
          List<String> fieldNames = parseFieldNames(extension, "fields");
          List<String> rawFieldNames =
            parseFieldNames(extension, "raw-fields");
          if (fieldNames.size() == 0 && rawFieldNames.size() == 0) {
            if (LOG.isWarnEnabled()) {
              LOG.warn("QueryFilter: " + extension.getId()
                     + " names no fields.");
            }
            continue;
          }
          filters[i] = (QueryFilter) extension.getExtensionInstance();//这个具体是干了什么啊？？？
          FIELD_NAMES.addAll(fieldNames);
          FIELD_NAMES.addAll(rawFieldNames);
          objectCache.setObject("FIELD_NAMES", FIELD_NAMES);
          RAW_FIELD_NAMES.addAll(rawFieldNames);
          objectCache.setObject("RAW_FIELD_NAMES", RAW_FIELD_NAMES);
        }
        objectCache.setObject(QueryFilter.class.getName(), filters);
      } catch (PluginRuntimeException e) {
        throw new RuntimeException(e);
      }
      this.queryFilters = (QueryFilter[]) objectCache.getObject(QueryFilter.class
          .getName());
    } else {
      // 第一次运行的时候objectCache中的objectMap已近被赋予了数据，所以后面使用的时候都有了
      FIELD_NAMES = (HashSet<String>) objectCache.getObject("FIELD_NAMES");
      RAW_FIELD_NAMES = (HashSet<String>) objectCache.getObject("RAW_FIELD_NAMES");
    }
  }              

  /** Run all defined filters. */
  public BooleanQuery filter(Query input) throws QueryException {
    // first check that all field names are claimed by some plugin
    Clause[] clauses = input.getClauses();
    for (int i = 0; i < clauses.length; i++) {
      Clause c = clauses[i];
      if (!isField(c.getField()))
        throw new QueryException("Not a known field name:"+c.getField());
    }
    //input: 传  媒；
    //output:
    //+(url:传^4.0 anchor:传^2.0 content:传 title:传^1.5 host:传^2.0) +
    //(url:媒^4.0 anchor:媒^2.0 content:媒 title:媒^1.5 host:媒^2.0) 
    //url:"传 媒"~5^4.0 anchor:"传 媒"~4^2.0 content:"传 媒"~5 title:"传 媒"~5^1.5 host:"传 媒"~5^2.0
    // then run each plugin
    BooleanQuery output = new BooleanQuery();
    for (int i = 0; i < this.queryFilters.length; i++) {
      output = this.queryFilters[i].filter(input, output);
    }
    return output;
  }

  public boolean isField(String name) {
    return FIELD_NAMES.contains(name);
  }
  
  public boolean isRawField(String name) {
    return RAW_FIELD_NAMES.contains(name);
  }
}
