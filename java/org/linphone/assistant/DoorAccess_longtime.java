package org.linphone.assistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.linphone.R;
import org.linphone.activities.DialerActivity;

public class DoorAccess_longtime extends Activity {
    private List<Map<String, String>> floorparentList = new ArrayList<Map<String, String>>();
    private List<List<Map<String, String>>> floorchildData =
            new ArrayList<List<Map<String, String>>>();
    private ExpandableListView floorlistview;
    private floorsAdapter flooradapter;
    private ListView timelistview;
    private HashSet<String> floorhashSet;
    final List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
    // 選定授權陣列(ex:{會議室,國際廳})
    private ArrayList<String> Authorization = new ArrayList<String>();
    private Context context = this;
    JSONArray timelist;
    JSONArray TimeSection;
    ArrayList<JSONArray> devices = new ArrayList<>();
    final ArrayList confirm_name = new ArrayList<>();
    ArrayAdapter member_adapter;
    SimpleAdapter longtime_adapter;
    ArrayList<JSONObject> floors_confirmjson = new ArrayList<>();
    ArrayList<JSONObject> ele_confirmjson = new ArrayList<>();
    ArrayList<JSONObject> select_json = new ArrayList<>();
    JSONArray accessinfo = new JSONArray();
    String timezoneid = "";
    String timezoneinfo = "";
    JSONObject allinfo = new JSONObject();
    ListView longtimelist;
    String DomainIP = "";

    public void refresh() {

        onCreate(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dooraccess_longtime);
        ImageButton goBA = findViewById(R.id.B_BA);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton goguard = findViewById(R.id.B_Guard);
        final ListView comfirmlist = findViewById(R.id.confirmlist);
        Button next = findViewById(R.id.submit);
        Button cancel = findViewById(R.id.cancel);
        final ListView timelistview = findViewById(R.id.timelist);
        longtimelist = findViewById(R.id.longtimelist);
        SharedPreferences sPrefs = getSharedPreferences("Domain", MODE_PRIVATE);
        DomainIP = sPrefs.getString("IP", "");

        // 上一步的確認資料
        // ("cards", confirm_id);卡號陣列
        // ("group_id", confirm_group_id);部門號陣列
        // ("group_name", confirm_group_name);部門名陣列
        // ("member_name", confirm_name);人員名陣列
        try {
            String cominginfo = getIntent().getExtras().getString("memberinfo");
            allinfo = new JSONObject(cominginfo);
        } catch (Exception e) {
        }
        next.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (timezoneid.equals("")) {
                            Toast.makeText(DoorAccess_longtime.this, "請選擇一個時間表", Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        sentdate();
                    }
                });
        cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cacelsetting();
                    }
                });

        goBA.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_longtime.this, BApage.class);
                        startActivity(intent);
                    }
                });
        gocall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_longtime.this, DialerActivity.class);
                        startActivity(intent);
                    }
                });
        goguard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_longtime.this, Guardpage2.class);
                        startActivity(intent);
                    }
                });

        /* comfirmlist.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent, View view, int position, long id) {
                confirm_name.remove(position);
                confirmjson.remove(position);
            }
        });*/

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
                                                try {

                                                    HttpGet httpGet =
                                                            new HttpGet(
                                                                    "http://"
                                                                            + DomainIP
                                                                            + "/riway/api/v1/access/Managers/list");
                                                    HttpClient httpClient = new DefaultHttpClient();
                                                    httpClient
                                                            .getParams()
                                                            .setParameter(
                                                                    CoreConnectionPNames
                                                                            .CONNECTION_TIMEOUT,
                                                                    2000);
                                                    httpClient
                                                            .getParams()
                                                            .setParameter(
                                                                    CoreConnectionPNames.SO_TIMEOUT,
                                                                    2000);
                                                    HttpResponse response =
                                                            httpClient.execute(httpGet);
                                                    HttpEntity responseHttpEntity =
                                                            response.getEntity();
                                                    String code =
                                                            EntityUtils.toString(
                                                                    response.getEntity());
                                                    JSONObject temp1 = new JSONObject(code);
                                                    JSONArray array1 = temp1.getJSONArray("data");

                                                    for (int i = 0; i < array1.length(); i++) {
                                                        HashMap<String, Object> item =
                                                                new HashMap<String, Object>();
                                                        JSONObject jsonObject =
                                                                array1.getJSONObject(i);
                                                        item.put("json", jsonObject.toString());
                                                        JSONObject test;
                                                        test =
                                                                new JSONObject(
                                                                        jsonObject.getString(
                                                                                "info"));
                                                        item.put("name", test.getString("info"));
                                                        item.put("isSelected", false);

                                                        data.add(item);
                                                    }
                                                    longtime_adapter =
                                                            new SimpleAdapter(
                                                                    DoorAccess_longtime.this,
                                                                    data,
                                                                    R.layout.longtime_listitem,
                                                                    new String[] {"name"},
                                                                    new int[] {R.id.name}) {
                                                                @Override
                                                                public View getView(
                                                                        int position,
                                                                        View convertView,
                                                                        ViewGroup parent) {
                                                                    final View newView =
                                                                            super.getView(
                                                                                    position,
                                                                                    convertView,
                                                                                    parent);
                                                                    if (data.get(position)
                                                                            .get("isSelected")
                                                                            .toString()
                                                                            .equals("false")) {
                                                                        newView.setBackgroundColor(
                                                                                0x202127);
                                                                    } else {
                                                                        newView.setBackgroundColor(
                                                                                0xFF00FFFF);
                                                                    }

                                                                    return newView;
                                                                }
                                                            };

                                                    longtimelist.setAdapter(longtime_adapter);
                                                } catch (Exception e) {
                                                    Log.e("error", e.toString());
                                                }
                                            }
                                        });
                            }
                        })
                .start();
        longtimelist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, final int position, long id) {
                        if (data.get(position).get("isSelected").toString().equals("false")) {
                            data.get(position).put("isSelected", true);
                        } else {
                            data.get(position).put("isSelected", false);
                        }
                        longtime_adapter.notifyDataSetChanged();
                    }
                });

        timelistview.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        try {

                            Toast.makeText(
                                            DoorAccess_longtime.this,
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
        timelistview.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(
                            AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                        try {
                            timezoneid =
                                    timelist.getJSONObject(myItemInt).getString("TimeSecGP_Id");
                            timezoneinfo =
                                    timelist.getJSONObject(myItemInt).getString("TimeSecGpName");
                        } catch (Exception e) {
                        }
                    }
                });
    }

    /** 记录正在选中的子listview的item条目 用hashset是为了去除重复值 */
    private void setListener() {
        floorlistview.setOnGroupExpandListener(
                new ExpandableListView.OnGroupExpandListener() {

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        // 存取已选定的集合
                    }
                });
        // ExpandableListView的Group的点击事件
        floorlistview.setOnGroupClickListener(
                new ExpandableListView.OnGroupClickListener() {

                    @Override
                    public boolean onGroupClick(
                            ExpandableListView parent, View v, int groupPosition, long id) {
                        // 可以写点击后实现的功能

                        return false;
                    }
                });
        // ExpandableListView的child的点击事件

        floorlistview.setOnChildClickListener(
                new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(
                            ExpandableListView parent,
                            View v,
                            int groupPosition,
                            int childPosition,
                            long id) {
                        member_adapter =
                                new ArrayAdapter(DoorAccess_longtime.this, R.layout.mylist_item);

                        Map<String, String> map =
                                floorchildData.get(groupPosition).get(childPosition);
                        Log.e(
                                "isSelected",
                                floorchildData
                                        .get(groupPosition)
                                        .get(childPosition)
                                        .get("isSelected")
                                        .toString());
                        if (floorchildData
                                .get(groupPosition)
                                .get(childPosition)
                                .get("isSelected")
                                .equals("false")) {
                            floorchildData
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .put("isSelected", "true");
                        } else {
                            floorchildData
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .put("isSelected", "false");
                        }
                        flooradapter.notifyDataSetChanged();
                        return false;
                    }
                });
    }

    // 初始化数据
    private void initData() {
        // 所有樓層(1F,2F,3F,)
        ArrayList<String> floors = new ArrayList<String>();
        // 樓層的內容陣列(康樂室,圖書室)
        ArrayList<ArrayList<Map<String, String>>> floosLv2 =
                new ArrayList<ArrayList<Map<String, String>>>();
        ArrayList<String> elevtor = new ArrayList<String>();
        ArrayList<String> elevtor2 = new ArrayList<String>();
        ArrayList<String> elevtor3 = new ArrayList<String>();
        // 樓層的內容陣列(康樂室,圖書室)
        ArrayList<ArrayList<Map<String, String>>> elevtorLv2 =
                new ArrayList<ArrayList<Map<String, String>>>();
        ArrayList<ArrayList<Map<String, String>>> elevtorLv2_2 =
                new ArrayList<ArrayList<Map<String, String>>>();
        ArrayList<ArrayList<Map<String, String>>> elevtorLv2_3 =
                new ArrayList<ArrayList<Map<String, String>>>();
        try {
            try {
                HttpGet httpGet =
                        new HttpGet("http://" + DomainIP + "/riway/api/v1/access/all/info");
                HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity responseHttpEntity = response.getEntity();
                String code = EntityUtils.toString(response.getEntity());
                JSONObject temp1 = new JSONObject(code);
                JSONArray array1 = temp1.getJSONArray("access2");
                JSONArray array2 = temp1.getJSONArray("elevator2");
                accessinfo = temp1.getJSONArray("AccessInfo");
                timelist = temp1.getJSONArray("TimeZone");
                TimeSection = temp1.getJSONArray("TimeSection");
                ArrayAdapter adapter2 =
                        new ArrayAdapter(this, R.layout.simple_list_single_whitetext);
                ArrayList name = new ArrayList<>();
                for (int i = 0; i < timelist.length(); i++) {
                    try {
                        name.add(timelist.getJSONObject(i).getString("TimeSecGpName"));
                    } catch (Exception e) {
                    }
                }
                adapter2.addAll(name);
                timelistview.setAdapter(adapter2);
                timelistview.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
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
                        floosLv2.add(test);
                    } else {
                        for (int j = 0; j < devicesarray.length(); j++) {
                            JSONObject str_value2 = devicesarray.getJSONObject(j);
                            Log.d("內容", str_value2 + "");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("parentname", str_value.getString("floor_name"));
                            map.put("name", str_value2.getString("DeployDeviceName"));
                            map.put("DeployDevice_Id", str_value2.getString("DeployDevice_Id"));
                            map.put(
                                    "Dep_DeployDevice_Id",
                                    str_value2.getString("Dep_DeployDevice_Id"));
                            map.put("DeployDevice_No", str_value2.getString("DeployDevice_No"));
                            test.add(map);
                        }
                        floosLv2.add(test);
                    }
                }
                // 樓層第二層
                for (int i = 0; i < floors.size(); i++) {
                    Map<String, String> groupMap = new HashMap<String, String>();
                    groupMap.put("groupText", floors.get(i).toString());
                    groupMap.put("isGroupCheckd", "No");
                    floorparentList.add(groupMap);
                }
                for (int i = 0; i < floors.size(); i++) {
                    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                    for (int j = 0; j < floosLv2.get(i).size(); j++) {
                        if (floosLv2.get(i).get(0).get("name") != "") {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("childItem", floosLv2.get(i).get(j).get("name"));
                            map.put("parentname", floosLv2.get(i).get(j).get("parentname"));
                            map.put(
                                    "DeployDevice_Id",
                                    "" + floosLv2.get(i).get(j).get("DeployDevice_Id"));
                            map.put(
                                    "DeployDevice_No",
                                    "" + floosLv2.get(i).get(j).get("DeployDevice_No"));
                            map.put(
                                    "Dep_DeployDevice_Id",
                                    floosLv2.get(i).get(j).get("Dep_DeployDevice_Id"));
                            map.put("isSelected", "false");
                            map.put("gojson", floosLv2.get(i).get(j).toString());
                            list.add(map);
                        }
                    }
                    floorchildData.add(list);
                }
                // =================================電梯
                floors.clear();
                floosLv2.clear();
                for (int i = 0; i < array2.length(); i++) {
                    JSONObject str_value = array2.getJSONObject(i);
                    floors.add(str_value.getString("DeployDeviceName"));
                    JSONArray devicesarray = str_value.getJSONArray("floors");
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
                        floosLv2.add(test);
                    } else {
                        for (int j = 0; j < devicesarray.length(); j++) {
                            JSONObject str_value2 = devicesarray.getJSONObject(j);
                            Log.d("內容", str_value2 + "");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("parentname", str_value.getString("DeployDeviceName"));
                            map.put("name", str_value2.getString("DeployDeviceName"));
                            map.put("DeployDevice_Id", str_value2.getString("DeployDevice_Id"));
                            map.put(
                                    "Dep_DeployDevice_Id",
                                    str_value2.getString("Dep_DeployDevice_Id"));
                            map.put("DeployDevice_No", str_value2.getString("DeployDevice_No"));
                            test.add(map);
                        }
                        floosLv2.add(test);
                    }
                }
                // 樓層第二層
                for (int i = 0; i < floors.size(); i++) {
                    Map<String, String> groupMap = new HashMap<String, String>();
                    groupMap.put("groupText", floors.get(i).toString());
                    groupMap.put("isGroupCheckd", "No");
                    floorparentList.add(groupMap);
                }
                for (int i = 0; i < floors.size(); i++) {
                    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                    for (int j = 0; j < floosLv2.get(i).size(); j++) {
                        if (floosLv2.get(i).get(0).get("DeployDeviceName") != "") {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("childItem", floosLv2.get(i).get(j).get("name"));
                            map.put("parentname", floosLv2.get(i).get(j).get("parentname"));
                            map.put(
                                    "DeployDevice_Id",
                                    "" + floosLv2.get(i).get(j).get("DeployDevice_Id"));
                            map.put(
                                    "DeployDevice_No",
                                    "" + floosLv2.get(i).get(j).get("DeployDevice_No"));
                            map.put(
                                    "Dep_DeployDevice_Id",
                                    floosLv2.get(i).get(j).get("Dep_DeployDevice_Id"));
                            map.put("isSelected", "false");
                            map.put("gojson", floosLv2.get(i).get(j).toString());
                            list.add(map);
                        }
                    }
                    floorchildData.add(list);
                }

                flooradapter = new floorsAdapter();
                floorlistview.setAdapter(flooradapter);
                // floorlistview.expandGroup(0);
                floorhashSet = new HashSet<String>();
                flooradapter = new floorsAdapter();
                floorlistview.setAdapter(flooradapter);
                floorhashSet = new HashSet<String>();
                Log.e("floorchildData", floorchildData.toString());

            } catch (Exception e) {
                Toast.makeText(DoorAccess_longtime.this, e.toString(), Toast.LENGTH_SHORT).show();
                Log.e("錯誤", e.toString());
            }

        } catch (Exception e) {
            Log.e("885", e.toString());
        }
    }

    private void initView() {
        floorlistview = (ExpandableListView) findViewById(R.id.flooorlistview);
        timelistview = findViewById(R.id.timelist);
    }

    /** 樓層适配adapter */
    private class floorsAdapter extends BaseExpandableListAdapter {
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return floorchildData.get(groupPosition).get(childPosition);
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

            final DoorAccess_longtime.ViewHolder holder;
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
                    floorchildData.get(groupPosition).get(childPosition).get("childItem"));

            String isChecked =
                    floorchildData.get(groupPosition).get(childPosition).get("isChecked");
            if (floorchildData
                    .get(groupPosition)
                    .get(childPosition)
                    .get("isSelected")
                    .equals("false")) {
                convertView.setBackgroundColor(0x202127);
            } else {
                convertView.setBackgroundColor(0xFF00FFFF);
            }

            holder.TextID.setText(
                    floorchildData.get(groupPosition).get(childPosition).get("DeployDevice_Id"));
            holder.childText.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            member_adapter =
                                    new ArrayAdapter(
                                            DoorAccess_longtime.this, R.layout.mylist_item);
                            String get =
                                    floorchildData
                                            .get(groupPosition)
                                            .get(childPosition)
                                            .get("DeployDevice_Id");

                            Log.e("123", get);
                            if (floorchildData
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .get("isSelected")
                                    .equals("false")) {
                                floorchildData
                                        .get(groupPosition)
                                        .get(childPosition)
                                        .put("isSelected", "true");
                            } else {
                                floorchildData
                                        .get(groupPosition)
                                        .get(childPosition)
                                        .put("isSelected", "false");
                            }
                            flooradapter.notifyDataSetChanged();
                        }
                    });
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return floorchildData.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return floorparentList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return floorparentList.size();
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
            DoorAccess_longtime.ViewHolder holder = null;
            if (convertView == null) {
                holder = new DoorAccess_longtime.ViewHolder();
                convertView = View.inflate(context, R.layout.group_item, null);
                holder.groupText = (TextView) convertView.findViewById(R.id.id_group_text);
                holder.groupBox = (CheckBox) convertView.findViewById(R.id.id_group_checkbox);
                convertView.setTag(holder);
            } else {
                holder = (DoorAccess_longtime.ViewHolder) convertView.getTag();
            }

            holder.groupText.setText(floorparentList.get(groupPosition).get("groupText"));
            final String isGroupCheckd = floorparentList.get(groupPosition).get("isGroupCheckd");

            if ("No".equals(isGroupCheckd)) {
                holder.groupBox.setChecked(false);
            } else {
                holder.groupBox.setChecked(true);
            }
            long packedPosition = floorlistview.getPackedPositionForGroup(groupPosition);

            String x = floorparentList.get(groupPosition).get("groupText");
            Log.e("x", x);
            Log.e("selcetelevator ", getGroup(groupPosition) + "");

            /*
             * groupListView的点击事件
             */
            /*   holder.groupBox.setOnClickListener(
            new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    CheckBox groupBox = (CheckBox) v.findViewById(R.id.id_group_checkbox);
                    if (!isExpanded) {
                        // 展开某个group view
                        floorlistview.expandGroup(groupPosition);
                    } else {
                        // 关闭某个group view
                        floorlistview.collapseGroup(groupPosition);
                    }

                    if ("No".equals(isGroupCheckd)) {
                        floorlistview.expandGroup(groupPosition);
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
    // 電梯adapter

    private class ViewHolder {
        TextView groupText, childText, TextID;
        CheckBox groupBox, childBox;
    }

    private void sentdate() {
        Log.e("floorchildData", floorchildData.toString());
        floors_confirmjson.clear();
        try {
            for (int i = 0; i < floorchildData.size(); i++) {
                for (int j = 0; j < floorchildData.get(i).size(); j++) {
                    if (floorchildData.get(i).get(j).get("isSelected").equals("true")) {
                        JSONObject go = new JSONObject();
                        go.put(
                                "DeployDevice_No",
                                floorchildData.get(i).get(j).get("DeployDevice_No"));
                        go.put("name", floorchildData.get(i).get(j).get("name"));
                        go.put(
                                "Dep_DeployDevice_Id",
                                floorchildData.get(i).get(j).get("Dep_DeployDevice_Id"));
                        go.put(
                                "info",
                                floorchildData.get(i).get(j).get("parentname")
                                        + "_"
                                        + floorchildData.get(i).get(j).get("childItem")
                                        + " "
                                        + timezoneinfo);
                        floors_confirmjson.add(go);
                    }
                }
            }
            for (int x = 0; x < floors_confirmjson.size(); x++) {
                floors_confirmjson.get(x).put("TimeSecGP_Id", timezoneid);
            }
            Log.e("confirmjson", floors_confirmjson.toString());
        } catch (Exception e) {
            Log.e("1667", e.toString());
        }
        try {
            URL url = new URL("http://" + DomainIP + "/riway/api/v1/access/Managers/setting");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url.toURI());
            StringEntity params = new StringEntity(floors_confirmjson.toString(), "UTF-8");
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(params);
            // Prepare JSON to send by
            // setting the entity
            HttpResponse response = httpClient.execute(httpPost);
            String json_string = EntityUtils.toString(response.getEntity());
            Log.e("json_string", json_string);
            JSONObject temp1 = new JSONObject(json_string);
            String errors = temp1.getString("errors");
            if (errors.equals("")) {
                Toast.makeText(DoorAccess_longtime.this, "設定完成", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(DoorAccess_longtime.this, DoorAccess_longtime.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(DoorAccess_longtime.this, errors, Toast.LENGTH_SHORT).show();
            }
            Log.e("data", errors);
        } catch (Exception e) {
            Log.e("846", e.toString());
        }
    }

    private void cacelsetting() {
        try {
            ArrayList<JSONObject> cancel_json = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).get("isSelected").toString().equals("true")) {
                    JSONObject test = new JSONObject(data.get(i).get("json").toString());
                    test.put("TimeSecGP_Id", "0");
                    cancel_json.add(test);
                }
            }
            URL url = new URL("http://" + DomainIP + "/riway/api/v1/access/Managers/setting");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url.toURI());
            StringEntity params = new StringEntity(cancel_json.toString(), "UTF-8");
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(params);
            // Prepare JSON to send by
            // setting the entity
            HttpResponse response = httpClient.execute(httpPost);
            String json_string = EntityUtils.toString(response.getEntity());
            Log.e("json_string", json_string);
            JSONObject temp1 = new JSONObject(json_string);
            String errors = temp1.getString("errors");
            Log.e("data", errors.toString());
            if (errors.equals("")) {
                Toast.makeText(DoorAccess_longtime.this, "取消完成", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(DoorAccess_longtime.this, DoorAccess_longtime.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(DoorAccess_longtime.this, errors, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("846", e.toString());
        }
    }
}
