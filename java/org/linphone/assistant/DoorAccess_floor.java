package org.linphone.assistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.linphone.R;
import org.linphone.activities.DialerActivity;

public class DoorAccess_floor extends Activity {
    private List<Map<String, String>> parentList = new ArrayList<Map<String, String>>();
    private List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
    private ExpandableListView exListView;
    private Context context = this;
    private MyAdapter adapter;
    private HashSet<String> hashSet;
    // 選定授權陣列(ex:{會議室,國際廳})
    private ArrayList<String> Authorization = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dooraccess_floor);
        final ListView memberlist = findViewById(R.id.memberlist);
        ImageButton goBA = findViewById(R.id.B_BA);
        ImageButton godoor = findViewById(R.id.B_door);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton goguard = findViewById(R.id.B_Guard);
        ImageButton memberdata = findViewById(R.id.memberdata);
        Button next = findViewById(R.id.submit);
        final ProgressBar pgSpinner = findViewById(R.id.progressBar_Spinner);
        pgSpinner.setVisibility(View.INVISIBLE);
        final ArrayList mData = new ArrayList<>();
        final ArrayList<String> stringList =
                (ArrayList<String>) getIntent().getStringArrayListExtra("ListString");
        ArrayAdapter adapter2 = new ArrayAdapter(this, R.layout.mylist_item);
        adapter2.addAll(stringList);
        memberlist.setAdapter(adapter2);
        goBA.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_floor.this, BApage.class);
                        startActivity(intent);
                    }
                });
        gocall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_floor.this, DialerActivity.class);
                        startActivity(intent);
                    }
                });
        goguard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_floor.this, Guardpage.class);
                        startActivity(intent);
                    }
                });
        next.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String member = "";
                        String floor = "";
                        for (int i = 0; i < stringList.size(); i++) {
                            member += stringList.get(i).toString() + ",";
                        }
                        for (int j = 0; j < Authorization.size(); j++) {
                            floor += Authorization.get(j).toString() + ",";
                        }
                        AlertDialog.Builder dialog = new AlertDialog.Builder(DoorAccess_floor.this);
                        dialog.setTitle("授權確認");
                        dialog.setMessage("將" + member + "\n加入到:" + Authorization + "的授權名單");
                        dialog.setNegativeButton(
                                "確認",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        Toast.makeText(
                                                        DoorAccess_floor.this,
                                                        "加入成功",
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                        Intent intent = new Intent();
                                        intent.setClass(DoorAccess_floor.this, DoorAccess.class);
                                        startActivity(intent);
                                    }
                                });
                        dialog.show();
                    }
                });
        pgSpinner.setVisibility(View.VISIBLE);

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
                                                pgSpinner.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        })
                .start();
    }

    /** 记录正在选中的子listview的item条目 用hashset是为了去除重复值 */
    private void setListener() {
        exListView.setOnGroupExpandListener(
                new ExpandableListView.OnGroupExpandListener() {

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        // 存取已选定的集合
                        hashSet = new HashSet<String>();
                    }
                });
        // ExpandableListView的Group的点击事件
        exListView.setOnGroupClickListener(
                new ExpandableListView.OnGroupClickListener() {

                    @Override
                    public boolean onGroupClick(
                            ExpandableListView parent, View v, int groupPosition, long id) {
                        // 可以写点击后实现的功能

                        return false;
                    }
                });
        // ExpandableListView的child的点击事件

        exListView.setOnChildClickListener(
                new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(
                            ExpandableListView parent,
                            View v,
                            int groupPosition,
                            int childPosition,
                            long id) {
                        Map<String, String> map = childData.get(groupPosition).get(childPosition);
                        String childChecked = map.get("isChecked");
                        if ("No".equals(childChecked)) {
                            map.put("isChecked", "Yes");
                            hashSet.add("选定" + childPosition);
                            String yes =
                                    (adapter.getChild(groupPosition, childPosition)).toString();
                            Log.e("看", yes);
                            yes = yes.substring(0, yes.indexOf(","));
                            String str1 = yes.substring(0, yes.indexOf("="));
                            yes = yes.substring(str1.length() + 1, yes.length());
                            Authorization.add(yes);

                        } else {
                            map.put("isChecked", "No");
                            if (hashSet.contains("选定" + childPosition)) {
                                hashSet.remove("选定" + childPosition);
                                String no =
                                        (adapter.getChild(groupPosition, childPosition)).toString();
                                no = no.substring(0, no.indexOf(","));
                                String str1 = no.substring(0, no.indexOf("="));
                                no = no.substring(str1.length() + 1, no.length());
                                int index = Authorization.indexOf(no);
                                Authorization.remove(index);
                            }
                        }
                        if (hashSet.size() == childData.get(groupPosition).size()) {
                            parentList.get(groupPosition).put("isGroupCheckd", "Yes");
                        } else {
                            parentList.get(groupPosition).put("isGroupCheckd", "No");
                        }
                        adapter.notifyDataSetChanged();
                        return false;
                    }
                });
    }

    // 初始化数据
    private void initData() {
        // 所有樓層+電梯陣列(1F,2F,3F,電梯名字)
        ArrayList<String> floors = new ArrayList<String>();
        // 樓層+電梯的內容陣列(康樂室,圖書室,電梯包含的樓層)
        ArrayList<ArrayList<Map<String, String>>> IDarray =
                new ArrayList<ArrayList<Map<String, String>>>();
        try {
            HttpGet httpGet = new HttpGet("http://54.95.142.9/riway/api/v1/access/floor/list");
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
                floors.add(str_value.getString("floor_name"));
                // 取得各樓層資料
                HttpGet httpGet2 =
                        new HttpGet(
                                "http://54.95.142.9/riway/api/v1/access/device/list/"
                                        + str_value.getString("id"));
                HttpClient httpClient2 = new DefaultHttpClient();
                httpClient2.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
                httpClient2.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
                HttpResponse response2 = httpClient2.execute(httpGet2);
                HttpEntity responseHttpEntity2 = response2.getEntity();
                String code2 = EntityUtils.toString(response2.getEntity());
                JSONObject temp2 = new JSONObject(code2);
                JSONArray array2 = temp2.getJSONArray("data");
                if (array2.length() > 0) {
                    ArrayList<Map<String, String>> test = new ArrayList<Map<String, String>>();

                    for (int y = 0; y < array2.length(); y++) {
                        JSONObject str_value2 = array2.getJSONObject(y);
                        Log.d("內容", str_value2 + "");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", str_value2.getString("device_name"));
                        map.put("ID", str_value2.getString("id"));
                        test.add(map);
                    }
                    IDarray.add(test);
                } else {
                    ArrayList<Map<String, String>> test = new ArrayList<Map<String, String>>();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name", "");
                    map.put("ID", "");
                    test.add(map);
                    IDarray.add(test);
                }
            }
            /// 取得電梯
            HttpGet httpGet3 = new HttpGet("http://54.95.142.9/riway/api/v1/elevator/main/list");
            HttpClient httpClient3 = new DefaultHttpClient();
            httpClient3.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
            httpClient3.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
            HttpResponse response3 = httpClient.execute(httpGet3);
            HttpEntity responseHttpEntity3 = response3.getEntity();
            String code3 = EntityUtils.toString(response3.getEntity());
            JSONObject temp3 = new JSONObject(code3);
            JSONArray array3 = temp3.getJSONArray("data");
            for (int x = 0; x < array3.length(); x++) {
                JSONObject str_value = array3.getJSONObject(x);
                floors.add(str_value.getString("elevator_name"));
                // 取得各樓層資料
                HttpGet httpGet4 =
                        new HttpGet(
                                "http://54.95.142.9/riway/api/v1/elevator/intervals/list/"
                                        + str_value.getString("id"));
                HttpClient httpClient4 = new DefaultHttpClient();
                httpClient4.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
                httpClient4.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
                HttpResponse response4 = httpClient4.execute(httpGet4);
                HttpEntity responseHttpEntity4 = response4.getEntity();
                String code4 = EntityUtils.toString(response4.getEntity());
                JSONObject temp4 = new JSONObject(code4);
                JSONArray array4 = temp4.getJSONArray("data");
                if (array4.length() > 0) {
                    ArrayList<Map<String, String>> test = new ArrayList<Map<String, String>>();

                    for (int y = 0; y < array4.length(); y++) {
                        JSONObject str_value2 = array4.getJSONObject(y);
                        Log.d("內容", str_value2 + "");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", str_value2.getString("name"));
                        map.put("ID", str_value2.getString("id"));
                        test.add(map);
                    }
                    IDarray.add(test);
                } else {
                    ArrayList<Map<String, String>> test = new ArrayList<Map<String, String>>();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name", "");
                    map.put("ID", "");
                    test.add(map);
                    IDarray.add(test);
                }
            }

            Log.d("樓層", floors + "");
            Log.d("樓LV2", IDarray + "");
            for (int i = 0; i < floors.size(); i++) {
                Map<String, String> groupMap = new HashMap<String, String>();
                groupMap.put("groupText", floors.get(i).toString());
                groupMap.put("isGroupCheckd", "No");
                parentList.add(groupMap);
            }
            for (int i = 0; i < floors.size(); i++) {
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                for (int j = 0; j < IDarray.get(i).size(); j++) {
                    if (IDarray.get(i).get(0).get("name") != "") {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("childItem", IDarray.get(i).get(j).get("name"));
                        map.put("isChecked", "No");
                        map.put("ID", "" + IDarray.get(i).get(j).get("ID"));
                        list.add(map);
                    }
                }
                childData.add(list);
            }
            adapter = new MyAdapter();
            exListView.setAdapter(adapter);
            exListView.expandGroup(0);
            hashSet = new HashSet<String>();

        } catch (Exception e) {
            Toast.makeText(DoorAccess_floor.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        exListView = (ExpandableListView) findViewById(R.id.exlistview);
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

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.listview_item, null);
                holder.childText = (TextView) convertView.findViewById(R.id.id_text);
                holder.childBox = (CheckBox) convertView.findViewById(R.id.id_checkbox);
                holder.TextID = (TextView) convertView.findViewById(R.id.text_ID);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.childText.setText(
                    childData.get(groupPosition).get(childPosition).get("childItem"));
            String isChecked = childData.get(groupPosition).get(childPosition).get("isChecked");
            holder.TextID.setText(childData.get(groupPosition).get(childPosition).get("ID"));

            if ("No".equals(isChecked)) {
                holder.childBox.setChecked(false);
            } else {
                holder.childBox.setChecked(true);
            }
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
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.group_item, null);
                holder.groupText = (TextView) convertView.findViewById(R.id.id_group_text);
                holder.groupBox = (CheckBox) convertView.findViewById(R.id.id_group_checkbox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
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
                                exListView.expandGroup(groupPosition);
                            } else {
                                // 关闭某个group view
                                exListView.collapseGroup(groupPosition);
                            }

                            if ("No".equals(isGroupCheckd)) {
                                exListView.expandGroup(groupPosition);
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
        CheckBox groupBox, childBox;
    }
}
