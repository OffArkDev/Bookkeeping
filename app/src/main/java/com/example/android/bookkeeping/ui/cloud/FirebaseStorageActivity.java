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

import com.example.android.bookkeeping.Constants;
import com.example.android.bookkeeping.MyApplication;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.data.AccountSaver;
import com.example.android.bookkeeping.data.DataCloud;
import com.example.android.bookkeeping.data.TransactionSaver;
import com.example.android.bookkeeping.di.components.StorageComponent;
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

    private DataCloud dataCloud = new DataCloud();

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

    public StorageComponent getCloudComponent() {
        return ((MyApplication) getApplication())
                .getApplicationComponent()
                .newStorageComponent(new ActivityModule(this), new StorageModule(this));
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
        showLoading();

        compositeDisposable.add(accountsRepository.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AccountSaver>>() {
                    @Override
                    public void accept(List<AccountSaver> accountSavers) {
                        dataCloud.setAccountsList(accountSavers);
                        hideBarIfFlowLoaded();
                    }
                }));

        compositeDisposable.add(transactionsRepository.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TransactionSaver>>() {
                    @Override
                    public void accept(List<TransactionSaver> transactionSavers) {
                        dataCloud.setTransactionList(transactionSavers);
                        hideBarIfFlowLoaded();
                    }
                }));
    }

    public void hideBarIfFlowLoaded() {
        if (!isOneFlowLoaded) {
            isOneFlowLoaded = true;
        } else {
            hideLoading();
        }
    }

    public void showLoading() {
        vButtons.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

    }

    public void hideLoading() {
        vButtons.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

    }


    public void saveToCloud() {
        showLoading();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference().child(Constants.CLOUD_DATA_PATH + email);

        Gson gson = new Gson();
        String json = gson.toJson(dataCloud);
        byte[] data = json.getBytes();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i(TAG, "onFailure: " + exception.getMessage());
                hideLoading();

            }
        }).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, R.string.saved_success, Toast.LENGTH_SHORT).show();
                hideLoading();

            }
        });
    }

    public void loadFromCloud() {
        showLoading();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference().child(Constants.CLOUD_DATA_PATH + email);
        final long maxDownLoadSize = 5 * 1024 * 1024;
        storageRef.getBytes(maxDownLoadSize).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if (bytes == null) {
                    Toast.makeText(context, R.string.no_data_cloud, Toast.LENGTH_SHORT).show();
                } else {
                    String strData = new String(bytes);
                    Gson gson = new Gson();
                    dataCloud = gson.fromJson(strData, DataCloud.class);
                    updateData();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i(TAG, "onFailure: " + exception.getMessage());
                Toast.makeText(context, R.string.loading_failed, Toast.LENGTH_SHORT).show();
                hideLoading();
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
                                   Log.i(TAG, "account deletion completed");
                                       loadAccountsToInternal();
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable)  {
                                   Log.e(TAG, "account deletion failed " + throwable.getMessage());
                               }
                           }
                ));

        compositeDisposable.add(transactionsRepository.deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                               @Override
                               public void run() {
                                   Log.i(TAG, "account deletion completed");
                                       loadTransactionsToInternal();
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable)  {
                                   Log.e(TAG, "account deletion failed " + throwable.getMessage());
                               }
                           }
                ));

    }

    public void loadAccountsToInternal() {
        List<AccountSaver> listAccounts = dataCloud.getAccountsList();

        compositeDisposable.add(accountsRepository.insertList(listAccounts)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<long[]>() {
                    @Override
                    public void accept(long[] aLong) {
                        Log.i(TAG, "insert accounts success");
                        if (isOneFlowLoaded) {
                            hideLoading();
                            Toast.makeText(context, R.string.loading_database_success, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                        } else isOneFlowLoaded = true;
                    }
                }));
    }

    public void loadTransactionsToInternal() {
        List<TransactionSaver> listTransactions = dataCloud.getTransactionList();
        compositeDisposable.add(transactionsRepository.insertList(listTransactions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<long[]>() {
                    @Override
                    public void accept(long[] aLong) {
                        Log.i(TAG, "insert transactions success");
                        if (isOneFlowLoaded) {
                            hideLoading();
                            Toast.makeText(context,  R.string.loading_database_success, Toast.LENGTH_SHORT).show();
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
