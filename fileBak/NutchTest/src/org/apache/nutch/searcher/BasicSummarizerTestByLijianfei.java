package org.apache.nutch.searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

// Hadoop imports
import org.apache.hadoop.conf.Configuration;

// Lucene imports
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.*;

// Nutch imports
import org.apache.nutch.analysis.NutchDocumentAnalyzer;
import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.Summarizer;
import org.apache.nutch.searcher.Summary;
import org.apache.nutch.searcher.Summary.Ellipsis;
import org.apache.nutch.searcher.Summary.Fragment;
import org.apache.nutch.searcher.Summary.Highlight;

import org.apache.nutch.util.NutchConfiguration;


/** Implements hit summarization. */
public class BasicSummarizerTestByLijianfei implements Summarizer {
  
  private int sumContext = 10;//5
  private int sumLength = 100;//20
  private Analyzer analyzer = null;
  private Configuration conf = null;
  
  private final static Comparator ORDER_COMPARATOR = new Comparator() {
    public int compare(Object o1, Object o2) {
      return ((Excerpt) o1).getOrder() - ((Excerpt) o2).getOrder();
    }
  };
  
  private final static Comparator SCORE_COMPARATOR = new Comparator() {
    public int compare(Object o1, Object o2) {
      Excerpt excerpt1 = (Excerpt) o1;//第二个对象
      Excerpt excerpt2 = (Excerpt) o2;//第一个对象

      if (excerpt1 == null && excerpt2 != null) {
        return -1;
      } else if (excerpt1 != null && excerpt2 == null) {
        return 1;
      } else if (excerpt1 == null && excerpt2 == null) {
        return 0;
      }

      int numToks1 = excerpt1.numUniqueTokens();//找到这个term里面有几个搜索关键词出现
      int numToks2 = excerpt2.numUniqueTokens();

      if (numToks1 < numToks2) {
        return -1;
      } else if (numToks1 == numToks2) {
    	  int len1 = excerpt1.numFragments();
    	  int len2 = excerpt2.numFragments();
        return len1 - len2;//如果俩个export中关键词出现次数相同，那么返回他们的passages的长度的差值
      } else {
        return 1;
      }
    }
  };

  
  public BasicSummarizerTestByLijianfei() { }
  
  private BasicSummarizerTestByLijianfei(Configuration conf) {
    setConf(conf);
  }
  
  
  /* ----------------------------- *
   * <implementation:Configurable> *
   * ----------------------------- */
  
  public Configuration getConf() {
    return conf;
  }
  
  public void setConf(Configuration conf) {
    this.conf = conf;
    this.analyzer = new NutchDocumentAnalyzer(conf);
    this.sumContext = conf.getInt("searcher.summary.context", 5);//5这里修改配置文件怎么数据没有改变呢,是不是在插件里面。
    this.sumLength = conf.getInt("searcher.summary.length", 20);//20
  }
  
  /* ------------------------------ *
   * </implementation:Configurable> *
   * ------------------------------ */
  
  
  /* --------------------------- *
   * <implementation:Summarizer> *
   * --------------------------- */
  
  //原代码的摘要提取核心算法
  
  public Summary getSummaryOrg(String text, Query query) {

	    
	    // Simplistic implementation.  Finds the first fragments in the document
	    // containing any query terms.
	    //
	    // TODO: check that phrases in the query are matched in the fragment
	    
	    Token[] tokens = getTokens(text);             // parse text to token array
	    
	    if (tokens.length == 0)
	      return new Summary();
	    
	    String[] terms = query.getTerms();
	    HashSet highlight = new HashSet();            // put query terms in table
	    for (int i = 0; i < terms.length; i++)
	      highlight.add(terms[i]);
	    
	    // A list to store document's excerpts.
	    // (An excerpt is a Vector full of Fragments and Highlights)
	    List excerpts = new ArrayList();
	    
	    //
	    // Iterate through all terms in the document
	    //
	    int lastExcerptPos = 0;
	    for (int i = 0; i < tokens.length; i++) {
	      //
	      // If we find a term that's in the query...
	      //
	      if (highlight.contains(tokens[i].term())) {
	        //
	        // Start searching at a point SUM_CONTEXT terms back,
	        // and move SUM_CONTEXT terms into the future.
	        //
	        int startToken = (i > sumContext) ? i - sumContext : 0;
	        int endToken = Math.min(i + sumContext, tokens.length);
	        int offset = tokens[startToken].startOffset();
	        int j = startToken;
	        
	        //
	        // Iterate from the start point to the finish, adding
	        // terms all the way.  The end of the passage is always
	        // SUM_CONTEXT beyond the last query-term.
	        //
	        Excerpt excerpt = new Excerpt(i);
	        if (i != 0) {
	          excerpt.add(new Summary.Ellipsis());
	        }
	        
	        //
	        // Iterate through as long as we're before the end of
	        // the document and we haven't hit the max-number-of-items
	        // -in-a-summary.
	        //
	        while ((j < endToken) && (j - startToken < sumLength)) {
	          //
	          // Now grab the hit-element, if present
	          //
	          Token t = tokens[j];
	          if (highlight.contains(t.term())) {
	            excerpt.addToken(t.term());
	            excerpt.add(new Fragment(text.substring(offset, t.startOffset())));
	            excerpt.add(new Highlight(text.substring(t.startOffset(),t.endOffset())));
	            offset = t.endOffset();
	            endToken = Math.min(j + sumContext, tokens.length);
	          }
	          
	          j++;
	        }
	        
	        lastExcerptPos = endToken;
	        
	        //
	        // We found the series of search-term hits and added
	        // them (with intervening text) to the excerpt.  Now
	        // we need to add the trailing edge of text.
	        //
	        // So if (j < tokens.length) then there is still trailing
	        // text to add.  (We haven't hit the end of the source doc.)
	        // Add the words since the last hit-term insert.
	        //
	        if (j < tokens.length) {
	          excerpt.add(new Fragment(text.substring(offset,tokens[j].endOffset())));
	        }
	        
	        //
	        // Remember how many terms are in this excerpt
	        //
	        excerpt.setNumTerms(j - startToken);
	        
	        //
	        // Store the excerpt for later sorting
	        //
	        excerpts.add(excerpt);
	        System.out.println("i="+i+": "+excerpt.passages.toString());
	        //
	        // Start SUM_CONTEXT places away.  The next
	        // search for relevant excerpts begins at i-SUM_CONTEXT
	        //
	        i = j + sumContext;
	      }
	    }
	    
	    // Sort the excerpts based on their score
	    Collections.sort(excerpts, SCORE_COMPARATOR);
	    
	    //
	    // If the target text doesn't appear, then we just
	    // excerpt the first SUM_LENGTH words from the document.
	    //
	    if (excerpts.size() == 0) {
	      Excerpt excerpt = new Excerpt(0);
	      int excerptLen = Math.min(sumLength, tokens.length);
	      lastExcerptPos = excerptLen;
	      
	      excerpt.add(new Fragment(text.substring(tokens[0].startOffset(), tokens[excerptLen-1].startOffset())));
	      excerpt.setNumTerms(excerptLen);
	      excerpts.add(excerpt);
	    }
	    
	    //
	    // Now choose the best items from the excerpt set.
	    // Stop when we have enought excerpts to build our Summary.
	    //
	    double tokenCount = 0;
	    int numExcerpt = excerpts.size()-1;
	    List bestExcerpts = new ArrayList();
	    while (tokenCount <= sumLength && numExcerpt >= 0) {
	      Excerpt excerpt = (Excerpt) excerpts.get(numExcerpt--);
	      bestExcerpts.add(excerpt);
	      tokenCount += excerpt.getNumTerms();
	    }    
	    // Sort the best excerpts based on their natural order
	    Collections.sort(bestExcerpts, ORDER_COMPARATOR);
	    
	    //
	    // Now build our Summary from the best the excerpts.
	    //
	    tokenCount = 0;
	    numExcerpt = 0;
	    Summary s = new Summary();
	    //test by lijianfei
	    Fragment fraTest = new Fragment("org-org-org-org-org-org-org：");
	    s.add(fraTest);
	    
	    while (tokenCount <= sumLength && numExcerpt < bestExcerpts.size()) {
	      Excerpt excerpt = (Excerpt) bestExcerpts.get(numExcerpt++);
	      double tokenFraction = (1.0 * excerpt.getNumTerms()) / excerpt.numFragments();
	      for (Enumeration e = excerpt.elements(); e.hasMoreElements(); ) {
	        Fragment f = (Fragment) e.nextElement();
	        // Don't add fragments if it takes us over the max-limit
	        if (tokenCount + tokenFraction <= sumLength) {
	          s.add(f);
	        }
	        tokenCount += tokenFraction;
	      }
	    }
	    if (tokenCount > 0 && lastExcerptPos < tokens.length)
	      s.add(new Ellipsis());

	    return s;
	  
  }
  
  //源代码的摘要提取核心算法
  
  
  
  
  
  public Summary getSummary(String text, Query query) {
    
    // Simplistic implementation.  Finds the first fragments in the document
    // containing any query terms.
    //
    // TODO: check that phrases in the query are matched in the fragment
    
    Token[] tokens = getTokens(text);             // parse text to token array
    
    if (tokens.length == 0)
      return new Summary();
    
    String[] terms = query.getTerms();
    //todobyli 判断这个查询词是否是文本的句首，如果是的话把第一段截取下来作为摘要直接返回。
    boolean isHeadFlag = isSentenceHeand(text,query);//by lijianfei
    String firstSegment =null;
    if(isHeadFlag){
    	firstSegment = getFirstSegment(text);
    	firstSegment  = firstSegment + "..."; 
    	Summary summaryOffirstSegment = new Summary();
    	Fragment fraTest = new Fragment(firstSegment);
    	summaryOffirstSegment.add(fraTest);
    	return summaryOffirstSegment;
    }
    //todobyli
    HashSet highlight = new HashSet();            // put query terms in table
    for (int i = 0; i < terms.length; i++)
      highlight.add(terms[i]);
    
    // A list to store document's excerpts.
    // (An excerpt is a Vector full of Fragments and Highlights)
    List excerpts = new ArrayList();
    
    //
    // Iterate through all terms in the document
    //
    int lastExcerptPos = 0;
    for (int i = 0; i < tokens.length; i++) {
      //
      // If we find a term that's in the query...
      //
    	String cur_term = tokens[i].term();
      if (highlight.contains(tokens[i].term())) {//先找到本次搜索的第一个位置，然后确定一个窗口，根据这个窗口产生一个摘要片段
        //
        // Start searching at a point SUM_CONTEXT terms back,
        // and move SUM_CONTEXT terms into the future.
        //
        int startToken = (i > sumContext) ? i - sumContext : 0;// 向前移动sumContext个位置 
        int endToken = Math.min(i + sumContext, tokens.length);// 向后移动sumContext个位置，所以先找到本次搜索的第一个位置，然后把搜索范围定位在［i - sumContext，i + sumContext］
        int offset = tokens[startToken].startOffset();// 改进思路3:----在原文本中的位置，分析：其实可以从前一个结束位置（180），开始往后找，找这段文本中最后一个句子的开始位置i，然后截取位置i到搜索关键词t之间的文本，这样更有意义。
        int j = startToken;
        if(offset>0)//by lijianfei,加上下面俩句就是改进思路3的做法
        offset = tokens[lastExcerptPos].startOffset()-1;//by lijianfei 从前一个结束位置（180)开始往后找，找找这段文本中最后一个句子的开始位置i，然后截取位置i到搜索关键词t之间的文本
        //
        // Iterate from the start point to the finish, adding
        // terms all the way.  The end of the passage is always
        // SUM_CONTEXT beyond the last query-term.
        //
        Excerpt excerpt = new Excerpt(i);
        if (i != 0) {
          excerpt.add(new Summary.Ellipsis());
        }
        
        //
        // Iterate through as long as we're before the end of
        // the document and we haven't hit the max-number-of-items
        // -in-a-summary.分析：也就是说在一次查找中sumContext内个字符中没有找到，则本次摘要查找结束
        //
        while ((j < endToken) && (j - startToken < sumLength)) {//sumLength是设定的查找范围的最长长度，也就是
          //
          // Now grab the hit-element, if present创新思路1：如果是搜索的人名，如果找到这个名称在句首，则把这个句子截取下来。基本上这个句子是对这个名称的介绍。
          //
          Token t = tokens[j];
          if (highlight.contains(t.term())) {
        	   //by li
               String textCru = text.substring(offset, t.startOffset());//这里offset很关键
               String textAfterLastSymbol = getPartySegmentAfterLastSymbol(textCru); //截取最后一个标点符号后面的文本
               if(textAfterLastSymbol.equals("")||textAfterLastSymbol == null){
            	   textAfterLastSymbol = textCru;
               }
               //by li
	            excerpt.addToken(t.term());                //
	            excerpt.add(new Fragment(textAfterLastSymbol));// 前一个关键词和这个关键词之间的所有文本,如果把textCru修改为textAfterLastSymbol则使用了改进后的方法
	            excerpt.add(new Highlight(text.substring(t.startOffset(),t.endOffset())));//加入这个搜索关键词
	            offset = t.endOffset();// 关键词的下一个位置
	            endToken = Math.min(j + sumContext, tokens.length);// 如果出现连续的关键词，其实sumContext是一个窗口大小，移动搜索窗口位置
          }
          
          j++;
        }
        
        lastExcerptPos = endToken;//最后一个窗口的值x，窗口是[x-39,x]
        
        //
        // We found the series of search-term hits and added
        // them (with intervening text) to the excerpt.  Now
        // we need to add the trailing edge of text.
        //
        // So if (j < tokens.length) then there is still trailing
        // text to add.  (We haven't hit the end of the source doc.)
        // Add the words since the last hit-term insert.
        //
        if (j < tokens.length) {
          //创新思路2：这个其实不太完整，应该截取offset到下一个句号或者逗号（还有其他标点符号）的文本，才有意义，因为此时的j和sumContext、sumLength有关系，具有不确定性。导致摘要文本不成句，显示的乱七八糟。
          String strOrg = text.substring(offset,tokens[j].endOffset());        
          //by lijianfei 
          String strResult =  adjustSegment(strOrg);//调整后的结果，应该比原来的小一些，因为会向左移动；对于窗口比较大的情况，效果更明显。
          //System.out.println("原来的一个excerpt的最后一段："+strOrg);
          //System.out.println("修改后的一个excerpt的最后一段："+strResult);
        //by lijianfei
          excerpt.add(new Fragment(strResult));// 最后一个关键词到tokens[j].endOffset()之间的文本，也就是到这个窗口的结束位置。改进以后的方法相当于向左微调窗口的结束位置，使得语句完整通俗
        }
        
        //
        // Remember how many terms are in this excerpt
        //
        excerpt.setNumTerms(j - startToken);
        
        //
        // Store the excerpt for later sorting
        //
        excerpts.add(excerpt);
        System.out.println("i="+i+": "+excerpt.passages.toString());
        //
        // Start SUM_CONTEXT places away.  The next
        // search for relevant excerpts begins at i-SUM_CONTEXT
        //
        i = j + sumContext;//下一个搜索的位置是本次搜索结束位置j的后面sumContext个
      }
    }
    
    // Sort the excerpts based on their score
    Collections.sort(excerpts, SCORE_COMPARATOR);//经过排序后，出现查询关键词比如“网 络”多的，如果一样多，则把passge长度长的放在了数组的后面－－luhn思想
    
    //
    // If the target text doesn't appear, then we just
    // excerpt the first SUM_LENGTH words from the document.
    //
    if (excerpts.size() == 0) {
      Excerpt excerpt = new Excerpt(0);
      int excerptLen = Math.min(sumLength, tokens.length);
      lastExcerptPos = excerptLen;
      
      excerpt.add(new Fragment(text.substring(tokens[0].startOffset(), tokens[excerptLen-1].startOffset())));
      excerpt.setNumTerms(excerptLen);
      excerpts.add(excerpt);
    }
    
    //
    // Now choose the best items from the excerpt set.
    // Stop when we have enought excerpts to build our Summary.
    //
    double tokenCount = 0;
    int numExcerpt = excerpts.size()-1;
    List bestExcerpts = new ArrayList();
    while (tokenCount <= sumLength && numExcerpt >= 0) {	//sumLength是不是设置的有点小呢？
      Excerpt excerpt = (Excerpt) excerpts.get(numExcerpt--);
      bestExcerpts.add(excerpt);
      tokenCount += excerpt.getNumTerms();
    }    
    // Sort the best excerpts based on their natural order
    Collections.sort(bestExcerpts, ORDER_COMPARATOR);//按照片段在原文档中的位置排序，靠前的片段排在前面
    
    //
    // Now build our Summary from the best the excerpts.
    //
    tokenCount = 0;
    numExcerpt = 0;
    Summary s = new Summary();
    //test by lijianfei
    Fragment fraTest = new Fragment("improve-improve-improve-improve：");
    s.add(fraTest);
    //test by lijianfei
    while (tokenCount <= sumLength && numExcerpt < bestExcerpts.size()) {//创新思路4:过分的把限定了sumLength的长度，应该根据实际情况动态调整
      Excerpt excerpt = (Excerpt) bestExcerpts.get(numExcerpt++);
      int numTerms = excerpt.getNumTerms();
      int numFragments = excerpt.numFragments();//passages.size();
      double tokenFraction = (1.0 * numTerms) / numFragments;//总长度 除以 被分割开的小段长度（也就是关键词出现次数）？有什么意义？－－－求每个小段的平均长度，为了控制长度在sumLength内。创新4:不合理，应该计算每个小段本身的实际长度
      for (Enumeration e = excerpt.elements(); e.hasMoreElements(); ) {
        Fragment f = (Fragment) e.nextElement();
        // Don't add fragments if it takes us over the max-limit
        if (tokenCount + tokenFraction <= sumLength) {//sumLength的长度实际上是控制的numTerms的长度，控制摘要由几个Terms组成
          s.add(f);
        }
        else{
        	s.add(f);// by lijianfei
        }
        tokenCount += tokenFraction;
      }
    }
    
    if (tokenCount > 0 && lastExcerptPos < tokens.length)
      s.add(new Ellipsis());
    //by li
    //Fragment fraTest = new Fragment("testingtestingtestingtestingv testingtestingtestingtestingvtestingtestingtestingtestingv");
    //s.add(fraTest);
    //by li
    return s;
  }
  
  /* ---------------------------- *
   * </implementation:Summarizer> *
   * ---------------------------- */
  
  /** Maximun number of tokens inspect in a summary . */
  private static final int token_deep = 2000;

  /**
   * Class Excerpt represents a single passage found in the document, with some
   * appropriate regions highlit.
   */
  class Excerpt {
    Vector passages = new Vector();
    SortedSet tokenSet = new TreeSet();
    int numTerms = 0;
    int order = 0;
    
    /**
     */
    public Excerpt(int order) {
      this.order = order;
    }
    
    /**
     */
    public void addToken(String token) {
      tokenSet.add(token);
    }
    
    /**
     * Return how many unique toks we have
     */
    public int numUniqueTokens() {
      return tokenSet.size();
    }
    
    /**
     * How many fragments we have.
     */
    public int numFragments() {
      return passages.size();
    }
    
    public void setNumTerms(int numTerms) {
      this.numTerms = numTerms;//如果搜索到的这个词到其后面的5个词之间没有被搜索词，则其数值为10，其后又出现n个搜索词，则其值为10+n
    }
    
    public int getOrder() {
      return order;
    }
    
    public int getNumTerms() {
      return numTerms;//其实统计的是每个查询过程中窗口大小，因为如果在一个原始窗口的查询中，有多于1个的查询关键字的话，则numTerms会随着出现查询关键字的增多而相应的增多。就是一个查询excerpt中连续term的个数。
    }
    
    /**
     * Add a frag to the list.
     */
    public void add(Fragment fragment) {
      passages.add(fragment);
    }
    
    /**
     * Return an Enum for all the fragments
     */
    public Enumeration elements() {
      return passages.elements();
    }
  }
  
  
  private Token[] getTokens(String text) {
    ArrayList<Token> result = new ArrayList<Token>();
    TokenStream ts = analyzer.tokenStream("content", new StringReader(text));
    // test high by lijianfei
    //String tttt = Highlighter.getBestFragment();
    
    //test 
    TermAttribute termAtt = ts.getAttribute(TermAttribute.class);
    OffsetAttribute offsetAtt = ts.getAttribute(OffsetAttribute.class);
    PositionIncrementAttribute posIncrAtt = ts.getAttribute(PositionIncrementAttribute.class);
    TypeAttribute typeAtt = ts.getAttribute(TypeAttribute.class);
    try {
      while (result.size() < token_deep && ts.incrementToken()) {//result中为什么要设置一个最大大小呢
        final Token token = new Token(
            termAtt.termBuffer(), 0, termAtt.termLength(), 
            offsetAtt.startOffset(), offsetAtt.endOffset());
        token.setType(typeAtt.type());
        token.setPositionIncrement(posIncrAtt.getPositionIncrement());
        result.add(token);
      }
    } catch (IOException e) {
      // Ignore (?)
    }

    try {
      ts.close();
    } catch (IOException e) {
      // ignore
    }
    return (Token[]) result.toArray(new Token[result.size()]);
  }
  
  /**
   * fallowing text fuction by lijianfei
   */
  
  // 判断查询词是不是在文本的开头位置
  public boolean isSentenceHeand(String text, Query query){
	  String[] terms = query.getTerms();
	  String queryStr =null;
	  StringBuffer queryBuffer = new StringBuffer();
	  for(int i=0;i<terms.length;i++)
	  {
		  queryBuffer.append(terms[i]);
	  }
	  queryStr = queryBuffer.toString();
	  if( text.startsWith(queryStr)){
		  return true;
	  }
	  else{
		  return false;
	  }
	 
  }
  
 //调整摘要最后的一段，使其通俗易懂。方法：截取text。从头到出现下面这些标点符号之间的文本
  public String adjustSegment(String text){
	  String retStr = null;
	  int pos = 0;
	  //text = "网络智囊团-中国网络电视台 (如果您已经是注册会员，请先登陆；如果您还不是会员，请点击这里 注册 )";
	  StringBuffer bu = new StringBuffer(text).reverse();
      String subEnd = bu.toString();
      int len = subEnd.length();
      int pos1 = subEnd.indexOf(")");
      int pos2 = subEnd.indexOf("。");//如果句子中有这些标点符号，则截取offset到标点符号间的文本。
      int pos3 = subEnd.indexOf("-");
      int pos4 = subEnd.indexOf(":");
      int pos5 = subEnd.indexOf(";");
      int pos6 = subEnd.indexOf(",");
      if(pos1>-1){
    	  pos = len - pos1;
    	  retStr = text.substring(0, pos);
    	  retStr = retStr + " ";
    	  return retStr;
      }
      if(pos2>-1){
    	  pos = len - pos2;
    	  retStr = text.substring(0, pos);
    	  retStr = retStr + " ";
    	  return retStr;
      }
      if(pos3>-1){
    	  pos = len - pos3;
    	  retStr = text.substring(0, pos-1);
    	  retStr = retStr + " ";
    	  return retStr;
      }
      if(pos4>-1){
    	  pos = len - pos4;
    	  retStr = text.substring(0, pos-1);
    	  retStr = retStr + " ";
    	  return retStr;
      }
      if(pos5>-1){
    	  pos = len - pos5;
    	  retStr = text.substring(0, pos);
    	  retStr = retStr + " ";
    	  return retStr;
      }

      if(pos6>-1){
    	  pos = len - pos6;
    	  retStr = text.substring(0, pos-1);
    	  retStr = retStr + " ";
    	  return retStr;
      }
      return text;
  }
  
  //截取这个语句，从最后一个标点符号以后的文本。
  public String getPartySegmentAfterLastSymbol(String textorg){
	  String result = null;
	  //textorg = "hello 第四章 会员的权利 一、会员有权享受个人信息资料的保护。 二、会员有选择参与智囊团活动的权利。三、会员有权监督中国网络电视台的内容编排、页面设计、用户体验等质量并提出意见、建议";
	  StringBuffer bu = new StringBuffer(textorg).reverse();
	  String text = bu.toString();
	  int length = text.length();
	  int indexSignal = 0;
	  int pos = 0;
	  //找第一个汉字
	  for (int index = 0;index<=textorg.length()-1;index++){
		   //将字符串拆开成单个的字符
		   String w=textorg.substring(index, index+1);
		   if(w.compareTo("\u4e00")>0&&w.compareTo("\u9fa5")<0){// \u4e00-\u9fa5 中文汉字的范围
		    //System.out.println("第一个中文的索引位置:"+index+",值是："+w);
		    break;
		   }
	   }
	  //找原文本中的第一个中文符号
	  for (int index = 0;index<=text.length()-1;index++){
		   //将字符串拆开成单个的字符
		   String w=text.substring(index, index+1);
		   String reg =" ［（《【。，！？；-】》）］.,?－)";//存放你要检测的标点符号,后面的.,?)为英文中常用的标点符号
		   if(reg.indexOf(w)!=-1){//
		    //System.out.println("最后一个标点符号的索引位置："+(length-index)+",值为："+w);
		    indexSignal = index;
		    break;
		   }
	   }
	  pos = length - indexSignal;
	  result = textorg.substring(pos,length).trim();
	  return result;
	  
  }
  
  public static void testFunctionByli(){
	  //用Unicode码实现
	  String s = "第四章 会员的权利 一、会员有权享受个人信息资料的保护。 二、会员有选择参与 i am hello,which is li.I anm";
	  StringBuffer bu = new StringBuffer(s).reverse();
      s = bu.toString();
      
	  //找第一个汉字
	  for (int index = 0;index<=s.length()-1;index++){
		  //将字符串拆开成单个的字符
		   String w=s.substring(index, index+1);
		   if(w.compareTo("\u4e00")>0&&w.compareTo("\u9fa5")<0){// \u4e00-\u9fa5 中文汉字的范围
		    System.out.println("第一个中文的索引位置:"+index+",值是："+w);
		    break;
		   }
	   }
	  //找第一个中文符号
	  for (int index = 0;index<=s.length()-1;index++){
		   //将字符串拆开成单个的字符
		   String w=s.substring(index, index+1);
		   String reg ="［（《【。，！？；、】》）］－";//存放你要检测的中文符号
		   if(reg.indexOf(w)!=-1){//
		    System.out.println("第一个中文符号的索引位置："+index+",值为："+w);
		    break;
		   }
	   }
  }
  
  
  //截取文本的第一句。
  public String getFirstSegment(String text){
	  String result = null;
	  int pos = text.indexOf("。");
	  result = text.substring(0, pos+1);
	  return result;
  }
  
  /**
   * above text fuction by lijianfei
   */ 
  
  
  
  /**
   * Tests Summary-generation.  User inputs the name of a
   * text file and a query string
   */
  public static void main(String argv[]) throws IOException {
	  //testbyli();
      // Test arglist
	 
    if (argv.length < 2) {
      System.out.println("Usage: java org.apache.nutch.searcher.Summarizer <textfile> <queryStr>");
      return;
    }
    
    Configuration conf = NutchConfiguration.create();
    Summarizer s = new BasicSummarizerTestByLijianfei(conf);
    
    //
    // Parse the args
    //
    File textFile = new File(argv[0]);
    StringBuffer queryBuf = new StringBuffer();
    for (int i = 1; i < argv.length; i++) {
      queryBuf.append(argv[i]);
      queryBuf.append(" ");
    }
    
    //
    // Load the text file into a single string.
    //
    StringBuffer body = new StringBuffer();
    BufferedReader in = new BufferedReader(new FileReader(textFile));
    try {
      System.out.println("About to read " + textFile + " from " + in);
      String str = in.readLine();
      while (str != null) {
        body.append(str);
        str = in.readLine();
      }
    } finally {
      in.close();
    }
    
    // Convert the query string into a proper Query
    Query query = Query.parse(queryBuf.toString(), conf);
    Summary summary = s.getSummary(body.toString(), query);//函数换成getSummaryOrg(body.toString(), query)，就是原来的算法
    String summaryStr = summary.toString();
    System.out.println("Summary: '" + summaryStr + "'");
  }
}
