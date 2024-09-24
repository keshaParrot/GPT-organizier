package com.example.gptorganizier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gptorganizier.service.GoogleAuthService;

public class WelcomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isFirstLaunch = preferences.getBoolean("isFirstLaunch", true);

        if (!isFirstLaunch) {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
            return;
        }

        Button loginButton = findViewById(R.id.login_button);
        Button skipButton = findViewById(R.id.skip_button);

        loginButton.setOnClickListener(v -> {
            GoogleAuthService.signIn(this, REQUEST_CODE_SIGN_IN);
        });

        skipButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.apply();
            finish();
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            GoogleAuthService.handleSignInResult(resultCode, data);
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
