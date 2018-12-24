package com.example.android.bookkeeping.ui.cloud;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;


public class FirebaseAuthActivity extends AppCompatActivity {

    private static final String TAG = "myfirebaseauth";

    private EditText etEmail;
    private EditText etPassword;

    private Button btnReg;
    private Button btnAuth;

    private ProgressBar pbProgress;

    @Inject
    public Context context;

    @Inject
    public FirebaseAuth mAuth;


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
                    Toast.makeText(FirebaseAuthActivity.this, R.string.password_longer, Toast.LENGTH_SHORT).show();
                } else {
                    registration(etEmail.getText().toString(), etPassword.getText().toString());
                }
            }
        });

        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(etEmail.getText().toString().trim(), etPassword.getText().toString());
            }
        });

    }

    public void signIn(final String email , String password)
    {
        showOrHideProgress(true);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Intent intent = new Intent(context, FirebaseStorageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                    showOrHideProgress(false);
                }
                else {
                    Toast.makeText(FirebaseAuthActivity.this, R.string.auth_fail, Toast.LENGTH_SHORT).show();
                    showOrHideProgress(false);
                }
            }
        });
    }
    public void registration (String email , String password){
        showOrHideProgress(true);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(FirebaseAuthActivity.this, R.string.reg_success, Toast.LENGTH_SHORT).show();
                    showOrHideProgress(false);

                }
                else {
                    if (task.getException() != null) {
                        Log.i(TAG, task.getException().getMessage());
                        Log.i(TAG, task.getException().toString());
                    }
                    Toast.makeText(FirebaseAuthActivity.this, R.string.reg_fail, Toast.LENGTH_SHORT).show();
                    showOrHideProgress(false);
                }
            }
        });
    }

    public void showOrHideProgress(Boolean show) {
        if (show) {
            pbProgress.setVisibility(View.VISIBLE);
            btnAuth.setVisibility(View.GONE);
            btnReg.setVisibility(View.GONE);
        } else {
            pbProgress.setVisibility(View.GONE);
            btnAuth.setVisibility(View.VISIBLE);
            btnReg.setVisibility(View.VISIBLE);
        }
    }


}
