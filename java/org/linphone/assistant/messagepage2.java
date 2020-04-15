package org.linphone.assistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.R;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

public class messagepage2 extends Activity {
    ViewPager pager;
    ArrayList<View> pagerList;
    String pass = "";
    String token = "";
    String house_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_page2);
        ImageButton home = findViewById(R.id.B_home);
        Button M_submit = findViewById(R.id.R_submit);
        Button Gas_submit = findViewById(R.id.Gas_submit);
        final EditText event = findViewById(R.id.detail);
        final EditText R_gas = findViewById(R.id.R_gas);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            FileInputStream fin = openFileInput("info.txt");
            byte[] buffer = new byte[100];
            int byteCount = fin.read(buffer);
            String outinfo = "";
            outinfo = new String(buffer, 0, byteCount, "utf-8");
            String out[] = outinfo.split("\\,");
            pass = out[1];
            fin.close();
        } catch (Exception e) {
        }
        try {
            URL url = new URL("http://49.159.128.172:8888/api/v1/household/device/login");
            JSONObject jo = new JSONObject();
            jo.put(
                    "device_uuid",
                    Settings.Secure.getString(
                            getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
            jo.put("password", pass);
            // jo.put("password", "09426153");
            HttpClient httpClient = new DefaultHttpClient();
            AbstractHttpEntity entity = new ByteArrayEntity(jo.toString().getBytes("UTF8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            // try {
            HttpPost httpPost = new HttpPost(url.toURI());
            httpPost.setEntity(entity);
            // Prepare JSON to send by setting the entity
            HttpResponse response = httpClient.execute(httpPost);
            String json_string = EntityUtils.toString(response.getEntity());
            JSONObject temp1 = new JSONObject(json_string);
            JSONObject data = temp1.getJSONObject("data");
            token = data.getString("access_token");
            house_id = data.getString("household_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(messagepage2.this, home.class);
                        startActivity(intent);
                    }
                });
        M_submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            URL url2 = new URL("http://49.159.128.172:8888/api/v1/question");
                            JSONObject jo2 = new JSONObject();
                            jo2.put("household_id", house_id);
                            jo2.put(
                                    "household_device_uuid",
                                    Settings.Secure.getString(
                                            getContext().getContentResolver(),
                                            Settings.Secure.ANDROID_ID));
                            jo2.put("question_content", event.getText().toString());
                            jo2.put("main_image_file", "");
                            HttpClient httpClient2 = new DefaultHttpClient();
                            AbstractHttpEntity entity2 =
                                    new ByteArrayEntity(jo2.toString().getBytes("UTF8"));
                            entity2.setContentType(
                                    new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                            HttpPost httpPost2 = new HttpPost(url2.toURI());
                            httpPost2.setHeader("Authorization", "Bearer " + token);
                            httpPost2.setEntity(entity2);
                            // Prepare JSON to send by setting the entity
                            HttpResponse response2 = httpClient2.execute(httpPost2);
                            String json_string2 = EntityUtils.toString(response2.getEntity());
                            JSONObject temp2 = new JSONObject(json_string2);
                            String data2 = temp2.getString("errors");

                            if (data2.toString().equals("")) {
                                Toast.makeText(messagepage2.this, "成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(
                                                messagepage2.this,
                                                data2.toString(),
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Gas_submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Calendar c = Calendar.getInstance();
                            int year = c.get(Calendar.YEAR);
                            int month = c.get(Calendar.MONTH);
                            System.out.println(year + "/" + month);

                            HttpGet httpGet =
                                    new HttpGet(
                                            "http://49.159.128.172:8888/api/v1/gas/meter/record/check/1/"
                                                    + year
                                                    + "/"
                                                    + (month + 1));
                            HttpClient httpClient2 = new DefaultHttpClient();
                            httpGet.setHeader("Authorization", "Bearer " + token);
                            HttpResponse response2 = httpClient2.execute(httpGet);
                            HttpEntity responseHttpEntity = response2.getEntity();
                            String json_string2 = EntityUtils.toString(response2.getEntity());
                            JSONObject temp2 = new JSONObject(json_string2);

                            String code = temp2.getString("code");
                            if (code.equals("19")) {
                                Toast.makeText(messagepage2.this, "請等待管理員開放填寫", Toast.LENGTH_SHORT)
                                        .show();
                            } else if (code.equals("20")) {
                                Toast.makeText(messagepage2.this, "本月已填寫", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                String mainID = temp2.getString("gas_meter_main_id");
                                try {
                                    URL url3 =
                                            new URL(
                                                    "http://49.159.128.172:8888/api/v1/gas/meter/record");
                                    JSONObject jo3 = new JSONObject();
                                    jo3.put("gas_meter_main_id", mainID);
                                    jo3.put("household_id", house_id);
                                    jo3.put(
                                            "household_device_uuid",
                                            Settings.Secure.getString(
                                                    getContext().getContentResolver(),
                                                    Settings.Secure.ANDROID_ID));
                                    jo3.put("record", R_gas.getText().toString());
                                    HttpClient httpClient3 = new DefaultHttpClient();
                                    AbstractHttpEntity entity3 =
                                            new ByteArrayEntity(jo3.toString().getBytes("UTF8"));
                                    entity3.setContentType(
                                            new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                                    HttpPost httpPost3 = new HttpPost(url3.toURI());
                                    httpPost3.setHeader("Authorization", "Bearer " + token);
                                    httpPost3.setEntity(entity3);
                                    // Prepare JSON to send by setting the entity
                                    HttpResponse response3 = httpClient3.execute(httpPost3);
                                    String json_string3 =
                                            EntityUtils.toString(response3.getEntity());
                                    JSONObject temp3 = new JSONObject(json_string2);

                                    String data2 = temp3.getString("errors");

                                    if (data2.toString().equals("")) {
                                        Toast.makeText(messagepage2.this, "成功", Toast.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        Toast.makeText(
                                                        messagepage2.this,
                                                        data2.toString(),
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
