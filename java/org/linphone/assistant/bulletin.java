package org.linphone.assistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import org.linphone.R;

public class bulletin extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bulletin_page);
        ImageButton home = findViewById(R.id.B_home);
        home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(bulletin.this, home.class);
                        startActivity(intent);
                    }
                });
    }
}
