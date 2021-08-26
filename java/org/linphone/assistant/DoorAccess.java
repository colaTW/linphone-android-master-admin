package org.linphone.assistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ListView;
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
    private ListView confirmgroup;
    final ArrayList member_id = new ArrayList<>();
    final ArrayList confirm_id = new ArrayList<>();
    final ArrayList member_name = new ArrayList<>();
    final ArrayList confirm_name = new ArrayList<>();
    final ArrayList confirm_group_name = new ArrayList<>();
    final ArrayList confirm_group_id = new ArrayList<>();

    private List<Map<String, String>> parentList = new ArrayList<Map<String, String>>();
    private List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
    private DoorAccess.MyAdapter adapter;
    private ExpandableListView grouplist;
    private HashSet<String> hashSet;
    String department = "";
    private TextView nowpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dooraccess);
        final ExpandableListView departmentlist = findViewById(R.id.departmentlist);
        final ListView memberlist = findViewById(R.id.memberlist);
        final ListView confirmlist = findViewById(R.id.confirmlist);
        final ListView confirmgroup = findViewById(R.id.confirmgroup);
        Button nextstep = findViewById(R.id.nextstep);
        final Button lastpage = findViewById(R.id.lastpage);
        Button nextpage = findViewById(R.id.nextpage);
        final TextView nowpage = findViewById(R.id.nowpage);
        TextView totalpage = findViewById(R.id.totalpage);
        final ArrayAdapter department_adapter = new ArrayAdapter(this, R.layout.mylist_item);
        final ArrayAdapter comfirm_adapter = new ArrayAdapter(this, R.layout.mylist_item);
        final ArrayAdapter comfirm_group_adapter = new ArrayAdapter(this, R.layout.mylist_item);
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

        // 增加標頭

        confirmlist.setAdapter(comfirm_adapter);
        confirmgroup.setAdapter(comfirm_group_adapter);
        memberlist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        String stringText;
                        // in normal case
                        stringText = ((TextView) view).getText().toString();
                        Log.e("confirm_id", confirm_id.toString());
                        Log.e("member_id.get(position)", member_id.get(position).toString());

                        if (confirm_id.contains(member_id.get(position))) {
                            Toast.makeText(DoorAccess.this, "已在選取名單內", Toast.LENGTH_SHORT).show();

                        } else {
                            int pos = member_name.indexOf(stringText);
                            confirm_name.add(member_name.get(position));
                            confirm_id.add(member_id.get(position));
                            Log.e("23", confirm_name.toString());
                            comfirm_adapter.clear();
                            comfirm_adapter.addAll(confirm_name);
                            confirmlist.setAdapter(comfirm_adapter);
                        }
                    }
                });
        confirmlist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        String stringText;
                        stringText = ((TextView) view).getText().toString();
                        int pos = confirm_name.indexOf(stringText);
                        confirm_name.remove(position);
                        confirm_id.remove(position);
                        comfirm_adapter.clear();
                        comfirm_adapter.addAll(confirm_name);
                        confirmlist.setAdapter(comfirm_adapter);
                    }
                });
        confirmgroup.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        String stringText;
                        stringText = ((TextView) view).getText().toString();
                        confirm_group_name.remove(position);
                        confirm_group_id.remove(position);
                        comfirm_group_adapter.clear();
                        comfirm_group_adapter.addAll(confirm_group_name);
                        confirmgroup.setAdapter(comfirm_group_adapter);
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
        nextstep.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (confirm_name.size() == 0 && confirm_group_name.size() == 0) {
                            Toast.makeText(DoorAccess.this, "請先選擇人員", Toast.LENGTH_SHORT).show();
                        } else {

                            JSONObject memberinfo = new JSONObject();
                            JSONArray cards = new JSONArray();
                            for (int i = 0; i < confirm_id.size(); i++) {
                                cards.put(confirm_id.get(i));
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

                            } catch (Exception e) {
                                Log.e("error", e.toString());
                            }
                            Log.e("memberinfo", memberinfo.toString());

                            Intent intent = new Intent();
                            intent.putExtra("memberinfo", memberinfo.toString());
                            intent.setClass(DoorAccess.this, DoorAccess_floor.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    void getpage(int page, String department) {
        memberlist = (ListView) findViewById(R.id.memberlist);
        totalpage = (TextView) findViewById(R.id.totalpage);
        member_name.clear();
        member_id.clear();
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
                member_name.add(jsonObject.getString("name"));
                member_id.add(jsonObject.getString("card_number"));
            }
            ArrayAdapter member_adapter = new ArrayAdapter(this, R.layout.mylist_item);
            member_adapter.addAll(member_name);
            memberlist.setAdapter(member_adapter);

            // 將JSON字串，放到JSONArray中。

        } catch (Exception e) {
            // Toast.makeText(memberdatabase.this, e.toString(), Toast.LENGTH_SHORT).show();
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

                            /* String str1 = name.substring(0, name.indexOf("="));
                            String str2 = name.substring(str1.length() + 1, name.length());
                            // 取得ID
                            String str3 = info.substring(0, info.indexOf(","));
                            String str4 = info.substring(str3.length() + 1, info.length());
                            String str5 = str4.substring(0, str4.indexOf("="));
                            String str6 = str4.substring(str5.length() + 1, str4.length());*/
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
            HttpGet httpGet = new HttpGet("http://18.181.171.107/riway/api/v1/clients/departments");
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

                JSONArray array2 = str_value.getJSONArray("childs");
                if (array2.length() > 0) {
                    ArrayList<Map<String, String>> test = new ArrayList<Map<String, String>>();

                    for (int y = 0; y < array2.length(); y++) {
                        JSONObject str_value2 = array2.getJSONObject(y);
                        Log.d("內容", str_value2 + "");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", str_value2.getString("name"));
                        map.put("ID", str_value2.getString("id"));
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
        confirmgroup = (ListView) findViewById(R.id.confirmgroup);
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
            DoorAccess.ViewHolder holder = null;
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
}
