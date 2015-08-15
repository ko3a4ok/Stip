package io.ololo.stip;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ko3a4ok on 8/15/15.
 */
public class StipApplication extends Application {
    SharedPreferences preferences;
    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences("stip", Context.MODE_PRIVATE);
    }

    public String getToken() {
        return preferences.getString("token", null);
    }

    public void storeToken(String token) {
        preferences.edit().putString("token", token).commit();
    }
}
