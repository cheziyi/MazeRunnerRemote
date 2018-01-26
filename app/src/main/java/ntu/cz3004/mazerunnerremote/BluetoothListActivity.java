package ntu.cz3004.mazerunnerremote;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;


import ntu.cz3004.mazerunnerremote.adapters.AppBluetoothAdapter;
import ntu.cz3004.mazerunnerremote.managers.BluetoothManager;

public class BluetoothListActivity extends AppCompatActivity {

    private RecyclerView bluetoothListRecyclerView;
    private AppBluetoothAdapter appBluetoothAdapter;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Discover new device
                appBluetoothAdapter.add(device);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_list);

        appBluetoothAdapter = new AppBluetoothAdapter(new AppBluetoothAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BluetoothDevice bluetoothDevice) {
                Toast.makeText(getApplicationContext(), bluetoothDevice.getName() + bluetoothDevice.getAddress(), Toast.LENGTH_SHORT).show();

            }
        });
        bluetoothListRecyclerView = findViewById(R.id.bluetoothListRecyclerView);
        bluetoothListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bluetoothListRecyclerView.setAdapter(appBluetoothAdapter);

        try {
            BluetoothManager.startScanning(this, mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        BluetoothManager.stopScanning(this, mReceiver);
    }


}
