package com.tsu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;


import com.util.DateTextUtil;
import com.util.HttpUtil;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.app.AlertDialog;


public class TestAndroid extends Activity {
    Button login;     //登陆按钮
    Button app_exit;
    EditText username;       //用户名输入框
    EditText pwd;            //密码输入框
    TextView versonText;
    OnClickListener listener1=null;         //登陆事件监听器
    OnClickListener listener2=null;         //重置事件监听器
    OnClickListener listener3=null;         //退出Action
    CheckBox rememberDetails = null;
    ProgressDialog pd = null;
    String UPDATE_SERVERAPK = "Connector.apk"; 
    Handler handler;
    
    Button reset;                //重置按钮
    String user;
    String verCodeShow;
    public static final String downloadUrl = "http://account.cuc.edu.cn/download/Connector.apk";
    static final String versionURL = "http://account.cuc.edu.cn/download/android.txt";
    String newVerName = "";//新版本名称
	int newVerCode = -1;//新版本号
    public static final String TAG = "TestAndroid";
    public static final String MY_PREFS = "SharedPreferences"; 
    static String CurrenTs = "";
    public static String hostip = "";            //本机IP       
    public static String hostmac = "";            //本机MAC
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // 下载更新接口
        updateAPK();
        SysApplication.getInstance().addActivity(this);
        setTitle("Iconnector");                 //设置标题
        initControls();
    }
    
    @Override
    protected void onStart(){
    	super.onStart();
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		super.onKeyDown(keyCode, event);
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finishAll();
		}
		
		return true;
	}
    
    public void alertMessage(String msessage){
    	 Dialog alertDialog = new AlertDialog.Builder(this). 
                 setTitle("认证错误！").
                 setMessage(msessage).
                 setIcon(R.drawable.user).
                 setPositiveButton("确定", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                    	username.setText("");
         				pwd.setText("");
         				username.setFocusable(true);
                     }
                    }).
                    setNegativeButton("取消", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {

                     }
                    }).
                create(); 
         alertDialog.show(); 
    }
    public void initControls(){
    	
    	login=(Button)findViewById(R.id.login);             //获取登陆按钮
        reset=(Button)findViewById(R.id.reset);          //获取重置按钮
        app_exit= (Button)findViewById(R.id.App_exit);
        username=(EditText)findViewById(R.id.username);          //获取用户名输入框
        pwd=(EditText)findViewById(R.id.password);      //获取密码框
        versonText = (TextView)findViewById(R.id.versinID);
        rememberDetails = (CheckBox) findViewById(R.id.RememberMe);
        login.setBackgroundColor(getResources().getColor(R.color.btnLoginColor));
        reset.setBackgroundColor(getResources().getColor(R.color.btnLoginColor));
        app_exit.setBackgroundColor(getResources().getColor(R.color.btnLoginColor));
		versonText.setText("软件版本：" +verCodeShow);
        
     	rememberDetails.setOnClickListener(new CheckBox.OnClickListener(){
    		public void onClick (View v){
    			RememberMe();
    		}
    	});
        listener1=new OnClickListener(){                //登陆事件监听
        	public void onClick(View v)
        	{        		
        		String Username = username.getText().toString();          //获取用户名密码
        		String Password =pwd.getText().toString();           
        		user = Username;
        		if(Username.equals("")||Password.equals("")){
        			Toast.makeText(getApplicationContext(), 
        					"用户名和密码不能为空!", 
        					Toast.LENGTH_SHORT).show();
        			return;
        		}
        		try {
        			String loginMessage = UserOperate.login(Username, Password);
					if(loginMessage.equals("true")){
						 saveLoggedInUserId(0,Username,Password);
						 Intent intent1 = new Intent(TestAndroid.this,NicMsgAction.class);
						 Bundle bundle=new Bundle(); 
						 bundle.putString("msg","互联网已连接");
						 bundle.putString("username", Username);
						 bundle.putString("startTime",DateTextUtil.getFormatCurentTime());
						 bundle.putString("ip", UserOperate.getUserIp());
						 bundle.putString("versionName", verCodeShow);
						 intent1.putExtras(bundle);
				 		 startActivity(intent1);
					}
					else{
						alertMessage(loginMessage);
					}
				} catch (Exception e) {
					Log.v(TAG,e.getMessage());
				}
        	}
        };
        
        
        
        listener2=new OnClickListener(){
			@Override
			public void onClick(View v) {      //重置为空
				username.setText("");
				pwd.setText("");
			}
        };
        
        listener3 = new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		finishAll();
        	}
        };
        
        app_exit.setOnClickListener(listener3);
        login.setOnClickListener(listener1);       //给登陆按钮添加事件监听
        reset.setOnClickListener(listener2);	   //给重置按钮添加事件监听
        getRemember();
        
    }
    
    public void RememberMe(){
    	boolean thisRemember = rememberDetails.isChecked();
    	SharedPreferences prefs = getSharedPreferences(MY_PREFS, 0);
    	Editor editor = prefs.edit();
    	editor.putBoolean("remember", thisRemember);
    	editor.commit();
    }
    
    public void getRemember(){
    	SharedPreferences prefs = getSharedPreferences(MY_PREFS, 0);
    	String thisUsername = prefs.getString("username", "");
    	String thisPassword = prefs.getString("password", "");
    	boolean thisRemember = prefs.getBoolean("remember", false);
    	if(thisRemember) {
    		username.setText(thisUsername);
    		pwd.setText(thisPassword);
    		rememberDetails.setChecked(thisRemember);
    	}
    }
    
    
    public void saveLoggedInUserId(long id, String username, String password) {
    	SharedPreferences settings = getSharedPreferences(MY_PREFS, 0);
    	Editor myEditor = settings.edit();
    	myEditor.putLong("uid", id);
    	myEditor.putString("username", username);
    	myEditor.putString("password", password);
    	boolean rememberThis = rememberDetails.isChecked();
    	myEditor.putBoolean("remember", rememberThis);
    	myEditor.commit();
    }
    
    // 退出整个应用程序
 	public void finishAll(){
 		SysApplication.getInstance().exitApplication();
     }
    
 	//查看互联网是否已经连接，如果连接了，直接跳到连接界面
 	public void getInternetLinked() throws Exception{
 		username=(EditText)findViewById(R.id.username); 
 		String user = username.getText().toString();
 		if(UserOperate.checkStatus() == 3){
 			 Intent intent2 = new Intent(TestAndroid.this,NicMsgAction.class);
			 Bundle bundle=new Bundle(); 
			 bundle.putString("msg","互联网已连接");
			 bundle.putString("username", user);
			 bundle.putString("startTime",DateTextUtil.getFormatCurentTime());
			 intent2.putExtras(bundle);
	 		 startActivity(intent2);
 		}
 	}
    public String getLocalMacAddress() {
	       WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);       
	        WifiInfo info = wifi.getConnectionInfo();       
	        return info.getMacAddress();       
	    }
    
  // 版本更新接口
  public void updateAPK(){
	 
	  initHander();
	  int verCode = -1;
	  if(getServerVerson()){
		  verCode = this.getVerCode(this);
		  if(newVerCode > verCode){
			  doNewVersionUpdate();
		  }
		  else{
			  return;
		  }
	  }
		  
  }
  
  public int getVerCode(Context content){
	  int verCode = -1;
	  try{
		  verCode = content.getPackageManager().getPackageInfo("com.tsu", 0).versionCode;
	  }catch(NameNotFoundException e){
		  Log.e("版本号获取异常", e.getMessage()); 
	  }
	  return verCode;
  }
  
  public String getVerName(Context context){
	  String verName = ""; 
	  try{
		  verName = context.getPackageManager().getPackageInfo("com.tsu", 0).versionName;
	  }catch(NameNotFoundException e){
		  Log.e("版本号名称异常", e.getMessage()); 
	  }
	  return verName;
  }
  
  public boolean getServerVerson(){
	  try {
		  String versonStr = getVersonData();
		  int lll = versonStr.trim().length();
		  newVerCode =  Integer.valueOf(versonStr.trim());
		  verCodeShow = this.getVerName(this) + "." + versonStr;
		  newVerName = verCodeShow;
	} catch (Exception e){
		Log.v("getServerVerson 出错",e.getMessage());
		return false;
	}
	  return true;
  }
  
  
  public void doNewVersionUpdate(){
	  int verCode = this.getVerCode(this);
	  String verName = this.getVerName(this);
	  String curVersonName = verName + verCode;
      StringBuffer sb = new StringBuffer();
      sb.append("当前版本：");    sb.append(curVersonName); 
	  sb.append(",发现版本： ");  sb.append(newVerName);   
	  sb.append(",是否更新");
	  Dialog dialog = new AlertDialog.Builder(this)      
	   .setTitle("软件更新")        
	 .setMessage(sb.toString())     
	    .setPositiveButton("立即更新", new DialogInterface.OnClickListener(){
	    	@Override           
			 public void onClick(DialogInterface dialog, int which) {    
			            pd = new ProgressDialog(TestAndroid.this);
			            pd.setTitle("正在下载");
			            pd.setMessage("请稍后。。。");         
			            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);     
			            downFile(downloadUrl); //新版本的下载地址      
			           }
	    })
	    .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
	    @Override
		 public void onClick(DialogInterface dialog, int which) {   
		              // TODO Auto-generated method stub         
		  }       
		  })
	    .create();
	    dialog.show();
  }
  
 public String getVersonData(){
	 HttpClient client = new DefaultHttpClient();
	 HttpGet get = new HttpGet(versionURL);
	 HttpResponse response = null;
	 String VersionStr = "";
	 try{
		  response = client.execute(get);
		  if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
			 return "0";
		  }
		  HttpEntity entity = response.getEntity();
		  long length = 0;
		  length = entity.getContentLength();
		  InputStream is =  entity.getContent();
		  FileOutputStream fileOutputStream = null;
		  byte []b = new byte[16];
		  int len = is.read(b, 0, 16);
		  VersionStr = new String(b);
		  return VersionStr;
	 }catch(Exception e){
		 Log.v("获得版本号异常",e.getMessage());
		 return null;
	 }
 } 
 
 public void alertWifiLink(){
	 if(false == isWifiConnect()){
		 Dialog dialog = new AlertDialog.Builder(this)      
		   .setTitle("登录提示")        
		 .setMessage("wifi没有连接，请先连接wifi")     
		    .setPositiveButton("确定", new DialogInterface.OnClickListener(){
		    	@Override           
				 public void onClick(DialogInterface dialog, int which) {    
				           finish();
				           }
		    })
		    .create();
		 dialog.show();
		 //Toast.makeText(this, "wifi not linked", Toast.LENGTH_LONG).show();
		 }
 }
 public boolean isWifiConnect(){
	 boolean ret;
	 ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	 NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	 ret = mWifi.isConnected(); 
	 return ret;
 }
 public void downFile(final String url){
	  String errormsg = "";
	  pd.show();
	  Thread th = new Thread(){
		  public void run(){
			  HttpClient client = new DefaultHttpClient(); 
			  HttpGet get = new HttpGet(url); 
			  HttpResponse response = null; 
			  try{
				  response = client.execute(get);
				  HttpEntity entity = response.getEntity();
				  long length = entity.getContentLength();
				  InputStream is =  entity.getContent();
				  FileOutputStream fileOutputStream = null;
				  if(is != null){
					  File SDFile = android.os.Environment.getExternalStorageDirectory();
					  String filePath = SDFile.getAbsolutePath() + File.separator +UPDATE_SERVERAPK;
					  File file = new File(filePath);
					  if(file.exists()){
						  file.delete();
						  file.createNewFile();
					  }
					  fileOutputStream = new FileOutputStream(file,true);
					  byte[] b = new byte[1024];
					  int charb = -1;
                      int count = 0;
                      while((charb = is.read(b)) != -1){
                    	  fileOutputStream.write(b, 0, charb);
                    	  count += charb;
                      }
                      fileOutputStream.flush();
                      }
                  if(fileOutputStream!=null){
                        fileOutputStream.close();
                  }
                  
				  //通知下载完成，然后安装
                   downOver();
			  }catch(Exception e){
				  Log.v("下载文件异常",e.getMessage());
			  }
		  }
	  };
	  th.start();
  } 
  
 //下载完成，通过handler将下载对话框取消
 public void downOver(){
     new Thread(){
         public void run(){
             Message message = handler.obtainMessage();
             handler.sendMessage(message);
         }
     }.start();
 }
 
 public void initHander(){
	   handler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            super.handleMessage(msg);                
	            pd.cancel();
	            update();
	        }
	    };
 }
 
 // 安装应用apk
 public void update(){
     Intent intent = new Intent(Intent.ACTION_VIEW);
     File SDFile = android.os.Environment.getExternalStorageDirectory();
     String filePath = SDFile.getAbsolutePath() + File.separator +UPDATE_SERVERAPK;
	 File file = new File(filePath);
	 long len = file.length();
     intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
     startActivity(intent);
 }
 
 // 卸载应用
 public void delete(){
 	Uri packageURI = Uri.parse("package:com.tsu"); 
 	Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI); 
 	startActivity(uninstallIntent);
 }
  public void notNewVersionUpdate(){
	  int verCode = this.getVerCode(this);     
	  String verName = this.getVerName(this);
	  StringBuilder sb = new StringBuilder(); 
	  sb.append("当前版本：");        
	  sb.append(verName);     
	  sb.append(" Code:");   
	  sb.append(verCode);   
	  sb.append("\n已是最新版本，无需更新"); 
	  Dialog dialog = new AlertDialog.Builder(this)
	  .setTitle("软件更新")
	  .setMessage(sb.toString())
	  .setPositiveButton("确定", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			finish();
		}
	})
	.create();
	 dialog.show();
  }
  
}