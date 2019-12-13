package org.linphone.assistant;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import org.linphone.R;

import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

public class login extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        TextView UUID = findViewById(R.id.UUID);
        TextView Num = findViewById(R.id.Num);
        Bundle bundle = getIntent().getExtras();
        String user = bundle.getString("user");
        String Domain = bundle.getString("Domain");
        String Password = bundle.getString("Password");
        Num.setText(user);
        String android_id =
                Settings.Secure.getString(
                        getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID.setText(android_id);
    }
}
