package ntu.cz3004.mazerunnerremote.fragments;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ntu.cz3004.mazerunnerremote.R;
import ntu.cz3004.mazerunnerremote.dto.Command;
import ntu.cz3004.mazerunnerremote.engines.BotEngine;
import ntu.cz3004.mazerunnerremote.managers.BluetoothManager;

import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.bt;

/**
 * Created by Aung on 1/28/2018.
 */

public class CheckC567Fragment extends MainFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //Checklist C7 components
    private TextView updateModeTextVuew;
    private Switch updateModeSwitch;
    private Button updateButton;

    private Button btnExplore;
    private Button btnRace;
    private ImageButton btnRotateACW;
    private ImageButton btnRotateCW;

    //Canvas view
    private BotEngine botEngine;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_c567, container, false);
        updateModeTextVuew = view.findViewById(R.id.updateModeTextVuew);
        updateModeSwitch = view.findViewById(R.id.updateModeSwitch);
        updateButton = view.findViewById(R.id.updateButton);
        botEngine = view.findViewById(R.id.botEngine);

        btnExplore = view.findViewById(R.id.btnExplore);
        btnRace = view.findViewById(R.id.btnRace);
        btnRotateACW = view.findViewById(R.id.btnRotateACW);
        btnRotateCW = view.findViewById(R.id.btnRotateCW);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateModeSwitch.setOnCheckedChangeListener(this);
        updateButton.setOnClickListener(this);
        btnExplore.setOnClickListener(this);
        btnRace.setOnClickListener(this);
        btnRotateACW.setOnClickListener(this);
        btnRotateCW.setOnClickListener(this);
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.nav_check_c567;
    }

    @Override
    public void onPause() {
        super.onPause();
        botEngine.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        botEngine.resume();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.updateButton:
                BluetoothManager.SendCommand(new Command(Command.CommandTypes.SEND_MAP));
                break;
            case R.id.btnExplore:
                Command exploreCommand = new Command(Command.CommandTypes.BEGIN_EXPLORE);
                exploreCommand.setLocation(botEngine.getBotX(), botEngine.getBotY());
                exploreCommand.setDirection(botEngine.getHeadingInDegree());
                BluetoothManager.SendCommand(exploreCommand);
                setInteraction(false);
                updateModeSwitch.setChecked(true);
                break;
            case R.id.btnRace:
                Command raceCommand = new Command(Command.CommandTypes.BEGIN_FASTEST_PATH);
                Point wayPoint = botEngine.getWayPoint();
                if(wayPoint != null) {
                    raceCommand.setLocation(wayPoint.x, wayPoint.y);
                    BluetoothManager.SendCommand(raceCommand);
                    setInteraction(false);
                    updateModeSwitch.setChecked(true);
                }
                else {
                    Toast.makeText(getActivity(), "Please set waypoint", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnRotateACW:
                botEngine.rotateBot(false);
                break;
            case R.id.btnRotateCW:
                botEngine.rotateBot(true);
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()){
            case R.id.updateModeSwitch:
                changeUpdateMode(isChecked);
                BluetoothManager.SendCommand(new Command(isChecked ? Command.CommandTypes.AUTO_START : Command.CommandTypes.AUTO_STOP));
                break;
        }
    }

    private void changeUpdateMode(boolean isAutoMode) {
        updateButton.setEnabled(!isAutoMode);
        updateModeTextVuew.setText(isAutoMode ? "Update Mode (auto)" : "Update Mode (manual)");
        botEngine.setAutoUpdating(isAutoMode);
    }

    public void setInteraction(boolean isEnable) {
        botEngine.setAllowInteration(isEnable);
        btnRace.setEnabled(isEnable);
        btnExplore.setEnabled(isEnable);
        btnRotateACW.setEnabled(isEnable);
        btnRotateCW.setEnabled(isEnable);
    }

}
