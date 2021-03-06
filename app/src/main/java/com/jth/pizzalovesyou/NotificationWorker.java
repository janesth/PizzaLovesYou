package com.jth.pizzalovesyou;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {

    NotificationManagerCompat notificationManager;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        setupNotificationManager();
    }

    @NonNull
    @Override
    public Result doWork() {
        CharSequence company = getInputData().getString("data_company");
        NotificationCompat.Builder builder = setupBuilder(company);
        notificationManager.notify(1, builder.build());
        return Result.success();
    }

    protected void setupNotificationManager() {
        String channelId = "PIZZA_CHANNEL";
        CharSequence channelName = "Pizza Channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{1000, 2000});
        notificationManager.createNotificationChannel(notificationChannel);
    }

    protected NotificationCompat.Builder setupBuilder(CharSequence company) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "PIZZA_CHANNEL");

        //Set content
        builder.setSmallIcon(R.mipmap.ic_pizza);
        builder.setContentTitle(String.format(getApplicationContext().getResources().getString(R.string.notif_sender), company));
        builder.setContentText("Hope you're ok " + new String(Character.toChars(0x1F60A)));

        //div. settings
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder;
    }
}
