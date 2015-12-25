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
 	<div style="text-align:center;"><h2></>对不起，已经过了投票截止日期，现在不能进行投票！</h2></div>
    <center>	
    
 	</center>
 </body>
</html>
