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
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.R;
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
        final Activity activity = this;

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
                        type = "3";
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
                // String ANDROID_ID = java.util.UUID.randomUUID().toString();
                // System.out.println(ANDROID_ID);
                try {
                    JSONObject info2;
                    JSONObject info = new JSONObject(result.getContents());
                    info2 = info;
                    user = info2.getString("user");
                    Domain = info2.getString("domain");
                    Password = info2.getString("Pwd");
                    guard = info2.getString("guard");

                    Bundle bundle = new Bundle();
                    // 儲存資料　第一個為參數key，第二個為Value
                    bundle.putString("user", user);
                    bundle.putString("Domain", Domain);
                    bundle.putString("Password", Password);
                    bundle.putString("type", type);
                    bundle.putString("guard", guard);
                    Intent intent = new Intent();
                    intent.setClass(MenuAssistantActivity.this, login.class);

                    intent.putExtras(bundle); // 記得put進去，不然資料不會帶過去哦
                    startActivity(intent);
                    MenuAssistantActivity.this.finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // mAccountCreator.setUsername(user);
                //  mAccountCreator.setDomain(Domain);
                // mAccountCreator.setPassword(Password);
                // mAccountCreator.setTransport(TransportType.Udp);
                // createProxyConfigAndLeaveAssistant();
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
