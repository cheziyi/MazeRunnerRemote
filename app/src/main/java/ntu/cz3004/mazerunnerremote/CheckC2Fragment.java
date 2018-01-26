package ntu.cz3004.mazerunnerremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Set;

import ntu.cz3004.mazerunnerremote.adapters.AppBluetoothAdapter;
import ntu.cz3004.mazerunnerremote.managers.BluetoothManager;

import static android.app.Activity.RESULT_OK;

public class CheckC2Fragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView bluetoothStatusTextView;
    private Switch bluetoothSwitch;

    private LinearLayout bluetoothControlLayout;

    private RecyclerView pairedDeviceRecyclerView;
    private RecyclerView discoveredDevicesRecyclerView;

    private Button searchBluetoothBtn;

    private AppBluetoothAdapter pairedDevicesAdapter;
    private AppBluetoothAdapter discoveredDevicesAdapter;

    public CheckC2Fragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_c2, container, false);
        bluetoothStatusTextView = view.findViewById(R.id.bluetoothStatusTextView);
        bluetoothSwitch = view.findViewById(R.id.bluetoothSwitch);
        bluetoothControlLayout = view.findViewById(R.id.bluetoothControlLayout);
        pairedDeviceRecyclerView = view.findViewById(R.id.pairedDeviceRecyclerView);
        discoveredDevicesRecyclerView = view.findViewById(R.id.discoveredDevicesRecyclerView);
        searchBluetoothBtn = view.findViewById(R.id.searchBluetoothBtn);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Paired Devices
        pairedDevicesAdapter = new AppBluetoothAdapter(new AppBluetoothAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BluetoothDevice bluetoothDevice) {
                //TODO: click on paired device
                BluetoothManager.unpairDevice(bluetoothDevice);
            }
        });
        pairedDeviceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pairedDeviceRecyclerView.setAdapter(pairedDevicesAdapter);

        //Discovered Devices
        discoveredDevicesAdapter = new AppBluetoothAdapter(new AppBluetoothAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BluetoothDevice bluetoothDevice) {
                //TODO: click on discovered device.
                BluetoothManager.pairDevice(bluetoothDevice);
            }
        });
        discoveredDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        discoveredDevicesRecyclerView.setAdapter(discoveredDevicesAdapter);

        //initialise switch (in case bluetooth is on when opening app)
        bluetoothSwitch.setOnCheckedChangeListener(this); //set listener first
        bluetoothSwitch.setChecked(BluetoothManager.isBtEnabled());

        searchBluetoothBtn.setOnClickListener(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BluetoothManager.REQUEST_ENABLE_BT)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(getActivity(), "bluetooth enabled", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getActivity(), "Failed to enable bluetooth", Toast.LENGTH_LONG).show();
                bluetoothSwitch.setChecked(false);
            }
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        switch (compoundButton.getId()){
            case R.id.bluetoothSwitch:
                BluetoothManager.setBtEnabled(bluetoothSwitch.isChecked(), this);
                bluetoothStatusTextView.setText(bluetoothSwitch.isChecked() ? "Bluetooth (on)" : "Bluetooth (off)");
                bluetoothControlLayout.setVisibility(bluetoothSwitch.isChecked() ? View.VISIBLE : View.GONE);
                searchBluetoothBtn.setVisibility(bluetoothSwitch.isChecked() ? View.VISIBLE : View.GONE);
                if(bluetoothSwitch.isChecked()){
                    Log.v("Aungg", "isChecked");
                    findPairedDevices();
                }
                else{
                    Log.v("Aungg", "isNotChecked");
                    pairedDevicesAdapter.clear();
                    discoveredDevicesAdapter.clear();
                    BluetoothManager.stopScanning(getActivity(), mReceiver);
                }
                break;
        }

    }

    private void findPairedDevices() {

        //Start registering
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);


        Set<BluetoothDevice> pairedDevices = BluetoothManager.getDefaultBtAdapter().getBondedDevices();
        Log.v("Aungg", String.valueOf(pairedDevices.size()));
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device);
            }
        }
        else{
            //TODO: GUI to show that there is no paired devices.
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (action){
                    case BluetoothDevice.ACTION_FOUND:
                        // Discover new device
                        discoveredDevicesAdapter.add(device);
                        break;
                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        Toast.makeText(getActivity(), "state changed", Toast.LENGTH_SHORT).show();
                        if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                            //paired
                            Toast.makeText(getActivity(), "paired", Toast.LENGTH_SHORT).show();
                            pairedDevicesAdapter.add(device);
                        }
                        else if(device.getBondState() == BluetoothDevice.BOND_NONE){
                            //unpaired
                            Toast.makeText(getActivity(), "unpaired", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        Log.v("Aungg", "state changed");
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                                BluetoothAdapter.ERROR);
                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                break;
                            case BluetoothAdapter.STATE_ON:
                                break;
                            case BluetoothAdapter.STATE_TURNING_ON:
                                break;
                        }
                        break;

                }
            }
        }
    };

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.searchBluetoothBtn:
                /*Intent myIntent = new Intent(getActivity(), BluetoothListActivity.class);
                getActivity().startActivity(myIntent);*/
                try {
                    BluetoothManager.startScanning(getActivity(), mReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BluetoothManager.stopScanning(getActivity(), mReceiver);
    }
}
