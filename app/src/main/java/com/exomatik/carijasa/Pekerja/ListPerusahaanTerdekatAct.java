package com.exomatik.carijasa.Pekerja;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.carijasa.Adapter.RecyclerFotoJasa;
import com.exomatik.carijasa.Adapter.RecyclerPerusahaan;
import com.exomatik.carijasa.Featured.ItemClickSupport;
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
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.NumberFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListPerusahaanTerdekatAct extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    public static ArrayList<ModelUser> listPerusahaan = new ArrayList<ModelUser>();
    private ImageView back;
    private GoogleMap mMap;
    private View v;
    private RecyclerView rcFoto;
    private RecyclerPerusahaan adapterPerusahaan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_list_perusahaan_terdekat);

        init();

        setData();
        setAdapter();
        onClick();
    }

    private void onClick() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPerusahaan = null;
                finish();
            }
        });

        ItemClickSupport.addTo(rcFoto).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                DetailPerusahaanAct.detailPerusahaan = listPerusahaan.get(position);
                startActivity(new Intent(ListPerusahaanTerdekatAct.this, DetailPerusahaanAct.class));
                listPerusahaan = null;
                finish();
            }
        });
    }

    private void init() {
        back = (ImageView) findViewById(R.id.back);
        rcFoto = (RecyclerView) findViewById(R.id.rc_perusahaan);
    }

    private void setData() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(ListPerusahaanTerdekatAct.this);
    }

    @Override
    public void onBackPressed() {
        listPerusahaan = null;
        finish();
    }

    private void setAdapter() {
        adapterPerusahaan = new RecyclerPerusahaan(listPerusahaan, ListPerusahaanTerdekatAct.this);
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(ListPerusahaanTerdekatAct.this, LinearLayoutManager.VERTICAL, false);
        rcFoto.setLayoutManager(localLinearLayoutManager);
        rcFoto.setNestedScrollingEnabled(false);
        rcFoto.setAdapter(adapterPerusahaan);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = null;

        for (int a = 0; a < listPerusahaan.size(); a++){
            String replace = listPerusahaan.get(a).getLocationPoint().replace("lat/lng: (", "");
            replace = replace.replace(")", "");
            String lat[] = replace.split(",");
            sydney = new LatLng(Float.parseFloat(lat[0]), Float.parseFloat(lat[1]));

            mMap.addMarker(new MarkerOptions().position(sydney).title(listPerusahaan.get(a).getNamaLengkap()));
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13.0f));

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Periksa Koneksi Anda", Toast.LENGTH_SHORT).show();
    }
}
