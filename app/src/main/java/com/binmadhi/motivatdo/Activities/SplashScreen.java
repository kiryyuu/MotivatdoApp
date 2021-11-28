package com.binmadhi.motivatdo.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import com.binmadhi.motivatdo.R;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "TAG";
    private TextView tvAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        try {

            tvAppName = findViewById(R.id.tvAppName);
            //counter to wait for few minutes and show the splash screen
            new CountDownTimer(3000, 1000) {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onFinish() {
                    Intent intent = new Intent(SplashScreen.this, LogInActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onTick(long millisUntilFinished) {

                }
            }.start();

        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }
}