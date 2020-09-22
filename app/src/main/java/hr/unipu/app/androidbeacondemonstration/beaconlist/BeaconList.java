package hr.unipu.app.androidbeacondemonstration.beaconlist;

import android.util.Log;

import java.util.ArrayList;

import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.meter;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.yards;

/**
 * Ova klasa služi za metode liste Beacon-a.
 *
 * @author Leopold Juraga
 * @version 1.0
 */
public class BeaconList {
    private static final String TAG = BeaconList.class.getSimpleName();
    public static ArrayList<BeaconListObject> beaconArrayListMeters = new ArrayList<>();
    public static ArrayList<BeaconListObject> beaconArrayListYards = new ArrayList<>();
    public static ArrayList<BeaconListObject> beaconArrayList = new ArrayList<>();

    /**
     * Metoda koja dodaje Beacon-e u beaconArrayListMeters i beaconArrayListYards ArrayList-e.
     */
    public static void addToBeaconArrayList(String id1, String id2, String id3, int rssi, int txPower, double distance){
        BeaconListObject beaconListObjectMeters = new BeaconListObject();
        beaconListObjectMeters.setId1(id1);
        beaconListObjectMeters.setId2(id2);
        beaconListObjectMeters.setId3(id3);
        beaconListObjectMeters.setRssi(rssi);
        beaconListObjectMeters.setTxPower(txPower);
        beaconListObjectMeters.setDistance(distance);
        beaconArrayListMeters.add(beaconListObjectMeters);
        BeaconListObject beaconListObjectYards = new BeaconListObject();
        beaconListObjectYards.setId1(id1);
        beaconListObjectYards.setId2(id2);
        beaconListObjectYards.setId3(id3);
        beaconListObjectYards.setRssi(rssi);
        beaconListObjectYards.setTxPower(txPower);
        beaconListObjectYards.setDistance(distance*1.094);
        beaconArrayListYards.add(beaconListObjectYards);
    }

    /**
     * Metoda koja određuje koja ArrayList-a će se koristiti, a što ovisi o mjernoj jedinici za udaljenost.
     */
    public static ArrayList getBeaconArrayList(String distanceUnit) {
        if (distanceUnit.equals(meter)){
            beaconArrayList = beaconArrayListMeters;
            return beaconArrayList;
        }else if (distanceUnit.equals(yards)){
            beaconArrayList = beaconArrayListYards;
            return beaconArrayList;
        }
        return null;
    }

    /**
     * Metoda koja dostavlja beacon ID.
     */
    public static String getBeaconID(int index) {
        return beaconArrayListMeters.get(index).getId1()+beaconArrayListMeters.get(index).getId2()+beaconArrayListMeters.get(index).getId3();
    }

    /**
     * Metoda koja dostavlja beacon ID u definiranom obliku.
     */
    public static String getBeaconIDname(int index) {
        return "UUID: " + beaconArrayListMeters.get(index).getId1() + "\nMajor: "+ beaconArrayListMeters.get(index).getId2() + " Minor: "+ beaconArrayListMeters.get(index).getId3();
    }

    /**
     * Metoda koja dostavlja udaljenost Beacona-a pomoču ID-a.
     */
    public static double getDistanceOfBeaconByID(String id, String distanceUnit){
        double distance = 0;
        if (distanceUnit.equals(meter)){
            beaconArrayList = beaconArrayListMeters;
            for (BeaconListObject beacon : beaconArrayList) {
                if(id.equals(beacon.getId1()+beacon.getId2()+beacon.getId3())){
                    distance = beacon.getDistance();
                }
            }
        }else if (distanceUnit.equals(yards)){
            beaconArrayList = beaconArrayListYards;
            for (BeaconListObject beacon : beaconArrayList) {
                if(id.equals(beacon.getId1()+beacon.getId2()+beacon.getId3())){
                    distance = beacon.getDistance();
                }
            }
        }
        return distance;
    }

    /**
     * Metoda koja govori dali je Beacon s definiranim ID-om vidljiv.
     */
    public static boolean isBeaconVisible(String id){
        Log.e(TAG, "id is " + id);
        boolean result = false;
        Log.e(TAG, "result before is " + result);
        for (BeaconListObject beacon : beaconArrayListMeters) {
            if(id.equals(beacon.getId1()+beacon.getId2()+beacon.getId3())){
                Log.e(TAG, "id to search for is " + id);
                Log.e(TAG, "id in list is " + beacon.getId1()+beacon.getId2()+beacon.getId3());
                result = true;
                Log.e(TAG, "result search is " + result);
            }
        }
        Log.e(TAG, "result after is " + result);
        return result;
    }

    /**
     * Metoda koja briše ArrayList-e.
     */
    public static void clearBeaconArrayList() {
        beaconArrayListMeters.clear();
        beaconArrayListYards.clear();
        beaconArrayList.clear();
    }
}

