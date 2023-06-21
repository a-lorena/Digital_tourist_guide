package com.example.digitaltouristguide.Service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class GeofenceHelper extends ContextWrapper {

	public static final String TAG = "GeofenceHelper";
	PendingIntent pendingIntent;

	public GeofenceHelper(Context base) {
		super(base);
	}

	public GeofencingRequest getGeofencingRequest(List<Geofence> geofencesList) {

		return new GeofencingRequest.Builder()
				.addGeofences(geofencesList)
				.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
				.build();
	}

	public Geofence getGeofence(String id, LatLng latLng, float radius, int transitionTypes) {
		return new Geofence.Builder()
				.setCircularRegion(latLng.latitude, latLng.longitude, radius)
				.setRequestId(id)
				.setExpirationDuration(Geofence.NEVER_EXPIRE)
				.setTransitionTypes(transitionTypes)
				.build();
	}

	public PendingIntent getPendingIntent() {
		if (pendingIntent != null) {
			return pendingIntent;
		}

		Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		return pendingIntent;
	}
}
