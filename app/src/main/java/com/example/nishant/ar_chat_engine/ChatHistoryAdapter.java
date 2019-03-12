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
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

import org.apache.commons.lang3.StringEscapeUtils;

import static com.example.nishant.ar_chat_engine.MainActivity.btn_send;
import static com.example.nishant.ar_chat_engine.MainActivity.et_mssg;
import static com.example.nishant.ar_chat_engine.MainActivity.ll_send;
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
    public static List messages;
    public static String id;

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
                    Html.fromHtml(StringEscapeUtils.unescapeJava(chatUser.get(position).getMessages().getMessage())));
        }


        holder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {


                     id = chatUser.get(position).getProfile().getId().toString();


                    Call<Usermessages> usermessagesCall = client.getusermessage("token a074856d07f11acfaa0a979e8c773b2611f429b2", chatUser.get(position).getProfile().getId().toString(), "1");
                    usermessagesCall.enqueue(new Callback<Usermessages>() {
                        @Override
                        public void onResponse(Call<Usermessages> call, Response<Usermessages> response) {

                            Log.d("asdfghjkl", String.valueOf(response.body()));
                            ll_send.setVisibility(View.VISIBLE);


                            messages = new ArrayList<>();
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

                    et_mssg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//
                        }
                    });

//                    et_mssg.seo(new View.OnFocusChangeListener() {
//                        @Override
//                        public void onFocusChange(View v, boolean hasFocus) {
////                            if (hasFocus) {
//                                InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
////                            }
////                            else{
////                                InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
////                                imm.hideSoftInputFromWindow(et_mssg.getWindowToken(),0);
////                            }
//                        }
//                    });


                    btn_send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            MessageCreate messageCreate = new MessageCreate(Integer.parseInt(id), (StringEscapeUtils.unescapeJava(et_mssg.getText().toString())));
                            Log.d("messagedata", String.valueOf(messageCreate));
                            Call<MessageCreate> messageCreateCall = client.createmessage("token a074856d07f11acfaa0a979e8c773b2611f429b2", messageCreate);
                            Log.d("messagedata", String.valueOf(messageCreate.getReceiver()));
                            Log.d("messagedata", String.valueOf(messageCreateCall));


                            Messages m = new Messages();
                            m.setId(1);
                            m.setSender(Integer.valueOf(userid));
                            m.setMessage(et_mssg.getText().toString());
                            m.setReceiver(Integer.valueOf(id));
                            m.setTimestamp(new java.util.Date());
                            m.setIs_read(false);
                            messages.add(0, m);
                            Log.e("update", String.valueOf(m));


                            ChatWindowAdapter chatWindowAdapter = new ChatWindowAdapter(c, messages, id);
//                    chatWindowAdapter.notifyDataSetChanged();

                            LinearLayoutManager layoutManager = new LinearLayoutManager(c);
                            layoutManager.setReverseLayout(true);
                            layoutManager.setStackFromEnd(true);
                            rv.setLayoutManager(layoutManager);
                            rv.setAdapter(chatWindowAdapter);
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
                            rv.scrollToPosition(0);
//                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                                        imm.hideSoftInputFromWindow(message.getWindowToken(), 0);
                            et_mssg.setText("");


                            messageCreateCall.enqueue(new Callback<MessageCreate>() {
                                @Override
                                public void onResponse(Call<MessageCreate> call, Response<MessageCreate> response) {
                                    Log.d("responsebody", String.valueOf(response.body()));

                                }

                                @Override
                                public void onFailure(Call<MessageCreate> call, Throwable t) {
                                    Log.d("responsebody", String.valueOf(t.getMessage()));
                                }
                            });
                        }
                    });

                }

                try {

                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
                    holder.time.setText(parseFormat.format(chatUser.get(position).getMessages().getTimestamp()));

                } catch (Exception e) {
                    System.out.println(e);

                }

            }
        });
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
