package com.camera.simplewebcam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

public class BackupCameraActivity extends Activity {
	
	private final static String TAG = "BackupCameraActivity";
	private static boolean activityRunning=false;
	CameraPreview cp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		activityRunning = true;
		cp = new CameraPreview(this);
		setContentView(cp);
		
		//start monitoring service
    	
		startVehicleMonitoringService();
		
		
     	registerCloseReceiver();
		registerMUsbReceiver();
		
        
	}
	
	private void startVehicleMonitoringService() {
		
		Intent VehicleMonitoringServiceIntent = new Intent(BackupCameraActivity.this, VehicleMonitoringService.class);
    	startService(VehicleMonitoringServiceIntent);	
     	Log.w(TAG, "Starting Service from BackupCameraActivity");
		
	}

	@Override
	public void onPause() {
		super.onPause();
		activityRunning = false;
		unregisterReceiver(mUsbReceiver);
		unregisterReceiver(closeReceiver);
		//android.os.Process.killProcess(android.os.Process.myPid());
	 Log.w(TAG, "in onPause");	
	}
	
	@Override
	public void onResume() {
		super.onResume();
		activityRunning = true;
		registerCloseReceiver();
		registerMUsbReceiver();
		 Log.w(TAG, "in onResume");	

	}
	
	private void registerMUsbReceiver() {
		IntentFilter usbfilter = new IntentFilter();
        usbfilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        usbfilter.addAction("com.ford.openxc.NO_CAMERA_DETECTED");
        registerReceiver(mUsbReceiver, usbfilter);
		
	}

	private void registerCloseReceiver() {
		//create intent filter to listen for unreversing of vehicle to close activity
		IntentFilter closeFilter = new IntentFilter();
		closeFilter.addAction("com.ford.openxc.VEHICLE_UNREVERSED");
		registerReceiver(closeReceiver, closeFilter);
		
	}

	BroadcastReceiver closeReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};
	
	BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        
		@Override
        public void onReceive(Context context, Intent intent) {
            Log.w("USB ERROR", "Usb device detached");
            usbError();   
        }
    };
	
	public void finish() {
		this.finish();
		activityRunning = false;
		Log.w(TAG, "in onfinish");
		//unregisterReceiver(mUsbReceiver);
		//unregisterReceiver(closeReceiver);
		//android.os.Process.killProcess(android.os.Process.myPid());
	}
	 
	public void usbError(){
	        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
	        vibrator.vibrate(2000);

	        new AlertDialog.Builder(this)
	        .setTitle("USB Device Unplugged!")
	        .setMessage("FordBackupCam is closing. Please insert device(s) and reopen from main menu.")
	        .setCancelable(false)
	        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int id) {
	        		activityRunning = false;
	        		android.os.Process.killProcess(android.os.Process.myPid());
	            }
	        }).show();

	}
	
	public static boolean isRunning() {
		return activityRunning;
		
	}
	
//TODO
	//write ondestroy to unbind from services/receivers  
	@Override
	public void onDestroy(){
		activityRunning = false;
		Log.w(TAG, "in ondestroy");
	}
	
	
	
}
	 
	
