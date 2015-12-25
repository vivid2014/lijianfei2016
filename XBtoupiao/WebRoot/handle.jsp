<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="cuc.vote.info.*,cuc.vote.infoctl.*"%>
<%
   String purview =  (String)session.getAttribute("purview");
   if ((purview == null) || ("".equals(purview))) {
    session.setAttribute("purview","1");
  	String[] selpic = request.getParameterValues("pic");
    String remoteIp = request.getRemoteAddr();
    //System.out.println(remoteIp);
    String selectedPic = selpic[0];
    String ip = remoteIp;
   int pic1sum = 0; //方案二
   int pic2sum = 0; //方案二
   int pic3sum = 0; //方案三
   int pic4sum = 0; //方案四
   int yj1sum = 0;
   int yj2sum = 0;
   int yj3sum = 0;
   int pic5sum = 0;//方案五
   int pic6sum = 0;//方案六
  
   if(selectedPic.equals("pic1")){
	 pic1sum = 1;
   }
   if(selectedPic.equals("pic2")){
	 pic2sum = 1;
   }
   if(selectedPic.equals("pic3")){
	 pic3sum = 1;
   }
   if(selectedPic.equals("pic4")){
	 pic4sum = 1;
   }
   if(selectedPic.equals("pic5")){
		 pic5sum = 1;
   }
   
   if(selectedPic.equals("pic6")){
		 pic6sum = 1;
   }
        VoteData queryData = new VoteData();
        VoteDataCtl ctl =  new VoteDataCtl();
    	queryData.setIp(ip);
    	queryData.setPic1Count(pic1sum);
    	queryData.setPic2Count(pic2sum);
    	queryData.setPic3Count(pic3sum);
    	queryData.setPic4Count(pic4sum);
    	queryData.setYi1Count(pic5sum);//yi1Count变量表示图片5被选中的次数
    	queryData.setYi2Count(pic6sum);//yi2Count变量表示图片6被选中的次数
    	queryData.setYi3Count(yj3sum);
  		ctl.insertInfo(queryData);
%>
	<script type="text/javascript">
  		 {window.alert('恭喜您投票成功！');window.location='index.jsp';} 
    </script>
<% 
	}
	else
	{		
%>
	<script type="text/javascript">
		{window.alert('您已经投过票了，不可以重复投票!');window.location='index.jsp';} 
	</script>
<%		
	}	
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>My JSP 'MyJsp.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	

  </head>
  

  
  
  </body>
</html>
