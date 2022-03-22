package org.linphone.assistant;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URL;
import java.util.ArrayList;
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

public class DoorAccess extends Activity {
    int now = 1;
    int totalcount = 0;
    private ListView memberlist;
    private TextView totalpage;
    private TextView title;
    final ArrayList member_id = new ArrayList<>();
    final ArrayList member_name = new ArrayList<>();
    final ArrayList confirm_name = new ArrayList<>();
    final ArrayList confirm_group_name = new ArrayList<>();
    final ArrayList confirm_group_id = new ArrayList<>();
    final ArrayList select_group_name = new ArrayList<>();
    final ArrayList select_group_id = new ArrayList<>();
    final ArrayList select_type_name = new ArrayList<>();
    final ArrayList parentList_ID = new ArrayList<>();
    private List<Map<String, String>> parentList = new ArrayList<Map<String, String>>();
    private List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
    private DoorAccess.MyAdapter adapter;
    private ExpandableListView grouplist;
    private HashSet<String> hashSet;
    String department = "";
    private TextView nowpage;
    EditText select_name;
    private Spinner select_type;
    List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> confirm_id = new ArrayList<HashMap<String, Object>>();
    SimpleAdapter member_adapter;
    SimpleAdapter confrim_adapter;
    String DomainIP = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dooraccess);
        final ExpandableListView departmentlist = findViewById(R.id.departmentlist);
        final ListView memberlist = findViewById(R.id.memberlist);
        final ListView confirmlist = findViewById(R.id.confirmlist);
        Button nextstep = findViewById(R.id.nextstep);
        final Button lastpage = findViewById(R.id.lastpage);
        Button nextpage = findViewById(R.id.nextpage);
        Button selectall = findViewById(R.id.selectall);
        Button confrimbutton = findViewById(R.id.confirmlistbutton);
        Button clearall = findViewById(R.id.clearall);
        final TextView nowpage = findViewById(R.id.nowpage);
        final TextView totalpage = findViewById(R.id.totalpage);
        Button select = findViewById(R.id.select);
        final EditText select_name = findViewById(R.id.select_name);
        final TextView title = findViewById(R.id.title);
        final Spinner select_group = findViewById(R.id.select_group);
        final Spinner select_type = findViewById(R.id.select_type);
        select_group_name.add("全部");
        select_group_id.add("0");
        select_type_name.add("電話");
        select_type_name.add("姓名");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        DoorAccess.this, R.layout.myspinner_item, select_type_name);
        adapter.setDropDownViewResource(R.layout.myspinner_dropitem);
        select_type.setAdapter(adapter);
        SharedPreferences sPrefs = getSharedPreferences("Domain", MODE_PRIVATE);
        DomainIP = sPrefs.getString("IP", "");

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
                                                ArrayAdapter<String> adapterLv1 =
                                                        new ArrayAdapter<String>(
                                                                DoorAccess.this,
                                                                R.layout.myspinner_item,
                                                                select_group_name);
                                                adapterLv1.setDropDownViewResource(
                                                        R.layout.myspinner_dropitem);
                                                select_group.setAdapter(adapterLv1);
                                            }
                                        });
                            }
                        })
                .start();

        // 增加標頭

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
        select.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        member_id.clear();
                        member_name.clear();
                        data.clear();
                        now = 1;
                        nowpage.setText("1");
                        title.setText(
                                select_group_name
                                        .get(select_group.getSelectedItemPosition())
                                        .toString());
                        try {
                            URL url =
                                    new URL(
                                            "http://"
                                                    + DomainIP
                                                    + "/riway/api/v1/clients/simple/main/list");
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
                            JSONArray temp1 = new JSONArray(code);

                            for (int i = 0; i < temp1.length(); i++) {
                                HashMap<String, Object> item = new HashMap<String, Object>();
                                JSONObject jsonObject = temp1.getJSONObject(i);
                                item.put("name", jsonObject.getString("name"));
                                item.put("mobile", jsonObject.getString("mobile"));
                                item.put("isSelected", false);
                                item.put("card_number", jsonObject.getString("card_number"));
                                item.put("department", jsonObject.getString("department"));
                                member_name.add(jsonObject.getString("name"));
                                member_id.add(jsonObject.getString("card_number"));
                                data.add(item);
                            }

                            member_adapter =
                                    new SimpleAdapter(
                                            DoorAccess.this,
                                            data,
                                            R.layout.member_listitem,
                                            new String[] {
                                                "name", "mobile",
                                            },
                                            new int[] {
                                                R.id.name, R.id.mobile,
                                            }) {
                                        @Override
                                        public View getView(
                                                int position, View convertView, ViewGroup parent) {
                                            final View newView =
                                                    super.getView(position, convertView, parent);
                                            if (data.get(position)
                                                    .get("isSelected")
                                                    .toString()
                                                    .equals("false")) {
                                                newView.setBackgroundColor(0x202127);
                                            } else {
                                                newView.setBackgroundColor(0xFF00FFFF);
                                            }

                                            return newView;
                                        }
                                    };

                            memberlist.setAdapter(member_adapter);

                            // 將JSON字串，放到JSONArray中。

                        } catch (Exception e) {
                            Toast.makeText(DoorAccess.this, e.toString(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
        nextstep.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (confirm_name.size() == 0) {
                            Toast.makeText(DoorAccess.this, "請先選擇人員", Toast.LENGTH_SHORT).show();
                        } else {

                            JSONObject memberinfo = new JSONObject();
                            JSONArray cards = new JSONArray();
                            for (int i = 0; i < confirm_id.size(); i++) {
                                cards.put(confirm_id.get(i).get("card_number").toString());
                            }
                            JSONArray group_id = new JSONArray();
                            for (int i = 0; i < confirm_group_id.size(); i++) {
                                group_id.put(confirm_group_id.get(i));
                            }
                            try {
                                memberinfo.put("cards", cards);
                                memberinfo.put("group_id", group_id);
                                memberinfo.put("group_name", confirm_group_name);
                                memberinfo.put("member_name", confirm_name);
                                memberinfo.put("print", "0");
                                memberinfo.put("getaccess", "0");

                            } catch (Exception e) {
                                Log.e("error", e.toString());
                            }
                            Log.e("memberinfo", memberinfo.toString());

                            Intent intent = new Intent();
                            intent.putExtra("memberinfo", memberinfo.toString());
                            intent.setClass(DoorAccess.this, DoorAccess_floor_elevtor.class);
                            startActivity(intent);
                        }
                    }
                });
        memberlist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, final int position, long id) {
                        if (data.get(position).get("isSelected").toString().equals("false")) {
                            data.get(position).put("isSelected", true);
                        } else {
                            data.get(position).put("isSelected", false);
                        }
                        member_adapter.notifyDataSetChanged();
                    }
                });
        selectall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (data.size() > 0) {
                            for (int i = 0; i < data.size(); i++) {
                                data.get(i).put("isSelected", true);
                                member_adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
        // 全部清除按鈕
        clearall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        confirm_name.clear();
                        confirm_id.clear();
                        confirmlist.setAdapter(null);
                    }
                });
        // 確定執行按鈕
        confrimbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < data.size(); i++) {
                            if (data.get(i).get("isSelected").toString().equals("true")) {
                                confirm_name.add(data.get(i).get("name").toString());
                                confirm_id.add(data.get(i));
                            }
                        }
                        confrim_adapter =
                                new SimpleAdapter(
                                        DoorAccess.this,
                                        confirm_id,
                                        R.layout.member_group_listitem,
                                        new String[] {
                                            "name", "group",
                                        },
                                        new int[] {
                                            R.id.name, R.id.group,
                                        }) {
                                    @Override
                                    public View getView(
                                            final int position, View view, final ViewGroup parent) {
                                        ViewHolder holder;
                                        if (view == null) {
                                            view =
                                                    View.inflate(
                                                            DoorAccess.this,
                                                            R.layout.member_group_listitem,
                                                            null);
                                            holder = new DoorAccess.ViewHolder();
                                            holder.cacel = view.findViewById(R.id.cancelimg);
                                            holder.TextID = view.findViewById(R.id.name);
                                            holder.childText = view.findViewById(R.id.group);
                                            view.setTag(holder);
                                        } else {
                                            holder = (DoorAccess.ViewHolder) view.getTag();
                                        }
                                        holder.TextID.setText(
                                                confirm_id.get(position).get("name").toString());
                                        holder.childText.setText(
                                                confirm_id
                                                        .get(position)
                                                        .get("department")
                                                        .toString());
                                        holder.cacel.setOnClickListener(
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        confirm_name.remove(position);
                                                        confirm_id.remove(position);
                                                        confrim_adapter.notifyDataSetChanged();
                                                    }
                                                });

                                        return view;
                                    }
                                };

                        confirmlist.setAdapter(confrim_adapter);
                        memberlist.setAdapter(null);
                        data.clear();
                    }
                });
    }

    void getpage(int page, String department) {
        memberlist = (ListView) findViewById(R.id.memberlist);
        totalpage = (TextView) findViewById(R.id.totalpage);
        select_name = findViewById(R.id.select_name);
        select_type = findViewById(R.id.select_type);
        data.clear();
        member_name.clear();
        member_id.clear();
        try {
            URL url = new URL("http://" + DomainIP + "/riway/api/v1/clients/simple/main/list");
            JSONObject body = new JSONObject();
            body.put("page", 1);
            if (select_type.getSelectedItem().toString().equals("姓名")) {
                body.put("name", select_name.getText().toString());
            } else {
                body.put("mobile", select_name.getText().toString());
            }
            if (department != "") {
                body.put("department", department);
            }
            Log.e("department", body.toString());

            HttpClient httpClient = new DefaultHttpClient();
            AbstractHttpEntity entity = new ByteArrayEntity(body.toString().getBytes("UTF8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            HttpPost httpPost = new HttpPost(url.toURI());
            httpPost.setEntity(entity);
            // Prepare JSON to send by setting the entity
            HttpResponse response = httpClient.execute(httpPost);
            String code = EntityUtils.toString(response.getEntity());
            JSONArray temp1 = new JSONArray(code);

            for (int i = 0; i < temp1.length(); i++) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                JSONObject jsonObject = temp1.getJSONObject(i);
                item.put("name", jsonObject.getString("name"));
                item.put("mobile", jsonObject.getString("mobile"));
                item.put("isSelected", false);
                item.put("card_number", jsonObject.getString("card_number"));
                item.put("department", jsonObject.getString("department"));
                member_name.add(jsonObject.getString("name"));
                member_id.add(jsonObject.getString("card_number"));
                data.add(item);
            }

            member_adapter =
                    new SimpleAdapter(
                            DoorAccess.this,
                            data,
                            R.layout.member_listitem,
                            new String[] {
                                "name", "mobile",
                            },
                            new int[] {
                                R.id.name, R.id.mobile,
                            }) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            final View newView = super.getView(position, convertView, parent);
                            if (data.get(position).get("isSelected").toString().equals("false")) {
                                newView.setBackgroundColor(0x202127);
                            } else {
                                newView.setBackgroundColor(0xFF00FFFF);
                            }

                            return newView;
                        }
                    };

            memberlist.setAdapter(member_adapter);

            // 將JSON字串，放到JSONArray中。

        } catch (Exception e) {
            Toast.makeText(DoorAccess.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setListener() {
        /* grouplist.setOnGroupExpandListener(
        new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                // 存取已选定的集合
                hashSet = new HashSet<String>();
            }
        });*/
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
                        title.setText(
                                childData.get(groupPosition).get(childPosition).get("childItem"));
                        Map<String, String> map = childData.get(groupPosition).get(childPosition);
                        String no = childData.get(groupPosition).get(childPosition).get("ID");
                        department = no;
                        now = 1;
                        nowpage.setText("1");
                        select_name.setText("");
                        getpage(now, department);
                        return true;
                    }
                });
        /* grouplist.setOnItemLongClickListener(
        new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(
                    AdapterView<?> parent, View view, int position, long id) {
                int itemType = grouplist.getPackedPositionType(id);
                Log.e("itemType", itemType + "");

                if (itemType == grouplist.PACKED_POSITION_TYPE_CHILD) {
                    int groupPosition = grouplist.getPackedPositionGroup(id);
                    int childPosition = grouplist.getPackedPositionChild(id);
                    Log.e("1", grouplist.getPackedPositionChild(id) + "");
                    Log.e("2", grouplist.getPackedPositionGroup(id) + "");
                    // 取得exapnd的子項目資料(ex:[childitem="",id=""])
                    childData.get(groupPosition).get(childPosition).get("ID");
                    String info =
                            adapter.getChild(
                                            grouplist.getPackedPositionGroup(id),
                                            grouplist.getPackedPositionChild(id))
                                    .toString();
                    // 取得name
                    String name =
                            childData
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .get("childItem");


                    String ID = childData.get(groupPosition).get(childPosition).get("ID");
                    Log.e("test", ID);

                    if (confirm_group_id.contains(ID)) {
                        Toast.makeText(DoorAccess.this, "已在選取名單內", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        ArrayAdapter comfirm_group_adapter =
                                new ArrayAdapter(DoorAccess.this, R.layout.mylist_item);
                        comfirm_group_adapter.clear();
                        confirm_group_name.add(name);
                        confirm_group_id.add(ID);
                        comfirm_group_adapter.addAll(confirm_group_name);
                        confirmgroup.setAdapter(comfirm_group_adapter);
                    }

                    // do your per-item callback here
                    return true; // true if we consumed the click, false if not

                } else if (itemType == grouplist.PACKED_POSITION_TYPE_GROUP) {
                    int groupPosition = grouplist.getPackedPositionGroup(id);
                    // 取得exapnd的子項目資料(ex:[childitem="",id=""])
                    for (int i = 0; i < childData.get(groupPosition).size(); i++) {
                        String name = childData.get(groupPosition).get(i).get("childItem");
                        String ID = childData.get(groupPosition).get(i).get("ID");
                        Log.e("test", ID);

                        if (confirm_group_id.contains(ID)) {

                        } else {
                            ArrayAdapter comfirm_group_adapter =
                                    new ArrayAdapter(DoorAccess.this, R.layout.mylist_item);
                            comfirm_group_adapter.clear();
                            confirm_group_name.add(name);
                            confirm_group_id.add(ID);
                            comfirm_group_adapter.addAll(confirm_group_name);
                            confirmgroup.setAdapter(comfirm_group_adapter);
                        }
                    }

                    return true; // true if we consumed the click, false if not

                } else {
                    // null item; we don't consume the click
                    return false;
                }
            }
        });*/

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
            Log.d("select_group_name", select_group_name.toString());
            Log.d("select_group_id", select_group_id.toString());

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
            adapter = new DoorAccess.MyAdapter();
            grouplist.setAdapter(adapter);
            grouplist.expandGroup(0);
            hashSet = new HashSet<String>();

        } catch (Exception e) {
            Toast.makeText(DoorAccess.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        grouplist = (ExpandableListView) findViewById(R.id.departmentlist);
        nowpage = findViewById(R.id.nowpage);
        title = findViewById(R.id.title);
        select_name = findViewById(R.id.select_name);
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

            final DoorAccess.ViewHolder holder;
            if (convertView == null) {
                holder = new DoorAccess.ViewHolder();
                convertView = View.inflate(DoorAccess.this, R.layout.listview_item_noradio, null);
                holder.childText = (TextView) convertView.findViewById(R.id.id_text);
                holder.TextID = (TextView) convertView.findViewById(R.id.text_ID);
                convertView.setTag(holder);
            } else {
                holder = (DoorAccess.ViewHolder) convertView.getTag();
            }
            holder.childText.setText(
                    childData.get(groupPosition).get(childPosition).get("childItem"));
            String isChecked = childData.get(groupPosition).get(childPosition).get("isChecked");
            holder.TextID.setText(childData.get(groupPosition).get(childPosition).get("ID"));
            holder.childText.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            title.setText(holder.childText.getText());
                            department = holder.TextID.getText().toString();
                            now = 1;
                            nowpage.setText("1");
                            select_name.setText("");
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
            final DoorAccess.ViewHolder holder;
            if (convertView == null) {
                holder = new DoorAccess.ViewHolder();
                convertView = View.inflate(DoorAccess.this, R.layout.group_item, null);
                holder.groupText = (TextView) convertView.findViewById(R.id.id_group_text);
                holder.groupBox = (CheckBox) convertView.findViewById(R.id.id_group_checkbox);

                convertView.setTag(holder);
            } else {
                holder = (DoorAccess.ViewHolder) convertView.getTag();
            }

            holder.groupText.setText(parentList.get(groupPosition).get("groupText"));
            final String isGroupCheckd = parentList.get(groupPosition).get("isGroupCheckd");

            if ("No".equals(isGroupCheckd)) {
                holder.groupBox.setChecked(false);
            } else {
                holder.groupBox.setChecked(true);
            }
            holder.groupText.setOnLongClickListener(
                    new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            // TODO Auto-generated method stub
                            title.setText(holder.groupText.getText());
                            Log.e("123", select_group_id.toString());
                            select_group_name.indexOf(holder.groupText.getText());

                            department =
                                    select_group_id
                                            .get(
                                                    select_group_name.indexOf(
                                                            holder.groupText.getText().toString()))
                                            .toString();
                            now = 1;
                            nowpage.setText("1");
                            select_name.setText("");
                            getpage(now, department);
                            return true;
                        }
                    });

            /*
             * groupListView的点击事件
             */
            /* holder.groupBox.setOnClickListener(
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
            });*/

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
        ImageView cacel;
    }
}
