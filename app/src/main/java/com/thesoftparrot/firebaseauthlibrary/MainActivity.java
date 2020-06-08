package com.thesoftparrot.firebaseauthlibrary;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thesoftparrot.fbauth.AuthBaseActivity;

public class MainActivity extends AuthBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button phoneAuth = findViewById(R.id.auth_phone_btn);
        Button emailAuth = findViewById(R.id.auth_email_btn);

        phoneAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInByPhoneNumber();
            }
        });

        emailAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInByEmail();
            }
        });

    }

    @Override
    protected void authSuccessful(String userId) {
        Log.d("MainActivity", "_on_AuthSuccessful_UserId: "+userId);
        Toast.makeText(this, "Authentication Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void authFailed(String error) {
        Toast.makeText(this, "Error: "+error, Toast.LENGTH_SHORT).show();
    }
}