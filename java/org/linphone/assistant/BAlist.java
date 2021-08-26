package org.linphone.assistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
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

public class BAlist extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balist);
        ListView BAlist1 = findViewById(R.id.BAlist1);
        ListView BAlist2 = findViewById(R.id.BAlist2);
        ListView BAlist3 = findViewById(R.id.BAlist3);
        Button goback = findViewById(R.id.goback);
        try {
            HttpGet httpGet = new HttpGet("http://18.181.171.107/riway/api/v1/alert/few/minutes");
            HttpClient httpClient2 = new DefaultHttpClient();
            httpClient2.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
            httpClient2.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
            HttpResponse response2 = httpClient2.execute(httpGet);
            HttpEntity responseHttpEntity = response2.getEntity();
            String code = EntityUtils.toString(response2.getEntity());
            JSONObject temp1 = new JSONObject(code);
            JSONArray array1 = temp1.getJSONArray("items");

            if (array1.length() > 0) {
                JSONObject test = array1.getJSONObject(0);
                List<String> number = new ArrayList();
                List<String> local = new ArrayList();
                List<String> status = new ArrayList();
                number.add("事件");
                local.add("地點");
                status.add("狀態");
                for (int n = 0; n < array1.length(); n++) {
                    JSONObject str_value = array1.getJSONObject(n);
                    String Event = str_value.getString("Event");
                    String name = str_value.getString("Name");
                    String alert = str_value.getString("alert");
                    number.add(Event);
                    local.add(name);
                    status.add(alert);
                }

                ArrayAdapter adapter1 = new ArrayAdapter(BAlist.this, R.layout.mylist_item, number);
                ArrayAdapter adapter2 = new ArrayAdapter(BAlist.this, R.layout.mylist_item, local);
                ArrayAdapter adapter3 = new ArrayAdapter(BAlist.this, R.layout.mylist_item, status);
                BAlist1.setAdapter(adapter1);
                BAlist2.setAdapter(adapter2);
                BAlist3.setAdapter(adapter3);
                // new一個ArrayAdapter，android.R.layout.simple_list_item_1為ListView顯示的佈局檔案
                // 位ListView設定Adapter
            }
        } catch (Exception e) {
            Toast.makeText(BAlist.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        goback.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(BAlist.this, BApage.class);
                        startActivity(intent);
                    }
                });
    }
}
