package ntu.cz3004.mazerunnerremote.managers;

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
            if(isBtEnabled()){
                Toast.makeText(fragment.getActivity(), "Already enabled", Toast.LENGTH_SHORT).show();
            }
            else{
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                fragment.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        else{
            if(!isBtEnabled()){
                Toast.makeText(fragment.getActivity(), "Already disabled", Toast.LENGTH_SHORT).show();
            }
            else{
                if(getDefaultBtAdapter().disable()){
                    Toast.makeText(fragment.getActivity(), "bluetooth disabled", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    public static void startScanning(Context context, BroadcastReceiver mReceiver) throws Exception {
        if(!isBtEnabled()){
            throw new Exception("Bluetooth is not enabled.");
        }
        else{
            if(BluetoothAdapter.getDefaultAdapter().startDiscovery()){
                Toast.makeText(context, "starting", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
            }
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            context.registerReceiver(mReceiver, filter);
        }
    }

    public static void stopScanning(Context context, BroadcastReceiver mReceiver){
        context.unregisterReceiver(mReceiver);
        getDefaultBtAdapter().cancelDiscovery();
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
