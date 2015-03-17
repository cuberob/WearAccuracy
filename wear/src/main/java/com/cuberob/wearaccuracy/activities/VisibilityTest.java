package com.cuberob.wearaccuracy.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.TypedValue;
import android.widget.TextView;

import com.cuberob.wearaccuracy.R;

public class VisibilityTest extends BaseActivity {

    public static final String CONTENT = "content:string";
    public static final String SIZE = "size:int";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visibility_test);

        final String content = getIntent().getStringExtra(CONTENT);
        final int textSize = getIntent().getIntExtra(SIZE, 8);

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


    static public Intent getStartIntent(int textSize, String content, Context context){
        Intent i = new Intent(context, VisibilityTest.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(CONTENT, content);
        i.putExtra(SIZE, textSize);
        return i;
    }
}
