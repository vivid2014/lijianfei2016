import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class stuBaidu extends Thread{
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
    // ȫ������
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    // ������ʽΪ���ָ��ַ�
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

    // ת���ֽ�����Ϊ16�����ִ�
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
            // md.digest() �ú����ֵΪ��Ź�ϣֵ����byte����
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }
    
    //��url�е�tn=shituresult�޸ĳ�tn=insimijson��pn=page����
    /*
     * ԭURL��www.shitu.baidu.com�ϴ�ͼƬ���
     * urlhttp://shitu.baidu.com/i?ct=2&tn=shituresult&pn=0&rn=20&querysign=1438691479,2355860581&isImage=1&
     * keyword=%E5%88%98%E5%BE%B7%E5%8D%8E%2A%E7%BE%8A%E8%83%8E%E7%9B%98%2A%E6%9C%B1%E4%B8%BD%E5%80%A9%2A%E5%BC%A0%E6%9F%8F%E8%8A%9D%2A%E5%A4%A7%E5%A9%9A%2A%E5%88%98%E5%98%89%E7%8E%B2%2A%E6%A2%81%E6%9C%9D%E4%BC%9F%2A%E5%8F%A4%E5%A4%A9%E4%B9%90%2A%E5%8D%8E%E4%BB%94%2A&shituRetNum=355&similarRetNum=600&faceRetNum=1000&setnum=0&beautynum=0&imgurl=http%3A%2F%2Ftutu2.baidu.com%2F1001%2Fsimilar%2F20141122%2F16%2F1438691479%2C2355860581.jpg
     *
     * �����ȥ��ݵ�url��
     * http://shitu.baidu.com/i?ct=2&tn=insimijson&pn=0&rn=20&querysign=1438691479,2355860581&isImage=1&
     * keyword=%E5%88%98%E5%BE%B7%E5%8D%8E%2A%E7%BE%8A%E8%83%8E%E7%9B%98%2A%E6%9C%B1%E4%B8%BD%E5%80%A9%2A%E5%BC%A0%E6%9F%8F%E8%8A%9D%2A%E5%A4%A7%E5%A9%9A%2A%E5%88%98%E5%98%89%E7%8E%B2%2A%E6%A2%81%E6%9C%9D%E4%BC%9F%2A%E5%8F%A4%E5%A4%A9%E4%B9%90%2A%E5%8D%8E%E4%BB%94%2A&shituRetNum=355&similarRetNum=600&faceRetNum=1000&setnum=0&beautynum=0&imgurl=http%3A%2F%2Ftutu2.baidu.com%2F1001%2Fsimilar%2F20141122%2F16%2F1438691479%2C2355860581.jpg
     *
     */
    public static ArrayList<String> getUrl(int page, String url){
    	String u1=url.substring(0,url.indexOf("&tn=")+4);
    	url=url.substring(url.indexOf("&tn=")+4,url.length());
    	url=u1+"insimijson"+url.substring(url.indexOf("&"),url.length());
    	u1=url.substring(0,url.indexOf("&tn=")+4);
    	u1=url.substring(0,url.indexOf("&pn=")+4);
    	url=url.substring(url.indexOf("&pn=")+4,url.length());
    	url=u1+page+url.substring(url.indexOf("&"),url.length());
		ArrayList<String> list=new ArrayList<String>();
		try{
			String json = getStream(url);
			String str = new String (json.getBytes("GBK"),"UTF-8");
			list=getPattern(str, "\"objURL\":\"(http://[^;^<^>^=^'^\"]*?)\",");
		}catch(Exception e){}
		return list;
    }
	public void run(){
		int to=0;
		int folderw=folder;
		for(String url:list){
			if(folderw!=folder){
				break;
			}
			if(to<sum){
				to++;//
				continue;
				}
			System.out.println(folderw+": "+url);
			getFile(url, "file/image/shitu/"+folderw+"/"+GetMD5Code(url)+".jpg");
			File file=new File("file/image/shitu/"+folderw+"/"+GetMD5Code(url)+".jpg");
			if(file.length()<1000){
				file.delete();	
			}
			sum++;
			to++;
		}
	}
	public static int sum=0,folder=1; 
	public static ArrayList<String> list=new ArrayList<String>();
	public static void main(String args[]){
		for(String keyUrl:readToArraylist("file/stuUrl.txt")){
			if(!keyUrl.contains("http://"))continue;
			//File file=new File("imge/shitu/"+folder+"/");
		//	if(!file.exists())
			//	file.mkdirs();
			File file=null;
			while((file=new File("file/image/shitu/"+folder+"/")).exists())folder++;
				file.mkdir();
			for(int start=0;start<10000;start+=60){
				ArrayList<String> list1=getUrl(start,keyUrl.trim());
				if(list1.size()==0)break;
				for(String url:list1){
					if(!list.contains(url)&&url.contains("http://"))
						list.add(url);
				}
			}
			for(int i=0;i<30;i++){
				System.out.println("������"+i+"���߳�");
				new stuBaidu().start();
				
			}
			while(sum<list.size()+1)try{
				System.out.println(sum+" : "+list.size());
				Thread.sleep(10000);
				}catch(Exception e){}
			sum=0;
			list=new ArrayList<String>();
			//folder++;
		}
		//System.out.println(getUrl(10,"���»�").size());
	}

}
