package hr.unipu.app.androidbeacondemonstration.ui.room;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import hr.unipu.app.androidbeacondemonstration.R;
import hr.unipu.app.androidbeacondemonstration.beaconlist.BeaconList;

/**
 * Ova klasa služi za metode fragment-a RoomMap.
 *
 * @author Leopold Juraga
 * @version 1.0
 */
public class RoomMapFragment extends Fragment {

    private static final String TAG = RoomMapFragment.class.getSimpleName();
    static PinView roomMap;
    static int PointFx;
    static int PointFy;
    private String distanceUnit;
    public static boolean stopPinMover = false;
    private Context context;

    /**
     * Metoda koja se izvršava kod pokretanja fragment-a.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_room_map, container, false);

        stopPinMover = false;

        context = getActivity().getApplicationContext();

        RelativeLayout room = (RelativeLayout) root.findViewById(R.id.room);
        room.setVisibility(View.VISIBLE);

        roomMap = root.findViewById(R.id.room_map);
        roomMap.setImage(ImageSource.resource(R.drawable.room));

        movePin(1000);

        return root;
    }

    /**
     * Metoda koja određuje lokaciju za 2 ili 3 Beacon-a.
     *
     * @author Leopold Juraga
     */
    public static Location getLocation(String beaconNumber, Location beaconA, Location beaconB, Location beaconC, double distanceA, double distanceB, double distanceC) {

        double[][] positions = new double[0][];
        double[] distances = new double[0];
        boolean test = true;
        if(beaconNumber.equals("2")){
            positions = new double[][] { { beaconA.getLongitude(), beaconA.getLatitude() }, { beaconB.getLongitude(), beaconB.getLatitude() } };
            distances = new double[] { distanceA, distanceB };
            Log.e(TAG, "positions are: " + beaconA.getLongitude() + "   " + beaconA.getLatitude() + "   " + beaconB.getLongitude() + "   " + beaconB.getLatitude());
            Log.e(TAG, "distances are: " + distanceA + "   " + distanceB);
        }else if (beaconNumber.equals("3")){
            positions = new double[][] { { beaconA.getLongitude(), beaconA.getLatitude() }, { beaconB.getLongitude(), beaconB.getLatitude() }, { beaconC.getLongitude(), beaconC.getLatitude() } };
            distances = new double[] { distanceA, distanceB, distanceC };
            Log.e(TAG, "positions are: " + beaconA.getLongitude() + "   " + beaconA.getLatitude() + "   " + beaconB.getLongitude() + "   " + beaconB.getLatitude() + "   " + beaconC.getLongitude() + "   " + beaconC.getLatitude());
            Log.e(TAG, "distances are: " + distanceA + "   " + distanceB + "   " + distanceC);
        }

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

        double[] centroid = optimum.getPoint().toArray();
        Log.e(TAG, "centroid is " + centroid[0] + "   " + centroid[1]);

        Location userLocation = new Location("UserLocation");
        userLocation.setLongitude(centroid[0]);
        userLocation.setLatitude(centroid[1]);
        Log.e(TAG, "userLocation is " + userLocation.getLongitude() + "   " + userLocation.getLatitude());

        return userLocation;
    }

    /**
     * Metoda koja služi za crtanje pin-a.
     */
    private static void drawPin(Location loc){
        double roomX = RoomData.getRoomX();
        double roomY = RoomData.getRoomY();
        double posX = 0;
        double posY = 0;
        if (loc != null){
            posX = loc.getLongitude();
            posY = loc.getLatitude();
        }
        double roomXpixel = roomX/743;
        double roomYpixel = roomY/743;
        double posXpixel = posX/roomXpixel;
        double posYpixel = posY/roomYpixel;
        PointFx = (int) posXpixel + 23;
        PointFy = (int) posYpixel + 23;
        Log.e(TAG, "roomX is " + roomX + " and roomY is " + roomY);
        Log.e(TAG, "posX is " + posX + " and posX is " + posY);
        Log.e(TAG, "roomXpixel is " + roomXpixel + " and roomYpixel is " + roomYpixel);
        Log.e(TAG, "posXpixel is " + posXpixel + " and posYpixel is " + posYpixel);
        Log.e(TAG, "PointFx is " + PointFx + " and PointFy is " + PointFy);
        if (PointFx >= 23 && PointFx <= 766 && PointFy >= 23 && PointFy <= 766){//ako je u granicama sobe postavi pin
            roomMap.setPin(new PointF(PointFx, PointFy));
        }
    }

    /**
     * Metoda koja služi za pomicanje pin-a.
     */
    private void movePin(final int millisecondsDelay){
        final Handler pinMover = new Handler();

        pinMover.postDelayed(new Runnable(){
            public void run(){
                Location beaconA = new Location("beaconA");
                beaconA.setLongitude(RoomData.getBeacon1x());
                beaconA.setLatitude(RoomData.getBeacon1y());
                Location beaconB = new Location("beaconB");
                beaconB.setLongitude(RoomData.getBeacon2x());
                beaconB.setLatitude(RoomData.getBeacon2y());
                Location beaconC = new Location("beaconC");
                beaconC.setLongitude(RoomData.getBeacon3x());
                beaconC.setLatitude(RoomData.getBeacon3y());
                Location loc = null;
                Log.e(TAG, "is beaconA visible " + BeaconList.isBeaconVisible(RoomData.getBeacon1ID()));
                Log.e(TAG, "is beaconB visible " + BeaconList.isBeaconVisible(RoomData.getBeacon2ID()));
                if (RoomData.getBeaconNumber().equals("3")){
                    Log.e(TAG, "is beaconC visible " + BeaconList.isBeaconVisible(RoomData.getBeacon3ID()));
                }

                try {
                    distanceUnit = RoomData.getDistanceUnit(context);
                } catch (NullPointerException e) {
                    //Error
                }

                if (BeaconList.isBeaconVisible(RoomData.getBeacon1ID()) == true && BeaconList.isBeaconVisible(RoomData.getBeacon2ID()) == true && RoomData.getBeaconNumber().equals("2")){
                    if (String.valueOf(BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon1ID(), distanceUnit)) != null) {
                        RoomData.setBeacon1distance(BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon1ID(), distanceUnit));
                        Log.e(TAG, "beaconA distance is " + BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon1ID(), distanceUnit));
                    }
                    if (String.valueOf(BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon2ID(), distanceUnit)) != null) {
                        RoomData.setBeacon2distance(BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon2ID(), distanceUnit));
                        Log.e(TAG, "beaconB distance is " + BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon2ID(), distanceUnit));
                    }
                    loc = getLocation(RoomData.getBeaconNumber(), beaconA, beaconB, beaconC, RoomData.getBeacon1distance(), RoomData.getBeacon2distance(), RoomData.getBeacon3distance());
                    Log.e(TAG, "beaconA is " + beaconA + " and beaconB is " + beaconB + " and beaconC is " + beaconC);
                    Log.e(TAG, "loc is " + loc);
                    drawPin(loc);
                }
                if (BeaconList.isBeaconVisible(RoomData.getBeacon1ID()) == true && BeaconList.isBeaconVisible(RoomData.getBeacon2ID()) == true && BeaconList.isBeaconVisible(RoomData.getBeacon3ID()) == true && RoomData.getBeaconNumber().equals("3")){
                    if (String.valueOf(BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon1ID(), distanceUnit)) != null) {
                        RoomData.setBeacon1distance(BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon1ID(), distanceUnit));
                        Log.e(TAG, "beaconA distance is " + BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon1ID(), distanceUnit));
                    }
                    if (String.valueOf(BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon2ID(), distanceUnit)) != null) {
                        RoomData.setBeacon2distance(BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon2ID(), distanceUnit));
                        Log.e(TAG, "beaconB distance is " + BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon2ID(), distanceUnit));
                    }
                    if (String.valueOf(BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon3ID(), distanceUnit)) != null){
                        RoomData.setBeacon3distance(BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon3ID(), distanceUnit));
                        Log.e(TAG, "beaconC distance is " + BeaconList.getDistanceOfBeaconByID(RoomData.getBeacon3ID(), distanceUnit));
                    }
                    loc = getLocation(RoomData.getBeaconNumber(), beaconA, beaconB, beaconC, RoomData.getBeacon1distance(), RoomData.getBeacon2distance(), RoomData.getBeacon3distance());
                    Log.e(TAG, "beaconA is " + beaconA + " and beaconB is " + beaconB + " and beaconC is " + beaconC);
                    Log.e(TAG, "loc is " + loc);
                    drawPin(loc);
                }

                if (!stopPinMover) {
                    pinMover.postDelayed(this, millisecondsDelay);
                }
            }
        }, millisecondsDelay);
    }

}