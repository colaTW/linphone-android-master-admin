package org.linphone.assistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import org.linphone.R;
import org.linphone.activities.DialerActivity;

public class home extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton gobull = findViewById(R.id.B_bull);
        ImageButton gomess = findViewById(R.id.B_mess);
        ImageButton gomail = findViewById(R.id.B_mail);
        ImageButton gocctv = findViewById(R.id.B_cctv);
        ImageButton golist = findViewById(R.id.list);
        ImageButton gosercu = findViewById(R.id.B_security);

        golist.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(home.this, house_qrcode.class);
                        startActivity(intent);
                    }
                });

        gocctv.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(home.this, cctv.class);
                        startActivity(intent);
                    }
                });

        gocall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(home.this, DialerActivity.class);
                        startActivity(intent);
                    }
                });
        gobull.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(home.this, bulletin.class);
                        startActivity(intent);
                    }
                });
        gomess.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(home.this, message.class);
                        startActivity(intent);
                    }
                });
        gomail.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(home.this, mailbox.class);
                        startActivity(intent);
                    }
                });
        gosercu.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(home.this, sercu.class);
                        startActivity(intent);
                    }
                });
    }
}
