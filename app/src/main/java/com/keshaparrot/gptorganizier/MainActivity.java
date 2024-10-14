package com.keshaparrot.gptorganizier;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.keshaparrot.gptorganizier.Menu.ConfirmDialogFragment;
import com.keshaparrot.gptorganizier.R;
import com.keshaparrot.gptorganizier.Menu.CreateRecordDialogFragment;
import com.keshaparrot.gptorganizier.adapters.RecordAdapter;
import com.keshaparrot.gptorganizier.listener.LoginListener;
import com.keshaparrot.gptorganizier.listener.RecordObserverListener;
import com.keshaparrot.gptorganizier.service.GoogleAuthService;
import com.keshaparrot.gptorganizier.service.UpdateService;
import com.keshaparrot.gptorganizier.viewmodel.RecordViewModel;
import com.keshaparrot.gptorganizier.domain.Record;
import com.keshaparrot.gptorganizier.Menu.SideBarManager;
import com.keshaparrot.gptorganizier.service.ContentExchangeService;
import com.keshaparrot.gptorganizier.service.GoogleDriveSyncService;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements RecordObserverListener, LoginListener {

    private SideBarManager sideBarManager;
    private RecordAdapter recordAdapter;
    private RecordViewModel recordViewModel;
    SharedPreferences preferences;
    private Observer<List<Record>> recordObserver;
    private UpdateService updateService;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        GoogleAuthService googleAuthService = GoogleAuthService.getInstance(this);
                        googleAuthService.handleSignInResult(result.getResultCode(), result.getData(), this);
                    } else {
                        Log.d("MainActivity", "Login failed or canceled.");
                    }
                });

        preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isFirstLaunch", false);
        editor.putBoolean("itemShowTypeLink", true);
        editor.apply();

        updateService = UpdateService.getInstance(this);

        recordViewModel = new RecordViewModel(this);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        sideBarManager = new SideBarManager(this, drawerLayout, navigationView,this,this);
        sideBarManager.updateMenu();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordAdapter = new RecordAdapter(new ContentExchangeService(this));
        recyclerView.setAdapter(recordAdapter);

        observeRecords();
        changeRecordTypeHeader();

        ImageButton openDrawerButton = findViewById(R.id.open_drawer_button);
        openDrawerButton.setOnClickListener(v -> sideBarManager.openDrawer());

        ImageButton newRecordButton = findViewById(R.id.create_record_button);
        newRecordButton.setOnClickListener(view -> new CreateRecordDialogFragment().show(getSupportFragmentManager(), "CreateRecordDialog"));

        GoogleDriveSyncService.getInstance(this).syncGetDatabase();

        Log.d("is logged in user?", String.valueOf(GoogleAuthService.getInstance().isLoggedIn()));
    }

    public void observeRecords() {
        boolean showTypeLink = preferences.getBoolean("itemShowTypeLink", false);
        LiveData<List<Record>> recordsLiveData;

        if (!showTypeLink) {
            recordsLiveData = recordViewModel.getAllPrompts();
        } else {
            recordsLiveData = recordViewModel.getAllLinks();
        }

        if (recordObserver != null) {
            recordsLiveData.removeObserver(recordObserver);
        }

        recordObserver = new Observer<List<Record>>() {
            @Override
            public void onChanged(List<Record> records) {
                Log.d("MainActivity", "New records: " + records.toString());
                recordAdapter.updateList(records);
                changeRecordTypeHeader();
            }
        };

        recordsLiveData.observe(this, recordObserver);
    }
    public void changeRecordTypeHeader(){
        TextView recordTypeTextView = findViewById(R.id.record_type);
        boolean showTypeLink = preferences.getBoolean("itemShowTypeLink", false);

        recordTypeTextView.setText(showTypeLink ?"Links":"Prompts");
    }
    @Override
    public void login() {
        ConfirmDialogFragment.newInstance(
                getString(R.string.log_in_message),
                getString(R.string.log_in),
                getString(R.string.log_in),
                getString(R.string.skip),
                accepted -> {
                    if (accepted) {
                        GoogleAuthService googleAuthService = GoogleAuthService.getInstance(this);
                        Intent signInIntent = googleAuthService.getCredential().newChooseAccountIntent();
                        signInLauncher.launch(signInIntent);
                    }
                }).show(this.getSupportFragmentManager(), "ConfirmDialog");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GoogleAuthService.REQUEST_CODE_SIGN_IN) {
            GoogleAuthService.getInstance().handleSignInResult(resultCode, data, this);

        }
    }
    @Override
    public void execObserveRecords() {
        observeRecords();
    }
    @Override
    protected void onPause() {
        super.onPause();
        try {
            GoogleDriveSyncService.getInstance().syncSendDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
