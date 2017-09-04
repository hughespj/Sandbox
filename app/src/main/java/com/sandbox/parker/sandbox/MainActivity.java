package com.sandbox.parker.sandbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.joda.time.DateTime;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DateTime time = new DateTime(DateTime.now());
        TextView timeView = ((TextView) findViewById(R.id.time_view));
        timeView.setText(time.toString());
    }
}
