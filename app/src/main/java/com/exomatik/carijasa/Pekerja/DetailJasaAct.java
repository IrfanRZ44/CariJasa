package com.exomatik.carijasa.Pekerja;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.carijasa.Adapter.RecyclerFotoJasa;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.NumberFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DetailJasaAct extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    public static ModelJasa dataJasa;
    public static ModelUser dataUser;
    private ImageView back;
    private TextView textNamaPerusahaan, textEmail, textKontak, textAlamat, textGaji;
    private GoogleMap mMap;
    private View v;
    private RecyclerView rcFoto;
    private RecyclerFotoJasa adapterFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_detail_jasa);

        init();

        setData();
        setAdapter();
        onClick();
    }

    private void onClick() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataJasa = null;
                dataUser = null;
                finish();
            }
        });

        ItemClickSupport.addTo(rcFoto).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                LihatFotoJasaAct.namaJasa = dataUser.getNamaLengkap();
                LihatFotoJasaAct.url = dataJasa.getFoto().get(position);
                startActivity(new Intent(DetailJasaAct.this, LihatFotoJasaAct.class));
            }
        });
    }

    private void init() {
        back = (ImageView) findViewById(R.id.back);
        textNamaPerusahaan = (TextView) findViewById(R.id.text_nama);
        textEmail = (TextView) findViewById(R.id.text_email);
        textKontak = (TextView) findViewById(R.id.text_kontak);
        textAlamat = (TextView) findViewById(R.id.text_alamat);
        textGaji = (TextView) findViewById(R.id.text_gaji);
        rcFoto = (RecyclerView) findViewById(R.id.rc_foto);
    }

    private void setData() {
        textNamaPerusahaan.setText(dataUser.getNamaLengkap());
        textEmail.setText(dataUser.getEmail());
        textKontak.setText(dataUser.getNoHp());
        textAlamat.setText(dataUser.getAlamat());

        NumberFormat format = NumberFormat.getCurrencyInstance();

        String gaji = format.format(Long.parseLong(dataJasa.getGaji()));

        textGaji.setText(gaji);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(DetailJasaAct.this);
    }

    @Override
    public void onBackPressed() {
        dataJasa = null;
        dataUser = null;
        finish();
    }

    private void setAdapter() {
        adapterFoto = new RecyclerFotoJasa(dataJasa.getFoto(), DetailJasaAct.this);
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(DetailJasaAct.this, LinearLayoutManager.HORIZONTAL, false);
        rcFoto.setLayoutManager(localLinearLayoutManager);
        rcFoto.setNestedScrollingEnabled(false);
        rcFoto.setAdapter(adapterFoto);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney;

        String replace = dataUser.getLocationPoint().replace("lat/lng: (", "");
        replace = replace.replace(")", "");
        String lat[] = replace.split(",");
        sydney = new LatLng(Float.parseFloat(lat[0]), Float.parseFloat(lat[1]));

        mMap.addMarker(new MarkerOptions().position(sydney).title(dataUser.getNamaLengkap()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.0f));

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Periksa Koneksi Anda", Toast.LENGTH_SHORT).show();
    }


}
