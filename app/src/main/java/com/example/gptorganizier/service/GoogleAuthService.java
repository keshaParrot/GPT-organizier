package com.example.gptorganizier.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
        }
        return credential;
    }

    public static void signIn(Activity activity, int requestCode) {
        credential = getCredential(activity);
        Intent signInIntent = credential.newChooseAccountIntent();
        activity.startActivityForResult(signInIntent, requestCode);
    }

    public static void handleSignInResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
            String accountName = data.getStringExtra("authAccount");
            credential.setSelectedAccountName(accountName);
            Log.d("GoogleAuthService", "Logged in as: " + accountName);
        } else {
            Log.d("GoogleAuthService", "Login failed or canceled.");
        }
    }
    public static String getAccountName() {
        return credential.getSelectedAccountName();
    }
}
