package ntu.cz3004.mazerunnerremote.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ntu.cz3004.mazerunnerremote.R;
import ntu.cz3004.mazerunnerremote.view_holders.MessageViewHolder;

/**
 * Created by Aung on 1/27/2018.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    List<String> messageList;

    public MessageListAdapter() {
        messageList = new ArrayList<>();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_message, parent, false);
        return new MessageViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.bind(messageList.get(position));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void add(String writeMessage) {
        messageList.add(writeMessage);
        notifyItemInserted(messageList.size() - 1);
    }

    public void clear(){
        messageList = new ArrayList<>();
        notifyDataSetChanged();
    }

}
