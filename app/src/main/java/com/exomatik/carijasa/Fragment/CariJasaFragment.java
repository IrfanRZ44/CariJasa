package com.exomatik.carijasa.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.carijasa.Activity.SplashActivity;
import com.exomatik.carijasa.Adapter.RecyclerPerusahaan;
import com.exomatik.carijasa.Featured.ItemClickSupport;
import com.exomatik.carijasa.Featured.UserSave;
import com.exomatik.carijasa.Model.ModelJasa;
import com.exomatik.carijasa.Model.ModelUser;
import com.exomatik.carijasa.Pekerja.DetailJasaAct;
import com.exomatik.carijasa.Pekerja.DetailPerusahaanAct;
import com.exomatik.carijasa.Pekerja.MainPekerjaAct;
import com.exomatik.carijasa.Perusahaan.ProfilePerusahaanAct;
import com.exomatik.carijasa.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by IrfanRZ on 21/09/2018.
 */

public class CariJasaFragment extends Fragment {
    private View v;
    private UserSave userSave;
    private ProgressDialog progressDialog;
    private EditText etCari;
    private RecyclerView rcKategori;
    private TextView textNothing;
    private RecyclerPerusahaan adapterJasa;
    private ArrayList<ModelUser> listPerusahaan = new ArrayList<ModelUser>();

    public CariJasaFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.frag_cari, container, false);

        init();

        setAdapter();
        onClick();
        getData(null);

        return v;
    }

    private void init() {
        etCari = (EditText) v.findViewById(R.id.et_cari);
        rcKategori = (RecyclerView) v.findViewById(R.id.rc_kategori);
        textNothing = (TextView) v.findViewById(R.id.text_nothing);

        userSave = new UserSave(getContext());
    }

    private void setAdapter() {
        adapterJasa = new RecyclerPerusahaan(listPerusahaan, getContext());
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        rcKategori.setLayoutManager(localLinearLayoutManager);
        rcKategori.setNestedScrollingEnabled(false);
        rcKategori.setAdapter(adapterJasa);
    }

    private void getData(final String nama) {
        FirebaseDatabase.getInstance()
                .getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listPerusahaan.removeAll(listPerusahaan);
                        boolean cek = true;
                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();
                            while (localIterator.hasNext()) {
                                ModelUser data = (ModelUser) ((DataSnapshot) localIterator.next()).getValue(ModelUser.class);

                                if (data.getJenisAkun().equals(getResources().getString(R.string.akun_1)) && data.getFoto() != null){
                                    if (nama == null) {
                                        listPerusahaan.add(data);
                                        adapterJasa.notifyDataSetChanged();
                                        cek = false;
                                    } else {

                                        if (data.getNamaLengkap().contains(nama)) {
                                            listPerusahaan.add(data);
                                            adapterJasa.notifyDataSetChanged();
                                            cek = false;
                                        }
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Snackbar snackbar = Snackbar
                                .make(v, getResources().getString(R.string.error) + databaseError.getMessage().toString(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
    }

    private void onClick() {
        etCari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cari = etCari.getText().toString();

                if (cari.isEmpty()) {
                    getData(null);
                } else {
                    getData(cari);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ItemClickSupport.addTo(rcKategori).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                getDataLocation(listPerusahaan.get(position));
            }
        });
    }

    private void getDataLocation(final ModelUser dataPerusahaan) {
        FirebaseDatabase.getInstance()
                .getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();
                            while (localIterator.hasNext()) {
                                ModelUser localDataUser = (ModelUser) ((DataSnapshot) localIterator.next()).getValue(ModelUser.class);
                                if (localDataUser.getUid().toString().equals(dataPerusahaan.getUid())) {
                                    DetailPerusahaanAct.detailPerusahaan = dataPerusahaan;
                                    startActivity(new Intent(getActivity(), DetailPerusahaanAct.class));
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
