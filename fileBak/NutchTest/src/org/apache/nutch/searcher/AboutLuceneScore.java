package org.apache.nutch.searcher;
import java.io.IOException;
import java.util.Date;

import net.teamhot.lucene.ThesaurusAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.LockObtainFailedException;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.util.StringUtils;
import org.apache.nutch.parse.*;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.util.NutchConfiguration;


public class AboutLuceneScore {
	private String path = "E:\\Lucene\\index";
	
public void createIndex(){
	   IndexWriter writer;
	   try {
	    writer = new IndexWriter(path,new ThesaurusAnalyzer(),true);
	   
	    Field fieldA = new Field("contents","一人",Field.Store.YES,Field.Index.TOKENIZED); 
	    Document docA = new Document(); 
     docA.add(fieldA);
   
	    Field fieldB = new Field("contents","一人 之交 一人之交",Field.Store.YES,Field.Index.TOKENIZED);
	     Document docB = new Document(); 
	    docB.add(fieldB);
    
	    Field fieldC = new Field("contents","一人 之下 一人之下",Field.Store.YES,Field.Index.TOKENIZED);
	     Document docC = new Document(); 
	     docC.add(fieldC);
	    
	    Field fieldD = new Field("contents","一人 做事 一人当 一人做事一人当",Field.Store.YES,Field.Index.TOKENIZED); 
	     Document docD = new Document(); 
	     docD.add(fieldD);
	    
	    Field fieldE = new Field("contents","一人 做事 一人當 一人做事一人當",Field.Store.YES,Field.Index.TOKENIZED);
	    Document docE = new Document(); 
	     docE.add(fieldE);
	
	     writer.addDocument(docA);
	     writer.addDocument(docB);
	     writer.addDocument(docC);
	     writer.addDocument(docD);
	     writer.addDocument(docE);
	   
	     writer.close();
	    } catch (CorruptIndexException e) {
	     e.printStackTrace();
	   } catch (LockObtainFailedException e) {
	    e.printStackTrace();
	   } catch (IOException e) {
		   e.printStackTrace();
	    }
	 }
	 
	 public static void main(String[] args) {
    AboutLuceneScore aus = new AboutLuceneScore();
	    aus.createIndex();    // 建立索引
	    try {
	     String keyword = "一人";
	     Term term = new Term("contents",keyword);
	     Query query = new TermQuery(term); 
	     IndexSearcher searcher = new IndexSearcher(aus.path);
	     Date startTime = new Date();
	     Hits hits = searcher.search(query);
	     TermDocs termDocs = searcher.getIndexReader().termDocs(term);
	    while(termDocs.next()){
	      System.out.print("搜索关键字<"+keyword+">在编号为 "+termDocs.doc());
	      System.out.println(" 的Document中出现过 "+termDocs.freq()+" 次");
	     }
	     System.out.println("********************************************************************");
	     for(int i=0;i<hits.length();i++){
	      System.out.println("Document的内部编号为 ： "+hits.id(i));
	      System.out.println("Document内容为 ： "+hits.doc(i));
	      System.out.println("Document得分为 ： "+hits.score(i));
	      Explanation e = searcher.explain(query, hits.id(i));
	      System.out.println("Explanation为 ： \n"+e);
	      System.out.println("Document对应的Explanation的一些参数值如下： ");
	      System.out.println("Explanation的getValue()为 ： "+e.getValue());
	      System.out.println("Explanation的getDescription()为 ： "+e.getDescription());
	      System.out.println("********************************************************************");
	     }
	    System.out.println("共检索出符合条件的Document "+.length()+" 个。");
	     Date finishTime = new Date();
	     long timeOfSearch = finishTime.getTime() - startTime.getTime();
	     System.out.println("本次搜索所用的时间为 "+timeOfSearch+" ms");
	    } catch (CorruptIndexException e) {
	     e.printStackTrace();
	    } catch (IOException e) {
	     e.printStackTrace();
	    }
	   
	 }
}
