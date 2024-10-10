package com.keshaparrot.gptorganizier.Menu;

import static android.content.Context.MODE_PRIVATE;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


import com.keshaparrot.gptorganizier.R;
import com.keshaparrot.gptorganizier.listener.LoginListener;
import com.keshaparrot.gptorganizier.listener.RecordObserverListener;
import com.keshaparrot.gptorganizier.service.GoogleAuthService;
import com.keshaparrot.gptorganizier.service.UpdateService;
import com.google.android.material.navigation.NavigationView;


public class SideBarManager {

    private Context context;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;
    private RecordObserverListener recordObserverListener;
    private LoginListener loginListener;

    private Button loginButton;
    private Button promptButton;
    private Button linkButton;
    private Button logoutButton;
    private ImageButton updateCheckButton;

    public SideBarManager(Context context, DrawerLayout drawerLayout, NavigationView navigationView, RecordObserverListener listener, LoginListener loginListener) {
        this.context = context;
        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;
        this.recordObserverListener = listener;
        this.loginListener = loginListener;
        this.sharedPreferences = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);

        setupDrawer();
    }

    private void setupDrawer() {
        loginButton = navigationView.findViewById(R.id.nav_login_button);
        promptButton = navigationView.findViewById(R.id.nav_prompt_button);
        linkButton = navigationView.findViewById(R.id.nav_link_button);
        logoutButton = navigationView.findViewById(R.id.nav_logout_button);
        updateCheckButton = navigationView.findViewById(R.id.update_app_button);

        loginButton.setOnClickListener(v -> {
            loginListener.login();
            closeDrawer();
        });
        promptButton.setOnClickListener(v -> updateItemShowType(false));
        linkButton.setOnClickListener(v -> updateItemShowType(true));
        logoutButton.setOnClickListener(v -> logout());
        updateCheckButton.setOnClickListener(v -> {
            UpdateService updateService = UpdateService.getInstance();
            updateService.checkForUpdateOnStart();
            updateService.enableUpdateReminder();
            closeDrawer();
        });
    }

    private void updateItemShowType(boolean showLinks) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("itemShowTypeLink", showLinks);
        editor.apply();
        recordObserverListener.execObserveRecords();
        drawerLayout.closeDrawers();
    }

    private void logout() {
        ConfirmDialogFragment.newInstance(context.getString(R.string.log_out_message), accepted -> {
            if (accepted) {
                GoogleAuthService googleAuthService = GoogleAuthService.getInstance();
                googleAuthService.signOut(context);
                updateMenu();
                drawerLayout.closeDrawers();
            }
        }).show(((AppCompatActivity) context).getSupportFragmentManager(), "ConfirmDialog");
    }

    public void openDrawer() {
        if (!drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.openDrawer(navigationView);
            updateMenu();
        }
    }

    public void closeDrawer() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        }
    }

    public void updateMenu() {
        updateLogButton();
        updateUpdateButton();
    }
    public void updateLogButton(){
        UpdateService service = UpdateService.getInstance();
        boolean condition = service.isNewVersionAvailable(service.getCurrentVersion());
        updateCheckButton.setImageResource(condition?R.drawable.application_updated_24dp:R.drawable.application_need_update_24dp);
    }
    public void updateUpdateButton(){
        GoogleAuthService googleAuthService = GoogleAuthService.getInstance(context);
        boolean isLoggedIn = googleAuthService.isLoggedIn();
        loginButton.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        logoutButton.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
    }
}
