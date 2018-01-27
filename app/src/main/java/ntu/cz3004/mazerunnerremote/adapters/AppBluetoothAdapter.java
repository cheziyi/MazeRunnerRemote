package ntu.cz3004.mazerunnerremote.adapters;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ntu.cz3004.mazerunnerremote.R;
import ntu.cz3004.mazerunnerremote.view_holders.BluetoothViewHolder;

/**
 * Created by Aung on 1/26/2018.
 */

public class AppBluetoothAdapter extends RecyclerView.Adapter<BluetoothViewHolder> {

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(BluetoothDevice bluetoothObject);
    }

    private List<BluetoothDevice> bluetoothList;

    public AppBluetoothAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        bluetoothList = new ArrayList<>();
    }

    @Override
    public BluetoothViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_bluetooth, parent, false);
        return new BluetoothViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(BluetoothViewHolder holder, int position) {
        holder.bind(bluetoothList.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return bluetoothList.size();
    }

    public void add(BluetoothDevice bluetoothDevice) {
        for(int i = 0; i < bluetoothList.size(); i++) {
            if(bluetoothList.get(i).getAddress().equals(bluetoothDevice.getAddress())){
                return;
            }
        }
        bluetoothList.add(bluetoothDevice);
        notifyItemInserted(bluetoothList.size() - 1);
    }

    public void update(BluetoothDevice bluetoothDevice) {
        for(int i = 0; i < bluetoothList.size(); i++) {
            if(bluetoothList.get(i).getAddress().equals(bluetoothDevice.getAddress())){
                //update device info
                bluetoothList.remove(i);
                bluetoothList.add(i, bluetoothDevice);
                notifyItemRangeChanged(i, 1);
                return;
            }
        }
    }

    public void remove(BluetoothDevice bluetoothDevice){
        for(int i = 0; i < bluetoothList.size(); i++) {
            if(bluetoothList.get(i).getAddress().equals(bluetoothDevice.getAddress())){
                bluetoothList.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void clear() {
        bluetoothList = new ArrayList<>();
        notifyDataSetChanged();
    }
}
