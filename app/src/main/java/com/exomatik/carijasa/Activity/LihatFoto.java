package com.exomatik.carijasa.Activity;

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

public class LihatFoto extends AppCompatActivity {
    private ImageView back;
    private PhotoView imgFoto;
    private TextView textTitle;
    private UserSave userPreference;
    private static final int PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_lihat_foto);

        back = (ImageView) findViewById(R.id.back);
        imgFoto = (PhotoView) findViewById(R.id.img_foto);
        textTitle = (TextView) findViewById(R.id.text_title);

        userPreference = new UserSave(this);

        Uri localUri = Uri.parse(userPreference.getKEY_USER().getFoto());
        Picasso.with(this).load(localUri).into(imgFoto);
        textTitle.setText(userPreference.getKEY_USER().getNamaLengkap());

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
