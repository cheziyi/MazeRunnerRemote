package ntu.cz3004.mazerunnerremote.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import ntu.cz3004.mazerunnerremote.R;

//import static ntu.cz3004.mazerunnerremote.services.BluetoothService.MESSAGE_READ;

/**
 * Created by Aung on 1/28/2018.
 */

public class CheckC3Fragment extends MainFragment implements View.OnClickListener {

    private FrameLayout container;
    private BottomNavigationView navigation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_c3, container, false);
        container = view.findViewById(R.id.container);
        navigation = view.findViewById(R.id.navigation);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.container, new ControlButtonFragment());
        ft.commit();

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.menu_button:
                        ft.replace(R.id.container, new ControlButtonFragment());
                        ft.commit();
                        return true;
                    case R.id.menu_joystick:
                        ft.replace(R.id.container, new ControlJoystickFragment());
                        ft.commit();
                        return true;
                    case R.id.menu_touch:
                        ft.replace(R.id.container, new ControlTouchFragment());
                        ft.commit();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.nav_check_c3;
    }

    @Override
    public void onBtDataReceived(byte[] data, String message) {

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
    }
}
