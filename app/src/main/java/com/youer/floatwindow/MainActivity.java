package com.youer.floatwindow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openFloatWindow(View view) {
        LinearLayout floatView = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.float_view, null);

        FloatWindow floatWindow = new FloatWindow.Builder(this, floatView)
            .setGravity(Gravity.BOTTOM)
            .build();
        floatWindow.show();
    }
}