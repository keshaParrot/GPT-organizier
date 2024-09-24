package com.example.gptorganizier;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gptorganizier.domain.Record;
import com.example.gptorganizier.domain.TypeOfRecord;
import com.example.gptorganizier.service.DatabaseService;
import com.example.gptorganizier.service.GoogleAuthService;

import java.util.Date;

public class MainActivity extends AppCompatActivity {



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

        DatabaseService dbService = DatabaseService.getInstance(this, GoogleAuthService.getCredential(this));
        Record record = new Record(1L,
                "test",
                "test",
                "test",
                new Date(2000,2,2),
                new Date(2000,2,2),
                TypeOfRecord.PROMPT);

        //dbService.addRecord(record);
        Log.i("MA", String.valueOf(dbService.getAllPrompts().get(0)));
    }

    //TODO on destroy save database
}
