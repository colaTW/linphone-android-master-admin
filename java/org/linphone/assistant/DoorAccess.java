package org.linphone.assistant;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import java.security.acl.Group;
import java.util.ArrayList;

public class DoorAccess extends Activity {
    private ArrayList<Group> gData = null;
    private ArrayList<ArrayList<ClipData.Item>> iData = null;
    private ArrayList<ClipData.Item> lData = null;
    private Context mContext;
    private ListView memberlist;
    final ArrayList id = new ArrayList<>();
    final ArrayList name = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dooraccess);
        final ListView memberlist = findViewById(R.id.memberlist);
        ImageButton goBA = findViewById(R.id.B_BA);
        ImageButton godoor = findViewById(R.id.B_door);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton goguard = findViewById(R.id.B_Guard);
        ImageButton memberdata = findViewById(R.id.memberdata);
        Button dooracess = findViewById(R.id.dooracess);
        Button approvedlist = findViewById(R.id.approvedList);
        Button accescard = findViewById(R.id.AccessCard);
        Button next = findViewById(R.id.nextpage);
        final ProgressBar pgSpinner = findViewById(R.id.memberlistspiner);
        pgSpinner.setVisibility(View.INVISIBLE);

        pgSpinner.setVisibility(View.VISIBLE);
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                getmemberlist();
                                                pgSpinner.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        })
                .start();

        // 增加標頭
        // View header = LayoutInflater.from(this).inflate(R.layout.list_view_header, null);
        // memberlist.addHeaderView(header);

        goBA.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess.this, BApage.class);
                        startActivity(intent);
                    }
                });

        gocall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess.this, DialerActivity.class);
                        startActivity(intent);
                    }
                });
        goguard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess.this, Guardpage.class);
                        startActivity(intent);
                    }
                });
        memberdata.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess.this, memberdatabase.class);
                        startActivity(intent);
                    }
                });

        approvedlist.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess.this, ApporvedList.class);
                        startActivity(intent);
                    }
                });
        accescard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess.this, AccesCard.class);
                        startActivity(intent);
                    }
                });
        next.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SparseBooleanArray checkedItemPositions =
                                memberlist.getCheckedItemPositions();
                        Bundle bundle = new Bundle();
                        ArrayList<String> stringList = new ArrayList<String>();

                        for (int i = 0; i < name.size(); i++) {
                            // 根据key获取对应的boolean值，没有则返回false
                            Boolean aBoolean = checkedItemPositions.get(i);
                            if (aBoolean) {
                                Log.e("i:" + i, name.get(i) + "");
                                stringList.add(name.get(i).toString());
                            }
                        }

                        Intent intent = new Intent();
                        intent.putStringArrayListExtra("ListString", stringList);
                        intent.setClass(DoorAccess.this, DoorAccess_floor.class);
                        startActivity(intent);
                    }
                });
    }

    private void getmemberlist() {
        memberlist = (ListView) findViewById(R.id.memberlist);
        try {
            HttpGet httpGet = new HttpGet("http://54.95.142.9/riway/api/v1/clients/main/list");
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity responseHttpEntity = response.getEntity();
            String code = EntityUtils.toString(response.getEntity());
            JSONObject temp1 = new JSONObject(code);
            JSONObject array1 = temp1.getJSONObject("lists");
            int total = temp1.getInt("totalPages");
            JSONArray array2 = array1.getJSONArray("data");
            for (int i = 0; i < array2.length(); i++) {
                JSONObject jsonObject = array2.getJSONObject(i);
                name.add(jsonObject.getString("name") + jsonObject.getString("id"));
                id.add(jsonObject.getString("id"));
            }
            if (total > 1) {
                for (int i = 2; i <= total; i++)
                    httpGet =
                            new HttpGet(
                                    "http://54.95.142.9/riway/api/v1/clients/main/list?page=" + i);
                httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
                response = httpClient.execute(httpGet);
                responseHttpEntity = response.getEntity();
                code = EntityUtils.toString(response.getEntity());
                temp1 = new JSONObject(code);
                array1 = temp1.getJSONObject("lists");
                array2 = array1.getJSONArray("data");
                for (int i = 0; i < array2.length(); i++) {
                    JSONObject jsonObject = array2.getJSONObject(i);
                    name.add(jsonObject.getString("name") + jsonObject.getString("id"));
                    id.add(jsonObject.getString("id"));
                }
            }
            memberlist.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            ArrayAdapter adapter = new ArrayAdapter(this, R.layout.mymulitlist_item);
            adapter.addAll(name);
            Log.e("看", name.toString());
            memberlist.setAdapter(adapter);

            // 將JSON字串，放到JSONArray中。

        } catch (Exception e) {
            Toast.makeText(DoorAccess.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
