package ntu.cz3004.mazerunnerremote.managers;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * Created by Aung on 1/26/2018.
 */

public class BluetoothManager {

    public static int REQUEST_ENABLE_BT = 99;

    public static BluetoothAdapter getDefaultBtAdapter(){
        return BluetoothAdapter.getDefaultAdapter();
    }

    public static boolean isBtEnabled(){
        return getDefaultBtAdapter().isEnabled();
    }

    public static void setBtEnabled(boolean setEnabled, Fragment fragment)
    {
        if(setEnabled){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            fragment.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else{
            if(getDefaultBtAdapter().disable()){
                Toast.makeText(fragment.getActivity(), "bluetooth disabled", Toast.LENGTH_LONG).show();
            }
            else{
                //TODO: fail to disable BT
            }
        }
    }

    public static void startScanning(Activity activity, BroadcastReceiver mReceiver) throws Exception {
        if(!isBtEnabled()){
            throw new Exception("Bluetooth is not enabled.");
        }
        else{
            if(BluetoothAdapter.getDefaultAdapter().startDiscovery()){
                Toast.makeText(activity, "starting", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show();
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            activity.registerReceiver(mReceiver, filter);
        }
    }

    public static void stopScanning(Activity activity, BroadcastReceiver mReceiver){
        try{
            activity.unregisterReceiver(mReceiver);
            getDefaultBtAdapter().cancelDiscovery();
        } catch (Exception e){
            // already unregistered
        }
    }

    public static void pairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.v("Aung", e.getMessage());
        }
    }
    public static void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.v("Aung", e.getMessage());
        }
    }

}
