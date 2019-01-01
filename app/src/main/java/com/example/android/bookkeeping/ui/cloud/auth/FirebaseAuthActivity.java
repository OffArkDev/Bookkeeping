package com.example.android.bookkeeping.ui.cloud.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.bookkeeping.MyApplication;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.di.components.CloudAuthComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.FirebaseModule;
import com.example.android.bookkeeping.ui.cloud.storage.FirebaseStorageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;


public class FirebaseAuthActivity extends AppCompatActivity implements FirebaseAuthMvpView{

    private static final String TAG = "myfirebaseauth";

    private EditText etEmail;
    private EditText etPassword;

    private Button btnReg;
    private Button btnAuth;

    private ProgressBar pbProgress;

    @Inject
    public FirebaseAuth mAuth;

    @Inject
    FirebaseAuthMvpPresenter<FirebaseAuthMvpView> presenter;

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, FirebaseAuthActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_firebase);
        getCloudComponent().inject(this);
        findViews();
        setOnClickListeners();
    }

    public CloudAuthComponent getCloudComponent() {
        return ((MyApplication) getApplication())
                .getApplicationComponent()
                .newCloudAuthComponent(new ActivityModule(this), new FirebaseModule());
    }

    public void findViews() {
        etEmail =  findViewById(R.id.et_email);
        etPassword =  findViewById(R.id.et_password);
        btnReg = findViewById(R.id.btn_registration);
        btnAuth = findViewById(R.id.btn_sign_in);
        pbProgress = findViewById(R.id.progress_bar);
    }

    public void setOnClickListeners() {
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etPassword.getText().length() < 6) {
                    showMessage(R.string.password_longer);
                } else {
                    presenter.btnRegistrationClick(etEmail.getText().toString(), etPassword.getText().toString());
                }
            }
        });

        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.btnAuthorizationClick(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });

    }

    public void signIn(final String email , String password)
    {
        showLoading();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    openFirebaseStorageActivity(email);
                    hideLoading();
                }
                else {
                    showMessage(R.string.auth_fail);
                    hideLoading();
                }
            }
        });
    }

    @Override
    public void openFirebaseStorageActivity(String email) {
        Intent intent = FirebaseStorageActivity.getStartIntent(this, email);
        startActivity(intent);
        finish();
    }

    public void registration (String email , String password){
        showLoading();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    showMessage(R.string.reg_success);
                    hideLoading();
                }
                else {
                    if (task.getException() != null) {
                        Log.i(TAG, task.getException().getMessage());
                        Log.i(TAG, task.getException().toString());
                    }
                    showMessage(R.string.reg_fail);
                    hideLoading();
                }
            }
        });
    }

    @Override
    public void showLoading() {
        pbProgress.setVisibility(View.VISIBLE);
        btnAuth.setVisibility(View.GONE);
        btnReg.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        pbProgress.setVisibility(View.GONE);
        btnAuth.setVisibility(View.VISIBLE);
        btnReg.setVisibility(View.VISIBLE);
    }



    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onError(int resId) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void showMessage(int resId) {
        Toast.makeText(FirebaseAuthActivity.this, resId, Toast.LENGTH_SHORT).show();
    }




}
