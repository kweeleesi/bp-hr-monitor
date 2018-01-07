package com.example.hp.heartrytcare.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hp.heartrytcare.R;

public class SignupActivity extends AppCompatActivity {

    private Button signUp;
    private TextView signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeFields();

        Spannable wordtoSpan = new SpannableString(getResources().getString(R.string.signin));
        wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue_link)), 3, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new UnderlineSpan(), 3, wordtoSpan.length(), 0);
        signIn.setText(wordtoSpan);
    }

    private void initializeFields() {
        signUp = (Button) findViewById(R.id.signUp);
        signIn = (TextView) findViewById(R.id.signIn);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 1/8/2018 login page 
            }
        });
    }
}
