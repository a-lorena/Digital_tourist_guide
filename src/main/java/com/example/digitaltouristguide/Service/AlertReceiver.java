package com.example.digitaltouristguide.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import java.util.Random;

public class AlertReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String title = intent.getStringExtra("alarm_title");
		String message = intent.getStringExtra("alarm_message");
		String openID = intent.getStringExtra("open_id");

		NotificationHelper helper = new NotificationHelper(context);
		NotificationCompat.Builder builder = helper.getNotification(title, message, openID, context);

		int notificationID = new Random().nextInt(5000);
		helper.getManager().notify(notificationID, builder.build());
	}
}
