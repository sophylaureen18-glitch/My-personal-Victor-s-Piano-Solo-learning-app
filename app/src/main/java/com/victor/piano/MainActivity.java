package com.victor.piano;

import android.Manifest;
import android.app.*;
import android.graphics.Color;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.*;
import android.provider.Settings;
import android.webkit.*;
import androidx.webkit.WebViewAssetLoader;
import android.view.Window;
import java.util.*;

public class MainActivity extends Activity {
    private static final int REQ_AUDIO=1001, REQ_NOTIFICATIONS=1002;
    private static final String APP_URL="https://appassets.androidplatform.net/assets/index.html";
    private static final String PREFS="victor_notifications";
    private static final String CHANNEL_ID="william";
    private WebView webView;
    private PermissionRequest pendingPermissionRequest;

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Window w=getWindow(); w.setStatusBarColor(0xFF17131F); w.setNavigationBarColor(0xFF0F0C14);
        createNotificationChannel();
        if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},REQ_NOTIFICATIONS);

        webView=new WebView(this);
        webView.setBackgroundColor(Color.TRANSPARENT);
        WebSettings s=webView.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false); s.setBuiltInZoomControls(false); s.setDisplayZoomControls(false);
        s.setLoadWithOverviewMode(false); s.setUseWideViewPort(false); s.setCacheMode(WebSettings.LOAD_NO_CACHE);
        s.setAllowFileAccess(false); s.setAllowContentAccess(false);
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder().addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this)).build();
        webView.setWebViewClient(new WebViewClient(){
            @Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request){
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
            @Override public void onPageFinished(WebView view, String url){
                super.onPageFinished(view,url);
                view.evaluateJavascript("(function(){try{var old=document.getElementById('androidMeasureFix');if(old)old.remove();var st=document.createElement('style');st.id='androidMeasureFix';st.textContent='#measureLegend{display:flex;gap:6px;flex-wrap:wrap;margin-top:9px}#measureLegend button{padding:8px 9px;min-width:48px}#measureLegend button.active{border-color:var(--accent);color:var(--accent);box-shadow:0 0 0 1px var(--accent) inset}body.wall-home::before{opacity:.92 !important}body.wall-home::after{background:linear-gradient(180deg,rgba(9,8,13,.06),rgba(9,8,13,.18)) !important}#home .timer,#home #timer,#home [id*=timer]{display:none !important}';document.head.appendChild(st);var p=document.getElementById('taktPicker');if(p&&!document.getElementById('measureLegend')){var l=document.createElement('div');l.id='measureLegend';l.className='takt-picker';p.parentNode.insertBefore(l,p);var m=(document.getElementById('taktNumber')?.textContent||'').match(/\\d+/g)||[];if(m.length){for(var i=+m[0],e=m[1]?+m[1]:+m[0];i<=e;i++){var b=document.createElement('button');b.type='button';b.textContent='Takt '+i;b.onclick=function(){document.querySelectorAll('#measureLegend button').forEach(function(x){x.classList.remove('active')});this.classList.add('active')};l.appendChild(b)}}}}catch(e){}})();", null);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override public void onPermissionRequest(final PermissionRequest request){runOnUiThread(()->{
                boolean audio=false; for(String r:request.getResources()) if(PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(r)) audio=true;
                if(audio){pendingPermissionRequest=request;
                    if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED) request.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE});
                    else requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},REQ_AUDIO);
                }
            });}
        });
        webView.addJavascriptInterface(new AndroidBridge(), "AndroidApp");
        setContentView(webView); webView.loadUrl(APP_URL);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=26){
            NotificationChannel c=new NotificationChannel(CHANNEL_ID,"William-Benachrichtigungen",NotificationManager.IMPORTANCE_DEFAULT);
            c.setDescription("Begrüßungen, Erinnerungen und Lob von William.");
            getSystemService(NotificationManager.class).createNotificationChannel(c);
        }
    }

    public void showWilliam(String type){
        String[] a;
        if("welcome".equals(type)) a=new String[]{
            "Ah, du bist wieder da. Ich habe mich schon gefragt, wann du zurückkommst.",
            "Willkommen zurück. Dein Piano wartet bereits auf dich.",
            "Schön, dich wiederzusehen. Bereit für ein wenig Musik?",
            "Da bist du ja. Dann können wir wohl anfangen.",
            "Willkommen. Heute machen wir wieder einen kleinen Schritt."
        };
        else if("hour".equals(type)) a=new String[]{
            "Du bist nun schon eine Weile hier. Wie wäre es mit deinem ersten Takt?",
            "Das Piano wartet geduldig auf dich. Ein paar Minuten genügen für heute.",
            "Nun… vielleicht ist es Zeit für eine kleine Runde am Piano.",
            "Du wolltest doch noch ein wenig üben. Nur ein Takt wäre schon genug."
        };
        else if("praise".equals(type)) a=new String[]{
            "Sehr gut. Du wirst von Tag zu Tag sicherer.",
            "Das war eine schöne Übung. Du darfst ruhig ein wenig stolz auf dich sein.",
            "Wieder ein Stück geschafft. Langsam, aber stetig.",
            "Du hast heute Fortschritte gemacht. Das zählt.",
            "Ausgezeichnet. Dein Weihnachtsprojekt nimmt Gestalt an."
        };
        else if("away".equals(type)) a=new String[]{
            "Ich wollte dich nicht stören… aber dein Piano vermisst dich ein wenig.",
            "Du hast heute noch Zeit für einen kleinen Abschnitt.",
            "Nur ein paar Minuten. Mehr verlange ich heute gar nicht von dir.",
            "Dein Piano wartet noch geduldig. Vielleicht sehen wir uns später dort."
        };
        else a=new String[]{
            "Ich bin da. Jetzt eine kleine Piano-Runde?",
            "Nun, da du mich schon gerufen hast… spielen wir ein wenig?",
            "Ich höre dir zu. Ein kleiner Abschnitt vielleicht?"
        };
        String body=a[new Random().nextInt(a.length)];
        postWilliam(body,type);
    }

    private void postWilliam(String body,String type){
        if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED) return;
        Intent i=new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,CHANNEL_ID):new Notification.Builder(this);
        b.setSmallIcon(com.victor.piano.R.drawable.ic_piano).setContentTitle("🌿 William").setContentText(body)
         .setStyle(new Notification.BigTextStyle().bigText(body)).setAutoCancel(true).setContentIntent(pi)
         .setCategory(Notification.CATEGORY_REMINDER);
        getSystemService(NotificationManager.class).notify(Math.abs((type+body).hashCode()),b.build());
    }

    private void schedule(String type,long trigger){
        Intent i=new Intent(this,NotificationReceiver.class).setAction(type);
        PendingIntent pi=PendingIntent.getBroadcast(this,type.hashCode(),i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am=getSystemService(AlarmManager.class);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,trigger,pi);
    }
    private void cancel(String type){
        Intent i=new Intent(this,NotificationReceiver.class).setAction(type);
        PendingIntent pi=PendingIntent.getBroadcast(this,type.hashCode(),i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        getSystemService(AlarmManager.class).cancel(pi);
    }
    private void scheduleDaily(String time){
        try{
            String[] p=time.split(":"); int h=Integer.parseInt(p[0]),m=Integer.parseInt(p[1]);
            Calendar c=Calendar.getInstance(); c.set(Calendar.HOUR_OF_DAY,h); c.set(Calendar.MINUTE,m); c.set(Calendar.SECOND,0); c.set(Calendar.MILLISECOND,0);
            if(c.getTimeInMillis()<=System.currentTimeMillis()) c.add(Calendar.DAY_OF_YEAR,1);
            schedule("away",c.getTimeInMillis());
        }catch(Exception ignored){}
    }

    public class AndroidBridge {
        @JavascriptInterface public void showWilliam(String type){runOnUiThread(()->MainActivity.this.showWilliam(type));}
        @JavascriptInterface public void practiceStarted(){cancel("hour");}
        @JavascriptInterface public void scheduleHourReminder(boolean enabled){
            if(!enabled){cancel("hour");return;}
            schedule("hour",System.currentTimeMillis()+60L*60L*1000L);
        }
        @JavascriptInterface public void scheduleDailyReminder(boolean enabled,String time){
            if(!enabled){cancel("away");return;} scheduleDaily(time);
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grants){
        super.onRequestPermissionsResult(requestCode,permissions,grants);
        if(requestCode==REQ_AUDIO && pendingPermissionRequest!=null){
            if(grants.length>0&&grants[0]==PackageManager.PERMISSION_GRANTED) pendingPermissionRequest.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE});
            else pendingPermissionRequest.deny(); pendingPermissionRequest=null;
        }
    }
    @Override public void onBackPressed(){if(webView!=null&&webView.canGoBack())webView.goBack();else super.onBackPressed();}
    @Override protected void onDestroy(){if(webView!=null)webView.destroy();super.onDestroy();}
}