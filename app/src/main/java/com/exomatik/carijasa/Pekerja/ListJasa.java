package com.exomatik.carijasa.Pekerja;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.carijasa.Adapter.RecyclerJasaPerusahaan;
import com.exomatik.carijasa.Adapter.RecyclerPerusahaan;
import com.exomatik.carijasa.Featured.ItemClickSupport;
import com.exomatik.carijasa.Featured.UserSave;
import com.exomatik.carijasa.Model.ModelJasa;
import com.exomatik.carijasa.Model.ModelUser;
import com.exomatik.carijasa.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListJasa extends AppCompatActivity {
    public static String namaKategori;
    private View view;
    private ArrayList<String> listKategori = new ArrayList<String>();
    private RecyclerView rcKategori;
    private RecyclerJasaPerusahaan adapterJasa;
    private ArrayList<ModelJasa> listJasa = new ArrayList<ModelJasa>();
    private TextView textNothing;
    private UserSave userSave;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_list_jasa);

        init();
        showProgress();
        setAdapter();
        getData();
        onClick();
    }

    private void init() {
        rcKategori = (RecyclerView) findViewById(R.id.rc_kategori);
        textNothing = (TextView) findViewById(R.id.text_nothing);

        userSave = new UserSave(ListJasa.this);
    }

    @Override
    public void onBackPressed() {
        namaKategori = null;
        finish();
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(ListJasa.this);
        progressDialog.setMessage(getResources().getString(R.string.progress_title1));
        progressDialog.setTitle(getResources().getString(R.string.progress_text1));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    private void onClick() {

        ItemClickSupport.addTo(rcKategori).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                getDataLocation(listJasa.get(position));
            }
        });
    }

    private void setAdapter() {
        adapterJasa = new RecyclerJasaPerusahaan(listJasa, ListJasa.this);
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(ListJasa.this, 1, false);
        rcKategori.setLayoutManager(localLinearLayoutManager);
        rcKategori.setNestedScrollingEnabled(false);
        rcKategori.setAdapter(adapterJasa);
    }

    private void getData() {
        FirebaseDatabase.getInstance()
                .getReference("jasa")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator localIterator1 = dataSnapshot.getChildren().iterator();
                        listJasa.removeAll(listJasa);
                        boolean cek = true;

                        if (dataSnapshot.exists()) {
                            while (localIterator1.hasNext()) {
                                DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                                Iterator local = localDataSnapshot.getChildren().iterator();
                                while (local.hasNext()) {
                                    DataSnapshot dataDS = (DataSnapshot) local.next();
                                    ModelJasa data = (ModelJasa) ((DataSnapshot) dataDS).getValue(ModelJasa.class);

                                    if (data.getKategori().equals(namaKategori)) {
                                        listJasa.add(data);
                                        adapterJasa.notifyDataSetChanged();
                                        cek = false;
                                    }
                                }
                            }
                        }


                        if (cek) {
                            adapterJasa.notifyDataSetChanged();
                            textNothing.setVisibility(View.VISIBLE);
                        } else {
                            textNothing.setVisibility(View.GONE);
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

    private void getDataLocation(final ModelJasa dataJasa) {
        FirebaseDatabase.getInstance()
                .getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();
                            while (localIterator.hasNext()) {
                                ModelUser localDataUser = (ModelUser) ((DataSnapshot) localIterator.next()).getValue(ModelUser.class);
                                if (localDataUser.getUid().toString().equals(dataJasa.getUid())) {
                                    DetailJasaAct.dataJasa = dataJasa;
                                    DetailJasaAct.dataUser = localDataUser;
                                    startActivity(new Intent(ListJasa.this, DetailJasaAct.class));
                                }
                            }
                        } else {
                            Toast.makeText(ListJasa.this, getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ListJasa.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
