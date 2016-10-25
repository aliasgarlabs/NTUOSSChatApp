package com.aliasgarmurtaza.ntuosschat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;

/**
 * Created by Aliasgar Murtaza on 7/17/16.
 * This is an adapter for RecyclerView.
 * RecyclerView is an android view to show lists.
 * The code in this class has nothing to do with Firebase.
 * Please Google if you have doubts on RecyclerViews as it is out of scope for our demo.
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

        //If the name is too long, just use the first name
        if(message.getFrom().length()>10)
        {
            message.setFrom(message.getFrom().substring(0,message.getFrom().indexOf(" ")));
        }

        if (message.getMessageType() == Message.TYPE_TEXT) {
            //Message type is Text
            holder.message.setText(message.getText());
            holder.from.setText(message.getFrom());
            holder.imageView.setVisibility(View.GONE);
            holder.message.setVisibility(View.VISIBLE);
        }
        else if (message.getMessageType() == Message.TYPE_IMAGE) {
            //Message type is Image
            holder.from.setText(message.getFrom());
            //Setting default image so that imageView is not empty.
            holder.imageView.setImageResource(R.drawable.default_image);
            holder.message.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            //Downloading image with Glide. Free open-source library by Google. Swift.
            Glide.with(context).load(message.getImageURL()).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView message, from;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            message = (TextView) view.findViewById(R.id.message);
            from = (TextView) view.findViewById(R.id.from);
            imageView = (ImageView) view.findViewById(R.id.message_image);
        }
    }
}