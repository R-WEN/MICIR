package com.example.micir;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.micir.MyDB.FoodItemDAO;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import at.markushi.ui.CircleButton;

public class Open_Page extends Activity {
    private static int SPLASH_TIME_OUT = 3000;
    private FoodItemDAO db;

    private CircleButton micirbtn;
    private TextView tv;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private ImageView loadingiv;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open__page);
        db = new FoodItemDAO(this);

        loadingiv=(ImageView)findViewById(R.id.loading_iv);
        tv = (TextView) findViewById(R.id.openpage_tv);
        micirbtn = (CircleButton) findViewById(R.id.micirbtn);
        micirbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Open_Page.this, TopHorizontalNtbActivity.class);
                startActivity(intent);
            }
        });
        String s = null;
        new iniTask().execute(s);







    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setAlarm(String result) {
        System.out.println("show notification.");
        CharSequence from="MICIR";
        CharSequence message =result;
        NotificationManager noMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent call=new Intent(this,Open_Page.class);
        call.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,call,0);
        Notification notif=new Notification.Builder(this)
                .setContentIntent(contentIntent)
                .setContentTitle(from)
                .setContentText(message)
                .setTicker(from)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.micir_logo)
                .build();

        noMgr.notify(0,notif);



        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,8);

        Intent intent = new Intent();
        intent.putExtra("message", result);
        intent.setClass(this, AlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_HALF_DAY, pending);


    }




    private class iniTask extends AsyncTask<String, Integer, String> {
        private String notifiString="";
        @Override
        protected void onPreExecute() {
            //執行前 設定可以在這邊設定
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {
            //執行中 在背景做事情
            int progress = 0;

            String result = "";
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = sDateFormat.format(new Date());
            String today = getYear(date) + getMonth(date) + getDay(date);
            result += date + "\n";
            progress += 10;
            publishProgress(progress);
            ArrayList<FoodItem> items = db.getAll();
            Iterator<FoodItem> iterator = items.iterator();
            int Extra_count = 0;
            int count = 0;
            while (iterator.hasNext()) {
                FoodItem item = iterator.next();
                String s = getYear(item.getFoodDate()) + getMonth(item.getFoodDate()) + getDay(item.getFoodDate());
                if ((Long.parseLong(s) - Long.parseLong(today)) < 0) {
                    Extra_count++;
                } else if ((Long.parseLong(s) - Long.parseLong(today)) <= 3) {
                    count++;
                }
                progress += 80 / items.size();
                publishProgress(progress);
            }
            /*if (Extra_count > 0) {
                result += "有" + Extra_count + "項食物已經過期!!\n";
            }*/
            result += "有" + count + "項食物3天內過期";
            if (count >0){  notifiString="今天有"+count+"樣三天內即將過期囉";}
            publishProgress(100);
            return result;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //執行中 可以在這邊告知使用者進度
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String result) {
            //執行後 完成背景任務
            super.onPostExecute(result);
            tv.setText(result);
            if(!notifiString.equals("")) {
                setAlarm(notifiString);
            }
            fadeinanimation();

        }
    }


    private void fadeinanimation() {
        final Animation fadein = new AlphaAnimation(0, 1);
        fadein.setDuration(1000);
        fadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                micirbtn.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        micirbtn.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
        micirbtn.startAnimation(fadein);
        tv.startAnimation(fadein);


        final Animation fadeout = new AlphaAnimation(1, 0);
        fadeout.setDuration(1000);
        fadeout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                loadingiv.clearAnimation();
                loadingiv.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        loadingiv.startAnimation(fadeout);
    }


    private String getYear(String date) {
        System.out.println("Y:" + date.substring(0, 4));
        return date.substring(0, 4);

    }

    private String getMonth(String date) {
        System.out.println("M:" + date.substring(5, 7));
        return date.substring(5, 7);
    }

    private String getDay(String date) {
        System.out.println("D:" + date.substring(8, 10));
        return date.substring(8, 10);
    }


}
