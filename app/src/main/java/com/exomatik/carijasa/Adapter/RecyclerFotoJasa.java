package com.exomatik.carijasa.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.exomatik.carijasa.Model.ModelJasa;
import com.exomatik.carijasa.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by IrfanRZ on 03/08/2019.
 */

public class RecyclerFotoJasa extends RecyclerView.Adapter<RecyclerFotoJasa.bidangViewHolder> {
    private ArrayList<String> dataList;
    private Context context;
    private ProgressDialog progressDialog = null;

    public RecyclerFotoJasa(ArrayList<String> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public bidangViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_foto_jasa, parent, false);

        this.context = parent.getContext();
        return new bidangViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(bidangViewHolder holder, final int position) {
        if (dataList.get(position) == null){
            holder.btnAdd.setImageResource(R.drawable.ic_add_blue);
            holder.btnAdd.setBackground(context.getResources().getDrawable(R.drawable.btn_box_gray));
        }
        else {
            Uri localUri = Uri.parse(dataList.get(position));
            Picasso.with(context).load(localUri).into(holder.btnAdd);
        }
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder {
        private ImageButton btnAdd;

        public bidangViewHolder(View itemView) {
            super(itemView);

            btnAdd = (ImageButton) itemView.findViewById(R.id.btn_add_foto);
        }
    }
}