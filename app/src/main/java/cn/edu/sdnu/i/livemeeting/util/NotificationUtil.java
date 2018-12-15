package cn.edu.sdnu.i.livemeeting.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import cn.edu.sdnu.i.livemeeting.MainActivity;
import cn.edu.sdnu.i.livemeeting.R;

public class NotificationUtil {

    //TODO 消息提示

//    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//    Intent notificationIntent = new Intent(context, MainActivity.class);
//notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
//    PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
//mBuilder.setContentTitle(senderStr)//设置通知栏标题
//            .setContentText(contentStr)
//            .setContentIntent(intent) //设置通知栏点击意图
//            .setNumber(++pushNum) //设置通知集合的数量
//            .setTicker(senderStr+":"+contentStr) //通知首次出现在通知栏，带上升动画效果的
//            .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
//            .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
//            .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
//    Notification notify = mBuilder.build();
//    notify.flags |= Notification.FLAG_AUTO_CANCEL;
//mNotificationManager.notify(pushId, notify);
}
