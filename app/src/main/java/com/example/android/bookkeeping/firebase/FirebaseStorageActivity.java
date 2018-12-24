package com.example.android.bookkeeping.firebase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.cloud.DataPOJO;
import com.example.android.bookkeeping.data.AccountSaver;
import com.example.android.bookkeeping.data.TransactionSaver;
import com.example.android.bookkeeping.repository.AccountsRepository;
import com.example.android.bookkeeping.repository.TransactionsRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class FirebaseStorageActivity extends AppCompatActivity {

    private final static String TAG = "mystorage";

    private Button btnSave;
    private Button btnLoad;
    private ProgressBar progressBar;
    private View vButtons;

    private String email;

    private DataPOJO dataPOJO = new DataPOJO();

    private boolean isFlowLoaded = false;

 //   @Inject
    public Context context;

  //  @Inject
    public AccountsRepository accountsRepository;

  //  @Inject
    public TransactionsRepository transactionsRepository;

  //  @Inject
    public CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_firebase);
//        DaggerParserComponent.builder()
//                .appModule(new AppModule(getApplication()))
//                .storageModule(new StorageModule(getApplication()))
//                .urlParserModule(new UrlParserModule(null))
//                .build()
//                .injectFirebaseStorageActivity(this);

        findViews();
        email = getIntent().getStringExtra("email");
        getDataFromDatabase();

        setClickListeners();
    }

    public void setClickListeners() {

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showOrHideProgressBar(true);
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageRef = firebaseStorage.getReference().child("data/" + email);

                Gson gson = new Gson();
                String json = gson.toJson(dataPOJO);
                byte[] data = json.getBytes();

                UploadTask uploadTask = storageRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.i(TAG, "onFailure: " + exception.getMessage());
                        showOrHideProgressBar(false);

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG, "onSuccess:");
                        showOrHideProgressBar(false);

                    }
                });

            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrHideProgressBar(true);

                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageRef = firebaseStorage.getReference().child("data/" + email);
                final long THREE_MEGABYTE = 3*1024 * 1024;
                storageRef.getBytes(THREE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.i(TAG, "success: " + new String(bytes));
                        showOrHideProgressBar(false);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.i(TAG, "onFailure: " + exception.getMessage());
                        showOrHideProgressBar(false);

                    }
                });

            }
        });

    }

    public void findViews() {
        btnSave = findViewById(R.id.button_cloud_save);
        btnLoad = findViewById(R.id.button_cloud_load);
        progressBar = findViewById(R.id.progress_bar);
        vButtons = findViewById(R.id.button_layer);
    }

    public void getDataFromDatabase() {
        showOrHideProgressBar(true);

        compositeDisposable.add(accountsRepository.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AccountSaver>>() {
                    @Override
                    public void accept(List<AccountSaver> accountSavers) {
                        dataPOJO.setAccountsList(accountSavers);
                        hideBarIfFlowLoaded();
                    }
                }));

        compositeDisposable.add(transactionsRepository.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TransactionSaver>>() {
                    @Override
                    public void accept(List<TransactionSaver> transactionSavers) {
                        dataPOJO.setTransactionList(transactionSavers);
                        hideBarIfFlowLoaded();
                    }
                }));
    }

    public void hideBarIfFlowLoaded() {
        if (!isFlowLoaded) {
            isFlowLoaded = true;
        } else {
            showOrHideProgressBar(false);
        }
    }

    public void showOrHideProgressBar(Boolean show) {
        if (show) {
            vButtons.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            vButtons.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.dispose();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        compositeDisposable.dispose();
    }
}
