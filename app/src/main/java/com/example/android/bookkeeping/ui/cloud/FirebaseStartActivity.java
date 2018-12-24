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

import com.example.android.bookkeeping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class FirebaseStartActivity extends AppCompatActivity {

    private static final String TAG = "myfirebasestart";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText etEmail;
    private EditText etPassword;

    private Button btnReg;
    private Button btnAuth;

    private Context context = this;

    private ProgressBar pbProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_firebase);

        findViews();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.i(TAG, "firebase auth success");
                } else {
                    Toast.makeText(FirebaseStartActivity.this, "Firebase auth failed", Toast.LENGTH_SHORT).show();
                }
            }
        };

        setOnClickListeners();
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
                    Toast.makeText(FirebaseStartActivity.this, "Password must be at least 6 characters long ", Toast.LENGTH_SHORT).show();
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
                    intent.putExtra("email", email);
                    startActivity(intent);
                    showOrHideProgress(false);
                }
                else {
                    Toast.makeText(FirebaseStartActivity.this, "Authorization fail", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(FirebaseStartActivity.this, "Registration successfull", Toast.LENGTH_SHORT).show();
                    showOrHideProgress(false);

                }
                else {
                    Log.i(TAG, task.getException().getMessage());
                    Log.i(TAG, task.getException().toString());
                    Toast.makeText(FirebaseStartActivity.this, "Registration fail", Toast.LENGTH_SHORT).show();
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
