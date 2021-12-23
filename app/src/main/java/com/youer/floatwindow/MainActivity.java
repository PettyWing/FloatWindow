package com.youer.floatwindow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openFloatWindow(View view) {
        LinearLayout floatView = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.float_view, null);

        FloatWindow floatWindow = new FloatWindow.Builder(this, floatView)
            .build();
        floatWindow.show();

        floatView.findViewById(R.id.image).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
            }
        });
    }
}