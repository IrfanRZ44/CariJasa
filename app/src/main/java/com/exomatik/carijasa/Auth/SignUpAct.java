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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.carijasa.Model.ModelUser;
import com.exomatik.carijasa.R;
import com.exomatik.carijasa.Activity.SplashActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpAct extends AppCompatActivity {
    private Button btnSignIn, btnSignUp;
    private EditText etEmail, etPassword, etNama;
    private CheckBox cbShow;
    private RelativeLayout btnJenis1, btnJenis2;
    private ProgressDialog progressDialog;
    private String jenisAkun;
    private View v;
    private TextView textNama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sign_up);

        init();

        jenisAkun = getResources().getString(R.string.akun_1);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpAct.this, SignInAct.class));
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekEditText();
            }
        });

        btnJenis1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenisAkun = getResources().getString(R.string.akun_1);
                textNama.setText(getResources().getString(R.string.nama_perusahaan));
                btnJenis1.setBackground(getResources().getDrawable(R.drawable.border_rl_gradient_blue));
                btnJenis2.setBackground(getResources().getDrawable(R.drawable.border_rl_gradient_gray));
            }
        });

        btnJenis2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenisAkun = getResources().getString(R.string.akun_2);
                textNama.setText(getResources().getString(R.string.nama_pekerja));
                btnJenis1.setBackground(getResources().getDrawable(R.drawable.border_rl_gradient_gray));
                btnJenis2.setBackground(getResources().getDrawable(R.drawable.border_rl_gradient_blue));
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
        btnJenis1 = (RelativeLayout) findViewById(R.id.rl_jenis1);
        btnJenis2 = (RelativeLayout) findViewById(R.id.rl_jenis2);
        etNama = (EditText) findViewById(R.id.et_nama);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_pass);
        cbShow = (CheckBox) findViewById(R.id.show_pass);
        v = (View) findViewById(android.R.id.content);
        textNama = (TextView) findViewById(R.id.text_nama);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void cekEditText() {
        String nama = etNama.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPassword.getText().toString();

        if (nama.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            if (nama.isEmpty()) {
                etNama.setError(getResources().getString(R.string.error_data_kosong));
            }
            if (email.isEmpty()) {
                etEmail.setError(getResources().getString(R.string.error_data_kosong));
            }
            if (pass.isEmpty()) {
                etPassword.setError(getResources().getString(R.string.error_data_kosong));
            }
        } else {
            progressDialog = new ProgressDialog(SignUpAct.this);
            progressDialog.setMessage(getResources().getString(R.string.progress_title1));
            progressDialog.setTitle(getResources().getString(R.string.progress_text1));
            progressDialog.setCancelable(false);
            progressDialog.show();
            prosesDaftar(nama, email, pass, jenisAkun);
        }
    }

    private void prosesDaftar(final String nama, final String email, String pass, final String jenis) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    addUsertoFirebase(task.getResult().getUser(), nama, email, jenis);
                } else {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(v, getResources().getString(R.string.error_unknown), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                if (e.getMessage().toString().contains("email address is already in use")) {
                    etEmail.setError(getResources().getString(R.string.error_email_terdaftar));
                    Snackbar snackbar = Snackbar
                            .make(v, getResources().getString(R.string.error_email_terdaftar), Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(v, getResources().getString(R.string.error) + e.getMessage().toString(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    private void addUsertoFirebase(FirebaseUser firebaseUser, final String nama,
                                   final String email, final String jenis) {
        final ModelUser userData = new ModelUser(nama, email, firebaseUser.getUid(), jenis, null, null, null, null);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child("users")
                .child(userData.getUid())
                .setValue(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            startActivity(new Intent(SignUpAct.this, SplashActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Snackbar snackbar = Snackbar
                                    .make(v, getResources().getString(R.string.error) + task.getException().getMessage().toString(), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignUpAct.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
