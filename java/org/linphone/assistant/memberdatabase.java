package org.linphone.assistant;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.net.URL;
import java.security.acl.Group;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.linphone.R;
import org.linphone.activities.DialerActivity;

public class memberdatabase extends Activity {
    private ArrayList<Group> gData = null;
    private ArrayList<ArrayList<ClipData.Item>> iData = null;
    private ArrayList<ClipData.Item> lData = null;
    private Context mContext;
    private ExpandableListView list;
    private ListView memberlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memberdatabase);
        ImageButton goBA = findViewById(R.id.B_BA);
        ImageButton godoor = findViewById(R.id.B_door);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton goguard = findViewById(R.id.B_Guard);
        ImageButton memberdata = findViewById(R.id.memberdata);
        Button dooracess = findViewById(R.id.dooracess);
        Button approvedlist = findViewById(R.id.approvedList);
        Button accescard = findViewById(R.id.AccessCard);
        RadioGroup cardtype = findViewById(R.id.cardtype);
        RadioButton cardbutton = findViewById(R.id.card);
        RadioButton QRbutton = findViewById(R.id.QRcode);
        Button sendButton = findViewById(R.id.sendButton);
        final LinearLayout cardlayout = findViewById(R.id.cardlayout);
        final ArrayList mData = new ArrayList<>();
        final EditText name = findViewById(R.id.Editname);
        final EditText phone = findViewById(R.id.Editphone);
        final ProgressBar listspiner = findViewById(R.id.memberlistspiner);
        final ProgressBar sendspiner = findViewById(R.id.sendspiner);
        listspiner.setVisibility(View.INVISIBLE);
        sendspiner.setVisibility(View.INVISIBLE);

        QRbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cardlayout.setVisibility(View.INVISIBLE);
                    }
                });
        cardbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cardlayout.setVisibility(View.VISIBLE);
                    }
                });
        listspiner.setVisibility(View.VISIBLE);
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                getmemberlist();
                                                listspiner.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        })
                .start();

        sendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listspiner.setVisibility(View.VISIBLE);
                        sendspiner.setVisibility(View.VISIBLE);
                        new Thread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                runOnUiThread(
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    URL url =
                                                                            new URL(
                                                                                    "http://54.95.142.9/riway/api/v1/clients/main/one");
                                                                    JSONObject jo =
                                                                            new JSONObject();
                                                                    jo.put(
                                                                            "name",
                                                                            name.getText()
                                                                                    .toString());
                                                                    jo.put(
                                                                            "phone",
                                                                            phone.getText()
                                                                                    .toString());
                                                                    jo.put(
                                                                            "mobile",
                                                                            phone.getText()
                                                                                    .toString());
                                                                    HttpClient httpClient =
                                                                            new DefaultHttpClient();
                                                                    AbstractHttpEntity entity =
                                                                            new ByteArrayEntity(
                                                                                    jo.toString()
                                                                                            .getBytes(
                                                                                                    "UTF8"));
                                                                    entity.setContentType(
                                                                            new BasicHeader(
                                                                                    HTTP.CONTENT_TYPE,
                                                                                    "application/json"));
                                                                    HttpPost httpPost =
                                                                            new HttpPost(
                                                                                    url.toURI());
                                                                    httpPost.setEntity(entity);
                                                                    // Prepare JSON to send by
                                                                    // setting the entity
                                                                    HttpResponse response =
                                                                            httpClient.execute(
                                                                                    httpPost);
                                                                    String json_string =
                                                                            EntityUtils.toString(
                                                                                    response
                                                                                            .getEntity());
                                                                    JSONObject temp1 =
                                                                            new JSONObject(
                                                                                    json_string);
                                                                    JSONObject data =
                                                                            temp1.getJSONObject(
                                                                                    "data");
                                                                    String error =
                                                                            data.getString(
                                                                                    "errors");
                                                                    if (error.equals("")) {
                                                                        Toast.makeText(
                                                                                        memberdatabase
                                                                                                .this,
                                                                                        "新增成功",
                                                                                        Toast
                                                                                                .LENGTH_SHORT)
                                                                                .show();
                                                                        name.setText("");
                                                                        phone.setText("");
                                                                        sendspiner.setVisibility(
                                                                                View.GONE);
                                                                        getmemberlist();
                                                                        listspiner.setVisibility(
                                                                                View.GONE);
                                                                    }
                                                                } catch (Exception e) {
                                                                    Toast.makeText(
                                                                                    memberdatabase
                                                                                            .this,
                                                                                    e.toString(),
                                                                                    Toast
                                                                                            .LENGTH_SHORT)
                                                                            .show();
                                                                }
                                                            }
                                                        });
                                            }
                                        })
                                .start();
                    }
                });

        goBA.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(memberdatabase.this, BApage.class);
                        startActivity(intent);
                    }
                });

        gocall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(memberdatabase.this, DialerActivity.class);
                        startActivity(intent);
                    }
                });
        goguard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(memberdatabase.this, Guardpage.class);
                        startActivity(intent);
                    }
                });
        dooracess.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(memberdatabase.this, DoorAccess.class);
                        startActivity(intent);
                    }
                });
        approvedlist.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(memberdatabase.this, ApporvedList.class);
                        startActivity(intent);
                    }
                });
        accescard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(memberdatabase.this, AccesCard.class);
                        startActivity(intent);
                    }
                });
    }

    private void getmemberlist() {
        final ArrayList name = new ArrayList<>();
        final ArrayList id = new ArrayList<>();
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
            final ArrayAdapter adapter2 = new ArrayAdapter(this, R.layout.mylist_item);
            adapter2.addAll(name);
            memberlist.setAdapter(adapter2);
            // 將JSON字串，放到JSONArray中。

        } catch (Exception e) {
            Toast.makeText(memberdatabase.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
