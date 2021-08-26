package org.linphone.assistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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

public class member_modify extends Activity {
    int now = 1;
    int totalcount = 0;
    final ArrayList name = new ArrayList<>();
    final ArrayList id = new ArrayList<>();
    final ArrayList phone = new ArrayList<>();
    final ArrayList<JSONObject> allinfo = new ArrayList<>();
    private ListView memberlist;
    private TextView totalpage;
    ArrayList<String> Lv1 = new ArrayList<>();
    final ArrayList<ArrayList<String>> Lv2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = this.getIntent();
        // 取得傳遞過來的資料
        final String groupid = intent.getStringExtra("groupid");
        final String getdata = intent.getStringExtra("data");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meberlist_modify);
        final ListView memberlist = findViewById(R.id.memberlist);
        final Button lastpage = findViewById(R.id.lastpage);
        Button nextpage = findViewById(R.id.nextpage);
        Button delete = findViewById(R.id.B_delete);
        Button modify = findViewById(R.id.B_modify);
        final TextView nowpage = findViewById(R.id.nowpage);
        TextView totalpage = findViewById(R.id.totalpage);
        try {
            JSONArray array1 = new JSONArray(getdata);
            for (int x = 0; x < array1.length(); x++) {
                JSONObject str_value = array1.getJSONObject(x);
                Lv1.add(str_value.getString("name"));
                JSONArray array2 = str_value.getJSONArray("childs");
                if (array2.length() > 0) {
                    ArrayList<Map<String, String>> test = new ArrayList<Map<String, String>>();
                    ArrayList<String> test2 = new ArrayList<>();
                    for (int y = 0; y < array2.length(); y++) {
                        JSONObject str_value2 = array2.getJSONObject(y);
                        Log.d("內容", str_value2 + "");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", str_value2.getString("name"));
                        test2.add(str_value2.getString("name"));
                        map.put("ID", str_value2.getString("id"));
                        test.add(map);
                    }
                    Lv2.add(test2);
                } else {
                    ArrayList<Map<String, String>> test = new ArrayList<Map<String, String>>();
                    ArrayList<String> test2 = new ArrayList<>();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name", "");
                    map.put("ID", "");
                    test.add(map);
                    test2.add("");
                    Lv2.add(test2);
                }
            }

        } catch (JSONException err) {
            Log.d("Error", err.toString());
        }
        lastpage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (now == 1) {

                        } else {
                            now--;
                            getpage(now, groupid);
                            nowpage.setText(String.valueOf(now));
                        }
                    }
                });
        nextpage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (now == totalcount) {
                        } else {
                            now++;
                            getpage(now, groupid);
                            nowpage.setText(String.valueOf(now));
                        }
                    }
                });
        delete.setOnClickListener(
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
                                AlertDialog.Builder dialog =
                                        new AlertDialog.Builder(member_modify.this);
                                dialog.setTitle("刪除確認");
                                dialog.setMessage("將" + name.get(i) + "刪除");
                                dialog.setNegativeButton(
                                        "確認",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                // TODO Auto-generated method stub
                                                Toast.makeText(
                                                                member_modify.this,
                                                                "刪除成功",
                                                                Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                                dialog.show();
                            }
                        }
                    }
                });
        modify.setOnClickListener(
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
                                showdailog(allinfo.get(i));
                            }
                        }
                    }
                });
        memberlist.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        try {
                            String stringText;
                            // in normal case
                            stringText = ((TextView) view).getText().toString();
                            Toast.makeText(
                                            member_modify.this,
                                            allinfo.get(name.indexOf(stringText))
                                                    .getString("phone"),
                                            Toast.LENGTH_SHORT)
                                    .show();
                            return true;
                        } catch (Exception e) {
                        }
                        return true;
                    }
                });
        getpage(1, groupid);
    }

    void getpage(int page, String department) {
        memberlist = (ListView) findViewById(R.id.memberlist);
        totalpage = (TextView) findViewById(R.id.totalpage);
        name.clear();
        phone.clear();
        id.clear();
        allinfo.clear();
        try {
            URL url = new URL("http://18.181.171.107/riway/api/v1/clients/main/list");
            JSONObject body = new JSONObject();
            body.put("page", page);
            body.put("department", department);
            HttpClient httpClient = new DefaultHttpClient();
            AbstractHttpEntity entity = new ByteArrayEntity(body.toString().getBytes("UTF8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            HttpPost httpPost = new HttpPost(url.toURI());
            httpPost.setEntity(entity);
            // Prepare JSON to send by setting the entity
            HttpResponse response = httpClient.execute(httpPost);
            String code = EntityUtils.toString(response.getEntity());
            JSONObject temp1 = new JSONObject(code);
            JSONObject array1 = temp1.getJSONObject("lists");
            int total = temp1.getInt("totalPages");
            totalcount = total;
            totalpage.setText(total + "");
            JSONArray array2 = array1.getJSONArray("data");
            for (int i = 0; i < array2.length(); i++) {
                JSONObject jsonObject = array2.getJSONObject(i);
                allinfo.add(jsonObject);
                name.add(jsonObject.getString("name") + jsonObject.getString("id"));
                id.add(jsonObject.getString("id"));
                phone.add(jsonObject.getString("phone"));
            }
            memberlist.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            final ArrayAdapter adapter2 =
                    new ArrayAdapter(this, R.layout.mymulitlist_item_onechioce);
            adapter2.addAll(name);
            memberlist.setAdapter(adapter2);
            // 將JSON字串，放到JSONArray中。

        } catch (Exception e) {
            // Toast.makeText(memberdatabase.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    void showdailog(JSONObject info) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(member_modify.this);
        View view = getLayoutInflater().inflate(R.layout.modify_dailog, null);
        Button modify = view.findViewById(R.id.modify);
        EditText name = view.findViewById(R.id.Editname);
        EditText phone = view.findViewById(R.id.Editphone);
        final Spinner groupLv1 = view.findViewById(R.id.groupLv1);
        final Spinner groupLv2 = view.findViewById(R.id.groupLv2);
        final ArrayAdapter<String> adapterLv1 =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Lv1);
        ArrayAdapter<String> adapterLv2 =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Lv2.get(1));
        groupLv1.setAdapter(adapterLv1);
        groupLv2.setAdapter(adapterLv2);
        groupLv1.setSelection(getIndex(groupLv1, "總部大樓員工"));

        groupLv1.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> adapterView, View view, int position, long l) {
                        ArrayAdapter<String> adapterLv2 =
                                new ArrayAdapter<String>(
                                        member_modify.this,
                                        android.R.layout.simple_spinner_item,
                                        Lv2.get(position));

                        groupLv2.setAdapter(adapterLv2);
                        groupLv2.setSelection(getIndex(groupLv2, "工程部"));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
        try {
            name.setText(info.getString("name"));
            phone.setText(info.getString("phone"));
        } catch (Exception e) {
        }
        alertDialog.setTitle("資料修改");
        alertDialog.setView(view);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        Log.e("index", index + "");
        return index;
    }
}
