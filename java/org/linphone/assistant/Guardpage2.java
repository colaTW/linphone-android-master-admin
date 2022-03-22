package org.linphone.assistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.net.URL;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.linphone.R;
import org.linphone.activities.DialerActivity;

public class Guardpage2 extends Activity {
    private ArrayList<Group> gData = null;
    private ArrayList<ArrayList<ClipData.Item>> iData = null;
    private ArrayList<ClipData.Item> lData = null;
    private Context mContext;
    private ExpandableListView list;
    final ArrayList member_id = new ArrayList<>();
    final ArrayList confirm_id = new ArrayList<>();
    final ArrayList member_name = new ArrayList<>();
    final ArrayList confirm_name = new ArrayList<>();
    final ArrayList confirm_group_name = new ArrayList<>();
    final ArrayList confirm_group_id = new ArrayList<>();
    final ArrayList select_group_name = new ArrayList<>();
    final ArrayList select_group_id = new ArrayList<>();
    final ArrayList select_type_name = new ArrayList<>();
    private List<Map<String, String>> parentList = new ArrayList<Map<String, String>>();
    private List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
    private Guardpage2.MyAdapter adapter;
    private ExpandableListView grouplist;
    private HashSet<String> hashSet;
    int now = 1;
    int totalcount = 0;
    private ListView memberlist;
    private TextView totalpage;
    String department = "";
    private TextView nowpage;
    String is_push_all_notification = "0";
    String DomainIP = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guardpage2);
        ImageButton goBA = findViewById(R.id.B_BA);
        ImageButton godoor = findViewById(R.id.B_door);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton goguard = findViewById(R.id.B_Guard);
        ImageButton memberdata = findViewById(R.id.memberdata);
        ImageButton dooracess = findViewById(R.id.dooracess);
        ImageButton approvedlist = findViewById(R.id.approvedList);
        ImageButton accescard = findViewById(R.id.AccessCard);
        ImageButton IPsetting = findViewById(R.id.IPsetting);
        Button setting = findViewById(R.id.setting);
        final Button lastpage = findViewById(R.id.lastpage);
        Button nextpage = findViewById(R.id.nextpage);
        final TextView nowpage = findViewById(R.id.nowpage);
        final TextView totalpage = findViewById(R.id.totalpage);
        final ListView memberlist = findViewById(R.id.memberlist);
        Button select = findViewById(R.id.select);
        final Spinner select_group = findViewById(R.id.select_group);
        final Spinner select_type = findViewById(R.id.select_type);
        final EditText select_name = findViewById(R.id.select_name);
        SharedPreferences sPrefs = getSharedPreferences("Domain", MODE_PRIVATE);
        DomainIP = sPrefs.getString("IP", "");
        select_group_name.add("全部");
        select_group_id.add("0");
        select_type_name.add("電話");
        select_type_name.add("姓名");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        Guardpage2.this, R.layout.myspinner_item, select_type_name);
        adapter.setDropDownViewResource(R.layout.myspinner_dropitem);
        select_type.setAdapter(adapter);

        ArrayAdapter<String> adapterLv1 =
                new ArrayAdapter<String>(
                        Guardpage2.this, R.layout.myspinner_item, select_group_name);
        adapterLv1.setDropDownViewResource(R.layout.myspinner_dropitem);
        select_group.setAdapter(adapterLv1);

        select.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        member_id.clear();
                        member_name.clear();
                        now = 1;
                        nowpage.setText("1");
                        try {
                            URL url =
                                    new URL(
                                            "http://"
                                                    + DomainIP
                                                    + "/riway/api/v1/clients/main/list");
                            JSONObject body = new JSONObject();
                            body.put("page", 1);
                            if (select_type.getSelectedItem().toString().equals("姓名")) {
                                body.put("name", select_name.getText().toString());
                            } else {
                                body.put("mobile", select_name.getText().toString());
                            }
                            department =
                                    select_group_id
                                            .get(select_group.getSelectedItemPosition())
                                            .toString();
                            if (select_group_id.get(select_group.getSelectedItemPosition()) != "") {
                                body.put(
                                        "department",
                                        select_group_id
                                                .get(select_group.getSelectedItemPosition())
                                                .toString());
                                department =
                                        select_group_id
                                                .get(select_group.getSelectedItemPosition())
                                                .toString();
                            }
                            Log.e("department", body.toString());

                            HttpClient httpClient = new DefaultHttpClient();
                            AbstractHttpEntity entity =
                                    new ByteArrayEntity(body.toString().getBytes("UTF8"));
                            entity.setContentType(
                                    new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
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
                            Log.e("123", total + "");
                            JSONArray array2 = array1.getJSONArray("data");
                            for (int i = 0; i < array2.length(); i++) {
                                JSONObject jsonObject = array2.getJSONObject(i);
                                member_name.add(
                                        jsonObject.getString("name")
                                                + "　　　　　　　　"
                                                + jsonObject.getString("mobile"));
                                member_id.add(jsonObject.getString("card_number"));
                            }
                            ArrayAdapter member_adapter =
                                    new ArrayAdapter(Guardpage2.this, R.layout.mylist_item);
                            member_adapter.addAll(member_name);
                            memberlist.setAdapter(member_adapter);

                            // 將JSON字串，放到JSONArray中。

                        } catch (Exception e) {
                            Toast.makeText(Guardpage2.this, e.toString(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
        setting.setOnClickListener(
                new View.OnClickListener() {
                    long[] mHits = new long[3];

                    @Override
                    public void onClick(View view) {
                        showdailog();
                    }
                });
        memberlist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, final int position, long id) {
                        Log.e("member_id.get(position)", member_id.get(position).toString());
                        AlertDialog.Builder dialog;
                        dialog = new AlertDialog.Builder(Guardpage2.this);
                        dialog.setTitle("設定推播");
                        dialog.setNegativeButton(
                                "開啟",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        try {
                                            URL url =
                                                    new URL(
                                                            "http://"
                                                                    + DomainIP
                                                                    + "/riway/api/v1/clients/notiy/setting");
                                            JSONObject notificationinfo = new JSONObject();
                                            notificationinfo.put(
                                                    "card_number",
                                                    member_id.get(position).toString());
                                            notificationinfo.put("is_push_notification", "1");
                                            HttpClient httpClient = new DefaultHttpClient();
                                            AbstractHttpEntity entity =
                                                    new ByteArrayEntity(
                                                            notificationinfo
                                                                    .toString()
                                                                    .getBytes("UTF8"));
                                            entity.setContentType(
                                                    new BasicHeader(
                                                            HTTP.CONTENT_TYPE, "application/json"));
                                            HttpPost httpPost = new HttpPost(url.toURI());
                                            httpPost.setEntity(entity);
                                            // Prepare JSON to send by setting the entity
                                            HttpResponse response = httpClient.execute(httpPost);
                                            String code =
                                                    EntityUtils.toString(response.getEntity());
                                            JSONObject temp1 = new JSONObject(code);
                                            if (temp1.getString("errors").equals("")) {
                                                Toast.makeText(
                                                                Guardpage2.this,
                                                                "設定完成",
                                                                Toast.LENGTH_SHORT)
                                                        .show();
                                            } else {
                                                Toast.makeText(
                                                                Guardpage2.this,
                                                                temp1.getString("errors"),
                                                                Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                            Log.e("notificationinfo", notificationinfo.toString());

                                            getpage(now, department);
                                        } catch (Exception e) {
                                            Log.e("Exception", e.toString());
                                        }
                                    }
                                });
                        dialog.setPositiveButton(
                                "關閉",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        try {
                                            URL url =
                                                    new URL(
                                                            "http://"
                                                                    + DomainIP
                                                                    + "/riway/api/v1/clients/notiy/setting");
                                            JSONObject notificationinfo = new JSONObject();
                                            notificationinfo.put(
                                                    "card_number",
                                                    member_id.get(position).toString());
                                            notificationinfo.put("is_push_notification", "0");
                                            HttpClient httpClient = new DefaultHttpClient();
                                            AbstractHttpEntity entity =
                                                    new ByteArrayEntity(
                                                            notificationinfo
                                                                    .toString()
                                                                    .getBytes("UTF8"));
                                            entity.setContentType(
                                                    new BasicHeader(
                                                            HTTP.CONTENT_TYPE, "application/json"));
                                            HttpPost httpPost = new HttpPost(url.toURI());
                                            httpPost.setEntity(entity);
                                            // Prepare JSON to send by setting the entity
                                            HttpResponse response = httpClient.execute(httpPost);
                                            String code =
                                                    EntityUtils.toString(response.getEntity());
                                            JSONObject temp1 = new JSONObject(code);
                                            if (temp1.getString("errors").equals("")) {
                                                Toast.makeText(
                                                                Guardpage2.this,
                                                                "設定完成",
                                                                Toast.LENGTH_SHORT)
                                                        .show();
                                            } else {
                                                Toast.makeText(
                                                                Guardpage2.this,
                                                                temp1.getString("errors"),
                                                                Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                            Log.e("notificationinfo", notificationinfo.toString());
                                            getpage(now, department);
                                        } catch (Exception e) {
                                            Log.e("Exception", e.toString());
                                        }
                                    }
                                });
                        dialog.show();
                    }
                });
        lastpage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (now == 1) {
                        } else {
                            now--;
                            getpage(now, department);
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
                            getpage(now, department);
                            nowpage.setText(String.valueOf(now));
                        }
                    }
                });
        goBA.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(Guardpage2.this, BApage.class);
                        startActivity(intent);
                    }
                });

        gocall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(Guardpage2.this, DialerActivity.class);
                        startActivity(intent);
                    }
                });
        IPsetting.setOnClickListener(
                new View.OnClickListener() {
                    long[] mHits = new long[3];

                    @Override
                    public void onClick(View view) {
                        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                        if (mHits[0] >= (SystemClock.uptimeMillis() - 800)) {
                            IPsettingdialog();
                        }
                    }
                });
        godoor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(Guardpage2.this, memberdatabase.class);
                        startActivity(intent);
                    }
                });

        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                initView();
                                                initData();
                                                setListener();
                                            }
                                        });
                            }
                        })
                .start();
    }

    void getpage(int page, String department) {
        memberlist = (ListView) findViewById(R.id.memberlist);
        totalpage = (TextView) findViewById(R.id.totalpage);
        member_name.clear();
        member_id.clear();
        try {
            URL url = new URL("http://" + DomainIP + "/riway/api/v1/clients/main/list");
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
            List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
            Log.e("123", "231");

            for (int i = 0; i < array2.length(); i++) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                JSONObject jsonObject = array2.getJSONObject(i);
                item.put("name", jsonObject.getString("name"));
                item.put("mobile", jsonObject.getString("mobile"));

                if (jsonObject.getString("is_push_notification").equals("1")) {
                    item.put("status", "推播中");

                } else {
                    item.put("status", "未推播");
                }
                data.add(item);

                member_id.add(jsonObject.getString("card_number"));
            }
            SimpleAdapter adapter =
                    new SimpleAdapter(
                            this,
                            data,
                            R.layout.guard_listitem,
                            new String[] {"name", "mobile", "status"},
                            new int[] {R.id.name, R.id.mobile, R.id.status});

            // ArrayAdapter member_adapter = new ArrayAdapter(this, R.layout.mylist_item,new
            // String[] {"name", "status"},new int[] {R.id.name, R.id.status});
            // member_adapter.addAll(member_name);
            memberlist.setAdapter(adapter);

        } catch (Exception e) {
            Toast.makeText(Guardpage2.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setListener() {
        grouplist.setOnGroupExpandListener(
                new ExpandableListView.OnGroupExpandListener() {

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        // 存取已选定的集合
                        hashSet = new HashSet<String>();
                    }
                });
        // ExpandableListView的Group的点击事件
        grouplist.setOnGroupClickListener(
                new ExpandableListView.OnGroupClickListener() {

                    @Override
                    public boolean onGroupClick(
                            ExpandableListView parent, View v, int groupPosition, long id) {
                        // 可以写点击后实现的功能

                        return false;
                    }
                });

        // ExpandableListView的child的点击事件

        grouplist.setOnChildClickListener(
                new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(
                            ExpandableListView parent,
                            View v,
                            int groupPosition,
                            int childPosition,
                            long id) {
                        Map<String, String> map = childData.get(groupPosition).get(childPosition);
                        String no = childData.get(groupPosition).get(childPosition).get("ID");
                        department = no;
                        now = 1;
                        nowpage.setText("1");
                        getpage(now, department);
                        return false;
                    }
                });
        grouplist.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        return false;
                    }
                });
    }

    // 初始化数据
    private void initData() {
        // 所有group
        ArrayList<String> groups = new ArrayList<String>();
        // 第二層group
        ArrayList<ArrayList<Map<String, String>>> groups2 =
                new ArrayList<ArrayList<Map<String, String>>>();
        try {
            HttpGet httpGet =
                    new HttpGet("http://" + DomainIP + "/riway/api/v1/clients/departments");
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity responseHttpEntity = response.getEntity();
            String code = EntityUtils.toString(response.getEntity());
            JSONObject temp1 = new JSONObject(code);
            JSONArray array1 = temp1.getJSONArray("data");
            for (int x = 0; x < array1.length(); x++) {
                JSONObject str_value = array1.getJSONObject(x);
                groups.add(str_value.getString("name"));
                select_group_name.add(str_value.getString("name"));
                select_group_id.add(str_value.getString("id"));
                JSONArray array2 = str_value.getJSONArray("childs");
                if (array2.length() > 0) {
                    ArrayList<Map<String, String>> test = new ArrayList<Map<String, String>>();

                    for (int y = 0; y < array2.length(); y++) {
                        JSONObject str_value2 = array2.getJSONObject(y);
                        Log.d("內容", str_value2 + "");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", str_value2.getString("name"));
                        map.put("ID", str_value2.getString("id"));
                        select_group_name.add(str_value2.getString("name"));
                        select_group_id.add(str_value2.getString("id"));
                        test.add(map);
                    }
                    groups2.add(test);
                } else {
                    ArrayList<Map<String, String>> test = new ArrayList<Map<String, String>>();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name", "");
                    map.put("ID", "");
                    test.add(map);
                    groups2.add(test);
                }
            }

            Log.d("樓層", groups + "");
            Log.d("樓LV2", groups2 + "");
            for (int i = 0; i < groups.size(); i++) {
                Map<String, String> groupMap = new HashMap<String, String>();
                groupMap.put("groupText", groups.get(i).toString());
                parentList.add(groupMap);
            }
            for (int i = 0; i < groups.size(); i++) {
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                for (int j = 0; j < groups2.get(i).size(); j++) {
                    if (groups2.get(i).get(0).get("name") != "") {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("childItem", groups2.get(i).get(j).get("name"));
                        map.put("ID", "" + groups2.get(i).get(j).get("ID"));
                        list.add(map);
                    }
                }
                childData.add(list);
            }
            adapter = new Guardpage2.MyAdapter();
            grouplist.setAdapter(adapter);
            grouplist.expandGroup(0);
            hashSet = new HashSet<String>();

        } catch (Exception e) {
            Toast.makeText(Guardpage2.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        grouplist = (ExpandableListView) findViewById(R.id.grouplist);
        nowpage = findViewById(R.id.nowpage);
    }

    /** 适配adapter */
    private class MyAdapter extends BaseExpandableListAdapter {
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childData.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }

        @Override
        public View getChildView(
                int groupPosition,
                int childPosition,
                boolean isLastChild,
                View convertView,
                ViewGroup parent) {

            final Guardpage2.ViewHolder holder;
            if (convertView == null) {
                holder = new Guardpage2.ViewHolder();
                convertView = View.inflate(Guardpage2.this, R.layout.listview_item_noradio, null);
                holder.childText = (TextView) convertView.findViewById(R.id.id_text);
                holder.TextID = (TextView) convertView.findViewById(R.id.text_ID);
                convertView.setTag(holder);
            } else {
                holder = (Guardpage2.ViewHolder) convertView.getTag();
            }
            holder.childText.setText(
                    childData.get(groupPosition).get(childPosition).get("childItem"));
            String isChecked = childData.get(groupPosition).get(childPosition).get("isChecked");
            holder.TextID.setText(childData.get(groupPosition).get(childPosition).get("ID"));
            holder.childText.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            department = holder.TextID.getText().toString();
                            now = 1;
                            nowpage.setText("1");
                            getpage(now, department);
                        }
                    });
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return childData.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return parentList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return parentList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }

        @Override
        public View getGroupView(
                final int groupPosition,
                final boolean isExpanded,
                View convertView,
                ViewGroup parent) {
            Guardpage2.ViewHolder holder = null;
            if (convertView == null) {
                holder = new Guardpage2.ViewHolder();
                convertView = View.inflate(Guardpage2.this, R.layout.group_item, null);
                holder.groupText = (TextView) convertView.findViewById(R.id.id_group_text);
                holder.groupBox = (CheckBox) convertView.findViewById(R.id.id_group_checkbox);
                convertView.setTag(holder);
            } else {
                holder = (Guardpage2.ViewHolder) convertView.getTag();
            }
            holder.groupText.setText(parentList.get(groupPosition).get("groupText"));
            final String isGroupCheckd = parentList.get(groupPosition).get("isGroupCheckd");

            if ("No".equals(isGroupCheckd)) {
                holder.groupBox.setChecked(false);
            } else {
                holder.groupBox.setChecked(true);
            }

            /*
             * groupListView的点击事件
             */
            holder.groupBox.setOnClickListener(
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            CheckBox groupBox = (CheckBox) v.findViewById(R.id.id_group_checkbox);
                            if (!isExpanded) {
                                // 展开某个group view
                                grouplist.expandGroup(groupPosition);
                            } else {
                                // 关闭某个group view
                                grouplist.collapseGroup(groupPosition);
                            }

                            if ("No".equals(isGroupCheckd)) {
                                grouplist.expandGroup(groupPosition);
                                groupBox.setChecked(true);
                                parentList.get(groupPosition).put("isGroupCheckd", "Yes");
                                List<Map<String, String>> list = childData.get(groupPosition);
                                for (Map<String, String> map : list) {
                                    map.put("isChecked", "Yes");
                                }
                            } else {
                                groupBox.setChecked(false);
                                parentList.get(groupPosition).put("isGroupCheckd", "No");
                                List<Map<String, String>> list = childData.get(groupPosition);
                                for (Map<String, String> map : list) {
                                    map.put("isChecked", "No");
                                }
                            }
                            notifyDataSetChanged();
                        }
                    });

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private class ViewHolder {
        TextView groupText, childText, TextID;
        CheckBox groupBox;
    }

    void showdailog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Guardpage2.this);
        View view = getLayoutInflater().inflate(R.layout.notify_dailog, null);
        final TextView monday_starttime = view.findViewById(R.id.monday_starttime);
        final TextView monday_endtime = view.findViewById(R.id.monday_endtime);
        final TextView tuesday_starttime = view.findViewById(R.id.tuesday_starttime);
        final TextView tuesday_endtime = view.findViewById(R.id.tuesday_endtime);
        final TextView wednesday_starttime = view.findViewById(R.id.wednesday_starttime);
        final TextView wednesday_endtime = view.findViewById(R.id.wednesday_endtime);
        final TextView thursday_starttime = view.findViewById(R.id.thursday_starttime);
        final TextView thursday_endtime = view.findViewById(R.id.thursday_endtime);
        final TextView friday_starttime = view.findViewById(R.id.friday_starttime);
        final TextView friday_endtime = view.findViewById(R.id.friday_endtime);
        final TextView saturday_starttime = view.findViewById(R.id.saturday_starttime);
        final TextView saturday_endtime = view.findViewById(R.id.saturday_endtime);
        final TextView sunday_starttime = view.findViewById(R.id.sunday_starttime);
        final TextView sunday_endtime = view.findViewById(R.id.sunday_endtime);
        final Switch isnotify = view.findViewById(R.id.isnotify);
        final SimpleDateFormat df = new SimpleDateFormat("hh:mm");

        try {
            HttpGet httpGet =
                    new HttpGet("http://" + DomainIP + "/riway/api/v1/push/notification/setting");
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity responseHttpEntity = response.getEntity();
            String code = EntityUtils.toString(response.getEntity());
            JSONObject temp1 = new JSONObject(code);
            JSONObject array1 = temp1.getJSONObject("data");
            String day1 = array1.getString("push_notification_time_monday");
            String day2 = array1.getString("push_notification_time_tuesday");
            String day3 = array1.getString("push_notification_time_wednesday");
            String day4 = array1.getString("push_notification_time_thursday");
            String day5 = array1.getString("push_notification_time_friday");
            String day6 = array1.getString("push_notification_time_saturday");
            String day7 = array1.getString("push_notification_time_sunday");
            String[] split;
            split = day1.split("-");
            monday_starttime.setText(split[0]);
            monday_endtime.setText(split[1]);
            split = day2.split("-");
            tuesday_starttime.setText(split[0]);
            tuesday_endtime.setText(split[1]);
            split = day3.split("-");
            wednesday_starttime.setText(split[0]);
            wednesday_endtime.setText(split[1]);
            split = day4.split("-");
            thursday_starttime.setText(split[0]);
            thursday_endtime.setText(split[1]);
            split = day5.split("-");
            friday_starttime.setText(split[0]);
            friday_endtime.setText(split[1]);
            split = day6.split("-");
            saturday_starttime.setText(split[0]);
            saturday_endtime.setText(split[1]);
            split = day7.split("-");
            sunday_starttime.setText(split[0]);
            sunday_endtime.setText(split[1]);

            is_push_all_notification = array1.getString("is_push_all_notification");
            if (is_push_all_notification.equals("1")) {
                isnotify.setChecked(true);
            } else {
                isnotify.setChecked(false);
            }

        } catch (Exception e) {
            Log.e("erroes678", e.toString());
        }

        isnotify.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            is_push_all_notification = "1";
                        } else {
                            is_push_all_notification = "0";
                        }
                    }
                });

        monday_starttime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage2.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                monday_starttime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        monday_endtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage2.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                monday_endtime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        tuesday_starttime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage2.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                tuesday_starttime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        tuesday_endtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage2.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                tuesday_endtime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        wednesday_starttime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage2.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                wednesday_starttime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        wednesday_endtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage2.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                wednesday_endtime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        thursday_starttime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage2.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                thursday_starttime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        thursday_endtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage2.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                thursday_endtime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        friday_starttime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage2.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                friday_starttime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        friday_endtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage2.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                friday_endtime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        saturday_starttime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage2.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                saturday_starttime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        saturday_endtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage2.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                saturday_endtime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        sunday_starttime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage2.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                sunday_starttime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        sunday_endtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage2.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                sunday_endtime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        alertDialog.setTitle("推播開關以及推播時段設定");
        alertDialog.setNegativeButton(
                "確認設定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            Date start1 = df.parse(monday_starttime.getText().toString());
                            Date end1 = df.parse(monday_endtime.getText().toString());
                            Date start2 = df.parse(tuesday_starttime.getText().toString());
                            Date end2 = df.parse(tuesday_endtime.getText().toString());
                            Date start3 = df.parse(wednesday_starttime.getText().toString());
                            Date end3 = df.parse(wednesday_endtime.getText().toString());
                            Date start4 = df.parse(thursday_starttime.getText().toString());
                            Date end4 = df.parse(thursday_endtime.getText().toString());
                            Date start5 = df.parse(friday_starttime.getText().toString());
                            Date end5 = df.parse(friday_endtime.getText().toString());
                            Date start6 = df.parse(saturday_starttime.getText().toString());
                            Date end6 = df.parse(saturday_endtime.getText().toString());
                            Date start7 = df.parse(sunday_starttime.getText().toString());
                            Date end7 = df.parse(sunday_endtime.getText().toString());
                            if (start1.after(end1)) {
                                Toast.makeText(
                                                Guardpage2.this,
                                                "星期一的結束時間需大於起始時間",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else if (start2.after(end2)) {
                                Toast.makeText(
                                                Guardpage2.this,
                                                "星期二的結束時間需大於起始時間",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else if (start3.after(end3)) {
                                Toast.makeText(
                                                Guardpage2.this,
                                                "星期三的結束時間需大於起始時間",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else if (start4.after(end4)) {
                                Toast.makeText(
                                                Guardpage2.this,
                                                "星期四的結束時間需大於起始時間",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else if (start5.after(end5)) {
                                Toast.makeText(
                                                Guardpage2.this,
                                                "星期五的結束時間需大於起始時間",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else if (start6.after(end6)) {
                                Toast.makeText(
                                                Guardpage2.this,
                                                "星期六的結束時間需大於起始時間",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else if (start7.after(end7)) {
                                Toast.makeText(
                                                Guardpage2.this,
                                                "星期日的結束時間需大於起始時間",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else {
                                URL url =
                                        new URL(
                                                "http://"
                                                        + DomainIP
                                                        + "/riway/api/v1/push/notification/setting");
                                JSONObject notificationinfo = new JSONObject();
                                notificationinfo.put(
                                        "is_push_all_notification", is_push_all_notification);
                                notificationinfo.put(
                                        "push_notification_time_monday",
                                        monday_starttime.getText().toString()
                                                + "-"
                                                + monday_endtime.getText().toString());
                                notificationinfo.put(
                                        "push_notification_time_tuesday",
                                        tuesday_starttime.getText().toString()
                                                + "-"
                                                + tuesday_endtime.getText().toString());
                                notificationinfo.put(
                                        "push_notification_time_wednesday",
                                        wednesday_starttime.getText().toString()
                                                + "-"
                                                + wednesday_endtime.getText().toString());
                                notificationinfo.put(
                                        "push_notification_time_thursday",
                                        thursday_starttime.getText().toString()
                                                + "-"
                                                + thursday_endtime.getText().toString());
                                notificationinfo.put(
                                        "push_notification_time_friday",
                                        friday_starttime.getText().toString()
                                                + "-"
                                                + friday_endtime.getText().toString());
                                notificationinfo.put(
                                        "push_notification_time_saturday",
                                        saturday_starttime.getText().toString()
                                                + "-"
                                                + saturday_endtime.getText().toString());
                                notificationinfo.put(
                                        "push_notification_time_sunday",
                                        saturday_starttime.getText().toString()
                                                + "-"
                                                + saturday_endtime.getText().toString());
                                HttpClient httpClient = new DefaultHttpClient();
                                AbstractHttpEntity entity =
                                        new ByteArrayEntity(
                                                notificationinfo.toString().getBytes("UTF8"));
                                entity.setContentType(
                                        new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                                HttpPost httpPost = new HttpPost(url.toURI());
                                httpPost.setEntity(entity);
                                // Prepare JSON to send by setting the entity
                                HttpResponse response = httpClient.execute(httpPost);
                                String code = EntityUtils.toString(response.getEntity());
                                JSONObject temp1 = new JSONObject(code);
                                Log.e("notificationinfo", notificationinfo.toString());
                                if (temp1.getString("errors").equals("")) {
                                    Toast.makeText(Guardpage2.this, "設定完成", Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    Toast.makeText(
                                                    Guardpage2.this,
                                                    temp1.getString("errors"),
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }

                        } catch (Exception e) {
                            Log.e("Exception", e.toString());
                        }
                    }
                });
        alertDialog.setPositiveButton(
                "離開",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {}
                });
        alertDialog.setView(view);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    void IPsettingdialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Guardpage2.this);
        View view = getLayoutInflater().inflate(R.layout.printsetting, null);
        final EditText IP = view.findViewById(R.id.printerIP);
        SharedPreferences sPrefs = getSharedPreferences("printer", MODE_PRIVATE);
        final String printIP = sPrefs.getString("IP", "");
        IP.setText(printIP);
        alertDialog.setPositiveButton(
                "確認",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            SharedPreferences sPrefs =
                                    getSharedPreferences("printer", MODE_PRIVATE);
                            sPrefs.edit().putString("IP", IP.getText().toString()).commit();
                            /* SharedPreferences.Editor editor = sPrefs.edit(); // 获取Editor对象
                            editor.putString("IP", IP.getText().toString()); // 存储数据
                            editor.commit();*/
                        } catch (Exception e) {
                            Toast.makeText(Guardpage2.this, "請確認IP以及熱感機連線狀態", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });

        alertDialog.setTitle("IP設定");
        alertDialog.setView(view);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }
}
