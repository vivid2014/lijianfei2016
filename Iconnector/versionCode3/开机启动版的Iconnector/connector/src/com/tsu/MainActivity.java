package com.tsu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class MainActivity extends Activity {

	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.mian_activity);
	        SysApplication.getInstance().addActivity(this);
			checkWifi();
			
		}
	   
	   @Override
	    protected void onStart(){
	    	super.onStart();
	    	String mode = "";
	    	String factory = "";
	    	mode = UserOperate.getMobileModle();//M032
	    	factory = UserOperate.getMobileFactory();//MEIZU
	    }
	   
	   public void checkWifi(){
		   alertWifiLink();
	   }
	   
	   public void alertWifiLink(){
			 if(false == isWifiConnect()){
				 Dialog dialog = new AlertDialog.Builder(this)      
				   .setTitle("登录提示")        
				 .setMessage("wifi没有连接，请先连接校内wifi")
				    .setPositiveButton("确定", new DialogInterface.OnClickListener(){
				    	@Override           
						 public void onClick(DialogInterface dialog, int which) {    
						           finish();
						           }
				    })
				    .create();
				 dialog.show();
				 } else{
				     Intent intent = new Intent(MainActivity.this,TestAndroid.class);
					 startActivity(intent);
			 }
		 }
		 public boolean isWifiConnect(){
			 boolean ret;
			 ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			 NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			 ret = mWifi.isConnected(); 
			 return ret;
		 }
}
