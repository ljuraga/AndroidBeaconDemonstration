package hr.unipu.app.androidbeacondemonstration.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.javiersantos.appupdater.enums.Display;

import hr.unipu.app.androidbeacondemonstration.MainActivity;
import hr.unipu.app.androidbeacondemonstration.R;

import static hr.unipu.app.androidbeacondemonstration.ui.room.RoomMapFragment.stopPinMover;

/**
 * Ova klasa služi za metode fragment-a About.
 *
 * @author Leopold Juraga
 * @version 1.0
 */
public class AboutFragment extends Fragment {

    /**
     * Metoda koja se izvršava kod pokretanja fragment-a.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_about, container, false);

        stopPinMover = true;

        Button checkForUpdatesButton = (Button) root.findViewById(R.id.checkForUpdatesButton);
        checkForUpdatesButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.checkForUpdatesFragment(getActivity(), Display.DIALOG);
            }
        });

        return root;
    }
}