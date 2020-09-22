package hr.unipu.app.androidbeacondemonstration.ui.room;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;

import hr.unipu.app.androidbeacondemonstration.MainActivity;
import hr.unipu.app.androidbeacondemonstration.R;
import hr.unipu.app.androidbeacondemonstration.StoreData;
import hr.unipu.app.androidbeacondemonstration.ui.search.SearchFragment;

import static hr.unipu.app.androidbeacondemonstration.StoreKeys.beacon1Xkey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.beacon1XunitKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.beacon1Ykey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.beacon1YunitKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.beacon2Xkey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.beacon2XunitKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.beacon2Ykey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.beacon2YunitKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.beacon3Xkey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.beacon3XunitKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.beacon3Ykey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.beacon3YunitKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.beaconNumberKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.distanceUnitKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.roomXkey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.roomXunitKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.roomYkey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.roomYunitKey;
import static hr.unipu.app.androidbeacondemonstration.ui.room.RoomMapFragment.stopPinMover;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.meter;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.yards;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsFragment.rssiFilterChanged;

/**
 * Ova klasa služi za metode fragment-a RoomSetup.
 *
 * @author Leopold Juraga
 * @version 1.0
 */
public class RoomSetupFragment extends Fragment {

    private static final String TAG = RoomSetupFragment.class.getSimpleName();
    LinearLayout beacon3;
    LinearLayout selectBeacon3;
    Button selectBeacon3button;
    private boolean radioButton2BeaconsSelected = true;
    private boolean radioButton3BeaconsSelected = false;
    private static String distanceUnit;
    private String twoBeacons = "2";
    private String threeBeacons = "3";
    private RadioGroup radioGroupBeacons;
    private static Context context;
    private EditText enterRoomSizeX;
    private EditText enterRoomSizeY;
    private EditText enterBeacon1X;
    private EditText enterBeacon1Y;
    private EditText enterBeacon2X;
    private EditText enterBeacon2Y;

    /**
     * Metoda koja se izvršava kod pokretanja fragment-a.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_room_setup, container, false);

        stopPinMover = true;

        context = getActivity().getApplicationContext();

        if (rssiFilterChanged == true){
            showRSSIwariningDialog();
        }

        Log.e(TAG,"isStartedEnteringData is " + RoomData.isStartedEnteringData());
        Log.e(TAG,"isSelectBeaconsON is " + RoomData.isSelectBeaconsON());
        Log.e(TAG,"isStartedSelectingBeacon1 is " + RoomData.isStartedSelectingBeacon1());
        Log.e(TAG,"isStartedSelectingBeacon2 is " + RoomData.isStartedSelectingBeacon2());
        Log.e(TAG,"isStartedSelectingBeacon3 is " + RoomData.isStartedSelectingBeacon3());

        enterRoomSizeX = (EditText) root.findViewById(R.id.enterRoomSizeX);
        final TextView roomSizeXDistanceUnit = (TextView) root.findViewById(R.id.roomSizeXDistanceUnit);
        enterRoomSizeY = (EditText) root.findViewById(R.id.enterRoomSizeY);
        final TextView roomSizeYDistanceUnit = (TextView) root.findViewById(R.id.roomSizeYDistanceUnit);
        enterBeacon1X = (EditText) root.findViewById(R.id.enterBeacon1X);
        final TextView beacon1XDistanceUnit = (TextView) root.findViewById(R.id.beacon1XDistanceUnit);
        enterBeacon1Y = (EditText) root.findViewById(R.id.enterBeacon1Y);
        final TextView beacon1YDistanceUnit = (TextView) root.findViewById(R.id.beacon1YDistanceUnit);
        enterBeacon2X = (EditText) root.findViewById(R.id.enterBeacon2X);
        final TextView beacon2XDistanceUnit = (TextView) root.findViewById(R.id.beacon2XDistanceUnit);
        enterBeacon2Y = (EditText) root.findViewById(R.id.enterBeacon2Y);
        final TextView beacon2YDistanceUnit = (TextView) root.findViewById(R.id.beacon2YDistanceUnit);
        final EditText enterBeacon3X = (EditText) root.findViewById(R.id.enterBeacon3X);
        final TextView beacon3XDistanceUnit = (TextView) root.findViewById(R.id.beacon3XDistanceUnit);
        final EditText enterBeacon3Y = (EditText) root.findViewById(R.id.enterBeacon3Y);
        final TextView beacon3YDistanceUnit = (TextView) root.findViewById(R.id.beacon3YDistanceUnit);

        beacon3 = (LinearLayout) root.findViewById(R.id.beacon3);
        selectBeacon3 = (LinearLayout) root.findViewById(R.id.selectBeacon3);
        selectBeacon3button = (Button) root.findViewById(R.id.selectBeacon3button);
        if (radioButton2BeaconsSelected == true && radioButton3BeaconsSelected == false && RoomData.getBeaconNumber().equals(twoBeacons)){
            beacon3.setVisibility(View.GONE);
            RoomData.setBeaconNumber(twoBeacons);
            selectBeacon3.setVisibility(View.GONE);
            selectBeacon3button.setVisibility(View.GONE);
        }

        if (RoomData.isStartedEnteringData() == false) {
            RoomData.setBeacon1selected(false);
            RoomData.setBeacon1ID("", "");
            RoomData.setStartedSelectingBeacon1(false);
            RoomData.setBeacon2selected(false);
            RoomData.setBeacon2ID("", "");
            RoomData.setStartedSelectingBeacon2(false);
            RoomData.setBeacon3selected(false);
            RoomData.setBeacon3ID("", "");
            RoomData.setStartedSelectingBeacon3(false);
        }

        checkForStoredValues(enterRoomSizeX, roomXkey, roomXunitKey);
        checkForStoredValues(enterRoomSizeY, roomYkey, roomYunitKey);
        checkForStoredValues(enterBeacon1X, beacon1Xkey, beacon1XunitKey);
        checkForStoredValues(enterBeacon1Y, beacon1Ykey, beacon1YunitKey);
        checkForStoredValues(enterBeacon2X, beacon2Xkey, beacon2XunitKey);
        checkForStoredValues(enterBeacon2Y, beacon2Ykey, beacon2YunitKey);
        checkForStoredValues(enterBeacon3X, beacon3Xkey, beacon3XunitKey);
        checkForStoredValues(enterBeacon3Y, beacon3Ykey, beacon3YunitKey);

        distanceUnit = RoomData.getDistanceUnit(context);

        setDistanceUnitTextView(roomSizeXDistanceUnit, distanceUnit);
        setDistanceUnitTextView(roomSizeYDistanceUnit, distanceUnit);
        setDistanceUnitTextView(beacon1XDistanceUnit, distanceUnit);
        setDistanceUnitTextView(beacon1YDistanceUnit, distanceUnit);
        setDistanceUnitTextView(beacon2XDistanceUnit, distanceUnit);
        setDistanceUnitTextView(beacon2YDistanceUnit, distanceUnit);
        setDistanceUnitTextView(beacon3XDistanceUnit, distanceUnit);
        setDistanceUnitTextView(beacon3YDistanceUnit, distanceUnit);

        addTextChangedListener(enterRoomSizeX, roomXkey);
        addTextChangedListener(enterRoomSizeY, roomYkey);
        addTextChangedListener(enterBeacon1X, beacon1Xkey);
        addTextChangedListener(enterBeacon1Y, beacon1Ykey);
        addTextChangedListener(enterBeacon2X, beacon2Xkey);
        addTextChangedListener(enterBeacon2Y, beacon2Ykey);
        addTextChangedListener(enterBeacon3X, beacon3Xkey);
        addTextChangedListener(enterBeacon3Y, beacon3Ykey);

        radioGroupBeacons = (RadioGroup) root.findViewById(R.id.radioGroupBeacons);

        checkRadioGroupBeacons(twoBeacons);
        checkRadioGroupBeacons(threeBeacons);

        radioGroupBeacons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.radioButton2Beacons:
                        setRadioGroupBeacons(twoBeacons);
                        break;
                    case R.id.radioButton3Beacons:
                        setRadioGroupBeacons(threeBeacons);
                        break;
                }
            }
        });

        final TextView selectBeacon1beaconName = (TextView) root.findViewById(R.id.selectBeacon1beaconName);
        final TextView selectBeacon2beaconName = (TextView) root.findViewById(R.id.selectBeacon2beaconName);
        final TextView selectBeacon3beaconName = (TextView) root.findViewById(R.id.selectBeacon3beaconName);

        if (RoomData.isBeacon1selected() == true){
            selectBeacon1beaconName.setText(RoomData.getBeacon1IDname());
        }
        if (RoomData.isBeacon2selected() == true){
            selectBeacon2beaconName.setText(RoomData.getBeacon2IDname());
        }
        if (RoomData.isBeacon3selected() == true){
            selectBeacon3beaconName.setText(RoomData.getBeacon3IDname());
        }

        Button selectBeacon1button = (Button) root.findViewById(R.id.selectBeacon1button);
        selectBeacon1button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBeaconButton(1);
            }
        });

        Button selectBeacon2button = (Button) root.findViewById(R.id.selectBeacon2button);
        selectBeacon2button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBeaconButton(2);
            }
        });

        selectBeacon3button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBeaconButton(3);
            }
        });

        Button addTestValues = (Button) root.findViewById(R.id.addTestValues);
        addTestValues.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomData.setRoomX(5.35);
                enterRoomSizeX.setText(String.valueOf(RoomData.getRoomX()));
                RoomData.setRoomY(3.3);
                enterRoomSizeY.setText(String.valueOf(RoomData.getRoomY()));
                RoomData.setBeacon1x(0.3);
                enterBeacon1X.setText(String.valueOf(RoomData.getBeacon1x()));
                RoomData.setBeacon1y(0.3);
                enterBeacon1Y.setText(String.valueOf(RoomData.getBeacon1y()));
                RoomData.setBeacon2x(5);
                enterBeacon2X.setText(String.valueOf(RoomData.getBeacon2x()));
                RoomData.setBeacon2y(3);
                enterBeacon2Y.setText(String.valueOf(RoomData.getBeacon2y()));
            }
        });
        addTestValues.setVisibility(View.GONE);

        Button clear = (Button) root.findViewById(R.id.clear);
        clear.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterRoomSizeX.setText("");
                StoreData.putPref(roomXkey, "", context);
                RoomData.setRoomX(0);
                enterRoomSizeY.setText("");
                StoreData.putPref(roomYkey, "", context);
                RoomData.setRoomY(0);
                enterBeacon1X.setText("");
                StoreData.putPref(beacon1Xkey, "", context);
                RoomData.setBeacon1x(0);
                enterBeacon1Y.setText("");
                StoreData.putPref(beacon1Ykey, "", context);
                RoomData.setBeacon1y(0);
                enterBeacon2X.setText("");
                StoreData.putPref(beacon2Xkey, "", context);
                RoomData.setBeacon2x(0);
                enterBeacon2Y.setText("");
                StoreData.putPref(beacon2Ykey, "", context);
                RoomData.setBeacon2y(0);
                enterBeacon3X.setText("");
                StoreData.putPref(beacon3Xkey, "", context);
                RoomData.setBeacon3x(0);
                enterBeacon3Y.setText("");
                StoreData.putPref(beacon3Ykey, "", context);
                RoomData.setBeacon3y(0);

                RoomData.setBeacon1selected(false);
                RoomData.setBeacon1ID(String.valueOf(R.string.select_beacon_not_selected), "");
                selectBeacon1beaconName.setText(R.string.select_beacon_not_selected);
                RoomData.setBeacon2selected(false);
                RoomData.setBeacon2ID(String.valueOf(R.string.select_beacon_not_selected), "");
                selectBeacon2beaconName.setText(R.string.select_beacon_not_selected);
                RoomData.setBeacon3selected(false);
                RoomData.setBeacon3ID(String.valueOf(R.string.select_beacon_not_selected), "");
                selectBeacon3beaconName.setText(R.string.select_beacon_not_selected);
            }
        });
        clear.setVisibility(View.GONE);

        Button start = (Button) root.findViewById(R.id.start);
        start.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkData = false;
                boolean checkBeaconSelection = false;

                if (radioButton2BeaconsSelected == true && radioButton3BeaconsSelected == false){
                    if (!enterRoomSizeX.getText().toString().trim().equals("") && !enterRoomSizeY.getText().toString().trim().equals("") && !enterBeacon1X.getText().toString().trim().equals("") && !enterBeacon1Y.getText().toString().trim().equals("") && !enterBeacon2X.getText().toString().trim().equals("") && !enterBeacon2Y.getText().toString().trim().equals("")){
                        setBeacon2data();
                        checkData = true;
                    }else{
                        showSnackbar("data");
                    }
                }else if (radioButton2BeaconsSelected == false && radioButton3BeaconsSelected == true){
                    if (!enterRoomSizeX.getText().toString().trim().equals("") && !enterRoomSizeY.getText().toString().trim().equals("") && !enterBeacon1X.getText().toString().trim().equals("") && !enterBeacon1Y.getText().toString().trim().equals("") && !enterBeacon2X.getText().toString().trim().equals("") && !enterBeacon2Y.getText().toString().trim().equals("") && !enterBeacon3X.getText().toString().trim().equals("") && !enterBeacon3Y.getText().toString().trim().equals("")){
                        setBeacon2data();
                        RoomData.setBeacon3x(checkComma(enterBeacon3X.getText().toString()));
                        RoomData.setBeacon3y(checkComma(enterBeacon3Y.getText().toString()));
                        checkData = true;
                    }else{
                        showSnackbar("data");
                    }
                }

                if (radioButton2BeaconsSelected == true && radioButton3BeaconsSelected == false){
                    if (RoomData.isBeacon1selected() == false || RoomData.isBeacon2selected() == false){
                        showSnackbar("beacons");
                    }else if (RoomData.isBeacon1selected() == true || RoomData.isBeacon2selected() == true){
                        checkBeaconSelection = true;
                    }
                }else if (radioButton2BeaconsSelected == false && radioButton3BeaconsSelected == true){
                    if (RoomData.isBeacon1selected() == false || RoomData.isBeacon2selected() == false || RoomData.isBeacon3selected() == false){
                        showSnackbar("beacons");
                    }else if (RoomData.isBeacon1selected() == true || RoomData.isBeacon2selected() == true || RoomData.isBeacon3selected() == true){
                        checkBeaconSelection = true;
                    }
                }

                if (checkData == true && checkBeaconSelection == true) {
                    RoomData.setStartedEnteringData(false);

                    RoomMapFragment roomMapFragment = new RoomMapFragment();

                    getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, roomMapFragment).addToBackStack("RoomSetup").commit();
                }
            }
        });

        return root;
    }

    /**
     * Metoda u kojoj je implementiran prikaz Snackbar-a.
     */
    private void showSnackbar(String selection){
        if (selection.equals("data")){
            Snackbar.make(getView(), R.string.room_setup_toast_data, Snackbar.LENGTH_LONG).show();
        }
        if (selection.equals("beacons")){
            Snackbar.make(getView(), R.string.room_setup_toast_beacons, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Metoda koja postasvlja mjernu jedinicu za udaljenost.
     */
    private void setDistanceUnitTextView(TextView textView, String distanceUnit){
        if (distanceUnit.equals(meter)){
            textView.setText("m");
        }else if (distanceUnit.equals(yards)){
            textView.setText("yd");
        }
    }

    /**
     * Metoda koja provjerava pohranjen odabira broja Beacon-a te ga postavlja, a ako on ne postoji postavlja odabir na 2 Beacon-a.
     */
    private void checkRadioGroupBeacons(String beaconNumber){
        if (StoreData.getPref(beaconNumberKey, context) != null){
            RoomData.setBeaconNumber(StoreData.getPref(beaconNumberKey, context));
            if (RoomData.getBeaconNumber().equals(beaconNumber)){
                setRadioGroupBeaconsCheck(beaconNumber);
            }
        }else if (StoreData.getPref(beaconNumberKey, context) == null){
            RoomData.setBeaconNumber(twoBeacons);
            setRadioGroupBeaconsCheck(twoBeacons);
        }
    }

    /**
     * Metoda koja služi za postavljanje prikaza layout-a fragment-a ovisno o broju Beacon-a.
     */
    private void setRadioGroupBeacons(String beaconNumber){
        if (beaconNumber.equals(twoBeacons)){
            beacon3.setVisibility(View.GONE);
            selectBeacon3.setVisibility(View.GONE);
            selectBeacon3button.setVisibility(View.GONE);
            radioButton2BeaconsSelected = true;
            radioButton3BeaconsSelected = false;
            RoomData.setBeaconNumber(twoBeacons);
            StoreData.putPref(beaconNumberKey, twoBeacons, context);
        }else if (beaconNumber.equals(threeBeacons)){
            beacon3.setVisibility(View.VISIBLE);
            selectBeacon3.setVisibility(View.VISIBLE);
            selectBeacon3button.setVisibility(View.VISIBLE);
            radioButton2BeaconsSelected = false;
            radioButton3BeaconsSelected = true;
            RoomData.setBeaconNumber(threeBeacons);
            StoreData.putPref(beaconNumberKey, threeBeacons, context);
        }
    }

    /**
     * Metoda koja mjenja odabir broja Beacon-a u prikazu layout-a fragment-a.
     */
    private void setRadioGroupBeaconsCheck(String beaconNumber){
        if (beaconNumber.equals(twoBeacons)){
            radioGroupBeacons.check(R.id.radioButton2Beacons);
            setRadioGroupBeacons(twoBeacons);
        }else if (beaconNumber.equals(threeBeacons)){
            radioGroupBeacons.check(R.id.radioButton3Beacons);
            setRadioGroupBeacons(threeBeacons);
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
                            storeInRoomData(storageKey, textView.getText().toString());
                            Log.e(TAG,storageKey + " in RoomData afterTextChanged is " + RoomData.getRoomX());
                            StoreData.putPref(storageKey, textView.getText().toString(), context);
                            Log.e(TAG, storageKey + " in SharedPreferences afterTextChanged is " + StoreData.getPref(storageKey, context));
                            Log.e(TAG,"valueUnitKey is " + RoomData.getValueUnitKey());
                            if (StoreData.getPref(distanceUnitKey, context).equals(meter)){
                                StoreData.putPref(RoomData.getValueUnitKey(), meter, context);
                            }else if (StoreData.getPref(distanceUnitKey, context).equals(yards)){
                                StoreData.putPref(RoomData.getValueUnitKey(), yards, context);
                            }
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
     * Metoda koja provijerava phranjenu vrijednost te ako ona postoji upisuje ju u EditText.
     */
    private void checkForStoredValues(EditText editText, String storageKey, String valueUnitKey) {
        if (StoreData.getPref(storageKey, context) != null && !StoreData.getPref(storageKey, context).isEmpty()){
            Log.e(TAG,"roomDataValue before stored data is " + getRoomDataValue(storageKey));
            storeInRoomData(storageKey, StoreData.getPref(storageKey, context));
            Log.e(TAG,"storageKey stored data is " + StoreData.getPref(storageKey, context));
            Log.e(TAG,"roomDataValue after stored data is " + getRoomDataValue(storageKey));
            checkUnitAndSetEditText(editText, String.valueOf(getRoomDataValue(storageKey)), valueUnitKey);
            Log.e(TAG,"storageKey stored data after checkUnitAndSetEditText is " + StoreData.getPref(storageKey, context));
            Log.e(TAG,"roomDataValue after checkUnitAndSetEditText is " + getRoomDataValue(storageKey));
        }else {
            Log.e(TAG, storageKey + " roomDataValue before -1 is " + getRoomDataValue(storageKey));
            if (getRoomDataValue(storageKey) == -1){
                editText.setText("");
                Log.e(TAG, storageKey + " roomDataValue after -1 is " + getRoomDataValue(storageKey));
            }else{
                checkUnitAndSetEditText(editText, String.valueOf(getRoomDataValue(storageKey)), valueUnitKey);
                Log.e(TAG, storageKey + " roomDataValue after else is " + getRoomDataValue(storageKey));
            }
        }
    }

    /**
     * Metoda koja pohranjenu vrijednost upisuje u EditText.
     */
    private void checkUnitAndSetEditText(EditText editText, String text, String valueUnitKey){
        DecimalFormat df = new DecimalFormat("###.###");
        if (!text.isEmpty()){
            if (StoreData.getPref(distanceUnitKey, context).equals(meter) && StoreData.getPref(valueUnitKey, context).equals(yards)){
                editText.setText(df.format(Double.parseDouble(text)/1.094));
            }else if (StoreData.getPref(distanceUnitKey, context).equals(yards) && StoreData.getPref(valueUnitKey, context).equals(meter)){
                editText.setText(df.format(Double.parseDouble(text)*1.094));
            }else {
                editText.setText(df.format(Double.parseDouble(text)));
            }
        }
    }

    /**
     * Metoda koja pohranjuje vrijednosti u klasu RoomData.
     */
    private void storeInRoomData(String storageKey, String text){
        Log.e(TAG, "storageKey is " + storageKey);
        Log.e(TAG, "text is " + text + ".");
        if (storageKey.equals(roomXkey)){
            RoomData.setRoomX(Double.parseDouble(text));
            RoomData.setValueUnitKey(roomXunitKey);
        }else if (storageKey.equals(roomYkey)){
            RoomData.setRoomY(Double.parseDouble(text));
            RoomData.setValueUnitKey(roomYunitKey);
        }else if (storageKey.equals(beacon1Xkey)){
            RoomData.setBeacon1x(Double.parseDouble(text));
            RoomData.setValueUnitKey(beacon1XunitKey);
        }else if (storageKey.equals(beacon1Ykey)){
            RoomData.setBeacon1y(Double.parseDouble(text));
            RoomData.setValueUnitKey(beacon1YunitKey);
        }else if (storageKey.equals(beacon2Xkey)){
            RoomData.setBeacon2x(Double.parseDouble(text));
            RoomData.setValueUnitKey(beacon2XunitKey);
        }else if (storageKey.equals(beacon2Ykey)){
            RoomData.setBeacon2y(Double.parseDouble(text));
            RoomData.setValueUnitKey(beacon2YunitKey);
        }else if (storageKey.equals(beacon3Xkey)){
            RoomData.setBeacon3x(Double.parseDouble(text));
            RoomData.setValueUnitKey(beacon3XunitKey);
        }else if (storageKey.equals(beacon3Ykey)){
            RoomData.setBeacon3y(Double.parseDouble(text));
            RoomData.setValueUnitKey(beacon3YunitKey);
        }
    }

    /**
     * Metoda koja vrača pohranjene vrijednosti u klasi RoomData.
     */
    private double getRoomDataValue(String storageKey){
        if (storageKey.equals(roomXkey)){
            return RoomData.getRoomX();
        }else if (storageKey.equals(roomYkey)){
            return RoomData.getRoomY();
        }else if (storageKey.equals(beacon1Xkey)){
            return RoomData.getBeacon1x();
        }else if (storageKey.equals(beacon1Ykey)){
            return RoomData.getBeacon1y();
        }else if (storageKey.equals(beacon2Xkey)){
            return RoomData.getBeacon2x();
        }else if (storageKey.equals(beacon2Ykey)){
            return RoomData.getBeacon2y();
        }else if (storageKey.equals(beacon3Xkey)){
            return RoomData.getBeacon3x();
        }else if (storageKey.equals(beacon3Ykey)){
            return RoomData.getBeacon3y();
        }
        return 0;
    }

    /**
     * Metoda koja inicijalizira odabir Beacon-a 1, 2 ili 3 te prikazuje fragrment Search.
     */
    public void selectBeaconButton(int beacon){
        RoomData.setSelectBeaconsON(true);
        RoomData.setStartedEnteringData(true);
        if (beacon == 1){
            RoomData.setStartedSelectingBeacon1(true);
            RoomData.setBeacon1selected(false);
        }else if (beacon == 2){
            RoomData.setStartedSelectingBeacon2(true);
            RoomData.setBeacon2selected(false);
        }else if (beacon == 3){
            RoomData.setStartedSelectingBeacon3(true);
            RoomData.setBeacon3selected(false);
        }
        SearchFragment homeFragment = new SearchFragment();
        getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, homeFragment).addToBackStack("RoomSetup").commit();
    }

    /**
     * Metoda koja postavlja upisane vrijednosti u EditText-ovima u klasu RoomData.
     */
    public void setBeacon2data(){
        RoomData.setRoomX(checkComma(enterRoomSizeX.getText().toString()));
        RoomData.setRoomY(checkComma(enterRoomSizeY.getText().toString()));
        RoomData.setBeacon1x(checkComma(enterBeacon1X.getText().toString()));
        RoomData.setBeacon1y(checkComma(enterBeacon1Y.getText().toString()));
        RoomData.setBeacon2x(checkComma(enterBeacon2X.getText().toString()));
        RoomData.setBeacon2y(checkComma(enterBeacon2Y.getText().toString()));
    }

    /**
     * Metoda koja provijerava dali je u EditText-u broj upisan sa zarezom ili točkom te ako je zarez zamjenjuje ga s točkom.
     */
    public double checkComma(String value){
        Double correctValue=null;
        try{
            correctValue=Double.valueOf(value);
        }catch(Exception e){
            correctValue=Double.valueOf(value.replace(',','.'));
        }
        return correctValue;
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
}