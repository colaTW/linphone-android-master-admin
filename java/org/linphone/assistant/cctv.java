package org.linphone.assistant;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import org.linphone.R;

public class cctv extends Activity implements View.OnTouchListener {
    private VideoView videoView;
    private String uri =
            "rtsp://admin:dh123456@pingling.ddns.net:554/cam/realmonitor?channel=1&subtype=00&authbasic=YWRtaW46ZGgxMjM0NTY=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cctv_page);
        videoView = (VideoView) findViewById(R.id.videoView);
        ImageButton home = findViewById(R.id.B_home);
        home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(cctv.this, home.class);
                        startActivity(intent);
                    }
                });

        MediaController mediaController = new MediaController(this);
        videoView.setVideoURI(Uri.parse(uri));
        videoView.setMediaController(mediaController);
        videoView.start();
        videoView.setOnTouchListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.suspend();
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.videoView:
                Toast.makeText(cctv.this, "d", Toast.LENGTH_LONG).show();
                break;
        }
        return false;
    }
}
