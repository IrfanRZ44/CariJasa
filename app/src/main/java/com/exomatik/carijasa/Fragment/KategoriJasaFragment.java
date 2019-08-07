package com.exomatik.carijasa.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.carijasa.Adapter.RecyclerJasaPerusahaan;
import com.exomatik.carijasa.Featured.DataKategori;
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

public class KategoriJasaFragment extends Fragment{
    private View v;
    private UserSave userSave;
    private ProgressDialog progressDialog;
    private Spinner spinnerKategori;
    private ArrayList<String> listKategori = new ArrayList<String>();
    private RecyclerView rcKategori;
    private RecyclerJasaPerusahaan adapterJasa;
    private ArrayList<ModelJasa> listJasa = new ArrayList<ModelJasa>();
    private TextView textNothing;

    public KategoriJasaFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.frag_kategori, container, false);

        init();

        setSpinner();

        setAdapter();

        onClick();

        return v;
    }

    private void onClick() {
        spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getResources().getString(R.string.progress_title1));
                progressDialog.setTitle(getResources().getString(R.string.progress_text1));
                progressDialog.setCancelable(false);
                progressDialog.show();
                getData(listKategori.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ItemClickSupport.addTo(rcKategori).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                getDataLocation(listJasa.get(position));
            }
        });
    }

    private void init() {
        spinnerKategori = (Spinner) v.findViewById(R.id.spinner_kategori);
        rcKategori = (RecyclerView) v.findViewById(R.id.rc_kategori);
        textNothing = (TextView) v.findViewById(R.id.text_nothing);

        userSave = new UserSave(getContext());
    }

    private void setSpinner() {
        listKategori = new DataKategori().DataKategori("-");

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getContext(),
                R.layout.spinner_text_putih, listKategori);
        spinnerKategori.setAdapter(adapterSpinner);
    }

    private void getData(final String kategori) {
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

                                    if (data.getKategori().equals(kategori)) {
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
                                .make(v, getResources().getString(R.string.error) + databaseError.getMessage().toString(), Snackbar.LENGTH_LONG);
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

    private void setAdapter() {
        adapterJasa = new RecyclerJasaPerusahaan(listJasa, getContext());
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        rcKategori.setLayoutManager(localLinearLayoutManager);
        rcKategori.setNestedScrollingEnabled(false);
        rcKategori.setAdapter(adapterJasa);
    }
}
