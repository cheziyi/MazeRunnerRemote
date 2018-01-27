package ntu.cz3004.mazerunnerremote.wrappers;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

/**
 * Created by Aung on 1/27/2018.
 */

public class BluetoothDeviceWrapper {

    private BluetoothDevice bluetoothDevice;

    public BluetoothDeviceWrapper(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    @Override
    public String toString() {
        if(bluetoothDevice == null){
            return "no selection";
        }
        if(TextUtils.isEmpty(bluetoothDevice.getName())){
            return this.bluetoothDevice.getAddress();
        }
        return this.bluetoothDevice.getName();
    }
}
