package org.linphone.assistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.linphone.R;

public class BAlist extends Activity {
    String DomainIP = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balist);
        ListView BAlist1 = findViewById(R.id.BAlist1);
        ImageButton goback = findViewById(R.id.goback);
        ImageButton select = findViewById(R.id.select);
        final TextView startdate = findViewById(R.id.startdate);
        final TextView enddate = findViewById(R.id.enddate);
        SharedPreferences sPrefs = getSharedPreferences("Domain", MODE_PRIVATE);
        DomainIP = sPrefs.getString("IP", "");
        try {
            JSONObject allinfo = new JSONObject();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            String date = df.format(Calendar.getInstance().getTime());
            String date2 = df2.format(Calendar.getInstance().getTime());
            Log.e("date", date2);
            allinfo.put("startDate", date2);
            allinfo.put("endDate", date);

            URL url = new URL("http://" + DomainIP + "/riway/api/v1/alert/list");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url.toURI());
            StringEntity params = new StringEntity(allinfo.toString());
            Log.e("params", allinfo.toString());
            httpPost.addHeader("content-type", "application/json");
            httpPost.setEntity(params);
            // Prepare JSON to send by
            // setting the entity
            HttpResponse response = httpClient.execute(httpPost);
            String code = EntityUtils.toString(response.getEntity());
            Log.e("code", code);
            JSONObject temp1 = new JSONObject(code);
            JSONArray array1 = temp1.getJSONArray("items");
            List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

            if (array1.length() > 0) {
                JSONObject test = array1.getJSONObject(0);
                List<String> number = new ArrayList();
                List<String> local = new ArrayList();
                List<String> status = new ArrayList();
                number.add("事件　　　　　　　　　　　　地點　　　　　　　　　　　　狀態");
                for (int n = 0; n < array1.length(); n++) {
                    HashMap<String, Object> item = new HashMap<String, Object>();
                    JSONObject str_value = array1.getJSONObject(n);
                    item.put("name", str_value.getString("Name"));
                    item.put("event_time", str_value.getString("EventTime"));
                    item.put("alert", str_value.getString("alert"));
                    data.add(item);
                }
                SimpleAdapter adapter =
                        new SimpleAdapter(
                                this,
                                data,
                                R.layout.listitem,
                                new String[] {"name", "event_time", "alert"},
                                new int[] {R.id.name, R.id.event_time, R.id.alert});

                BAlist1.setAdapter(adapter);

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
        select.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); // 格式
                            Date date = format.parse(startdate.getText().toString()); // 第一個日期（字符串）
                            Date date1 = format.parse(enddate.getText().toString()); // 第二個日期（字符串）
                            if (date.getTime() > date1.getTime()) {
                                Toast.makeText(BAlist.this, "起始日期須大於結束日期", Toast.LENGTH_SHORT)
                                        .show();

                            } else {
                                JSONObject allinfo = new JSONObject();
                                URL url =
                                        new URL("http://" + DomainIP + "/riway/api/v1/alert/list");
                                HttpClient httpClient = new DefaultHttpClient();
                                HttpPost httpPost = new HttpPost(url.toURI());
                                allinfo.put("startDate", startdate.getText() + " 00:00:00");
                                allinfo.put("endDate", enddate.getText() + " 23:59:59");
                                StringEntity params = new StringEntity(allinfo.toString());
                                Log.e("params", allinfo.toString());
                                httpPost.addHeader("content-type", "application/json");
                                httpPost.setEntity(params);
                                // Prepare JSON to send by
                                // setting the entity
                                HttpResponse response = httpClient.execute(httpPost);
                                String code = EntityUtils.toString(response.getEntity());
                                Log.e("code", code);
                                JSONObject temp1 = new JSONObject(code);
                                JSONArray array1 = temp1.getJSONArray("items");
                                showdailog(array1);
                            }

                        } catch (Exception e) {
                            Toast.makeText(BAlist.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        startdate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        final DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

                        new DatePickerDialog(
                                        view.getContext(),
                                        new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(
                                                    DatePicker view, int year, int month, int day) {

                                                String dateString =
                                                        String.format(
                                                                "%d-%02d-%02d",
                                                                year, month + 1, day);

                                                startdate.setText(dateString);
                                            }
                                        },
                                        year,
                                        month,
                                        day)
                                .show();
                    }
                });
        enddate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        new DatePickerDialog(
                                        view.getContext(),
                                        new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(
                                                    DatePicker view, int year, int month, int day) {

                                                String dateString =
                                                        String.format(
                                                                "%d-%02d-%02d",
                                                                year, month + 1, day);
                                                enddate.setText(dateString);
                                            }
                                        },
                                        year,
                                        month,
                                        day)
                                .show();
                    }
                });
    }

    void showdailog(final JSONArray info) {
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        Log.e("info", info.toString());
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BAlist.this);
        View view = getLayoutInflater().inflate(R.layout.ba_select_list, null);
        ListView BAlist1 = view.findViewById(R.id.BAlist1);
        ImageButton closedailog = view.findViewById(R.id.closedailog);

        try {
            if (info.length() > 0) {
                JSONObject test = info.getJSONObject(0);

                for (int n = 0; n < info.length(); n++) {
                    HashMap<String, Object> item = new HashMap<String, Object>();
                    JSONObject str_value = info.getJSONObject(n);
                    item.put("name", str_value.getString("Name"));
                    item.put("event_time", str_value.getString("EventTime"));
                    item.put("alert", str_value.getString("alert"));
                    data.add(item);
                }
                SimpleAdapter adapter =
                        new SimpleAdapter(
                                this,
                                data,
                                R.layout.listitem,
                                new String[] {"name", "event_time", "alert"},
                                new int[] {R.id.name, R.id.event_time, R.id.alert});

                BAlist1.setAdapter(adapter);
            }
        } catch (Exception e) {
            Toast.makeText(BAlist.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        alertDialog.setView(view);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        closedailog.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
    }
}
