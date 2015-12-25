<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String clinetIp = request.getRemoteAddr();
System.out.println(clinetIp);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">   
    <title>校旗网络投票</title>
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
			font-size:20";}
	
	.center{
    	width:1280px;
    	height:1024px;
    	border:1px solid #ff1000;
    	background-color:yellow;
    	margin:20px auto;
    	}
			
	.cuc_pic1{
			display: block;
			width: 300px;
			height: 130px;
			padding: 0px 50px 0px 50px;
			background: url(img/cuc1.jpg) 10px 10px no-repeat;}
	</style>
	
	<script type="text/javascript">
		function checkSelect(){
			var imgs=document.getElementsByName('pic');
			checkedimgs=false;
			for(ii=0;ii<imgs.length;ii++)
				{
					if(imgs[ii].checked){checkedimgs=true;};
				}
			if (!checkedimgs)
				{
					alert('请选择校旗标识');
					return false;
				}
			return true;
}

	</script>
  </head>
 	<body style="background-color:ffffff;">
    <center>	
    <div id="tb">
    		<h2>中国传媒大学校旗网络投票</h2>	
   <form onSubmit="if (checkSelect()==false) return false;" action="handle.jsp" method="post">
    <table border="2" width="900" height="768" valign="top" style="border:1px solid">
  								<tr height="65" style="border-top:0px;">
  									<td width="50" class="left_td">ID</td>
  									<td width="100" class="left_td">方案</td>
  									<td width="220" class="left_td">图例</td>
  									<td width="100" class="left_td">选择</td>
  								</tr>
  								<tr height="210" style="border-top:0px;">
  									<td width="50" class="left_td">1</td>
  									<td width="100" class="left_td"><div style="text-align: left;">学校校旗为3：2长方形，以红色为底色，中间区域印有郭沫若字体的中文校名及校名英译全称，左上角配以学校校徽。校名颜色为白色，校徽为白底全色。</div></td>
  									<td width="220" class="left_td"><img src="img/fangan1.jpg" height="240"></td>
  									<td width="100" class="left_td"><p><input type="radio" size="2" value="pic1" name ="pic" ></p></td>
  								</tr>
  								<tr height="210" style="border-top:0px;">
  									<td width="50" class="left_td">2</td>
  									<td width="100" class="left_td"><div style="text-align: left;">学校校旗为3：2长方形，以红色为底色，中间区域印有郭沫若字体的中文校名及校名英译全称，校徽与校名为左右形式。校名颜色为白色，校徽为白底全色。</div></td>
  									<td width="220" class="left_td"><img src="img/fangan2.jpg" height="240"></td>
  									<td width="100" class="left_td"><p><input type="radio" size="2" value="pic2" name ="pic"></td>
  								</tr>
  								<%--	 
  								<tr height="210" style="border-top:0px;">
  									<td width="50" class="left_td">3</td>
  									<td width="100" class="left_td">上下白</td>
  									<td width="220" class="left_td"><img src="img/shangxia-bai.jpg" height="200"></td>
  									<td width="100" class="left_td"><p><input type="radio" size="2" value="pic3" name ="pic"></td>
  								</tr>
  								<tr height="210" style="border-top:0px;">
  									<td width="50" class="left_td">4</td>
  									<td width="100" class="left_td">上下黄</td>
  									<td width="220" class="left_td"><img src="img/shangxia-huang.jpg" height="200"></td>
  									<td width="100" class="left_td"><p><input type="radio" size="2" value="pic4" name ="pic"></td>
  								</tr>
  								
  								<tr height="210" style="border-top:0px;">
  									<td width="50" class="left_td">5</td>
  									<td width="100" class="left_td">中间白</td>
  									<td width="220" class="left_td"><img src="img/middle-bai.jpg" height="200"></td>
  									<td width="100" class="left_td"><p><input type="radio" size="2" value="pic5" name ="pic"></td>
  								</tr>
  								<tr height="210" style="border-top:0px;">
  									<td width="50" class="left_td">6</td>
  									<td width="100" class="left_td">中间黄</td>
  									<td width="220" class="left_td"><img src="img/middle-huang.jpg" height="200"></td>
  									<td width="100" class="left_td"><p><input type="radio" size="2" value="pic6" name ="pic"></td>
  								</tr>
  								--%>
  					<tr height="36">
  						<td style="text-align:center;border:0px solid;" colspan="4";>
  							<input type="SUBMIT" style="height:84px;width:580px; background-color:009ACD;" value="提    交"/>
  						</td>
  					</tr>
  	</table>
    </form>   
 </div>    
 <div id="bt" style="width:900;height:168;display:block;">
<p align="left" class="exp">&nbsp;&nbsp;
注：投票时只能选择其中之一</p>
</div>
 </center>
 </body>
</html>
