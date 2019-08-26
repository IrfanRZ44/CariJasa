package com.exomatik.carijasa.Perusahaan;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.exomatik.carijasa.Adapter.RecyclerFotoJasa;
import com.exomatik.carijasa.Adapter.RecyclerJasaPerusahaan;
import com.exomatik.carijasa.Featured.DataKategori;
import com.exomatik.carijasa.Featured.FileUtil;
import com.exomatik.carijasa.Featured.ItemClickSupport;
import com.exomatik.carijasa.Featured.UserSave;
import com.exomatik.carijasa.Model.ModelJasa;
import com.exomatik.carijasa.Model.ModelUser;
import com.exomatik.carijasa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import id.zelory.compressor.Compressor;

public class FormJasa extends AppCompatActivity {
    public static ModelJasa detailJasa;
    private ImageButton btnBack;
    private ProgressDialog progressDialog;
    private EditText etGaji;
    private Spinner spinKategori;
    private RelativeLayout btnSimpan, btnHapus;
    private UserSave userSave;
    private View v;
    private ArrayList<String> listKategori = new ArrayList<String>();
    private DataKategori dataKategori;
    private RecyclerView rcFoto;
    private ArrayList<String> listFotoJasa = new ArrayList<String>();
    private ArrayList<String> listTempFotoJasa = new ArrayList<String>();
    private RecyclerFotoJasa adapterFotoJasa;
    private int PICK_IMAGE = 100;
    private int positionFoto = 0;
    private File compressedImage, actualImage;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_form_jasa);

        init();

        setListKategori(null);
        setText();
        setSpinner();

        onClick();
    }

    private void onClick() {
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekEditText();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailJasa = null;
                startActivity(new Intent(FormJasa.this, ProfilePerusahaanAct.class));
                finish();
            }
        });

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapusData();
            }
        });

        ItemClickSupport.addTo(rcFoto).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (listFotoJasa.get(position) == null) {
                    positionFoto = position;
                    getFoto();
                }
            }
        });

        ItemClickSupport.addTo(rcFoto).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                if (listFotoJasa.get(position) != null) {
                    progressDialog = new ProgressDialog(FormJasa.this);
                    progressDialog.setMessage(getResources().getString(R.string.progress_title1));
                    progressDialog.setTitle(getResources().getString(R.string.progress_text1));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    hapusFoto(position);
                }
                return false;
            }
        });
    }

    private void setSpinner() {
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(FormJasa.this,
                R.layout.spinner_text_hitam, listKategori);
        spinKategori.setAdapter(adapterSpinner);
    }

    private void setListKategori(String kategoriSet) {
        if (kategoriSet == null) {
            listKategori = dataKategori.DataKategori("-");
        } else {
            listKategori = dataKategori.DataKategori(kategoriSet);
        }
    }

    private void init() {
        rcFoto = (RecyclerView) findViewById(R.id.rc_foto);
        btnBack = (ImageButton) findViewById(R.id.back);
        etGaji = (EditText) findViewById(R.id.et_gaji);
        spinKategori = (Spinner) findViewById(R.id.spinner_kategori);
        btnSimpan = (RelativeLayout) findViewById(R.id.rl_update);
        btnHapus = (RelativeLayout) findViewById(R.id.rl_hapus);

        v = (View) findViewById(android.R.id.content);

        userSave = new UserSave(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        dataKategori = new DataKategori();

        if (detailJasa == null) {
            btnHapus.setVisibility(View.GONE);
        } else {
            btnHapus.setVisibility(View.VISIBLE);
        }
    }

    private void getFoto() {
        progressDialog = new ProgressDialog(FormJasa.this);
        progressDialog.setMessage(getResources().getString(R.string.progress_title1));
        progressDialog.setTitle(getResources().getString(R.string.progress_text1));
        progressDialog.setCancelable(false);
        progressDialog.show();
        startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.INTERNAL_CONTENT_URI), PICK_IMAGE);
        progressDialog.dismiss();
    }

    private void setText() {
        if (detailJasa != null) {
            etGaji.setText(detailJasa.getGaji());
            listKategori.removeAll(listKategori);

            setListKategori(detailJasa.getKategori());
            listFotoJasa = detailJasa.getFoto();
            listTempFotoJasa = detailJasa.getFoto();

            listFotoJasa.add(null);
            setAdapter();
        } else {
            listFotoJasa.add(null);
            setAdapter();
        }
    }

    @Override
    public void onBackPressed() {
        detailJasa = null;
        startActivity(new Intent(FormJasa.this, ProfilePerusahaanAct.class));
        finish();
    }

    private void cekEditText() {
        String gaji = etGaji.getText().toString();

        if (gaji.isEmpty() || listKategori.get(spinKategori.getSelectedItemPosition()).equals("-") || listFotoJasa.size() == 1) {
            if (gaji.isEmpty()) {
                etGaji.setError(getResources().getString(R.string.error_data_kosong));
            }
            if (listKategori.get(spinKategori.getSelectedItemPosition()).equals("-")) {
                Snackbar.make(v, "Anda harus memilih salah satu kategori", Snackbar.LENGTH_LONG).show();
            }
            if (listFotoJasa.size() == 1) {
                Snackbar.make(v, "Anda harus mengupload minimal 1 foto", Snackbar.LENGTH_LONG).show();
            }
        } else {
            progressDialog = new ProgressDialog(FormJasa.this);
            progressDialog.setMessage(getResources().getString(R.string.progress_title1));
            progressDialog.setTitle(getResources().getString(R.string.progress_text1));
            progressDialog.setCancelable(false);
            progressDialog.show();

            String id = null;

            if (detailJasa != null) {
                id = detailJasa.getId();
            } else {
                id = System.currentTimeMillis() + "_" + gaji + "_" + listKategori.get(spinKategori.getSelectedItemPosition());
            }

            ModelJasa dataJasa = new ModelJasa(id, gaji, listKategori.get(spinKategori.getSelectedItemPosition())
                    , userSave.getKEY_USER().getUid(), listFotoJasa);

            for (int a = 0; a < listFotoJasa.size() - 1; a++) {
                if (listTempFotoJasa.size() != 0) {
                    if (listFotoJasa.get(a).equals(listTempFotoJasa.get(a))) {
                        simpanData(dataJasa);
                    } else {
                        replaceFotoJasa(dataJasa, a);
                    }
                } else {
                    simpanFoto(dataJasa, a);
                }
            }
        }
    }

    private void hapusFoto(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(FormJasa.this);

        alert.setTitle("Hapus");
        alert.setMessage("Apakah anda yakin ingin menghapus foto jasa ini?");
        alert.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listTempFotoJasa.get(position) == null) {
                    listFotoJasa.remove(position);
                    adapterFotoJasa.notifyItemChanged(position);
                    progressDialog.dismiss();
                } else {
                    if (listFotoJasa.get(position).equals(listTempFotoJasa.get(position))) {
                        StorageReference fotoDelete = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(listFotoJasa.get(position));
                        fotoDelete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                hapusDataFoto(position);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(v, "Error " + e.getMessage().toString(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        });
                    }
                }
            }
        });
        alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void hapusDataFoto(final int position) {
        DatabaseReference db_remove_kelas = FirebaseDatabase.getInstance().getReference().child("jasa")
                .child(detailJasa.getUid())
                .child(detailJasa.getId())
                .child("foto")
                .child(Integer.toString(position));
        db_remove_kelas.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listFotoJasa.remove(position);
                adapterFotoJasa.notifyDataSetChanged();
                progressDialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make(v, "Berhasil hapus foto", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make(v, "Error " + e.getMessage().toString(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private void replaceFotoJasa(final ModelJasa dataUploaded, final int position) {
        StorageReference fotoDelete = FirebaseStorage.getInstance()
                .getReferenceFromUrl(listFotoJasa.get(position));
        fotoDelete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                simpanFoto(dataUploaded, position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make(v, "Error " + e.getMessage().toString(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private void simpanFoto(final ModelJasa dataUploaded, final int posisi) {
        final String file = System.currentTimeMillis() + "_" + Uri.parse(listFotoJasa.get(posisi)).getLastPathSegment();
        mStorageRef.child("fotoJasa/" + file).putFile(Uri.parse(listFotoJasa.get(posisi))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            public void onSuccess(UploadTask.TaskSnapshot paramAnonymousTaskSnapshot) {
                int position = posisi;
                position = position + 2;
                listFotoJasa.set(posisi, paramAnonymousTaskSnapshot.getDownloadUrl().toString());
                adapterFotoJasa.notifyItemChanged(posisi);

                if (position == listFotoJasa.size()) {
                    simpanData(dataUploaded);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception paramAnonymousException) {
                progressDialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make(v, "Error, " + paramAnonymousException.getMessage().toString(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage(Integer.toString((int) progress) + " %");
                progressDialog.setProgress((int) progress);
                String progressText = taskSnapshot.getBytesTransferred() / 1024 + "KB/" + taskSnapshot.getTotalByteCount() / 1024 + "KB";
                progressDialog.setTitle(progressText);
            }
        });
    }

    private void simpanData(ModelJasa data) {
        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();

        localDatabaseReference
                .child("jasa")
                .child(userSave.getKEY_USER().getUid())
                .child(data.getId())
                .setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> paramAnonymous2Task) {
                        if (paramAnonymous2Task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(FormJasa.this, "Berhasil Upload Data", Toast.LENGTH_LONG).show();
                            detailJasa = null;
                            startActivity(new Intent(FormJasa.this, ProfilePerusahaanAct.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Snackbar.make(v, "Gagal Upload Data, error tidak diketahui", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Snackbar.make(v, "Error " + e.getMessage().toString(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void hapusData() {
        AlertDialog.Builder alert = new AlertDialog.Builder(FormJasa.this);

        alert.setTitle("Hapus");
        alert.setMessage("Apakah anda yakin ingin menghapus Jasa ini?");
        alert.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                DatabaseReference db_remove_kelas = FirebaseDatabase.getInstance()
                        .getReference("jasa")
                        .child(userSave.getKEY_USER().getUid())
                        .child(detailJasa.getId());
                db_remove_kelas.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                detailJasa = null;
                                startActivity(new Intent(FormJasa.this, ProfilePerusahaanAct.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                dialog.dismiss();
                                Toast.makeText(FormJasa.this, "Error " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void setAdapter() {
        adapterFotoJasa = new RecyclerFotoJasa(listFotoJasa, FormJasa.this);
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(FormJasa.this, LinearLayoutManager.HORIZONTAL, false);
        rcFoto.setLayoutManager(localLinearLayoutManager);
        rcFoto.setNestedScrollingEnabled(false);
        rcFoto.setAdapter(adapterFotoJasa);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((resultCode == -1) && (requestCode == PICK_IMAGE)) {
            try {
                actualImage = FileUtil.from(FormJasa.this, data.getData());
                compressedImage = new Compressor(FormJasa.this).compressToFile(actualImage);
                listFotoJasa.set(positionFoto, Uri.fromFile(compressedImage).toString());
                adapterFotoJasa.notifyItemChanged(positionFoto);
                listFotoJasa.add(null);
                adapterFotoJasa.notifyDataSetChanged();
            } catch (IOException e) {
                Toast.makeText(FormJasa.this, "Error " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
