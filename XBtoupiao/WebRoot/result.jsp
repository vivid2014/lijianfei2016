<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="cuc.vote.info.*,cuc.vote.infoctl.*"%>

<%
	if (SignIn.auth_header(request, response) == 0)
	{
		//查询当前结果
	  	int pic1sum = 0;
	 	int pic2sum = 0;
	  	int pic3sum = 0;
	  	int pic4sum = 0;
	  	int yj1sum = 0;
	  	int yj2sum = 0;
	  	int yj3sum = 0;
	    VoteData queryData = new VoteData();
	    VoteDataCtl ctl =  new VoteDataCtl();
	    pic1sum = ctl.getCount("pic1Count");
	    pic2sum = ctl.getCount("pic2Count");
	    pic3sum = ctl.getCount("pic3Count");
	    pic4sum = ctl.getCount("pic4Count");
	    yj1sum = ctl.getCount("yi1Count");//用数据表中的yi1Count字段表示图片5
	    yj2sum = ctl.getCount("yi2Count");//用数据表中的yi2Count字段表示图片6
	    yj3sum = ctl.getCount("yi3Count");
	 
%>
	

<html>
  <head>
    
    
    <title>投票结果</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

 <style> 
	.left_td{
			text-align:center;
			font-family:黑体;
			font-size:20;}
			
	.exp{
			sytle="font-family:黑体;
			font-size:20"}
	
	.center{
    width:1280px;
    height:1024px;
    border:1px solid #ff1000;
    margin:20px auto;}
			
	.cuc_pic1{
			display: block;
			width: 300px;
			height: 130px;
			padding: 0px 50px 0px 50px;
			background: url(img/cuc1.jpg) 10px 10px no-repeat;}
	</style>
  </head>
  		
    <center>
   		 	<center>	
    <div id="tb">
    		<h2></>中国传媒大学校旗网络投票结果</h2>	
    <table border="2" width="900" height="768" valign="top" style="border:1px solid">
  								<tr height="95" style="border-top:0px;">
  									<td width="50" class="left_td">ID</td>
  									<td width="100" class="left_td">方案</td>
  									<td width="220" class="left_td">图例</td>
  									<td width="100" class="left_td">票数</td>
  								</tr>
  								<tr height="210" style="border-top:0px;">
  									<td width="50" class="left_td">1</td>
  									<td width="100" class="left_td"><div style="text-align: left;">学校校旗为3：2长方形，以红色为底色，中间区域印有郭沫若字体的中文校名及校名英译全称，左上角配以学校校徽。校名颜色为白色，校徽为白底全色。</div></td>
  									<td width="220" class="left_td"><img src="img/fangan1.jpg" height="240"></td>
  									<td width="100" class="left_td"><p><%=pic1sum%>票</p></td>
  								</tr>
  								<tr height="210" style="border-top:0px;">
  									<td width="50" class="left_td">2</td>
  									<td width="100" class="left_td"><div style="text-align: left;">学校校旗为3：2长方形，以红色为底色，中间区域印有郭沫若字体的中文校名及校名英译全称，校徽与校名为左右形式。校名颜色为白色，校徽为白底全色。</td>
  									<td width="220" class="left_td"><img src="img/fangan2.jpg" height="240"></td>
  									<td width="100" class="left_td"><p><%=pic2sum%>票</p></td>
  								</tr>
  								<%--
  								<tr height="210" style="border-top:0px;">
  									<td width="50" class="left_td">3</td>
  									<td width="100" class="left_td">上下白</td>
  									<td width="220" class="left_td"><img src="img/shangxia-bai.jpg" height="200"></td>
  									<td width="100" class="left_td"><p><%=pic3sum%>票</p></td>
  								</tr>
  								<tr height="210" style="border-top:0px;">
  									<td width="50" class="left_td">4</td>
  									<td width="100" class="left_td">上下黄</td>
  									<td width="220" class="left_td"><img src="img/shangxia-huang.jpg" height="200"></td>
  									<td width="100" class="left_td"><p><%=pic4sum%>票</p></td>
  								</tr>
  								<tr height="210" style="border-top:0px;">
  									<td width="50" class="left_td">5</td>
  									<td width="100" class="left_td">中间白</td>
  									<td width="220" class="left_td"><img src="img/middle-bai.jpg" height="200"></td>
  									<td width="100" class="left_td"><p><%=yj1sum%>票</p></td>
  								</tr>
  								<tr height="210" style="border-top:0px;">
  									<td width="50" class="left_td">6</td>
  									<td width="100" class="left_td">中间黄</td>
  									<td width="220" class="left_td"><img src="img/middle-huang.jpg" height="200"></td>
  									<td width="100" class="left_td"><p><%=yj2sum%>票</p></td>
  								</tr>
  								--%>
  	</table>
 </center>
  	</center>
</html>







<%
}		
	else {
%>

<HTML><HEAD><TITLE>Unauthorized</TITLE></HEAD>

<BODY><H1>Unauthorized</H1>

Proper authorization is required for this area. Either your browser does not perform authorization, or your authorization has failed.

</BODY></HTML>

<%}%>