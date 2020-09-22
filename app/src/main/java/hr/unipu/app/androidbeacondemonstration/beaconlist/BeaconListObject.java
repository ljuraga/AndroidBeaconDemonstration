package hr.unipu.app.androidbeacondemonstration.beaconlist;

import java.text.DecimalFormat;

/**
 * Ova klasa služi za metode objekta BeaconList.
 *
 * @author Leopold Juraga
 * @version 1.0
 */
public class BeaconListObject {

    private String id1;
    private String id2;
    private String id3;
    private int rssi;
    private int txPower;
    private double distance;
    private static String distanceUnit;

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getId3() {
        return id3;
    }

    public void setId3(String id3) {
        this.id3 = id3;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getTxPower() {
        return txPower;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public static void setDistanceUnit(String unit) {
        distanceUnit = unit;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("###.###");
        return ("UUID: " + this.getId1() + "\nMajor: "+ this.getId2() + " Minor: "+ this.getId3() + "\nRSSI: "+ this.getRssi() + " TxPower: "+ this.getTxPower() + "\nDistance: " + df.format(this.getDistance()) + " " + distanceUnit);
    }
}
