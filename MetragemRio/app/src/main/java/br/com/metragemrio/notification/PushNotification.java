package br.com.metragemrio.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.parse.ParsePushBroadcastReceiver;

import java.util.Calendar;

import br.com.metragemrio.AppApplication;
import br.com.metragemrio.MainActivity;
import br.com.metragemrio.R;
import br.com.metragemrio.model.AlertNotification;

public class PushNotification extends ParsePushBroadcastReceiver {

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String message = extras != null ? extras.getString("com.parse.Data") : "";
        Gson gson = new Gson();
        AlertNotification push = gson.fromJson(message, AlertNotification.class);
        configureNotification(context, push);
    }

    private void configureNotification(Context context, AlertNotification notification) {
        String title = AppApplication.getContext().getString(R.string.app_name);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_message_black_36dp)
                        .setContentTitle(title)
                        .setContentText(notification.alert)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notification.alert));
        Notification pushNotification = builder.build();

        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) Calendar.getInstance().getTimeInMillis()/1000, i, PendingIntent.FLAG_ONE_SHOT);
        pushNotification.setLatestEventInfo(context, title, notification.alert, pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, pushNotification);
    }
}
