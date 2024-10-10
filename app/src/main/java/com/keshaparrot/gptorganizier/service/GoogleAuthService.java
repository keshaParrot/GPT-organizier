package com.keshaparrot.gptorganizier.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.DriveScopes;

import java.util.Collections;

public class GoogleAuthService {

    private static GoogleAuthService instance;
    private GoogleAccountCredential credential;
    public static final int REQUEST_CODE_SIGN_IN = 100;

    private GoogleAuthService(Context context) {
        credential = GoogleAccountCredential.usingOAuth2(
                context.getApplicationContext(), Collections.singletonList(DriveScopes.DRIVE_FILE));

        SharedPreferences sharedPreferences = context.getSharedPreferences("YourAppPrefs", Context.MODE_PRIVATE);
        String accountName = sharedPreferences.getString("accountName", null);
        if (accountName != null) {
            credential.setSelectedAccountName(accountName);
            Log.d("GoogleAuthService", "Restored account: " + accountName);
        }
    }

    public static synchronized GoogleAuthService getInstance(Context context) {
        if (instance == null) {
            instance = new GoogleAuthService(context);
        }
        return instance;
    }
    public static synchronized GoogleAuthService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GoogleAuthService is not initialized. Call getInstance(Context context) first.");
        }
        return instance;
    }

    public GoogleAccountCredential getCredential() {
        return credential;
    }

    public void signIn(Activity activity, int requestCode) {
        Intent signInIntent = credential.newChooseAccountIntent();
        activity.startActivityForResult(signInIntent, requestCode);
    }

    public void handleSignInResult(int resultCode, Intent data, Context context) {
        if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
            String accountName = data.getStringExtra("authAccount");
            credential.setSelectedAccountName(accountName);
            Log.d("GoogleAuthService", "Logged in as: " + accountName);

            SharedPreferences sharedPreferences = context.getSharedPreferences("YourAppPrefs", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("accountName", accountName).apply();
        } else {
            Log.d("GoogleAuthService", "Login failed or canceled.");
        }
    }

    public void signOut(Context context) {
        if (credential != null && credential.getSelectedAccountName() != null) {
            Log.d("GoogleAuthService", "User logged out: " + credential.getSelectedAccountName());
            credential.setSelectedAccountName(null);

            SharedPreferences sharedPreferences = context.getSharedPreferences("YourAppPrefs", Context.MODE_PRIVATE);
            sharedPreferences.edit().remove("accountName").apply();
        } else {
            Log.d("GoogleAuthService", "No user is currently logged in.");
        }
    }

    public String getAccountName() {
        return credential.getSelectedAccountName();
    }

    public boolean isLoggedIn() {
        return credential != null && credential.getSelectedAccountName() != null;
    }
}
