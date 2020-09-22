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
     * Metoda koja određuje lokaciju prema centru gravitacije za 2 ili 3 Beacon-a.
     * Preuzeto sa: https://stackoverflow.com/questions/20332856/triangulate-example-for-ibeacons/23382723.
     *
     * @author Vishnu Prabhu i Leopold Juraga
     */
    public static Location getLocationWithCenterOfGravity(String beaconNumber, Location beaconA, Location beaconB, Location beaconC, double distanceA, double distanceB, double distanceC) {

        //Every meter there are approx 4.5 points
        double METERS_IN_COORDINATE_UNITS_RATIO = 4.5;

        //http://stackoverflow.com/a/524770/663941
        //Find Center of Gravity
        double cogX = 0;
        double cogY = 0;
        if(beaconNumber.equals("2")){
            cogX = (beaconA.getLatitude() + beaconB.getLatitude()) / 2;
            cogY = (beaconA.getLongitude() + beaconB.getLongitude()) / 2;
        }else if (beaconNumber.equals("3")){
            cogX = (beaconA.getLatitude() + beaconB.getLatitude() + beaconC.getLatitude()) / 3;
            cogY = (beaconA.getLongitude() + beaconB.getLongitude() + beaconC.getLongitude()) / 3;
        }
        Location cog = new Location("Cog");
        cog.setLatitude(cogX);
        cog.setLongitude(cogY);


        //Nearest Beacon
        Location nearestBeacon = null;
        double shortestDistanceInMeters = 0;
        if(beaconNumber.equals("2")){
            if (distanceA < distanceB) {
                nearestBeacon = beaconA;
                shortestDistanceInMeters = distanceA;
            } else {
                nearestBeacon = beaconB;
                shortestDistanceInMeters = distanceB;
            }
        }else if (beaconNumber.equals("3")){
            if (distanceA < distanceB && distanceA < distanceC) {
                nearestBeacon = beaconA;
                shortestDistanceInMeters = distanceA;
            } else if (distanceB < distanceC) {
                nearestBeacon = beaconB;
                shortestDistanceInMeters = distanceB;
            } else {
                nearestBeacon = beaconC;
                shortestDistanceInMeters = distanceC;
            }
        }

        //http://www.mathplanet.com/education/algebra-2/conic-sections/distance-between-two-points-and-the-midpoint
        //Distance between nearest beacon and COG
        double distanceToCog = Math.sqrt(Math.pow(cog.getLatitude() - nearestBeacon.getLatitude(),2)
                + Math.pow(cog.getLongitude() - nearestBeacon.getLongitude(),2));

        //Convert shortest distance in meters into coordinates units.
        double shortestDistanceInCoordinationUnits = shortestDistanceInMeters * METERS_IN_COORDINATE_UNITS_RATIO;

        //http://math.stackexchange.com/questions/46527/coordinates-of-point-on-a-line-defined-by-two-other-points-with-a-known-distance?rq=1
        //On the line between Nearest Beacon and COG find shortestDistance point apart from Nearest Beacon

        double t = shortestDistanceInCoordinationUnits/distanceToCog;

        Location pointsDiff = new Location("PointsDiff");
        pointsDiff.setLatitude(cog.getLatitude() - nearestBeacon.getLatitude());
        pointsDiff.setLongitude(cog.getLongitude() - nearestBeacon.getLongitude());

        Location tTimesDiff = new Location("tTimesDiff");
        tTimesDiff.setLatitude(pointsDiff.getLatitude() * t);
        tTimesDiff.setLongitude(pointsDiff.getLongitude() * t);

        //Add t times diff with nearestBeacon to find coordinates at a distance from nearest beacon in line to COG.

        Location userLocation = new Location("UserLocation");
        userLocation.setLatitude(nearestBeacon.getLatitude() + tTimesDiff.getLatitude());
        userLocation.setLongitude(nearestBeacon.getLongitude() + tTimesDiff.getLongitude());

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
                Log.e(TAG, "is beaconC visible " + BeaconList.isBeaconVisible(RoomData.getBeacon3ID()));

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
                    loc = getLocationWithCenterOfGravity(RoomData.getBeaconNumber(), beaconA, beaconB, beaconC, RoomData.getBeacon1distance(), RoomData.getBeacon2distance(), RoomData.getBeacon3distance());
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
                    loc = getLocationWithCenterOfGravity(RoomData.getBeaconNumber(), beaconA, beaconB, beaconC, RoomData.getBeacon1distance(), RoomData.getBeacon2distance(), RoomData.getBeacon3distance());
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