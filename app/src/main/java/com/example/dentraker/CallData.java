package com.example.dentraker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.dentraker.utils.DBControll;
import com.example.dentraker.utils.PopUpCreators;
import com.example.dentraker.utils.ServerMethods;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.IOException;

public class CallData extends AppCompatActivity{
    private StrictMode.ThreadPolicy basepolicy;
    private ConstraintLayout main;
    private TextView endereco,datalabel,data,obs,statustxt;
    private ImageView image;
    private ImageButton backpage;
    private Button sendChanges,comments;
    private RadioGroup status;
    private SupportMapFragment map;
    private Globals globals;
    private void checkChangeInfo(String data, Globals.Call call) throws IOException{
        JSONObject res = DBControll.setCallStatus(data, call.getId());
        StrictMode.setThreadPolicy(basepolicy);
        if(res != null && res.has("Sucess")){
            globals.destroyCall();
            startActivity(new Intent(CallData.this,CallsPage.class));
            finish();
        }
    }
    @SuppressLint("SetTextI18n")
    private void setData(Globals.Call call){
        ConstraintSet cs = new ConstraintSet();
        cs.clone(main);
        cs.connect(datalabel.getId(),ConstraintSet.TOP,endereco.getId(),ConstraintSet.BOTTOM,56);
        endereco.setText(call.getValue("rua") +" - Nº: "+ call.getValue("numero") +" - Bairro: "+call.getValue("bairro"));
        data.setText(call.getValue("data"));
        obs.setText(call.getValue("obs"));
        Glide.with(this).load("http://192.168.15.9/dentraker"+call.getValue("anexo").replace("\\","/")).into(image);
        cs.applyTo(main);
        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globals.destroyCall();
                startActivity(new Intent(CallData.this,CallsPage.class));
                finish();
            }
        });
        if(globals.getUser().getIsInsp()){
            cs.setHorizontalBias(comments.getId(),0.89f);
            cs.applyTo(main);
            sendChanges.setVisibility(View.VISIBLE);
            status.setVisibility(View.VISIBLE);
            switch(call.getValue("status")){
                case "Aberto":
                    status.check(R.id.open);
                    break;
                case "Fechado":
                    status.check(R.id.closed);
                    break;
                case "EmInspecao":
                    status.check(R.id.inspection);
                    break;
            }
            sendChanges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject res = null;
                    try {
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
                        System.out.println(getResources().getResourceEntryName(status.getCheckedRadioButtonId()));
                        switch (getResources().getResourceEntryName(status.getCheckedRadioButtonId())) {
                            case "open":
                                checkChangeInfo("Aberto",call);
                                break;
                            case "inspection":
                                checkChangeInfo("EmInspecao",call);
                                break;
                            case "closed":
                                checkChangeInfo("Fechado",call);
                                break;
                        }
                        globals.destroyCall();
                        startActivity(new Intent(CallData.this,CallsPage.class));
                        finish();
                    }catch (IOException e){
                        PopUpCreators.createErrorPopup(main,main,getApplicationContext(),"Erro de conexão com a API !");
                    }

                }
            });
        }
        statustxt.setText(call.getValue("status"));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calldata);
        basepolicy = StrictMode.getThreadPolicy();
        globals = (Globals) getApplication();
        backpage = (ImageButton)findViewById(R.id.backpage);
        endereco = (TextView) findViewById(R.id.local);
        datalabel = (TextView) findViewById(R.id.datelabel);
        data = (TextView) findViewById(R.id.date);
        obs = (TextView) findViewById(R.id.obs);
        image = (ImageView) findViewById(R.id.attach);
        main = (ConstraintLayout) findViewById(R.id.main);
        statustxt = (TextView) findViewById(R.id.status);
        status = (RadioGroup) findViewById(R.id.statusoptions);
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        sendChanges = (Button) findViewById(R.id.sendchanges);
        comments = (Button) findViewById(R.id.comments);
        Globals.Call call = globals.getCall();
        setData(call);
        map.getMapAsync(map -> {
            LatLng place = null;
            try {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
                place = ServerMethods.getAddressCoords("AngeloSimoneti,930");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                StrictMode.setThreadPolicy(basepolicy);
            }
            assert place != null;
            map.addMarker(new MarkerOptions().position(place));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(place,17));
        });
        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CallData.this,CommentsPage.class));
                finish();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}