package com.exomatik.carijasa.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.carijasa.Auth.SignInAct;
import com.exomatik.carijasa.Featured.UserSave;
import com.exomatik.carijasa.Model.ModelUser;
import com.exomatik.carijasa.Pekerja.MainPekerjaAct;
import com.exomatik.carijasa.Perusahaan.ProfilePerusahaanAct;
import com.exomatik.carijasa.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private boolean back = false;
    private TextView textMaintenance;
    private UserSave userSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);

        init();

        getDataMaintenance();
    }

    private void init() {
        textMaintenance = (TextView) findViewById(R.id.text_maintenance);

        userSave = new UserSave(this);
    }

    private void getDataMaintenance() {
        FirebaseDatabase.getInstance().getReference("maintenance").addListenerForSingleValueEvent(this.valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            back = true;
            Toast.makeText(SplashActivity.this, paramAnonymousDatabaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

        public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
            if (paramAnonymousDataSnapshot.exists()) {
                Iterator localIterator = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator.hasNext()) {
                    String localDataString = (String) ((DataSnapshot) localIterator.next()).getValue(String.class);

                    if (localDataString.equals("aktif")){
                        appsActive();
                    }
                    else if (localDataString.equals("maintenance")){
                        textMaintenance.setVisibility(View.VISIBLE);
                        textMaintenance.setText(getResources().getString(R.string.text_maintenance));
                        back = true;
                    }
                    else if (localDataString.equals("service")){
                        textMaintenance.setVisibility(View.VISIBLE);
                        textMaintenance.setText(getResources().getString(R.string.text_will_maintenance_soon));

                        appsActive();
                    }
                }
            }
        }
    };

    private void appsActive(){
        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    saveDataUser(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                }
                else {
                    Intent homeIntent = new Intent(SplashActivity.this, SignInAct.class);
                    startActivity(homeIntent);
                    finish();
                }
            }
        }, 2000L);
    }

    @Override
    public void onBackPressed() {
        if (back){
            finish();
        }
    }

    private void saveDataUser(final String email){
        FirebaseDatabase.getInstance()
                .getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();
                            while (localIterator.hasNext()) {
                                ModelUser localDataUser = (ModelUser) ((DataSnapshot) localIterator.next()).getValue(ModelUser.class);
                                if (localDataUser.getEmail().toString().equalsIgnoreCase(email)){

                                    userSave.setKEY_USER(localDataUser);

                                    if (localDataUser.getJenisAkun().equals(getResources().getString(R.string.akun_1))){
                                        startActivity(new Intent(SplashActivity.this, ProfilePerusahaanAct.class));
                                        finish();
                                    }
                                    else if (localDataUser.getJenisAkun().equals(getResources().getString(R.string.akun_2))){
                                        startActivity(new Intent(SplashActivity.this, MainPekerjaAct.class));
                                        finish();
                                    }
                                }
                            }
                        }
                        else {
                            Toast.makeText(SplashActivity.this, getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(SplashActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
