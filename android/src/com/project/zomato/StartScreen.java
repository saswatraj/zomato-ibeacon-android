package com.project.zomato;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.jaalee.sdk.Beacon;
import com.jaalee.sdk.BeaconManager;
import com.jaalee.sdk.RangingListener;
import com.jaalee.sdk.Region;
import com.jaalee.sdk.ServiceReadyCallback;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class StartScreen extends Activity {
	
	private BeaconManager beaconManager;
	private static final int REQUEST_ENABLE_BT = 1234;
	private Region region;
	private boolean isRanging = false;
	private String userId ="141";
	private String filename = "data.txt";
	private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webview = new WebView(this);
        setContentView(webview);
        webview.loadUrl("https://www.zomato.com/");
        beaconManager = new BeaconManager(this);
        region = new Region("regionId",null,null,null);
        if(isMonitoringServiceRunning()){
        	Log.d("MESSAGE", "Service already running");
        	Toast.makeText(getApplicationContext(), "Already Running", Toast.LENGTH_SHORT).show();
        }else{
        	Log.d("MESSAGE", "Service not running. Starting it now");
        	Toast.makeText(getApplicationContext(), "Starting Service", Toast.LENGTH_SHORT).show();
        	Intent serviceIntent = new Intent(getApplicationContext(),BeaconMonitorService.class);
        	getApplicationContext().startService(serviceIntent);
        }
        showUserOfferPreferenceDialog();
        beaconManager.setRangingListener(new RangingListener(){

			@Override
			public void onBeaconsDiscovered(Region region, List beacons) {
				Log.d("MESSAGE","recieved ranged beacons");
				//filter beacons to detect only our UUID
				getActionBar().setSubtitle("Found beacons: " + beacons.size());
				if(beacons.size()>0){
					Beacon mybeacon = (Beacon)beacons.get(0);
					Log.d("MESSAGE",beaconRepr(mybeacon));
					new CouponValidityTask(getApplicationContext(),beaconRepr(mybeacon),userId,StartScreen.this,webview).execute();
				}
			}
        });
    }
    


	private String beaconRepr(Beacon beacon) {
    	return beacon.getMacAddress() + "_" + beacon.getMajor()
				+ "_" + beacon.getMinor() + "_"
				+ beacon.getProximityUUID();
	}
	
	private void dumpToFile() {
		Log.d("WRITE", "writing output");
		try {
			FileOutputStream outputStream = openFileOutput(
					filename, Context.MODE_PRIVATE);
			String out = isRanging + "";
			Log.d("WRITE", out);
			outputStream.write(out.getBytes());
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    private void showUserOfferPreferenceDialog() {
    	final Dialog dialog = new Dialog(StartScreen.this);
    	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.question_dialog);
		TextView message = (TextView) dialog.findViewById(R.id.textView1);
		message.setText("Would you like to receive special offers?");
		Button dialogExitButton = (Button)dialog.findViewById(R.id.button2);
		dialogExitButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				dumpToFile();
				dialog.dismiss();
			}
			
		});
		Button offerButton = (Button)dialog.findViewById(R.id.button1);
		offerButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startRanging();
				dumpToFile();
				dialog.dismiss();
			}
			
		});
		dialog.show();	
	}
    
    @Override
    protected void onStop() {
      if(isRanging==true){
    	  beaconManager.stopRanging(region);
    	  isRanging=false;
    	  dumpToFile();
      }
      super.onStop();
    }

	@Override
	protected void onDestroy() {
		beaconManager.disconnect();
		super.onDestroy();
	}
    
    private boolean isMonitoringServiceRunning(){
    	ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
    	for(RunningServiceInfo service:manager.getRunningServices(Integer.MAX_VALUE)){
    		//Log.d("MESSAGE",service.service.getClassName());
    		if("com.project.zomato.BeaconMonitorService".equals(service.service.getClassName())){
    			return true;
    		}
    	}
    	return false;
    }
    
	private void startRanging() {
		isRanging = true;
		// Check if device supports Bluetooth Low Energy.
		if (!beaconManager.hasBluetooth()) {
			Toast.makeText(this, "Device does not have Bluetooth Low Energy",
					Toast.LENGTH_LONG).show();
			return;
		}

		// If Bluetooth is not enabled, let user enable it.
		if (!beaconManager.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			connectToService();
		}
    }
    
    private void connectToService() {
		getActionBar().setSubtitle("Scanning...");
		beaconManager.connect(new ServiceReadyCallback() {
			@Override
			public void onServiceReady() {
				beaconManager.startRanging(region);
			}
		});
	}
}
