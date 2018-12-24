package com.example.android.bookkeeping.ui.cloud;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.bookkeeping.MyApplication;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.data.AccountSaver;
import com.example.android.bookkeeping.data.DataPOJO;
import com.example.android.bookkeeping.data.TransactionSaver;
import com.example.android.bookkeeping.di.components.CloudComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.StorageModule;
import com.example.android.bookkeeping.repository.AccountsRepository;
import com.example.android.bookkeeping.repository.TransactionsRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FirebaseStorageActivity extends AppCompatActivity {

    private final static String TAG = "mystorage";

    private Button btnSave;
    private Button btnLoad;
    private ProgressBar progressBar;
    private View vButtons;

    private String email;

    private DataPOJO dataPOJO = new DataPOJO();

    private boolean isOneFlowLoaded = false;


    @Inject
    public Context context;

    @Inject
    public AccountsRepository accountsRepository;

    @Inject
    public TransactionsRepository transactionsRepository;

    @Inject
    public CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_firebase);
        getCloudComponent().inject(this);
        findViews();
        email = getIntent().getStringExtra("email");

        getDataFromDatabase();
        setClickListeners();
    }

    public CloudComponent getCloudComponent() {
        return ((MyApplication) getApplication())
                .getApplicationComponent()
                .newCloudComponent(new ActivityModule(this), new StorageModule(this));
    }

    public void setClickListeners() {

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToCloud();
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFromCloud();
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
        if (!isOneFlowLoaded) {
            isOneFlowLoaded = true;
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


    public void saveToCloud() {
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
        }).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "Save success", Toast.LENGTH_SHORT).show();
                showOrHideProgressBar(false);

            }
        });
    }

    public void loadFromCloud() {
        showOrHideProgressBar(true);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference().child("data/" + email);
        final long maxDownLoadSize = 5 * 1024 * 1024;
        storageRef.getBytes(maxDownLoadSize).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if (bytes == null) {
                    Toast.makeText(context, "no data in the cloud", Toast.LENGTH_SHORT).show();
                } else {
                    String strData = new String(bytes);
                    Gson gson = new Gson();
                    dataPOJO = gson.fromJson(strData, DataPOJO.class);
                    updateData();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i(TAG, "onFailure: " + exception.getMessage());
                Toast.makeText(context, "Loading failed", Toast.LENGTH_SHORT).show();
                showOrHideProgressBar(false);
            }
        });
    }



    public void updateData() {
        isOneFlowLoaded = false;

        compositeDisposable.add(accountsRepository.deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                               @Override
                               public void run() {
                                   Log.i(TAG, "delete account complete");
                                       loadAccountsToInternal();
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable)  {
                                   Log.e(TAG, "delete account fail " + throwable.getMessage());
                               }
                           }
                ));

        compositeDisposable.add(transactionsRepository.deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                               @Override
                               public void run() {
                                   Log.i(TAG, "delete account complete");
                                       loadTransactionsToInternal();
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable)  {
                                   Log.e(TAG, "delete account fail " + throwable.getMessage());
                               }
                           }
                ));

    }

    public void loadAccountsToInternal() {
        List<AccountSaver> listAccounts = dataPOJO.getAccountsList();

        compositeDisposable.add(accountsRepository.insertList(listAccounts)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<long[]>() {
                    @Override
                    public void accept(long[] aLong) {
                        Log.i(TAG, "insert accounts success");
                        if (isOneFlowLoaded) {
                            showOrHideProgressBar(false);
                            Toast.makeText(context, "Loading from database success", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                        } else isOneFlowLoaded = true;
                    }
                }));
    }

    public void loadTransactionsToInternal() {
        List<TransactionSaver> listTransactions = dataPOJO.getTransactionList();

        compositeDisposable.add(transactionsRepository.insertList(listTransactions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<long[]>() {
                    @Override
                    public void accept(long[] aLong) {
                        Log.i(TAG, "insert transactions success");
                        if (isOneFlowLoaded) {
                            showOrHideProgressBar(false);
                            Toast.makeText(context, "Loading from database success", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                        } else isOneFlowLoaded = true;
                    }
                }));
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
