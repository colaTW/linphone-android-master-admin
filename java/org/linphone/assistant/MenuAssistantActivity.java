package org.linphone.assistant;

/*
MenuAssistantActivity.java
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.linphone.R;
import org.linphone.core.TransportType;
import org.linphone.settings.LinphonePreferences;

public class MenuAssistantActivity extends AssistantActivity {
    String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assistant_menu);

        TextView scan_btn = findViewById(R.id.scan_btn);
        TextView scan_btn2 = findViewById(R.id.scan_btn2);
        TextView scan_serect = findViewById(R.id.serect);
        final Activity activity = this;
        LinphonePreferences mPrefs;

        mPrefs = LinphonePreferences.instance();
        mPrefs.setInitiateVideoCall(true);
        mPrefs.setAutomaticallyAcceptVideoRequests(true);
        scan_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        type = "2";
                        IntentIntegrator integrator = new IntentIntegrator(activity);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                        integrator.setPrompt("Scan");
                        integrator.setCameraId(0);
                        integrator.setBeepEnabled(false);
                        integrator.setBarcodeImageEnabled(false);
                        integrator.setOrientationLocked(false);
                        integrator.initiateScan();
                    }
                });
        scan_btn2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        type = "1";
                        IntentIntegrator integrator = new IntentIntegrator(activity);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                        integrator.setPrompt("Scan");
                        integrator.setCameraId(0);
                        integrator.setBeepEnabled(false);
                        integrator.setBarcodeImageEnabled(false);
                        integrator.setOrientationLocked(false);
                        integrator.initiateScan();
                    }
                });
        scan_serect.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view3) {
                        AlertDialog.Builder alertDialog =
                                new AlertDialog.Builder(MenuAssistantActivity.this);
                        View view = getLayoutInflater().inflate(R.layout.accountsetting, null);
                        final EditText IP = view.findViewById(R.id.IP);
                        final EditText first = view.findViewById(R.id.first);
                        final EditText second = view.findViewById(R.id.second);

                        alertDialog.setPositiveButton(
                                "確認",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        mAccountCreator.setUsername(first.getText().toString());
                                        mAccountCreator.setDomain(IP.getText().toString());
                                        mAccountCreator.setPassword(second.getText().toString());
                                        mAccountCreator.setTransport(TransportType.Udp);
                                        createProxyConfigAndLeaveAssistant();
                                    }
                                });

                        alertDialog.setTitle("IP設定");
                        alertDialog.setView(view);
                        AlertDialog dialog = alertDialog.create();
                        dialog.show();
                    }
                });

        if (mAbortCreation) {
            return;
        }

        TextView genericConnection = findViewById(R.id.generic_connection);
        genericConnection.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(
                                new Intent(
                                        MenuAssistantActivity.this,
                                        GenericConnectionAssistantActivity.class));
                    }
                });
        if (getResources().getBoolean(R.bool.hide_generic_accounts_in_assistant)) {
            genericConnection.setVisibility(View.GONE);
        }

        if (getResources().getBoolean(R.bool.assistant_use_linphone_login_as_first_fragment)) {
            startActivity(
                    new Intent(
                            MenuAssistantActivity.this, AccountConnectionAssistantActivity.class));
            finish();
        } else if (getResources()
                .getBoolean(R.bool.assistant_use_generic_login_as_first_fragment)) {
            startActivity(
                    new Intent(
                            MenuAssistantActivity.this, GenericConnectionAssistantActivity.class));
            finish();
        } else if (getResources()
                .getBoolean(R.bool.assistant_use_create_linphone_account_as_first_fragment)) {
            startActivity(
                    new Intent(
                            MenuAssistantActivity.this,
                            PhoneAccountCreationAssistantActivity.class));
            finish();
        }
    }
    // 讀取QRCODE後
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_SHORT).show();

            } else {

                String user = "";
                String Domain = "";
                String Password = "";
                String guard = "";
                String code = "";
                String houseID;
                String houseType;
                // String ANDROID_ID = java.util.UUID.randomUUID().toString();
                // System.out.println(ANDROID_ID);
                try {
                    JSONObject info2;
                    JSONObject info = new JSONObject(result.getContents());
                    info2 = info;
                    user = info2.getString("household_number");
                    Domain = info2.getString("domain");
                    guard = info2.getString("guard");
                    houseID = info2.getString("household_id");
                    houseType = info2.getString("household_type");
                    HttpGet httpGet =
                            new HttpGet(
                                    "http://"
                                            + Domain
                                            + ":8888/api/v1/household/devices/captain/"
                                            + houseID);
                    HttpClient httpClient2 = new DefaultHttpClient();
                    HttpResponse response2 = httpClient2.execute(httpGet);
                    HttpEntity responseHttpEntity = response2.getEntity();
                    code = EntityUtils.toString(response2.getEntity());
                    JSONObject temp1 = new JSONObject(code);
                    JSONObject data1 = temp1.getJSONObject("data");
                    String check = data1.getString("code");

                    if (type == "2" && check == "1") {
                        Toast.makeText(
                                        MenuAssistantActivity.this,
                                        "戶長機已註冊，請選住戶機註冊",
                                        Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Bundle bundle = new Bundle();
                        // 儲存資料　第一個為參數key，第二個為Value
                        bundle.putString("user", user);
                        bundle.putString("Domain", Domain);
                        bundle.putString("Password", Password);
                        bundle.putString("type", type);
                        bundle.putString("guard", guard);
                        bundle.putString("household_id", houseID);
                        bundle.putString("household_type", type);

                        Intent intent = new Intent();
                        intent.setClass(MenuAssistantActivity.this, login.class);
                        intent.putExtras(bundle); // 記得put進去，不然資料不會帶過去哦
                        startActivity(intent);
                        MenuAssistantActivity.this.finish();
                    }

                } catch (Exception e) {
                    Toast.makeText(MenuAssistantActivity.this, e.toString(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getResources()
                    .getBoolean(R.bool.forbid_to_leave_assistant_before_account_configuration)) {
                // Do nothing
                return true;
            } else {
                LinphonePreferences.instance().firstLaunchSuccessful();
                goToLinphoneActivity();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
