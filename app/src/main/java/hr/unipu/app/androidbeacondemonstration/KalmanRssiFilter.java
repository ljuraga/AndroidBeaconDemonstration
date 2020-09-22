package hr.unipu.app.androidbeacondemonstration;

import android.util.Log;

import org.altbeacon.beacon.service.RssiFilter;

/**
 * Ova klasa služi za implementaciju Kalman RSSI Filter-a za koristenje u altbeacon api-u.
 * Preuzeto sa: https://stackoverflow.com/questions/36399927/distance-calculation-from-rssi-ble-android.
 *
 * @author Z3R0 i Leopold Juraga
 * @version 1.0
 */
public class KalmanRssiFilter implements RssiFilter {

    private static double R = 0.125d;   //  Process Noise
    private static double Q = 0.5d;   //  Measurement Noise
    private double A;   //  State Vector
    private double B;   //  Control Vector
    private double C;   //  Measurement Vector

    private Double x;   //  Filtered Measurement Value (No Noise)
    private double cov; //  Covariance

    private static final String TAG = KalmanRssiFilter.class.getSimpleName();

    public KalmanRssiFilter(){
        A = 1;
        B = 0;
        C = 1;
    }

    public static void setKALMAN_R(double newKALMAN_R){
        R = newKALMAN_R;
    }

    public static void setKALMAN_Q(double newKALMAN_Q){
        Q = newKALMAN_Q;
    }

    /** Public Methods **/
    public void addMeasurement(Integer rssi){
        applyFilter(rssi, 0.0d);
    }

    @Override
    public boolean noMeasurementsAvailable() {
        return false;
    }

    /**
     * Filters a measurement
     *
     * @param measurement The measurement value to be filtered
     * @param u The controlled input value
     * @return The filtered value
     */
    private double applyFilter(double measurement, double u) {
        double predX;           //  Predicted Measurement Value
        double K;               //  Kalman Gain
        double predCov;         //  Predicted Covariance
        if (x == null) {
            x = (1 / C) * measurement;
            cov = (1 / C) * Q * (1 / C);
            Log.d(TAG, "R: " + R);
            Log.d(TAG, "Q: " + Q);
        } else {
            predX = predictValue(u);
            predCov = getUncertainty();
            K = predCov * C * (1 / ((C * predCov * C) + Q));
            x = predX + K * (measurement - (C * predX));
            cov = predCov - (K * C * predCov);
        }
        return x;
    }

    public double calculateRssi() {
        return x;
    }

    @Override
    public int getMeasurementCount() {
        return 0;
    }

    /** Private Methods **/
    private double predictValue(double control){
        return (A * x) + (B * control);
    }

    private double getUncertainty(){
        return ((A * cov) * A) + R;
    }

    @Override
    public String toString() {
        return "KalmanFilter{" +
                "R=" + R +
                ", Q=" + Q +
                ", A=" + A +
                ", B=" + B +
                ", C=" + C +
                ", x=" + x +
                ", cov=" + cov +
                '}';
    }
}