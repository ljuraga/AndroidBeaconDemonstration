package hr.unipu.app.androidbeacondemonstration.ui.settings;

/**
 * Ova klasa služi za metode i varijable podataka potrebnih fragment-u Settings.
 *
 * @author Leopold Juraga
 * @version 1.0
 */
public class SettingsData {
    public static String meter = "Meters";
    public static String yards = "Yards";
    public static String scanIntervalTime = "scan";
    public static String timeBetweenScanInterval = "betweenScan";
    static int progressScan = -1;
    static int progressBetweenScan = -1;
    public static final long one = 1001l;
    public static final long two = 2001l;
    public static final long three = 3001l;
    public static final long four = 4001l;
    public static final long five = 5001l;
    public static final long six = 6001l;
    public static final long seven = 7001l;
    public static final long eight = 8001l;
    public static final long nine = 9001l;
    public static final long ten = 10001l;
    public static final String system = "system";
    public static final String dark = "dark";
    public static final String light = "light";
    public static final String none = "none";
    public static final String runningAverage = "runningAverage";
    public static final String arma = "arma";
    public static final String kalman = "kalman";
    static long sampleExpirationMilliseconds;
    static double DEFAULT_ARMA_SPEED;
    static double KALMAN_R;
    static double KALMAN_Q;

    public static int getProgress(String type) {
        if (type.equals(scanIntervalTime)){
            return progressScan;
        }else if (type.equals(timeBetweenScanInterval)){
            return progressBetweenScan;
        }
        return 0;
    }

    public static void setProgress(int progress, String type) {
        if (type.equals(scanIntervalTime)){
            progressScan = progress;
        }else if (type.equals(timeBetweenScanInterval)){
            progressBetweenScan = progress;
        }
    }

    public static long getInterval(int progressChangedValue){
        if (progressChangedValue == 1){
            return one;
        }else if (progressChangedValue == 2){
            return two;
        }else if (progressChangedValue == 3){
            return three;
        }else if (progressChangedValue == 4){
            return four;
        }else if (progressChangedValue == 5){
            return five;
        }else if (progressChangedValue == 6){
            return six;
        }else if (progressChangedValue == 7){
            return seven;
        }else if (progressChangedValue == 8){
            return eight;
        }else if (progressChangedValue == 9){
            return nine;
        }else if (progressChangedValue == 10){
            return ten;
        }
        return 0;
    }

    public static long getSampleExpirationMilliseconds() {
        return sampleExpirationMilliseconds;
    }

    public static void setSampleExpirationMilliseconds(long sampleExpirationMilliseconds) {
        SettingsData.sampleExpirationMilliseconds = sampleExpirationMilliseconds;
    }

    public static double getDEFAULT_ARMA_SPEED() {
        return DEFAULT_ARMA_SPEED;
    }

    public static void setDEFAULT_ARMA_SPEED(double DEFAULT_ARMA_SPEED) {
        SettingsData.DEFAULT_ARMA_SPEED = DEFAULT_ARMA_SPEED;
    }

    public static double getKALMAN_R() {
        return KALMAN_R;
    }

    public static void setKALMAN_R(double KALMAN_R) {
        SettingsData.KALMAN_R = KALMAN_R;
    }

    public static double getKALMAN_Q() {
        return KALMAN_Q;
    }

    public static void setKALMAN_Q(double KALMAN_Q) {
        SettingsData.KALMAN_Q = KALMAN_Q;
    }
}
