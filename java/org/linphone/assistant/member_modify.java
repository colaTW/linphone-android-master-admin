package org.linphone.assistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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
    ArrayList<String> Lv1_ID = new ArrayList<>();
    final ArrayList<ArrayList<String>> Lv2 = new ArrayList<>();
    final ArrayList select_type_name = new ArrayList<>();
    private Spinner select_type;
    EditText select_name;
    JSONObject getselect;
    String DomainIP = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = this.getIntent();
        // 取得傳遞過來的資料
        final String groupid = intent.getStringExtra("groupid");
        final String getdata = intent.getStringExtra("data");
        final String getselect = intent.getStringExtra("select");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meberlist_modify);
        final ListView memberlist = findViewById(R.id.memberlist);
        final Button lastpage = findViewById(R.id.lastpage);
        Button nextpage = findViewById(R.id.nextpage);
        Button delete = findViewById(R.id.B_delete);
        final TextView nowpage = findViewById(R.id.nowpage);
        TextView totalpage = findViewById(R.id.totalpage);
        Spinner select_type = findViewById(R.id.select_type);
        Button select_button = findViewById(R.id.select);
        final Button modify = findViewById(R.id.modify);
        final EditText modifyname = findViewById(R.id.Editname);
        final TextView modifyphone = findViewById(R.id.Editphone);
        final EditText password = findViewById(R.id.Editpassword);
        final Spinner groupLv1 = findViewById(R.id.groupLv1);
        final Spinner groupLv2 = findViewById(R.id.groupLv2);
        final LinearLayout modiftdialog = findViewById(R.id.modify_dialog);
        ImageButton memberdata = findViewById(R.id.memberdata);
        ImageButton dooracess = findViewById(R.id.dooracess);
        ImageButton approvedlist = findViewById(R.id.approvedList);
        ImageButton accescard = findViewById(R.id.AccessCard);
        ImageButton longtime = findViewById(R.id.Longtime);
        SharedPreferences sPrefs = getSharedPreferences("Domain", MODE_PRIVATE);
        DomainIP = sPrefs.getString("IP", "");

        if (getselect.equals("")) {
            select_type_name.add("電話");
            select_type_name.add("姓名");
        } else if (getselect.contains("name")) {
            select_type_name.add("電話");
        } else {
            select_type_name.add("姓名");
        }
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        member_modify.this, R.layout.myspinner_item, select_type_name);
        adapter.setDropDownViewResource(R.layout.myspinner_dropitem);
        select_type.setAdapter(adapter);
        try {
            JSONArray array1 = new JSONArray(getdata);
            for (int x = 0; x < array1.length(); x++) {
                JSONObject str_value = array1.getJSONObject(x);
                Lv1.add(str_value.getString("name"));
                Lv1_ID.add(str_value.getString("id"));
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
            final ArrayAdapter<String> adapterLv1 =
                    new ArrayAdapter<String>(this, R.layout.myspinner_item, Lv1);
            ArrayAdapter<String> adapterLv2 =
                    new ArrayAdapter<String>(this, R.layout.myspinner_item, Lv2.get(1));
            adapterLv1.setDropDownViewResource(R.layout.myspinner_dropitem);
            adapterLv2.setDropDownViewResource(R.layout.myspinner_dropitem);
            groupLv1.setAdapter(adapterLv1);
            groupLv2.setAdapter(adapterLv2);

        } catch (JSONException err) {
            Log.d("Error", err.toString());
        }
        memberdata.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(member_modify.this, memberdatabase.class);
                        startActivity(intent);
                    }
                });
        dooracess.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(member_modify.this, DoorAccess.class);
                        startActivity(intent);
                    }
                });
        approvedlist.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(member_modify.this, ApporvedList.class);
                        startActivity(intent);
                    }
                });
        accescard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(member_modify.this, AccesCard.class);
                        startActivity(intent);
                    }
                });
        longtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(member_modify.this, DoorAccess_longtime.class);
                        startActivity(intent);
                    }
                });
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

        select_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        now = 1;
                        nowpage.setText("1");
                        getpage(now, groupid);
                    }
                });

        try {
            URL url = new URL("http://" + DomainIP + "/riway/api/v1/clients/main/list");
            JSONObject body = new JSONObject();
            body.put("page", "1");
            body.put("department", groupid);
            if (getselect.equals("")) {

            } else if (getselect.contains("name")) {
                JSONObject select = new JSONObject(getselect);
                body.put("name", select.getString("name"));
            } else {
                JSONObject select = new JSONObject(getselect);
                body.put("mobile", select.getString("mobile"));
            }

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
            final List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < array2.length(); i++) {
                JSONObject jsonObject = array2.getJSONObject(i);
                allinfo.add(jsonObject);
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("name", jsonObject.getString("name"));
                item.put("mobile", jsonObject.getString("mobile"));
                data.add(item);
                name.add(String.format("%-15s", jsonObject.getString("name")) + "123");
                id.add(jsonObject.getString("id"));
                phone.add(jsonObject.getString("phone"));
            }
            memberlist.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            SimpleAdapter adapter2 =
                    new SimpleAdapter(
                            this,
                            data,
                            R.layout.mymulitlist_item_onechioce,
                            new String[] {"name", "mobile"},
                            new int[] {R.id.name, R.id.text1});

            memberlist.setAdapter(adapter2);
            memberlist.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        public void onItemClick(
                                AdapterView<?> parent, View view, final int position, long id) {
                            for (int i = 0; i < data.size(); i++) {
                                View v = memberlist.getChildAt(i);
                                CheckedTextView ctv = (CheckedTextView) v.findViewById(R.id.text1);
                                ctv.setChecked(false);
                            }
                            View v = memberlist.getChildAt(position);
                            CheckedTextView ctv = (CheckedTextView) v.findViewById(R.id.text1);
                            ctv.setChecked(true);
                            Log.e("allinfo.get(i)", allinfo.get(position).toString());
                            modiftdialog.setVisibility(View.VISIBLE);
                            try {
                                final JSONArray departments;
                                departments = allinfo.get(position).getJSONArray("departments");
                                final String department_name =
                                        departments.getJSONObject(0).getString("name");
                                modifyname.setText(allinfo.get(position).getString("name"));
                                modifyphone.setText(allinfo.get(position).getString("mobile"));
                                Log.e(
                                        "1231456",
                                        Lv1_ID.indexOf(
                                                        departments
                                                                .getJSONObject(0)
                                                                .getString("parent"))
                                                + "");
                                ArrayAdapter<String> adapterLv2 =
                                        new ArrayAdapter<String>(
                                                member_modify.this,
                                                R.layout.myspinner_item,
                                                Lv2.get(
                                                        Lv1_ID.indexOf(
                                                                departments
                                                                        .getJSONObject(0)
                                                                        .getString("parent"))));
                                adapterLv2.setDropDownViewResource(R.layout.myspinner_dropitem);
                                groupLv2.setAdapter(adapterLv2);

                                groupLv2.setSelection(getIndex(groupLv2, department_name));

                                groupLv1.setSelection(
                                        Lv1_ID.indexOf(
                                                departments.getJSONObject(0).getString("parent")));
                                groupLv1.setOnItemSelectedListener(
                                        new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(
                                                    AdapterView<?> adapterView,
                                                    View view,
                                                    int position,
                                                    long l) {
                                                ArrayAdapter<String> adapterLv2 =
                                                        new ArrayAdapter<String>(
                                                                member_modify.this,
                                                                R.layout.myspinner_item,
                                                                Lv2.get(position));
                                                adapterLv2.setDropDownViewResource(
                                                        R.layout.myspinner_dropitem);
                                                groupLv2.setAdapter(adapterLv2);
                                                groupLv2.setSelection(
                                                        getIndex(groupLv2, department_name));
                                                Log.e("department_name", department_name);
                                            }

                                            @Override
                                            public void onNothingSelected(
                                                    AdapterView<?> adapterView) {}
                                        });
                                modify.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    JSONArray departmentsdata =
                                                            new JSONArray(getdata);

                                                    URL url =
                                                            new URL(
                                                                    "http://"
                                                                            + DomainIP
                                                                            + "/riway/api/v1/clients/main/one/"
                                                                            + allinfo.get(position)
                                                                                    .getString(
                                                                                            "id"));
                                                    HttpClient client = new DefaultHttpClient();
                                                    HttpPut put = new HttpPut(url.toURI());
                                                    JSONObject jo = new JSONObject();
                                                    String x;
                                                    x =
                                                            departmentsdata
                                                                    .getJSONObject(
                                                                            groupLv1
                                                                                    .getSelectedItemPosition())
                                                                    .getJSONArray("childs")
                                                                    .getJSONObject(
                                                                            groupLv2
                                                                                    .getSelectedItemPosition())
                                                                    .getString("id");
                                                    jo.put("name", modifyname.getText().toString());
                                                    jo.put(
                                                            "mobile",
                                                            modifyphone.getText().toString());
                                                    jo.put("phone", "");
                                                    jo.put("groups", new JSONArray("[" + x + "]"));
                                                    jo.put(
                                                            "password",
                                                            password.getText().toString());
                                                    AbstractHttpEntity entity =
                                                            new ByteArrayEntity(
                                                                    jo.toString().getBytes("UTF8"));
                                                    entity.setContentType(
                                                            new BasicHeader(
                                                                    HTTP.CONTENT_TYPE,
                                                                    "application/json"));
                                                    put.setEntity(entity);
                                                    HttpResponse response = client.execute(put);
                                                    String json_string =
                                                            EntityUtils.toString(
                                                                    response.getEntity());
                                                    JSONObject temp1 = new JSONObject(json_string);
                                                    JSONObject data = temp1.getJSONObject("data");
                                                    String error = data.getString("errors");
                                                    if (error.equals("")) {
                                                        Toast.makeText(
                                                                        member_modify.this,
                                                                        "修改成功",
                                                                        Toast.LENGTH_LONG)
                                                                .show();
                                                        Intent intent = new Intent();
                                                        intent.setClass(
                                                                member_modify.this,
                                                                memberdatabase.class);
                                                        startActivity(intent);

                                                    } else {
                                                        Toast.makeText(
                                                                        member_modify.this,
                                                                        error.toString(),
                                                                        Toast.LENGTH_LONG)
                                                                .show();
                                                    }

                                                    Log.e("jo", temp1.toString());

                                                } catch (Exception e) {
                                                    Toast.makeText(
                                                                    member_modify.this,
                                                                    e.toString(),
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                }
                                            }
                                        });

                            } catch (Exception e) {
                                Log.e("erroer315", e.toString());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e("error1", e.toString());
        }
    }
    // String body=前面選擇的類別+值 (nmae:xxx/mobile:xxxx/"")並同時在這頁的選單中去掉
    void getpage(int page, String department) {
        memberlist = (ListView) findViewById(R.id.memberlist);
        totalpage = (TextView) findViewById(R.id.totalpage);
        select_type = findViewById(R.id.select_type);
        select_name = findViewById(R.id.select_name);
        final LinearLayout modiftdialog = findViewById(R.id.modify_dialog);
        final EditText modifyname = findViewById(R.id.Editname);
        final TextView modifyphone = findViewById(R.id.Editphone);
        final Spinner groupLv1 = findViewById(R.id.groupLv1);
        final Spinner groupLv2 = findViewById(R.id.groupLv2);
        final Button modify = findViewById(R.id.modify);
        final EditText password = findViewById(R.id.Editpassword);
        Intent intent = this.getIntent();
        final String getdata = intent.getStringExtra("data");
        name.clear();
        phone.clear();
        id.clear();
        allinfo.clear();
        try {
            URL url = new URL("http://" + DomainIP + "/riway/api/v1/clients/main/list");
            JSONObject body = new JSONObject(intent.getStringExtra("select"));

            body.put("page", page);
            body.put("department", department);
            if (select_type.getSelectedItem().toString().equals("姓名")) {

                body.put("name", select_name.getText().toString());
            } else {
                body.put("mobile", select_name.getText().toString());
            }
            Log.e("body", body.toString());
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
            final List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < array2.length(); i++) {
                JSONObject jsonObject = array2.getJSONObject(i);
                allinfo.add(jsonObject);
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("name", jsonObject.getString("name"));
                item.put("mobile", jsonObject.getString("mobile"));
                data.add(item);
                name.add(String.format("%-15s", jsonObject.getString("name")) + "123");
                id.add(jsonObject.getString("id"));
                phone.add(jsonObject.getString("phone"));
            }
            memberlist.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            SimpleAdapter adapter2 =
                    new SimpleAdapter(
                            this,
                            data,
                            R.layout.mymulitlist_item_onechioce,
                            new String[] {"name", "mobile"},
                            new int[] {R.id.name, R.id.text1});

            memberlist.setAdapter(adapter2);
            memberlist.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        public void onItemClick(
                                AdapterView<?> parent, View view, final int position, long id) {
                            for (int i = 0; i < data.size(); i++) {
                                View v = memberlist.getChildAt(i);
                                CheckedTextView ctv = (CheckedTextView) v.findViewById(R.id.text1);
                                ctv.setChecked(false);
                            }
                            View v = memberlist.getChildAt(position);
                            CheckedTextView ctv = (CheckedTextView) v.findViewById(R.id.text1);
                            ctv.setChecked(true);
                            modiftdialog.setVisibility(View.VISIBLE);
                            try {
                                final JSONArray departments;
                                departments = allinfo.get(position).getJSONArray("departments");
                                final String department_name =
                                        departments.getJSONObject(0).getString("name");
                                modifyname.setText(allinfo.get(position).getString("name"));
                                modifyphone.setText(allinfo.get(position).getString("phone"));
                                Log.e(
                                        "1231456",
                                        Lv1_ID.indexOf(
                                                        departments
                                                                .getJSONObject(0)
                                                                .getString("parent"))
                                                + "");
                                ArrayAdapter<String> adapterLv2 =
                                        new ArrayAdapter<String>(
                                                member_modify.this,
                                                R.layout.myspinner_item,
                                                Lv2.get(
                                                        Lv1_ID.indexOf(
                                                                departments
                                                                        .getJSONObject(0)
                                                                        .getString("parent"))));
                                adapterLv2.setDropDownViewResource(R.layout.myspinner_dropitem);
                                groupLv2.setAdapter(adapterLv2);

                                groupLv2.setSelection(getIndex(groupLv2, department_name));

                                groupLv1.setSelection(
                                        Lv1_ID.indexOf(
                                                departments.getJSONObject(0).getString("parent")));
                                groupLv1.setOnItemSelectedListener(
                                        new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(
                                                    AdapterView<?> adapterView,
                                                    View view,
                                                    int position,
                                                    long l) {
                                                ArrayAdapter<String> adapterLv2 =
                                                        new ArrayAdapter<String>(
                                                                member_modify.this,
                                                                R.layout.myspinner_item,
                                                                Lv2.get(position));
                                                adapterLv2.setDropDownViewResource(
                                                        R.layout.myspinner_dropitem);
                                                groupLv2.setAdapter(adapterLv2);
                                                groupLv2.setSelection(
                                                        getIndex(groupLv2, department_name));
                                                Log.e("department_name", department_name);
                                            }

                                            @Override
                                            public void onNothingSelected(
                                                    AdapterView<?> adapterView) {}
                                        });
                                modify.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    JSONArray departmentsdata =
                                                            new JSONArray(getdata);

                                                    URL url =
                                                            new URL(
                                                                    "http://"
                                                                            + DomainIP
                                                                            + "/riway/api/v1/clients/main/one/"
                                                                            + allinfo.get(position)
                                                                                    .getString(
                                                                                            "id"));
                                                    HttpClient client = new DefaultHttpClient();
                                                    HttpPut put = new HttpPut(url.toURI());
                                                    JSONObject jo = new JSONObject();
                                                    String x;
                                                    x =
                                                            departmentsdata
                                                                    .getJSONObject(
                                                                            groupLv1
                                                                                    .getSelectedItemPosition())
                                                                    .getJSONArray("childs")
                                                                    .getJSONObject(
                                                                            groupLv2
                                                                                    .getSelectedItemPosition())
                                                                    .getString("id");
                                                    jo.put("name", modifyname.getText().toString());
                                                    jo.put(
                                                            "mobile",
                                                            modifyphone.getText().toString());
                                                    jo.put("phone", "");
                                                    jo.put("groups", new JSONArray("[" + x + "]"));
                                                    jo.put(
                                                            "password",
                                                            password.getText().toString());
                                                    AbstractHttpEntity entity =
                                                            new ByteArrayEntity(
                                                                    jo.toString().getBytes("UTF8"));
                                                    entity.setContentType(
                                                            new BasicHeader(
                                                                    HTTP.CONTENT_TYPE,
                                                                    "application/json"));
                                                    put.setEntity(entity);
                                                    HttpResponse response = client.execute(put);
                                                    String json_string =
                                                            EntityUtils.toString(
                                                                    response.getEntity());
                                                    JSONObject temp1 = new JSONObject(json_string);
                                                    JSONObject data = temp1.getJSONObject("data");
                                                    String error = data.getString("errors");
                                                    if (error.equals("")) {
                                                        Toast.makeText(
                                                                        member_modify.this,
                                                                        "修改成功",
                                                                        Toast.LENGTH_LONG)
                                                                .show();
                                                        Intent intent = new Intent();
                                                        intent.setClass(
                                                                member_modify.this,
                                                                memberdatabase.class);
                                                        startActivity(intent);

                                                    } else {
                                                        Toast.makeText(
                                                                        member_modify.this,
                                                                        error.toString(),
                                                                        Toast.LENGTH_LONG)
                                                                .show();
                                                    }

                                                    Log.e("jo", temp1.toString());

                                                } catch (Exception e) {
                                                    Toast.makeText(
                                                                    member_modify.this,
                                                                    e.toString(),
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                }
                                            }
                                        });

                            } catch (Exception e) {
                                Log.e("erroer315", e.toString());
                            }
                        }
                    });
            // 將JSON字串，放到JSONArray中。

        } catch (Exception e) {
            Toast.makeText(member_modify.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    /// 彈出式視窗 目前已沒使用
    void showdailog(final JSONObject info, final String getdata) {
        Log.e("info", info.toString());
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(member_modify.this);
        View view = getLayoutInflater().inflate(R.layout.modify_dailog, null);
        Button modify = view.findViewById(R.id.modify);
        final EditText name = view.findViewById(R.id.Editname);
        final TextView phone = view.findViewById(R.id.Editphone);
        final EditText password = view.findViewById(R.id.Editpassword);
        final Spinner groupLv1 = view.findViewById(R.id.groupLv1);
        final Spinner groupLv2 = view.findViewById(R.id.groupLv2);
        final ArrayAdapter<String> adapterLv1 =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Lv1);
        ArrayAdapter<String> adapterLv2 =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Lv2.get(1));
        groupLv1.setAdapter(adapterLv1);
        groupLv2.setAdapter(adapterLv2);
        try {
            final JSONArray departments;
            departments = info.getJSONArray("departments");
            final String department_name = departments.getJSONObject(0).getString("name");
            Log.e("departments", departments.getJSONObject(0).getString("name"));
            groupLv1.setSelection(departments.getJSONObject(0).getInt("parent") - 1);
            groupLv2.setSelection(getIndex(groupLv2, department_name));
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
                            groupLv2.setSelection(getIndex(groupLv2, department_name));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

        } catch (Exception e) {
            Log.e("erroer315", e.toString());
        }

        try {
            name.setText(info.getString("name"));
            phone.setText(info.getString("mobile"));
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
