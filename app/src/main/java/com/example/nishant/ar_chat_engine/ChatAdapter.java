package com.example.nishant.ar_chat_engine;

/**
 * Created by Nishant on 16/02/18.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.List;


/**
 * Created by Nishant on 19/12/17.
 */


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatlistViewHolder> {
    Context c;



    public ChatAdapter(Context context) {
        c = context;


    }

    @Override
    public ChatlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ChatlistViewHolder(LayoutInflater.from(c).inflate(R.layout.chatlistlayout,parent,false));
    }

    @Override
    public void onBindViewHolder(final ChatlistViewHolder holder, final int position) {


    }

    @Override
    public int getItemCount() {
        return 15;
    }



    public class ChatlistViewHolder extends RecyclerView.ViewHolder {

        public ChatlistViewHolder(View itemView) {
            super(itemView);


        }
    }
}
