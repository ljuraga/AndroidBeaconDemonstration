package hr.unipu.app.androidbeacondemonstration;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Ova klasa služi za pohranjivanje podataka u memoriju uređaja koristeči SharedPreferences.
 *
 * @author Leopold Juraga
 * @version 1.0
 */
public class StoreData {
    public static void putPref(String key, String value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPref(String key, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, null);
    }
}
