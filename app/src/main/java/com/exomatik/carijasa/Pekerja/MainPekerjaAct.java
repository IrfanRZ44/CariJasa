package com.exomatik.carijasa.Pekerja;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.exomatik.carijasa.Activity.SplashActivity;
import com.exomatik.carijasa.Adapter.ViewPagerAdapter;
import com.exomatik.carijasa.Featured.UserSave;
import com.exomatik.carijasa.Fragment.CariJasaFragment;
import com.exomatik.carijasa.Fragment.KategoriJasaFragment;
import com.exomatik.carijasa.Fragment.SemuaPerusahaanFragment;
import com.exomatik.carijasa.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainPekerjaAct extends AppCompatActivity {
    private TextView textNama, textEmail;
    private UserSave userSave;
    private TabLayout tabMenu;
    private ViewPager viewMenu;
    private SemuaPerusahaanFragment fragKategori1;
    private KategoriJasaFragment fragKategori2;
    private CariJasaFragment fragKategori3;
    private ImageButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main_pekerja);

        init();

        setUpViewPager();
        setData();

        onClick();
    }

    private void onClick() {
        tabMenu.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    tabMenu.getTabAt(0).setIcon(R.drawable.ic_company_blue);
                    tabMenu.getTabAt(1).setIcon(R.drawable.ic_filter_gray);
                    tabMenu.getTabAt(2).setIcon(R.drawable.ic_cari_gray);
                }
                else if (tab.getPosition() == 1){
                    tabMenu.getTabAt(0).setIcon(R.drawable.ic_company_gray);
                    tabMenu.getTabAt(1).setIcon(R.drawable.ic_filter_blue);
                    tabMenu.getTabAt(2).setIcon(R.drawable.ic_cari_gray);
                }
                else if (tab.getPosition() == 2){
                    tabMenu.getTabAt(0).setIcon(R.drawable.ic_company_gray);
                    tabMenu.getTabAt(1).setIcon(R.drawable.ic_filter_gray);
                    tabMenu.getTabAt(2).setIcon(R.drawable.ic_cari_blue);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSave.setKEY_USER(null);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainPekerjaAct.this, SplashActivity.class));
                finish();
            }
        });
    }

    private void setUpViewPager() {
        final ViewPagerAdapter adapterKategori = new ViewPagerAdapter(getSupportFragmentManager());

        fragKategori1 = new SemuaPerusahaanFragment();
        fragKategori2 = new KategoriJasaFragment();
        fragKategori3 = new CariJasaFragment();

        adapterKategori.AddFragment(fragKategori1);
        adapterKategori.AddFragment(fragKategori2);
        adapterKategori.AddFragment(fragKategori3);

        viewMenu.setAdapter(adapterKategori);
        tabMenu.setupWithViewPager(viewMenu);

        tabMenu.getTabAt(0).setText(getResources().getString(R.string.menu_frag1));
        tabMenu.getTabAt(1).setText(getResources().getString(R.string.menu_frag2));
        tabMenu.getTabAt(2).setText(getResources().getString(R.string.menu_frag3));

        tabMenu.getTabAt(0).setIcon(R.drawable.ic_company_blue);
        tabMenu.getTabAt(1).setIcon(R.drawable.ic_filter_gray);
        tabMenu.getTabAt(2).setIcon(R.drawable.ic_cari_gray);
    }

    private void init() {
        textNama = (TextView) findViewById(R.id.text_nama);
        textEmail = (TextView) findViewById(R.id.text_email);
        tabMenu = (TabLayout) findViewById(R.id.tab_menu);
        viewMenu = (ViewPager) findViewById(R.id.view_menu);
        btnLogout = (ImageButton) findViewById(R.id.btn_logout);

        userSave = new UserSave(this);
    }

    private void setData() {
        textNama.setText(userSave.getKEY_USER().getNamaLengkap());
        textEmail.setText(userSave.getKEY_USER().getEmail());
    }
}
