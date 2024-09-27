package com.example.gptorganizier.Menu;

import android.app.Activity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
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

    Button loginButton;
    Button promptButton;
    Button linkButton;
    Button logoutButton;

    public SideBarManager(Context context, DrawerLayout drawerLayout, NavigationView navigationView) {
        this.context = context;
        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;

        setupDrawer();
    }

    private void setupDrawer() {

        loginButton = navigationView.findViewById(R.id.nav_login_button);
        promptButton = navigationView.findViewById(R.id.nav_prompt_button);
        linkButton = navigationView.findViewById(R.id.nav_link_button);
        logoutButton = navigationView.findViewById(R.id.nav_logout_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleAuthService.signIn((Activity) context, 100);
            }
        });

        promptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).loadPrompts();
                drawerLayout.closeDrawers();
            }
        });

        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).loadLinks();
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
        loginButton.setVisibility(View.GONE);
        logoutButton.setVisibility(View.GONE);

        if (!GoogleAuthService.isLoggedIn()) {
            loginButton.setVisibility(View.VISIBLE);
        } else {
            logoutButton.setVisibility(View.VISIBLE);
        }
    }
}
