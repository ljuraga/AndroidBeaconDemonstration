package hr.unipu.app.androidbeacondemonstration.ui.room;

import android.content.Context;

import hr.unipu.app.androidbeacondemonstration.StoreData;

import static hr.unipu.app.androidbeacondemonstration.StoreKeys.distanceUnitKey;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.meter;
import static hr.unipu.app.androidbeacondemonstration.ui.settings.SettingsData.yards;

/**
 * Ova klasa služi za metode i varijable podataka potrebnih fragment-ima RoomSetup i RoomMap.
 *
 * @author Leopold Juraga
 * @version 1.0
 */
public class RoomData {
    static double roomX = -1;
    static double roomY = -1;
    static String beaconNumber = "2";
    static boolean beacon1selected = false;
    static String beacon1ID;
    static String beacon1IDname;
    static double beacon1x = -1;
    static double beacon1y = -1;
    static double beacon1distance;
    static boolean beacon2selected = false;
    static String beacon2ID;
    static String beacon2IDname;
    static double beacon2x = -1;
    static double beacon2y = -1;
    static double beacon2distance;
    static boolean beacon3selected = false;
    static String beacon3ID;
    static String beacon3IDname;
    static double beacon3x = -1;
    static double beacon3y = -1;
    static double beacon3distance;
    static boolean selectBeaconsON = false;
    static boolean startedEnteringData = false;
    static boolean startedSelectingBeacon1 = false;
    static boolean startedSelectingBeacon2 = false;
    static boolean startedSelectingBeacon3 = false;
    static String valueUnitKey;

    public static double getRoomX() {
        return roomX;
    }

    public static void setRoomX(double roomX) {
        RoomData.roomX = roomX;
    }

    public static double getRoomY() {
        return roomY;
    }

    public static void setRoomY(double roomY) {
        RoomData.roomY = roomY;
    }

    public static String getBeaconNumber() {
        return beaconNumber;
    }

    public static void setBeaconNumber(String beaconNumber) {
        RoomData.beaconNumber = beaconNumber;
    }

    public static boolean isBeacon1selected() {
        return beacon1selected;
    }

    public static void setBeacon1selected(boolean beacon1selected) {
        RoomData.beacon1selected = beacon1selected;
    }

    public static String getBeacon1ID() {
        return beacon1ID;
    }

    public static String getBeacon1IDname() {
        return beacon1IDname;
    }

    public static void setBeacon1ID(String beacon1ID, String idName) {
        RoomData.beacon1ID = beacon1ID;
        beacon1IDname = idName;
    }

    public static double getBeacon1x() {
        return beacon1x;
    }

    public static void setBeacon1x(double beacon1x) {
        RoomData.beacon1x = beacon1x;
    }

    public static double getBeacon1y() {
        return beacon1y;
    }

    public static void setBeacon1y(double beacon1y) {
        RoomData.beacon1y = beacon1y;
    }

    public static double getBeacon1distance() {
        return beacon1distance;
    }

    public static void setBeacon1distance(double beacon1distance) {
        RoomData.beacon1distance = beacon1distance;
    }

    public static boolean isBeacon2selected() {
        return beacon2selected;
    }

    public static void setBeacon2selected(boolean beacon2selected) {
        RoomData.beacon2selected = beacon2selected;
    }

    public static String getBeacon2ID() {
        return beacon2ID;
    }

    public static String getBeacon2IDname() {
        return beacon2IDname;
    }

    public static void setBeacon2ID(String beacon2ID, String idName) {
        RoomData.beacon2ID = beacon2ID;
        beacon2IDname = idName;
    }

    public static double getBeacon2x() {
        return beacon2x;
    }

    public static void setBeacon2x(double beacon2x) {
        RoomData.beacon2x = beacon2x;
    }

    public static double getBeacon2y() {
        return beacon2y;
    }

    public static void setBeacon2y(double beacon2y) {
        RoomData.beacon2y = beacon2y;
    }

    public static double getBeacon2distance() {
        return beacon2distance;
    }

    public static void setBeacon2distance(double beacon2distance) {
        RoomData.beacon2distance = beacon2distance;
    }

    public static boolean isBeacon3selected() {
        return beacon3selected;
    }

    public static void setBeacon3selected(boolean beacon3selected) {
        RoomData.beacon3selected = beacon3selected;
    }

    public static String getBeacon3ID() {
        return beacon3ID;
    }

    public static String getBeacon3IDname() {
        return beacon3IDname;
    }

    public static void setBeacon3ID(String beacon3ID, String idName) {
        RoomData.beacon3ID = beacon3ID;
        beacon3IDname = idName;
    }

    public static double getBeacon3x() {
        return beacon3x;
    }

    public static void setBeacon3x(double beacon3x) {
        RoomData.beacon3x = beacon3x;
    }

    public static double getBeacon3y() {
        return beacon3y;
    }

    public static void setBeacon3y(double beacon3y) {
        RoomData.beacon3y = beacon3y;
    }

    public static double getBeacon3distance() {
        return beacon3distance;
    }

    public static void setBeacon3distance(double beacon3distance) {
        RoomData.beacon3distance = beacon3distance;
    }

    public static boolean isSelectBeaconsON() {
        return selectBeaconsON;
    }

    public static void setSelectBeaconsON(boolean selectBeaconsON) {
        RoomData.selectBeaconsON = selectBeaconsON;
    }

    public static boolean isStartedEnteringData() {
        return startedEnteringData;
    }

    public static void setStartedEnteringData(boolean startedEnteringData) {
        RoomData.startedEnteringData = startedEnteringData;
    }

    public static boolean isStartedSelectingBeacon1() {
        return startedSelectingBeacon1;
    }

    public static void setStartedSelectingBeacon1(boolean startedSelectingBeacon1) {
        RoomData.startedSelectingBeacon1 = startedSelectingBeacon1;
    }

    public static boolean isStartedSelectingBeacon2() {
        return startedSelectingBeacon2;
    }

    public static void setStartedSelectingBeacon2(boolean startedSelectingBeacon2) {
        RoomData.startedSelectingBeacon2 = startedSelectingBeacon2;
    }

    public static boolean isStartedSelectingBeacon3() {
        return startedSelectingBeacon3;
    }

    public static void setStartedSelectingBeacon3(boolean startedSelectingBeacon3) {
        RoomData.startedSelectingBeacon3 = startedSelectingBeacon3;
    }

    public static String getValueUnitKey() {
        return valueUnitKey;
    }

    public static void setValueUnitKey(String valueUnitKey) {
        RoomData.valueUnitKey = valueUnitKey;
    }

    public static String getDistanceUnit(Context context){
        String distanceUnit = null;
        if (StoreData.getPref(distanceUnitKey, context).equals(meter)){
            distanceUnit = meter;
        }else if (StoreData.getPref(distanceUnitKey, context).equals(yards)){
            distanceUnit = yards;
        }
        return distanceUnit;
    }
}
