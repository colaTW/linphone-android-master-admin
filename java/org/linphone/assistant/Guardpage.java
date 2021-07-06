package org.linphone.assistant;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import java.security.acl.Group;
import java.util.ArrayList;
import org.linphone.R;
import org.linphone.activities.DialerActivity;

public class Guardpage extends Activity {
    private ArrayList<Group> gData = null;
    private ArrayList<ArrayList<ClipData.Item>> iData = null;
    private ArrayList<ClipData.Item> lData = null;
    private Context mContext;
    private ExpandableListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guardpage);
        ExpandableListView memberlist = findViewById(R.id.memberlist);
        ImageButton goBA = findViewById(R.id.B_BA);
        ImageButton godoor = findViewById(R.id.B_door);
        ImageButton gocall = findViewById(R.id.B_call);
        ImageButton goguard = findViewById(R.id.B_Guard);
        ImageButton memberdata = findViewById(R.id.memberdata);
        Button dooracess = findViewById(R.id.dooracess);
        Button approvedlist = findViewById(R.id.approvedList);
        Button accescard = findViewById(R.id.AccessCard);
        goBA.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(Guardpage.this, BApage.class);
                        startActivity(intent);
                    }
                });
        godoor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(Guardpage.this, memberdatabase.class);
                        startActivity(intent);
                    }
                });
        gocall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(Guardpage.this, DialerActivity.class);
                        startActivity(intent);
                    }
                });
    }
}
