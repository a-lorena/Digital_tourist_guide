package com.example.digitaltouristguide.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import java.util.Random;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String title = intent.getStringExtra("title");
		String message = intent.getStringExtra("message");

		NotificationHelper helper = new NotificationHelper(context);
		NotificationCompat.Builder builder = helper.getGeofenceNotification(title, message);

		int notificationID = new Random().nextInt(5000);
		helper.getManager().notify(notificationID, builder.build());
	}
}