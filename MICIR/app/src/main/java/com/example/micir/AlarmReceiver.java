package com.example.micir;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

/**
 * Created by 正文 on 2016/12/13.
 */

public class AlarmReceiver extends BroadcastReceiver {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,"提醒摟",Toast.LENGTH_SHORT).show();
            CharSequence from="MICIR";
            CharSequence message =intent.getStringExtra("message");
            System.out.println("show notification.");

            NotificationManager noMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent call=new Intent(context,Open_Page.class);
            call.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(context,0,call,0);
            Notification notif=new Notification.Builder(context)
                    .setContentIntent(contentIntent)
                    .setContentTitle(from)
                    .setContentText(message)
                    .setTicker(from)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.micir_logo)
                    .build();

            noMgr.notify(0,notif);
            call.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(call);

        }

}
