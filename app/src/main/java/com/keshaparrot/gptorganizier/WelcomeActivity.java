package com.keshaparrot.gptorganizier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.keshaparrot.gptorganizier.R;
import com.keshaparrot.gptorganizier.service.GoogleAuthService;
import com.keshaparrot.gptorganizier.service.GoogleDriveSyncService;

import java.io.File;

public class WelcomeActivity extends AppCompatActivity {

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
            GoogleAuthService googleAuthService = GoogleAuthService.getInstance(this);
            googleAuthService.signIn(this, GoogleAuthService.REQUEST_CODE_SIGN_IN);
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
        if (requestCode == GoogleAuthService.REQUEST_CODE_SIGN_IN) {
            GoogleAuthService googleAuthService = GoogleAuthService.getInstance(this);
            googleAuthService.handleSignInResult(resultCode, data, this);
            if (resultCode == RESULT_OK) {
                File localDb = getDatabasePath("records.db");
                if (!localDb.exists()) {
                    try {
                        GoogleDriveSyncService.getInstance(this).downloadDatabaseFromGoogleDrive();
                        Toast.makeText(this, R.string.database_dowload_success, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, R.string.database_dowload_erros, Toast.LENGTH_SHORT).show();
                    }
                }

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
