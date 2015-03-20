package org.bodtx.smslocator;

import java.util.Date;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class SmsLocator extends Service {

	// private WakeLock wakeLock;

	private volatile double precision;

	private WifiManager wifiManager;
	
	LocationManager locationManager;

	private LocationListener locationListenerNetWork;

	private LocationListener locationListenerGPS;

	@Override
	public void onCreate() {
		super.onCreate();
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		// First find the action we need to handle
		String smsreceive = "android.provider.Telephony.SMS_RECEIVED";
		if (intent != null && intent.getAction().equals(smsreceive)) {
			/* The SMS-Messages are 'hiding' within the extras of the Intent. */
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				// Retrieve the data store in the SMS
				Object[] pdus = (Object[]) bundle.get("pdus");
				// Declare the associated SMS Messages
				SmsMessage[] smsMessages = new SmsMessage[pdus.length];
				// Rebuild your SMS Messages
				for (int i = 0; i < pdus.length; i++) {
					smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				// Parse your SMS Message
				SmsMessage currentMessage;
				String body = "null", from = "null";
				long time;
				for (int i = 0; i < smsMessages.length; i++) {
					currentMessage = smsMessages[i];
					body = currentMessage.getDisplayMessageBody();
					from = currentMessage.getDisplayOriginatingAddress();
					time = currentMessage.getTimestampMillis();
					Log.i("MySmsReceiver", "SMS :[" + from + "] " + body);
					// TODO ask for the notification to display it
				}
				if (body.equals("Position")) {
					precision = 10000.0;
					calculatePosition(from);
				} else if (body.equals("Stop")) {
					locationManager.removeUpdates(locationListenerGPS);
					locationManager.removeUpdates(locationListenerNetWork);
					stopSelf();
				}
			}
		}

		return START_NOT_STICKY;
	}

	private void calculatePosition(final String from) {
		// PowerManager mgr = (PowerManager)
		// this.getSystemService(Context.POWER_SERVICE);
		// wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
		// "MyWakeLock");
		// wakeLock.acquire();

		// TODO do not work on android >= 4.4
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			Intent intentGPS = new Intent("android.location.GPS_ENABLED_CHANGE");
			intentGPS.putExtra("enabled", true);
			sendBroadcast(intentGPS);
		}

		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);

		

		locationListenerNetWork = new LocationListener() {
			public void onLocationChanged(Location location) {
				Log.i("precision vs accuracy network", precision + " vs "+ location.getAccuracy());
				if (location.getAccuracy() < precision) {
					Log.i("Votre position Network", location.getLatitude() + " " + location.getLongitude());
					SmsManager.getDefault().sendTextMessage(
							from,
							null,
							"coordonnées antenne: " + location.getLatitude() + " " + location.getLongitude()
									+ " précision " + location.getAccuracy(), null, null);

					precision = location.getAccuracy();
				}
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		locationListenerGPS = new LocationListener() {
			public void onLocationChanged(Location location) {
				Log.i("precision vs accuracy gps", precision + " vs "+ location.getAccuracy());
				if (location.getAccuracy() < precision) {
					Log.i("Votre position GPS ", location.getLatitude() + " " + location.getLongitude());
					SmsManager.getDefault().sendTextMessage(
							from,
							null,
							"coordonnées GPS: " + location.getLatitude() + " " + location.getLongitude()
									+ " précision " + location.getAccuracy(), null, null);
					precision = location.getAccuracy();
				}
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetWork);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
			intent.putExtra("enabled", false);
			sendBroadcast(intent);
		}

		wifiManager.setWifiEnabled(false);

		// wakeLock.release();
		Log.i("arrêt", "arrêt");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
