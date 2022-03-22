package org.linphone.assistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.dantsu.escposprinter.EscPosCharsetEncoding;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
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

public class DoorAccess_floor_elevtor extends Activity {
    private List<Map<String, String>> floorparentList = new ArrayList<Map<String, String>>();
    private List<List<Map<String, String>>> floorchildData =
            new ArrayList<List<Map<String, String>>>();
    private ExpandableListView floorlistview;
    private floorsAdapter flooradapter;
    private ListView timelistview;
    private HashSet<String> floorhashSet;
    private List<Map<String, String>> elevtorparentList = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> elevtorparentList2 = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> elevtorparentList3 = new ArrayList<Map<String, String>>();
    private List<List<Map<String, String>>> elevtorchildData =
            new ArrayList<List<Map<String, String>>>();
    private List<List<Map<String, String>>> elevtorchildData2 =
            new ArrayList<List<Map<String, String>>>();
    private List<List<Map<String, String>>> elevtorchildData3 =
            new ArrayList<List<Map<String, String>>>();
    private ExpandableListView elevtorlistview, elevtorlistview2, elevtorlistview3;
    private elevtorAdapter elevtoradapter;
    private elevtorAdapter2 elevtoradapter2;
    private elevtorAdapter3 elevtoradapter3;
    private HashSet<String> elevtorhashSet, elevtorhashSet2, elevtorhashSet3;
    // 選定授權陣列(ex:{會議室,國際廳})
    private ArrayList<String> Authorization = new ArrayList<String>();
    private Context context = this;
    JSONArray timelist;
    JSONArray TimeSection;
    ArrayList<JSONArray> devices = new ArrayList<>();
    final ArrayList confirm_name = new ArrayList<>();
    ArrayAdapter member_adapter;
    ArrayList<JSONObject> floors_confirmjson = new ArrayList<>();
    ArrayList<JSONObject> ele_confirmjson = new ArrayList<>();
    ArrayList<JSONObject> select_json = new ArrayList<>();
    JSONArray accessinfo = new JSONArray();
    String timezoneid = "";
    JSONObject allinfo = new JSONObject();
    String DomainIP = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dooraccess_floor_elevator);
        ImageButton goBA = findViewById(R.id.B_BA);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton goguard = findViewById(R.id.B_Guard);
        ImageButton godoor = findViewById(R.id.B_door);
        final ListView comfirmlist = findViewById(R.id.confirmlist);
        Button next = findViewById(R.id.submit);
        Button showlist = findViewById(R.id.showlist);
        final ListView timelistview = findViewById(R.id.timelist);
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
                            Toast.makeText(
                                            DoorAccess_floor_elevtor.this,
                                            "請選擇一個時間表",
                                            Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        elevatordate();

                        floorsdate();
                    }
                });

        goBA.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_floor_elevtor.this, BApage.class);
                        startActivity(intent);
                    }
                });
        gocall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_floor_elevtor.this, DialerActivity.class);
                        startActivity(intent);
                    }
                });
        goguard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_floor_elevtor.this, Guardpage2.class);
                        startActivity(intent);
                    }
                });
        goguard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_floor_elevtor.this, memberdatabase.class);
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
                                    new AlertDialog.Builder(DoorAccess_floor_elevtor.this);
                            dialog.setTitle("已選清單");
                            dialog.setMessage("已選人員:" + object.get("member_name"));
                            dialog.show();

                        } catch (Exception e) {
                        }
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
                                                setListViewHeight(elevtorlistview);
                                                setListViewHeight(elevtorlistview2);
                                                setListViewHeight(elevtorlistview3);
                                            }
                                        });
                            }
                        })
                .start();

        timelistview.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        try {

                            Toast.makeText(
                                            DoorAccess_floor_elevtor.this,
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
                                new ArrayAdapter(
                                        DoorAccess_floor_elevtor.this, R.layout.mylist_item);

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
        // 電梯的========================================================================================

        elevtorlistview.setOnGroupExpandListener(
                new ExpandableListView.OnGroupExpandListener() {

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        // 存取已选定的集合
                    }
                });
        // ExpandableListView的Group的点击事件
        elevtorlistview.setOnGroupClickListener(
                new ExpandableListView.OnGroupClickListener() {

                    @Override
                    public boolean onGroupClick(
                            ExpandableListView parent, View v, int groupPosition, long id) {
                        // 可以写点击后实现的功能

                        return false;
                    }
                });

        // ExpandableListView的child的点击事件

        elevtorlistview.setOnChildClickListener(
                new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(
                            ExpandableListView parent,
                            View v,
                            int groupPosition,
                            int childPosition,
                            long id) {

                        Map<String, String> map =
                                elevtorchildData.get(groupPosition).get(childPosition);
                        Log.e(
                                "isSelected",
                                elevtorchildData
                                        .get(groupPosition)
                                        .get(childPosition)
                                        .get("isSelected")
                                        .toString());
                        if (elevtorchildData
                                .get(groupPosition)
                                .get(childPosition)
                                .get("isSelected")
                                .equals("false")) {
                            elevtorchildData
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .put("isSelected", "true");
                        } else {
                            elevtorchildData
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .put("isSelected", "false");
                        }
                        elevtoradapter.notifyDataSetChanged();
                        return false;
                    }
                });
        elevtorlistview2.setOnChildClickListener(
                new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(
                            ExpandableListView parent,
                            View v,
                            int groupPosition,
                            int childPosition,
                            long id) {

                        Map<String, String> map =
                                elevtorchildData2.get(groupPosition).get(childPosition);
                        Log.e(
                                "isSelected",
                                elevtorchildData2
                                        .get(groupPosition)
                                        .get(childPosition)
                                        .get("isSelected")
                                        .toString());
                        if (elevtorchildData2
                                .get(groupPosition)
                                .get(childPosition)
                                .get("isSelected")
                                .equals("false")) {
                            elevtorchildData2
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .put("isSelected", "true");
                        } else {
                            elevtorchildData2
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .put("isSelected", "false");
                        }
                        elevtoradapter2.notifyDataSetChanged();
                        return false;
                    }
                });
        elevtorlistview3.setOnChildClickListener(
                new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(
                            ExpandableListView parent,
                            View v,
                            int groupPosition,
                            int childPosition,
                            long id) {

                        Map<String, String> map =
                                elevtorchildData3.get(groupPosition).get(childPosition);
                        Log.e(
                                "isSelected",
                                elevtorchildData3
                                        .get(groupPosition)
                                        .get(childPosition)
                                        .get("isSelected")
                                        .toString());
                        if (elevtorchildData3
                                .get(groupPosition)
                                .get(childPosition)
                                .get("isSelected")
                                .equals("false")) {
                            elevtorchildData3
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .put("isSelected", "true");
                        } else {
                            elevtorchildData3
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .put("isSelected", "false");
                        }
                        elevtoradapter3.notifyDataSetChanged();
                        return false;
                    }
                });
        /*  ExpandableListViewSynchronisationTouchListener
                expandableListViewSynchronisationTouchListener =
                        new ExpandableListViewSynchronisationTouchListener(
                                this, elevtorlistview, elevtorlistview2, elevtorlistview3);
        elevtorlistview.setOnTouchListener(expandableListViewSynchronisationTouchListener);
        elevtorlistview2.setOnTouchListener(expandableListViewSynchronisationTouchListener);
        elevtorlistview3.setOnTouchListener(expandableListViewSynchronisationTouchListener);
        elevtorlistview.setOnChildClickListener(expandableListViewSynchronisationTouchListener);
        elevtorlistview2.setOnChildClickListener(expandableListViewSynchronisationTouchListener);
        elevtorlistview3.setOnChildClickListener(expandableListViewSynchronisationTouchListener);*/
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
            if (allinfo.getString("getaccess").equals("0")) {
                try {
                    HttpGet httpGet =
                            new HttpGet("http://" + DomainIP + "/riway/api/v1/access/all/info");
                    HttpClient httpClient = new DefaultHttpClient();
                    httpClient
                            .getParams()
                            .setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
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
                    // 取得電梯=========================================================================
                    JSONObject str_value = array2.getJSONObject(0);
                    elevtor.add(str_value.getString("DeployDeviceName"));
                    JSONArray devicesarray = str_value.getJSONArray("floors");
                    ArrayList<Map<String, String>> test = new ArrayList<Map<String, String>>();
                    if (devicesarray.length() == 0) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("DeployDeviceName", "");
                        map.put("DeployDevice_Id", "");
                        map.put("Dep_DeployDevice_Id", "");
                        map.put("DeployDevice_No", "");
                        test.add(map);
                        elevtorLv2.add(test);
                    } else {
                        for (int j = 0; j < devicesarray.length(); j++) {
                            JSONObject str_value2 = devicesarray.getJSONObject(j);
                            Log.d("內容", str_value2 + "");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("DeployDeviceName", str_value2.getString("DeployDeviceName"));
                            map.put("DeployDevice_Id", str_value2.getString("DeployDevice_Id"));
                            map.put(
                                    "Dep_DeployDevice_Id",
                                    str_value2.getString("Dep_DeployDevice_Id"));
                            map.put("DeployDevice_No", str_value2.getString("DeployDevice_No"));
                            test.add(map);
                        }
                        elevtorLv2.add(test);
                    }
                    Log.e("elevtor", elevtor.toString());
                    Log.e("elevtorLv2", elevtorLv2.toString());

                    // 電梯第二層
                    for (int i = 0; i < elevtor.size(); i++) {
                        Map<String, String> groupMap = new HashMap<String, String>();
                        groupMap.put("groupText", elevtor.get(i).toString());
                        groupMap.put("isGroupCheckd", "No");
                        elevtorparentList.add(groupMap);
                    }
                    for (int i = 0; i < elevtor.size(); i++) {
                        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                        for (int j = 0; j < elevtorLv2.get(i).size(); j++) {
                            if (elevtorLv2.get(i).get(0).get("DeployDeviceName") != "") {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put(
                                        "childItem",
                                        elevtorLv2.get(i).get(j).get("DeployDeviceName"));
                                map.put("isChecked", "No");
                                map.put(
                                        "DeployDevice_Id",
                                        "" + elevtorLv2.get(i).get(j).get("DeployDevice_Id"));
                                map.put(
                                        "Dep_DeployDevice_Id",
                                        "" + elevtorLv2.get(i).get(j).get("Dep_DeployDevice_Id"));
                                map.put(
                                        "DeployDevice_No",
                                        "" + elevtorLv2.get(i).get(j).get("DeployDevice_No"));
                                map.put("isSelected", "false");
                                map.put("gojson", elevtorLv2.get(i).get(j).toString());
                                list.add(map);
                            }
                        }
                        Log.e("list", list.toString());
                        elevtorchildData.add(list);
                    }
                    JSONObject str_value2 = array2.getJSONObject(1);
                    elevtor2.add(str_value2.getString("DeployDeviceName"));
                    JSONArray devicesarray2 = str_value2.getJSONArray("floors");
                    ArrayList<Map<String, String>> test2 = new ArrayList<Map<String, String>>();
                    if (devicesarray2.length() == 0) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("DeployDeviceName", "");
                        map.put("DeployDevice_Id", "");
                        map.put("Dep_DeployDevice_Id", "");
                        map.put("DeployDevice_No", "");
                        test2.add(map);
                        elevtorLv2_2.add(test);
                    } else {
                        for (int j = 0; j < devicesarray2.length(); j++) {
                            JSONObject str_value22 = devicesarray2.getJSONObject(j);
                            Log.d("內容", str_value22 + "");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("DeployDeviceName", str_value22.getString("DeployDeviceName"));
                            map.put("DeployDevice_Id", str_value22.getString("DeployDevice_Id"));
                            map.put(
                                    "Dep_DeployDevice_Id",
                                    str_value22.getString("Dep_DeployDevice_Id"));
                            map.put("DeployDevice_No", str_value22.getString("DeployDevice_No"));
                            test2.add(map);
                        }
                        elevtorLv2_2.add(test);
                    }
                    // 電梯第二層
                    for (int i = 0; i < elevtor2.size(); i++) {
                        Map<String, String> groupMap = new HashMap<String, String>();
                        groupMap.put("groupText", elevtor2.get(i).toString());
                        groupMap.put("isGroupCheckd", "No");
                        elevtorparentList2.add(groupMap);
                    }
                    for (int i = 0; i < elevtor2.size(); i++) {
                        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                        for (int j = 0; j < elevtorLv2_2.get(i).size(); j++) {
                            if (elevtorLv2.get(i).get(0).get("name") != "") {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put(
                                        "childItem",
                                        elevtorLv2_2.get(i).get(j).get("DeployDeviceName"));
                                map.put("isChecked", "No");
                                map.put(
                                        "DeployDevice_Id",
                                        "" + elevtorLv2_2.get(i).get(j).get("DeployDevice_Id"));
                                map.put(
                                        "Dep_DeployDevice_Id",
                                        "" + elevtorLv2_2.get(i).get(j).get("Dep_DeployDevice_Id"));
                                map.put(
                                        "DeployDevice_No",
                                        "" + elevtorLv2_2.get(i).get(j).get("DeployDevice_No"));
                                map.put("isSelected", "false");
                                map.put("gojson", elevtorLv2_2.get(i).get(j).toString());

                                list.add(map);
                            }
                        }
                        elevtorchildData2.add(list);
                    }

                    JSONObject str_value3 = array2.getJSONObject(2);
                    elevtor3.add(str_value3.getString("DeployDeviceName"));
                    JSONArray devicesarray3 = str_value3.getJSONArray("floors");
                    ArrayList<Map<String, String>> test3 = new ArrayList<Map<String, String>>();
                    if (devicesarray3.length() == 0) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("DeployDeviceName", "");
                        map.put("DeployDevice_Id", "");
                        map.put("Dep_DeployDevice_Id", "");
                        map.put("DeployDevice_No", "");
                        test3.add(map);
                        elevtorLv2_3.add(test);
                    } else {
                        for (int j = 0; j < devicesarray3.length(); j++) {
                            JSONObject str_value23 = devicesarray3.getJSONObject(j);
                            Log.d("內容", str_value23 + "");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("DeployDeviceName", str_value23.getString("DeployDeviceName"));
                            map.put("DeployDevice_Id", str_value23.getString("DeployDevice_Id"));
                            map.put(
                                    "Dep_DeployDevice_Id",
                                    str_value23.getString("Dep_DeployDevice_Id"));
                            map.put("DeployDevice_No", str_value23.getString("DeployDevice_No"));
                            test3.add(map);
                        }
                        elevtorLv2_3.add(test);
                    }
                    // 電梯第二層
                    for (int i = 0; i < elevtor3.size(); i++) {
                        Map<String, String> groupMap = new HashMap<String, String>();
                        groupMap.put("groupText", elevtor3.get(i).toString());
                        groupMap.put("isGroupCheckd", "No");
                        elevtorparentList3.add(groupMap);
                    }
                    for (int i = 0; i < elevtor3.size(); i++) {
                        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                        for (int j = 0; j < elevtorLv2_3.get(i).size(); j++) {
                            if (elevtorLv2.get(i).get(0).get("name") != "") {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put(
                                        "childItem",
                                        elevtorLv2_3.get(i).get(j).get("DeployDeviceName"));
                                map.put("isChecked", "No");
                                map.put(
                                        "DeployDevice_Id",
                                        "" + elevtorLv2_3.get(i).get(j).get("DeployDevice_Id"));
                                map.put(
                                        "Dep_DeployDevice_Id",
                                        "" + elevtorLv2_3.get(i).get(j).get("Dep_DeployDevice_Id"));
                                map.put(
                                        "DeployDevice_No",
                                        "" + elevtorLv2_3.get(i).get(j).get("DeployDevice_No"));
                                map.put("isSelected", "false");
                                map.put("gojson", elevtorLv2_3.get(i).get(j).toString());
                                list.add(map);
                            }
                        }
                        elevtorchildData3.add(list);
                    }

                    elevtoradapter = new elevtorAdapter();
                    elevtorlistview.setAdapter(elevtoradapter);
                    elevtorlistview.expandGroup(0);
                    elevtorhashSet = new HashSet<String>();
                    elevtoradapter2 = new elevtorAdapter2();
                    elevtorlistview2.setAdapter(elevtoradapter2);
                    elevtorlistview2.expandGroup(0);
                    elevtoradapter3 = new elevtorAdapter3();
                    elevtorlistview3.setAdapter(elevtoradapter3);
                    elevtorlistview3.expandGroup(0);
                    flooradapter = new floorsAdapter();
                    floorlistview.setAdapter(flooradapter);
                    floorhashSet = new HashSet<String>();

                } catch (Exception e) {
                    Toast.makeText(DoorAccess_floor_elevtor.this, e.toString(), Toast.LENGTH_SHORT)
                            .show();
                    Log.e("錯誤", e.toString());
                }
            } else {
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

                JSONObject object = new JSONObject(getIntent().getExtras().getString("selectinfo"));
                JSONObject object2 = new JSONObject(object.getString("elevator_accessinfo"));
                JSONArray object3 = new JSONArray(object2.getString("floorparentList"));
                Log.e("object", object.toString());
                floorparentList =
                        new Gson()
                                .fromJson(
                                        object2.getString("floorparentList"),
                                        new ArrayList<Map<String, String>>().getClass());
                elevtorparentList =
                        new Gson()
                                .fromJson(
                                        object2.getString("elevtorparentList"),
                                        new ArrayList<Map<String, String>>().getClass());
                elevtorparentList2 =
                        new Gson()
                                .fromJson(
                                        object2.getString("elevtorparentList2"),
                                        new ArrayList<Map<String, String>>().getClass());
                elevtorparentList3 =
                        new Gson()
                                .fromJson(
                                        object2.getString("elevtorparentList3"),
                                        new ArrayList<Map<String, String>>().getClass());

                timezoneid = object2.getString("timezoneid");
                timelistview.setItemChecked(Integer.valueOf(timezoneid) - 1, true);

                /* for (int i = 0; i < object3.length(); i++) {
                    Map<String, String> groupMap = new HashMap<String, String>();
                    groupMap.put("groupText", object3.getJSONObject(i).getString("groupText"));
                    groupMap.put("isGroupCheckd", "No");
                    floorparentList.add(groupMap);
                }*/
                /* object3 = new JSONArray(object2.getString("elevtorparentList"));
                for (int i = 0; i < object3.length(); i++) {
                    Map<String, String> groupMap = new HashMap<String, String>();
                    groupMap.put("groupText", object3.getJSONObject(i).getString("groupText"));
                    groupMap.put("isGroupCheckd", "No");
                    elevtorparentList.add(groupMap);
                }
                object3 = new JSONArray(object2.getString("elevtorparentList2"));
                for (int i = 0; i < object3.length(); i++) {
                    Map<String, String> groupMap = new HashMap<String, String>();
                    groupMap.put("groupText", object3.getJSONObject(i).getString("groupText"));
                    groupMap.put("isGroupCheckd", "No");
                    elevtorparentList2.add(groupMap);
                }
                object3 = new JSONArray(object2.getString("elevtorparentList3"));
                for (int i = 0; i < object3.length(); i++) {
                    Map<String, String> groupMap = new HashMap<String, String>();
                    groupMap.put("groupText", object3.getJSONObject(i).getString("groupText"));
                    groupMap.put("isGroupCheckd", "No");
                    elevtorparentList3.add(groupMap);
                }*/

                /*   floorchildData =
                        new Gson()
                                .fromJson(
                                        object2.getString("floorchildData").replace(" ", ""),
                                        new ArrayList<Map<String, String>>().getClass());
                Log.e("floorchildData1", floorchildData.toString());
                Log.e("floorchildData2", object2.getString("floorchildData").toString());*/
                /* JsonArray returnData =
                        new JsonParser()
                                .parse(object2.getString("floorchildData"))
                                .getAsJsonArray();
                Log.e("fkcloa", returnData.toString());*/

                object3 = new JSONArray(object2.getString("floorchildData").replace(" ", ""));

                for (int i = 0; i < object3.length(); i++) {
                    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                    for (int j = 0; j < object3.getJSONArray(i).length(); j++) {
                        if (object3.getJSONArray(i).getJSONObject(j).get("childItem") != "") {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put(
                                    "childItem",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("childItem")
                                            .toString());
                            map.put(
                                    "DeployDevice_Id",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("DeployDevice_Id")
                                            .toString());
                            map.put(
                                    "Dep_DeployDevice_Id",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("Dep_DeployDevice_Id")
                                            .toString());
                            map.put(
                                    "DeployDevice_No",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("DeployDevice_No")
                                            .toString());
                            map.put(
                                    "isSelected",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("isSelected")
                                            .toString());
                            map.put(
                                    "gojson",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("gojson")
                                            .toString());
                            list.add(map);
                        }
                    }
                    floorchildData.add(list);
                }

                object3 = new JSONArray(object2.getString("elevtorchildData").replace(" ", ""));
                JsonParser jsonParser = new JsonParser();
                JsonArray jsonArray =
                        (JsonArray) jsonParser.parse(object2.getString("elevtorchildData"));

                for (int i = 0; i < object3.length(); i++) {
                    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                    for (int j = 0; j < object3.getJSONArray(i).length(); j++) {
                        if (object3.getJSONArray(i).getJSONObject(j).get("childItem") != ""
                                && object3.getJSONArray(i)
                                                .getJSONObject(j)
                                                .get("childItem")
                                                .toString()
                                        != "null") {
                            Map<String, String> map = new HashMap<String, String>();

                            map.put(
                                    "childItem",
                                    jsonArray
                                            .get(i)
                                            .getAsJsonArray()
                                            .get(j)
                                            .getAsJsonObject()
                                            .get("childItem")
                                            .getAsString());
                            map.put(
                                    "DeployDevice_Id",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("DeployDevice_Id")
                                            .toString());
                            map.put(
                                    "Dep_DeployDevice_Id",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("Dep_DeployDevice_Id")
                                            .toString());
                            map.put(
                                    "DeployDevice_No",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("DeployDevice_No")
                                            .toString());
                            map.put(
                                    "isSelected",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("isSelected")
                                            .toString());
                            map.put(
                                    "gojson",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("gojson")
                                            .toString());
                            list.add(map);
                        }
                    }
                    elevtorchildData.add(list);
                }
                object3 = new JSONArray(object2.getString("elevtorchildData2").replace(" ", ""));
                jsonArray = (JsonArray) jsonParser.parse(object2.getString("elevtorchildData2"));
                for (int i = 0; i < object3.length(); i++) {
                    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                    for (int j = 0; j < object3.getJSONArray(i).length(); j++) {
                        if (object3.getJSONArray(i).getJSONObject(j).get("childItem") != ""
                                && object3.getJSONArray(i)
                                                .getJSONObject(j)
                                                .get("childItem")
                                                .toString()
                                        != "null") {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put(
                                    "childItem",
                                    jsonArray
                                            .get(i)
                                            .getAsJsonArray()
                                            .get(j)
                                            .getAsJsonObject()
                                            .get("childItem")
                                            .getAsString());
                            map.put(
                                    "DeployDevice_Id",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("DeployDevice_Id")
                                            .toString());
                            map.put(
                                    "Dep_DeployDevice_Id",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("Dep_DeployDevice_Id")
                                            .toString());
                            map.put(
                                    "DeployDevice_No",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("DeployDevice_No")
                                            .toString());
                            map.put(
                                    "isSelected",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("isSelected")
                                            .toString());
                            map.put(
                                    "gojson",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("gojson")
                                            .toString());
                            list.add(map);
                        }
                    }
                    elevtorchildData2.add(list);
                }
                object3 = new JSONArray(object2.getString("elevtorchildData3").replace(" ", ""));
                jsonArray = (JsonArray) jsonParser.parse(object2.getString("elevtorchildData3"));
                for (int i = 0; i < object3.length(); i++) {
                    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                    for (int j = 0; j < object3.getJSONArray(i).length(); j++) {
                        if (object3.getJSONArray(i).getJSONObject(j).get("childItem") != ""
                                && object3.getJSONArray(i)
                                                .getJSONObject(j)
                                                .get("childItem")
                                                .toString()
                                        != "null") {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put(
                                    "childItem",
                                    jsonArray
                                            .get(i)
                                            .getAsJsonArray()
                                            .get(j)
                                            .getAsJsonObject()
                                            .get("childItem")
                                            .getAsString());
                            map.put(
                                    "DeployDevice_Id",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("DeployDevice_Id")
                                            .toString());
                            map.put(
                                    "Dep_DeployDevice_Id",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("Dep_DeployDevice_Id")
                                            .toString());
                            map.put(
                                    "DeployDevice_No",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("DeployDevice_No")
                                            .toString());
                            map.put(
                                    "isSelected",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("isSelected")
                                            .toString());
                            map.put(
                                    "gojson",
                                    object3.getJSONArray(i)
                                            .getJSONObject(j)
                                            .get("gojson")
                                            .toString());
                            list.add(map);
                        }
                    }
                    elevtorchildData3.add(list);
                }

                flooradapter = new floorsAdapter();
                floorlistview.setAdapter(flooradapter);
                floorhashSet = new HashSet<String>();
                elevtoradapter = new elevtorAdapter();
                elevtorlistview.setAdapter(elevtoradapter);
                elevtorlistview.expandGroup(0);
                elevtorhashSet = new HashSet<String>();
                elevtoradapter2 = new elevtorAdapter2();
                elevtorlistview2.setAdapter(elevtoradapter2);
                elevtorlistview2.expandGroup(0);
                elevtoradapter3 = new elevtorAdapter3();
                elevtorlistview3.setAdapter(elevtoradapter3);
                elevtorlistview3.expandGroup(0);

                Log.e("floorchildDatacola", floorchildData.toString());
            }

        } catch (Exception e) {
            Log.e("885", e.toString());
        }
    }

    private void initView() {
        floorlistview = (ExpandableListView) findViewById(R.id.flooorlistview);
        elevtorlistview = (ExpandableListView) findViewById(R.id.elevatorlistview);
        elevtorlistview2 = (ExpandableListView) findViewById(R.id.elevatorlistview2);
        elevtorlistview3 = (ExpandableListView) findViewById(R.id.elevatorlistview3);
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

            final DoorAccess_floor_elevtor.ViewHolder holder;
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
                                            DoorAccess_floor_elevtor.this, R.layout.mylist_item);
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
            DoorAccess_floor_elevtor.ViewHolder holder = null;
            if (convertView == null) {
                holder = new DoorAccess_floor_elevtor.ViewHolder();
                convertView = View.inflate(context, R.layout.group_item, null);
                holder.groupText = (TextView) convertView.findViewById(R.id.id_group_text);
                holder.groupBox = (CheckBox) convertView.findViewById(R.id.id_group_checkbox);
                convertView.setTag(holder);
            } else {
                holder = (DoorAccess_floor_elevtor.ViewHolder) convertView.getTag();
            }

            holder.groupText.setText(floorparentList.get(groupPosition).get("groupText"));
            final String isGroupCheckd = floorparentList.get(groupPosition).get("isGroupCheckd");

            if ("No".equals(isGroupCheckd)) {
                holder.groupBox.setChecked(false);
            } else {
                holder.groupBox.setChecked(true);
            }
            long packedPosition = floorlistview.getPackedPositionForGroup(groupPosition);

            ArrayList selcetelevator = new ArrayList();
            for (int i = 0; i < elevtorchildData.size(); i++) {
                for (int j = 0; j < elevtorchildData.get(i).size(); j++) {
                    if (elevtorchildData.get(i).get(j).get("isSelected").equals("true")) {
                        selcetelevator.add(elevtorchildData.get(i).get(j).get("childItem"));
                    }
                }
            }
            for (int i = 0; i < elevtorchildData2.size(); i++) {
                for (int j = 0; j < elevtorchildData2.get(i).size(); j++) {
                    if (elevtorchildData2.get(i).get(j).get("isSelected").equals("true")) {
                        selcetelevator.add(elevtorchildData2.get(i).get(j).get("childItem"));
                    }
                }
            }
            for (int i = 0; i < elevtorchildData3.size(); i++) {
                for (int j = 0; j < elevtorchildData3.get(i).size(); j++) {
                    if (elevtorchildData3.get(i).get(j).get("isSelected").equals("true")) {
                        selcetelevator.add(elevtorchildData3.get(i).get(j).get("childItem"));
                    }
                }
            }

            String x = floorparentList.get(groupPosition).get("groupText");
            Log.e("x", x);
            Log.e("selcetelevator ", getGroup(groupPosition) + "");
            if (selcetelevator.indexOf(x) == -1) {
                convertView.setVisibility(View.GONE);
                floorlistview.collapseGroup(groupPosition);
                for (int i = 0; i < floorchildData.get(groupPosition).size(); i++) {
                    floorchildData.get(groupPosition).get(i).put("isSelected", "false");
                }
            } else {
                convertView.setVisibility(View.VISIBLE);
                floorlistview.expandGroup(groupPosition);
                // getGroup(groupPosition).setBackgroundColor(0xFF00FFFF);
            }

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
    private class elevtorAdapter extends BaseExpandableListAdapter {

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return elevtorchildData.get(groupPosition).get(childPosition);
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

            final DoorAccess_floor_elevtor.ViewHolder holder;
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
                    elevtorchildData.get(groupPosition).get(childPosition).get("childItem"));

            String isChecked =
                    elevtorchildData.get(groupPosition).get(childPosition).get("isChecked");
            if (elevtorchildData
                    .get(groupPosition)
                    .get(childPosition)
                    .get("isSelected")
                    .equals("false")) {
                convertView.setBackgroundColor(0x202127);
                flooradapter.notifyDataSetChanged();
            } else {
                convertView.setBackgroundColor(0xFF00FFFF);
                flooradapter.notifyDataSetChanged();
            }
            holder.TextID.setText(
                    elevtorchildData.get(groupPosition).get(childPosition).get("DeployDevice_Id"));
            holder.childText.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            member_adapter =
                                    new ArrayAdapter(
                                            DoorAccess_floor_elevtor.this, R.layout.mylist_item);
                            String get =
                                    elevtorchildData
                                            .get(groupPosition)
                                            .get(childPosition)
                                            .get("DeployDevice_Id");

                            Log.e("123", get);
                            if (elevtorchildData
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .get("isSelected")
                                    .equals("false")) {
                                elevtorchildData
                                        .get(groupPosition)
                                        .get(childPosition)
                                        .put("isSelected", "true");
                            } else {
                                elevtorchildData
                                        .get(groupPosition)
                                        .get(childPosition)
                                        .put("isSelected", "false");
                            }
                            elevtoradapter.notifyDataSetChanged();
                        }
                    });
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return elevtorchildData.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return elevtorparentList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return elevtorparentList.size();
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
            DoorAccess_floor_elevtor.ViewHolder holder = null;
            if (convertView == null) {
                holder = new DoorAccess_floor_elevtor.ViewHolder();

                convertView = View.inflate(context, R.layout.group_item, null);
                holder.groupText = (TextView) convertView.findViewById(R.id.id_group_text);
                holder.groupBox = (CheckBox) convertView.findViewById(R.id.id_group_checkbox);
                convertView.setTag(holder);
            } else {
                holder = (DoorAccess_floor_elevtor.ViewHolder) convertView.getTag();
            }
            holder.groupText.setText(elevtorparentList.get(groupPosition).get("groupText"));
            final String isGroupCheckd = elevtorparentList.get(groupPosition).get("isGroupCheckd");
            holder.groupText.setGravity(Gravity.CENTER);

            if ("No".equals(isGroupCheckd)) {
                holder.groupBox.setChecked(false);
            } else {
                holder.groupBox.setChecked(true);
            }

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

    private class elevtorAdapter2 extends BaseExpandableListAdapter {
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return elevtorchildData2.get(groupPosition).get(childPosition);
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

            final DoorAccess_floor_elevtor.ViewHolder holder;
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
                    elevtorchildData2.get(groupPosition).get(childPosition).get("childItem"));

            String isChecked =
                    elevtorchildData2.get(groupPosition).get(childPosition).get("isChecked");
            if (elevtorchildData2
                    .get(groupPosition)
                    .get(childPosition)
                    .get("isSelected")
                    .equals("false")) {
                convertView.setBackgroundColor(0x202127);
                flooradapter.notifyDataSetChanged();
            } else {
                convertView.setBackgroundColor(0xFF00FFFF);
                flooradapter.notifyDataSetChanged();
            }
            holder.TextID.setText(
                    elevtorchildData2.get(groupPosition).get(childPosition).get("DeployDevice_Id"));
            holder.childText.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            member_adapter =
                                    new ArrayAdapter(
                                            DoorAccess_floor_elevtor.this, R.layout.mylist_item);
                            String get =
                                    elevtorchildData2
                                            .get(groupPosition)
                                            .get(childPosition)
                                            .get("DeployDevice_Id");

                            Log.e("123", get);
                            if (elevtorchildData2
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .get("isSelected")
                                    .equals("false")) {
                                elevtorchildData2
                                        .get(groupPosition)
                                        .get(childPosition)
                                        .put("isSelected", "true");
                            } else {
                                elevtorchildData2
                                        .get(groupPosition)
                                        .get(childPosition)
                                        .put("isSelected", "false");
                            }
                            elevtoradapter2.notifyDataSetChanged();
                        }
                    });
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return elevtorchildData2.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return elevtorparentList2.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return elevtorparentList2.size();
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
            DoorAccess_floor_elevtor.ViewHolder holder = null;
            if (convertView == null) {
                holder = new DoorAccess_floor_elevtor.ViewHolder();

                convertView = View.inflate(context, R.layout.group_item, null);
                holder.groupText = (TextView) convertView.findViewById(R.id.id_group_text);
                holder.groupBox = (CheckBox) convertView.findViewById(R.id.id_group_checkbox);
                convertView.setTag(holder);
            } else {
                holder = (DoorAccess_floor_elevtor.ViewHolder) convertView.getTag();
            }
            holder.groupText.setText(elevtorparentList2.get(groupPosition).get("groupText"));
            final String isGroupCheckd = elevtorparentList2.get(groupPosition).get("isGroupCheckd");
            holder.groupText.setGravity(Gravity.CENTER);
            if ("No".equals(isGroupCheckd)) {
                holder.groupBox.setChecked(false);
            } else {
                holder.groupBox.setChecked(true);
            }

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

    private class elevtorAdapter3 extends BaseExpandableListAdapter {
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return elevtorchildData3.get(groupPosition).get(childPosition);
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

            final DoorAccess_floor_elevtor.ViewHolder holder;
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
                    elevtorchildData3.get(groupPosition).get(childPosition).get("childItem"));

            String isChecked =
                    elevtorchildData3.get(groupPosition).get(childPosition).get("isChecked");
            if (elevtorchildData3
                    .get(groupPosition)
                    .get(childPosition)
                    .get("isSelected")
                    .equals("false")) {
                convertView.setBackgroundColor(0x202127);
                flooradapter.notifyDataSetChanged();
            } else {
                convertView.setBackgroundColor(0xFF00FFFF);
                flooradapter.notifyDataSetChanged();
            }
            holder.TextID.setText(
                    elevtorchildData3.get(groupPosition).get(childPosition).get("DeployDevice_Id"));
            holder.childText.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            member_adapter =
                                    new ArrayAdapter(
                                            DoorAccess_floor_elevtor.this, R.layout.mylist_item);
                            String get =
                                    elevtorchildData3
                                            .get(groupPosition)
                                            .get(childPosition)
                                            .get("DeployDevice_Id");

                            Log.e("123", get);
                            if (elevtorchildData3
                                    .get(groupPosition)
                                    .get(childPosition)
                                    .get("isSelected")
                                    .equals("false")) {
                                elevtorchildData3
                                        .get(groupPosition)
                                        .get(childPosition)
                                        .put("isSelected", "true");
                            } else {
                                elevtorchildData3
                                        .get(groupPosition)
                                        .get(childPosition)
                                        .put("isSelected", "false");
                            }
                            elevtoradapter3.notifyDataSetChanged();
                        }
                    });
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return elevtorchildData3.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return elevtorparentList3.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return elevtorparentList3.size();
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
            DoorAccess_floor_elevtor.ViewHolder holder = null;
            if (convertView == null) {
                holder = new DoorAccess_floor_elevtor.ViewHolder();

                convertView = View.inflate(context, R.layout.group_item, null);
                holder.groupText = (TextView) convertView.findViewById(R.id.id_group_text);
                holder.groupBox = (CheckBox) convertView.findViewById(R.id.id_group_checkbox);
                convertView.setTag(holder);
            } else {
                holder = (DoorAccess_floor_elevtor.ViewHolder) convertView.getTag();
            }
            holder.groupText.setText(elevtorparentList3.get(groupPosition).get("groupText"));
            final String isGroupCheckd = elevtorparentList3.get(groupPosition).get("isGroupCheckd");
            holder.groupText.setGravity(Gravity.CENTER);
            if ("No".equals(isGroupCheckd)) {
                holder.groupBox.setChecked(false);
            } else {
                holder.groupBox.setChecked(true);
            }

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

    private void floorsdate() {
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
                                String.format(
                                        "0%10d",
                                        floorchildData.get(i).get(j).get("Dep_DeployDevice_Id")));
                        floors_confirmjson.add(go);
                    }
                }
            }
            for (int x = 0; x < floors_confirmjson.size(); x++) {
                floors_confirmjson.get(x).put("TimeZone", timezoneid);
            }
            Log.e("confirmjson", floors_confirmjson.toString());
        } catch (Exception e) {
            Log.e("1667", e.toString());
        }

        ArrayList<JSONObject> floorsdate = new ArrayList<>();
        ArrayList date_id = new ArrayList();
        for (int i = 0; i < floors_confirmjson.size(); i++) {
            try {
                String x =
                        date_id.indexOf(floors_confirmjson.get(i).getString("Dep_DeployDevice_Id"))
                                + "";
                Log.e("index", x);
                Log.e("date", date_id.toString());
                if (date_id.indexOf(floors_confirmjson.get(i).getString("Dep_DeployDevice_Id"))
                        == -1) {
                    date_id.add((floors_confirmjson.get(i).getString("Dep_DeployDevice_Id")));
                    JSONObject object1 = new JSONObject();
                    object1.put(
                            "device_id",
                            floors_confirmjson.get(i).getString("Dep_DeployDevice_Id"));
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
                            Integer.parseInt(
                                    floors_confirmjson.get(i).getString("DeployDevice_No")),
                            floors_confirmjson.get(i).getString("TimeZone"));
                    object1.put("doors", new JSONArray(list));

                    floorsdate.add(object1);
                } else {
                    JSONArray test =
                            floorsdate
                                    .get(
                                            date_id.indexOf(
                                                    floors_confirmjson
                                                            .get(i)
                                                            .getString("Dep_DeployDevice_Id")))
                                    .getJSONArray("doors");
                    test.put(
                            Integer.parseInt(
                                    floors_confirmjson.get(i).getString("DeployDevice_No")),
                            floors_confirmjson.get(i).getString("TimeZone"));

                    Log.e("R", test.toString());
                }

            } catch (Exception e) {
                Log.e("erreos", e.toString());
            }
        }
        Log.e("floorsdate", floorsdate.toString());

        try {
            JSONArray doors_access = new JSONArray();
            for (int i = 0; i < floorsdate.size(); i++) {
                doors_access.put(floorsdate.get(i));
            }
            Log.e("fkdoors_access0", doors_access.toString());

            JSONArray doors_selectinfo = new JSONArray();
            for (int i = 0; i < floors_confirmjson.size(); i++) {
                doors_selectinfo.put(floors_confirmjson.get(i));
            }

            for (int i = 0; i < doors_access.length(); i++) {
                for (int j = 0; j < accessinfo.length(); j++) {
                    JSONObject object = new JSONObject();
                    object = accessinfo.getJSONObject(j);
                    Log.e("fkdoors_access", doors_access.toString());
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
            /* Intent intent = new Intent();
            intent.putExtra("allinfo", allinfo.toString());
            intent.setClass(DoorAccess_floor_elevtor.this, DoorAccess_elevator.class);
            startActivity(intent);*/

        } catch (Exception e) {
            Log.e("errors", e.toString());
        }
    }

    private void elevatordate() {
        select_json.clear();
        try {
            for (int i = 0; i < elevtorchildData.size(); i++) {
                for (int j = 0; j < elevtorchildData.get(i).size(); j++) {
                    if (elevtorchildData.get(i).get(j).get("isSelected").equals("true")) {
                        JSONObject go = new JSONObject();
                        go.put(
                                "DeployDevice_No",
                                elevtorchildData.get(i).get(j).get("DeployDevice_No"));
                        go.put("DeployDeviceName", elevtorchildData.get(i).get(j).get("childItem"));
                        go.put(
                                "DeployDevice_Id",
                                elevtorchildData.get(i).get(j).get("DeployDevice_Id"));
                        go.put(
                                "Dep_DeployDevice_Id",
                                elevtorchildData.get(i).get(j).get("Dep_DeployDevice_Id"));
                        select_json.add(go);
                    }
                }
            }
            if (select_json.size() == 0) {
                Toast.makeText(DoorAccess_floor_elevtor.this, "請至少選擇一個電梯樓層", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            for (int x = 0; x < select_json.size(); x++) {
                select_json.get(x).put("TimeZone", timezoneid);
            }
            Log.e("select_json", select_json.toString());
        } catch (Exception e) {
            Log.e("1848", e.toString());
        }
        Button next = findViewById(R.id.submit);
        ArrayList<JSONObject> elevatordate = new ArrayList<>();
        ArrayList date_id = new ArrayList();
        next.setEnabled(false);

        for (int i = 0; i < select_json.size(); i++) {
            try {
                String x =
                        date_id.indexOf(select_json.get(i).getString("Dep_DeployDevice_Id")) + "";
                if (date_id.indexOf(select_json.get(i).getString("Dep_DeployDevice_Id")) == -1) {
                    date_id.add((select_json.get(i).getString("Dep_DeployDevice_Id")));
                    JSONObject object1 = new JSONObject();
                    object1.put("device_id", select_json.get(i).getString("Dep_DeployDevice_Id"));
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
            for (int x = 0; x < elevatordate.size(); x++) {
                JSONArray z = elevatordate.get(x).getJSONArray("floors");
                JSONObject object2 = elevatordate.get(x);
                int hex = 0;
                for (int y = 0; y < z.length(); y++) {
                    hex = hex | (int) Math.pow(2, Double.parseDouble(z.get(y).toString()));
                }
                JSONObject object3 = elevatordate.get(x);
                String hexstring = Integer.toHexString(hex);
                object3.put("floors_hex", String.format("%02x", hex) + ",00,00,00,00,00,00,00");
            }
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        // Log.e("confirmjson", confirmjson.toString());
        Log.e("elevatordate", elevatordate.toString());
        try {
            // String cominginfo = getIntent().getExtras().getString("allinfo");
            JSONArray elevator_access = new JSONArray();
            /*for (int i = 0; i < elevatordate.size(); i++) {
                elevator_access.put(elevatordate.get(i));
            }*/
            JSONArray elevator_selectinfo = new JSONArray();
            /* for (int i = 0; i < confirmjson.size(); i++) {
                elevator_selectinfo.put(confirmjson.get(i));
            }*/
            // 門禁授權需要資料
            allinfo.put("elevator_access", elevator_access);
            // 搜尋時回傳資料(結果值 右邊部分)
            allinfo.put("elevator_selectinfo", elevator_selectinfo);
            // 搜尋時回傳資料(所選樓層 左邊及中間部分)
            JSONObject accessinfo = new JSONObject();
            accessinfo.put("elevtorparentList", elevtorparentList);
            accessinfo.put("elevtorparentList2", elevtorparentList2);
            accessinfo.put("elevtorparentList3", elevtorparentList3);
            accessinfo.put("floorparentList", floorparentList);
            accessinfo.put("elevtorchildData", elevtorchildData);
            accessinfo.put("elevtorchildData2", elevtorchildData2);
            accessinfo.put("elevtorchildData3", elevtorchildData3);
            accessinfo.put("floorchildData", floorchildData);
            accessinfo.put("timezoneid", timezoneid);
            // accessinfo.put("comfirm_ele", comfirm_ele);
            // accessinfo.put("comfirm_elejson", comfirm_elejson);
            allinfo.put("elevator_accessinfo", accessinfo);
            Log.e("allinfo", allinfo.toString());

            URL url = new URL("http://" + DomainIP + "/riway/api/v1/access/allow/card");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url.toURI());
            StringEntity params = new StringEntity(allinfo.toString(), "UTF-8");
            Log.e("params", allinfo.toString());
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(params);
            // Prepare JSON to send by
            // setting the entity
            HttpResponse response = httpClient.execute(httpPost);
            String json_string = EntityUtils.toString(response.getEntity());
            Log.e("json_string", json_string);

            JSONObject temp1 = new JSONObject(json_string);
            String data = temp1.getString("errors");
            Log.e("end", data);
            if (data.equals("")) {
                // 若print值為2則列印qrcode
                if (allinfo.getString("print").equals("2")) {
                    try { // 用卡號查詢QRCODE
                        HttpGet httpGet =
                                new HttpGet(
                                        "http://"
                                                + DomainIP
                                                + "/riway/api/v1/clients/temporary/qrcode/"
                                                + allinfo.getJSONArray("cards").getString(0));
                        HttpClient httpClient2 = new DefaultHttpClient();
                        httpClient2
                                .getParams()
                                .setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
                        httpClient2.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
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
                                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        SharedPreferences sPrefs = getSharedPreferences("printer", MODE_PRIVATE);
                        final String printIP = sPrefs.getString("IP", "");
                        EscPosPrinter printer =
                                new EscPosPrinter(
                                        new TcpConnection(printIP, 9100),
                                        203,
                                        48f,
                                        32,
                                        new EscPosCharsetEncoding("Big5", 0));
                        printer.printFormattedText("[C]<u><font size='big'>門禁QRCODE</font></u>\n")
                                .printFormattedText(
                                        "[R]<img>"
                                                + PrinterTextParserImg.bitmapToHexadecimalString(
                                                        printer, decodedByte)
                                                + "</img>\n");
                        // 切紙指令
                        printer.printFormattedTextAndCut("");
                        printer.disconnectPrinter();
                        next.setEnabled(true);
                        Toast.makeText(DoorAccess_floor_elevtor.this, "授權成功", Toast.LENGTH_SHORT)
                                .show();
                        next.setEnabled(true);
                        Intent intent = new Intent();
                        intent.setClass(DoorAccess_floor_elevtor.this, ApporvedList.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(
                                        DoorAccess_floor_elevtor.this,
                                        "請確認IP以及熱感機連線狀態",
                                        Toast.LENGTH_SHORT)
                                .show();
                        next.setEnabled(true);
                    }

                } else {
                    next.setEnabled(true);
                    Toast.makeText(DoorAccess_floor_elevtor.this, "授權成功", Toast.LENGTH_SHORT)
                            .show();
                    next.setEnabled(true);
                    Intent intent = new Intent();
                    intent.setClass(DoorAccess_floor_elevtor.this, ApporvedList.class);
                    startActivity(intent);
                }

            } else {
                Toast.makeText(DoorAccess_floor_elevtor.this, data.toString(), Toast.LENGTH_SHORT)
                        .show();
                next.setEnabled(true);
            }

        } catch (Exception e) {
            Log.e("errorss", e.toString());
            next.setEnabled(true);
        }
    }

    private void setListViewHeight(ExpandableListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        int count = listAdapter.getCount();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
