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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.nishant.ar_chat_engine.MainActivity.rv;

/**
 * Created by Nishant on 19/12/17.
 */


public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ChatlistViewHolder> {
    Context c;
    List<ChatUser> chatUser;
    String userid;

    Retrofit.Builder builder;
    Retrofit retrofit;
    DataInterface client;

    public ChatHistoryAdapter(Context context, List<ChatUser> chatUser, String userid) {
        c = context;
        this.chatUser = chatUser;
        this.userid = userid;



        builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        client = retrofit.create(DataInterface.class);


    }

    @Override
    public ChatlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ChatlistViewHolder(LayoutInflater.from(c).inflate(R.layout.chatlistlayout,parent,false));
    }

    @Override
    public void onBindViewHolder(final ChatlistViewHolder holder, final int position) {

        Log.d("chanuser", String.valueOf(position));
        Log.d("chanuser", String.valueOf(chatUser.get(position).getMessages().getSender()));

        if(!String.valueOf(chatUser.get(position).getProfile().getThumbnail()).equals("null")) {
            Picasso.with(c).load(chatUser.get(position).getProfile().getThumbnail()).into(holder.profile);
        }

        holder.name.setText(chatUser.get(position).getProfile().getName());
        if(!String.valueOf(chatUser.get(position).getMessages().getMessage()).equals("null"))
        {
            holder.bio.setVisibility(View.VISIBLE);
            holder.bio.setText(
                    StringEscapeUtils.unescapeJava(chatUser.get(position).getMessages().getMessage()));
        }


        holder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isNetworkAvailable()) {

//                    Snackbar snackbar = Snackbar
////                            .make(ChatHistory.coordinatorLayout, "No internet connectivity", Snackbar.LENGTH_LONG);
//                    snackbar.show();
                } else {


                    String id = chatUser.get(position).getProfile().getId().toString();


                    Call<Usermessages> usermessagesCall = client.getusermessage("token a074856d07f11acfaa0a979e8c773b2611f429b2", chatUser.get(position).getProfile().getId().toString(), "1");
                    usermessagesCall.enqueue(new Callback<Usermessages>() {
                        @Override
                        public void onResponse(Call<Usermessages> call, Response<Usermessages> response) {

                            Log.d("asdfghjkl", String.valueOf(response.body()));


                            List messages = new ArrayList<>();
                            if (response.body().getMessages() != null) {
                                if (response.body().getMessages().size() > 0) {
                                    for (int i = 0; i < response.body().getMessages().size(); i++) {

                                        messages.add(response.body().getMessages().get(i));
                                    }
                                }
                                ChatWindowAdapter chatWindowAdapter = new ChatWindowAdapter(c, messages, id);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(c);
                                layoutManager.setReverseLayout(true);
                                layoutManager.setStackFromEnd(true);
                                rv.setLayoutManager(layoutManager);
                                rv.setAdapter(chatWindowAdapter);
                                rv.scrollToPosition(0);
                                rv.setHasFixedSize(true);
                                rv.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
                                    @Override
                                    public void onLoadMore(int current_page) {


                                        Call<Usermessages> usermessagesCall = client.getusermessage("token a074856d07f11acfaa0a979e8c773b2611f429b2", id, String.valueOf(current_page));
                                        usermessagesCall.enqueue(new Callback<Usermessages>() {
                                            @Override
                                            public void onResponse(Call<Usermessages> call, Response<Usermessages> response) {

                                                if (response.body() != null) {
                                                    for (int i = 0; i < response.body().getMessages().size(); i++) {
                                                        messages.add(response.body().getMessages().get(i));
                                                    }


                                                    chatWindowAdapter.notifyItemRangeInserted(chatWindowAdapter.getItemCount(), messages.size() - 1);
                                                }

//                        Log.d("responselength", String.valueOf(posts.size()));


                                            }

                                            @Override
                                            public void onFailure(Call<Usermessages> call, Throwable t) {

                                            }
                                        });


                                    }
                                });

                            }

                        }

                        @Override
                        public void onFailure(Call<Usermessages> call, Throwable t) {
//                            Toast.makeText(ChatWindow.this, "Network error !", Toast.LENGTH_SHORT).show();
                            Log.d("error ", t.toString());

                        }
                    });
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                }
            }
        });
        try {

            SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
            holder.time.setText(parseFormat.format(chatUser.get(position).getMessages().getTimestamp()));

        }
        catch(Exception e){
            System.out.println(e);

        }

    }

    @Override
    public int getItemCount() {
        return chatUser.size();
    }



    public class ChatlistViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        TextView name,bio;
        RelativeLayout rl_item;
        TextView time;
        ImageView readtick;

        public ChatlistViewHolder(View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);
            bio = itemView.findViewById(R.id.bio);
            time = itemView.findViewById(R.id.time);
            rl_item = itemView.findViewById(R.id.rl_item);
            readtick = itemView.findViewById(R.id.readtick);


        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
