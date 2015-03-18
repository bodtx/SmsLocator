package org.bodtx.smslocator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity implements
ConnectionCallbacks, OnConnectionFailedListener {

	private GoogleApiClient myGoogleApiClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Intent startSoundService = new Intent(this, SmsLocator.class);
//		this.startService(startSoundService);
		
		
	    
	    myGoogleApiClient = new GoogleApiClient.Builder(this)
	    .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
	    
	    
	    myGoogleApiClient.connect();


		
	}


	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.e("error", "fail connection google service!");
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		MyLocationListener myLocationListener = new MyLocationListener();
		
		 Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
				 myGoogleApiClient);
		 Log.i("location", "dernière position" +mLastLocation.getAccuracy());

		
		LocationRequest mLocationRequest = new LocationRequest();
	    mLocationRequest.setInterval(10000);
	    mLocationRequest.setFastestInterval(5000);
	    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	    
	    LocationServices.FusedLocationApi.requestLocationUpdates(
	    		myGoogleApiClient, mLocationRequest,  myLocationListener);

		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}


}
