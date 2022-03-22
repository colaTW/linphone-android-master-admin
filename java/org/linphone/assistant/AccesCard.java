package org.linphone.assistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URL;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.linphone.R;

public class AccesCard extends Activity {
    private ArrayList<Group> gData = null;
    private ArrayList<ArrayList<ClipData.Item>> iData = null;
    private ArrayList<ClipData.Item> lData = null;
    private Context mContext;
    private ExpandableListView list;
    ArrayList<String> cardtype_list = new ArrayList<>();
    ListView register_list;
    final ArrayList select_type_name = new ArrayList<>();
    Spinner select_type;
    EditText select_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accescard);
        /*  ImageButton memberdata = findViewById(R.id.memberdata);
        ImageButton dooracess = findViewById(R.id.dooracess);
        ImageButton approvedlist = findViewById(R.id.approvedList);
        ImageButton accescard = findViewById(R.id.AccessCard);*/
        SharedPreferences sPrefs = getSharedPreferences("Domain", MODE_PRIVATE);
        final String DomainIP = sPrefs.getString("IP", "");
        Button next = findViewById(R.id.next);
        final Spinner cardtype = findViewById(R.id.cardtype);
        final EditText name = findViewById(R.id.name);
        final EditText phone = findViewById(R.id.phone);
        final EditText remark = findViewById(R.id.remark);
        ListView register_list = findViewById(R.id.registerlist);
        final Spinner select_type = findViewById(R.id.select_type);
        Button select_button = findViewById(R.id.select);

        select_type_name.add("電話");
        select_type_name.add("姓名");
        cardtype_list.add("實體卡");
        cardtype_list.add("QRcode");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(AccesCard.this, R.layout.myspinner_item, select_type_name);
        adapter.setDropDownViewResource(R.layout.myspinner_dropitem);
        select_type.setAdapter(adapter);
        ArrayAdapter<String> adapter1 =
                new ArrayAdapter<String>(this, R.layout.myspinner_item, cardtype_list);
        adapter1.setDropDownViewResource(R.layout.myspinner_dropitem);
        cardtype.setAdapter(adapter1);
        cardtype.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> adapterView, View view, int position, long l) {}

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
        getlist();

        select_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getlist();
                    }
                });
        next.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkinfo()) {
                            // 登記臨時卡
                            try {
                                JSONObject temporaryinfo = new JSONObject();
                                URL url =
                                        new URL(
                                                "http://"
                                                        + DomainIP
                                                        + "/riway/api/v1/clients/temporary/card");
                                HttpClient httpClient = new DefaultHttpClient();
                                HttpPost httpPost = new HttpPost(url.toURI());
                                temporaryinfo.put(
                                        "card_type", cardtype.getSelectedItemPosition() + 1 + "");
                                temporaryinfo.put("client_name", name.getText().toString());
                                temporaryinfo.put("client_mobile", phone.getText().toString());
                                temporaryinfo.put("memo", remark.getText().toString());
                                StringEntity params =
                                        new StringEntity(temporaryinfo.toString(), "UTF-8");
                                Log.e("params", temporaryinfo.toString());
                                httpPost.addHeader("content-type", "application/json");
                                httpPost.setEntity(params);
                                // Prepare JSON to send by
                                // setting the entity
                                HttpResponse response = httpClient.execute(httpPost);
                                String json_string = EntityUtils.toString(response.getEntity());
                                Log.e("json_string", json_string);
                                JSONObject temp1 = new JSONObject(json_string);
                                String data = temp1.getString("code");
                                // 無錯誤碼進到授權頁面
                                if (data.equals("0")) {
                                    JSONObject memberinfo = new JSONObject();
                                    final ArrayList confirm_name = new ArrayList<>();
                                    JSONArray cards = new JSONArray();
                                    cards.put(temp1.getString("card_number"));
                                    confirm_name.add(temp1.getString("card_number"));
                                    Log.e("confirm_name", confirm_name.toString());
                                    try {
                                        memberinfo.put("cards", cards);
                                        memberinfo.put("group_id", "");
                                        memberinfo.put("group_name", "");
                                        memberinfo.put("member_name", confirm_name);
                                        memberinfo.put(
                                                "print", cardtype.getSelectedItemPosition() + 1);
                                        memberinfo.put("getaccess", "0");

                                    } catch (Exception e) {
                                        Log.e("error", e.toString());
                                    }
                                    Intent intent = new Intent();
                                    intent.putExtra("memberinfo", memberinfo.toString());
                                    intent.setClass(AccesCard.this, DoorAccess_floor_elevtor.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(
                                                    AccesCard.this,
                                                    temp1.getString("errors"),
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }

                            } catch (Exception e) {
                                Toast.makeText(AccesCard.this, e.toString(), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                });

        register_list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, final int position, long id) {
                        final String cardNumber;
                        TextView txt = (TextView) view.findViewById(R.id.card_number);
                        cardNumber = txt.getText().toString();
                        final AlertDialog.Builder dialog;
                        dialog = new AlertDialog.Builder(AccesCard.this);
                        dialog.setTitle("確認歸還卡片");
                        dialog.setMessage("卡片卡號:" + cardNumber);
                        dialog.setPositiveButton(
                                "確認",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        try {
                                            JSONObject info = new JSONObject();
                                            URL url =
                                                    new URL(
                                                            "http://"
                                                                    + DomainIP
                                                                    + "/riway/api/v1/clients/temporary/unbind/card");
                                            HttpClient httpClient = new DefaultHttpClient();
                                            HttpPost httpPost = new HttpPost(url.toURI());
                                            info.put("cardNumber", cardNumber);
                                            StringEntity params =
                                                    new StringEntity(info.toString(), "UTF-8");
                                            httpPost.addHeader("content-type", "application/json");
                                            httpPost.setEntity(params);
                                            // Prepare JSON to send by
                                            // setting the entity
                                            HttpResponse response = httpClient.execute(httpPost);
                                            String json_string =
                                                    EntityUtils.toString(response.getEntity());
                                            Log.e("json_string", json_string);
                                            JSONObject temp1 = new JSONObject(json_string);
                                            String data = temp1.getString("errors");
                                            if (data.equals("")) {
                                                Toast.makeText(
                                                                AccesCard.this,
                                                                "歸還成功",
                                                                Toast.LENGTH_SHORT)
                                                        .show();
                                                getlist();

                                            } else {
                                                Toast.makeText(
                                                                AccesCard.this,
                                                                data,
                                                                Toast.LENGTH_SHORT)
                                                        .show();
                                            }

                                        } catch (Exception e) {
                                            Toast.makeText(
                                                            AccesCard.this,
                                                            e.toString(),
                                                            Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    }
                                });
                        dialog.setNegativeButton(
                                "取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {}
                                });
                        dialog.show();
                        Log.e("stringText", cardNumber);
                    }
                });
        /* memberdata.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(AccesCard.this, memberdatabase.class);
                        startActivity(intent);
                    }
                });
        dooracess.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(AccesCard.this, DoorAccess.class);
                        startActivity(intent);
                    }
                });
        approvedlist.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(AccesCard.this, ApporvedList.class);
                        startActivity(intent);
                    }
                });
        accescard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(AccesCard.this, AccesCard.class);
                        startActivity(intent);
                    }
                });*/
    }

    boolean checkinfo() {
        EditText name = findViewById(R.id.name);
        EditText phone = findViewById(R.id.phone);
        if (name.getText().toString().equals("")) {
            Toast.makeText(AccesCard.this, "請填入名字", Toast.LENGTH_SHORT).show();

        } else if (phone.getText().toString().equals("")) {
            Toast.makeText(AccesCard.this, "請填入電話", Toast.LENGTH_SHORT).show();

        } else {
            return true;
        }
        return false;
    }

    void getlist() {
        SharedPreferences sPrefs = getSharedPreferences("Domain", MODE_PRIVATE);
        final String DomainIP = sPrefs.getString("IP", "");
        Log.e("DomainIP", DomainIP);
        select_type = findViewById(R.id.select_type);
        select_name = findViewById(R.id.select_name);
        ListView register_list = findViewById(R.id.registerlist);
        try {
            JSONObject body = new JSONObject();
            URL url = new URL("http://" + DomainIP + "/riway/api/v1/clients/temporary/card/list");

            if (select_type.getSelectedItem().toString().equals("姓名")) {
                body.put("name", select_name.getText().toString());
            } else if (select_type.getSelectedItem().toString().equals("電話")) {
                body.put("mobile", select_name.getText().toString());
            }
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url.toURI());
            StringEntity params = new StringEntity(body.toString(), "UTF-8");
            httpPost.addHeader("content-type", "application/json");
            httpPost.setEntity(params);
            // Prepare JSON to send by
            // setting the entity
            HttpResponse response = httpClient.execute(httpPost);
            String json_string = EntityUtils.toString(response.getEntity());
            Log.e("json_string", json_string);
            JSONArray temp1 = new JSONArray(json_string);
            List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
            Log.e("123", "231");

            for (int i = 0; i < temp1.length(); i++) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                if (temp1.getJSONObject(i).getString("card_type").equals("1")) {
                    item.put("card_type", "實體卡");
                } else {
                    item.put("card_type", "QRcode");
                }
                item.put("card_number", temp1.getJSONObject(i).getString("card_number"));
                item.put("client_name", temp1.getJSONObject(i).getString("client_name"));
                item.put("client_mobile", temp1.getJSONObject(i).getString("client_mobile"));
                item.put("memo", temp1.getJSONObject(i).getString("memo"));
                data.add(item);
            }

            SimpleAdapter adapter =
                    new SimpleAdapter(
                            this,
                            data,
                            R.layout.register_listitem,
                            new String[] {
                                "card_type", "card_number", "client_name", "client_mobile", "memo"
                            },
                            new int[] {
                                R.id.card_type,
                                R.id.card_number,
                                R.id.client_name,
                                R.id.client_mobile,
                                R.id.memo
                            });

            // ArrayAdapter member_adapter = new ArrayAdapter(this, R.layout.mylist_item,new
            // String[] {"name", "status"},new int[] {R.id.name, R.id.status});
            // member_adapter.addAll(member_name);
            register_list.setAdapter(adapter);

        } catch (Exception e) {
            Toast.makeText(AccesCard.this, e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("ffff", e.toString());
        }
    }
}
