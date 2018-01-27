package ntu.cz3004.mazerunnerremote.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ntu.cz3004.mazerunnerremote.R;

/**
 * Created by Aung on 1/27/2018.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private TextView messageTextView;

    public MessageViewHolder(View itemView) {
        super(itemView);
        messageTextView = itemView.findViewById(R.id.messageTextView);
    }

    public void bind(String message) {
        messageTextView.setText(message);
    }
}
