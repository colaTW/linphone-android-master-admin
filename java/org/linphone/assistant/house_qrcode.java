package org.linphone.assistant;

import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.VideoView;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
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

public class house_qrcode extends Activity {
    private VideoView videoView;
    private String uri =
            "rtsp://admin:dh123456@pingling.ddns.net:554/cam/realmonitor?channel=1&subtype=00&authbasic=YWRtaW46ZGgxMjM0NTY=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.house_qrcode);
        WebView webView = findViewById(R.id.wv_webview);

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
            String h_id = data.getString("household_id");
            String h_type = data.getString("household_type");
            if (h_type == "2") {
                HttpGet httpGet =
                        new HttpGet("http://49.159.128.172:8888/v1/households/qrcode/" + h_id);
                HttpClient httpClient2 = new DefaultHttpClient();
                HttpResponse response2 = httpClient2.execute(httpGet);
                HttpEntity responseHttpEntity = response2.getEntity();

                String qrcode = EntityUtils.toString(response2.getEntity());
                webView.setWebViewClient(new WebViewClient());
                // 使用简单的loadData()方法总会导致乱码，有可能是Android API的Bug
                // webView.loadData(data, "text/html", "GBK");
                webView.loadDataWithBaseURL(null, qrcode, "text/html", "utf-8", null);
            } else {
                Toast.makeText(house_qrcode.this, "非戶長機無法產生QRCODE", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(house_qrcode.this, home.class);
                startActivity(intent);
            }
            System.out.println(h_id);
            // } catch (Exception e) {
            //     Toast.makeText(house_qrcode.this, "post" + e.toString(),
            // Toast.LENGTH_SHORT).show();
            // }
            //   try {

            // } catch (Exception e) {
            //     Toast.makeText(house_qrcode.this, e.toString(), Toast.LENGTH_SHORT).show();
            //  }
        } catch (MalformedURLException e) {
            Toast.makeText(house_qrcode.this, "1:" + e.toString(), Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(house_qrcode.this, "2:" + e.toString(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(house_qrcode.this, "3:" + e.toString(), Toast.LENGTH_SHORT).show();
        } catch (URISyntaxException e) {
            Toast.makeText(house_qrcode.this, "4:" + e.toString(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Toast.makeText(house_qrcode.this, "5:" + e.toString(), Toast.LENGTH_SHORT).show();
        }

        String data = "";
    }
}
