package ntu.cz3004.mazerunnerremote.fragments;

import android.content.Context;
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

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import ntu.cz3004.mazerunnerremote.R;
import ntu.cz3004.mazerunnerremote.adapters.MessageListAdapter;

import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.bt;


/**
 * Created by Aung on 1/27/2018.
 */

public class CheckC1Fragment extends MainFragment implements View.OnClickListener {

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
    int getNavigationMenuItemId() {
        return R.id.nav_check_c1;
    }

    @Override
    public void onBtDataReceived(byte[] data, String message) {
        messageListAdapter.add("[PC]: " + message);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_c1, menu);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendBtn:
                bt.send(messageEditText.getText().toString(), true);
                messageListAdapter.add("[Android]: " + messageEditText.getText().toString());
                messageEditText.setText("");
                break;
        }
    }
}
