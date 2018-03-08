package ntu.cz3004.mazerunnerremote.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.controlwear.virtual.joystick.android.JoystickView;
import ntu.cz3004.mazerunnerremote.R;

//import static ntu.cz3004.mazerunnerremote.services.BluetoothService.MESSAGE_READ;

/**
 * Created by Aung on 1/28/2018.
 */

public class ControlJoystickFragment extends Fragment implements View.OnClickListener {

    private JoystickView joystickView;

    private boolean shouldMove = false;
    private boolean isMoving = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_joystick, container, false);
        joystickView = view.findViewById(R.id.joystickView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        joystickView.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                shouldMove = strength > 0;
                if(shouldMove) {
                    Log.d("joystick", "move");
                    if(angle >= 45 && angle < 135) {
                        Log.d("joystick", "up");
                    }
                    else if(angle >= 135 && angle < 225) {
                        Log.d("joystick", "left");
                    }
                    else if(angle >= 225 && angle < 315) {
                        Log.d("joystick", "down");
                    }
                    else {
                        Log.d("joystick", "right");
                    }
                    sendCommand();
                }
                else {
                    Log.d("joystick", "stop");
                }



            }
        });
    }

    private void sendCommand(){
        if(shouldMove && !isMoving) {
            isMoving = true;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    Log.d("joystick1", "sendcommand");
                    isMoving = false;
                }
            }, 1000);
        }

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
