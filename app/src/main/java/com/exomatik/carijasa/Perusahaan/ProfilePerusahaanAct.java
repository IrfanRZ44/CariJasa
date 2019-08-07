package com.exomatik.carijasa.Perusahaan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.exomatik.carijasa.Activity.LihatFoto;
import com.exomatik.carijasa.Adapter.RecyclerJasaPerusahaan;
import com.exomatik.carijasa.Featured.ItemClickSupport;
import com.exomatik.carijasa.Featured.UserSave;
import com.exomatik.carijasa.Model.ModelJasa;
import com.exomatik.carijasa.R;
import com.exomatik.carijasa.Activity.SplashActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePerusahaanAct extends AppCompatActivity{
    public ImageButton btnLogout, btnEdit;
    private UserSave userSave;
    public CircleImageView imgUser;
    public TextView textNama, textAlamat, textPhone, textEmail, textNothing;
    public ProgressDialog progressDialog;
    public Button btnTambah;
    public RecyclerJasaPerusahaan adapterJasa;
    public RecyclerView rcJasa;
    public ArrayList<ModelJasa> listJasa = new ArrayList<ModelJasa>();
    public View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_profile_perusahaan);

        init();

        setData();
        setAdapter();
        getData();
    }

    public void setData() {
        progressDialog = new ProgressDialog(ProfilePerusahaanAct.this);
        progressDialog.setMessage(getResources().getString(R.string.progress_title1));
        progressDialog.setTitle(getResources().getString(R.string.progress_text1));
        progressDialog.setCancelable(false);
        progressDialog.show();

        textNama.setText(userSave.getKEY_USER().getNamaLengkap());
        textEmail.setText(userSave.getKEY_USER().getEmail());

        if (userSave.getKEY_USER().getAlamat() == null) {
            textAlamat.setText("-");
        } else {
            textAlamat.setText(userSave.getKEY_USER().getAlamat());
        }

        if (userSave.getKEY_USER().getNoHp() == null) {
            textPhone.setText("-");
        } else {
            textPhone.setText(userSave.getKEY_USER().getNoHp());
        }

        if (userSave.getKEY_USER().getFoto() == null) {
            imgUser.setImageResource(R.drawable.logo2);
        } else {
            Uri localUri = Uri.parse(userSave.getKEY_USER().getFoto());
            Picasso.with(this).load(localUri).into(imgUser);
        }
    }

    private void init() {
        btnLogout = (ImageButton) findViewById(R.id.btn_logout);
        btnEdit = (ImageButton) findViewById(R.id.btn_edit);
        imgUser = (CircleImageView) findViewById(R.id.img_user);
        textNama = (TextView) findViewById(R.id.text_nama);
        textAlamat = (TextView) findViewById(R.id.text_alamat);
        textPhone = (TextView) findViewById(R.id.text_phone);
        textEmail = (TextView) findViewById(R.id.text_email);
        textNothing = (TextView) findViewById(R.id.text_nothing);
        rcJasa = (RecyclerView) findViewById(R.id.rc_jasa);
        btnTambah = (Button) findViewById(R.id.btn_tambah);
        rcJasa = (RecyclerView) findViewById(R.id.rc_jasa);
        view = (View) findViewById(android.R.id.content);

        userSave = new UserSave(this);

        onClick();
    }

    public void onClick(){
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSave.setKEY_USER(null);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfilePerusahaanAct.this, SplashActivity.class));
                finish();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePerusahaanAct.this, EditProfilAct.class));
                finish();
            }
        });

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePerusahaanAct.this, FormJasa.class));
                finish();
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userSave.getKEY_USER().getFoto() != null) {
                    startActivity(new Intent(ProfilePerusahaanAct.this, LihatFoto.class));
                }
            }
        });

        ItemClickSupport.addTo(rcJasa).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                FormJasa.detailJasa = listJasa.get(position);
                startActivity(new Intent(ProfilePerusahaanAct.this, FormJasa.class));
                finish();
            }
        });
    }

    public void getData() {
        FirebaseDatabase.getInstance()
                .getReference("jasa")
                .child(userSave.getKEY_USER().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ModelJasa data = snapshot.getValue(ModelJasa.class);

                                listJasa.add(data);
                                adapterJasa.notifyDataSetChanged();
                            }
                        }

                        if (listJasa.size() == 0) {
                            textNothing.setVisibility(View.VISIBLE);
                            rcJasa.setVisibility(View.GONE);
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Snackbar snackbar = Snackbar
                                .make(view, getResources().getString(R.string.error) + databaseError.getMessage().toString(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
    }

    private void setAdapter() {
        adapterJasa = new RecyclerJasaPerusahaan(listJasa, ProfilePerusahaanAct.this);
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(ProfilePerusahaanAct.this, LinearLayoutManager.VERTICAL, false);
        rcJasa.setLayoutManager(localLinearLayoutManager);
        rcJasa.setNestedScrollingEnabled(false);
        rcJasa.setAdapter(adapterJasa);
    }
}
