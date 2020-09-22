package hr.unipu.app.androidbeacondemonstration.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.service.ArmaRssiFilter;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.text.DecimalFormat;

import hr.unipu.app.androidbeacondemonstration.KalmanRssiFilter;
import hr.unipu.app.androidbeacondemonstration.MainActivity;
import hr.unipu.app.androidbeacondemonstration.NoRssiFilter;
import hr.unipu.app.androidbeacondemonstration.R;
import hr.unipu.app.androidbeacondemonstration.StoreData;
import hr.unipu.app.androidbeacondemonstration.ui.room.RoomData;

import static hr.unipu.app.androidbeacondemonstration.MainActivity.mBeaconManager;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.DEFAULT_ARMA_SPEEDKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.KALMAN_Qkey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.KALMAN_Rkey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.android5and6warningStateKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.distanceUnitKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.rssiFilterKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.sampleExpirationMillisecondsKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.scanIntervalTimeKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.themeKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.timeBetweenScanIntervalKey;
import static hr.unipu.app.androidbeacondemonstration.ui.room.RoomMapFragment.stopPinMover;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.arma;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.dark;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.kalman;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.light;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.meter;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.none;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.progressBetweenScan;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.progressScan;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.runningAverage;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.scanIntervalTime;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.system;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.timeBetweenScanInterval;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.yards;

/**
 * Ova klasa služi za metode fragment-a Settings.
 *
 * @author Leopold Juraga
 * @version 1.0
 */
public class SettingsFragment extends Fragment implements BeaconConsumer {

    private static final String TAG = SettingsFragment.class.getSimpleName();
    private IndicatorSeekBar scanIntervalTimeSeekBar;
    private IndicatorSeekBar timeBetweenScanIntervalsSeekBar;
    private Context context;
    public static boolean rssiFilterChanged = false;
    private LinearLayout layoutSampleExpiration;
    private LinearLayout layoutDEFAULT_ARMA_SPEED;
    private LinearLayout layoutKalmanRssiFilter;

    /**
     * Metoda koja se izvršava kod pokretanja fragment-a.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        stopPinMover = true;

        context = getActivity().getApplicationContext();

        RadioGroup radioGroupThemes = (RadioGroup) root.findViewById(R.id.radioGroupThemes);
        RadioButton radioButtonSystem = (RadioButton) root.findViewById(R.id.radioButtonSystem);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            radioButtonSystem.setVisibility(View.VISIBLE);
        }else {
            radioButtonSystem.setVisibility(View.GONE);
        }

        if (StoreData.getPref(themeKey, context).equals(system)){
            Log.d(TAG, "radioButtonSystem");
            radioGroupThemes.check(R.id.radioButtonSystem);
        }else if (StoreData.getPref(themeKey, context).equals(dark)){
            Log.d(TAG, "radioButtonDark");
            radioGroupThemes.check(R.id.radioButtonDark);
        }else if (StoreData.getPref(themeKey, context).equals(light)){
            Log.d(TAG, "radioButtonLight");
            radioGroupThemes.check(R.id.radioButtonLight);
        }

        radioGroupThemes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.radioButtonSystem:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        getActivity().recreate();
                        StoreData.putPref(themeKey, system, context);
                        break;
                    case R.id.radioButtonDark:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        getActivity().recreate();
                        StoreData.putPref(themeKey, dark, context);
                        break;
                    case R.id.radioButtonLight:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        getActivity().recreate();
                        StoreData.putPref(themeKey, light, context);
                        break;
                }
                ((MainActivity) getActivity()).recreate();
            }
        });

        Log.d(TAG, "StoreData distanceUnit: " + StoreData.getPref(distanceUnitKey, context));

        final RadioGroup radioGroupDistanceUnits = (RadioGroup) root.findViewById(R.id.radioGroupDistanceUnits);

        if (StoreData.getPref(distanceUnitKey, context).equals(meter)){
            Log.d(TAG, "radioButtonMeters");
            radioGroupDistanceUnits.check(R.id.radioButtonMeters);
        }else if (StoreData.getPref(distanceUnitKey, context).equals(yards)){
            Log.d(TAG, "radioButtonYards");
            radioGroupDistanceUnits.check(R.id.radioButtonYards);
        }

        radioGroupDistanceUnits.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.radioButtonMeters:
                        StoreData.putPref(distanceUnitKey, meter, context);
                        break;
                    case R.id.radioButtonYards:
                        StoreData.putPref(distanceUnitKey, yards, context);
                        break;
                }
            }
        });

        scanIntervalTimeSeekBar = root.findViewById(R.id.scanIntervalTimeSeekBar);
        scanIntervalTimeSeekBar.setIndicatorTextFormat("${PROGRESS} s");
        timeBetweenScanIntervalsSeekBar = root.findViewById(R.id.timeBetweenScanIntervalsSeekBar);
        timeBetweenScanIntervalsSeekBar.setIndicatorTextFormat("${PROGRESS} s");

        setProgress();

        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            scanIntervalTimeSeekBar.setProgress(5);
            scanIntervalTimeSeekBar.setEnabled(false);
            timeBetweenScanIntervalsSeekBar.setProgress(1);
            timeBetweenScanIntervalsSeekBar.setEnabled(false);
            showAndroid5and6warningDialog();
        }

        scanIntervalTimeSeekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            int progressChangedValue = 0;
            @Override
            public void onSeeking(SeekParams seekParams) {
                progressChangedValue = seekParams.progress;
                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    scanIntervalTimeSeekBar.setProgress(5);
                }else {
                    changeProgressBarValue(progressChangedValue, scanIntervalTime, scanIntervalTimeKey);
                }
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    showAndroid5and6warningDialog();
                }
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                String scanIntervalTimeToast = getResources().getString(R.string.settings_scanIntervalTimeToast);
                showSeekBarSnackBar(scanIntervalTimeToast, progressScan);
            }
        });

        timeBetweenScanIntervalsSeekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            int progressChangedValue = 0;
            @Override
            public void onSeeking(SeekParams seekParams) {
                progressChangedValue = seekParams.progress;
                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    timeBetweenScanIntervalsSeekBar.setProgress(1);
                }else {
                    changeProgressBarValue(progressChangedValue, timeBetweenScanInterval, timeBetweenScanIntervalKey);
                }
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    showAndroid5and6warningDialog();
                }
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                String timeBetweenScanIntervalsToast = getResources().getString(R.string.settings_timeBetweenScanIntervalsToast);
                showSeekBarSnackBar(timeBetweenScanIntervalsToast, progressBetweenScan);
            }
        });

        final RadioGroup radioGroupRssiFilters = (RadioGroup) root.findViewById(R.id.radioGroupRssiFilters);
        final EditText enterSampleExpirationMilliseconds = (EditText) root.findViewById(R.id.enterSampleExpirationMilliseconds);
        final EditText enterDEFAULT_ARMA_SPEED = (EditText) root.findViewById(R.id.enterDEFAULT_ARMA_SPEED);
        layoutSampleExpiration = (LinearLayout) root.findViewById(R.id.layoutSampleExpiration);
        layoutDEFAULT_ARMA_SPEED = (LinearLayout) root.findViewById(R.id.layoutDEFAULT_ARMA_SPEED);
        layoutKalmanRssiFilter = (LinearLayout) root.findViewById(R.id.layoutKalmanRssiFilter);
        EditText enterKALMAN_R = (EditText) root.findViewById(R.id.enterKALMAN_R);
        EditText enterKALMAN_Q = (EditText) root.findViewById(R.id.enterKALMAN_Q);

        if (StoreData.getPref(rssiFilterKey, context).equals(none)){
            radioGroupRssiFilters.check(R.id.radioButtonNoRssiFilter);
            setLayoutVisibility(none);
        }else if (StoreData.getPref(rssiFilterKey, context).equals(runningAverage)){
            radioGroupRssiFilters.check(R.id.radioButtonRunningAverageRssiFilter);
            setLayoutVisibility(runningAverage);
        }else if (StoreData.getPref(rssiFilterKey, context).equals(arma)){
            radioGroupRssiFilters.check(R.id.radioButtonArmaRssiFilter);
            setLayoutVisibility(arma);
        }else if (StoreData.getPref(rssiFilterKey, context).equals(kalman)){
            radioGroupRssiFilters.check(R.id.radioButtonKalmanRssiFilter);
            setLayoutVisibility(kalman);
        }

        radioGroupRssiFilters.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.radioButtonNoRssiFilter:
                        if (!StoreData.getPref(rssiFilterKey, context).equals(none)){
                            rssiFilterChanged = true;
                        }
                        StoreData.putPref(rssiFilterKey, none, context);
                        mBeaconManager.setRssiFilterImplClass(NoRssiFilter.class);
                        setLayoutVisibility(none);
                        showRSSIwariningDialog();
                        break;
                    case R.id.radioButtonRunningAverageRssiFilter:
                        if (!StoreData.getPref(rssiFilterKey, context).equals(runningAverage)){
                            rssiFilterChanged = true;
                        }
                        StoreData.putPref(rssiFilterKey, runningAverage, context);
                        mBeaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);
                        setLayoutVisibility(runningAverage);
                        showRSSIwariningDialog();
                        break;
                    case R.id.radioButtonArmaRssiFilter:
                        if (!StoreData.getPref(rssiFilterKey, context).equals(arma)){
                            rssiFilterChanged = true;
                        }
                        StoreData.putPref(rssiFilterKey, arma, context);
                        mBeaconManager.setRssiFilterImplClass(ArmaRssiFilter.class);
                        setLayoutVisibility(arma);
                        showRSSIwariningDialog();
                        break;
                    case R.id.radioButtonKalmanRssiFilter:
                        if (!StoreData.getPref(rssiFilterKey, context).equals(arma)){
                            rssiFilterChanged = true;
                        }
                        StoreData.putPref(rssiFilterKey, kalman, context);
                        mBeaconManager.setRssiFilterImplClass(KalmanRssiFilter.class);
                        setLayoutVisibility(kalman);
                        showRSSIwariningDialog();
                        break;
                }
            }
        });

        checkForStoredValues(enterSampleExpirationMilliseconds, sampleExpirationMillisecondsKey);
        checkForStoredValues(enterDEFAULT_ARMA_SPEED, DEFAULT_ARMA_SPEEDKey);
        checkForStoredValues(enterKALMAN_R, KALMAN_Rkey);
        checkForStoredValues(enterKALMAN_Q, KALMAN_Qkey);

        addTextChangedListener(enterSampleExpirationMilliseconds, sampleExpirationMillisecondsKey);
        addTextChangedListener(enterDEFAULT_ARMA_SPEED, DEFAULT_ARMA_SPEEDKey);
        addTextChangedListener(enterKALMAN_R, KALMAN_Rkey);
        addTextChangedListener(enterKALMAN_Q, KALMAN_Qkey);

        return root;
    }

    /**
     * Metoda koja mjenja interval ProgressBar-a.
     */
    public void changeProgressBarValue(int progressChangedValue, String scanType, String key){
        setScanPeriods(SettingsData.getInterval(progressChangedValue), scanType);
        Log.d(TAG, "getInterval is: " + SettingsData.getInterval(progressChangedValue));
        SettingsData.setProgress(progressChangedValue, scanType);
        StoreData.putPref(key, String.valueOf(progressChangedValue), context);
        Log.d(TAG, "progressChangedValue is: " + progressChangedValue);
    }

    /**
     * Metoda koja postavlja vrijeme skeniranja i vrijeme između skeniranja.
     */
    public void setScanPeriods(long period, String type){
        if (type.equals(scanIntervalTime)){
            mBeaconManager.setForegroundScanPeriod(period);
            mBeaconManager.setBackgroundScanPeriod(period);
        }else if (type.equals(timeBetweenScanInterval)){
            mBeaconManager.setForegroundBetweenScanPeriod(period);
            mBeaconManager.setBackgroundBetweenScanPeriod(period);
        }
        mBeaconManager.bind(this);
        if (mBeaconManager.isBound(this)) {
            try {
                mBeaconManager.updateScanPeriods();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda u kojoj je implementiran prikaz Snackbar-a.
     */
    public void showSeekBarSnackBar(String message, int progressChangedValueToast){
        String toastIntervalSecond = getResources().getString(R.string.settings_toastIntervalSecond);
        String toastIntervalSeconds = getResources().getString(R.string.settings_toastIntervalSeconds);
        if (progressChangedValueToast == 1){
            Snackbar.make(getView(), message + progressChangedValueToast + toastIntervalSecond, Snackbar.LENGTH_LONG).show();
        }else {
            Snackbar.make(getView(), message + progressChangedValueToast + toastIntervalSeconds, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Metoda koja pokazuje upozorenje korisnicima Androida 5 i 6 da ne mogu podešavati vrijeme skeniranja i vrijeme između skeniranja.
     */
    public void showAndroid5and6warningDialog(){
        if (StoreData.getPref(android5and6warningStateKey, context) == null){
            StoreData.putPref(android5and6warningStateKey, "0", context);
        }
        if (StoreData.getPref(android5and6warningStateKey, context).equals("0")){
            new AlertDialog.Builder((MainActivity) getActivity())
                    .setTitle(R.string.settings_android5and6warningDialog_title)
                    .setMessage(R.string.settings_android5and6warningDialog_message)
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            StoreData.putPref(android5and6warningStateKey, "1", context);
        }
    }

    /**
     * Metoda koja poziva metodu setProgressData za vrijeme skeniranja i vrijeme između skeniranja.
     */
    public void setProgress(){
        setProgressData(scanIntervalTime, scanIntervalTimeKey);
        setProgressData(timeBetweenScanInterval, timeBetweenScanIntervalKey);
    }

    /**
     * Metoda koja pohranjuje vrijeme skeniranja i vrijeme između skeniranja u klasu SettingsData.
     */
    public void setProgressData(String scanType, String key){
        Log.d(TAG, "changeProgressBarValue is: " + SettingsData.getProgress(scanType));
        if (StoreData.getPref(key, context) != null){
            SettingsData.setProgress(Integer.parseInt(StoreData.getPref(key, context)), scanType);
            if (scanType.equals(scanIntervalTime)){
                scanIntervalTimeSeekBar.setProgress(SettingsData.getProgress(scanType));
            }else if (scanType.equals(timeBetweenScanInterval)){
                timeBetweenScanIntervalsSeekBar.setProgress(SettingsData.getProgress(scanType));
            }
        }
    }

    /**
     * Metoda koja služi za postavljanje prikaza layout-a fragment-a ovisno o odabranom filter-u.
     */
    private void setLayoutVisibility(String layout){
        if (layout.equals(none)){
            layoutSampleExpiration.setVisibility(View.GONE);
            layoutDEFAULT_ARMA_SPEED.setVisibility(View.GONE);
            layoutKalmanRssiFilter.setVisibility(View.GONE);
        }else if (layout.equals(runningAverage)){
            layoutSampleExpiration.setVisibility(View.VISIBLE);
            layoutDEFAULT_ARMA_SPEED.setVisibility(View.GONE);
            layoutKalmanRssiFilter.setVisibility(View.GONE);
        }else if (layout.equals(arma)){
            layoutSampleExpiration.setVisibility(View.GONE);
            layoutDEFAULT_ARMA_SPEED.setVisibility(View.VISIBLE);
            layoutKalmanRssiFilter.setVisibility(View.GONE);
        }else if (layout.equals(kalman)){
            layoutSampleExpiration.setVisibility(View.GONE);
            layoutDEFAULT_ARMA_SPEED.setVisibility(View.GONE);
            layoutKalmanRssiFilter.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Metoda koja služi za pohranu upisanih vrijednosti nakon pisanja u EditText.
     */
    private void addTextChangedListener(final TextView textView, final String storageKey){
        textView.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    try {
                        if (TextUtils.isEmpty(textView.getText().toString()) != true){
                            storeInSettingsData(storageKey, textView.getText().toString());
                            Log.e(TAG,storageKey + " in RoomData afterTextChanged is " + RoomData.getRoomX());
                            StoreData.putPref(storageKey, textView.getText().toString(), context);
                            Log.e(TAG, storageKey + " in SharedPreferences afterTextChanged is " + StoreData.getPref(storageKey, context));
                            Log.e(TAG,"valueUnitKey is " + RoomData.getValueUnitKey());
                        }
                    } catch (NumberFormatException e) {
                        //Error
                    }
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){

            }
            public void onTextChanged(CharSequence s, int start, int before, int count){

            }
        });
    }

    /**
     * Metoda koja pohranjuje vrijednosti u klasu SettingsData.
     */
    private void storeInSettingsData(String storageKey, String text){
        Log.e(TAG, "storageKey is " + storageKey);
        Log.e(TAG, "text is " + text + ".");
        if (storageKey.equals(sampleExpirationMillisecondsKey)){
            SettingsData.setSampleExpirationMilliseconds(Long.parseLong(text));
        }else if (storageKey.equals(DEFAULT_ARMA_SPEEDKey)){
            SettingsData.setDEFAULT_ARMA_SPEED(Double.parseDouble(text));
        }else if (storageKey.equals(KALMAN_Rkey)) {
            SettingsData.setKALMAN_R(Double.parseDouble(text));
        }else if (storageKey.equals(KALMAN_Qkey)) {
            SettingsData.setKALMAN_Q(Double.parseDouble(text));
        }
    }

    /**
     * Metoda koja provijerava phranjenu vrijednost te ako ona postoji upisuje ju u EditText.
     */
    private void checkForStoredValues(EditText editText, String storageKey) {
        if (StoreData.getPref(storageKey, context) != null && !StoreData.getPref(storageKey, context).isEmpty()){
            storeInSettingsData(storageKey, StoreData.getPref(storageKey, context));
            checkUnitAndSetEditText(editText, storageKey);
        }else {
            if (getSettingsDataValue(storageKey) == -1){
                editText.setText("");
            }else{
                checkUnitAndSetEditText(editText, storageKey);
            }
        }
    }

    /**
     * Metoda koja vrača pohranjene vrijednosti u klasi SettingsData.
     */
    private double getSettingsDataValue(String storageKey){
        if (storageKey.equals(sampleExpirationMillisecondsKey)){
            return SettingsData.getSampleExpirationMilliseconds();
        }else if (storageKey.equals(DEFAULT_ARMA_SPEEDKey)){
            return SettingsData.getDEFAULT_ARMA_SPEED();
        }else if (storageKey.equals(KALMAN_Rkey)){
            return SettingsData.getKALMAN_R();
        }else if (storageKey.equals(KALMAN_Qkey)){
            return SettingsData.getKALMAN_Q();
        }
        return 0;
    }

    /**
     * Metoda koja pohranjenu vrijednost upisuje u EditText.
     */
    private void checkUnitAndSetEditText(EditText editText, String storageKey){
        if (!String.valueOf(getSettingsDataValue(storageKey)).isEmpty()){
            DecimalFormat df = new DecimalFormat("###.###");
            editText.setText(df.format(getSettingsDataValue(storageKey)));
        }
    }

    /**
     * Metoda koja pokazuje upozorenje kod promijene RSSI filtra.
     */
    private void showRSSIwariningDialog(){
        new AlertDialog.Builder((MainActivity) getActivity())
                .setTitle(R.string.settings_RSSIwariningDialog_title)
                .setMessage(R.string.settings_RSSIwariningDialog_message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onBeaconServiceConnect() {

    }

    @Override
    public Context getApplicationContext() {
        return null;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {

    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }
}