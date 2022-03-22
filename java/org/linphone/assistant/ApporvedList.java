package org.linphone.assistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.dantsu.escposprinter.EscPosCharsetEncoding;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import java.net.URL;
import java.security.acl.Group;
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

public class ApporvedList extends Activity {
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
    private List<Map<String, String>> parentList = new ArrayList<Map<String, String>>();
    private List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
    private ApporvedList.MyAdapter adapter;
    private ExpandableListView grouplist;
    private HashSet<String> hashSet;
    int now = 1;
    int totalcount = 0;
    private ListView memberlist;
    private TextView totalpage;
    private Button nextpage;
    private Button lastpage;
    String department = "";
    private TextView nowpage;
    final ArrayList select_type_name = new ArrayList<>();
    private Spinner select_type;
    EditText select_name;
    SimpleAdapter member_adapter;
    List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
    String checkedmember = "";
    String checkedmembername = "";
    String DomainIP = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apporvedlist);
        ImageButton goBA = findViewById(R.id.B_BA);
        ImageButton godoor = findViewById(R.id.B_door);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton goguard = findViewById(R.id.B_Guard);
        ImageButton memberdata = findViewById(R.id.memberdata);
        ImageButton dooracess = findViewById(R.id.dooracess);
        ImageButton approvedlist = findViewById(R.id.approvedList);
        ImageButton accescard = findViewById(R.id.AccessCard);
        ImageButton longtime = findViewById(R.id.Longtime);
        Button returnback = findViewById(R.id.returnback);
        Button gomain = findViewById(R.id.gomain);
        final Button lastpage = findViewById(R.id.lastpage);
        Button nextpage = findViewById(R.id.nextpage);
        final TextView nowpage = findViewById(R.id.nowpage);
        final ListView memberlist = findViewById(R.id.memberlist);
        final Spinner select_group = findViewById(R.id.select_group);
        final Spinner select_type = findViewById(R.id.select_type);
        Button select_button = findViewById(R.id.select);
        final TextView totalpage = findViewById(R.id.totalpage);
        Button print = findViewById(R.id.print);
        Button selectaccess = findViewById(R.id.selectaccess);
        SharedPreferences sPrefs = getSharedPreferences("Domain", MODE_PRIVATE);
        DomainIP = sPrefs.getString("IP", "");
        select_group_name.add("全部");
        select_group_id.add("0");
        select_type_name.add("電話");
        select_type_name.add("姓名");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        ApporvedList.this, R.layout.myspinner_item, select_type_name);
        adapter.setDropDownViewResource(R.layout.myspinner_dropitem);
        select_type.setAdapter(adapter);
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
        returnback.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
        gomain.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(ApporvedList.this, BApage.class);
                        startActivity(intent);
                    }
                });

        memberdata.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(ApporvedList.this, memberdatabase.class);
                        startActivity(intent);
                    }
                });
        dooracess.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(ApporvedList.this, DoorAccess.class);
                        startActivity(intent);
                    }
                });
        approvedlist.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(ApporvedList.this, ApporvedList.class);
                        startActivity(intent);
                    }
                });
        accescard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(ApporvedList.this, AccesCard.class);
                        startActivity(intent);
                    }
                });
        longtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(ApporvedList.this, DoorAccess_longtime.class);
                        startActivity(intent);
                    }
                });
        select_button.setOnClickListener(
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
                            JSONArray array2 = array1.getJSONArray("data");
                            data = new ArrayList<HashMap<String, Object>>();
                            for (int i = 0; i < array2.length(); i++) {
                                HashMap<String, Object> item = new HashMap<String, Object>();
                                JSONObject jsonObject = array2.getJSONObject(i);
                                item.put("name", jsonObject.getString("name"));
                                item.put("mobile", jsonObject.getString("mobile"));
                                item.put("isSelected", false);
                                item.put("card_number", jsonObject.getString("card_number"));
                                member_name.add(jsonObject.getString("name"));
                                member_id.add(jsonObject.getString("card_number"));
                                data.add(item);
                            }
                            member_adapter =
                                    new SimpleAdapter(
                                            ApporvedList.this,
                                            data,
                                            R.layout.member_listitem_checkbox,
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
                                            CheckedTextView checkedTextView =
                                                    (CheckedTextView)
                                                            newView.findViewById(R.id.mobile);
                                            if (data.get(position)
                                                    .get("isSelected")
                                                    .toString()
                                                    .equals("false")) {
                                                checkedTextView.setChecked(false);
                                            } else {
                                                checkedTextView.setChecked(true);
                                            }

                                            return newView;
                                        }
                                    };
                            memberlist.setAdapter(member_adapter);
                            memberlist.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

                        } catch (Exception e) {
                            Toast.makeText(ApporvedList.this, e.toString(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
        memberlist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, final int position, long id) {
                        for (int i = 0; i < data.size(); i++) {
                            data.get(i).put("isSelected", false);
                        }
                        if (data.get(position).get("isSelected").toString().equals("false")) {
                            data.get(position).put("isSelected", true);
                            checkedmember = data.get(position).get("card_number").toString();
                        } else {
                            data.get(position).put("isSelected", false);
                        }
                        member_adapter.notifyDataSetChanged();
                    }
                });
        print.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            // 用卡號查詢QRCODE
                            HttpGet httpGet =
                                    new HttpGet(
                                            "http://"
                                                    + DomainIP
                                                    + "/riway/api/v1/clients/temporary/qrcode/"
                                                    + checkedmember);
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
                            final String pureBase64Encoded;
                            pureBase64Encoded =
                                    encodedString.substring(encodedString.indexOf(",") + 1);
                            byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
                            Bitmap decodedByte =
                                    BitmapFactory.decodeByteArray(
                                            decodedBytes, 0, decodedBytes.length);
                            try {
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

                            } catch (Exception e) {
                                Toast.makeText(
                                                ApporvedList.this,
                                                "請確認IP以及熱感機連線狀態",
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(ApporvedList.this, e.toString(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
        selectaccess.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            confirm_name.clear();
                            JSONObject memberinfo = new JSONObject();
                            JSONArray cards = new JSONArray();
                            cards.put(checkedmember);
                            confirm_name.add(checkedmember);
                            JSONArray group_id = new JSONArray();
                            memberinfo.put("cards", cards);
                            memberinfo.put("group_id", group_id);
                            memberinfo.put("group_name", confirm_group_name);
                            memberinfo.put("getaccess", "1");
                            memberinfo.put("print", "0");
                            HttpGet httpGet =
                                    new HttpGet(
                                            "http://"
                                                    + DomainIP
                                                    + "/riway/api/v1/access/allow/card/"
                                                    + checkedmember);
                            httpGet.addHeader("Content-Type", "application/json");
                            HttpClient httpClient = new DefaultHttpClient();
                            httpClient
                                    .getParams()
                                    .setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
                            httpClient
                                    .getParams()
                                    .setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
                            HttpResponse response = httpClient.execute(httpGet);
                            HttpEntity responseHttpEntity = response.getEntity();
                            String respone = EntityUtils.toString(responseHttpEntity);
                            JSONObject temp1 = new JSONObject(respone);

                            Log.e("respone", respone.toString());

                            Intent intent = new Intent();
                            memberinfo.put("member_name", temp1.getString("client_name"));

                            intent.putExtra("memberinfo", memberinfo.toString());
                            intent.putExtra("selectinfo", respone.toString());
                            Log.e("selectinfo", respone.toString());
                            intent.setClass(ApporvedList.this, DoorAccess_floor_elevtor.class);
                            startActivity(intent);

                        } catch (Exception e) {
                            Log.e("Exception", e.toString());
                        }
                    }
                });
        /* memberlist.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent, View view, final int position, long id) {

                Log.e("member_id.get(position)", member_id.get(position).toString());
                AlertDialog.Builder dialog;
                dialog = new AlertDialog.Builder(ApporvedList.this);
                dialog.setTitle("請選擇需要的服務");
                dialog.setNegativeButton(
                        "查詢/修改",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {}
                        });
                dialog.setPositiveButton(
                        "列印QRCODE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                try {
                                    // 用卡號查詢QRCODE
                                    HttpGet httpGet =
                                            new HttpGet(
                                                    "http://18.181.171.107/riway/api/v1/clients/temporary/qrcode/"
                                                            + member_id
                                                                    .get(position)
                                                                    .toString());
                                    HttpClient httpClient2 = new DefaultHttpClient();
                                    httpClient2
                                            .getParams()
                                            .setParameter(
                                                    CoreConnectionPNames.CONNECTION_TIMEOUT,
                                                    2000);
                                    httpClient2
                                            .getParams()
                                            .setParameter(
                                                    CoreConnectionPNames.SO_TIMEOUT, 2000);
                                    HttpResponse response2 = httpClient2.execute(httpGet);
                                    HttpEntity responseHttpEntity2 = response2.getEntity();
                                    String code =
                                            EntityUtils.toString(response2.getEntity());
                                    JSONObject temp2 = new JSONObject(code);
                                    // 取得BASE64碼並影印
                                    String encodedString = temp2.getString("qrcode");
                                    final String pureBase64Encoded;
                                    pureBase64Encoded =
                                            encodedString.substring(
                                                    encodedString.indexOf(",") + 1);
                                    byte[] decodedBytes =
                                            Base64.decode(
                                                    pureBase64Encoded, Base64.DEFAULT);
                                    Bitmap decodedByte =
                                            BitmapFactory.decodeByteArray(
                                                    decodedBytes, 0, decodedBytes.length);
                                    try {
                                        SharedPreferences sPrefs =
                                                getSharedPreferences(
                                                        "printer", MODE_PRIVATE);
                                        final String printIP = sPrefs.getString("IP", "");
                                        EscPosPrinter printer =
                                                new EscPosPrinter(
                                                        new TcpConnection(printIP, 9100),
                                                        203,
                                                        48f,
                                                        32,
                                                        new EscPosCharsetEncoding(
                                                                "Big5", 0));
                                        printer.printFormattedText(
                                                        "[C]<u><font size='big'>門禁QRCODE</font></u>\n")
                                                .printFormattedText(
                                                        "[R]<img>"
                                                                + PrinterTextParserImg
                                                                        .bitmapToHexadecimalString(
                                                                                printer,
                                                                                decodedByte)
                                                                + "</img>\n");
                                        // 切紙指令
                                        printer.printFormattedTextAndCut("");
                                        printer.disconnectPrinter();

                                    } catch (Exception e) {
                                        Toast.makeText(
                                                        ApporvedList.this,
                                                        "請確認IP以及熱感機連線狀態",
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(
                                                    ApporvedList.this,
                                                    e.toString(),
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
                dialog.show();
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
                                                ArrayAdapter<String> adapterLv1 =
                                                        new ArrayAdapter<String>(
                                                                ApporvedList.this,
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
    }

    void getpage(int page, String department) {
        lastpage = findViewById(R.id.lastpage);
        nextpage = findViewById(R.id.nextpage);
        memberlist = (ListView) findViewById(R.id.memberlist);
        totalpage = (TextView) findViewById(R.id.totalpage);
        select_type = findViewById(R.id.select_type);
        select_name = findViewById(R.id.select_name);
        member_name.clear();
        member_id.clear();
        if (now == 1) {
            lastpage.setVisibility(View.INVISIBLE);
        } else {
            lastpage.setVisibility(View.VISIBLE);
        }
        if (now == totalcount) {
            nextpage.setVisibility(View.INVISIBLE);
        } else {
            nextpage.setVisibility(View.VISIBLE);
        }

        try {
            URL url = new URL("http://" + DomainIP + "/riway/api/v1/clients/main/list");
            JSONObject body = new JSONObject();
            body.put("page", page);
            body.put("department", department);
            Log.e("getSelectedItem", select_type.getSelectedItem().toString());
            if (select_type.getSelectedItem().toString().equals("姓名")) {
                body.put("name", select_name.getText().toString());
            } else if (select_type.getSelectedItem().toString().equals("電話")) {
                body.put("mobile", select_name.getText().toString());
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
            data = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < array2.length(); i++) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                JSONObject jsonObject = array2.getJSONObject(i);
                item.put("name", jsonObject.getString("name"));
                item.put("mobile", jsonObject.getString("mobile"));
                item.put("card_number", jsonObject.getString("card_number"));
                item.put("isSelected", false);
                member_name.add(jsonObject.getString("name"));
                member_id.add(jsonObject.getString("card_number"));
                data.add(item);
            }
            member_adapter =
                    new SimpleAdapter(
                            ApporvedList.this,
                            data,
                            R.layout.member_listitem_checkbox,
                            new String[] {
                                "name", "mobile",
                            },
                            new int[] {
                                R.id.name, R.id.mobile,
                            }) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            final View newView = super.getView(position, convertView, parent);
                            CheckedTextView checkedTextView =
                                    (CheckedTextView) newView.findViewById(R.id.mobile);
                            if (data.get(position).get("isSelected").toString().equals("false")) {
                                checkedTextView.setChecked(false);
                            } else {
                                checkedTextView.setChecked(true);
                            }

                            return newView;
                        }
                    };
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
                        select_name.setText("");
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
            adapter = new ApporvedList.MyAdapter();
            grouplist.setAdapter(adapter);
            grouplist.expandGroup(0);
            hashSet = new HashSet<String>();

        } catch (Exception e) {
            Toast.makeText(ApporvedList.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        grouplist = (ExpandableListView) findViewById(R.id.grouplist);
        nowpage = findViewById(R.id.nowpage);
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

            final ApporvedList.ViewHolder holder;
            if (convertView == null) {
                holder = new ApporvedList.ViewHolder();
                convertView = View.inflate(ApporvedList.this, R.layout.listview_item_noradio, null);
                holder.childText = (TextView) convertView.findViewById(R.id.id_text);
                holder.TextID = (TextView) convertView.findViewById(R.id.text_ID);
                convertView.setTag(holder);
            } else {
                holder = (ApporvedList.ViewHolder) convertView.getTag();
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
            ApporvedList.ViewHolder holder = null;
            if (convertView == null) {
                holder = new ApporvedList.ViewHolder();
                convertView = View.inflate(ApporvedList.this, R.layout.group_item, null);
                holder.groupText = (TextView) convertView.findViewById(R.id.id_group_text);
                holder.groupBox = (CheckBox) convertView.findViewById(R.id.id_group_checkbox);
                convertView.setTag(holder);
            } else {
                holder = (ApporvedList.ViewHolder) convertView.getTag();
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

    void showdailog(final Bitmap QRdata) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ApporvedList.this);
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
                            sPrefs.edit().putString("IP", IP.getText().toString()).commit();
                            /* SharedPreferences.Editor editor = sPrefs.edit(); // 获取Editor对象
                            editor.putString("IP", IP.getText().toString()); // 存储数据
                            editor.commit();*/
                        } catch (Exception e) {
                            Toast.makeText(ApporvedList.this, "請確認IP以及熱感機連線狀態", Toast.LENGTH_SHORT)
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
