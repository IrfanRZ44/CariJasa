package com.exomatik.carijasa.Perusahaan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.exomatik.carijasa.Featured.FileUtil;
import com.exomatik.carijasa.Featured.UserSave;
import com.exomatik.carijasa.Model.ModelUser;
import com.exomatik.carijasa.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class EditProfilAct extends AppCompatActivity {
    private ImageButton btnBack, btnAlamat;
    private CircleImageView imgUser;
    private int PICK_IMAGE = 100;
    private int PLACE_PICKER_REQUEST = 1;
    private File compressedImage, actualImage;
    private ProgressDialog progressDialog;
    private Uri imageUri = null;
    private String locationPoint = null;
    private EditText etNama, etPhone, etAlamat;
    private RelativeLayout btnSimpan;
    private UserSave userSave;
    private View v;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit_profil);

        init();

        setText();

        onClick();
    }

    private void onClick() {
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFoto();
            }
        });

        btnAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAlamat.setError(null);
                getLocation();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekEditText();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfilAct.this, ProfilePerusahaanAct.class));
                finish();
            }
        });
    }

    private void init() {
        btnBack = (ImageButton) findViewById(R.id.back);
        imgUser = (CircleImageView) findViewById(R.id.img_user);
        btnAlamat = (ImageButton) findViewById(R.id.img_pick_alamat);
        etNama = (EditText) findViewById(R.id.et_nama);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etAlamat = (EditText) findViewById(R.id.et_alamat);
        btnSimpan = (RelativeLayout) findViewById(R.id.rl_update);

        v = (View) findViewById(android.R.id.content);

        userSave = new UserSave(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void setText(){
        if (userSave.getKEY_USER().getFoto() == null){
            imgUser.setImageResource(R.drawable.logo2);
        }
        else {
            Uri localUri = Uri.parse(userSave.getKEY_USER().getFoto());
            Picasso.with(this).load(localUri).into(imgUser);
            etAlamat.setText(userSave.getKEY_USER().getAlamat());
            etPhone.setText(userSave.getKEY_USER().getNoHp());
            locationPoint = userSave.getKEY_USER().getLocationPoint();
        }

        etNama.setText(userSave.getKEY_USER().getNamaLengkap());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditProfilAct.this, ProfilePerusahaanAct.class));
        finish();
    }

    private void getLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(EditProfilAct.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Toast.makeText(EditProfilAct.this, "Error " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(EditProfilAct.this, "Error " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getFoto() {
        progressDialog = new ProgressDialog(EditProfilAct.this);
        progressDialog.setMessage(getResources().getString(R.string.progress_title1));
        progressDialog.setTitle(getResources().getString(R.string.progress_text1));
        progressDialog.setCancelable(false);
        progressDialog.show();
        startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.INTERNAL_CONTENT_URI), PICK_IMAGE);
        progressDialog.dismiss();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, EditProfilAct.this);
                etAlamat.setText(place.getName());

                locationPoint = place.getLatLng().toString();
            }
        }

        if ((resultCode == -1) && (requestCode == PICK_IMAGE)) {
            try {
                actualImage = FileUtil.from(EditProfilAct.this, data.getData());
                compressedImage = new Compressor(EditProfilAct.this).compressToFile(actualImage);
                imageUri = Uri.fromFile(compressedImage);
                Picasso.with(EditProfilAct.this).load(imageUri).into(imgUser);
            } catch (IOException e) {
                Toast.makeText(EditProfilAct.this, "Error " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cekEditText() {
        String nama = etNama.getText().toString();
        String phone = etPhone.getText().toString();
        String alamat = etAlamat.getText().toString();

        if (nama.isEmpty() || phone.isEmpty() || phone.length() < 6 || alamat.isEmpty() || locationPoint == null
                || (userSave.getKEY_USER().getFoto() == null && imageUri == null)) {
            if (nama.isEmpty()) {
                etNama.setError(getResources().getString(R.string.error_data_kosong));
            }
            if (phone.isEmpty()) {
                etPhone.setError(getResources().getString(R.string.error_data_kosong));
            }
            if (phone.length() < 6) {
                etPhone.setError(getResources().getString(R.string.error_telepon_tidak_cukup));
            }
            if (alamat.isEmpty()) {
                etAlamat.setError(getResources().getString(R.string.error_data_kosong));
            }
            if (locationPoint == null){
                etAlamat.setError(getResources().getString(R.string.error_location_kosong));
            }
            if (userSave.getKEY_USER().getFoto() == null && imageUri == null) {
                Log.e("Masuk", "foto");
                Snackbar snackbar = Snackbar
                        .make(v, getResources().getString(R.string.error_foto_kosong), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        } else {
            Log.e("Masuk", "nda");
            progressDialog = new ProgressDialog(EditProfilAct.this);
            progressDialog.setMessage(getResources().getString(R.string.progress_title1));
            progressDialog.setTitle(getResources().getString(R.string.progress_text1));
            progressDialog.setCancelable(false);
            progressDialog.show();

            ModelUser dataUser = new ModelUser(nama, userSave.getKEY_USER().getEmail(), userSave.getKEY_USER().getUid(),
                    userSave.getKEY_USER().getJenisAkun(), null, phone, alamat, locationPoint);
            uploadMethod(dataUser);
        }
    }

    private void uploadMethod(ModelUser dataUser) {
        if (userSave.getKEY_USER().getFoto() == null) {
            simpanFoto(dataUser, imageUri);
        } else {
            if (imageUri == null) {
                simpanData(dataUser, null);
            } else {
                hapusFoto(dataUser, imageUri);
            }
        }
    }

    private void hapusFoto(final ModelUser dataUploaded, final Uri image) {
        StorageReference fotoDelete = FirebaseStorage.getInstance().getReferenceFromUrl(userSave.getKEY_USER().getFoto());
        fotoDelete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                simpanFoto(dataUploaded, image);
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

    private void simpanFoto(final ModelUser dataUploaded, Uri image) {
        final String file = System.currentTimeMillis() + "_" + image.getLastPathSegment();
        mStorageRef.child("fotoKuliner/" + file).putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            public void onSuccess(UploadTask.TaskSnapshot paramAnonymousTaskSnapshot) {
                simpanData(dataUploaded, paramAnonymousTaskSnapshot);
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

    private void simpanData(ModelUser dataUploaded, UploadTask.TaskSnapshot snapshot) {
        String foto = null;
        if (snapshot == null) {
            foto = userSave.getKEY_USER().getFoto();
        } else {
            foto = snapshot.getDownloadUrl().toString();
        }
        final ModelUser dataUser = new ModelUser(dataUploaded.getNamaLengkap(), userSave.getKEY_USER().getEmail(), userSave.getKEY_USER().getUid(),
                userSave.getKEY_USER().getJenisAkun(), foto, dataUploaded.getNoHp(), dataUploaded.getAlamat(), dataUploaded.getLocationPoint());

        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();

        localDatabaseReference
                .child("users")
                .child(dataUploaded.getUid())
                .setValue(dataUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> paramAnonymous2Task) {
                        if (paramAnonymous2Task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfilAct.this, "Berhasil Upload Data", Toast.LENGTH_LONG).show();
                            userSave.setKEY_USER(dataUser);
                            startActivity(new Intent(EditProfilAct.this, ProfilePerusahaanAct.class));
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
}
