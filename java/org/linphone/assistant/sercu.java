package org.linphone.assistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import org.linphone.R;

public class sercu extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sercu_page);
        ImageButton home = findViewById(R.id.B_home);
        ImageView setting = findViewById(R.id.setting);
        ImageView reset = findViewById(R.id.reset);

        home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(sercu.this, home.class);
                        startActivity(intent);
                    }
                });
        setting.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(sercu.this, sercu_setting.class);
                        startActivity(intent);
                    }
                });
        reset.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(sercu.this, sercu_reset.class);
                        startActivity(intent);
                    }
                });
    }
}
