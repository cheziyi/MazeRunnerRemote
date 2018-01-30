package ntu.cz3004.mazerunnerremote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import ntu.cz3004.mazerunnerremote.adapters.MessageListAdapter;
import ntu.cz3004.mazerunnerremote.dto.Response;

import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.SendCommand;
import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.bt;


/**
 * Created by Aung on 1/27/2018.
 */

public class CheckC1Fragment extends Fragment implements View.OnClickListener {

    private EditText messageEditText;
    private Button sendBtn;

    private MessageListAdapter messageListAdapter;
    private RecyclerView messageRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_c1, container, false);
        messageRecyclerView = view.findViewById(R.id.messageRecyclerView);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendBtn = view.findViewById(R.id.sendBtn);

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                messageListAdapter.add("[PC]: " + message);
//                Response resp = new Gson().fromJson(message, Response.class);
//                if (resp.getGrid() != null)
//                    messageListAdapter.add("[GRID]: " + new Gson().toJson(resp.getGrid()));
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sendBtn.setOnClickListener(this);

        messageListAdapter = new MessageListAdapter();
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageRecyclerView.setAdapter(messageListAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_c8, menu);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendBtn:
                SendCommand(messageEditText.getText().toString());
                messageListAdapter.add("[Android]: " + messageEditText.getText().toString());
                messageEditText.setText("");
                break;
        }
    }
}
