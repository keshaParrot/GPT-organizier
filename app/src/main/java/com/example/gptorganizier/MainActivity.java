package com.example.gptorganizier;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gptorganizier.Menu.MenuManager;
import com.example.gptorganizier.adapters.RecordAdapter;
import com.example.gptorganizier.domain.Record;
import com.example.gptorganizier.Menu.SideBarManager;
import com.example.gptorganizier.service.ContentExchangeService;
import com.example.gptorganizier.service.DatabaseService;
import com.example.gptorganizier.service.GoogleAuthService;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    private SideBarManager sideBarManager;
    private MenuManager menuManager;
    private RecordAdapter recordAdapter;
    private DatabaseService db;

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

        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isFirstLaunch", false);
        editor.apply();

        db = DatabaseService.getInstance(this,GoogleAuthService.getCredential(this));
        List<Record> records = db.getAllPrompts();

        menuManager = MenuManager.getInstance(this);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        sideBarManager = new SideBarManager(this, drawerLayout, navigationView);
        sideBarManager.updateMenu();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordAdapter = new RecordAdapter(records,new ContentExchangeService(this));
        recyclerView.setAdapter(recordAdapter);

        ImageButton openDrawerButton = findViewById(R.id.open_drawer_button);
        openDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sideBarManager.openDrawer();
            }
        });
        ImageButton newRecordButton = findViewById(R.id.create_record_button);
        newRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuManager.showCreateRecordMenu();
            }
        });
    }

    public void loadPrompts() {
        List<Record> prompts = db.getAllPrompts();
        TextView recordTypeTextView = findViewById(R.id.record_type);
        recordTypeTextView.setText("Prompts");
        recordAdapter.updateList(prompts);
    }
    public void loadLinks() {
        List<Record> links = db.getAllLinks();
        TextView recordTypeTextView = findViewById(R.id.record_type);
        recordTypeTextView.setText("Links");
        recordAdapter.updateList(links);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (db != null) {
            db.synchronizeDatabase();
        }
    }
}
