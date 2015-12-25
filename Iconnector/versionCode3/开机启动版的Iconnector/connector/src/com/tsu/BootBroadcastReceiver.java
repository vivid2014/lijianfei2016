package com.tsu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootBroadcastReceiver extends BroadcastReceiver {

	final  String ACTION = "android.intent.action.BOOT_COMPLETED";
	 
	@Override
	public void onReceive(Context context, Intent intent) {
		 if(intent.getAction().equals(ACTION)){
			 Intent intentMainActivity = new Intent(context,MainActivity.class);
			 intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 context.startActivity(intentMainActivity);
		 }

	}

}
