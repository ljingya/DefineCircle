package com.example.shuiai.definecircle;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements CircleView.AddUpdateListener {
    private TextView textView;
    private float progress = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CircleView cycleProgressView = (CircleView) findViewById(R.id.progressBar);
        cycleProgressView.setProgressMax(500);
        cycleProgressView.setProgress(progress);
//        cycleProgressView.setColorArray(gradientColorArray);
        cycleProgressView.update();
        cycleProgressView.setAddUpdateListener(this);
        textView = (TextView) findViewById(R.id.text);
    }

    @Override
    public void onAddUpdateListener(float percentage) {
        textView.setText(""+progress * percentage);
    }
}
