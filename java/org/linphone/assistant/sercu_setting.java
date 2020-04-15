package org.linphone.assistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import org.linphone.R;

public class sercu_setting extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sercu_setting);
        ImageButton home = findViewById(R.id.B_home);
        ImageView reset = findViewById(R.id.reset);
        ImageView status = findViewById(R.id.status);
        final Switch lock = findViewById(R.id.lock);
        final ImageView lockimg = findViewById(R.id.lockimg);
        lock.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            lockimg.setBackgroundResource(R.drawable.secu6);
                        } else {
                            lockimg.setBackgroundResource(R.drawable.secu5);
                        }
                    }
                });

        home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(sercu_setting.this, home.class);
                        startActivity(intent);
                    }
                });
        reset.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(sercu_setting.this, sercu_reset.class);
                        startActivity(intent);
                    }
                });
        status.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(sercu_setting.this, sercu.class);
                        startActivity(intent);
                    }
                });
    }
}
