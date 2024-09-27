package com.example.gptorganizier.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.DriveScopes;

import java.util.Collections;

public class GoogleAuthService {

    private static GoogleAccountCredential credential;

    public static GoogleAccountCredential getCredential(Context context) {
        if (credential == null) {
            credential = GoogleAccountCredential.usingOAuth2(
                    context.getApplicationContext(), Collections.singletonList(DriveScopes.DRIVE_FILE));

            SharedPreferences sharedPreferences = context.getSharedPreferences("YourAppPrefs", Context.MODE_PRIVATE);
            String accountName = sharedPreferences.getString("accountName", null);
            if (accountName != null) {
                credential.setSelectedAccountName(accountName);
                Log.d("GoogleAuthService", "Restored account: " + accountName);
            }
        }
        return credential;
    }

    public static void signIn(Activity activity, int requestCode) {
        credential = getCredential(activity);
        Intent signInIntent = credential.newChooseAccountIntent();
        activity.startActivityForResult(signInIntent, requestCode);
    }

    public static void handleSignInResult(int resultCode, Intent data, Context context) {
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
    public static void signOut(Context context) {
        if (credential != null && credential.getSelectedAccountName() != null) {
            Log.d("GoogleAuthService", "User logged out: " + credential.getSelectedAccountName());
            credential.setSelectedAccountName(null);

            SharedPreferences sharedPreferences = context.getSharedPreferences("YourAppPrefs", Context.MODE_PRIVATE);
            sharedPreferences.edit().remove("accountName").apply();
        } else {
            Log.d("GoogleAuthService", "No user is currently logged in.");
        }
    }
    public static String getAccountName() {
        return credential.getSelectedAccountName();
    }
    public static boolean isLoggedIn() {
        return credential != null && credential.getSelectedAccountName() != null;
    }
}
