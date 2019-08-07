package com.exomatik.carijasa.Pekerja;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.exomatik.carijasa.Featured.UserSave;
import com.exomatik.carijasa.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

public class LihatFotoJasaAct extends AppCompatActivity {
    private ImageView back;
    private PhotoView imgFoto;
    private TextView textTitle;
    public static String url;
    public static String namaJasa;
    private static final int PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_lihat_foto);

        back = (ImageView) findViewById(R.id.back);
        imgFoto = (PhotoView) findViewById(R.id.img_foto);
        textTitle = (TextView) findViewById(R.id.text_title);

        Uri localUri = Uri.parse(url);
        Picasso.with(this).load(localUri).into(imgFoto);
        textTitle.setText(namaJasa);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
