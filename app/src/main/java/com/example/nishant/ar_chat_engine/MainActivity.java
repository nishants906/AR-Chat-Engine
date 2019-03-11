package com.example.nishant.ar_chat_engine;

import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ArFragment arFragment;
    private ArSceneView arSceneView;
    private boolean installRequested;


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
            Session session = DemoUtils.createArSession(this, installRequested);
            Config config = new Config(session);
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL);
            session.configure(config);
            arSceneView.setupSession(session);

        } catch (UnavailableException e) {
            Log.e("erroroccur",e.toString());
        }




//                DemoUtils.displayError(this, "Unable to load renderable", ex);
//            }
//
//            return null;
//        });


            arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
                @Override
                public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                    if (plane.getType()!=Plane.Type.HORIZONTAL_UPWARD_FACING){
                        return;
                    }
                    Anchor anchor = hitResult.createAnchor();
                    placeObject(arFragment,anchor);

                }
            });

        }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void placeObject(ArFragment fragment, Anchor anchor){

        ViewRenderable.builder()
                .setView(fragment.getContext(), R.layout.planet_card_view)
                .build()
                .thenAccept(renderable -> addnodetoscene(fragment, anchor, renderable))
                .exceptionally(throwable -> {
                    Log.d("enter1",throwable.getMessage());

                    return null;
                });



    }

    public void addnodetoscene(ArFragment fragment, Anchor anchor,ViewRenderable renderable){

        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(fragment.getArSceneView().getScene());

        Node node = new Node();
        node.setLocalPosition(new Vector3(0f,-0.121f,0));
        node.setParent(anchorNode);
        node.setRenderable(renderable);
        Log.d("node-created","created");

        View v = renderable.getView();

        RecyclerView rv = v.findViewById(R.id.rv_chat);
        ChatAdapter chatAdapter = new ChatAdapter(getApplicationContext());
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(lm);
        rv.setAdapter(chatAdapter);



        Call<List<ChatUser>> chatUserCall = client.getchatuser(getSharedPreferences("Tokenkey", MODE_PRIVATE).getString("token", "token1"), String.valueOf(pageposition));
        chatUserCall.enqueue(new Callback<List<ChatUser>>() {
            @Override
            public void onResponse(Call<List<ChatUser>> call, Response<List<ChatUser>> response) {
                list = new ArrayList<>();

                if (response.body()!=null) {
                    if (response.body().size() > 0) {

                        for (int i = 0; i < response.body().size(); i++) {

                            list.add(response.body().get(i));
                        }

                        progressBar.setVisibility(View.GONE);
                        lm = new LinearLayoutManager(getApplicationContext());
                        chatlistAdapter = new ChatHistoryAdapter(getApplicationContext(), list,HomeActivity.userid);
                        rv_chat.setAdapter(chatlistAdapter);
                        rv_chat.setLayoutManager(lm);
                        rv_chat.setHasFixedSize(true);
                        rv_chat.addOnScrollListener(new EndlessRecyclerOnScrollListener(lm) {
                            @Override
                            public void onLoadMore(int current_page)
                            {
                                ploader.setVisibility(View.VISIBLE);
                                Call<List<ChatUser>> chatUserCall = client.getchatuser(getSharedPreferences("Tokenkey", MODE_PRIVATE).getString("token", "token1"), String.valueOf(current_page));
                                chatUserCall.enqueue(new Callback<List<ChatUser>>() {
                                    @Override
                                    public void onResponse(Call<List<ChatUser>> call, Response<List<ChatUser>> response) {
                                        ploader.setVisibility(View.GONE);
                                        if (response.body()!=null) {

                                            for(int i =0;i<response.body().size();i++){

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
                        rl_nochat.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<ChatUser>> call, Throwable t) {
//                Toast.makeText(ChatHistory.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }
}
