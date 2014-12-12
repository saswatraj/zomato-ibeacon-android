package com.project.zomato;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.jaalee.sdk.BeaconManager;
import com.jaalee.sdk.MonitoringListener;
import com.jaalee.sdk.Region;
import com.jaalee.sdk.ServiceReadyCallback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BeaconMonitorService extends Service {
	
	private Region region;
	private BeaconManager beaconManager;
	private NotificationManager notificationManager;
	private static final int NOTIFICATION_ID = 123;
	private String filename = "data.txt";

	@Override
	public void onCreate() {
		Log.d("MESSAGE","Starting Service");
		region = new Region("regionId",null,null,null);
		beaconManager = new BeaconManager(this);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Default values are 5s of scanning and 25s of waiting time to save CPU cycles.
	    // In order for this demo to be more responsive and immediate we lower down those values.
	    beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
	    beaconManager.setMonitoringListener(new MonitoringListener() {

			@Override
			public void onEnteredRegion(com.jaalee.sdk.Region arg0) {
				postNotification("Entered region");
				//Start the application here
				String ranging = readFromFile();
				Log.d("VALUES",ranging);
				if(ranging.equals("false")){
					Intent dialogIntent = new Intent(getBaseContext(),StartScreen.class);
					dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplication().startActivity(dialogIntent);
				}
			}

			@Override
			public void onExitedRegion(com.jaalee.sdk.Region arg0) {
				postNotification("Exited region");
			}
	        
	      });
	    super.onCreate();
	}
	
	private String readFromFile() {
		String line="";
		try {
			Log.d("READWRITE", "reading the file");
			File file = new File(getApplicationContext()
					.getFilesDir(), filename);
			BufferedReader br = new BufferedReader(
					new FileReader(file.getAbsoluteFile()));
			line = br.readLine();
			Log.d("READWRITE", "reading::"+line);
			br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return line;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		beaconManager.connect(new ServiceReadyCallback() {
		      @Override
		      public void onServiceReady() {
		        beaconManager.startMonitoring(region);
		      }
		    });
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		beaconManager.disconnect();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void postNotification(String msg) {
	    Intent notifyIntent = new Intent(BeaconMonitorService.this, BeaconMonitorService.class);
	    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    PendingIntent pendingIntent = PendingIntent.getActivities(
	    		BeaconMonitorService.this,
	        0,
	        new Intent[]{notifyIntent},
	        PendingIntent.FLAG_UPDATE_CURRENT);
	    Notification notification = new Notification.Builder(BeaconMonitorService.this)
	        .setSmallIcon(R.drawable.beacon_gray)
	        .setContentTitle("Notify Demo")
	        .setContentText(msg)
	        .setAutoCancel(true)
	        .setContentIntent(pendingIntent)
	        .build();
	    notification.defaults |= Notification.DEFAULT_SOUND;
	    notification.defaults |= Notification.DEFAULT_LIGHTS;
	    notificationManager.notify(NOTIFICATION_ID, notification);
	  }

}
