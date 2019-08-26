package com.exomatik.carijasa.Pekerja;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.exomatik.carijasa.Featured.ItemClickSupport;
import com.exomatik.carijasa.Model.ModelJasa;
import com.exomatik.carijasa.Model.ModelUser;
import com.exomatik.carijasa.Perusahaan.ProfilePerusahaanAct;
import com.exomatik.carijasa.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by IrfanRZ on 06/08/2019.
 */

public class DetailPerusahaanAct extends ProfilePerusahaanAct{
    public static ModelUser detailPerusahaan;
    private static final int REQUEST_PHONE_CALL = 1;

    @Override
    public void setData() {
        progressDialog = new ProgressDialog(DetailPerusahaanAct.this);
        progressDialog.setMessage(getResources().getString(R.string.progress_title1));
        progressDialog.setTitle(getResources().getString(R.string.progress_text1));
        progressDialog.setCancelable(false);
        progressDialog.show();

        btnEdit.setVisibility(View.GONE);
        btnLogout.setVisibility(View.GONE);
        btnTambah.setVisibility(View.GONE);

        textNothing.setText("Perusahaan ini belum mempunyai jasa untuk ditawarkan");
        textNama.setText(detailPerusahaan.getNamaLengkap());
        textAlamat.setText(detailPerusahaan.getAlamat());
        textEmail.setText(detailPerusahaan.getNoHp());
        textPhone.setText(detailPerusahaan.getEmail());
        Uri localUri = Uri.parse(detailPerusahaan.getFoto());
        Picasso.with(this).load(localUri).into(imgUser);

        btnTelfon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + detailPerusahaan.getNoHp();

                Intent intent = new Intent(Intent.ACTION_DIAL);
//                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "082192759551", null)));
                intent.setData(Uri.parse(uri));
                if (ActivityCompat.checkSelfPermission(DetailPerusahaanAct.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DetailPerusahaanAct.this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                    Toast.makeText(DetailPerusahaanAct.this, "Please, Click Again After You Allow It", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(intent);
                }
            }
        });

        btnMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ detailPerusahaan.getEmail()});
                email.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                email.putExtra(Intent.EXTRA_TEXT, "Mail");

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });
    }

    @Override
    public void getData() {
        FirebaseDatabase.getInstance()
                .getReference("jasa")
                .child(detailPerusahaan.getUid())
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

    @Override
    public void onBackPressed() {
        detailPerusahaan = null;
        finish();
    }

    @Override
    public void onClick() {
        ItemClickSupport.addTo(rcJasa).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                progressDialog = new ProgressDialog(DetailPerusahaanAct.this);
                progressDialog.setMessage(getResources().getString(R.string.progress_title1));
                progressDialog.setTitle(getResources().getString(R.string.progress_text1));
                progressDialog.setCancelable(false);
                progressDialog.show();
                DetailJasaAct.dataJasa = listJasa.get(position);
                DetailJasaAct.dataUser = detailPerusahaan;
                startActivity(new Intent(DetailPerusahaanAct.this, DetailJasaAct.class));

                progressDialog.dismiss();
            }
        });
    }
}
