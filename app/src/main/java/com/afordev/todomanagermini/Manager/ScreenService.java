package com.afordev.todomanagermini.Manager;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class ScreenService extends Service {

    private ScreenOnReceiver mReceiver = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mReceiver = new ScreenOnReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		registerReceiver(mReceiver, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);
		
		if(intent != null){
			if(intent.getAction()==null){
				if(mReceiver==null){
					mReceiver = new ScreenOnReceiver();
					IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
					registerReceiver(mReceiver, filter);
				}
			}
		}
		return START_REDELIVER_INTENT;
	}
	
	@Override
	public void onDestroy(){		 	
		super.onDestroy();
		
		if(mReceiver != null){
			unregisterReceiver(mReceiver);
		}
	}
}