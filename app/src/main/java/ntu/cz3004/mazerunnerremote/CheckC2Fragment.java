package ntu.cz3004.mazerunnerremote;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import ntu.cz3004.mazerunnerremote.managers.BluetoothManager;

import static android.app.Activity.RESULT_OK;

public class CheckC2Fragment extends Fragment implements View.OnClickListener {

    private ToggleButton btBtn;
    private Button searchBluetoothBtn;

    public CheckC2Fragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_c2, container, false);
        btBtn = view.findViewById(R.id.btBtn);
        searchBluetoothBtn = view.findViewById(R.id.searchBluetoothBtn);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btBtn.setOnClickListener(this);
        searchBluetoothBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btBtn:
                BluetoothManager.setBtEnabled(btBtn.isChecked(), this);
                break;
            case R.id.searchBluetoothBtn:
                Intent myIntent = new Intent(getActivity(), BluetoothListActivity.class);
                getActivity().startActivity(myIntent);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BluetoothManager.REQUEST_ENABLE_BT)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(getActivity(), "bluetooth enabled", Toast.LENGTH_LONG).show();
            }
            else {

                Toast.makeText(getActivity(), "Failed to enable bluetooth", Toast.LENGTH_LONG).show();

            }
        }

    }
}
