package com.example.hp.heartrytcare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;


public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(null);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                        Intent homeIntent = new Intent(MainActivity.this, main_menu.class);
                        startActivity(homeIntent);
                        finish();
            }
        },SPLASH_TIME_OUT);
    }
}
