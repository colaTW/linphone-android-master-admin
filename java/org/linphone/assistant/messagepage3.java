package org.linphone.assistant;

import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.R;

public class messagepage3 extends Activity implements View.OnClickListener {
    ViewPager pager;
    ArrayList<View> pagerList;
    JSONArray out;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_page3);
        ImageButton home = findViewById(R.id.B_home);
        home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(messagepage3.this, home.class);
                        startActivity(intent);
                    }
                });

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        String pass = "";
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
            String uuid = data.getString("uu_id");
            token = data.getString("access_token");

            HttpGet httpGet =
                    new HttpGet(
                            "http://49.159.128.172:8888/api/v1/question/household/device/" + uuid);
            HttpClient httpClient2 = new DefaultHttpClient();
            httpGet.setHeader("Authorization", "Bearer " + token);
            HttpResponse response2 = httpClient2.execute(httpGet);
            HttpEntity responseHttpEntity = response2.getEntity();
            String json_string2 = EntityUtils.toString(response2.getEntity());
            JSONObject temp2 = new JSONObject(json_string2);
            JSONObject data2 = temp2.getJSONObject("data");
            JSONArray data3 = data2.getJSONArray("items");
            out = data3;
            TableLayout showlist = (TableLayout) findViewById(R.id.showlist);

            for (int i = 0; i < out.length(); i++) {
                TableRow row = new TableRow(this);
                TextView tv = new TextView(this);
                tv.setText(out.getJSONObject(i).getString("question_content"));
                row.addView(tv);
                ImageView img = new ImageView(this);
                URL uri = new URL(out.getJSONObject(i).getString("main_image"));
                Bitmap bmp = BitmapFactory.decodeStream(uri.openConnection().getInputStream());
                img.setImageBitmap(bmp);
                System.out.println(uri);
                row.addView(img);
                Button bt = new Button(this);
                bt.setText("查看回覆");
                bt.setId(Integer.valueOf(out.getJSONObject(i).getString("question_id")));
                bt.setOnClickListener(this);
                row.addView(bt);

                showlist.addView(row);
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

    public void onClick(View v) {
        String content = "";
        try {
            HttpGet httpGet =
                    new HttpGet(
                            "http://49.159.128.172:8888/api/v1/question/reply/list/" + v.getId());
            HttpClient httpClient2 = new DefaultHttpClient();
            httpGet.setHeader("Authorization", "Bearer " + token);
            HttpResponse response2 = httpClient2.execute(httpGet);
            HttpEntity responseHttpEntity = response2.getEntity();
            String json_string2 = EntityUtils.toString(response2.getEntity());
            JSONObject temp2 = new JSONObject(json_string2);
            JSONArray data3 = temp2.getJSONArray("items");
            System.out.println(temp2);
            content = data3.getJSONObject(0).getString("reply_content");
        } catch (Exception e) {
            e.printStackTrace();
        }
        new AlertDialog.Builder(messagepage3.this)
                .setIcon(R.drawable.appicon)
                .setTitle("回覆")
                .setMessage(content)
                .setPositiveButton(
                        "ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                .show();
    }
}
