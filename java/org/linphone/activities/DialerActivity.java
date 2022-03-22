package org.linphone.activities;

/*
DialerActivity.java
Copyright (C) 2019 Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.core.app.NotificationCompat;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.linphone.LinphoneManager;
import org.linphone.R;
import org.linphone.WebSocket.JWebSocketClient;
import org.linphone.assistant.BApage;
import org.linphone.call.CallActivity;
import org.linphone.contacts.ContactsActivity;
import org.linphone.contacts.ContactsManager;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.tools.Log;
import org.linphone.views.AddressText;
import org.linphone.views.CallButton;
import org.linphone.views.Digit;
import org.linphone.views.EraseButton;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import static android.app.Notification.DEFAULT_VIBRATE;
import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

public class DialerActivity extends MainActivity implements AddressText.AddressChangedListener {
    private static final String ACTION_CALL_LINPHONE = "org.linphone.intent.action.CallLaunched";
    private AddressText mAddress;
    private CallButton mStartCall, mAddCall, mTransferCall;
    private ImageView mAddContact, mBackToCall, gohome, alarm, alarm2;
    private boolean mIsTransfer;
    private CoreListenerStub mListener;
    private boolean mInterfaceLoaded;
    private String data2;
    private String tiltle;
    private String content;
    private String icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // txt_uuid = (TextView) findViewById(R.id.txt_uuid);
        //  txt = (TextView) findViewById(R.id.txt_hello);

        Toast.makeText(DialerActivity.this, "Connected!!", Toast.LENGTH_SHORT).show();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // URI uri = URI.create("ws://121.40.165.18:8800");
        URI uri = URI.create("ws://192.168.53.163:7878/ShowNum");

        JWebSocketClient client =
                new JWebSocketClient(uri) {
                    @Override
                    public void onMessage(String message) {
                        // message就是接收到的訊息
                        android.util.Log.e("onMessage", "123456" + message);
                        notificaioncall("危險警報", message, "warning");
                    }
                };
        try {
            client.connect();
        } catch (Exception e) {
            android.util.Log.e("error", e.toString());
        }

        String userID = "";
        try {
            userID = getSharedPreferences("info", MODE_PRIVATE).getString("user", "");
            URL url =
                    new URL(
                            "http://"
                                    + getSharedPreferences("info", MODE_PRIVATE)
                                            .getString("domain", "")
                                    + ":8888/api/v1/household/device/login");
            JSONObject jo = new JSONObject();
            jo.put(
                    "device_uuid",
                    Settings.Secure.getString(
                            getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
            jo.put("password", getSharedPreferences("info", MODE_PRIVATE).getInt("birth", 0));
            HttpClient httpClient = new DefaultHttpClient();
            AbstractHttpEntity entity = new ByteArrayEntity(jo.toString().getBytes("UTF8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            HttpPost httpPost = new HttpPost(url.toURI());
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            String json_string = EntityUtils.toString(response.getEntity());
            JSONObject temp1 = new JSONObject(json_string);
            JSONObject data = temp1.getJSONObject("data");
            String token = data.getString("access_token");
            String status = temp1.getString("status");
            if (status.equals("success")) {
                System.out.print("succes");
                SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
                pref.edit().putString("token", token).commit();
            }
        } catch (Exception e) {
            System.out.print("出錯了" + e);
        }

        if (mAbortCreation) {
            return;
        }
        // testpassword test = new testpassword();
        // test.change();

        mInterfaceLoaded = false;
        // Uses the fragment container layout to inflate the dialer view instead of using a fragment
        new AsyncLayoutInflater(this)
                .inflate(
                        R.layout.dialer,
                        null,
                        new AsyncLayoutInflater.OnInflateFinishedListener() {
                            @Override
                            public void onInflateFinished(
                                    @NonNull View view, int resid, @Nullable ViewGroup parent) {
                                LinearLayout fragmentContainer =
                                        findViewById(R.id.fragmentContainer);
                                LinearLayout.LayoutParams params =
                                        new LinearLayout.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.MATCH_PARENT);
                                fragmentContainer.addView(view, params);
                                initUI(view);
                                mInterfaceLoaded = true;
                            }
                        });

        if (isTablet()) {
            findViewById(R.id.fragmentContainer2).setVisibility(View.GONE);
        }

        mListener =
                new CoreListenerStub() {
                    @Override
                    public void onCallStateChanged(
                            Core core, Call call, Call.State state, String message) {
                        updateLayout();
                    }
                };

        // On dialer we ask for all permissions
        mPermissionsToHave =
                new String[] {
                    // This one is to allow floating notifications
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    // Required starting Android 9 to be able to start a foreground service
                    "android.permission.FOREGROUND_SERVICE",
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_CONTACTS
                };

        handleIntentParams(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntentParams(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDialerSelected.setVisibility(View.VISIBLE);

        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.addListener(mListener);
        }

        if (mInterfaceLoaded) {
            updateLayout();
            //  enableVideoPreviewIfTablet(true);
        }
    }

    @Override
    protected void onPause() {
        // enableVideoPreviewIfTablet(false);
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.removeListener(mListener);
        }
        super.onPause();
    }

    private void initUI(View view) {
        mAddress = view.findViewById(R.id.address);
        mAddress.setAddressListener(this);
        mAddress.setInputType(InputType.TYPE_NULL);

        EraseButton erase = view.findViewById(R.id.erase);
        erase.setAddressWidget(mAddress);

        mStartCall = view.findViewById(R.id.start_call2);
        mStartCall.setAddressWidget(mAddress);

        mAddCall = view.findViewById(R.id.add_call);
        mAddCall.setAddressWidget(mAddress);

        mTransferCall = view.findViewById(R.id.transfer_call);
        mTransferCall.setAddressWidget(mAddress);
        mTransferCall.setIsTransfer(true);

        mAddContact = view.findViewById(R.id.add_contact);
        mAddContact.setEnabled(false);

        alarm = findViewById(R.id.alarm);
        alarm2 = findViewById(R.id.alarm2);
        alarm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /*String Guard = "";

                        try {
                            Guard =
                                    getSharedPreferences("info", MODE_PRIVATE)
                                            .getString("guard", "");
                            System.out.print(Guard);

                        } catch (Exception e) {

                        }*/
                        mAddress.setText("lin9141047801112");
                        mStartCall.performClick();
                        mAddress.setText("");
                    }
                });
        alarm2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                });

        mAddContact.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DialerActivity.this, ContactsActivity.class);
                        intent.putExtra("EditOnClick", true);
                        intent.putExtra("SipAddress", mAddress.getText().toString());
                        startActivity(intent);
                    }
                });

        mBackToCall = view.findViewById(R.id.back_to_call);
        mBackToCall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DialerActivity.this, CallActivity.class));
                    }
                });
        gohome = view.findViewById(R.id.B_home);
        gohome.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sPrefs = getSharedPreferences("Domain", MODE_PRIVATE);
                        String DomainIP = sPrefs.getString("IP", "");
                        android.util.Log.e("DomainIP", DomainIP);
                        if (DomainIP == "") {
                            IPsettingdialog();
                        } else {
                             startActivity(new Intent(DialerActivity.this, BApage.class));
                        }
                    }
                });
        gohome.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        // TODO Auto-generated method stub
                        IPsettingdialog();
                        return true;
                    }
                });

        mIsTransfer = false;
        if (getIntent() != null) {
            mIsTransfer = getIntent().getBooleanExtra("Transfer", false);
            mAddress.setText(getIntent().getStringExtra("SipUri"));
        }

        setUpNumpad(view);
        updateLayout();
        // enableVideoPreviewIfTablet(true);
    }

    /* private void enableVideoPreviewIfTablet(boolean enable) {
        Core core = LinphoneManager.getCore();
        TextureView preview = findViewById(R.id.video_preview);
        if (preview != null && core != null) {
            if (enable && isTablet() && LinphonePreferences.instance().isVideoPreviewEnabled()) {
                preview.setVisibility(View.VISIBLE);
                core.setNativePreviewWindowId(preview);
                core.enableVideoPreview(true);

                ImageView changeCamera = findViewById(R.id.video_preview_change_camera);
                if (changeCamera != null && core.getVideoDevicesList().length > 1) {
                    changeCamera.setVisibility(View.VISIBLE);
                    changeCamera.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LinphoneManager.getCallManager().switchCamera();
                                }
                            });
                }
            } else {
                preview.setVisibility(View.GONE);
                core.setNativePreviewWindowId(null);
                core.enableVideoPreview(false);
            }
        }
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("isTransfer", mIsTransfer);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mIsTransfer = savedInstanceState.getBoolean("isTransfer");
    }

    @Override
    public void onAddressChanged() {
        Core core = LinphoneManager.getCore();
        mAddContact.setEnabled(
                core != null && core.getCallsNb() > 0 || !mAddress.getText().toString().equals(""));
    }

    private void updateLayout() {
        Core core = LinphoneManager.getCore();
        if (core == null) {
            return;
        }

        boolean atLeastOneCall = core.getCallsNb() > 0;
        // mStartCall.setVisibility(atLeastOneCall ? View.GONE : View.VISIBLE);
        mAddContact.setVisibility(atLeastOneCall ? View.GONE : View.VISIBLE);
        // if (!atLeastOneCall) {
        //   if (core.getVideoActivationPolicy().getAutomaticallyInitiate()) {
        //      mStartCall.setImageResource(R.drawable.call_video_start);
        // } else {
        //    mStartCall.setImageResource(R.drawable.call_audio_start);
        // }
        // }

        mBackToCall.setVisibility(atLeastOneCall ? View.VISIBLE : View.GONE);
        mAddCall.setVisibility(atLeastOneCall && !mIsTransfer ? View.GONE : View.GONE);
        mTransferCall.setVisibility(atLeastOneCall && mIsTransfer ? View.VISIBLE : View.GONE);
    }

    private void handleIntentParams(Intent intent) {
        if (intent == null) return;

        String action = intent.getAction();
        String addressToCall = null;
        if (ACTION_CALL_LINPHONE.equals(action)
                && (intent.getStringExtra("NumberToCall") != null)) {
            String numberToCall = intent.getStringExtra("NumberToCall");
            Log.i("[Dialer] ACTION_CALL_LINPHONE with number: " + numberToCall);
            LinphoneManager.getCallManager().newOutgoingCall(numberToCall, null);
        } else {
            Uri uri = intent.getData();
            if (uri != null) {
                Log.i("[Dialer] Intent data is: " + uri.toString());
                if (Intent.ACTION_CALL.equals(action)) {
                    addressToCall = intent.getData().toString();
                    addressToCall = addressToCall.replace("%40", "@");
                    addressToCall = addressToCall.replace("%3A", ":");
                    if (addressToCall.startsWith("sip:")) {
                        addressToCall = addressToCall.substring("sip:".length());
                    } else if (addressToCall.startsWith("tel:")) {
                        addressToCall = addressToCall.substring("tel:".length());
                    }
                    Log.i("[Dialer] ACTION_CALL with number: " + addressToCall);
                } else {
                    addressToCall =
                            ContactsManager.getInstance()
                                    .getAddressOrNumberForAndroidContact(getContentResolver(), uri);
                    Log.i("[Dialer] " + action + " with number: " + addressToCall);
                }
            } else {
                Log.w("[Dialer] Intent data is null for action " + action);
            }
        }

        if (addressToCall != null) {
            mAddress.setText(addressToCall);
        }
    }

    private void setUpNumpad(View view) {
        if (view == null) return;
        for (Digit v : retrieveChildren((ViewGroup) view, Digit.class)) {
            v.setAddressWidget(mAddress);
        }
    }

    private <T> Collection<T> retrieveChildren(ViewGroup viewGroup, Class<T> clazz) {
        final Collection<T> views = new ArrayList<>();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                views.addAll(retrieveChildren((ViewGroup) v, clazz));
            } else {
                if (clazz.isInstance(v)) views.add(clazz.cast(v));
            }
        }
        return views;
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public String getIdentity() {
        SharedPreferences preference =
                PreferenceManager.getDefaultSharedPreferences(DialerActivity.this);
        String identity = preference.getString("identity", null);
        if (identity == null) {
            identity = java.util.UUID.randomUUID().toString();
            preference.edit().putString("identity", identity).commit();
        }
        return identity;
    }

    private void notificaioncall(String ContentTitle, String ContentText, String newicon) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "cola";
            String description = "colatest";
            int importance =
                    NotificationManager.IMPORTANCE_HIGH; // Important for heads-up notification
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, BApage.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "1")
                        .setSmallIcon(R.drawable.onfire)
                        .setContentTitle(ContentTitle)
                        .setContentText(ContentText)
                        .setLargeIcon(
                                BitmapFactory.decodeResource(getResources(), R.drawable.onfire))
                        .setDefaults(DEFAULT_VIBRATE) // Important for heads-up notification
                        .setContentIntent(pendingIntent)
                        .setPriority(
                                Notification.PRIORITY_MAX); // Important for heads-up notification
        Notification buildNotification = mBuilder.build();
        NotificationManager mNotifyMgr =
                (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(001, buildNotification);
    }

    void IPsettingdialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DialerActivity.this);
        View view = getLayoutInflater().inflate(R.layout.domainsetting, null);
        final EditText IP = view.findViewById(R.id.domainIP);
        SharedPreferences sPrefs = getSharedPreferences("Domain", MODE_PRIVATE);
        final String DomainIP = sPrefs.getString("IP", "");
        IP.setText(DomainIP);
        alertDialog.setPositiveButton(
                "確認",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            SharedPreferences sPrefs = getSharedPreferences("Domain", MODE_PRIVATE);
                            sPrefs.edit().putString("IP", IP.getText().toString()).commit();
                            /* SharedPreferences.Editor editor = sPrefs.edit(); // 获取Editor对象
                            editor.putString("IP", IP.getText().toString()); // 存储数据
                            editor.commit();*/
                        } catch (Exception e) {
                            Toast.makeText(DialerActivity.this, "請確認IP連線狀態", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });

        alertDialog.setTitle("DomainIP設定");
        alertDialog.setView(view);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }
}
