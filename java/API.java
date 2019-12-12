import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class API extends AppCompatActivity {

    private void HttpPostData() {}

    protected void post() {
        try {
            URL url = new URL("http://139.162.102.224:89/api/v1/household-login");
            JSONObject jo = new JSONObject();
            jo.put("households_number", "A0201");
            jo.put("household_password", 123123);
            HttpClient httpClient = new DefaultHttpClient();
            AbstractHttpEntity entity = new ByteArrayEntity(jo.toString().getBytes("UTF8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            HttpPost httpPost = new HttpPost(url.toURI());
            httpPost.setEntity(entity);
            // Prepare JSON to send by setting the entity
            HttpResponse response = httpClient.execute(httpPost);
            String json_string = EntityUtils.toString(response.getEntity());
            JSONObject temp1 = new JSONObject(json_string);
            JSONObject data = temp1.getJSONObject("data");

            String tk = data.getString("name");
            System.out.println(tk);
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
    }
}
