package com.endows.endowsadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_activity);
        Button createAcctBtn = findViewById(R.id.create_acct_btn);
        createAcctBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createAcctIntent = new Intent(HomePageActivity.this,CreateAccountActivity.class);
                startActivity(createAcctIntent);
            }
        });
    }
}
