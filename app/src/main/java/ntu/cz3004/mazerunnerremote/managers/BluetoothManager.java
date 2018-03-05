package ntu.cz3004.mazerunnerremote.managers;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import ntu.cz3004.mazerunnerremote.dto.Command;

/**
 * Created by Aung on 1/26/2018.
 */

public class BluetoothManager {

    public static int REQUEST_ENABLE_BT = 99;

    public static BluetoothSPP bt;

    public static Boolean ManualDc = false;
    public static String LastConnectedDevice = "";

    public static BluetoothAdapter getDefaultBtAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    public static void setBtEnabled(boolean setEnabled, Activity activity) {
        if (setEnabled) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (getDefaultBtAdapter().disable()) {
                Toast.makeText(activity, "bluetooth disabled", Toast.LENGTH_LONG).show();
            } else {
                //TODO: fail to disable BT
            }
        }
    }

    public static void SendCommand(Command command){
        bt.send(command.getCommandString(), false);
    }

}
