package ntu.cz3004.mazerunnerremote.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import ntu.cz3004.mazerunnerremote.R;
import ntu.cz3004.mazerunnerremote.customlistener.OnSwipeListener;
import ntu.cz3004.mazerunnerremote.dto.Command;
import ntu.cz3004.mazerunnerremote.dto.Response;

import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.SendCommand;

//import static ntu.cz3004.mazerunnerremote.services.BluetoothService.MESSAGE_READ;

/**
 * Created by Aung on 1/28/2018.
 */

public class ControlTouchFragment extends Fragment implements View.OnTouchListener {

    private LinearLayout touchAreaLayout;
    private GestureDetector gestureDetector = new GestureDetector(getActivity(), new OnSwipeListener(){
        @Override
        public boolean onSwipe(Direction direction) {
            switch (direction) {
                case up:
                    SendCommand(new Command(Command.CommandTypes.FORWARD));
                    break;
                case right:
                    SendCommand(new Command(Command.CommandTypes.ROTATE_RIGHT));
                    break;
                case down:
                    SendCommand(new Command(Command.CommandTypes.REVERSE));
                    break;
                case left:
                    SendCommand(new Command(Command.CommandTypes.ROTATE_LEFT));
                    break;
            }
            return super.onSwipe(direction);
        }
    });

    private int botDirection = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_touch, container, false);
        touchAreaLayout = view.findViewById(R.id.touchAreaLayout);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        touchAreaLayout.setOnTouchListener(this);
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
    public boolean onTouch(View view, MotionEvent motionEvent) {
        gestureDetector.onTouchEvent(motionEvent);
        return true;
    }
}
