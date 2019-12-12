package org.linphone.views;

import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class Apimoudle extends AppCompatActivity {

    private void HttpPostData() {}

    public JSONObject Get(String s) {
        try {
            URL url = new URL(s);
            JSONObject jo = new JSONObject();
            // jo.put("households_number", "A0201");
            // jo.put("household_password", 123123);
            HttpClient httpClient = new DefaultHttpClient();
            AbstractHttpEntity entity = new ByteArrayEntity(jo.toString().getBytes("UTF8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            HttpGet httpGet = new HttpGet(url.toURI());
            // Prepare JSON to send by setting the entity
            HttpResponse response = httpClient.execute(httpGet);
            String json_string = EntityUtils.toString(response.getEntity());
            JSONObject temp1 = new JSONObject(json_string);

            return temp1;
            //  BufferedReader reader = new BufferedReader(new
            // InputStreamReader(response.getEntity().getContent(), "UTF-8"));

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

        return null;
    }
}
