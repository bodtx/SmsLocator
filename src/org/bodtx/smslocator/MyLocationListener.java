package org.bodtx.smslocator;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationListener;

public class MyLocationListener implements LocationListener {

	@Override
	public void onLocationChanged(Location arg0) {
		Log.i("location ", String.valueOf(arg0.getAccuracy()));

	}

}
