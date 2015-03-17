package com.cuberob.wearaccuracy.activities;

import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.TypedValue;
import android.widget.TextView;

import com.cuberob.wearaccuracy.R;

public class VisibilityTest extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visibility_test);

        final String content = getIntent().getStringExtra("content");
        final int textSize = getIntent().getIntExtra("size", 8);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                TextView textView = (TextView) stub.findViewById(R.id.text);
                textView.setText(content);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
            }
        });
    }
}
