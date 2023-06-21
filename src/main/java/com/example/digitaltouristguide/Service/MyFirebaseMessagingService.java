package com.example.digitaltouristguide.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.digitaltouristguide.Preview.AccPreview;
import com.example.digitaltouristguide.Preview.EventPreview;
import com.example.digitaltouristguide.Preview.InstitutionPreview;
import com.example.digitaltouristguide.Preview.MonumentPreview;
import com.example.digitaltouristguide.Preview.NaturePreview;
import com.example.digitaltouristguide.Preview.PutopisPreview;
import com.example.digitaltouristguide.Preview.RestaurantPreview;
import com.example.digitaltouristguide.Preview.TownPreview;
import com.example.digitaltouristguide.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

	@Override
	public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);

		String title = remoteMessage.getNotification().getTitle();
		String body = remoteMessage.getNotification().getBody();

		Map<String, String> extraData = remoteMessage.getData();
		String category = extraData.get("category");

		Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this,
				"channelID")
				.setContentTitle(title)
				.setContentText(body)
				.setSmallIcon(R.drawable.icon_town)
				.setColor(Color.GREEN)
				.setAutoCancel(true)
				.setSound(sound);

		Intent intent = null;
		if (category.equals("town")) {
			String townID = extraData.get("townID");

			intent = new Intent(this, TownPreview.class);
			intent.putExtra("location_id", townID);
			intent.putExtra("previewType", "pickTown");

		} else if (category.equals("institution")) {
			String institutionID = extraData.get("institutionID");

			intent = new Intent(this, InstitutionPreview.class);
			intent.putExtra("institution_id", institutionID);

		} else if (category.equals("monument")) {
			String monumentID = extraData.get("monumentID");

			intent = new Intent(this, MonumentPreview.class);
			intent.putExtra("monument_id", monumentID);

		} else if (category.equals("event")) {
			String eventID = extraData.get("eventID");

			intent = new Intent(this, EventPreview.class);
			intent.putExtra("event_id", eventID);

		} else if (category.equals("nature")) {
			String natureID = extraData.get("natureID");

			intent = new Intent(this, NaturePreview.class);
			intent.putExtra("nature_id", natureID);

		} else if (category.equals("restaurant")) {
			String restaurantID = extraData.get("restaurantID");

			intent = new Intent(this, RestaurantPreview.class);
			intent.putExtra("restaurant_id", restaurantID);

		} else if (category.equals("acc")) {
			String accID = extraData.get("accID");

			intent = new Intent(this, AccPreview.class);
			intent.putExtra("acc_id", accID);

		} else if (category.equals("Putopis")) {
			String putopisID = extraData.get("putopisID");
			String putopisAuthor = extraData.get("putopis_author");

			intent = new Intent(this, PutopisPreview.class);
			intent.putExtra("putopis_id", putopisID);
			intent.putExtra("putopis_author", putopisAuthor);
		}

		PendingIntent pendingIntent = PendingIntent.getActivity(
				this,
				10,
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT
		);

		builder.setContentIntent(pendingIntent);

		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		int notificationID = new Random().nextInt(3000);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(
					"channelID",
					"Demo",
					NotificationManager.IMPORTANCE_HIGH
			);
			manager.createNotificationChannel(channel);
		}

		builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
		manager.notify(notificationID, builder.build());
	}
}