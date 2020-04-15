package org.linphone.assistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import org.linphone.core.tools.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

public class bulletin extends Activity {
    protected static final float FLIP_DISTANCE = 50;
    GestureDetector mDetector;
    int i = 0;
    TextView title, release, content;
    ImageView Bu_img;
    JSONArray out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bulletin_page);
        ImageButton home = findViewById(R.id.B_home);
        title = findViewById(R.id.Bu_title);
        release = findViewById(R.id.Bu_release);
        content = findViewById(R.id.Bu_content);
        Bu_img = findViewById(R.id.Bu_img);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(bulletin.this, home.class);
                        startActivity(intent);
                    }
                });
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
            String token = data.getString("access_token");

            HttpGet httpGet = new HttpGet("http://49.159.128.172:8888/api/v1/bulletinBoard/");
            httpGet.setHeader("Authorization", "Bearer " + token);

            HttpClient httpClient2 = new DefaultHttpClient();
            HttpResponse response2 = httpClient2.execute(httpGet);
            HttpEntity responseHttpEntity = response2.getEntity();
            String json_string2 = EntityUtils.toString(response2.getEntity());
            JSONObject temp2 = new JSONObject(json_string2);
            JSONObject data2 = temp2.getJSONObject("data");
            JSONArray data3 = data2.getJSONArray("items");
            out = data3;

            //   Toast.makeText(bulletin.this, data2.toString(), Toast.LENGTH_SHORT).show();

        } catch (MalformedURLException e) {
            Toast.makeText(bulletin.this, "1:" + e.toString(), Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(bulletin.this, "2:" + e.toString(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(bulletin.this, "3:" + e.toString(), Toast.LENGTH_SHORT).show();
        } catch (URISyntaxException e) {
            Toast.makeText(bulletin.this, "4:" + e.toString(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            title.setText(out.getJSONObject(0).getString("title"));
            release.setText(out.getJSONObject(0).getString("superintendent_name"));
            content.setText(out.getJSONObject(0).getString("content"));
            URL url = new URL(out.getJSONObject(0).getString("main_image"));
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            Bu_img.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDetector =
                new GestureDetector(
                        this,
                        new GestureDetector.OnGestureListener() {

                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                // TODO Auto-generated method stub
                                return false;
                            }

                            @Override
                            public void onShowPress(MotionEvent e) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public boolean onScroll(
                                    MotionEvent e1,
                                    MotionEvent e2,
                                    float distanceX,
                                    float distanceY) {
                                // TODO Auto-generated method stub
                                return false;
                            }

                            @Override
                            public void onLongPress(MotionEvent e) {
                                // TODO Auto-generated method stub

                            }

                            /**
                             * e1 The first down motion event that started the fling. e2 The move
                             * motion event that triggered the current onFling.
                             */
                            @Override
                            public boolean onFling(
                                    MotionEvent e1,
                                    MotionEvent e2,
                                    float velocityX,
                                    float velocityY) {
                                if (e1.getX() - e2.getX() > FLIP_DISTANCE) {
                                    Log.i("MYTAG", "向左滑...");
                                    i++;
                                    if (i > (out.length() - 1)) {
                                        i = out.length() - 1;
                                        Toast.makeText(
                                                        bulletin.this,
                                                        "已為最後一則公告",
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                    try {
                                        title.setText(out.getJSONObject(i).getString("title"));
                                        release.setText(
                                                out.getJSONObject(i)
                                                        .getString("superintendent_name"));
                                        content.setText(out.getJSONObject(i).getString("content"));
                                        URL url =
                                                new URL(
                                                        out.getJSONObject(i)
                                                                .getString("main_image"));
                                        Bitmap bmp =
                                                BitmapFactory.decodeStream(
                                                        url.openConnection().getInputStream());
                                        Bu_img.setImageBitmap(bmp);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    return true;
                                }
                                if (e2.getX() - e1.getX() > FLIP_DISTANCE) {
                                    Log.i("MYTAG", "向右滑...");
                                    i--;
                                    if (i < 0) {
                                        i = 0;
                                        Toast.makeText(bulletin.this, "已無新公告", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                    try {
                                        title.setText(out.getJSONObject(i).getString("title"));
                                        release.setText(
                                                out.getJSONObject(i)
                                                        .getString("superintendent_name"));
                                        content.setText(out.getJSONObject(i).getString("content"));
                                        URL url =
                                                new URL(
                                                        out.getJSONObject(i)
                                                                .getString("main_image"));
                                        Bitmap bmp =
                                                BitmapFactory.decodeStream(
                                                        url.openConnection().getInputStream());
                                        Bu_img.setImageBitmap(bmp);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    return true;
                                }
                                if (e1.getY() - e2.getY() > FLIP_DISTANCE) {
                                    Log.i("MYTAG", "向上滑...");
                                    return true;
                                }
                                if (e2.getY() - e1.getY() > FLIP_DISTANCE) {
                                    Log.i("MYTAG", "向下滑...");
                                    return true;
                                }

                                Log.d("TAG", e2.getX() + " " + e2.getY());

                                return false;
                            }

                            @Override
                            public boolean onDown(MotionEvent e) {
                                // TODO Auto-generated method stub
                                return false;
                            }
                        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }
}
