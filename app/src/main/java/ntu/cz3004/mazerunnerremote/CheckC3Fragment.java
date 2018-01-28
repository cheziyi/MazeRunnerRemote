package ntu.cz3004.mazerunnerremote;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

import ntu.cz3004.mazerunnerremote.dto.AckResponse;
import ntu.cz3004.mazerunnerremote.dto.AckType;
import ntu.cz3004.mazerunnerremote.dto.Direction;
import ntu.cz3004.mazerunnerremote.dto.MoveCommand;
import ntu.cz3004.mazerunnerremote.dto.Response;
import ntu.cz3004.mazerunnerremote.engines.BotEngine;
import ntu.cz3004.mazerunnerremote.services.BluetoothService;

import static ntu.cz3004.mazerunnerremote.services.BluetoothService.MESSAGE_READ;

/**
 * Created by Aung on 1/28/2018.
 */

public class CheckC3Fragment extends Fragment implements View.OnClickListener {

    private TextView deviceStatusTextView;
    private BotEngine botEngine;
    private Button upBtn;
    private Button leftBtn;
    private Button rightBtn;
    private Button downBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_c3, container, false);
        deviceStatusTextView = view.findViewById(R.id.deviceStatusTextView);
        botEngine = view.findViewById(R.id.botEngine);
        upBtn = view.findViewById(R.id.upBtn);
        leftBtn = view.findViewById(R.id.leftBtn);
        rightBtn = view.findViewById(R.id.rightBtn);
        downBtn = view.findViewById(R.id.downBtn);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        upBtn.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        downBtn.setOnClickListener(this);
        botEngine.start();

        BluetoothService.setMessageHandler(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) message.obj;
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, message.arg1);
                        deviceStatusTextView.setText(readMessage);
                        return true;
                }
                return false;
            }
        }));
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
        String message = null;
        switch (view.getId()) {
            case R.id.upBtn:
                botEngine.setHeading(BotEngine.Heading.UP);
                message = new GsonBuilder().create().toJson(new MoveCommand(Direction.UP));
                break;
            case R.id.leftBtn:
                botEngine.setHeading(BotEngine.Heading.LEFT);
                message = new GsonBuilder().create().toJson(new MoveCommand(Direction.LEFT));
                break;
            case R.id.rightBtn:
                botEngine.setHeading(BotEngine.Heading.RIGHT);
                message = new GsonBuilder().create().toJson(new MoveCommand(Direction.RIGHT));
                break;
            case R.id.downBtn:
                botEngine.setHeading(BotEngine.Heading.DOWN);
                message = new GsonBuilder().create().toJson(new MoveCommand(Direction.DOWN));
                break;
        }
        String response = BluetoothService.sendRequest(message);
        AckResponse resp = new Gson().fromJson(response, AckResponse.class);
        if (resp.geAckType() == AckType.OK)
            botEngine.setUpdated(false);
    }
}
