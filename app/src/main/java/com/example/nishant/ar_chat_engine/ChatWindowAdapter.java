package com.example.nishant.ar_chat_engine;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nishant on 16/02/18.
 */

public class ChatWindowAdapter extends RecyclerView.Adapter<ChatWindowAdapter.ChatViewHolder> {

    Context c;
    List<Messages> usermessages;
    String id;

    public ChatWindowAdapter(Context applicationContext, List<Messages> messages, String id)
    {
        c = applicationContext;
        usermessages = new ArrayList<>();
        this.usermessages = messages;
        this.id = id;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(c).inflate(R.layout.chatwindowlayout,parent,false));
    }

    @Override
    public void onBindViewHolder(final ChatViewHolder holder, final int position) {
        SimpleDateFormat parsedateformat = new SimpleDateFormat("dd-MMM-yy");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");


        if(position!=0) {
            if ((usermessages.get(position).getTimestamp().getDate()>usermessages.get(position - 1).getTimestamp().getDate())||((usermessages.get(position).getTimestamp().getMonth()>usermessages.get(position - 1).getTimestamp().getMonth())||((usermessages.get(position).getTimestamp().getYear()>usermessages.get(position - 1).getTimestamp().getYear())))) {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(String.valueOf(parsedateformat.format(usermessages.get(position).getTimestamp())));
                holder.setIsRecyclable(false);
            }
        }


        try {
            if(usermessages!=null){
                if (usermessages.get(position).getIs_read()) {
                holder.readtick.setColorFilter(ContextCompat.getColor(c, R.color.green));
                holder.readtick1.setColorFilter(ContextCompat.getColor(c, R.color.green));
                }
            }
        }
        catch(Exception e){

        }



        if (Integer.parseInt(id)==usermessages.get(position).getSender()){
            holder.recieve.setVisibility(View.VISIBLE);
            holder.recieve.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(usermessages.get(position).getMessage())));
            holder.recieve.setMovementMethod(LinkMovementMethod.getInstance());
            holder.send.setVisibility(View.GONE);
            holder.timerecieve.setText(parseFormat.format(usermessages.get(position).getTimestamp()));
            holder.rl_recieve.setVisibility(View.VISIBLE);
            holder.rl_send.setVisibility(View.GONE);
        }
        else {
                holder.send.setVisibility(View.VISIBLE);
                holder.send.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(usermessages.get(position).getMessage())));
                holder.send.setMovementMethod(LinkMovementMethod.getInstance());
                holder.recieve.setVisibility(View.GONE);
                holder.timesend.setText(parseFormat.format(usermessages.get(position).getTimestamp()));
                holder.rl_recieve.setVisibility(View.GONE);
                holder.rl_send.setVisibility(View.VISIBLE);
            }
    }

    @Override
    public int getItemCount()
    {
        return usermessages.size();
    }



    public class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView send,recieve;
        TextView timesend,timerecieve;
        LinearLayout rl_recieve;
        LinearLayout rl_send;

        TextView date;
        ImageView readtick,readtick1;

        public ChatViewHolder(View itemView) {
            super(itemView);

            send = itemView.findViewById(R.id.send);
            recieve = itemView.findViewById(R.id.recieve);

            timesend = itemView.findViewById(R.id.time);
            timerecieve = itemView.findViewById(R.id.timerecieve);
            rl_recieve = itemView.findViewById(R.id.rl_recieve);
            rl_send = itemView.findViewById(R.id.rl_send);
            date = itemView.findViewById(R.id.date);
            readtick = itemView.findViewById(R.id.readtick);


        }
    }

}
