package ntu.cz3004.mazerunnerremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ntu.cz3004.mazerunnerremote.adapters.MessageListAdapter;
import ntu.cz3004.mazerunnerremote.managers.BluetoothManager;
import ntu.cz3004.mazerunnerremote.services.BluetoothService;
import ntu.cz3004.mazerunnerremote.wrappers.BluetoothDeviceWrapper;

import static ntu.cz3004.mazerunnerremote.services.BluetoothService.MESSAGE_DEVICE_NAME;
import static ntu.cz3004.mazerunnerremote.services.BluetoothService.MESSAGE_READ;
import static ntu.cz3004.mazerunnerremote.services.BluetoothService.MESSAGE_TOAST;
import static ntu.cz3004.mazerunnerremote.services.BluetoothService.MESSAGE_WRITE;
import static ntu.cz3004.mazerunnerremote.services.BluetoothService.STATE_CONNECTED;
import static ntu.cz3004.mazerunnerremote.services.BluetoothService.STATE_CONNECTING;
import static ntu.cz3004.mazerunnerremote.services.BluetoothService.STATE_LISTEN;
import static ntu.cz3004.mazerunnerremote.services.BluetoothService.STATE_NONE;

/**
 * Created by Aung on 1/27/2018.
 */

public class CheckC1Fragment extends Fragment implements View.OnClickListener {

    private Spinner deviceSpinner;
    private Button connectBtn;
    private int selectedIndex = 0;
    private EditText messageEditText;
    private Button sendBtn;

    private List<BluetoothDeviceWrapper> deviceList;

    BluetoothDevice selectedBluetoothDevice = null;

    private MessageListAdapter messageListAdapter;
    private RecyclerView messageRecyclerView;

    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) message.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    messageEditText.setText("");
                    messageListAdapter.add("[Android]: " + writeMessage);
                    return true;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) message.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, message.arg1);
                    messageListAdapter.add("[PC]: " + readMessage);
                    return true;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    return true;
                case MESSAGE_TOAST:
                    return true;
            }
            return false;
        }
    });

    private final Handler connectionStateChangedHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case STATE_CONNECTING:
                    connectBtn.setEnabled(false);
                    connectBtn.setText("connecting");
                    return true;
                case STATE_CONNECTED:
                    connectBtn.setEnabled(false);
                    connectBtn.setText("connected");
                    return true;
                case STATE_NONE:
                case STATE_LISTEN:
                    connectBtn.setEnabled(true);
                    connectBtn.setText("connect");
                    messageListAdapter.clear();
                    return true;
            }
            return false;
        }
    });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_c1, container, false);
        deviceSpinner = view.findViewById(R.id.deviceSpinner);
        connectBtn = view.findViewById(R.id.connectBtn);
        messageRecyclerView = view.findViewById(R.id.messageRecyclerView);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendBtn = view.findViewById(R.id.sendBtn);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        connectBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

        deviceList = new ArrayList<>();
        deviceList.add(new BluetoothDeviceWrapper(null));

        //get all paired devices
        Set<BluetoothDevice> pairedDevices = BluetoothManager.getDefaultBtAdapter().getBondedDevices();
        for(BluetoothDevice device : pairedDevices){
            deviceList.add(new BluetoothDeviceWrapper(device));
        }

        ArrayAdapter<BluetoothDeviceWrapper> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, deviceList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceSpinner.setAdapter(arrayAdapter);
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(selectedIndex != i || i == 0){
                    BluetoothService.stop();
                }
                selectedIndex = i;
                selectedBluetoothDevice = (selectedIndex == 0) ? null : deviceList.get(selectedIndex).getBluetoothDevice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        BluetoothService.setConnectionStateChangedHandler(connectionStateChangedHandler);
        BluetoothService.setMessageHandler(mHandler);

        //Paired Devices
        messageListAdapter = new MessageListAdapter();
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageRecyclerView.setAdapter(messageListAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_c8, menu);
    }

    private void sendMessage(String message) {

        // Check that we're actually connected before trying anything
        if (BluetoothService.getState() != STATE_CONNECTED) {
            printLog(deviceList.get(selectedIndex).getBluetoothDevice().getName() + " is not connected" + BluetoothService.getState());
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothService to write
            BluetoothService.sendRequest(message);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sendBtn:
                if(selectedIndex == 0){
                    Toast.makeText(getActivity(), "Please select device.", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendMessage(messageEditText.getText().toString());
                }

                break;
            case R.id.connectBtn:
                if(selectedBluetoothDevice != null){
                    BluetoothService.connect(selectedBluetoothDevice);
                }
                break;
        }
    }

    private void printLog(String message){
        Log.d("debug_c1", message);
    }
}
