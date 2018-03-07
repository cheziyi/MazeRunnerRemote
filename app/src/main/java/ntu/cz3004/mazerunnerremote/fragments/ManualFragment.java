package ntu.cz3004.mazerunnerremote.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ntu.cz3004.mazerunnerremote.R;

public class ManualFragment extends MainFragment {

    public ManualFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.nav_manual;
    }

    @Override
    public void onBtDataReceived(byte[] data, String message) {

    }
}
