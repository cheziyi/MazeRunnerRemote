package ntu.cz3004.mazerunnerremote.view_holders;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ntu.cz3004.mazerunnerremote.R;
import ntu.cz3004.mazerunnerremote.adapters.AppBluetoothAdapter;

/**
 * Created by Aung on 1/26/2018.
 */

public class BluetoothViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private LinearLayout layoutContainer;
    private TextView btName;

    private BluetoothDevice bluetoothDevice;
    private AppBluetoothAdapter.OnItemClickListener onItemClickListener;

    public BluetoothViewHolder(View itemView) {
        super(itemView);
        layoutContainer = itemView.findViewById(R.id.layoutContainer);
        btName = itemView.findViewById(R.id.btName);
    }

    public void bind(BluetoothDevice bluetoothDevice, final AppBluetoothAdapter.OnItemClickListener onItemClickListener) {
        this.bluetoothDevice = bluetoothDevice;
        this.onItemClickListener = onItemClickListener;
        String deviceStatus = "";
        switch (bluetoothDevice.getBondState()){
            case BluetoothDevice.BOND_BONDED:
                deviceStatus = "(paired)";
                break;
            case BluetoothDevice.BOND_BONDING:
                deviceStatus = "(pairing)";
                break;
            case BluetoothDevice.BOND_NONE:
                deviceStatus = "(unpaired)";
                break;
        }
        if(TextUtils.isEmpty(bluetoothDevice.getName())){
            btName.setText(bluetoothDevice.getAddress() + " " + deviceStatus);
        }
        else{
            btName.setText(bluetoothDevice.getName() + " " + deviceStatus);
            layoutContainer.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layoutContainer:
                if(onItemClickListener != null && bluetoothDevice != null){
                    onItemClickListener.onItemClick(bluetoothDevice);
                }
                break;
        }
    }
}
