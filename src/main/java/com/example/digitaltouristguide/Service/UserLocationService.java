package com.example.digitaltouristguide.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import org.jetbrains.annotations.NotNull;

public class UserLocationService extends Service {

	private LocationCallback locationCallback = new LocationCallback() {
		@Override
		public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
			super.onLocationResult(locationResult);

			if (locationResult != null && locationResult.getLastLocation() != null) {
				double latitude = locationResult.getLastLocation().getLatitude();
				double longitude = locationResult.getLastLocation().getLatitude();

			}
		}
	};

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implementes");
	}

	private void startLocationService() {
		String channelID = "location_notification";
	}
}
