package com.example.digitaltouristguide.Service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.digitaltouristguide.Preview.EventPreview;
import com.example.digitaltouristguide.R;

public class NotificationHelper extends ContextWrapper {
	public static final String channelID = "alarmChannelID";
	public static final String channelName = "Alarm Channel";
	private NotificationManager manager;

	public NotificationHelper(Context base) {
		super(base);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			createChannel();
		}

	}

	@TargetApi(Build.VERSION_CODES.O)
	public void createChannel() {
		NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

		channel.enableLights(true);
		channel.enableVibration(true);
		channel.setLightColor(R.color.colorPrimary);
		channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

		getManager().createNotificationChannel(channel);
	}

	public NotificationManager getManager() {
		if (manager == null) {
			manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}

		return manager;
	}

	public NotificationCompat.Builder getNotification(String title, String message, String id, Context context) {
		Intent openIntent = new Intent(context, EventPreview.class);
		openIntent.putExtra("event_id", id);
		openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

		PendingIntent openPendingIntent = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		return new NotificationCompat.Builder(getApplicationContext(), channelID)
				.setContentTitle(title)
				.setContentText(message)
				.setSmallIcon(R.drawable.icon_event_reminder)
				.setColor(Color.GREEN)
				.setAutoCancel(true)
				.setSound(sound)
				.setContentIntent(openPendingIntent);
	}

	public NotificationCompat.Builder getGeofenceNotification(String title, String message) {

		Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		return new NotificationCompat.Builder(getApplicationContext(), channelID)
				.setContentTitle(title)
				.setContentText(message)
				.setSmallIcon(R.drawable.icon_walk)
				.setColor(Color.GREEN)
				.setAutoCancel(true)
				.setSound(sound);
	}
}
