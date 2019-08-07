package com.exomatik.carijasa.Featured;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.exomatik.carijasa.Model.ModelUser;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by IrfanRZ on 02/08/2019.
 */

public class UserSave {
    private SharedPreferences preferences;
    private String KEY_USER = "user";

    public UserSave(Context paramContext) {
//        this.preferences = paramContext.getSharedPreferences("UserSave", MODE_PRIVATE);
        this.preferences = paramContext.getSharedPreferences("UserPref", 0);
    }

    public void setKEY_USER(ModelUser data) {
        ModelUser myObject = data;
        Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myObject);
        prefsEditor.putString(this.KEY_USER, json);
//        prefsEditor.commit();
        prefsEditor.apply();
    }

    public ModelUser getKEY_USER() {
        Gson gson = new Gson();
        String json = preferences.getString(this.KEY_USER, "");
        ModelUser obj = gson.fromJson(json, ModelUser.class);
        return obj;
    }
}
