package com.exomatik.carijasa.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.carijasa.Adapter.RecyclerJasaPerusahaan;
import com.exomatik.carijasa.Featured.ItemClickSupport;
import com.exomatik.carijasa.Featured.UserSave;
import com.exomatik.carijasa.Model.ModelJasa;
import com.exomatik.carijasa.Model.ModelUser;
import com.exomatik.carijasa.Pekerja.DetailJasaAct;
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
    private RecyclerJasaPerusahaan adapterJasa;
    private ArrayList<ModelJasa> listJasa = new ArrayList<ModelJasa>();

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
        adapterJasa = new RecyclerJasaPerusahaan(listJasa, getContext());
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        rcKategori.setLayoutManager(localLinearLayoutManager);
        rcKategori.setNestedScrollingEnabled(false);
        rcKategori.setAdapter(adapterJasa);
    }

    private void getData(final String nama) {
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

                                    if (nama == null){
                                        listJasa.add(data);
                                        adapterJasa.notifyDataSetChanged();
                                        cek = false;
                                    }
                                    else {
                                        if (data.getNamaJasa().contains(nama)) {
                                            listJasa.add(data);
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
                        }
                        else {
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

                if (cari.isEmpty()){
                    getData(null);
                }
                else {
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
                getDataLocation(listJasa.get(position));
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
                                    startActivity(new Intent(getActivity(), DetailJasaAct.class));
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
