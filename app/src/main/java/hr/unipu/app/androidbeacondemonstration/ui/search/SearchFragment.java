package hr.unipu.app.androidbeacondemonstration.ui.search;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.altbeacon.beacon.service.ArmaRssiFilter;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.util.ArrayList;

import hr.unipu.app.androidbeacondemonstration.KalmanRssiFilter;
import hr.unipu.app.androidbeacondemonstration.MainActivity;
import hr.unipu.app.androidbeacondemonstration.StoreData;
import hr.unipu.app.androidbeacondemonstration.beaconlist.BeaconListObject;
import hr.unipu.app.androidbeacondemonstration.R;
import hr.unipu.app.androidbeacondemonstration.beaconlist.BeaconList;
import hr.unipu.app.androidbeacondemonstration.ui.room.RoomData;
import hr.unipu.app.androidbeacondemonstration.ui.room.RoomSetupFragment;

import static hr.unipu.app.androidbeacondemonstration.StoreKeys.DEFAULT_ARMA_SPEEDKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.KALMAN_Qkey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.KALMAN_Rkey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.distanceUnitKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.rssiFilterKey;
import static hr.unipu.app.androidbeacondemonstration.StoreKeys.sampleExpirationMillisecondsKey;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.arma;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.kalman;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.meter;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.runningAverage;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.yards;
import static hr.unipu.app.androidbeacondemonstration.ui.room.RoomMapFragment.stopPinMover;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsFragment.rssiFilterChanged;

/**
 * Ova klasa služi za metode fragment-a Search.
 *
 * @author Leopold Juraga
 * @version 1.0
 */
public class SearchFragment extends Fragment {

    private static final String TAG = SearchFragment.class.getSimpleName();
    public static ArrayAdapter<BeaconListObject> beaconArrayAdapter;
    ListView beaconListView;
    ArrayList<BeaconListObject> beaconArrayList = new ArrayList();
    private Context context;
    private int beaconNumber;

    /**
     * Metoda koja se izvršava kod pokretanja fragment-a.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        stopPinMover = true;

        context = getActivity().getApplicationContext();

        if (RoomData.isStartedEnteringData() == false) {
            RoomData.setSelectBeaconsON(false);
            RoomData.setStartedSelectingBeacon1(false);
            RoomData.setStartedSelectingBeacon2(false);
            RoomData.setStartedSelectingBeacon3(false);
        }

        if (StoreData.getPref(rssiFilterKey, context).equals(runningAverage)) {
            RunningAverageRssiFilter.setSampleExpirationMilliseconds(Long.parseLong(StoreData.getPref(sampleExpirationMillisecondsKey, context)));
        }else if (StoreData.getPref(rssiFilterKey, context).equals(arma)) {
            ArmaRssiFilter.setDEFAULT_ARMA_SPEED(Double.parseDouble(StoreData.getPref(DEFAULT_ARMA_SPEEDKey, context)));
        }else if (StoreData.getPref(rssiFilterKey, context).equals(kalman)) {
            KalmanRssiFilter.setKALMAN_R(Double.parseDouble(StoreData.getPref(KALMAN_Rkey, context)));
            KalmanRssiFilter.setKALMAN_Q(Double.parseDouble(StoreData.getPref(KALMAN_Qkey, context)));
        }

        if (rssiFilterChanged == true){
            showRSSIwariningDialog();
        }

        beaconListView = (ListView) root.findViewById(R.id.beacon_list);

        if (StoreData.getPref(distanceUnitKey, context).equals(meter)){
            beaconArrayList = BeaconList.getBeaconArrayList(meter);
            BeaconListObject.setDistanceUnit("m");
        }else if (StoreData.getPref(distanceUnitKey, context).equals(yards)){
            beaconArrayList = BeaconList.getBeaconArrayList(yards);
            BeaconListObject.setDistanceUnit("yd");
        }

        beaconArrayAdapter = new ArrayAdapter<BeaconListObject>(getActivity(), android.R.layout.simple_list_item_1, beaconArrayList);
        beaconListView.setAdapter(beaconArrayAdapter);

        beaconListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            // argument position gives the index of item which is clicked
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3)
            {
                String id = BeaconList.getBeaconID(position);
                String idName = BeaconList.getBeaconIDname(position);
                beaconNumber = 0;
                if (RoomData.isSelectBeaconsON() == true) {
                    Log.e(TAG, "getBeaconNumber is " + RoomData.getBeaconNumber());
                    if (RoomData.isStartedSelectingBeacon1() == true){
                        if (RoomData.isBeacon1selected() == false) {
                            if (!RoomData.getBeacon2ID().equals(id) && !RoomData.getBeacon3ID().equals(id)){
                                beaconNumber = 1;
                                setBeaconID(id, idName);
                                Log.e(TAG, "Beacon 1 ID is " + RoomData.getBeacon1ID());
                                setBeacon();
                            }else if (RoomData.getBeacon2ID().equals(id)){
                                showSnackbar("alreadySelected",2);
                            }else if (RoomData.getBeacon3ID().equals(id)){
                                showSnackbar("alreadySelected",3);
                            }
                        }
                    }
                    if (RoomData.isStartedSelectingBeacon2() == true){
                        if (RoomData.isBeacon2selected() == false) {
                            if (!RoomData.getBeacon1ID().equals(id) && !RoomData.getBeacon3ID().equals(id)){
                                beaconNumber = 2;
                                setBeaconID(id, idName);
                                Log.e(TAG, "Beacon 2 ID is " + RoomData.getBeacon2ID());
                                setBeacon();
                            }else if (RoomData.getBeacon1ID().equals(id)){
                                showSnackbar("alreadySelected",1);
                            }else if (RoomData.getBeacon3ID().equals(id)){
                                showSnackbar("alreadySelected",3);
                            }
                        }
                    }
                    if (RoomData.isStartedSelectingBeacon3() == true){
                        if (RoomData.isBeacon3selected() == false) {
                            if (!RoomData.getBeacon1ID().equals(id) && !RoomData.getBeacon2ID().equals(id)){
                                beaconNumber = 3;
                                setBeaconID(id, idName);
                                Log.e(TAG, "Beacon 3 ID is " + RoomData.getBeacon3ID());
                                setBeacon();
                            }else if (RoomData.getBeacon1ID().equals(id)){
                                showSnackbar("alreadySelected",1);
                            }else if (RoomData.getBeacon2ID().equals(id)){
                                showSnackbar("alreadySelected",2);
                            }
                        }
                    }
                }
            }
        });

        return root;
    }

    /**
     * Metoda koja odabrani Beacon postavlja kao Beacon 1, 2 ili 3 za lociranje u prostoru.
     */
    public void setBeaconID(String id, String idName){
        if (beaconNumber == 1) {
            RoomData.setBeacon1selected(true);
            RoomData.setBeacon1ID(id, idName);
        }else if (beaconNumber == 2) {
            RoomData.setBeacon2selected(true);
            RoomData.setBeacon2ID(id, idName);
        }else if (beaconNumber == 3) {
            RoomData.setBeacon3selected(true);
            RoomData.setBeacon3ID(id, idName);
        }
    }

    /**
     * Metoda koja služi za prikaz Snackbar-a za odabrani beacon te povratak na fragment RoomSetup.
     */
    public void setBeacon(){
        showSnackbar("selected", 0);
        RoomSetupFragment roomSetupFragment = new RoomSetupFragment();
        getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, roomSetupFragment).addToBackStack("Search").commit();
        //reset selection
        RoomData.setSelectBeaconsON(false);
        RoomData.setStartedSelectingBeacon1(false);
        RoomData.setStartedSelectingBeacon2(false);
        RoomData.setStartedSelectingBeacon3(false);
        beaconNumber = 0;
    }

    /**
     * Metoda u kojoj je implementiran prikaz Snackbar-a.
     */
    public void showSnackbar(String selection, int number){
        String search_toast_beacon = getResources().getString(R.string.search_toast_beacon);
        String search_toast_beacon_selected = getResources().getString(R.string.search_toast_beacon_selected);
        String search_toast_beacon_already_selected = getResources().getString(R.string.search_toast_beacon_already_selected);
        String search_toast_beacon_dot = getResources().getString(R.string.search_toast_beacon_dot);
        if (selection.equals("selected")){
            Snackbar.make(getView(), search_toast_beacon + beaconNumber + search_toast_beacon_selected, Snackbar.LENGTH_LONG).show();
        }else if (selection.equals("alreadySelected")){
            Snackbar.make(getView(), search_toast_beacon + search_toast_beacon_already_selected + number + search_toast_beacon_dot, Snackbar.LENGTH_LONG).show();
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
}