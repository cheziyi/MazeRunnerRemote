package ntu.cz3004.mazerunnerremote;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import ntu.cz3004.mazerunnerremote.engines.BotEngine;

/**
 * Created by Aung on 1/28/2018.
 */

public class CheckC3Fragment extends Fragment implements View.OnClickListener {

    private BotEngine botEngine;
    private Button upBtn;
    private Button leftBtn;
    private Button rightBtn;
    private Button downBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_c3, container, false);
        botEngine = view.findViewById(R.id.botEngine);
        upBtn = view.findViewById(R.id.upBtn);
        leftBtn = view.findViewById(R.id.leftBtn);
        rightBtn = view.findViewById(R.id.rightBtn);
        downBtn = view.findViewById(R.id.downBtn);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        upBtn.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        downBtn.setOnClickListener(this);
        botEngine.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        botEngine.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        botEngine.resume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.upBtn:
                botEngine.setHeading(BotEngine.Heading.UP);
                break;
            case R.id.leftBtn:
                botEngine.setHeading(BotEngine.Heading.LEFT);
                break;
            case R.id.rightBtn:
                botEngine.setHeading(BotEngine.Heading.RIGHT);
                break;
            case R.id.downBtn:
                botEngine.setHeading(BotEngine.Heading.DOWN);
                break;
        }
        botEngine.setUpdated(false);
    }
}
