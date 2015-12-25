package cuc.vote.util;
import java.sql.*;


public class MySqlConn {

	static String mysqlDBDriver="com.mysql.jdbc.Driver";
	static String urls = "jdbc:mysql://202.205.16.131:3306/vote?user=testing&password=nicpass&useUnicode=true&characterEncoding=UTF-8";
	static String urlsServerPClocal = "jdbc:mysql://localhost:3306/vote?user=testing&password=nicpass&useUnicode=true&characterEncoding=UTF-8";
	static String urlslijianfeiPClocal = "jdbc:mysql://localhost:3306/vote?user=root&password=123456&useUnicode=true&characterEncoding=UTF-8";
	
	static Connection Conn=null;
	  
	public MySqlConn() {}
		
	static public void initdbspool(){
			try {
	    	Class.forName("com.mysql.jdbc.Driver").newInstance(); 
	    	Conn = DriverManager.getConnection(urlslijianfeiPClocal);
	    }catch(SQLException e){
	    	e.printStackTrace();
	    	System.out.println("ErrorCode:"+e.getErrorCode());
	    } catch (Exception e) {
	    	System.err.println(e.getMessage());	
	    }
	  }
	  
	  static public Connection getConnection() {
	  	try {
	  		if ((Conn==null)||(Conn.isClosed()))  //保证每次都是同一个连接
	  			initdbspool();
	  	} catch (SQLException e) {
	  		System.err.println(e.getMessage());
	  	}
	  	return Conn;
	  }
	  
	  
	public  void closeConnection() {  
	        try {  
	            if (Conn != null)  
	                Conn.close();  
	        } catch (Exception e) {  
	            System.out.println("关闭数据库问题 ：");  
	            e.printStackTrace();  
	        }  
	    }  
	  
	  //用于测试
	  public static void main(String args[]) {
		  
	  	try {
	  		//MySqlConn.getConnection();
	  		MySqlConn.initdbspool();
	  		Connection conn = MySqlConn.getConnection();
	  		conn.close();
	  	} catch (SQLException e) {
	  		System.err.println(e.getMessage());
	  		}
	  }
	  
}
