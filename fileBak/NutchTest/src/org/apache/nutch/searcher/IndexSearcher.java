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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.nutch.indexer.FsDirectory;
import org.apache.nutch.indexer.NutchSimilarity;

/** Implements {@link Searcher} and {@link HitDetailer} for either a single
 * merged index, or a set of indexes. */
public class IndexSearcher implements Searcher, HitDetailer {

  private org.apache.lucene.search.Searcher luceneSearcher;
  private org.apache.lucene.index.IndexReader reader;
  private LuceneQueryOptimizer optimizer;
  private FileSystem fs;
  private Configuration conf;
  private QueryFilters queryFilters;

  /** Construct given a number of indexes. */
  public IndexSearcher(Path[] indexDirs, Configuration conf) throws IOException {
    IndexReader[] readers = new IndexReader[indexDirs.length];
    this.conf = conf;
    this.fs = FileSystem.get(conf);
    for (int i = 0; i < indexDirs.length; i++) {
      readers[i] = IndexReader.open(getDirectory(indexDirs[i]));
    }
    init(new MultiReader(readers), conf);
  }

  /** Construct given a single merged index. */
  public IndexSearcher(Path index,  Configuration conf)
    throws IOException {
    this.conf = conf;
    this.fs = FileSystem.get(conf);//�����IndexReader.open(Directory directory)��������lucene-core-3.0.1��ʵ�ֵ�
    init(IndexReader.open(getDirectory(index)), conf);//������index��ʼ��(init)һ��IndexSearcher
  }

  private void init(IndexReader reader, Configuration conf) throws IOException {
    this.reader = reader;
    this.luceneSearcher = new org.apache.lucene.search.IndexSearcher(reader);//IndexSearcher�а�һ��luceneSearcher����
    this.luceneSearcher.setSimilarity(new NutchSimilarity());//lucene-core-3.0.1��ʵ�ֵ�
    this.optimizer = new LuceneQueryOptimizer(conf);//��ѯ�Ż���lucene-core-3.0.1��ʵ�ֵ�
    this.queryFilters = new QueryFilters(conf);//��ѯ������
  }

  private Directory getDirectory(Path file) throws IOException {
    if ("file".equals(this.fs.getUri().getScheme())) {
      Path qualified = file.makeQualified(FileSystem.getLocal(conf));
      File fsLocal = new File(qualified.toUri());
      return FSDirectory.open(new File(fsLocal.getAbsolutePath()));
    } else {
      return new FsDirectory(this.fs, file, false, this.conf);
    }
  }


  @Deprecated
  public Hits search(Query query, int numHits,
                     String dedupField, String sortField, boolean reverse)

    throws IOException {
    query.setParams(new QueryParams(numHits,
        QueryParams.DEFAULT_MAX_HITS_PER_DUP, dedupField, sortField, reverse));
    return search(query);
  }
  

  public Hits search(Query query) throws IOException {
    org.apache.lucene.search.BooleanQuery luceneQuery = this.queryFilters.filter(query);//转化为一个lucene查询，和this.queryFilters有关系，修改this.queryFilters可以改变检索结果
    String deupField = query.getParams().getDedupField();
    String SortField = query.getParams().getSortField();
    int NumHits = query.getParams().getNumHits();
    boolean isReverse = query.getParams().isReverse();
    TopDocs docs = optimizer.optimize(luceneQuery, luceneSearcher,NumHits,SortField,isReverse);
    return translateHits(docs, deupField,SortField);
  }


  public String getExplanation(Query query, Hit hit) throws IOException {
    return luceneSearcher.explain(this.queryFilters.filter(query),
        Integer.valueOf(hit.getUniqueKey())).toHtml();
  }

  public HitDetails getDetails(Hit hit) throws IOException {

    Document doc = luceneSearcher.doc(Integer.valueOf(hit.getUniqueKey()));//根据id找到对应的doc

    List<Fieldable> docFields = doc.getFields();
    String[] fields = new String[docFields.size()];
    String[] values = new String[docFields.size()];
    for (int i = 0; i < docFields.size(); i++) {
      Fieldable field = docFields.get(i);
      fields[i] = field.name();//[title, segment, boost, digest, tstamp, url]
      values[i] = field.stringValue();//[CNTV联盟__网络联盟, 20150106195936, 1.0001521, a70317069bfcec9b8ec429360c8bce77, 20150106115949315, http://un.cntv.cn/]
    }

    return new HitDetails(fields, values);
  }

  public HitDetails[] getDetails(Hit[] hits) throws IOException {
    HitDetails[] results = new HitDetails[hits.length];
    for (int i = 0; i < hits.length; i++)
      results[i] = getDetails(hits[i]);
    return results;
  }

  private Hits translateHits(TopDocs topDocs,
                             String dedupField, String sortField)//dedupField=="site"
    throws IOException {

    String[] dedupValues = null;//dedupValues存放爬去到的所有网页site
    if (dedupField != null) 
      dedupValues = FieldCache.DEFAULT.getStrings(reader, dedupField);

    ScoreDoc[] scoreDocs = topDocs.scoreDocs;//排名前十的doc=1244 score=0.4836314, doc=1617 score=0.04386186, doc=320 score=0.03713764, doc=319 score=0.035688944
    int length = scoreDocs.length;
    Hit[] hits = new Hit[length];
    for (int i = 0; i < length; i++) {
      
      int doc = scoreDocs[i].doc;
      
      WritableComparable sortValue;               // convert value to writable
      if (sortField == null) {
        sortValue = new FloatWritable(scoreDocs[i].score);
      } else {
        Object raw = ((FieldDoc)scoreDocs[i]).fields[0];
        if (raw instanceof Integer) {
          sortValue = new IntWritable(((Integer)raw).intValue());
        } else if (raw instanceof Float) {
          sortValue = new FloatWritable(((Float)raw).floatValue());
        } else if (raw instanceof String) {
          sortValue = new Text((String)raw);
        } else {
          throw new RuntimeException("Unknown sort value type!");
        }
      }

      String dedupValue = dedupValues == null ? null : dedupValues[doc];

      hits[i] = new Hit(Integer.toString(doc), sortValue, dedupValue);
    }
    return new Hits(topDocs.totalHits, hits);
  }
  
  public void close() throws IOException {
    if (luceneSearcher != null) { luceneSearcher.close(); }
    if (reader != null) { reader.close(); }
  }

}
