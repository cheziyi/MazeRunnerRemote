package ntu.cz3004.mazerunnerremote.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Aung on 1/31/2018.
 */

public abstract class MainFragment extends Fragment {

    private OnFragmentViewCreatedListener onFragmentViewCreatedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onFragmentViewCreatedListener = (OnFragmentViewCreatedListener) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        onFragmentViewCreatedListener.onViewCreated(getNavigationMenuItemId());
    }

    abstract int getNavigationMenuItemId();
}
