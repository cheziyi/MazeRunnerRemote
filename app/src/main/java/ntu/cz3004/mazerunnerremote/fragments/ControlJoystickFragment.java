package ntu.cz3004.mazerunnerremote.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import io.github.controlwear.virtual.joystick.android.JoystickView;
import ntu.cz3004.mazerunnerremote.R;
import ntu.cz3004.mazerunnerremote.dto.Command;
import ntu.cz3004.mazerunnerremote.dto.Response;

import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.SendCommand;
import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.bt;

//import static ntu.cz3004.mazerunnerremote.services.BluetoothService.MESSAGE_READ;

/**
 * Created by Aung on 1/28/2018.
 */

public class ControlJoystickFragment extends MainFragment implements View.OnClickListener {

    private JoystickView joystickView;

    private boolean shouldMove = false;
    private boolean isMoving = false;

    private int botDirection = 0;


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
                shouldMove = strength > 30;
                if(shouldMove) {
                    if(angle >= 45 && angle < 135) {
                        sendCommand(0);
                    }
                    else if(angle >= 135 && angle < 225) {
                        sendCommand(270);
                    }
                    else if(angle >= 225 && angle < 315) {
                        sendCommand(180);
                    }
                    else {
                        sendCommand(90);
                    }
                }
                else {
                }
            }
        });
    }

    private void sendCommand(final int direction){
        if(shouldMove && !isMoving) {
            isMoving = true;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(botDirection == direction) {
                        SendCommand(new Command(Command.CommandTypes.FORWARD));
                    }
                    else {
                        if((botDirection - direction > 0 && botDirection - direction != 270) || botDirection - direction == -270) {
                            SendCommand(new Command(Command.CommandTypes.ROTATE_LEFT));
                        }
                        else{
                            SendCommand(new Command(Command.CommandTypes.ROTATE_RIGHT));
                        }
                    }
                    isMoving = false;
                }
            }, 300);
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
    int getNavigationMenuItemId() {
        return R.id.nav_check_c8;
    }

    @Override
    public void onBtDataReceived(byte[] data, String message) {
        Response resp = new Gson().fromJson(message, Response.class);
        Response.RobotPosition robotPosition = resp.getRobotPosition();
        if (robotPosition != null) {
            botDirection = robotPosition.getDirection();
        }
    }

    @Override
    public void onClick(View view) {
    }
}
