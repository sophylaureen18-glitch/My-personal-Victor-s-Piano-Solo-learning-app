package com.victor.piano;

import android.content.*;
import android.app.*;
import java.util.*;

public class NotificationReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context,Intent intent){
        String type=intent.getAction();
        if(type==null)return;
        Intent launch=new Intent(context,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(context,0,launch,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        String[] a;
        if("hour".equals(type)) a=new String[]{
            "Du bist nun schon eine Weile hier. Wie wäre es mit deinem ersten Takt?",
            "Das Piano wartet geduldig auf dich. Ein paar Minuten genügen für heute.",
            "Nun… vielleicht ist es Zeit für eine kleine Runde am Piano.",
            "Du wolltest doch noch ein wenig üben. Nur ein Takt wäre schon genug."
        };
        else a=new String[]{
            "Ich wollte dich nicht stören… aber dein Piano vermisst dich ein wenig.",
            "Du hast heute noch Zeit für einen kleinen Abschnitt.",
            "Nur ein paar Minuten. Mehr verlange ich heute gar nicht von dir.",
            "Dein Piano wartet noch geduldig. Vielleicht sehen wir uns später dort."
        };
        String body=a[new Random().nextInt(a.length)];
        NotificationManager nm=context.getSystemService(NotificationManager.class);
        if(nm==null)return;
        Notification.Builder b=android.os.Build.VERSION.SDK_INT>=26?new Notification.Builder(context,"william"):new Notification.Builder(context);
        b.setSmallIcon(com.victor.piano.R.drawable.ic_piano).setContentTitle("🌿 William").setContentText(body)
         .setStyle(new Notification.BigTextStyle().bigText(body)).setAutoCancel(true).setContentIntent(pi)
         .setCategory(Notification.CATEGORY_REMINDER);
        if(android.os.Build.VERSION.SDK_INT<26)b.setPriority(Notification.PRIORITY_DEFAULT);
        nm.notify((type+System.currentTimeMillis()).hashCode(),b.build());
        if("away".equals(type)){
            Calendar c=Calendar.getInstance(); c.add(Calendar.DAY_OF_YEAR,1); c.set(Calendar.HOUR_OF_DAY,c.get(Calendar.HOUR_OF_DAY)); 
            // The next daily reminder is scheduled by the app when it is opened again.
        }
    }
}