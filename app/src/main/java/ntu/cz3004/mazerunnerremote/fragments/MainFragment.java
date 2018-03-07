package ntu.cz3004.mazerunnerremote.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.bt;

/**
 * Created by Aung on 1/31/2018.
 */

public abstract class MainFragment extends Fragment {

    private OnFragmentViewCreatedListener onFragmentViewCreatedListener;
    private OnBtReceivedListener onBtReceivedListener;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                onBtReceivedListener.onBtReceived(data, message);
                onBtDataReceived(data, message);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onFragmentViewCreatedListener = (OnFragmentViewCreatedListener) context;
        onBtReceivedListener = (OnBtReceivedListener) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        onFragmentViewCreatedListener.onViewCreated(getNavigationMenuItemId());
    }

    abstract int getNavigationMenuItemId();

    public abstract void onBtDataReceived(byte[] data, String message);
}
