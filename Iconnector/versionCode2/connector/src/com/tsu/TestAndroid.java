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
    Button login;     //��½��ť
    Button app_exit;
    EditText username;       //�û��������
    EditText pwd;            //���������
    TextView versonText;
    OnClickListener listener1=null;         //��½�¼�������
    OnClickListener listener2=null;         //�����¼�������
    OnClickListener listener3=null;         //�˳�Action
    CheckBox rememberDetails = null;
    ProgressDialog pd = null;
    String UPDATE_SERVERAPK = "Connector.apk"; 
    Handler handler;
    
    Button reset;                //���ð�ť
    String user;
    String verCodeShow;
    public static final String downloadUrl = "http://account.cuc.edu.cn/download/Connector.apk";
    static final String versionURL = "http://account.cuc.edu.cn/download/android.txt";
    String newVerName = "";//�°汾����
	int newVerCode = -1;//�°汾��
    public static final String TAG = "TestAndroid";
    public static final String MY_PREFS = "SharedPreferences"; 
    static String CurrenTs = "";
    public static String hostip = "";            //����IP       
    public static String hostmac = "";            //����MAC
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // ���ظ��½ӿ�
        updateAPK();
        SysApplication.getInstance().addActivity(this);
        setTitle("Iconnector");                 //���ñ���
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
                 setTitle("��֤����").
                 setMessage(msessage).
                 setIcon(R.drawable.user).
                 setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                    	username.setText("");
         				pwd.setText("");
         				username.setFocusable(true);
                     }
                    }).
                    setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {

                     }
                    }).
                create(); 
         alertDialog.show(); 
    }
    public void initControls(){
    	
    	login=(Button)findViewById(R.id.login);             //��ȡ��½��ť
        reset=(Button)findViewById(R.id.reset);          //��ȡ���ð�ť
        app_exit= (Button)findViewById(R.id.App_exit);
        username=(EditText)findViewById(R.id.username);          //��ȡ�û��������
        pwd=(EditText)findViewById(R.id.password);      //��ȡ�����
        versonText = (TextView)findViewById(R.id.versinID);
        rememberDetails = (CheckBox) findViewById(R.id.RememberMe);
        login.setBackgroundColor(getResources().getColor(R.color.btnLoginColor));
        reset.setBackgroundColor(getResources().getColor(R.color.btnLoginColor));
        app_exit.setBackgroundColor(getResources().getColor(R.color.btnLoginColor));
		versonText.setText("����汾��" +verCodeShow);
        
     	rememberDetails.setOnClickListener(new CheckBox.OnClickListener(){
    		public void onClick (View v){
    			RememberMe();
    		}
    	});
        listener1=new OnClickListener(){                //��½�¼�����
        	public void onClick(View v)
        	{        		
        		String Username = username.getText().toString();          //��ȡ�û�������
        		String Password =pwd.getText().toString();           
        		user = Username;
        		if(Username.equals("")||Password.equals("")){
        			Toast.makeText(getApplicationContext(), 
        					"�û��������벻��Ϊ��!", 
        					Toast.LENGTH_SHORT).show();
        			return;
        		}
        		try {
        			String loginMessage = UserOperate.login(Username, Password);
					if(loginMessage.equals("true")){
						 saveLoggedInUserId(0,Username,Password);
						 Intent intent1 = new Intent(TestAndroid.this,NicMsgAction.class);
						 Bundle bundle=new Bundle(); 
						 bundle.putString("msg","������������");
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
			public void onClick(View v) {      //����Ϊ��
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
        login.setOnClickListener(listener1);       //����½��ť����¼�����
        reset.setOnClickListener(listener2);	   //�����ð�ť����¼�����
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
    
    // �˳�����Ӧ�ó���
 	public void finishAll(){
 		SysApplication.getInstance().exitApplication();
     }
    
 	//�鿴�������Ƿ��Ѿ����ӣ���������ˣ�ֱ���������ӽ���
 	public void getInternetLinked() throws Exception{
 		username=(EditText)findViewById(R.id.username); 
 		String user = username.getText().toString();
 		if(UserOperate.checkStatus() == 3){
 			 Intent intent2 = new Intent(TestAndroid.this,NicMsgAction.class);
			 Bundle bundle=new Bundle(); 
			 bundle.putString("msg","������������");
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
    
  // �汾���½ӿ�
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
		  Log.e("�汾�Ż�ȡ�쳣", e.getMessage()); 
	  }
	  return verCode;
  }
  
  public String getVerName(Context context){
	  String verName = ""; 
	  try{
		  verName = context.getPackageManager().getPackageInfo("com.tsu", 0).versionName;
	  }catch(NameNotFoundException e){
		  Log.e("�汾�������쳣", e.getMessage()); 
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
		Log.v("getServerVerson ����",e.getMessage());
		return false;
	}
	  return true;
  }
  
  
  public void doNewVersionUpdate(){
	  int verCode = this.getVerCode(this);
	  String verName = this.getVerName(this);
	  String curVersonName = verName + verCode;
      StringBuffer sb = new StringBuffer();
      sb.append("��ǰ�汾��");    sb.append(curVersonName); 
	  sb.append(",���ְ汾�� ");  sb.append(newVerName);   
	  sb.append(",�Ƿ����");
	  Dialog dialog = new AlertDialog.Builder(this)      
	   .setTitle("�������")        
	 .setMessage(sb.toString())     
	    .setPositiveButton("��������", new DialogInterface.OnClickListener(){
	    	@Override           
			 public void onClick(DialogInterface dialog, int which) {    
			            pd = new ProgressDialog(TestAndroid.this);
			            pd.setTitle("��������");
			            pd.setMessage("���Ժ󡣡���");         
			            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);     
			            downFile(downloadUrl); //�°汾�����ص�ַ      
			           }
	    })
	    .setNegativeButton("�ݲ�����", new DialogInterface.OnClickListener() {
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
		 Log.v("��ð汾���쳣",e.getMessage());
		 return null;
	 }
 } 
 
 public void alertWifiLink(){
	 if(false == isWifiConnect()){
		 Dialog dialog = new AlertDialog.Builder(this)      
		   .setTitle("��¼��ʾ")        
		 .setMessage("wifiû�����ӣ���������wifi")     
		    .setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
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
                  
				  //֪ͨ������ɣ�Ȼ��װ
                   downOver();
			  }catch(Exception e){
				  Log.v("�����ļ��쳣",e.getMessage());
			  }
		  }
	  };
	  th.start();
  } 
  
 //������ɣ�ͨ��handler�����ضԻ���ȡ��
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
 
 // ��װӦ��apk
 public void update(){
     Intent intent = new Intent(Intent.ACTION_VIEW);
     File SDFile = android.os.Environment.getExternalStorageDirectory();
     String filePath = SDFile.getAbsolutePath() + File.separator +UPDATE_SERVERAPK;
	 File file = new File(filePath);
	 long len = file.length();
     intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
     startActivity(intent);
 }
 
 // ж��Ӧ��
 public void delete(){
 	Uri packageURI = Uri.parse("package:com.tsu"); 
 	Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI); 
 	startActivity(uninstallIntent);
 }
  public void notNewVersionUpdate(){
	  int verCode = this.getVerCode(this);     
	  String verName = this.getVerName(this);
	  StringBuilder sb = new StringBuilder(); 
	  sb.append("��ǰ�汾��");        
	  sb.append(verName);     
	  sb.append(" Code:");   
	  sb.append(verCode);   
	  sb.append("\n�������°汾���������"); 
	  Dialog dialog = new AlertDialog.Builder(this)
	  .setTitle("�������")
	  .setMessage(sb.toString())
	  .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			finish();
		}
	})
	.create();
	 dialog.show();
  }
  
}