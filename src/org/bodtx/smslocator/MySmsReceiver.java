/**<ul>
 * <li>SmsListenerTuto</li>
 * <li>com.android2ee.formation.service.smslisteners.smslistenertuto</li>
 * <li>15 oct. 2012</li>
 * 
 * <li>======================================================</li>
 *
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 *
 /**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br> 
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 *  Belongs to <strong>Mathias Seguy</strong></br>
 ****************************************************************************************************************</br>
 * This code is free for any usage except training and can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * 
 * *****************************************************************************************************************</br>
 *  Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 *  Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br> 
 *  Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 *  <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */
package org.bodtx.smslocator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals This class aims to listen for SMS reception and launch a service to
 *        display a notifiation (why a service ? just to show how it can work,
 *        it's a dummy app)
 */
public class MySmsReceiver extends BroadcastReceiver {

	LocationManager locationManager;

	public MySmsReceiver() {
	}

	@Override
	public void onReceive(final Context context, Intent intent) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListenerNetWork = new LocationListener() {
			public void onLocationChanged(Location location) {
				Log.i("Votre position Network", location.getLatitude() + " " + location.getLongitude());

				Location locationA = new Location("Cirso");

				locationA.setLatitude(43.6299978);
				locationA.setLongitude(1.4755789);

				float distanceTo = location.distanceTo(locationA);

				Log.i("Distance du cirso ", String.valueOf(distanceTo));
				Toast.makeText(context, "distance du cirso "+distanceTo, Toast.LENGTH_LONG).show();
				if (distanceTo < 1500.0) {
					SmsManager.getDefault().sendTextMessage("+33687419862", null, "Je pars du boulot", null, null);
				}

			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		String action = intent.getAction();
		Calendar now = Calendar.getInstance();
		int jourSemaine = now.get(Calendar.DAY_OF_WEEK);
		if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action) && jourSemaine != Calendar.SATURDAY
				&& jourSemaine != Calendar.SUNDAY && now.get(Calendar.HOUR_OF_DAY) > 14) {
			// Get the BluetoothDevice object from the Intent
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			Toast.makeText(context, device.getName() + "\n" + device.getAddress(), Toast.LENGTH_LONG).show();
			if (device.getName().equals("BT_CAR_SYSTEM")) {
				locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListenerNetWork,null);
			}
		} else {
			Log.i("MySmsReceiver", "SMS Received");
			// Launch the service:
			// Build the Intent
			Intent serviceStart = new Intent(context, SmsLocator.class);
			// Set its action
			serviceStart.setAction("android.provider.Telephony.SMS_RECEIVED");
			// Add elements (the SMS data in fact)
			serviceStart.putExtras(intent.getExtras());
			// start the service
			context.startService(serviceStart);
		}
	}

}
