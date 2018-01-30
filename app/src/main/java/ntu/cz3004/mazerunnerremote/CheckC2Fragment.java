package ntu.cz3004.mazerunnerremote;

import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import ntu.cz3004.mazerunnerremote.adapters.BluetoothListAdapter;
import ntu.cz3004.mazerunnerremote.managers.BluetoothManager;

import static android.app.Activity.RESULT_OK;
import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.bt;

public class CheckC2Fragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView bluetoothStatusTextView;
    private Switch bluetoothSwitch;

    private LinearLayout bluetoothControlLayout;

    private RecyclerView pairedDeviceRecyclerView;

    private ProgressBar scanningProgressBar;
    private RecyclerView discoveredDevicesRecyclerView;

    private Button searchBluetoothBtn;
    private Button connectBluetoothBtn;

    private BluetoothListAdapter pairedDevicesAdapter;
    private BluetoothListAdapter discoveredDevicesAdapter;

    public CheckC2Fragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        printLog("onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_check_c2, container, false);
        bluetoothStatusTextView = view.findViewById(R.id.bluetoothStatusTextView);
        bluetoothSwitch = view.findViewById(R.id.bluetoothSwitch);
        bluetoothControlLayout = view.findViewById(R.id.bluetoothControlLayout);
        pairedDeviceRecyclerView = view.findViewById(R.id.pairedDeviceRecyclerView);
        scanningProgressBar = view.findViewById(R.id.scanningProgressBar);
        discoveredDevicesRecyclerView = view.findViewById(R.id.discoveredDevicesRecyclerView);
        searchBluetoothBtn = view.findViewById(R.id.searchBluetoothBtn);
        connectBluetoothBtn = view.findViewById(R.id.connectBluetoothBtn);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        printLog("registerReceiver() called");
        getActivity().registerReceiver(mReceiver, filter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        printLog("onViewCreated() called");

        //Paired Devices
        pairedDevicesAdapter = new BluetoothListAdapter(new BluetoothListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BluetoothDevice bluetoothDevice) {
                //TODO: click on paired device
                printLog("attempt to unpair device: " + bluetoothDevice.getName());
                BluetoothManager.unpairDevice(bluetoothDevice);
            }
        });
        pairedDeviceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pairedDeviceRecyclerView.setAdapter(pairedDevicesAdapter);

        //Discovered Devices
        discoveredDevicesAdapter = new BluetoothListAdapter(new BluetoothListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BluetoothDevice bluetoothDevice) {
                if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    BluetoothManager.pairDevice(bluetoothDevice);
                }
            }
        });
        discoveredDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        discoveredDevicesRecyclerView.setAdapter(discoveredDevicesAdapter);

        //initialise switch (in case bluetooth is on when opening app)
        bluetoothSwitch.setOnCheckedChangeListener(this); //set listener first
        searchBluetoothBtn.setOnClickListener(this);
        connectBluetoothBtn.setOnClickListener(this);
        updateUI(BluetoothManager.isBtEnabled());
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            } else {
                Toast.makeText(getContext().getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        switch (compoundButton.getId()) {
            case R.id.bluetoothSwitch:
                BluetoothManager.setBtEnabled(bluetoothSwitch.isChecked(), this);
                break;
        }

    }

    private void updateUI(boolean isEnabled) {
        bluetoothSwitch.setChecked(isEnabled);
        bluetoothStatusTextView.setText(isEnabled ? "Bluetooth (on)" : "Bluetooth (off)");
        bluetoothControlLayout.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        searchBluetoothBtn.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        if (isEnabled) {
            Log.v("Aungg", "isChecked");
            getPairedDevices();
        } else {
            Log.v("Aungg", "isNotChecked");
            pairedDevicesAdapter.clear();
            BluetoothManager.getDefaultBtAdapter().cancelDiscovery();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        printLog("onResume() called");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printLog("onCreate() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        printLog("onDestroy() called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        printLog("onDestroyView() called");
        printLog("unregisterReceiver() called");
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        printLog("onPause() called");
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (action) {
                    case BluetoothDevice.ACTION_FOUND:
                        // Discover new device
                        printLog("ACTION_FOUND: " + device.getName() + " " + device.getAddress());
                        discoveredDevicesAdapter.add(device);
                        break;
                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        printLog("ACTION_BOND_STATE_CHANGED");
                        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                            //paired
                            printLog("device: " + device.getName() + " (PAIRED CALLBACK)");
                            pairedDevicesAdapter.add(device);
                            discoveredDevicesAdapter.update(device);
                        } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                            printLog("device: " + device.getName() + " (PAIRING CALLBACK)");
                            discoveredDevicesAdapter.update(device);
                        } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                            //unpaired
                            printLog("device: " + device.getName() + " (DONE CALLBACK)");
                            pairedDevicesAdapter.remove(device);
                            discoveredDevicesAdapter.update(device);
                        }
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        printLog("ACTION_STATE_CHANGED");
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                                BluetoothAdapter.ERROR);
                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                printLog("STATE_OFF");
                                updateUI(false);
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                printLog("STATE_TURNING_OFF");
                                break;
                            case BluetoothAdapter.STATE_ON:
                                printLog("STATE_ON");
                                updateUI(true);
                                break;
                            case BluetoothAdapter.STATE_TURNING_ON:
                                printLog("STATE_TURNING_ON");
                                break;
                        }
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        printLog("ACTION_DISCOVERY_STARTED");
                        scanningProgressBar.setVisibility(View.VISIBLE);
                        searchBluetoothBtn.setEnabled(false);
                        searchBluetoothBtn.setText("scanning");
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        printLog("ACTION_DISCOVERY_FINISHED");
                        scanningProgressBar.setVisibility(View.GONE);
                        searchBluetoothBtn.setEnabled(true);
                        searchBluetoothBtn.setText("scan devices");
                        discoveredDevicesAdapter.clear();
                        break;

                }
            }
        }
    };

    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = BluetoothManager.getDefaultBtAdapter().getBondedDevices();
        printLog("No. of paired devices: " + String.valueOf(pairedDevices.size()));
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice bluetoothDevice : pairedDevices) {
                printLog("Found paired device: " + bluetoothDevice.getName() + " " + bluetoothDevice.getAddress());
                pairedDevicesAdapter.add(bluetoothDevice);
            }
        } else {
            //TODO: GUI to show that there is no paired devices.
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.searchBluetoothBtn:
                try {
                    printLog("startDiscovery() called");
                    if (BluetoothAdapter.getDefaultAdapter().startDiscovery()) {
                        printLog("startDiscovery() success");
                    } else {
                        printLog("startDiscovery() failed");
                    }
                } catch (Exception e) {
                    printLog("Error: " + e.getMessage());
                }
                break;
            case R.id.connectBluetoothBtn:
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getContext().getApplicationContext(), ConnectBluetoothActivity.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
        }
    }

    private void printLog(String message) {
        Log.d("debug_c2", message);
    }
}
