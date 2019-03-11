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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ArFragment arFragment;
    private ArSceneView arSceneView;
    private boolean installRequested;
    ViewRenderable vi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_scene_view);
        arSceneView = arFragment.getArSceneView();

 d
        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {

                Log.d("hitresult","hitresult");
                AnchorNode anchorNode = new AnchorNode(hitResult.createAnchor());
                anchorNode.setParent(arFragment.getArSceneView().getScene());

                Node infoCard = new Node();
                infoCard.setParent(anchorNode);
                infoCard.setRenderable(vi);

                RecyclerView rv = vi.getView().findViewById(R.id.rv_chat);
                ChatAdapter chatAdapter = new ChatAdapter(getApplicationContext());
                LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
                rv.setLayoutManager(lm);
                rv.setAdapter(chatAdapter);

            }
        });
//
//        CompletableFuture<ViewRenderable> recyc = ViewRenderable.builder().setView(this, R.layout.planet_card_view).build();
//
//        try {
//            vi = recyc.get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }



}
