package ntu.cz3004.mazerunnerremote.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import ntu.cz3004.mazerunnerremote.R;

import static ntu.cz3004.mazerunnerremote.managers.BluetoothManager.bt;

/**
 * Created by wangb on 8/2/2018.
 */

public class CheckC8Fragment extends MainFragment implements View.OnClickListener {

    private EditText textString1;
    private EditText textString2;
    private Button btnPersistent1;
    private Button btnPersistent2;
    private Button btnViewPersistent1;
    private Button btnViewPersistent2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_c8,container,false);
        textString1 = view.findViewById(R.id.textString1);
        textString2 = view.findViewById(R.id.textString2);
        btnPersistent1= view.findViewById(R.id.btnPersistent1);
        btnPersistent2 = view.findViewById(R.id.btnPersistent2);
        btnViewPersistent1 = view.findViewById(R.id.btnViewPersistent1);
        btnViewPersistent2=view.findViewById(R.id.btnViewPersistent2);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textString1.setOnClickListener(this);
        textString2.setOnClickListener(this);
        btnPersistent1.setOnClickListener(this);
        btnPersistent2.setOnClickListener(this);
        btnViewPersistent1.setOnClickListener(this);
        btnViewPersistent2.setOnClickListener(this);
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.nav_check_c8;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        SharedPreferences sharedPref = this.getContext().getSharedPreferences("persistentStrings",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
            switch (view.getId()) {
                case R.id.btnPersistent1:
                    editor.putString("string1", textString1.getText().toString());
                    editor.apply();
                    Toast.makeText(view.getContext(),"Saved!", Toast.LENGTH_LONG).show();
                    break;

                case R.id.btnPersistent2:
                    editor.putString("string2", textString2.getText().toString());
                    editor.apply();
                    Toast.makeText(view.getContext(),"Saved!", Toast.LENGTH_LONG).show();
                    break;

                case R.id.btnViewPersistent1:
                    String string1=sharedPref.getString("string1","");
                    bt.send(string1, false);
                    break;

                case R.id.btnViewPersistent2:
                    String string2 = sharedPref.getString("string2","");
                    bt.send(string2, false);
                    break;
            }
    }



}
