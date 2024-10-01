package com.example.gptorganizier.Menu;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.gptorganizier.MainActivity;
import com.example.gptorganizier.R;
import com.example.gptorganizier.service.GoogleAuthService;
import com.google.android.material.navigation.NavigationView;


public class SideBarManager {

    private Context context;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;

    Button loginButton;
    Button promptButton;
    Button linkButton;
    Button logoutButton;

    public SideBarManager(Context context, DrawerLayout drawerLayout, NavigationView navigationView) {
        this.context = context;
        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;
        this.sharedPreferences = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);

        setupDrawer();
    }

    private void setupDrawer() {
        loginButton = navigationView.findViewById(R.id.nav_login_button);
        promptButton = navigationView.findViewById(R.id.nav_prompt_button);
        linkButton = navigationView.findViewById(R.id.nav_link_button);
        logoutButton = navigationView.findViewById(R.id.nav_logout_button);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleAuthService.signIn((Activity) context, 100);
            }
        });

        promptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("itemShowTypeLink", false);
                editor.apply();
                ((MainActivity) context).updateAdapter();
                drawerLayout.closeDrawers();
            }
        });

        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("itemShowTypeLink", true);
                editor.apply();
                ((MainActivity) context).updateAdapter();
                drawerLayout.closeDrawers();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleAuthService.signOut((Activity) context);
                updateMenu();
                drawerLayout.closeDrawers();
            }
        });
    }

    public void openDrawer() {
        if (!drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.openDrawer(navigationView);
        }
    }

    public void closeDrawer() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        }
    }

    public void updateMenu() {
        boolean isLoggedIn = GoogleAuthService.isLoggedIn();
        loginButton.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        logoutButton.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
    }
}
