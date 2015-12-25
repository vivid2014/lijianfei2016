package com.tsu;

import java.text.DecimalFormat;

import com.util.DateTextUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NicMsgAction extends Activity {
	
	Button DisconnectBT;
	TextView usernameTV;
	TextView stateTV;
	TextView address;
	TextView linkedTime;
	TextView remianFlux;
	TextView versonText;
	static final String TAG = "NicMsgAction"; 
	OnClickListener listener0  = null;
	OnClickListener listener1  = null;
	String startTime = "";
	String begin = "";
	boolean timeFlag = true;
	LinkThread thread = null;
	private Handler handler;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nicmessage);
        SysApplication.getInstance().addActivity(this);
		initView();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		super.onKeyDown(keyCode, event);
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Dialog alertDialog = new AlertDialog.Builder(this)
			.setTitle("������ʾ��")
			.setMessage("���غ󽫶Ͽ����ӣ�")
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog,int which){
				try {
					
					UserOperate.disconnectInternet();
					thread.stopThread(); // ���������������߳�
					finish();
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					finish();
				}
				
			}
			})
			.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	//
                }
               }).
           create(); 
			alertDialog.show();
		}
		
		return true;
	}

	public void initView(){
		DisconnectBT = (Button)findViewById(R.id.disconnect);
		usernameTV = (TextView)findViewById(R.id.username);
		stateTV = (TextView)findViewById(R.id.state);
		address = (TextView)findViewById(R.id.YouyIp);
		linkedTime = (TextView)findViewById(R.id.linkTime);
		remianFlux = (TextView)findViewById(R.id.availableCapacity);
		versonText = (TextView)findViewById(R.id.versinName);
		DisconnectBT.setBackgroundColor(getResources().getColor(R.color.btnLoginColor));
		DisconnectBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				try {
					thread.stopThread();// ��ͣ���߳�
					String retStr = UserOperate.disconnectInternet();
					//UserOperate.disconnectCampus();
				    if(retStr.equals("true")){
				    	    stateTV.setText("�������Ѿ��Ͽ�!");
				    	    timeFlag = false;
							disConnectAlertMessage();
					}
					else{
						disConnectFalseAlertMessage(retStr);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		Intent intent = getIntent();
		String username = intent.getStringExtra("username");
		String msg= intent.getStringExtra("msg");
	    startTime = intent.getStringExtra("startTime");
	    begin = startTime;
	    String ipAddress = intent.getStringExtra("ip");
	    String verName = intent.getStringExtra("versionName");
		updateMessage();
		thread = new LinkThread(handler); //�����̣߳�����һֱ����
		timeCount();
		usernameTV.setText(username);
		stateTV.setText(msg);
		address.setText(ipAddress);
		versonText.setText("����汾��" +verName);
		
	}
	
	
    public void disConnectAlertMessage(){
   	 Dialog alertDialog = new AlertDialog.Builder(this). 
                setTitle("������ʾ��").
                setMessage("�������Ѿ��Ͽ�!").
                setIcon(R.drawable.user_define_icon).
                setPositiveButton("���µ�¼��", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	finish();
                    }
                   }).
                   setNegativeButton("�˳�", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	stateTV.setText("�������Ѿ��Ͽ�!");
                    	finishAll();
                    }
                   }).
               create(); 
        alertDialog.show(); 
   }
    
   public void disConnectFalseAlertMessage(String retStr){
	   Dialog alertD  = new AlertDialog.Builder(this).
			   setTitle("�Ͽ�ʧ��!").
			   setMessage(retStr).
               setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                   @Override
	                   public void onClick(DialogInterface dialog, int which) {
	                	   
	                   }
                  }).
                  create();
	   alertD.show();
   }
    
   // �˳�����Ӧ�ó���
	public void finishAll(){
		SysApplication.getInstance().exitApplication();
    }
	
	public Handler getHandler(){
		return this.handler;
		}

	public void updateMessage(){
	    handler = new Handler() {

			@Override
            public void handleMessage(Message msg) {
				int getArg;
				float flux = 0.000f;
                switch (msg.what) {
                case 0:
                	linkedTime.setText((String)msg.obj);
                	break;
                case 1:
                	stateTV.setText((String)msg.obj);
                	thread.stopThread();
                	break;
                case 2:
                	getArg = msg.arg1;
                	if(getArg == 0){
                		remianFlux.setText("���");
                	}
                	else{
                		flux = getArg;
                		flux = flux/1024;
                		String showFlux = new DecimalFormat("0.000").format(flux);
                		remianFlux.setText(showFlux +"GB");
                	}
                	break;
                }
                	
            }

        };
	}
	
	public void timeCount(){
		Thread th = new Thread(){
			@Override
			public void run(){
				while(timeFlag){
					String usedTime = "";
				    try {
				    	String curDate = DateTextUtil.getFormatCurentTime();
						usedTime = DateTextUtil.getLoginTime(curDate, begin);
						Message msg = Message.obtain();
					    msg.what = 0;
					    msg.obj = usedTime;
					    handler.sendMessage(msg);
					    Thread.sleep(1000);
					    int statu = UserOperate.checkStatus(); //�鿴������״̬���˴��رհ�wifi���ǲ�����ʾ��wifi�Ѿ��Ͼ�������Ϣ��û����Ӧ��
					    if(statu == 1){
					    	timeFlag = false;
					    	msg.what = 1;
					    	msg.obj = "wifi�Ѿ��Ͽ�";
							handler.sendMessage(msg) ;
					    }
					
					} catch (Exception e) {
						Log.v("��ʱ�����쳣",e.getMessage());
					}
				    
				}
				
			}
		};
		th.start();
	}
	
    // ��ʾ��¼ʱ�䣬��Ҫһ���̣߳� 
    public static void showLinkTime(String start) throws Exception{/*
    	String AllLink = "У԰����������������";
    	String ret = UserOperate.checkStatus();
    	String linkTime = ""; 
    	while(ret.equals(AllLink)){
    		linkTime = DateTextUtil.getLoginTime(start,DateTextUtil.getFormatCurentTime());
    	}
    */}
}
