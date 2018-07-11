package com.example.android.bookkeeping.firebase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookkeeping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class FirebaseStartActivity extends AppCompatActivity {
    private static final String LOG_TAG = "myfirebasestart";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText ETemail;
    private EditText ETpassword;

    private Button buttonReg;
    private Button buttonAuth;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_firebase);


        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {



                } else {
                    Toast.makeText(FirebaseStartActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();

                }

            }
        };

        ETemail =  findViewById(R.id.et_email);
        ETpassword =  findViewById(R.id.et_password);
        buttonReg = findViewById(R.id.btn_registration);
        buttonAuth = findViewById(R.id.btn_sign_in);


        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ETpassword.getText().length() < 6) {
                    Toast.makeText(FirebaseStartActivity.this, "Password must be at least 6 characters long ", Toast.LENGTH_SHORT).show();
                } else {
                    registration(ETemail.getText().toString(), ETpassword.getText().toString());
                }
            }
        });

        buttonAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    signin(ETemail.getText().toString(), ETpassword.getText().toString());
            }
        });


    }


    public void signin(String email , String password)
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Intent intent = new Intent(context, FirebaseStorageActivity.class);
                    startActivity(intent);
                }else
                    Toast.makeText(FirebaseStartActivity.this, "Authorization fail", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void registration (String email , String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(FirebaseStartActivity.this, "Registration successfull", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.i(LOG_TAG, task.getException().getMessage());
                    Log.i(LOG_TAG, task.getException().toString());
                    Toast.makeText(FirebaseStartActivity.this, "Registration fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
