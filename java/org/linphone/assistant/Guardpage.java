package org.linphone.assistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.net.URL;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

public class Guardpage extends Activity {
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
    final ArrayList select_type_name = new ArrayList<>();
    private List<Map<String, String>> parentList = new ArrayList<Map<String, String>>();
    private List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
    private HashSet<String> hashSet;
    int now = 1;
    int totalcount = 0;
    private ListView memberlist;
    private TextView totalpage;
    String department = "";
    private TextView nowpage;
    String is_push_all_notification = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guardpage);
        ImageButton goBA = findViewById(R.id.B_BA);
        ImageButton godoor = findViewById(R.id.B_door);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton goguard = findViewById(R.id.B_Guard);
        ImageButton memberdata = findViewById(R.id.memberdata);
        ImageButton dooracess = findViewById(R.id.dooracess);
        ImageButton approvedlist = findViewById(R.id.approvedList);
        ImageButton accescard = findViewById(R.id.AccessCard);
        ImageButton IPsetting = findViewById(R.id.IPsetting);
        final TextView nowpage = findViewById(R.id.nowpage);
        final Button lastpage = findViewById(R.id.lastpage);
        Button nextpage = findViewById(R.id.nextpage);
        final ListView memberlist = findViewById(R.id.memberlist);
        final TextView monday_starttime = findViewById(R.id.monday_starttime);
        final TextView monday_endtime = findViewById(R.id.monday_endtime);
        final TextView tuesday_starttime = findViewById(R.id.tuesday_starttime);
        final TextView tuesday_endtime = findViewById(R.id.tuesday_endtime);
        final TextView wednesday_starttime = findViewById(R.id.wednesday_starttime);
        final TextView wednesday_endtime = findViewById(R.id.wednesday_endtime);
        final TextView thursday_starttime = findViewById(R.id.thursday_starttime);
        final TextView thursday_endtime = findViewById(R.id.thursday_endtime);
        final TextView friday_starttime = findViewById(R.id.friday_starttime);
        final TextView friday_endtime = findViewById(R.id.friday_endtime);
        final TextView saturday_starttime = findViewById(R.id.saturday_starttime);
        final TextView saturday_endtime = findViewById(R.id.saturday_endtime);
        final TextView sunday_starttime = findViewById(R.id.sunday_starttime);
        final TextView sunday_endtime = findViewById(R.id.sunday_endtime);
        final Switch isnotify = findViewById(R.id.isnotify);
        final SimpleDateFormat df = new SimpleDateFormat("hh:mm");
        Button apply = findViewById(R.id.apply);
        Button addnew = findViewById(R.id.addnew);

        goBA.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(Guardpage.this, BApage.class);
                        startActivity(intent);
                    }
                });

        gocall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(Guardpage.this, DialerActivity.class);
                        startActivity(intent);
                    }
                });

        godoor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(Guardpage.this, memberdatabase.class);
                        startActivity(intent);
                    }
                });
        goBA.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(Guardpage.this, BApage.class);
                        startActivity(intent);
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
        IPsetting.setOnClickListener(
                new View.OnClickListener() {
                    long[] mHits = new long[3];

                    @Override
                    public void onClick(View view) {
                        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                        if (mHits[0] >= (SystemClock.uptimeMillis() - 800)) {
                            IPsettingdialog();
                        }
                    }
                });
        addnew.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(Guardpage.this, Guardpage_addnew.class);
                        startActivity(intent);
                    }
                });
        apply.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Date start1 = df.parse(monday_starttime.getText().toString());
                            Date end1 = df.parse(monday_endtime.getText().toString());
                            Date start2 = df.parse(tuesday_starttime.getText().toString());
                            Date end2 = df.parse(tuesday_endtime.getText().toString());
                            Date start3 = df.parse(wednesday_starttime.getText().toString());
                            Date end3 = df.parse(wednesday_endtime.getText().toString());
                            Date start4 = df.parse(thursday_starttime.getText().toString());
                            Date end4 = df.parse(thursday_endtime.getText().toString());
                            Date start5 = df.parse(friday_starttime.getText().toString());
                            Date end5 = df.parse(friday_endtime.getText().toString());
                            Date start6 = df.parse(saturday_starttime.getText().toString());
                            Date end6 = df.parse(saturday_endtime.getText().toString());
                            Date start7 = df.parse(sunday_starttime.getText().toString());
                            Date end7 = df.parse(sunday_endtime.getText().toString());
                            if (start1.after(end1)) {
                                Toast.makeText(
                                                Guardpage.this,
                                                "星期一的結束時間需大於起始時間",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else if (start2.after(end2)) {
                                Toast.makeText(
                                                Guardpage.this,
                                                "星期二的結束時間需大於起始時間",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else if (start3.after(end3)) {
                                Toast.makeText(
                                                Guardpage.this,
                                                "星期三的結束時間需大於起始時間",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else if (start4.after(end4)) {
                                Toast.makeText(
                                                Guardpage.this,
                                                "星期四的結束時間需大於起始時間",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else if (start5.after(end5)) {
                                Toast.makeText(
                                                Guardpage.this,
                                                "星期五的結束時間需大於起始時間",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else if (start6.after(end6)) {
                                Toast.makeText(
                                                Guardpage.this,
                                                "星期六的結束時間需大於起始時間",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else if (start7.after(end7)) {
                                Toast.makeText(
                                                Guardpage.this,
                                                "星期日的結束時間需大於起始時間",
                                                Toast.LENGTH_SHORT)
                                        .show();

                            } else {
                                URL url =
                                        new URL(
                                                "http://18.181.171.107/riway/api/v1/push/notification/setting");
                                JSONObject notificationinfo = new JSONObject();
                                notificationinfo.put(
                                        "is_push_all_notification", is_push_all_notification);
                                notificationinfo.put(
                                        "push_notification_time_monday",
                                        monday_starttime.getText().toString()
                                                + "-"
                                                + monday_endtime.getText().toString());
                                notificationinfo.put(
                                        "push_notification_time_tuesday",
                                        tuesday_starttime.getText().toString()
                                                + "-"
                                                + tuesday_endtime.getText().toString());
                                notificationinfo.put(
                                        "push_notification_time_wednesday",
                                        wednesday_starttime.getText().toString()
                                                + "-"
                                                + wednesday_endtime.getText().toString());
                                notificationinfo.put(
                                        "push_notification_time_thursday",
                                        thursday_starttime.getText().toString()
                                                + "-"
                                                + thursday_endtime.getText().toString());
                                notificationinfo.put(
                                        "push_notification_time_friday",
                                        friday_starttime.getText().toString()
                                                + "-"
                                                + friday_endtime.getText().toString());
                                notificationinfo.put(
                                        "push_notification_time_saturday",
                                        saturday_starttime.getText().toString()
                                                + "-"
                                                + saturday_endtime.getText().toString());
                                notificationinfo.put(
                                        "push_notification_time_sunday",
                                        saturday_starttime.getText().toString()
                                                + "-"
                                                + saturday_endtime.getText().toString());
                                HttpClient httpClient = new DefaultHttpClient();
                                AbstractHttpEntity entity =
                                        new ByteArrayEntity(
                                                notificationinfo.toString().getBytes("UTF8"));
                                entity.setContentType(
                                        new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                                HttpPost httpPost = new HttpPost(url.toURI());
                                httpPost.setEntity(entity);
                                // Prepare JSON to send by setting the entity
                                HttpResponse response = httpClient.execute(httpPost);
                                String code = EntityUtils.toString(response.getEntity());
                                JSONObject temp1 = new JSONObject(code);
                                Log.e("notificationinfo", notificationinfo.toString());
                                if (temp1.getString("errors").equals("")) {
                                    Toast.makeText(Guardpage.this, "設定完成", Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    Toast.makeText(
                                                    Guardpage.this,
                                                    temp1.getString("errors"),
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }

                        } catch (Exception e) {
                            Log.e("Exception", e.toString());
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
                                                getpage(1, "0");
                                            }
                                        });
                            }
                        })
                .start();
        try {
            HttpGet httpGet =
                    new HttpGet("http://18.181.171.107/riway/api/v1/push/notification/setting");
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity responseHttpEntity = response.getEntity();
            String code = EntityUtils.toString(response.getEntity());
            JSONObject temp1 = new JSONObject(code);
            JSONObject array1 = temp1.getJSONObject("data");
            String day1 = array1.getString("push_notification_time_monday");
            String day2 = array1.getString("push_notification_time_tuesday");
            String day3 = array1.getString("push_notification_time_wednesday");
            String day4 = array1.getString("push_notification_time_thursday");
            String day5 = array1.getString("push_notification_time_friday");
            String day6 = array1.getString("push_notification_time_saturday");
            String day7 = array1.getString("push_notification_time_sunday");
            String[] split;
            split = day1.split("-");
            monday_starttime.setText(split[0]);
            monday_endtime.setText(split[1]);
            split = day2.split("-");
            tuesday_starttime.setText(split[0]);
            tuesday_endtime.setText(split[1]);
            split = day3.split("-");
            wednesday_starttime.setText(split[0]);
            wednesday_endtime.setText(split[1]);
            split = day4.split("-");
            thursday_starttime.setText(split[0]);
            thursday_endtime.setText(split[1]);
            split = day5.split("-");
            friday_starttime.setText(split[0]);
            friday_endtime.setText(split[1]);
            split = day6.split("-");
            saturday_starttime.setText(split[0]);
            saturday_endtime.setText(split[1]);
            split = day7.split("-");
            sunday_starttime.setText(split[0]);
            sunday_endtime.setText(split[1]);

            is_push_all_notification = array1.getString("is_push_all_notification");
            if (is_push_all_notification.equals("1")) {
                isnotify.setChecked(true);
            } else {
                isnotify.setChecked(false);
            }

        } catch (Exception e) {
            Log.e("erroes678", e.toString());
        }

        isnotify.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            is_push_all_notification = "1";
                        } else {
                            is_push_all_notification = "0";
                        }
                    }
                });
        monday_starttime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                monday_starttime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        monday_endtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                monday_endtime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        tuesday_starttime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                tuesday_starttime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        tuesday_endtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                tuesday_endtime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        wednesday_starttime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                wednesday_starttime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        wednesday_endtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                wednesday_endtime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        thursday_starttime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                thursday_starttime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        thursday_endtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                thursday_endtime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        friday_starttime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                friday_starttime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        friday_endtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                friday_endtime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        saturday_starttime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                saturday_starttime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        saturday_endtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                saturday_endtime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        sunday_starttime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                sunday_starttime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
                    }
                });
        sunday_endtime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(
                                        Guardpage.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view, int hourOfDay, int minute) {
                                                String timeString =
                                                        String.format(
                                                                "%02d:%02d", hourOfDay, minute);
                                                sunday_endtime.setText(timeString);
                                            }
                                        },
                                        hour,
                                        minute,
                                        false)
                                .show();
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
            body.put("is_push_notification", 1);
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
            List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
            Log.e("123", "231");

            for (int i = 0; i < array2.length(); i++) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                JSONObject jsonObject = array2.getJSONObject(i);
                item.put("name", jsonObject.getString("name"));
                item.put("mobile", jsonObject.getString("mobile"));

                if (jsonObject.getString("is_push_notification").equals("1")) {
                    item.put("status", "推播中");

                } else {
                    item.put("status", "未推播");
                }
                data.add(item);

                member_id.add(jsonObject.getString("card_number"));
            }
            SimpleAdapter adapter =
                    new SimpleAdapter(
                            this,
                            data,
                            R.layout.guard_listitem,
                            new String[] {"name", "mobile", "status"},
                            new int[] {R.id.name, R.id.mobile, R.id.status});

            // ArrayAdapter member_adapter = new ArrayAdapter(this, R.layout.mylist_item,new
            // String[] {"name", "status"},new int[] {R.id.name, R.id.status});
            // member_adapter.addAll(member_name);
            memberlist.setAdapter(adapter);

        } catch (Exception e) {
            Toast.makeText(Guardpage.this, "here" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private class ViewHolder {
        TextView groupText, childText, TextID;
        CheckBox groupBox;
    }

    void IPsettingdialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Guardpage.this);
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
                            SharedPreferences sPrefs =
                                    getSharedPreferences("printer", MODE_PRIVATE);
                            sPrefs.edit().putString("IP", IP.getText().toString()).commit();
                            /* SharedPreferences.Editor editor = sPrefs.edit(); // 获取Editor对象
                            editor.putString("IP", IP.getText().toString()); // 存储数据
                            editor.commit();*/
                        } catch (Exception e) {
                            Toast.makeText(Guardpage.this, "請確認IP以及熱感機連線狀態", Toast.LENGTH_SHORT)
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
