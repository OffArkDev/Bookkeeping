package com.example.android.bookkeeping.ui.cloud.storage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.bookkeeping.MyApplication;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.di.components.CloudStorageComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.StorageModule;
import com.example.android.bookkeeping.ui.mvp.BaseActivity;

import javax.inject.Inject;

public class FirebaseStorageActivity extends BaseActivity implements FirebaseStorageMvpView {

    private final static String TAG = "mystorage";

    private Button btnSave;
    private Button btnLoad;
    private ProgressBar progressBar;
    private View vButtons;

    @Inject
    public FirebaseStorageMvpPresenter<FirebaseStorageMvpView> presenter;

    public static Intent getStartIntent(Context context, String email) {
        Intent intent = new Intent(context, FirebaseStorageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.putExtra("email", email);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_firebase);
        getCloudComponent().inject(this);
        findViews();
        setOnClickListeners();

        presenter.onAttach(this);
        presenter.getEmailFromIntent(getIntent());
    }

    public CloudStorageComponent getCloudComponent() {
        return ((MyApplication) getApplication())
                .getApplicationComponent()
                .newCloudStorageComponent(new ActivityModule(this), new StorageModule(this));
    }

    @Override
    public void setOnClickListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.btnSaveClick();
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.btnLoadClick();
            }
        });
    }


    public void findViews() {
        btnSave = findViewById(R.id.button_cloud_save);
        btnLoad = findViewById(R.id.button_cloud_load);
        progressBar = findViewById(R.id.progress_bar);
        vButtons = findViewById(R.id.button_layer);
    }

    @Override
    public void returnResult() {
        setResult(RESULT_OK);
    }

    @Override
    public void showLoading() {
        vButtons.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        vButtons.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

}
