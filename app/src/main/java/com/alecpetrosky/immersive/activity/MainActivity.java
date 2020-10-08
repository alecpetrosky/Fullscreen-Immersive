package com.alecpetrosky.immersive.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alecpetrosky.immersive.*;

public class MainActivity extends BaseFullscreenActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
    }
}