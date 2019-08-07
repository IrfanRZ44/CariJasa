package com.exomatik.carijasa.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.exomatik.carijasa.Adapter.RecyclerPerusahaan;
import com.exomatik.carijasa.Featured.ItemClickSupport;
import com.exomatik.carijasa.Featured.UserSave;
import com.exomatik.carijasa.Model.ModelUser;
import com.exomatik.carijasa.Pekerja.DetailPerusahaanAct;
import com.exomatik.carijasa.Pekerja.ListPerusahaanTerdekatAct;
import com.exomatik.carijasa.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by IrfanRZ on 21/09/2018.
 */

public class SemuaPerusahaanFragment extends Fragment {
    private View v;
    private UserSave userSave;
    private ProgressDialog progressDialog;
    private Button btnMaps;
    private RecyclerView rcCompany;
    private RecyclerPerusahaan adapterPerusahaan;
    private ArrayList<ModelUser> listPerusahaan = new ArrayList<ModelUser>();

    public SemuaPerusahaanFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.frag_semua, container, false);

        init();

        setAdapter();
        getDataCompany();
        onClick();

        return v;
    }

    private void onClick() {
        ItemClickSupport.addTo(rcCompany).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                DetailPerusahaanAct.detailPerusahaan = listPerusahaan.get(position);
                startActivity(new Intent(getActivity(), DetailPerusahaanAct.class));
            }
        });

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListPerusahaanTerdekatAct.listPerusahaan = listPerusahaan;
                startActivity(new Intent(getActivity(), ListPerusahaanTerdekatAct.class));
            }
        });
    }

    private void init() {
        btnMaps = (Button) v.findViewById(R.id.btn_maps);
        rcCompany = (RecyclerView) v.findViewById(R.id.rc_company);

        userSave = new UserSave(getContext());
    }

    private void setAdapter() {
        adapterPerusahaan = new RecyclerPerusahaan(listPerusahaan, getContext());
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        rcCompany.setLayoutManager(localLinearLayoutManager);
        rcCompany.setNestedScrollingEnabled(false);
        rcCompany.setAdapter(adapterPerusahaan);
    }

    private void getDataCompany() {
        FirebaseDatabase.getInstance()
                .getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listPerusahaan.removeAll(listPerusahaan);
                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();
                            while (localIterator.hasNext()) {
                                ModelUser localDataUser = (ModelUser) ((DataSnapshot) localIterator.next()).getValue(ModelUser.class);

                                if (localDataUser.getFoto() != null){
                                    listPerusahaan.add(localDataUser);
                                    adapterPerusahaan.notifyDataSetChanged();
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
