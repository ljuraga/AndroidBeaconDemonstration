package hr.unipu.app.androidbeacondemonstration;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.ArmaRssiFilter;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.util.Collection;
import java.util.List;

import hr.unipu.app.androidbeacondemonstration.beaconlist.BeaconList;
import hr.unipu.app.androidbeacondemonstration.ui.search.SearchFragment;
import hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData;

import static hr.unipu.app.androidbeacondemonstration.StoreKeys.DEFAULT_ARMA_SPEEDKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.KALMAN_Qkey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.KALMAN_Rkey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.distanceUnitKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.rssiFilterKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.sampleExpirationMillisecondsKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.scanIntervalTimeKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.themeKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.timeBetweenScanIntervalKey;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.arma;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.dark;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.kalman;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.light;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.meter;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.none;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.runningAverage;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.system;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.yards;

/**
 * Ova klasa služi za metode aktivnosti Main.
 *
 * @author Leopold Juraga
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private AppBarConfiguration mAppBarConfiguration;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static BeaconManager mBeaconManager;
    private Region region = new Region("all-beacons-region", null, null, null);
    private Context context;
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private static String updateJson = "https://raw.githubusercontent.com/ljuraga/AndroidBeaconDemonstration/master/update.json";

    /**
     * Metoda koja se izvršava odmah kod pokretanja aplikacije.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());

        BeaconList.clearBeaconArrayList();

        update();

        //ListView test data
//        BeaconList.addToBeaconArrayList("11", "12", "13", -80, -65, 2.59664864);
//        BeaconList.addToBeaconArrayList("21", "22", "23", -90, -65, 4.85468948);
//        BeaconList.addToBeaconArrayList("31", "32", "33", -71, -65, 1.18698788);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_search, R.id.nav_room, R.id.nav_settings, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (StoreData.getPref(themeKey, this) == null) {
                StoreData.putPref(themeKey, system, this);
                switch (currentNightMode) {
                    case Configuration.UI_MODE_NIGHT_NO:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        StoreData.putPref(themeKey, light, this);
                        break;
                    case Configuration.UI_MODE_NIGHT_YES:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        StoreData.putPref(themeKey, dark, this);
                        break;
                }
            }else {
                if (StoreData.getPref(themeKey, this).equals(system)) {
                    switch (currentNightMode) {
                        case Configuration.UI_MODE_NIGHT_NO:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            break;
                        case Configuration.UI_MODE_NIGHT_YES:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            break;
                    }
                }else if (StoreData.getPref(themeKey, this).equals(dark)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else if (StoreData.getPref(themeKey, this).equals(light)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        }else {
            if (StoreData.getPref(themeKey, this) == null) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                StoreData.putPref(themeKey, light, this);
            }else {
                if (StoreData.getPref(themeKey, this).equals(dark)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else if (StoreData.getPref(themeKey, this).equals(light)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        }

        checkPermissionBluetoothLocation();

        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.URI_BEACON_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));//iBeacon layout

        if (StoreData.getPref(rssiFilterKey, this) == null) {
            StoreData.putPref(rssiFilterKey, runningAverage, this);
            mBeaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);
            StoreData.putPref(sampleExpirationMillisecondsKey, String.valueOf(20000), context);
            RunningAverageRssiFilter.setSampleExpirationMilliseconds(20000l);
            StoreData.putPref(DEFAULT_ARMA_SPEEDKey, String.valueOf(0.1), context);
            StoreData.putPref(KALMAN_Rkey, String.valueOf(0.125), context);
            StoreData.putPref(KALMAN_Qkey, String.valueOf(0.5), context);
        }else if (StoreData.getPref(rssiFilterKey, this).equals(none)) {
            mBeaconManager.setRssiFilterImplClass(NoRssiFilter.class);
        }else if (StoreData.getPref(rssiFilterKey, this).equals(runningAverage)) {
            mBeaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);
            RunningAverageRssiFilter.setSampleExpirationMilliseconds(Long.parseLong(StoreData.getPref(sampleExpirationMillisecondsKey, this)));
        }else if (StoreData.getPref(rssiFilterKey, this).equals(arma)) {
            mBeaconManager.setRssiFilterImplClass(ArmaRssiFilter.class);
            ArmaRssiFilter.setDEFAULT_ARMA_SPEED(Double.parseDouble(StoreData.getPref(DEFAULT_ARMA_SPEEDKey, this)));
        }else if (StoreData.getPref(rssiFilterKey, this).equals(kalman)) {
            mBeaconManager.setRssiFilterImplClass(KalmanRssiFilter.class);
            KalmanRssiFilter.setKALMAN_R(Double.parseDouble(StoreData.getPref(KALMAN_Rkey, this)));
            KalmanRssiFilter.setKALMAN_Q(Double.parseDouble(StoreData.getPref(KALMAN_Qkey, this)));
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            mBeaconManager.setForegroundScanPeriod(5001l);
            mBeaconManager.setBackgroundScanPeriod(5001l);
            mBeaconManager.setForegroundBetweenScanPeriod(1100l);
            mBeaconManager.setBackgroundBetweenScanPeriod(1100l);
            mBeaconManager.setAndroidLScanningDisabled(true);
        }else {
            if (StoreData.getPref(scanIntervalTimeKey, this) == null && StoreData.getPref(timeBetweenScanIntervalKey, this) == null) {
                StoreData.putPref(scanIntervalTimeKey, "1", this);
                StoreData.putPref(timeBetweenScanIntervalKey, "1", this);
            }
            mBeaconManager.setForegroundScanPeriod(SettingsData.getInterval(Integer.parseInt(StoreData.getPref(scanIntervalTimeKey, this))));
            mBeaconManager.setBackgroundScanPeriod(SettingsData.getInterval(Integer.parseInt(StoreData.getPref(scanIntervalTimeKey, this))));
            mBeaconManager.setForegroundBetweenScanPeriod(SettingsData.getInterval(Integer.parseInt(StoreData.getPref(timeBetweenScanIntervalKey, this))));
            mBeaconManager.setBackgroundBetweenScanPeriod(SettingsData.getInterval(Integer.parseInt(StoreData.getPref(timeBetweenScanIntervalKey, this))));
            Log.d(TAG, "scanIntervalTime interval value is: " + SettingsData.getInterval(Integer.parseInt(StoreData.getPref(scanIntervalTimeKey, this))));
            Log.d(TAG, "scanIntervalTimeKey value is: " + StoreData.getPref(scanIntervalTimeKey, this));
            Log.d(TAG, "timeBetweenScanInterval interval value is: " + SettingsData.getInterval(Integer.parseInt(StoreData.getPref(timeBetweenScanIntervalKey, this))));
            Log.d(TAG, "timeBetweenScanIntervalKey value is: " + StoreData.getPref(timeBetweenScanIntervalKey, this));
        }

        mBeaconManager.bind(this);

        if (StoreData.getPref(distanceUnitKey, context) == null){
            StoreData.putPref(distanceUnitKey, meter, context);
        }
    }

    /**
     * Metoda koja služi za provijeru ažuriranja aplikacije.
     */
    private void update(){
        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON(updateJson)
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        Log.d(TAG, "Latest Version: " + update.getLatestVersion());
                        Log.d(TAG, "Latest Version Code: " + update.getLatestVersionCode());
                        Log.d(TAG, "Release notes: " + update.getReleaseNotes());
                        Log.d(TAG, "URL: " + update.getUrlToDownload());
                        Log.d(TAG, "Is update available? " + isUpdateAvailable);
                        if (isUpdateAvailable == true){
                            checkForUpdatesActivity(Display.DIALOG);
                        }
                    }
                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Log.d(TAG, "AppUpdater Error: Something went wrong");
                    }
                });
        appUpdaterUtils.start();
    }

    /**
     * Metoda koja služi za ažuriranje aplikacije.
     */
    private static void checkForUpdates(AppUpdater appUpdater, Display display) {
        try {
            appUpdater.setDisplay(display)
                    .setUpdateFrom(UpdateFrom.JSON)
                    .setUpdateJSON(updateJson)
                    .setTitleOnUpdateAvailable(R.string.update_title)
                    .setButtonUpdate(R.string.update_update)
                    .setButtonDismiss(R.string.update_dismiss)
                    .setTitleOnUpdateNotAvailable(R.string.noupdate_title)
                    .setContentOnUpdateNotAvailable(R.string.noupdate_message)
                    .setButtonDoNotShowAgain(null)
                    .showAppUpdated(true)
                    .start();
        }catch (IllegalArgumentException e){

        }
    }

    /**
     * Metoda koja služi za pozivanje metode za ažuriranje aplikacije iz aktivnosti.
     */
    private void checkForUpdatesActivity(Display display) {
        AppUpdater appUpdater = new AppUpdater(this);
        checkForUpdates(appUpdater, display);
    }

    /**
     * Metoda koja služi za pozivanje metode za ažuriranje aplikacije iz fragment-a.
     */
    public static void checkForUpdatesFragment(Context context, Display display) {
        AppUpdater appUpdater = new AppUpdater(context);
        checkForUpdates(appUpdater, display);
    }

    /**
     * Metoda koja provijerava dopuštenja te dali su bluetooth i lokacija uključeni.
     */
    public void checkPermissionBluetoothLocation() {
        requestPermissions();

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocationDialog();
        }

        //Zakomentriati ovu if funkciju kod testiranja u android studio android 5 emulatoru jer emulator nema bluetooth adapter i aplikacija će se rušiti
        if (!adapter.isEnabled()){
            showBluetoothDialog();
        }
    }

    /**
     * Metoda koja služi za zahtijev dopuštenja od korisnika.
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {

                            }

                            if (report.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).
                    withErrorListener(new PermissionRequestErrorListener() {
                        @Override
                        public void onError(DexterError error) {
                            Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .onSameThread()
                    .check();
        }else {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
//                            Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                            }

                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied()) {
                                // show alert dialog navigating to Settings
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).
                    withErrorListener(new PermissionRequestErrorListener() {
                        @Override
                        public void onError(DexterError error) {
                            Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .onSameThread()
                    .check();
        }
    }

    /**
     * Metoda koja prikazuje Dialog koji korisniku daje izbor da aplikaciji da potrebna dopuštenja ili da odustane i izađe iz aplikacije.
     */
    private void showSettingsDialog() {
        AlertDialog.Builder settingsDialog = new AlertDialog.Builder(MainActivity.this);
        settingsDialog.setTitle(R.string.location_permission_title);
        settingsDialog.setMessage(R.string.location_permission_message);
        settingsDialog.setPositiveButton(R.string.goto_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        settingsDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        settingsDialog.show();
    }

    /**
     * Metoda koja otvara postavke aplikacije u sustavu kako bi korisnik mogao dati potrebna dopuštenja.
     */
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /**
     * Metoda koja prikazuje Dialog koji korisniku daje izbor da upali lokkaciju ili da odustane i izađe iz aplikacije.
     */
    private void showLocationDialog() {
        final AlertDialog.Builder locationDialog = new AlertDialog.Builder(this);
        locationDialog.setMessage(R.string.location_enable_message)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = locationDialog.create();
        alert.show();
    }

    /**
     * Metoda koja prikazuje Dialog koji korisniku daje izbor da upali bluetooth ili da odustane i izađe iz aplikacije.
     */
    private void showBluetoothDialog() {
        final AlertDialog.Builder bluetoothDialog = new AlertDialog.Builder(this);
        bluetoothDialog.setMessage(R.string.bluetooth_enable_message)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        adapter.enable();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = bluetoothDialog.create();
        alert.show();
    }

    /**
     * Metoda koja stvara meni.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Metoda za akcije stavki menija.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.exit) {
            exit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Metoda koja služi za pronalaženje Beacon-a i dodavanje pronađenih beacon-a u ArrayList.
     */
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BeaconList.clearBeaconArrayList();
                        for (Beacon b : beacons) {
                            Identifier id3;
                            if (b.getServiceUuid() == 0xFEAA){
                                id3 = Identifier.fromInt(0);
                            }else{
                                id3 = b.getId3();
                            }
                            BeaconList.addToBeaconArrayList(b.getId1().toString(), b.getId2().toString(), id3.toString(), b.getRssi(), b.getTxPower(), b.getDistance());

                            String distanceUnit = null;
                            if (StoreData.getPref(distanceUnitKey, context).equals(meter)){
                                distanceUnit = "m";
                            }else if (StoreData.getPref(distanceUnitKey, context).equals(yards)){
                                distanceUnit = "yd";
                            }

                            if(b.getId1().toString().equals("f7826da6-4fa2-4e98-8024-bc5b71e0893e") && b.getId2().toString().equals("25397") && id3.toString().equals("31770")) {
                                Log.e(TAG, "wth95h:\nRSSI: " + b.getRssi() + " TxPower: " + b.getTxPower() + "\nDistance: " + b.getDistance() + distanceUnit);
                            }else if(b.getId1().toString().equals("f7826da6-4fa2-4e98-8024-bc5b71e0893e") && b.getId2().toString().equals("60334") && id3.toString().equals("13943")) {
                                Log.e(TAG, "wt6Gal:\nRSSI: " + b.getRssi() + " TxPower: " + b.getTxPower() + "\nDistance: " + b.getDistance() + distanceUnit);
                            }else{
                                Log.e(TAG, "UUID: " + b.getId1() + " Major: "+ b.getId2() + " Minor: "+ id3 + " RSSI: " + b.getRssi() + " TxPower: " + b.getTxPower() + " Distance: " + b.getDistance() + distanceUnit);
                            }
                        }

                        SearchFragment.beaconArrayAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda koja prikazuje Dialog koji korisniku daje izbor dali hoče izači iz aplikacije.
     */
    private void exit(){
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.exit_title)
                .setMessage(R.string.exit_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shutDownApp();
                        return;
                    }
                }).setNegativeButton(R.string.no, null).show();
    }

    /**
     * Metoda koja gasi aplikaciju.
     */
    private void shutDownApp(){
        finish();
        System.exit(0);
    }

    /**
     * Metoda koja ponovno pali aplikaciju.
     */
    public void restartApp(){
        startActivityForResult(new Intent(this, MainActivity.class), 0);
        shutDownApp();
    }

    /**
     * Metoda koja se izvodi kad se pritisne tipka nazad.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exit();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    /**
     * Metoda koja se izvodi kod nastavka aplikacije nakon sto je aplikacija radila u pozadini.
     */
    @Override
    public void onResume() {
        super.onResume();
    }
}