package cuc.vote.infoctl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import cuc.vote.info.VoteData;
import cuc.vote.util.*;

public class VoteDataCtl {
	Connection conn;
	PreparedStatement stmt;
	
	public VoteDataCtl(){
		conn = MySqlConn.getConnection();
	}
	
	public VoteData getInfo(){
		VoteData vd = new VoteData();
		String strsql = "select * from XBvoteinfo";
		
		try{
			stmt = conn.prepareStatement(strsql);
			ResultSet rs = stmt.executeQuery(strsql);
			if(rs.next()){
				int pic1Count = rs.getInt("pic1Count");
				int pic2Count = rs.getInt("pic2Count");
				int pic3Count = rs.getInt("pic3Count");
				int pic4Count = rs.getInt("pic4Count");
				
				int yi1Count = rs.getInt("yi1Count");
				int yi2Count = rs.getInt("yi2Count");
				int yi3Count = rs.getInt("yi3Count");
				
				vd.setPic1Count(pic1Count);
				vd.setPic2Count(pic2Count);
				vd.setPic3Count(pic3Count);
				vd.setPic4Count(pic4Count);
				vd.setYi1Count(yi1Count);
				vd.setYi2Count(yi2Count);
				vd.setYi3Count(yi3Count);
			}
		}catch(SQLException e){
			System.err.println(e.getMessage());
		}finally{
			try{
				stmt.close();
			}catch(SQLException w){
				System.err.println(w.getMessage());
			}
		}
		return vd;
	}
	
	public int getCount(String cowValue){
		int count = -1;
		String strsql = "select count(" +cowValue + ") sum from XBvoteinfo where " + cowValue
		+"=1";
		try{
			stmt = conn.prepareStatement(strsql);
			ResultSet rs = stmt.executeQuery(strsql);
			while(rs.next()){
				count = rs.getInt("sum");
			}
			return count;
		}catch(SQLException e){
			System.err.println(e.getMessage());
		}finally{
			try{
				stmt.close();
			}catch(SQLException w){
				System.err.println(w.getMessage());
			}
		}
		return -1;
	}
	
	
	
	
	public void updateInfo(VoteData rs){
		int pic1Count = rs.getPic1Count();
		int pic2Count = rs.getPic2Count();
		int pic3Count = rs.getPic3Count();
		int pic4Count = rs.getPic4Count();
		
		int yi1Count = rs.getYi1Count();
		int yi2Count = rs.getYi2Count();
		int yi3Count = rs.getYi3Count();
		String strsql = "update XBvoteinfo set pic1Count=" + pic1Count +
		", pic2Count=" + pic2Count +", pic3Count="+pic3Count+", pic4Count="+pic4Count
		+", yi1Count=" + yi1Count+", yi2Count="+yi2Count+", yi3Count=" + yi3Count;
		try{
			stmt = conn.prepareStatement(strsql);
			stmt.executeUpdate();
		}catch(SQLException e){
			System.out.println("更新数据库时出错：");  
	        e.printStackTrace(); 			
		}catch(Exception w){
			System.out.println("更新数据出错");
			w.printStackTrace();
		}finally{
			try{
				stmt.close();
			}catch(SQLException e){
				System.out.println("update后关闭数据库连接出错");
				System.err.println(e.getMessage());
			}
		}
	}
	
	
	public boolean deleteSQL(String sql) {
	        try {  
	            stmt = conn.prepareStatement(sql);  
	            stmt.executeUpdate();  
	            return true;  
	        } catch (SQLException e) {  
	            System.out.println("删除数据库时出错：");  
	            e.printStackTrace();  
	        } catch (Exception e) {  
	            System.out.println("删除时出错：");  
	            e.printStackTrace();  
	        }finally{
				try{
					stmt.close();
				}catch(SQLException e){
					System.out.println("delete后关闭数据库连接出错");
					System.err.println(e.getMessage());
				}
			}
	        return false;  
	    }
	public boolean insertInfo(VoteData rs) {
		String ip = rs.getIp();
		int pic1Count = rs.getPic1Count();
		int pic2Count = rs.getPic2Count();
		int pic3Count = rs.getPic3Count();
		int pic4Count = rs.getPic4Count();
		
		int yi1Count = rs.getYi1Count();
		int yi2Count = rs.getYi2Count();
		int yi3Count = rs.getYi3Count();
		String strsql = "insert into XBvoteinfo values(" + pic1Count + "," + pic2Count +
		"," + pic3Count + "," + pic4Count +"," +yi1Count +"," + yi2Count + "," + yi3Count + ",'" + ip +"'" +")";
		
	        try {  
	            stmt = conn.prepareStatement(strsql);  
	            stmt.executeUpdate();  
	            return true;  
	        } catch (SQLException e) {  
	            System.out.println("插入数据库时出错：");  
	            e.printStackTrace();  
	        } catch (Exception e) {  
	            System.out.println("插入时出错：");  
	            e.printStackTrace();  
	        }finally{
				try{
					stmt.close();
				}catch(SQLException e){
					System.out.println("插入数据后关闭数据库连接出错");
					System.err.println(e.getMessage());
				}
			}
	        return false;
	    }  
	
	public boolean isHaveCurIp(String ip){
		String strsql = "select * from XBvoteinfo where ipaddr = " +"'" +ip +"'";
		try{
			stmt = conn.prepareStatement(strsql);
			ResultSet rs = stmt.executeQuery(strsql);
			if(rs.next()){
				return true;
			}
			else
				{
					return false;
				}
		}catch(SQLException e){
			System.err.println(e.getMessage());
		}finally{
			try{
				stmt.close();
			}catch(SQLException w){
				System.err.println(w.getMessage());
			}
		}
	return true;
	}
	
	
}
