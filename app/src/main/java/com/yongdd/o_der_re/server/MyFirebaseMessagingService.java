package com.yongdd.o_der_re.server;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
      //  super.onMessageReceived(remoteMessage);
        try {
            if(remoteMessage!=null) {
                sendNotification(remoteMessage);
            }else{
                Log.d("notification","remoteMessage is null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) throws JSONException {

        String title = remoteMessage.getData().get("title");
        if(title==null){
            title = remoteMessage.getNotification().getTitle();

        }

            final String CHANNEL_ID = "ChannerID";
            NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                final String CHANNEL_NAME = "O:Der";
                final String CHANNEL_DESCRIPTION = "새로운 주문이 들어왔습니다.";
                final int importance = NotificationManager.IMPORTANCE_HIGH;

                // add in API level 26
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
                mChannel.setDescription(CHANNEL_DESCRIPTION);
                mChannel.enableLights(true);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                mManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setSmallIcon(R.drawable.ic_launcher_app_icon);
            builder.setAutoCancel(true);
            builder.setDefaults(Notification.DEFAULT_ALL);
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("알림");
            builder.setContentText("새로운 주문이 들어왔습니다.");
            //builder.setContentIntent()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                builder.setVibrate(new long[]{500, 500});
            }
            mManager.notify(0, builder.build());

            zeroListReset();

    }

    public void zeroListReset(){
        MainActivity mainActivity = new MainActivity();
        HomeFragment homeFragment = new HomeFragment();
        if(MainActivity.firstZero){
            if(MainActivity.orderLists.size()==0){
                mainActivity.getDate();
                homeFragment.reloadView();
            }
        }else{
            return;
        }

    }


}