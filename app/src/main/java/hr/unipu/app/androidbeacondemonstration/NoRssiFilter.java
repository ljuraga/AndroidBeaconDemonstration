package hr.unipu.app.androidbeacondemonstration;

import org.altbeacon.beacon.service.RssiFilter;

/**
 * Ova klasa služi za implementaciju nikakvog RSSI Filter-a za koristenje u altbeacon api-u.
 *
 * @author Leopold Juraga
 * @version 1.0
 */
public class NoRssiFilter implements RssiFilter {

    private double rssi;

    @Override
    public void addMeasurement(Integer integer) {
        rssi = integer;
    }

    @Override
    public boolean noMeasurementsAvailable() {
        return false;
    }

    @Override
    public double calculateRssi() {
        return rssi;
    }

    @Override
    public int getMeasurementCount() {
        return 0;
    }
}