package com.udacity.inventory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get unix current DATETIME
        long epoch = System.currentTimeMillis()/1000;
        Log.v(">>EPOCH NOW DATE>>>", String.valueOf(epoch));
        /*

        Epoch timestamp: 1528046360
        Timestamp in milliseconds: 1528046360000
        Human time (GMT): Sunday, June 3, 2018 5:19:20 PM
        Human time (your time zone): Sunday, June 3, 2018 2:19:20 PM GMT-03:00

         */
    }
}
