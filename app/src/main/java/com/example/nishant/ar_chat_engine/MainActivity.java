package com.example.nishant.ar_chat_engine;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.nishant.ar_chat_engine.ChatHistoryAdapter.id;
import static com.example.nishant.ar_chat_engine.ChatHistoryAdapter.messages;

public class MainActivity extends AppCompatActivity {

    ArFragment arFragment;
    private ArSceneView arSceneView;
    private boolean installRequested;

    Retrofit.Builder builder;
    Retrofit retrofit;
    DataInterface client;
    List<ChatUser> list;
    LinearLayoutManager lm;
    public static RecyclerView rv;

    ChatHistoryAdapter chatlistAdapter;
    public static LinearLayout ll_send;
    public static FloatingActionButton btn_send;
    public static EditText et_mssg;
    Boolean added=false;
    private Socket mSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (!DemoUtils.checkIsSupportedDeviceOrFinish(this)) {
            // Not a supported device.
            return;
        }

        setContentView(R.layout.activity_main);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_scene_view);
        arSceneView = arFragment.getArSceneView();


        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_scene_view);
        arSceneView = arFragment.getArSceneView();

        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getPlaneDiscoveryController().setInstructionView(null);

        try {
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            //run
            mSocket.connect();
            mSocket.on("onMessage", onMessage);
            mSocket.on("onChat", onChat);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }





        builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        client = retrofit.create(DataInterface.class);


        try {
            Session session = DemoUtils.createArSession(this, installRequested);
            Config config = new Config(session);
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL);
            session.configure(config);
            arSceneView.setupSession(session);

//            Session session = new Session(MainActivity.this);
//            arFragment.getArSceneView().setupSession(session);
//
//            Session session1 = arFragment.getArSceneView().getSession();
//
//            Log.d("sessions",String.valueOf(session1));
//            float[] pos = { 2,0,0 };
//            float[] rotation = {0,0,0,0};
//
//
//            Anchor anchor =  session.createAnchor(new Pose(pos,rotation));
//            Log.d("sessions",String.valueOf(anchor));
//
//            placeObject(arFragment,anchor);
//
//
        } catch (Exception e) {
            Log.e("erroroccur",e.toString());
        }


//        arFragment.getArSceneView().getArFrame();


//        anchorNode.setParent(arFragment.getArSceneView().getScene());
            arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
                @Override
                public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                    if (plane.getType()!=Plane.Type.HORIZONTAL_UPWARD_FACING){
                        return;
                    }

                    Anchor anchor = hitResult.createAnchor();

                }
            });

        }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void placeObject(ArFragment fragment, Anchor anchor){

        ViewRenderable.builder()
                .setView(fragment.getContext(), R.layout.planet_card_view)
                .build()
                .thenAccept(renderable -> {
                    if(!added) {
                        if(addnodetoscene(fragment, anchor, renderable)){
                         added = true;
                        }
                    }
                })
                .exceptionally(throwable -> {
                    Log.d("enter1",throwable.getMessage());

                    return null;
                });



    }

    public Boolean addnodetoscene(ArFragment fragment, Anchor anchor,ViewRenderable renderable){

        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(fragment.getArSceneView().getScene());

        Node node = new Node();
        node.setLocalPosition(new Vector3(0f,-0.121f,0));
        node.setParent(anchorNode);
        node.setRenderable(renderable);
        Log.d("node-created","created");

        View v = renderable.getView();

        rv = v.findViewById(R.id.rv_chat);
        ll_send = v.findViewById(R.id.ll_send);
        et_mssg = v.findViewById(R.id.mssg);
        btn_send =  v.findViewById(R.id.mssg_send);
        ll_send.setVisibility(View.GONE);

//        ChatAdapter chatAdapter = new ChatAdapter(getApplicationContext());
        lm = new LinearLayoutManager(getApplicationContext());




        Call<List<ChatUser>> chatUserCall = client.getchatuser("token a074856d07f11acfaa0a979e8c773b2611f429b2", String.valueOf(1));
        chatUserCall.enqueue(new Callback<List<ChatUser>>() {
            @Override
            public void onResponse(Call<List<ChatUser>> call, Response<List<ChatUser>> response) {
                list = new ArrayList<>();

                if (response.body()!=null) {
                    if (response.body().size() > 0) {

                        for (int i = 0; i < response.body().size(); i++) {

                            list.add(response.body().get(i));
                        }

                        lm = new LinearLayoutManager(getApplicationContext());
                        chatlistAdapter = new ChatHistoryAdapter(getApplicationContext(), list,"9");
                        rv.setAdapter(chatlistAdapter);
                        rv.setLayoutManager(lm);
                        rv.setHasFixedSize(true);
                        rv.addOnScrollListener(new EndlessRecyclerOnScrollListener(lm) {
                            @Override
                            public void onLoadMore(int current_page)
                            {
                                Call<List<ChatUser>> chatUserCall = client.getchatuser(getSharedPreferences("Tokenkey", MODE_PRIVATE).getString("token", "token1"), String.valueOf(current_page));
                                chatUserCall.enqueue(new Callback<List<ChatUser>>() {
                                    @Override
                                    public void onResponse(Call<List<ChatUser>> call, Response<List<ChatUser>> response) {
                                        if (response.body()!=null) {

                                            for(int i =0;i<response.body().size();i++)
                                            {
                                                list.add(response.body().get(i));
                                                chatlistAdapter.notifyItemRangeInserted(chatlistAdapter.getItemCount(),list.size()-1);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<ChatUser>> call, Throwable t) {
//                        Toast.makeText(ChatHistory.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        });
                    } else {
                    }
                }
            }
            @Override
            public void onFailure(Call<List<ChatUser>> call, Throwable t) {
//                Toast.makeText(ChatHistory.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

return true;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("keyevent", String.valueOf(event.getKeyCode()));
        int keyaction = event.getAction();

        if(keyaction == KeyEvent.ACTION_DOWN)
        {
            int keycode = event.getKeyCode();
            int keyunicode = event.getUnicodeChar(event.getMetaState() );
            char character = (char) keyunicode;
            Log.d("keyevent", String.valueOf(character));

            if (event.getKeyCode()==62){
                et_mssg.setText(et_mssg.getText().toString()+" ");
            }
            if (event.getKeyCode()==67){
                if(et_mssg.getText().length()!=0) {
                    et_mssg.setText(et_mssg.getText().toString().substring(0, (et_mssg.getText().length() - 1)));
                }
            }
            else {
                et_mssg.setText(et_mssg.getText().toString() + character);
            }
            et_mssg.setSelection(et_mssg.getText().length());
        }
        return super.onKeyDown(keyCode, event);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("error", "connected1");
//                         Toast.makeText(getApplicationContext(),
//
//                            "connect", Toast.LENGTH_LONG).show();

                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("data", "diconnected");
//                            Toast.makeText(getApplicationContext(),
//                                              "disconnect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("error", "Error connecting");
//                    Toast.makeText(getApplicationContext(),
//                            "error_connect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    finish();
                    startActivity(getIntent());
                    JSONObject json = null;
                    try {
                        json = new JSONObject(String.valueOf(args[0]));
                        Log.e("update","done");
                        Messages  m = new Messages();
                        m.setId(json.getInt("id"));
                        m.setSender(json.getInt("sender"));
                        m.setMessage(json.getString("message"));
                        m.setReceiver(json.getInt("reciever"));
                        m.setTimestamp(Date.valueOf(json.getString("timestamp")));
                        messages.add(m);

                        ChatWindowAdapter chatWindowAdapter = new ChatWindowAdapter(getApplicationContext(), messages, ChatHistoryAdapter.id);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        layoutManager.setReverseLayout(true);
                        layoutManager.setStackFromEnd(true);
                        rv.setLayoutManager(layoutManager);
                        rv.setAdapter(chatWindowAdapter);
                        rv.setHasFixedSize(true);
                        rv.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
                            @Override
                            public void onLoadMore(int current_page) {


                                Call<Usermessages> usermessagesCall = client.getusermessage(getSharedPreferences("Tokenkey", MODE_PRIVATE).getString("token", "token1"), ChatHistoryAdapter.id, String.valueOf(current_page));
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


                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    {
                    }
                }
            });
        }
    };
    private Emitter.Listener onChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject json = null;
                    try {
                        json = new JSONObject(String.valueOf(args[0]));
                        Log.e("update","done");
                        Messages  m = new Messages();
                        m.setId(json.getInt("id"));
                        m.setSender(json.getInt("sender"));
                        m.setMessage(json.getString("message"));
                        m.setReceiver(json.getInt("receiver"));

                        m.setTimestamp(new java.util.Date());
                        messages.add(0,m);
                        Log.e("update", String.valueOf(m));

                        ChatWindowAdapter chatWindowAdapter = new ChatWindowAdapter(getApplicationContext(), messages, ChatHistoryAdapter.id);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        layoutManager.setReverseLayout(true);
                        layoutManager.setStackFromEnd(true);
                        rv.setLayoutManager(layoutManager);
                        rv.setAdapter(chatWindowAdapter);
                        rv.setHasFixedSize(true);
                        rv.scrollToPosition(0);


                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    {
                        Log.e("error", String.valueOf(String.valueOf(args[0])));
                    }
                }
            });
        }
    };

    @Override
    public void onBackPressed() {
        mSocket.disconnect();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(et_mssg.getWindowToken(),0);

        super.onDestroy();
    }

}
