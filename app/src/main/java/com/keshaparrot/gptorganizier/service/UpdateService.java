package com.keshaparrot.gptorganizier.service;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;


import androidx.annotation.NonNull;

import com.keshaparrot.gptorganizier.MainActivity;
import com.keshaparrot.gptorganizier.Menu.AppUpdateDialogFragment;
import com.keshaparrot.gptorganizier.Menu.AppUpdateInfoDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateService {

    //TODO check and implement here UI


    //TODO
    // при ініціалізації класи, якшо userwantgetremind true, виконується метода checkupdate, яка кидає екран апдейту і міняє шаред преференсес на needupdate true
    // потім при натисканні апдейт виконуєьбся метода, яка завантажує апдейт і міняє версію
    // в випадку відмови просто закривається меню
    // в випадку червоноі кнопки окрім закривання ще й userwantgetremind ставиться на false

    //TODO check update
    // version will be in json or in build
    // userwantgetremind get enable disable
    // needupdate get set
    // download update

    private static final String TAG = "UpdateService";
    private Context context;

    private static final String GITHUB_API_URL = "https://api.github.com/repos/keshaParrot/GPT-organizier/releases/latest";
    private static final String PREFS_NAME = "AppPrefs";
    private static final String VERSION_KEY = "currentVersion";

    private SharedPreferences preferences;
    private static UpdateService instance;

    private UpdateService(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        checkForUpdateOnStart();
    }
    public static synchronized UpdateService getInstance(Context context){
        if (instance == null) {
            instance = new UpdateService(context);
        }
        return instance;
    }

    public static synchronized UpdateService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UpdateService is not initialized. Call getInstance(Context context) first.");
        }
        return instance;
    }

    public String getCurrentVersion() {
        return preferences.getString(VERSION_KEY, "0.5.0");
    }

    public void downloadApk() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(GITHUB_API_URL));
        request.setTitle("Downloading update");
        request.setDescription("Downloading the latest version of the app");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "new_version.apk");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }
    private void saveNewVersion(String newVersion) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(VERSION_KEY, newVersion);
        editor.apply();
    }
    public void checkForUpdateOnStart() {
        if (!preferences.getBoolean("isUserWillGetReminder", true)) {
            Log.d("", "user off reminding about update");
            return;
        }

        new Thread(() -> {
            try {
                JSONObject jsonResponse = getJsonObject();
                String latestVersion = jsonResponse.getString("tag_name");
                String apkUrl = jsonResponse.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");

                if (isNewVersionAvailable(latestVersion)) {
                    showUpdateDialog(latestVersion, apkUrl);
                } else {
                    Log.d(TAG, "No new version available.");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error checking for update", e);
            }
        }).start();
    }

    private static @NonNull JSONObject getJsonObject() throws IOException, JSONException {
        URL url = new URL(GITHUB_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();

        return new JSONObject(content.toString());
    }

    public void showCurrentVersionDialog() {
        AppUpdateInfoDialogFragment infoDialog = AppUpdateInfoDialogFragment.newInstance(context);
        infoDialog.show(((MainActivity) context).getSupportFragmentManager(), "AppUpdateInfoDialog");
    }
    public void disableUpdateReminder() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isUserWillGetReminder", false);
        editor.apply();
    }
    public void enableUpdateReminder() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isUserWillGetReminder", true);
        editor.apply();
    }
    public String getLatestVersion() {
        final String[] latestVersion = {null};

        Thread thread = new Thread(() -> {
            try {
                JSONObject jsonResponse = getJsonObject();
                latestVersion[0] = jsonResponse.getString("tag_name");

            } catch (Exception e) {
                Log.e(TAG, "Error retrieving latest version", e);
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return latestVersion[0];
    }
    public boolean isNewVersionAvailable(String latestVersion) {
        String currentVersion = getCurrentVersion();
        return !currentVersion.equals(latestVersion);
    }
    private void showUpdateDialog(String latestVersion, String apkUrl) {
        AppUpdateDialogFragment updateDialog = AppUpdateDialogFragment.newInstance(context);
        updateDialog.show(((MainActivity) context).getSupportFragmentManager(), "AppUpdateDialog");
    }
}

