package org.linphone.assistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import org.linphone.R;

public class message extends Activity {
    ViewPager pager;
    ArrayList<View> pagerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messge_viewpage);
        ImageButton home = findViewById(R.id.B_home);
        ImageButton page1 = findViewById(R.id.messagepage1);
        ImageButton page2 = findViewById(R.id.messagepage2);
        ImageButton page3 = findViewById(R.id.messagepage3);
        ImageButton page4 = findViewById(R.id.messagepage4);
        ImageButton page5 = findViewById(R.id.messagepage5);

        page1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(message.this, messagepage1.class);
                        startActivity(intent);
                    }
                });
        page2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(message.this, messagepage2.class);
                        startActivity(intent);
                    }
                });
        page3.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(message.this, messagepage3.class);
                        startActivity(intent);
                    }
                });
        page4.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(message.this, messagepage4.class);
                        startActivity(intent);
                    }
                });
        page5.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(message.this, messagepage5.class);
                        startActivity(intent);
                    }
                });
        home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(message.this, home.class);
                        startActivity(intent);
                    }
                });
    }
}
