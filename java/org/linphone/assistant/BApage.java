package org.linphone.assistant;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.linphone.R;
import org.linphone.activities.DialerActivity;

public class BApage extends Activity {
    Timer timer;
    private ArrayList<Group> gData = null;
    private ArrayList<ArrayList<ClipData.Item>> iData = null;
    private ArrayList<ClipData.Item> lData = null;
    private Context mContext;
    private ExpandableListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bapage);
        SharedPreferences sPrefs = getSharedPreferences("Domain", MODE_PRIVATE);
        final String DomainIP = sPrefs.getString("IP", "");
        ImageButton goBA = findViewById(R.id.B_BA);
        ImageButton godoor = findViewById(R.id.B_door);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton goguard = findViewById(R.id.B_Guard);
        ImageButton golist = findViewById(R.id.golist);

        // final ListView BAlist = findViewById(R.id.BAlist);
        WebView baweb = findViewById(R.id.Baweb);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        baweb.getSettings().setJavaScriptEnabled(true);
        baweb.setWebViewClient(new WebViewClient()); // 不調用系統瀏覽器
        baweb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        baweb.getSettings().setDomStorageEnabled(true);
        baweb.loadUrl("http://18.181.171.107/riway_BA/BAview.html");
        golist.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(BApage.this, BAlist.class);
                        startActivity(intent);
                    }
                });
        godoor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(BApage.this, memberdatabase.class);
                        startActivity(intent);
                    }
                });
        gocall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(BApage.this, DialerActivity.class);
                        startActivity(intent);
                    }
                });
        goguard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(BApage.this, Guardpage.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        timer.scheduleAtFixedRate(new RemindTask(), 0, 5000);
    }

    private class RemindTask extends TimerTask {
        WebView baweb = findViewById(R.id.Baweb);

        @Override
        public void run() {
            runOnUiThread(
                    new Runnable() {
                        public void run() {
                            SharedPreferences sPrefs = getSharedPreferences("Domain", MODE_PRIVATE);
                            final String DomainIP = sPrefs.getString("IP", "");
                            baweb.loadUrl("http://18.181.171.107/riway_BA/BAview.html");
                        }
                    });
        }
    }
}
