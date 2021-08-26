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
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

public class DialerActivity extends MainActivity implements AddressText.AddressChangedListener {
    private static final String ACTION_CALL_LINPHONE = "org.linphone.intent.action.CallLaunched";

    private AddressText mAddress;
    private CallButton mStartCall, mAddCall, mTransferCall;
    private ImageView mAddContact, mBackToCall, gohome, alarm;

    private boolean mIsTransfer;
    private CoreListenerStub mListener;
    private boolean mInterfaceLoaded;
    private NotificationManagerCompat notificationManager;
    private Socket mSocket;
    private String data2;
    private String tiltle;
    private String content;
    private String icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManager = NotificationManagerCompat.from(this);

        // txt_uuid = (TextView) findViewById(R.id.txt_uuid);
        //  txt = (TextView) findViewById(R.id.txt_hello);
        RatKiller app = (RatKiller) getApplication();
        mSocket = app.getmSocket();
        mSocket.connect();
        Toast.makeText(DialerActivity.this, "Connected!!", Toast.LENGTH_SHORT).show();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // mSocket.emit("login", "53326483");
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

        mSocket.emit("login", userID);
        //  txt_uuid.setText("Login UUID:\n"+UUID);
        Toast.makeText(DialerActivity.this, "Login ID:" + userID, Toast.LENGTH_SHORT).show();

        mSocket.on(
                "new_msg2",
                new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        // txt.setText("接收資料");
                        android.util.Log.e("123", "data:" + args[0]);
                        String test = args[0].toString();
                        System.out.println(test);
                        // String data_ = "{'title':'公告1','content':'訊息測試'}";
                        final JSONObject jsonObj;

                        try {
                            jsonObj = new JSONObject(test);
                            tiltle = jsonObj.getString("title");
                            content = jsonObj.getString("content");
                            icon = jsonObj.getString("icon");

                        } catch (Exception e) {
                        }

                        data2 = args[0].toString();
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        // Toast.makeText(MainActivity.this, data2,
                                        // Toast.LENGTH_SHORT).show();
                                        // whatever your UI logic
                                        notificaioncall(tiltle, content, icon);
                                        Toast.makeText(
                                                        DialerActivity.this,
                                                        "新消息",
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                    }
                });
        mSocket.on(
                "dio",
                new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        // txt.setText("接收資料");
                        android.util.Log.e("12345", "data:" + args[0]);
                        String data = args[0].toString();
                        System.out.println(data);
                        // String data_ = "{'title':'公告1','content':'訊息測試'}";
                        final JSONObject jsonObj;

                        try {
                            jsonObj = new JSONObject(data);
                            // tiltle = jsonObj.getString("title");
                            // content = jsonObj.getString("content");
                            icon = "OoO";
                            System.out.println("watch here" + jsonObj.has("icon"));

                        } catch (Exception e) {
                        }

                        data2 = args[0].toString();
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        // Toast.makeText(MainActivity.this, data2,
                                        // Toast.LENGTH_SHORT).show();
                                        // whatever your UI logic
                                        notificaioncall("危險警報", data2, "test");
                                        Toast.makeText(
                                                        DialerActivity.this,
                                                        "新消息2",
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                    }
                });

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
        alarm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Guard = "";
                        try {
                            Guard =
                                    getSharedPreferences("info", MODE_PRIVATE)
                                            .getString("guard", "");
                            System.out.print(Guard);

                        } catch (Exception e) {

                        }
                        mAddress.setText(Guard);
                        mStartCall.performClick();
                        mAddress.setText("");
                    }
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
                        startActivity(new Intent(DialerActivity.this, BApage.class));
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

        mSocket.off("disconnect");
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
        System.out.println(newicon);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.photo)
                        .setLargeIcon(
                                BitmapFactory.decodeResource(getResources(), R.drawable.photo))
                        .setContentTitle(ContentTitle)
                        .setOngoing(false)
                        .setAutoCancel(true)
                        .setContentText(ContentText);
        // .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
        if (newicon.equals("warning")) {
            notificationBuilder.setSmallIcon(R.drawable.onfire);
            notificationBuilder.setLargeIcon(
                    BitmapFactory.decodeResource(getResources(), R.drawable.onfire));
            int soundResId = R.raw.fire;
            Uri soundUri =
                    Uri.parse(
                            ContentResolver.SCHEME_ANDROID_RESOURCE
                                    + "://"
                                    + getPackageName()
                                    + "/"
                                    + soundResId);
            System.out.println(soundUri);
            notificationBuilder.setSound(soundUri, AudioManager.STREAM_ALARM);
        } else {
            notificationBuilder.setSmallIcon(R.drawable.photo);
            notificationBuilder.setDefaults(
                    Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) (Math.random() * 99 + 101), notificationBuilder.build());
    }
}
