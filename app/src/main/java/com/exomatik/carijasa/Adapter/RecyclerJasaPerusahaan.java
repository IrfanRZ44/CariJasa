package com.exomatik.carijasa.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.exomatik.carijasa.Model.ModelJasa;
import com.exomatik.carijasa.R;

import java.text.NumberFormat;
import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by IrfanRZ on 03/08/2019.
 */

public class RecyclerJasaPerusahaan extends RecyclerView.Adapter<RecyclerJasaPerusahaan.bidangViewHolder> {
    private ArrayList<ModelJasa> dataList;
    private Context context;
    private ProgressDialog progressDialog = null;

    public RecyclerJasaPerusahaan(ArrayList<ModelJasa> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public bidangViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_jasa, parent, false);

        this.context = parent.getContext();
        return new bidangViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(bidangViewHolder holder, final int position) {
        NumberFormat format = NumberFormat.getCurrencyInstance();

        String gaji = format.format(Long.parseLong(dataList.get(position).getGaji()));

        holder.textNama.setText(dataList.get(position).getNamaJasa());
        holder.textGaji.setText(gaji);
        holder.textKategori.setText("Kategori : " + dataList.get(position).getKategori());
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder {
        private TextView textNama, textKategori, textGaji;

        public bidangViewHolder(View itemView) {
            super(itemView);

            textNama = (TextView) itemView.findViewById(R.id.text_title);
            textKategori = (TextView) itemView.findViewById(R.id.text_kategori);
            textGaji = (TextView) itemView.findViewById(R.id.text_gaji);
        }
    }
}