package org.linphone.assistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.dantsu.escposprinter.EscPosCharsetEncoding;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.linphone.R;
import org.linphone.activities.DialerActivity;

public class DoorAccess_elevator extends Activity {
    private List<Map<String, String>> parentList = new ArrayList<Map<String, String>>();
    private List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
    private ExpandableListView exListView;
    private ListView elevatorlist;
    private Context context = this;
    private MyAdapter adapter;
    private HashSet<String> hashSet;
    JSONArray timelist;
    JSONArray TimeSection;
    ArrayList<JSONArray> devices = new ArrayList<>();
    ArrayList<JSONObject> elevatorjson = new ArrayList<>();
    ArrayList<JSONObject> confirmjson = new ArrayList<>();
    final ArrayList confirm_name = new ArrayList<>();
    final ArrayList select_id = new ArrayList<>();
    ArrayList<JSONObject> select_json = new ArrayList<>();
    ArrayList<String> comfirm_ele = new ArrayList<String>();
    ArrayList<JSONObject> comfirm_elejson = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dooraccess_elevator);
        ListView elevatorlist = findViewById(R.id.elevatorlist);
        ImageButton goBA = findViewById(R.id.B_BA);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton goguard = findViewById(R.id.B_Guard);
        final ListView comfirmlist = findViewById(R.id.confirmlist);
        Button next = findViewById(R.id.submit);
        Button showlist = findViewById(R.id.showlist);

        final ArrayList mData = new ArrayList<>();
        final ArrayAdapter comfirm_adapter = new ArrayAdapter(this, R.layout.mylist_item);

        goBA.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_elevator.this, BApage.class);
                        startActivity(intent);
                    }
                });
        gocall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_elevator.this, DialerActivity.class);
                        startActivity(intent);
                    }
                });
        goguard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_elevator.this, Guardpage2.class);
                        startActivity(intent);
                    }
                });
        next.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("最後1", select_id.toString());
                        Log.e("最後2", select_json.toString());
                        Log.e("最後3", comfirm_elejson.toString());

                        elevatordate();
                    }
                });
        comfirmlist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        String stringText;
                        stringText = ((TextView) view).getText().toString();
                        confirm_name.remove(position);
                        confirmjson.remove(position);
                        comfirm_adapter.clear();
                        comfirm_adapter.addAll(confirm_name);
                        comfirmlist.setAdapter(comfirm_adapter);
                    }
                });
        elevatorlist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        String stringText;
                        stringText = ((TextView) view).getText().toString();
                        Log.e("stringText", stringText);
                        try {
                            String get = comfirm_elejson.get(position).getString("DeployDevice_Id");
                            if (isexist(get, confirmjson)) {
                                Toast.makeText(
                                                DoorAccess_elevator.this,
                                                "已在選取名單內",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else {
                                showdailog(comfirm_elejson.get(position));
                            }
                        } catch (Exception e) {
                        }
                    }
                });
        showlist.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONObject object = new JSONObject();
                        try {
                            object = new JSONObject(getIntent().getExtras().getString("allinfo"));
                            AlertDialog.Builder dialog =
                                    new AlertDialog.Builder(DoorAccess_elevator.this);
                            dialog.setTitle("已選清單");
                            dialog.setMessage(
                                    "已選人員:"
                                            + object.get("member_name")
                                            + "\n"
                                            + "已選部門"
                                            + object.get("group_name")
                                            + "\n"
                                            + "已選樓層"
                                            + object.get("doors_info"));
                            dialog.show();

                        } catch (Exception e) {
                        }
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
                        Log.e("childData", parentList.get(groupPosition).toString());
                        // 勾選時
                        if ("No".equals(childChecked)) {
                            if (comfirm_ele.indexOf(parentList.get(groupPosition).get("groupText"))
                                    == -1) {
                                comfirm_ele.add(parentList.get(groupPosition).get("groupText"));
                                ArrayAdapter adapter2 =
                                        new ArrayAdapter(
                                                DoorAccess_elevator.this, R.layout.mylist_item);
                                adapter2.addAll(comfirm_ele);
                                elevatorlist.setAdapter(adapter2);
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put(
                                            "DeployDevice_Id",
                                            parentList.get(groupPosition).get("DeployDevice_Id"));
                                    object.put(
                                            "DeployDeviceName",
                                            parentList.get(groupPosition).get("DeployDeviceName"));
                                    object.put(
                                            "DeployDevice_IP",
                                            parentList.get(groupPosition).get("DeployDevice_IP"));
                                    object.put(
                                            "IP_Port",
                                            parentList.get(groupPosition).get("IP_Port"));
                                    object.put(
                                            "DeployDevice_No",
                                            parentList.get(groupPosition).get("DeployDevice_No"));

                                    comfirm_elejson.add(object);
                                } catch (Exception e) {
                                }
                            }
                            map.put("isChecked", "Yes");
                            hashSet.add("选定" + childPosition);
                            select_id.add(
                                    childData
                                            .get(groupPosition)
                                            .get(childPosition)
                                            .get("DeployDevice_Id"));
                            try {
                                JSONObject object1 = new JSONObject();
                                object1.put(
                                        "DeployDeviceName",
                                        childData
                                                .get(groupPosition)
                                                .get(childPosition)
                                                .get("childItem"));
                                object1.put(
                                        "DeployDevice_Id",
                                        childData
                                                .get(groupPosition)
                                                .get(childPosition)
                                                .get("DeployDevice_Id"));
                                object1.put(
                                        "Dep_DeployDevice_Id",
                                        childData
                                                .get(groupPosition)
                                                .get(childPosition)
                                                .get("Dep_DeployDevice_Id"));
                                object1.put(
                                        "DeployDevice_No",
                                        childData
                                                .get(groupPosition)
                                                .get(childPosition)
                                                .get("DeployDevice_No"));
                                select_json.add(object1);
                            } catch (Exception e) {
                            }
                        }
                        // 取消勾選時
                        else {
                            map.put("isChecked", "No");
                            int x = 0;
                            for (int i = 0; i < childData.get(groupPosition).size(); i++) {
                                if (childData.get(groupPosition).get(i).get("isChecked") == "Yes") {
                                    x++;
                                }
                            }
                            if (x == 0) {
                                int y = 0;
                                y =
                                        comfirm_ele.indexOf(
                                                parentList.get(groupPosition).get("groupText"));
                                comfirm_ele.remove(y);
                                comfirm_elejson.remove(y);
                                ArrayAdapter adapter2 =
                                        new ArrayAdapter(
                                                DoorAccess_elevator.this, R.layout.mylist_item);
                                adapter2.addAll(comfirm_ele);
                                elevatorlist.setAdapter(adapter2);
                            }
                            int index =
                                    select_id.indexOf(
                                            childData
                                                    .get(groupPosition)
                                                    .get(childPosition)
                                                    .get("DeployDevice_Id"));

                            select_id.remove(index);
                            select_json.remove(index);
                            if (hashSet.contains("选定" + childPosition)) {
                                hashSet.remove("选定" + childPosition);
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
        // 電梯陣列(電梯名字)
        ArrayList<String> floors = new ArrayList<String>();
        // 電梯的內容陣列(電梯包含的樓層)
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
            JSONArray array1 = temp1.getJSONArray("access");
            JSONArray array2 = temp1.getJSONArray("elevator2");
            timelist = temp1.getJSONArray("TimeZone");
            TimeSection = temp1.getJSONArray("TimeSection");
            // 取得樓層
            for (int i = 0; i < array2.length(); i++) {
                JSONObject str_value = array2.getJSONObject(i);
                elevatorjson.add(str_value);
                floors.add(str_value.getString("DeployDeviceName"));
                JSONArray devicesarray = str_value.getJSONArray("floors");
                devices.add(devicesarray);
                Log.e("devicesarray" + i, devicesarray.toString());
                Log.e("長度", devicesarray.length() + "");
                ArrayList<Map<String, String>> test = new ArrayList<Map<String, String>>();
                if (devicesarray.length() == 0) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("DeployDeviceName", "");
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
                        map.put("DeployDeviceName", str_value2.getString("DeployDeviceName"));
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
                groupMap.put("DeployDevice_Id", elevatorjson.get(i).getString("DeployDevice_Id"));
                groupMap.put("DeployDeviceName", elevatorjson.get(i).getString("DeployDeviceName"));
                groupMap.put("DeployDevice_IP", elevatorjson.get(i).getString("DeployDevice_IP"));
                groupMap.put("IP_Port", elevatorjson.get(i).getString("IP_Port"));
                groupMap.put("DeployDevice_No", elevatorjson.get(i).getString("DeployDevice_No"));

                parentList.add(groupMap);
            }
            for (int i = 0; i < floors.size(); i++) {
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                for (int j = 0; j < IDarray.get(i).size(); j++) {
                    if (IDarray.get(i).get(0).get("name") != "") {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("childItem", IDarray.get(i).get(j).get("DeployDeviceName"));
                        map.put("isChecked", "No");
                        map.put(
                                "DeployDevice_Id",
                                "" + IDarray.get(i).get(j).get("DeployDevice_Id"));
                        map.put(
                                "Dep_DeployDevice_Id",
                                "" + IDarray.get(i).get(j).get("Dep_DeployDevice_Id"));
                        map.put(
                                "DeployDevice_No",
                                "" + IDarray.get(i).get(j).get("DeployDevice_No"));

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
            Toast.makeText(DoorAccess_elevator.this, e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("錯誤", e.toString());
        }
    }

    private void initView() {
        exListView = (ExpandableListView) findViewById(R.id.exlistview);
        elevatorlist = (ListView) findViewById(R.id.elevatorlist);
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
                holder.TextID = (TextView) convertView.findViewById(R.id.text_ID);
                holder.childBox = (CheckBox) convertView.findViewById(R.id.id_checkbox);

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

    void showdailog(final JSONObject get) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(DoorAccess_elevator.this);
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
                            confirm_name.add(get.get("DeployDeviceName") + "/" + stringText);
                            get.put(
                                    "TimeZone",
                                    timelist.getJSONObject(position).getString("TimeSecGP_Id"));
                            confirmjson.add(get);
                            comfirm_adapter.addAll(confirm_name);
                            comfirm.setAdapter(comfirm_adapter);
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
                            Log.e("0", TimeSection.getJSONObject(0).toString());
                            Log.e("1", TimeSection.getJSONObject(0).toString());

                            Log.e("2", TimeSection.getJSONObject(0).toString());
                            Log.e("3", TimeSection.getJSONObject(0).toString());
                            Log.e("4", TimeSection.getJSONObject(0).toString());
                            Log.e("5", TimeSection.getJSONObject(0).toString());
                            Log.e("6", TimeSection.getJSONObject(0).toString());

                            Toast.makeText(
                                            DoorAccess_elevator.this,
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

    private boolean isexist(String ID, ArrayList<JSONObject> object) {

        try {
            for (int i = 0; i < confirmjson.size(); i++) {
                if (confirmjson.get(i).getString("DeployDevice_Id").equals(ID)) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private void elevatordate() {
        Button next = findViewById(R.id.submit);
        ArrayList<JSONObject> elevatordate = new ArrayList<>();
        ArrayList date_id = new ArrayList();
        next.setEnabled(false);
        if (select_json.size() == 0 && confirmjson.size() != 0) {
            Toast.makeText(DoorAccess_elevator.this, "請選擇電梯需授權樓層", Toast.LENGTH_SHORT).show();
            next.setEnabled(true);

        } else if (select_json.size() != 0 && confirmjson.size() == 0) {
            Toast.makeText(DoorAccess_elevator.this, "請選擇電梯時間表", Toast.LENGTH_SHORT).show();
            next.setEnabled(true);

        } else {
            for (int i = 0; i < select_json.size(); i++) {
                try {
                    String x =
                            date_id.indexOf(select_json.get(i).getString("Dep_DeployDevice_Id"))
                                    + "";
                    if (date_id.indexOf(select_json.get(i).getString("Dep_DeployDevice_Id"))
                            == -1) {
                        date_id.add((select_json.get(i).getString("Dep_DeployDevice_Id")));
                        JSONObject object1 = new JSONObject();
                        object1.put(
                                "device_id", select_json.get(i).getString("Dep_DeployDevice_Id"));
                        JSONArray list = new JSONArray();
                        list.put(select_json.get(i).getString("DeployDevice_No"));
                        JSONArray list2 = new JSONArray();
                        list2.put(select_json.get(i).getString("DeployDeviceName"));
                        object1.put("floors", list);
                        object1.put("TimeZone", "");
                        object1.put("ele_name", list2);
                        elevatordate.add(object1);
                    } else {
                        JSONArray test =
                                elevatordate
                                        .get(
                                                date_id.indexOf(
                                                        select_json
                                                                .get(i)
                                                                .getString("Dep_DeployDevice_Id")))
                                        .getJSONArray("floors");
                        test.put(select_json.get(i).getString("DeployDevice_No"));
                        JSONArray test2 =
                                elevatordate
                                        .get(
                                                date_id.indexOf(
                                                        select_json
                                                                .get(i)
                                                                .getString("Dep_DeployDevice_Id")))
                                        .getJSONArray("ele_name");
                        test2.put(select_json.get(i).getString("DeployDeviceName"));
                    }

                } catch (Exception e) {
                    Log.e("erreos", e.toString());
                }
            }
            try {
                for (int j = 0; j < confirmjson.size(); j++) {

                    if (date_id.indexOf(confirmjson.get(j).getString("DeployDevice_Id")) == -1) {
                        Toast.makeText(
                                        DoorAccess_elevator.this,
                                        "請為有選取樓層的電梯選擇時間表",
                                        Toast.LENGTH_SHORT)
                                .show();
                        next.setEnabled(true);
                        return;

                    } else {

                        JSONObject object2 =
                                elevatordate.get(
                                        date_id.indexOf(
                                                confirmjson.get(j).getString("DeployDevice_Id")));

                        object2.put("TimeZone", confirmjson.get(j).getString("TimeZone"));
                    }
                }
            } catch (Exception e) {
                Log.e("errerssss", e.toString());
            }
            try {
                for (int x = 0; x < elevatordate.size(); x++) {
                    if (elevatordate.get(x).getString("TimeZone") == "") {
                        Toast.makeText(
                                        DoorAccess_elevator.this,
                                        "請為有選取樓層的電梯選擇時間表",
                                        Toast.LENGTH_SHORT)
                                .show();
                        next.setEnabled(true);

                    } else {
                        JSONArray z = elevatordate.get(x).getJSONArray("floors");
                        JSONObject object2 = elevatordate.get(x);
                        int hex = 0;
                        for (int y = 0; y < z.length(); y++) {
                            hex = hex | (int) Math.pow(2, Double.parseDouble(z.get(y).toString()));
                        }
                        JSONObject object3 = elevatordate.get(x);
                        String hexstring = Integer.toHexString(hex);
                        object3.put(
                                "floors_hex", String.format("%02x", hex) + ",00,00,00,00,00,00,00");
                    }
                }
            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }

            Log.e("confirmjson", confirmjson.toString());
            Log.e("elevatordate", elevatordate.toString());
            try {
                String cominginfo = getIntent().getExtras().getString("allinfo");
                JSONObject allinfo = new JSONObject(cominginfo);
                JSONArray elevator_access = new JSONArray();
                for (int i = 0; i < elevatordate.size(); i++) {
                    elevator_access.put(elevatordate.get(i));
                }
                JSONArray elevator_selectinfo = new JSONArray();
                for (int i = 0; i < confirmjson.size(); i++) {
                    elevator_selectinfo.put(confirmjson.get(i));
                }
                // 門禁授權需要資料
                allinfo.put("elevator_access", elevator_access);
                // 搜尋時回傳資料(結果值 右邊部分)
                allinfo.put("elevator_selectinfo", elevator_selectinfo);
                // 搜尋時回傳資料(所選樓層 左邊及中間部分)
                JSONObject accessinfo = new JSONObject();

                accessinfo.put("select_json", select_json);
                accessinfo.put("comfirm_ele", comfirm_ele);
                accessinfo.put("comfirm_elejson", comfirm_elejson);
                allinfo.put("elevator_accessinfo", accessinfo);

                URL url = new URL("http://18.181.171.107/riway/api/v1/access/allow/card");
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url.toURI());
                StringEntity params = new StringEntity(allinfo.toString(), "UTF-8");
                Log.e("params", allinfo.toString());
                httpPost.addHeader("content-type", "application/json");
                httpPost.setEntity(params);
                // Prepare JSON to send by
                // setting the entity
                HttpResponse response = httpClient.execute(httpPost);
                String json_string = EntityUtils.toString(response.getEntity());
                Log.e("json_string", json_string);

                JSONObject temp1 = new JSONObject(json_string);
                String data = temp1.getString("errors");
                if (data.equals("")) {
                    // 若print值為2則列印qrcode
                    if (allinfo.getString("print").equals("2")) {
                        try { // 用卡號查詢QRCODE
                            HttpGet httpGet =
                                    new HttpGet(
                                            "http://18.181.171.107/riway/api/v1/clients/temporary/qrcode/"
                                                    + allinfo.getJSONArray("cards").getString(0));
                            HttpClient httpClient2 = new DefaultHttpClient();
                            httpClient2
                                    .getParams()
                                    .setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
                            httpClient2
                                    .getParams()
                                    .setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
                            HttpResponse response2 = httpClient2.execute(httpGet);
                            HttpEntity responseHttpEntity2 = response2.getEntity();
                            String code = EntityUtils.toString(response2.getEntity());
                            JSONObject temp2 = new JSONObject(code);
                            // 取得BASE64碼並影印
                            String encodedString = temp2.getString("qrcode");

                            final String pureBase64Encoded =
                                    encodedString.substring(encodedString.indexOf(",") + 1);
                            byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
                            Bitmap decodedByte =
                                    BitmapFactory.decodeByteArray(
                                            decodedBytes, 0, decodedBytes.length);
                            SharedPreferences sPrefs =
                                    getSharedPreferences("printer", MODE_PRIVATE);
                            final String printIP = sPrefs.getString("IP", "");
                            EscPosPrinter printer =
                                    new EscPosPrinter(
                                            new TcpConnection(printIP, 9100),
                                            203,
                                            48f,
                                            32,
                                            new EscPosCharsetEncoding("Big5", 0));
                            printer.printFormattedText(
                                            "[C]<u><font size='big'>門禁QRCODE</font></u>\n")
                                    .printFormattedText(
                                            "[R]<img>"
                                                    + PrinterTextParserImg
                                                            .bitmapToHexadecimalString(
                                                                    printer, decodedByte)
                                                    + "</img>\n");
                            // 切紙指令
                            printer.printFormattedTextAndCut("");
                            printer.disconnectPrinter();
                            next.setEnabled(true);
                            Toast.makeText(DoorAccess_elevator.this, "授權成功", Toast.LENGTH_SHORT)
                                    .show();
                            next.setEnabled(true);
                            Intent intent = new Intent();
                            intent.setClass(DoorAccess_elevator.this, ApporvedList.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(
                                            DoorAccess_elevator.this,
                                            "請確認IP以及熱感機連線狀態",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }

                    } else {
                        next.setEnabled(true);
                        Toast.makeText(DoorAccess_elevator.this, "授權成功", Toast.LENGTH_SHORT).show();
                        next.setEnabled(true);
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_elevator.this, ApporvedList.class);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(DoorAccess_elevator.this, data.toString(), Toast.LENGTH_SHORT)
                            .show();
                    next.setEnabled(true);
                }

            } catch (Exception e) {
                Log.e("errorss", e.toString());
                next.setEnabled(true);
            }
        }
    }

    private class ViewHolder {
        TextView groupText, childText, TextID;
        CheckBox groupBox, childBox;
    }

    void printdailog(final Bitmap QRdata) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DoorAccess_elevator.this);
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
                            EscPosPrinter printer =
                                    new EscPosPrinter(
                                            new TcpConnection(IP.getText().toString(), 9100),
                                            203,
                                            48f,
                                            32,
                                            new EscPosCharsetEncoding("Big5", 0));
                            printer.printFormattedText(
                                            "[C]<u><font size='big'>門禁QRCODE</font></u>\n")
                                    .printFormattedText(
                                            "[R]<img>"
                                                    + PrinterTextParserImg
                                                            .bitmapToHexadecimalString(
                                                                    printer, QRdata)
                                                    + "</img>\n");
                            // 切紙指令
                            printer.printFormattedTextAndCut("");
                            printer.disconnectPrinter();
                            SharedPreferences sPrefs =
                                    getSharedPreferences("printer", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sPrefs.edit(); // 获取Editor对象
                            editor.putString("IP", IP.getText().toString()); // 存储数据
                            editor.commit();
                            Toast.makeText(DoorAccess_elevator.this, "授權成功", Toast.LENGTH_SHORT)
                                    .show();
                            Intent intent = new Intent();
                            intent.setClass(DoorAccess_elevator.this, ApporvedList.class);
                            startActivity(intent);

                        } catch (Exception e) {
                            Toast.makeText(
                                            DoorAccess_elevator.this,
                                            "請確認IP以及熱感機連線狀態",
                                            Toast.LENGTH_SHORT)
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
