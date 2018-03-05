package ntu.cz3004.mazerunnerremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import ntu.cz3004.mazerunnerremote.managers.BluetoothManager;

import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.bt;

/**
 * Created by Aung on 1/30/2018.
 */

public class ConnectBluetoothActivity extends DeviceList implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private TextView bluetoothStatusTextView;
    private Switch bluetoothSwitch;
    private ProgressBar scanningProgressBar;
    private LinearLayout deviceListLayout;
    private Button scanButton;
    private Button disconnectButton;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothDevice.ACTION_FOUND:
                        // Discover new device
                        break;
                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                                BluetoothAdapter.ERROR);
                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                updateUI(false);
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                break;
                            case BluetoothAdapter.STATE_ON:
                                updateUI(true);
                                break;
                            case BluetoothAdapter.STATE_TURNING_ON:
                                break;
                        }
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        scanningProgressBar.setVisibility(View.VISIBLE);
                        scanButton.setEnabled(false);
                        scanButton.setText("scanning");
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        scanningProgressBar.setVisibility(View.GONE);
                        scanButton.setEnabled(true);
                        scanButton.setText("scan devices");
                        break;

                }
            }
        }
    };

    private void updateUI(boolean isBluetoothOn) {
        bluetoothSwitch.setChecked(isBluetoothOn);
        deviceListLayout.setVisibility(isBluetoothOn ? View.VISIBLE : View.GONE);

        boolean isConnected = bt.getServiceState() == BluetoothState.STATE_CONNECTED;
        scanButton.setEnabled(!isConnected);
        scanButton.setText(isConnected ? "Connected to " + bt.getConnectedDeviceName() + " (" + bt.getConnectedDeviceAddress() + ")" : "scan devices");
        disconnectButton.setVisibility(isConnected ? View.VISIBLE : View.GONE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //find views
        bluetoothStatusTextView = findViewById(R.id.bluetoothStatusTextView);
        bluetoothSwitch = findViewById(R.id.bluetoothSwitch);
        scanningProgressBar = findViewById(R.id.scanningProgressBar);
        deviceListLayout = findViewById(R.id.deviceListLayout);
        scanButton = findViewById(R.id.button_scan);
        disconnectButton = findViewById(R.id.disconnectButton);
        bluetoothSwitch.setOnCheckedChangeListener(this);
        disconnectButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(broadcastReceiver, filter);
        updateUI(bt.isBluetoothEnabled());
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.bluetoothSwitch:
                BluetoothManager.setBtEnabled(isChecked, this);
                bluetoothStatusTextView.setText(isChecked ? "Bluetooth (on)" : "Bluetooth (off)");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.disconnectButton:
                BluetoothManager.ManualDc = true;
                bt.disconnect();
                updateUI(bt.isBluetoothEnabled());
                break;
        }
    }
}
