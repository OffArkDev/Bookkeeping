package com.example.android.bookkeeping.firebase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.android.bookkeeping.R;

public class FirebaseStorageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_firebase);

        Button buttonSave = findViewById(R.id.button_cloud_save);
        Button buttonLoad = findViewById(R.id.button_cloud_load);

    }
}
