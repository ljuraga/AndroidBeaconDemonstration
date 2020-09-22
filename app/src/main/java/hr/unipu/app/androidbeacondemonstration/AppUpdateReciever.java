package hr.unipu.app.androidbeacondemonstration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Ova klasa služi za izvršavanje naredbi kod preinstalacije ili ažuriranjja aplikacije.
 *
 * @author Leopold Juraga
 * @version 1.0
 */
public class AppUpdateReciever extends BroadcastReceiver {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
//        clearAppData(context);
        Log.d(TAG, "Updated!");
//        Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
    }

    private void clearAppData(Context context) {
        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear " + context.getPackageName() + " HERE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
