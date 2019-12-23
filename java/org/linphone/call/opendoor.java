package org.linphone.call;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import org.linphone.R;

public class opendoor extends Activity {

    private void HttpPostData() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);

        ImageView mGet = findViewById(R.id.Openkey);

        mGet.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast toast =
                                Toast.makeText(opendoor.this, "Hello world!", Toast.LENGTH_LONG);
                        // 顯示Toast
                        toast.show();
                    }
                });
    }
}
