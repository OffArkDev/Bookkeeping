package com.example.android.bookkeeping.ui.cloud.storage;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.bookkeeping.Constants;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.model.AccountSaver;
import com.example.android.bookkeeping.model.DataCloud;
import com.example.android.bookkeeping.model.TransactionSaver;
import com.example.android.bookkeeping.repository.AccountsRepository;
import com.example.android.bookkeeping.repository.TransactionsRepository;
import com.example.android.bookkeeping.ui.mvp.BasePresenter;
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

public class FirebaseStoragePresenter<V extends FirebaseStorageMvpView> extends BasePresenter<V> implements FirebaseStorageMvpPresenter<V> {

    private final static String TAG = "storagePresenter";

    private String email;

    private DataCloud dataCloud = new DataCloud();

    private boolean isOneFlowLoaded = false;

    @Inject
    AccountsRepository accountsRepository;

    @Inject
    TransactionsRepository transactionsRepository;

    @Inject
    CompositeDisposable compositeDisposable;

    @Inject
    FirebaseStoragePresenter() {
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);

        getDataFromDatabase();
    }


    @Override
    public void getEmailFromIntent(Intent intent) {
        email = intent.getStringExtra("email");
    }

    @Override
    public void btnSaveClick() {
        saveToCloud();
    }

    @Override
    public void btnLoadClick() {
        loadFromCloud();
    }

    private void getDataFromDatabase() {
        getMvpView().showLoading();

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

    private void saveToCloud() {
        getMvpView().showLoading();
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
                getMvpView().hideLoading();

            }
        }).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getMvpView().showMessage( R.string.saved_success);
                        getMvpView().hideLoading();

                    }
                });
    }

    private void hideBarIfFlowLoaded() {
        if (!isOneFlowLoaded) {
            isOneFlowLoaded = true;
        } else {
            getMvpView().hideLoading();
        }
    }


    private void loadFromCloud() {
        getMvpView().showLoading();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference().child(Constants.CLOUD_DATA_PATH + email);
        final long maxDownLoadSize = 5 * 1024 * 1024;
        storageRef.getBytes(maxDownLoadSize).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if (bytes == null) {
                    getMvpView().showMessage(R.string.no_data_cloud);
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
                getMvpView().showMessage(R.string.loading_failed);
                getMvpView().hideLoading();
            }
        });
    }


    private void updateData() {
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
                               public void accept(Throwable throwable) {
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
                               public void accept(Throwable throwable) {
                                   Log.e(TAG, "account deletion failed " + throwable.getMessage());
                               }
                           }
                ));

    }

    private void loadAccountsToInternal() {
        List<AccountSaver> listAccounts = dataCloud.getAccountsList();

        compositeDisposable.add(accountsRepository.insertList(listAccounts)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<long[]>() {
                    @Override
                    public void accept(long[] aLong) {
                        Log.i(TAG, "insert accounts success");
                        if (isOneFlowLoaded) {
                            getMvpView().hideLoading();
                            getMvpView().showMessage(R.string.loading_database_success);
                            getMvpView().returnResult();
                        } else isOneFlowLoaded = true;
                    }
                }));
    }

    private void loadTransactionsToInternal() {
        List<TransactionSaver> listTransactions = dataCloud.getTransactionList();

        compositeDisposable.add(transactionsRepository.insertList(listTransactions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<long[]>() {
                    @Override
                    public void accept(long[] aLong) {
                        Log.i(TAG, "insert transactions success");
                        if (isOneFlowLoaded) {
                            getMvpView().hideLoading();
                            getMvpView().showMessage(R.string.loading_database_success);
                            getMvpView().returnResult();
                        } else isOneFlowLoaded = true;
                    }
                }));
    }
}
