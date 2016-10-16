package com.aliasgarmurtaza.ntuosschat;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Aliasgar Murtaza on 7/17/16.
 */


public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessagesRecyclerAdapter.MyViewHolder> {


    private List<Message> messageList;
    private Context context;


    public MessagesRecyclerAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.message.setText(message.getMessage());
        holder.from.setText(message.getFrom());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView message, from;


        public MyViewHolder(View view) {
            super(view);
            message = (TextView) view.findViewById(R.id.message);
            from = (TextView) view.findViewById(R.id.from);
        }


    }


}