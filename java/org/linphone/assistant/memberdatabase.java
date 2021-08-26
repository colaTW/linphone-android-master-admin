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
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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
import org.linphone.activities.DialerActivity;

public class memberdatabase extends Activity {

    private ExpandableListView grouplist;
    Spinner groupLv1;
    Spinner groupLv2;
    final ArrayList name = new ArrayList<>();
    final ArrayList id = new ArrayList<>();
    private HashSet<String> hashSet;
    private List<Map<String, String>> parentList = new ArrayList<Map<String, String>>();
    private List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
    private MyAdapter adapter;
    ArrayAdapter<String> adapterLv1;
    ArrayAdapter<String> adapterLv2;
    ArrayList<String> Lv1 = new ArrayList<>();
    final ArrayList<ArrayList<String>> Lv2 = new ArrayList<>();
    String nextdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memberdatabase);
        ImageButton goBA = findViewById(R.id.B_BA);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton goguard = findViewById(R.id.B_Guard);
        Button dooracess = findViewById(R.id.dooracess);
        Button approvedlist = findViewById(R.id.approvedList);
        Button accescard = findViewById(R.id.AccessCard);
        RadioButton cardbutton = findViewById(R.id.card);
        RadioButton QRbutton = findViewById(R.id.QRcode);
        Button sendButton = findViewById(R.id.sendButton);
        final LinearLayout cardlayout = findViewById(R.id.cardlayout);
        final EditText nametext = findViewById(R.id.Editname);
        final EditText phone = findViewById(R.id.Editphone);
        final ProgressBar sendspiner = findViewById(R.id.sendspiner);
        sendspiner.setVisibility(View.INVISIBLE);
        sendspiner.setVisibility(View.VISIBLE);

        QRbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cardlayout.setVisibility(View.INVISIBLE);
                    }
                });
        cardbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cardlayout.setVisibility(View.VISIBLE);
                    }
                });

        sendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendspiner.setVisibility(View.VISIBLE);
                        new Thread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                runOnUiThread(
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    URL url =
                                                                            new URL(
                                                                                    "http://18.181.171.107/riway/api/v1/clients/main/one");
                                                                    JSONObject jo =
                                                                            new JSONObject();
                                                                    jo.put(
                                                                            "name",
                                                                            nametext.getText()
                                                                                    .toString());
                                                                    jo.put(
                                                                            "phone",
                                                                            phone.getText()
                                                                                    .toString());
                                                                    jo.put(
                                                                            "mobile",
                                                                            phone.getText()
                                                                                    .toString());
                                                                    HttpClient httpClient =
                                                                            new DefaultHttpClient();
                                                                    AbstractHttpEntity entity =
                                                                            new ByteArrayEntity(
                                                                                    jo.toString()
                                                                                            .getBytes(
                                                                                                    "UTF8"));
                                                                    entity.setContentType(
                                                                            new BasicHeader(
                                                                                    HTTP.CONTENT_TYPE,
                                                                                    "application/json"));
                                                                    HttpPost httpPost =
                                                                            new HttpPost(
                                                                                    url.toURI());
                                                                    httpPost.setEntity(entity);
                                                                    // Prepare JSON to send by
                                                                    // setting the entity
                                                                    HttpResponse response =
                                                                            httpClient.execute(
                                                                                    httpPost);
                                                                    String json_string =
                                                                            EntityUtils.toString(
                                                                                    response
                                                                                            .getEntity());
                                                                    JSONObject temp1 =
                                                                            new JSONObject(
                                                                                    json_string);
                                                                    JSONObject data =
                                                                            temp1.getJSONObject(
                                                                                    "data");
                                                                    String error =
                                                                            data.getString(
                                                                                    "errors");
                                                                    if (error.equals("")) {
                                                                        nametext.setText("");
                                                                        phone.setText("");
                                                                        sendspiner.setVisibility(
                                                                                View.GONE);

                                                                        Toast.makeText(
                                                                                        memberdatabase
                                                                                                .this,
                                                                                        "新增成功",
                                                                                        Toast
                                                                                                .LENGTH_LONG)
                                                                                .show();
                                                                    } else {
                                                                        Toast.makeText(
                                                                                        memberdatabase
                                                                                                .this,
                                                                                        error
                                                                                                .toString(),
                                                                                        Toast
                                                                                                .LENGTH_SHORT)
                                                                                .show();
                                                                    }
                                                                } catch (Exception e) {
                                                                    Toast.makeText(
                                                                                    memberdatabase
                                                                                            .this,
                                                                                    e.toString(),
                                                                                    Toast
                                                                                            .LENGTH_SHORT)
                                                                            .show();
                                                                }
                                                                sendspiner.setVisibility(View.GONE);
                                                            }
                                                        });
                                            }
                                        })
                                .start();
                    }
                });

        goBA.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(memberdatabase.this, BApage.class);
                        startActivity(intent);
                    }
                });

        gocall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(memberdatabase.this, DialerActivity.class);
                        startActivity(intent);
                    }
                });
        goguard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(memberdatabase.this, Guardpage.class);
                        startActivity(intent);
                    }
                });
        dooracess.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(memberdatabase.this, DoorAccess.class);
                        startActivity(intent);
                    }
                });
        approvedlist.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(memberdatabase.this, ApporvedList.class);
                        startActivity(intent);
                    }
                });
        accescard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(memberdatabase.this, AccesCard.class);
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
                                                sendspiner.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        })
                .start();
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
                        Intent intent = new Intent();
                        intent.setClass(memberdatabase.this, member_modify.class);
                        intent.putExtra("groupid", no.toString());
                        intent.putExtra("data", nextdata);
                        startActivity(intent);

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
            HttpGet httpGet = new HttpGet("http://18.181.171.107/riway/api/v1/clients/departments");
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity responseHttpEntity = response.getEntity();
            String code = EntityUtils.toString(response.getEntity());
            JSONObject temp1 = new JSONObject(code);
            JSONArray array1 = temp1.getJSONArray("data");
            nextdata = array1.toString();
            for (int x = 0; x < array1.length(); x++) {
                JSONObject str_value = array1.getJSONObject(x);
                groups.add(str_value.getString("name"));
                Lv1.add(str_value.getString("name"));
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
                    groups2.add(test);
                } else {
                    ArrayList<Map<String, String>> test = new ArrayList<Map<String, String>>();
                    ArrayList<String> test2 = new ArrayList<>();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name", "");
                    map.put("ID", "");
                    test.add(map);
                    test2.add("");
                    groups2.add(test);
                    Lv2.add(test2);
                }
            }

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
            adapter = new memberdatabase.MyAdapter();
            grouplist.setAdapter(adapter);
            grouplist.expandGroup(0);
            hashSet = new HashSet<String>();
            ArrayAdapter<String> adapterLv1 =
                    new ArrayAdapter<String>(this, R.layout.myspinner_item, Lv1);
            adapterLv2 = new ArrayAdapter<String>(this, R.layout.myspinner_item, Lv2.get(1));
            groupLv1.setAdapter(adapterLv1);
            groupLv2.setAdapter(adapterLv2);

        } catch (Exception e) {
            Toast.makeText(memberdatabase.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        groupLv1.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> adapterView, View view, int position, long l) {
                        adapterLv2 =
                                new ArrayAdapter<String>(
                                        memberdatabase.this,
                                        R.layout.myspinner_item,
                                        Lv2.get(position));
                        groupLv2.setAdapter(adapterLv2);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
    }

    private void initView() {
        grouplist = (ExpandableListView) findViewById(R.id.grouplist);
        groupLv1 = findViewById(R.id.groupLv1);
        groupLv2 = findViewById(R.id.groupLv2);
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

            final memberdatabase.ViewHolder holder;
            if (convertView == null) {
                holder = new memberdatabase.ViewHolder();
                convertView =
                        View.inflate(memberdatabase.this, R.layout.listview_item_noradio, null);
                holder.childText = (TextView) convertView.findViewById(R.id.id_text);
                holder.TextID = (TextView) convertView.findViewById(R.id.text_ID);
                convertView.setTag(holder);
            } else {
                holder = (memberdatabase.ViewHolder) convertView.getTag();
            }
            holder.childText.setText(
                    childData.get(groupPosition).get(childPosition).get("childItem"));
            String isChecked = childData.get(groupPosition).get(childPosition).get("isChecked");
            holder.TextID.setText(childData.get(groupPosition).get(childPosition).get("ID"));
            holder.childText.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setClass(memberdatabase.this, member_modify.class);
                            intent.putExtra("groupid", holder.TextID.getText().toString());
                            intent.putExtra("data", nextdata);
                            startActivity(intent);
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
            memberdatabase.ViewHolder holder = null;
            if (convertView == null) {
                holder = new memberdatabase.ViewHolder();
                convertView = View.inflate(memberdatabase.this, R.layout.group_item, null);
                holder.groupText = (TextView) convertView.findViewById(R.id.id_group_text);
                holder.groupBox = (CheckBox) convertView.findViewById(R.id.id_group_checkbox);
                convertView.setTag(holder);

            } else {
                holder = (memberdatabase.ViewHolder) convertView.getTag();
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
