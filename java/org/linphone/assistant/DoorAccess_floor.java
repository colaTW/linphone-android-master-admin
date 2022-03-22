package org.linphone.assistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
    private ListView memberlist;
    private Context context = this;
    private MyAdapter adapter;
    private HashSet<String> hashSet;
    // 選定授權陣列(ex:{會議室,國際廳})
    private ArrayList<String> Authorization = new ArrayList<String>();
    JSONArray timelist;
    JSONArray TimeSection;
    ArrayList<JSONArray> devices = new ArrayList<>();
    final ArrayList confirm_name = new ArrayList<>();
    final ArrayList<String> member = new ArrayList<>();
    ArrayAdapter member_adapter;
    ArrayList<JSONObject> chiocejson = new ArrayList<>();
    ArrayList<JSONObject> confirmjson = new ArrayList<>();
    JSONArray accessinfo = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dooraccess_floor);
        ImageButton goBA = findViewById(R.id.B_BA);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton goguard = findViewById(R.id.B_Guard);
        final ListView comfirmlist = findViewById(R.id.confirmlist);
        final ListView memberlist = findViewById(R.id.memberlist);
        Button next = findViewById(R.id.submit);
        Button showlist = findViewById(R.id.showlist);
        final ProgressBar pgSpinner = findViewById(R.id.progressBar_Spinner);
        pgSpinner.setVisibility(View.INVISIBLE);
        // 上一步的確認資料
        // ("cards", confirm_id);卡號陣列
        // ("group_id", confirm_group_id);部門號陣列
        // ("group_name", confirm_group_name);部門名陣列
        // ("member_name", confirm_name);人員名陣列

        final ArrayAdapter comfirm_adapter = new ArrayAdapter(this, R.layout.mylist_item);
        member_adapter = new ArrayAdapter(DoorAccess_floor.this, R.layout.mylist_item);

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
                        intent.setClass(DoorAccess_floor.this, Guardpage2.class);
                        startActivity(intent);
                    }
                });
        showlist.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONObject object = new JSONObject();
                        try {
                            object =
                                    new JSONObject(getIntent().getExtras().getString("memberinfo"));
                            AlertDialog.Builder dialog =
                                    new AlertDialog.Builder(DoorAccess_floor.this);
                            dialog.setTitle("已選清單");
                            dialog.setMessage(
                                    "已選人員:"
                                            + object.get("member_name")
                                            + "\n"
                                            + "已選部門"
                                            + object.get("group_name"));
                            dialog.show();

                        } catch (Exception e) {
                        }
                    }
                });
        next.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("最後1", confirmjson.toString());
                        Log.e("最後2", confirm_name.toString());
                        if (confirmjson.size() == 0 && member.size() != 0) {
                            Toast.makeText(DoorAccess_floor.this, "請為門禁點選擇時間", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            floorsdate();
                        }

                        /*Intent intent = new Intent();
                        intent.setClass(DoorAccess_floor.this, DoorAccess_elevator.class);
                        startActivity(intent);
                        /*String member = "";
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
                                        intent.setClass(DoorAccess_floor.this, ApporvedList.class);
                                        startActivity(intent);
                                    }
                                });
                        dialog.show();*/
                    }
                });
        comfirmlist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        confirm_name.remove(position);
                        confirmjson.remove(position);
                        comfirm_adapter.clear();
                        comfirm_adapter.addAll(confirm_name);
                        comfirmlist.setAdapter(comfirm_adapter);
                    }
                });

        memberlist.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        String stringText;
                        member.remove(position);
                        chiocejson.remove(position);
                        member_adapter.clear();
                        member_adapter.addAll(member);
                        memberlist.setAdapter(member_adapter);
                        return true;
                    }
                });
        memberlist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        showdailog(member);
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
                        member_adapter =
                                new ArrayAdapter(DoorAccess_floor.this, R.layout.mylist_item);

                        Map<String, String> map = childData.get(groupPosition).get(childPosition);
                        Log.e(
                                "isSelected",
                                childData
                                        .get(groupPosition)
                                        .get(childPosition)
                                        .get("isSelected")
                                        .toString());
                        if (childData
                                .get(groupPosition)
                                .get(childPosition)
                                .get("isSelected")
                                .equals("false")) {
                            childData
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .put("isSelected", "true");
                        } else {
                            childData
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .put("isSelected", "false");
                        }
                        adapter.notifyDataSetChanged();
                        return false;

                        /* String get =
                                childData
                                        .get(groupPosition)
                                        .get(childPosition)
                                        .get("DeployDevice_Id");

                        Log.e("1", get);

                        if (isexist(get, chiocejson)) {
                            Toast.makeText(DoorAccess_floor.this, "已在選取名單內", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put(
                                        "name",
                                        childData
                                                .get(groupPosition)
                                                .get(childPosition)
                                                .get("childItem"));
                                jsonObject.put(
                                        "DeployDevice_Id",
                                        childData
                                                .get(groupPosition)
                                                .get(childPosition)
                                                .get("DeployDevice_Id"));
                                jsonObject.put(
                                        "Dep_DeployDevice_Id",
                                        childData
                                                .get(groupPosition)
                                                .get(childPosition)
                                                .get("Dep_DeployDevice_Id"));
                                jsonObject.put(
                                        "DeployDevice_No",
                                        childData
                                                .get(groupPosition)
                                                .get(childPosition)
                                                .get("DeployDevice_No"));

                            } catch (Exception e) {
                            }
                            chiocejson.add(jsonObject);
                            member.add(
                                    childData
                                            .get(groupPosition)
                                            .get(childPosition)
                                            .get("childItem"));
                            member_adapter.clear();
                            member_adapter.addAll(member);
                            memberlist.setAdapter(member_adapter);
                        }/*

                        /*  if (member.contains(get)) {
                            Toast.makeText(DoorAccess_floor.this, "已在選取名單內", Toast.LENGTH_SHORT)
                                    .show();

                        } else {
                            member.add(get);
                            member_adapter.clear();
                            member_adapter.addAll(member);
                            memberlist.setAdapter(member_adapter);
                        }*/

                        /*   try {
                            for (int i = 0; i < devices.size(); i++) {
                                JSONArray getobject = devices.get(i);
                                for (int j = 0; j < getobject.length(); j++) {
                                    if (getobject.getJSONObject(j).getString("name").equals(str2)) {
                                        showdailog(getobject.getJSONObject(j));
                                        break;
                                    }
                                    ;
                                }
                            }
                        } catch (Exception e) {
                                                }*/

                    }
                });
    }

    // 初始化数据
    private void initData() {
        // 所有樓層(1F,2F,3F,)
        ArrayList<String> floors = new ArrayList<String>();
        // 樓層的內容陣列(康樂室,圖書室)
        ArrayList<ArrayList<Map<String, String>>> IDarray =
                new ArrayList<ArrayList<Map<String, String>>>();
        try {
            HttpGet httpGet = new HttpGet("http://18.181.171.107/riway/api/v1/access/all/info");
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity responseHttpEntity = response.getEntity();
            String code = EntityUtils.toString(response.getEntity());
            JSONObject temp1 = new JSONObject(code);
            JSONArray array1 = temp1.getJSONArray("access2");
            JSONArray array2 = temp1.getJSONArray("elevator");
            accessinfo = temp1.getJSONArray("AccessInfo");
            timelist = temp1.getJSONArray("TimeZone");
            TimeSection = temp1.getJSONArray("TimeSection");
            // 取得樓層
            for (int i = 0; i < array1.length(); i++) {
                JSONObject str_value = array1.getJSONObject(i);
                floors.add(str_value.getString("floor_name"));
                JSONArray devicesarray = str_value.getJSONArray("devices");
                devices.add(devicesarray);
                Log.e("devicesarray" + i, devicesarray.toString());
                Log.e("長度", devicesarray.length() + "");
                ArrayList<Map<String, String>> test = new ArrayList<Map<String, String>>();
                if (devicesarray.length() == 0) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name", "");
                    map.put("DeployDevice_Id", "");
                    map.put("Dep_DeployDevice_Id", "");
                    map.put("DeployDevice_No", "");
                    test.add(map);
                    IDarray.add(test);
                } else {
                    for (int j = 0; j < devicesarray.length(); j++) {
                        JSONObject str_value2 = devicesarray.getJSONObject(j);
                        Log.d("內容", str_value2 + "");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", str_value2.getString("DeployDeviceName"));
                        map.put("DeployDevice_Id", str_value2.getString("DeployDevice_Id"));
                        map.put("Dep_DeployDevice_Id", str_value2.getString("Dep_DeployDevice_Id"));
                        map.put("DeployDevice_No", str_value2.getString("DeployDevice_No"));
                        test.add(map);
                    }
                    IDarray.add(test);
                }
            }

            // 取得電梯
            /* for (int i = 0; i < array2.length(); i++) {
                JSONObject str_value = array2.getJSONObject(i);
                floors.add(str_value.getString("name"));
                JSONArray devicesarray = str_value.getJSONArray("intervals");
                Log.e("devicesarray" + i, devicesarray.toString());
                Log.e("長度", devicesarray.length() + "");
                ArrayList<Map<String, String>> test = new ArrayList<Map<String, String>>();
                if (devicesarray.length() == 0) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name", "");
                    map.put("ID", "");
                    test.add(map);
                    IDarray.add(test);
                } else {
                    for (int j = 0; j < devicesarray.length(); j++) {
                        JSONObject str_value2 = devicesarray.getJSONObject(j);
                        Log.d("內容", str_value2 + "");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", str_value2.getString("name"));
                        map.put("ID", str_value2.getString("id"));
                        test.add(map);
                    }
                    IDarray.add(test);
                }
            }*/
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
                        map.put(
                                "DeployDevice_Id",
                                "" + IDarray.get(i).get(j).get("DeployDevice_Id"));
                        map.put(
                                "DeployDevice_No",
                                "" + IDarray.get(i).get(j).get("DeployDevice_No"));
                        map.put(
                                "Dep_DeployDevice_Id",
                                IDarray.get(i).get(j).get("Dep_DeployDevice_Id"));
                        map.put("isSelected", "false");

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
            Log.e("錯誤", e.toString());
        }
    }

    private void initView() {
        exListView = (ExpandableListView) findViewById(R.id.exlistview);
        memberlist = findViewById(R.id.memberlist);
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
                final int groupPosition,
                final int childPosition,
                boolean isLastChild,
                View convertView,
                ViewGroup parent) {

            final DoorAccess_floor.ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.listview_item_noradio, null);
                holder.childText = (TextView) convertView.findViewById(R.id.id_text);
                holder.TextID = (TextView) convertView.findViewById(R.id.text_ID);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.childText.setText(
                    childData.get(groupPosition).get(childPosition).get("childItem"));

            String isChecked = childData.get(groupPosition).get(childPosition).get("isChecked");
            if (childData.get(groupPosition).get(childPosition).get("isSelected").equals("false")) {
                convertView.setBackgroundColor(0x202127);
            } else {
                convertView.setBackgroundColor(0xFF00FFFF);
            }
            holder.TextID.setText(
                    childData.get(groupPosition).get(childPosition).get("DeployDevice_Id"));
            holder.childText.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            member_adapter =
                                    new ArrayAdapter(DoorAccess_floor.this, R.layout.mylist_item);
                            String get =
                                    childData
                                            .get(groupPosition)
                                            .get(childPosition)
                                            .get("DeployDevice_Id");
                            Log.e("123", get);
                            if (isexist(get, chiocejson)) {
                                Toast.makeText(DoorAccess_floor.this, "已在選取名單內", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put(
                                            "name",
                                            childData
                                                    .get(groupPosition)
                                                    .get(childPosition)
                                                    .get("childItem"));
                                    jsonObject.put(
                                            "DeployDevice_Id",
                                            childData
                                                    .get(groupPosition)
                                                    .get(childPosition)
                                                    .get("DeployDevice_Id"));
                                    jsonObject.put(
                                            "Dep_DeployDevice_Id",
                                            childData
                                                    .get(groupPosition)
                                                    .get(childPosition)
                                                    .get("Dep_DeployDevice_Id"));
                                    jsonObject.put(
                                            "DeployDevice_No",
                                            childData
                                                    .get(groupPosition)
                                                    .get(childPosition)
                                                    .get("DeployDevice_No"));

                                } catch (Exception e) {
                                }
                                chiocejson.add(jsonObject);
                                member.add(
                                        childData
                                                .get(groupPosition)
                                                .get(childPosition)
                                                .get("childItem"));
                                member_adapter.clear();
                                member_adapter.addAll(member);
                                memberlist.setAdapter(member_adapter);
                            }
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
            DoorAccess_floor.ViewHolder holder = null;
            if (convertView == null) {
                holder = new DoorAccess_floor.ViewHolder();

                convertView = View.inflate(context, R.layout.group_item, null);
                holder.groupText = (TextView) convertView.findViewById(R.id.id_group_text);
                holder.groupBox = (CheckBox) convertView.findViewById(R.id.id_group_checkbox);
                convertView.setTag(holder);
            } else {
                holder = (DoorAccess_floor.ViewHolder) convertView.getTag();
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
            /*  holder.groupBox.setOnClickListener(
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

    void showdailog(final ArrayList getarray) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(DoorAccess_floor.this);
        View view = getLayoutInflater().inflate(R.layout.timezone_dailog, null);
        alertDialog.setTitle("時間區表");
        alertDialog.setView(view);
        final AlertDialog dialog = alertDialog.create();
        final ListView comfirm = findViewById(R.id.confirmlist);
        final ListView time = view.findViewById(R.id.timelist);
        ArrayAdapter adapter2 = new ArrayAdapter(this, R.layout.mylist_item);
        final ArrayAdapter comfirm_adapter = new ArrayAdapter(this, R.layout.mylist_item);
        ArrayList name = new ArrayList<>();
        for (int i = 0; i < timelist.length(); i++) {
            try {
                name.add(timelist.getJSONObject(i).getString("TimeSecGpName"));
            } catch (Exception e) {
            }
        }
        adapter2.addAll(name);
        time.setAdapter(adapter2);
        time.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        String stringText;
                        // in normal case
                        stringText = ((TextView) view).getText().toString();
                        try {
                            Log.e(
                                    position + "序號",
                                    timelist.getJSONObject(position).getString("TimeSecGP_Id"));

                        } catch (Exception e) {
                            Log.e("err", e.toString());
                        }
                        try {
                            for (int i = 0; i < getarray.size(); i++) {
                                JSONObject go = chiocejson.get(i);
                                confirm_name.add(getarray.get(i) + "/" + stringText);
                                go.put(
                                        "TimeZone",
                                        timelist.getJSONObject(position).getString("TimeSecGP_Id"));
                                confirmjson.add(go);
                            }
                            comfirm_adapter.addAll(confirm_name);
                            comfirm.setAdapter(comfirm_adapter);
                            member.clear();
                            chiocejson.clear();
                            member_adapter.clear();
                            memberlist.setAdapter(member_adapter);

                        } catch (Exception e) {
                        }
                        dialog.dismiss();
                    }
                });
        time.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        try {

                            Toast.makeText(
                                            DoorAccess_floor.this,
                                            "星期一:"
                                                    + TimeSection.getJSONObject(
                                                                    Integer.parseInt(
                                                                            timelist.getJSONObject(
                                                                                            position)
                                                                                    .getString(
                                                                                            "TSG0")))
                                                            .getString("TimeSecStart1")
                                                    + "~"
                                                    + TimeSection.getJSONObject(
                                                                    Integer.parseInt(
                                                                            timelist.getJSONObject(
                                                                                            position)
                                                                                    .getString(
                                                                                            "TSG0")))
                                                            .getString("timeSecEnd1")
                                                    + "\n"
                                                    + "星期二:"
                                                    + TimeSection.getJSONObject(
                                                                    Integer.parseInt(
                                                                            timelist.getJSONObject(
                                                                                            position)
                                                                                    .getString(
                                                                                            "TSG1")))
                                                            .getString("TimeSecStart1")
                                                    + "~"
                                                    + TimeSection.getJSONObject(
                                                                    Integer.parseInt(
                                                                            timelist.getJSONObject(
                                                                                            position)
                                                                                    .getString(
                                                                                            "TSG1")))
                                                            .getString("timeSecEnd1")
                                                    + "\n"
                                                    + "星期三:"
                                                    + TimeSection.getJSONObject(
                                                                    Integer.parseInt(
                                                                            timelist.getJSONObject(
                                                                                            position)
                                                                                    .getString(
                                                                                            "TSG2")))
                                                            .getString("TimeSecStart1")
                                                    + "~"
                                                    + TimeSection.getJSONObject(
                                                                    Integer.parseInt(
                                                                            timelist.getJSONObject(
                                                                                            position)
                                                                                    .getString(
                                                                                            "TSG2")))
                                                            .getString("timeSecEnd1")
                                                    + "\n"
                                                    + "星期四:"
                                                    + TimeSection.getJSONObject(
                                                                    Integer.parseInt(
                                                                            timelist.getJSONObject(
                                                                                            position)
                                                                                    .getString(
                                                                                            "TSG3")))
                                                            .getString("TimeSecStart1")
                                                    + "~"
                                                    + TimeSection.getJSONObject(
                                                                    Integer.parseInt(
                                                                            timelist.getJSONObject(
                                                                                            position)
                                                                                    .getString(
                                                                                            "TSG3")))
                                                            .getString("timeSecEnd1")
                                                    + "\n"
                                                    + "星期五:"
                                                    + TimeSection.getJSONObject(
                                                                    Integer.parseInt(
                                                                            timelist.getJSONObject(
                                                                                            position)
                                                                                    .getString(
                                                                                            "TSG4")))
                                                            .getString("TimeSecStart1")
                                                    + "~"
                                                    + TimeSection.getJSONObject(
                                                                    Integer.parseInt(
                                                                            timelist.getJSONObject(
                                                                                            position)
                                                                                    .getString(
                                                                                            "TSG4")))
                                                            .getString("timeSecEnd1")
                                                    + "\n"
                                                    + "星期六:"
                                                    + TimeSection.getJSONObject(
                                                                    Integer.parseInt(
                                                                            timelist.getJSONObject(
                                                                                            position)
                                                                                    .getString(
                                                                                            "TSG5")))
                                                            .getString("TimeSecStart1")
                                                    + "~"
                                                    + TimeSection.getJSONObject(
                                                                    Integer.parseInt(
                                                                            timelist.getJSONObject(
                                                                                            position)
                                                                                    .getString(
                                                                                            "TSG5")))
                                                            .getString("timeSecEnd1")
                                                    + "\n"
                                                    + "星期日:"
                                                    + TimeSection.getJSONObject(
                                                                    Integer.parseInt(
                                                                            timelist.getJSONObject(
                                                                                            position)
                                                                                    .getString(
                                                                                            "TSG6")))
                                                            .getString("TimeSecStart1")
                                                    + "~"
                                                    + TimeSection.getJSONObject(
                                                                    Integer.parseInt(
                                                                            timelist.getJSONObject(
                                                                                            position)
                                                                                    .getString(
                                                                                            "TSG6")))
                                                            .getString("timeSecEnd1")
                                                    + "\n",
                                            Toast.LENGTH_SHORT)
                                    .show();
                            return true;
                        } catch (Exception e) {
                        }
                        return true;
                    }
                });

        dialog.show();
    }

    private class ViewHolder {
        TextView groupText, childText, TextID;
        CheckBox groupBox, childBox;
    }

    private boolean isexist(String ID, ArrayList<JSONObject> object) {
        try {
            for (int i = 0; i < chiocejson.size(); i++) {
                if (chiocejson.get(i).getString("DeployDevice_Id").equals(ID)) {
                    return true;
                }
            }
            for (int i = 0; i < confirmjson.size(); i++) {
                if (confirmjson.get(i).getString("DeployDevice_Id").equals(ID)) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private void floorsdate() {
        ArrayList<JSONObject> floorsdate = new ArrayList<>();
        ArrayList date_id = new ArrayList();
        for (int i = 0; i < confirmjson.size(); i++) {
            try {
                String x =
                        date_id.indexOf(confirmjson.get(i).getString("Dep_DeployDevice_Id")) + "";
                Log.e("index", x);
                Log.e("date", date_id.toString());
                if (date_id.indexOf(confirmjson.get(i).getString("Dep_DeployDevice_Id")) == -1) {
                    date_id.add((confirmjson.get(i).getString("Dep_DeployDevice_Id")));
                    JSONObject object1 = new JSONObject();
                    object1.put("device_id", confirmjson.get(i).getString("Dep_DeployDevice_Id"));
                    ArrayList<String> list = new ArrayList<String>();
                    list.add("00");
                    list.add("00");
                    list.add("00");
                    list.add("00");
                    list.add("00");
                    list.add("00");
                    list.add("00");
                    list.add("00");
                    list.set(
                            Integer.parseInt(confirmjson.get(i).getString("DeployDevice_No")),
                            confirmjson.get(i).getString("TimeZone"));
                    object1.put("doors", new JSONArray(list));

                    floorsdate.add(object1);
                } else {
                    JSONArray test =
                            floorsdate
                                    .get(
                                            date_id.indexOf(
                                                    confirmjson
                                                            .get(i)
                                                            .getString("Dep_DeployDevice_Id")))
                                    .getJSONArray("doors");
                    test.put(
                            Integer.parseInt(confirmjson.get(i).getString("DeployDevice_No")),
                            confirmjson.get(i).getString("TimeZone"));

                    Log.e("R", test.toString());
                }

            } catch (Exception e) {
                Log.e("erreos", e.toString());
            }
        }
        Log.e("floorsdate", floorsdate.toString());

        try {
            String cominginfo = getIntent().getExtras().getString("memberinfo");
            JSONObject allinfo = new JSONObject(cominginfo);
            JSONArray doors_access = new JSONArray();
            for (int i = 0; i < floorsdate.size(); i++) {
                doors_access.put(floorsdate.get(i));
            }
            JSONArray doors_selectinfo = new JSONArray();
            for (int i = 0; i < confirmjson.size(); i++) {
                doors_selectinfo.put(confirmjson.get(i));
            }
            for (int i = 0; i < doors_access.length(); i++) {
                for (int j = 0; j < accessinfo.length(); j++) {
                    JSONObject object = new JSONObject();
                    object = accessinfo.getJSONObject(j);
                    Log.e("DeployDevice_Id", object.getString("DeployDevice_Id"));
                    Log.e("device_id", doors_access.getJSONObject(i).getString("device_id"));
                    if (object.getString("DeployDevice_Id")
                            .equals(doors_access.getJSONObject(i).getString("device_id"))) {
                        doors_access
                                .getJSONObject(i)
                                .put("DeployDevice_IP", object.getString("DeployDevice_IP"));
                        doors_access.getJSONObject(i).put("IP_Port", object.getString("IP_Port"));
                        doors_access
                                .getJSONObject(i)
                                .put("DeployDevice_No", object.getString("DeployDevice_No"));
                    }
                }
            }
            for (int i = 0; i < doors_selectinfo.length(); i++) {
                for (int j = 0; j < accessinfo.length(); j++) {
                    JSONObject object = new JSONObject();
                    object = accessinfo.getJSONObject(j);
                    Log.e("DeployDevice_Id", object.getString("DeployDevice_Id"));
                    Log.e(
                            "device_id",
                            doors_selectinfo.getJSONObject(i).getString("Dep_DeployDevice_Id"));
                    if (object.getString("DeployDevice_Id")
                            .equals(
                                    doors_selectinfo
                                            .getJSONObject(i)
                                            .getString("Dep_DeployDevice_Id"))) {
                        doors_selectinfo
                                .getJSONObject(i)
                                .put("DeployDevice_IP", object.getString("DeployDevice_IP"));
                        doors_selectinfo
                                .getJSONObject(i)
                                .put("IP_Port", object.getString("IP_Port"));
                        doors_selectinfo
                                .getJSONObject(i)
                                .put("DeployDevice_No", object.getString("DeployDevice_No"));
                    }
                }
            }
            allinfo.put("doors_access", doors_access);
            allinfo.put("doors_selectinfo", doors_selectinfo);
            allinfo.put("doors_info", confirm_name);
            Log.e("allinfo", allinfo.toString());
            Intent intent = new Intent();
            intent.putExtra("allinfo", allinfo.toString());
            intent.setClass(DoorAccess_floor.this, DoorAccess_elevator.class);
            startActivity(intent);

        } catch (Exception e) {
            Log.e("errors", e.toString());
        }
    }
}
