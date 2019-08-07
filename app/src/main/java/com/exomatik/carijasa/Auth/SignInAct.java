package com.exomatik.carijasa.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.exomatik.carijasa.R;
import com.exomatik.carijasa.Activity.SplashActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignInAct extends AppCompatActivity {
    private Button btnSignIn, btnSignUp;
    private EditText etEmail, etPassword;
    private CheckBox cbShow;
    private ProgressDialog progressDialog;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sign_in);

        init();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInAct.this, SignUpAct.class));
                finish();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekEditText();
            }
        });

        cbShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean) {
                if (paramAnonymousBoolean) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    return;
                }
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    private void init() {
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_pass);
        cbShow = (CheckBox) findViewById(R.id.show_pass);

        v = (View) findViewById(android.R.id.content);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void cekEditText() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()){
            if (email.isEmpty()){
                etEmail.setError(getResources().getString(R.string.error_data_kosong));
            }
            if (password.isEmpty()){
                etPassword.setError(getResources().getString(R.string.error_data_kosong));
            }
        }
        else {
            progressDialog = new ProgressDialog(SignInAct.this);
            progressDialog.setMessage(getResources().getString(R.string.progress_title1));
            progressDialog.setTitle(getResources().getString(R.string.progress_text1));
            progressDialog.setCancelable(false);
            progressDialog.show();
            prosesLogin(email, password);
        }
    }

    private void prosesLogin(String email, String pass) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    startActivity(new Intent(SignInAct.this, SplashActivity.class));
                    finish();
                }else {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(v, "Mohon maaf, " + task.getException().getMessage().toString() , Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                if (e.getMessage().toString().contains("There is no user record")){
                    etEmail.setError(getResources().getString(R.string.error_email_not_found));
                }
                Snackbar snackbar = Snackbar
                        .make(v, "Mohon maaf, " + e.getMessage().toString() , Snackbar.LENGTH_LONG);
                snackbar.setAction("Klik untuk Daftar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(SignInAct.this, SignUpAct.class));
                        finish();
                    }
                });
                snackbar.show();
            }
        });
    }
}
