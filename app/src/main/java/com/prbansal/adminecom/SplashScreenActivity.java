package com.prbansal.adminecom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        setContentView(R.layout.activity_splash_screen);*/

        startActivity(new Intent(SplashScreenActivity.this,CatelogActivity.class));
        finish();
    }
}