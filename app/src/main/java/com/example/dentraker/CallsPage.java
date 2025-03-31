package com.example.dentraker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dentraker.utils.DBControll;
import com.example.dentraker.utils.PopUpCreators;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CallsPage extends AppCompatActivity {
    private StrictMode.ThreadPolicy basePolicy;
    private LinearLayout usercalls,nextcalls;
    private Globals globals;
    private Globals.User usuario;
    private ConstraintLayout main;
    private JSONArray getDataFromPoints(boolean usesId){
        Object points = null;
        if(usesId){
            try{
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
                points = DBControll.getPoints(usuario.getIdUsuario());
                if(points.getClass() == JSONArray.class){
                    return (JSONArray) points;
                }else if(points.getClass() == JSONObject.class){
                    return null;
                }
            }catch (IOException e){
                System.out.println("Erro de conexão com o DB" + e.toString());
            }
            finally{
                StrictMode.setThreadPolicy(basePolicy);
            }
        }
        else{
            try{
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
                points = DBControll.getPoints(null);
                if(points.getClass() == JSONArray.class){
                    return (JSONArray) points;
                }else if(points.getClass() == JSONObject.class){
                    return null;
                }
            }catch (IOException e){
                System.out.println("Erro de conexão com o DB " + e.toString());
            }finally{
                StrictMode.setThreadPolicy(basePolicy);
            }
        }
        return null;
    };
    private void setPoints(LinearLayout displayView, boolean isUser) {
            JSONArray points = getDataFromPoints(isUser);
            if(points != null && points.length() > 0){
                //Insere chamados sem filtro no campo de "Seus Pontos" - Alterar para somente
                LayoutInflater inflater = LayoutInflater.from(this);
                for (int i = 0; i < points.length(); i++){
                    try{
                        JSONObject lineData = points.getJSONObject(i);
                        TextView lineComponent = (TextView) inflater.inflate(R.layout.point_view,displayView,false);
                        GradientDrawable background = new GradientDrawable();
                        if(i == 0) {
                            background.setCornerRadii(new float[]{50, 50, 50, 50, 0, 0, 0, 0});
                        }else if(i == 4){
                            background.setCornerRadii(new float[]{0, 0, 0, 0, 50, 50, 50, 50});
                        }
                        switch (lineData.getString("statuschamado")){
                            case "Aberto":
                                background.setColor(0xeefa7777);
                                break;
                            case "Fechado":
                                background.setColor(0xee66ff66);
                                break;
                            case "EmInspecao":
                                background.setColor(0xeeffff66);
                                break;
                        }
                        background.setStroke(3,0xff000000);
                        lineComponent.setBackground(background);
                        lineComponent.setOnClickListener(v -> {
                            globals.setCall(lineData);
                            startActivity(new Intent(CallsPage.this,CallData.class));
                            finish();
                        });
                        lineComponent.setText(lineData.getString("rua"));
                        displayView.addView(lineComponent);
                        //Preicsa realizar os codigo de filtragem por proximidade e que foram criados pelo usuários
                    }catch (JSONException e){
                        PopUpCreators.createErrorPopup(main,main,getApplicationContext(),"Erro ao encontrar os pontos, tente novamente");
                        globals.destroyUser();
                        globals.destroyCall();
                        startActivity(new Intent(CallsPage.this,MainActivity.class));
                        finish();
                    }
                }
            }else{
                displayView.setGravity(Gravity.CENTER);
                TextView errorline = new TextView(this);
                errorline.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                errorline.setTextColor(0xee000000);
                errorline.setTextSize(18);
                errorline.setText("Nenhum ponto cadastrado, crie um no botão abaixo !");
                errorline.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                displayView.addView(errorline);
            }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ImageButton backPage;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_callspage);
        basePolicy = StrictMode.getThreadPolicy();
        globals = (Globals) getApplication();
        usuario = globals.getUser();
        System.out.println(usuario.getIdUsuario());
        main = findViewById(R.id.main);
        usercalls =  findViewById(R.id.usercalls);
        nextcalls =  findViewById(R.id.nextcalls);
        Button insertCall =  findViewById(R.id.insertCall);
        backPage =  findViewById(R.id.backpage);
        insertCall.setOnClickListener(v -> {
            startActivity(new Intent(CallsPage.this,CreateCall.class));
            finish();
        });
        backPage.setOnClickListener(v -> {
            startActivity(new Intent(CallsPage.this,MainActivity.class));
            globals.destroyUser();
            finish();
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            setPoints(usercalls,true);
            setPoints(nextcalls,false);
        }
    }

}