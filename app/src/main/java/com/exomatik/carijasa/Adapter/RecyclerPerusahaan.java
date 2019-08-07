package com.exomatik.carijasa.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exomatik.carijasa.Model.ModelJasa;
import com.exomatik.carijasa.Model.ModelUser;
import com.exomatik.carijasa.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by IrfanRZ on 03/08/2019.
 */

public class RecyclerPerusahaan extends RecyclerView.Adapter<RecyclerPerusahaan.bidangViewHolder> {
    private ArrayList<ModelUser> dataList;
    private Context context;
    private ProgressDialog progressDialog = null;

    public RecyclerPerusahaan(ArrayList<ModelUser> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public bidangViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_perusahaan, parent, false);

        this.context = parent.getContext();
        return new bidangViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(bidangViewHolder holder, final int position) {
        Uri localUri = Uri.parse(dataList.get(position).getFoto());
        Picasso.with(context).load(localUri).into(holder.imgPerusahaan);

        holder.textNama.setText(dataList.get(position).getNamaLengkap());
        holder.textAlamat.setText(dataList.get(position).getAlamat());
        holder.textPhone.setText(dataList.get(position).getNoHp());
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder {
        private TextView textNama, textPhone, textAlamat;
        private CircleImageView imgPerusahaan;

        public bidangViewHolder(View itemView) {
            super(itemView);

            textNama = (TextView) itemView.findViewById(R.id.text_title);
            textAlamat = (TextView) itemView.findViewById(R.id.text_alamat);
            textPhone = (TextView) itemView.findViewById(R.id.text_phone);
            imgPerusahaan = (CircleImageView) itemView.findViewById(R.id.img_user);
        }
    }
}