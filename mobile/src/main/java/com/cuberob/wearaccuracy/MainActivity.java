package com.cuberob.wearaccuracy;

import android.os.Bundle;
import android.view.View;

/**
 * Created by rob.knegt on 11-3-2015.
 */
public class MainActivity extends GooglePlayServicesActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v){
        int id = v.getId();
        switch(id){
            case R.id.four_button:
                sendMessage("4".getBytes(), "/start");
                break;
            case R.id.two_button:
                sendMessage("2".getBytes(), "/start");
                break;
        }
    }

}
