package ntu.cz3004.mazerunnerremote.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import ntu.cz3004.mazerunnerremote.R;
import ntu.cz3004.mazerunnerremote.dto.Command;
import ntu.cz3004.mazerunnerremote.dto.Response;
import ntu.cz3004.mazerunnerremote.engines.BotEngine;

import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.SendCommand;
import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.bt;

//import static ntu.cz3004.mazerunnerremote.services.BluetoothService.MESSAGE_READ;

/**
 * Created by Aung on 1/28/2018.
 */

public class CheckC3Fragment extends MainFragment implements View.OnClickListener {

    private TextView deviceStatusTextView;
    private ImageButton forwardBtn;
    private ImageButton rotateLeftBtn;
    private ImageButton rotateRightBtn;
    private ImageButton reverseBtn;
    private ImageButton strafeRightBtn;
    private ImageButton strafeLeftBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_c3, container, false);
        deviceStatusTextView = view.findViewById(R.id.deviceStatusTextView);
        forwardBtn = view.findViewById(R.id.imageButtonForward);
        rotateLeftBtn = view.findViewById(R.id.imageButtonRotateLeft);
        rotateRightBtn = view.findViewById(R.id.imageButtonRotateRight);
        reverseBtn = view.findViewById(R.id.imageButtonReverse);
        strafeRightBtn = view.findViewById(R.id.imageButtonStrafeRight);
        strafeLeftBtn = view.findViewById(R.id.imageButtonStrafeLeft);

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Response resp = new Gson().fromJson(message, Response.class);
                if(resp.getStatus() != null) {
                    deviceStatusTextView.setText(resp.getStatus());
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        forwardBtn.setOnClickListener(this);
        strafeLeftBtn.setOnClickListener(this);
        strafeRightBtn.setOnClickListener(this);
        reverseBtn.setOnClickListener(this);
        rotateLeftBtn.setOnClickListener(this);
        rotateRightBtn.setOnClickListener(this);
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.nav_check_c3;
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
        String message = null;
        switch (view.getId()) {
            case R.id.imageButtonForward:
                SendCommand(new Command(Command.CommandTypes.FORWARD));
                break;
            case R.id.imageButtonRotateLeft:
                SendCommand(new Command(Command.CommandTypes.ROTATE_LEFT));
                break;
            case R.id.imageButtonRotateRight:
                SendCommand(new Command(Command.CommandTypes.ROTATE_RIGHT));
                break;
            case R.id.imageButtonReverse:
                SendCommand(new Command(Command.CommandTypes.REVERSE));
                break;
            case R.id.imageButtonStrafeLeft:
                SendCommand(new Command(Command.CommandTypes.STRAFE_LEFT));
                break;
            case R.id.imageButtonStrafeRight:
                SendCommand(new Command(Command.CommandTypes.STRAFE_RIGHT));
                break;
        }
    }
}
