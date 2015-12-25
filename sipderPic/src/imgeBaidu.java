
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class imgeBaidu extends Thread{
	public static HttpURLConnection getConn(String str_url) {
		HttpURLConnection conn=null;
		URL url=null;
		try{
			url = new URL(str_url);
			conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("GET");
			conn.setReadTimeout(60000);
			conn.setRequestProperty("Connection", "keep-alive");
			//conn.setRequestProperty("Cookie", cookie+"; Hm_lvt_7ae828c21b3320e25ac17838242cc806=1404386032,1404466887,1404483206,1404515357; Hm_lpvt_7ae828c21b3320e25ac17838242cc806=1404519915; accm=9AB70C812FB414B874B612917861800F");
			conn.setRequestProperty("User-agent","Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/53");
		}catch(Exception e){e.printStackTrace();}
		return conn;
	}
	public static String getStream(String url){
		StringBuffer pagContent=new StringBuffer();
		HttpURLConnection conn=getConn(url);
		try{
			String line="";
			BufferedReader inputBuffer=null;
			try{
				if((url.contains("video")&&!url.contains("vlist"))||url.contains("pinglun.sohu")||url.contains("jiaju.sina")||url.contains("tv.sohu.com/hothdtv/"))
					inputBuffer=new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
				else if(url.contains("tv.sohu"))
					inputBuffer=new BufferedReader(new InputStreamReader(conn.getInputStream(),"gbk"));
				else
					inputBuffer=new BufferedReader(new InputStreamReader(conn.getInputStream(),"gb2312"));
			}
			catch(Exception ex){
				conn.disconnect();
			}
			while((line=inputBuffer.readLine())!=null)
				pagContent.append(line+"\r\n");
			inputBuffer.close();
		}catch(Exception e){}
		conn.disconnect();
		return pagContent.toString();
	}
	public static ArrayList<String> getPattern(String stream,String compile_str){
		Pattern patten = Pattern.compile(compile_str);
		Matcher matcher = patten.matcher(stream);
    	ArrayList<String> list=new ArrayList<String>();
    	while (matcher.find()) {
			String temp=matcher.group(1);
        	if(!list.contains(temp)){
        		list.add(temp);
        	}
        }
		return list;
	}
	public static ArrayList<String> readToArraylist(String path){
		ArrayList<String> list=new ArrayList<String>();
		String strLine="";
		BufferedReader reader=null;
		try{
			reader= new BufferedReader (new FileReader(path));
			while ((strLine = reader.readLine()) != null) {
				list.add(strLine);
			}
			reader.close();
		}catch(Exception e){System.out.println(e.getMessage());}
		return list;
	}
	public static boolean getFile(String url,String filePath){
		HttpURLConnection conn=getConn(url);
			InputStream inStream = null;
			try{
				inStream = conn.getInputStream();
			}catch(Exception ex){
			}
            FileOutputStream outstream =null;

    	try{
    		outstream= new FileOutputStream(filePath); 
            int l = -1;       
            byte[] tmp = new byte[1024];     
            while ((l = inStream.read(tmp)) != -1) 
            	outstream.write(tmp,0,l);
            outstream.flush();
            outstream.close();
		}catch(Exception e){
			System.out.println("error: "+e.getMessage());
		}
		conn.disconnect();
		return true;
	}
    // 全局数组
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }

    public static String GetMD5Code(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }
	public static ArrayList<String> getUrl(int page,String word){
		String path = "http://image.baidu.com/i?";
		ArrayList<String> list=new ArrayList<String>();
		try{
		String param = "tn=baiduimagejson&ie=utf-8&ic=0&rn=60&pn="+page+"&word=" + URLEncoder.encode(word,"UTF-8");
		String url = path+param;
		String json = getStream(path+param);
		String str = new String (json.getBytes("GBK"),"UTF-8");
		list=getPattern(str, "\"objURL\":\"(http://[^;^<^>^=^'^\"]*?)\",");
		}catch(Exception e){}
		return list;
		//imageData = ImageUtil.resolveImageData(json);
	}
	public void run(){
		int to=0;
		String key=keyw;
		for(String url:list){
			if(!key.equals(keyw))break;
			if(to<sum){to++;continue;}
			System.out.println(keyw+": "+url);
			getFile(url, "image/img/"+keyw.trim()+"/"+GetMD5Code(url)+".jpg");
			sum++;
			System.out.println("sum的数值是："+sum);
			to++;
		}
	}
	
	public static String outCurPath() throws IOException{
		File directory = new File("");//设定为当前文件夹 
		String curCanPath = directory.getCanonicalPath();//获取标准的路径 
		String curAbsPath = directory.getAbsolutePath();//获取绝对路径 
		try{ 
		    System.out.println("标准相对路径："+curCanPath);
		    System.out.println("绝对路径："+curAbsPath);
		}catch(Exception e){}
		return curCanPath; 
	}
	public static int sum=0;
	public static String keyw="";
	public static ArrayList<String> list=new ArrayList<String>();
	public static void main(String args[]){
		try {
			outCurPath();
		} catch (IOException e1) {
			e1.getMessage().toString();
		}
		for(String key:readToArraylist("file/keyword.txt")){
			keyw=key;
			File file=new File("file/image/img/"+key.trim()+"/");
			if(!file.exists())
				file.mkdirs();
			for(int start=0;start<10000;start+=60){
				ArrayList<String> list1=getUrl(start,key.trim());
				if(list1.size()==0)break;
				for(String url:list1){
					if(!list.contains(url)&&url.contains("http://"))
						list.add(url);
				}
			}
			for(int i=0;i<30;i++)new imgeBaidu().start();
			while(sum<list.size()+1)
				try{ 
					System.out.println(sum+" : "+list.size());
					Thread.sleep(10000);
					}catch(Exception e){}
			sum=0;
			list=new ArrayList<String>();
		}
		//System.out.println(getUrl(10,"刘德华").size());
	}
}

