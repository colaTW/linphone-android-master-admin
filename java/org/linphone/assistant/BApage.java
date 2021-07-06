package org.linphone.assistant;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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
        ExpandableListView memberlist = findViewById(R.id.memberlist);
        ImageButton goBA = findViewById(R.id.B_BA);
        ImageButton godoor = findViewById(R.id.B_door);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton goguard = findViewById(R.id.B_Guard);
        final ListView BAlist = findViewById(R.id.BAlist);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

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
        timer.scheduleAtFixedRate(new RemindTask(), 0, 5000); //
    }

    private class RemindTask extends TimerTask {
        ListView BAlist = findViewById(R.id.BAlist);

        @Override
        public void run() {
            runOnUiThread(
                    new Runnable() {
                        public void run() {
                            Toast.makeText(BApage.this, "test", Toast.LENGTH_SHORT);
                            try {
                                HttpGet httpGet =
                                        new HttpGet(
                                                "http://54.95.142.9/riway/api/v1/alert/few/minutes");
                                HttpClient httpClient2 = new DefaultHttpClient();
                                httpClient2
                                        .getParams()
                                        .setParameter(
                                                CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
                                httpClient2
                                        .getParams()
                                        .setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
                                HttpResponse response2 = httpClient2.execute(httpGet);
                                HttpEntity responseHttpEntity = response2.getEntity();
                                String code = EntityUtils.toString(response2.getEntity());
                                JSONObject temp1 = new JSONObject(code);
                                JSONArray array1 = temp1.getJSONArray("items");
                                if (array1.length() > 0) {
                                    JSONObject test = array1.getJSONObject(0);
                                    List<String> m_li = new ArrayList();
                                    m_li.add("事件  　　位置　　　　狀態");

                                    for (int n = 0; n < array1.length(); n++) {
                                        JSONObject str_value = array1.getJSONObject(n);
                                        String Event = str_value.getString("Event");
                                        String name = str_value.getString("Name");
                                        String alert = str_value.getString("alert");
                                        m_li.add(Event + "  　　" + name + "  　" + alert);
                                    }
                                    ArrayAdapter adapter =
                                            new ArrayAdapter(
                                                    BApage.this,
                                                    android.R.layout.simple_list_item_1,
                                                    m_li);
                                    BAlist.setAdapter(adapter);
                                    // new一個ArrayAdapter，android.R.layout.simple_list_item_1為ListView顯示的佈局檔案
                                    // 位ListView設定Adapter
                                }
                            } catch (Exception e) {
                                Toast.makeText(BApage.this, e.toString(), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
        }
    }
}
