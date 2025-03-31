package com.example.dentraker;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dentraker.utils.DBControll;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Signin extends AppCompatActivity {
    private boolean isInspCheckd,status = false;
    private StrictMode.ThreadPolicy basePolicy;
    private TextView inspLabel;
    private EditText username,pass,mail,nome,phone,bdate,inspcode;
    private ArrayList<EditText> fields;
    private Switch isInsp;
    private Button trySign;
    private ConstraintLayout layout;
    private final ConstraintSet layoutConfig = new ConstraintSet();
    private ImageButton backbutton;
    private Globals globals;
    private Globals.User user;
    private static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }
    private String formatDate(String date){
        try{
            SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date d = data.parse(date);
            SimpleDateFormat sqldata = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());

            System.out.println(sqldata.format(d));
            return sqldata.format(d);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return "";
    };
    private void changeInspField(){
        if(isInsp.isChecked()){
            layoutConfig.connect(inspcode.getId(),ConstraintSet.TOP,isInsp.getId(),ConstraintSet.BOTTOM);
            layoutConfig.connect(trySign.getId(),ConstraintSet.TOP,inspcode.getId(),ConstraintSet.BOTTOM);
            inspcode.setVisibility(View.VISIBLE);
            inspLabel.setVisibility(View.VISIBLE);
            isInspCheckd=true;
        }else{
            layoutConfig.connect(inspcode.getId(),ConstraintSet.TOP,trySign.getId(),ConstraintSet.BOTTOM);
            layoutConfig.connect(trySign.getId(),ConstraintSet.TOP,isInsp.getId(),ConstraintSet.BOTTOM);
            inspcode.setVisibility(View.GONE);
            inspLabel.setVisibility(View.GONE);
            isInspCheckd=false;
        }
    }
    private void insertFields(){
        if(isInsp.isChecked()){
            fields = new ArrayList<EditText>(Arrays.asList(username,pass,mail,nome,phone,bdate,inspcode));
        }else{
            fields = new ArrayList<EditText>(Arrays.asList(username,pass,mail,nome,phone,bdate));
        }

    }
    private boolean checkStatus(){
        int count = 0;
        int minAmount = isInspCheckd ? fields.toArray().length : fields.toArray().length-1;
        for (EditText t:fields) {
            if(!t.getText().toString().isEmpty()){
                if(t.getId() == R.id.pass && t.getText().length() < 10 ){
                    t.setError("A senha deve possuir mais de 10 caracteres");
                }else if(t.getId() == R.id.pass && t.getText().length() >= 10){
                    count ++;
                }else if(t.getId() != R.id.pass){
                    count++;
                }

            }else{
                t.setError("Preencha todos os campos");
            }
        }
        return count >= minAmount;
    }
    private final View.OnClickListener trySignCL = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            insertFields();
            status = checkStatus();
            if (status) {
                try {
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
                    JSONObject data = (JSONObject) new JSONObject();
                    data.put("username", username.getText().toString());
                    data.put("pass", pass.getText().toString());
                    data.put("mail", mail.getText().toString());
                    data.put("nome", nome.getText().toString());
                    data.put("phone", phone.getText().toString());
                    data.put("bdate", formatDate(bdate.getText().toString()));
                    if(isInspCheckd){
                        data.put("inspCode",inspcode.getText().toString());
                        data.put("isInsp",true);
                    }else{
                        data.put("isInsp",false);
                    }
                    if(DBControll.createUser(data).has("Sucess")) {
                        globals.setUser(data);
                        startActivity(new Intent(Signin.this,CallsPage.class));
                        finish();
                    }
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                } finally {
                    StrictMode.setThreadPolicy(basePolicy);
                }
            }else{
                System.out.println("Min fields not filled");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        basePolicy = StrictMode.getThreadPolicy();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);
        layout= (ConstraintLayout)findViewById(R.id.main);
        inspLabel = (TextView)findViewById(R.id.inspLabel);
        username = (EditText)findViewById(R.id.username);
        pass = (EditText)findViewById(R.id.pass);
        mail = (EditText)findViewById(R.id.mail);
        nome = (EditText)findViewById(R.id.nome);
        phone = (EditText)findViewById(R.id.phone);
        bdate = (EditText)findViewById(R.id.bdate);
        inspcode = (EditText)findViewById(R.id.inspectorCode);
        isInsp = (Switch)findViewById(R.id.isInspector);
        trySign = (Button)findViewById(R.id.trysign);
        backbutton = (ImageButton)findViewById(R.id.back);
        globals = (Globals) getApplication();
        trySign.setOnClickListener(trySignCL);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backpage = new Intent(Signin.this,MainActivity.class);
                startActivity(backpage);
                finish();
            }
        });
        changeInspField();
        layoutConfig.clone(layout);
        isInsp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeInspField();
            }
        });
        bdate.addTextChangedListener(new TextWatcher() {
            boolean isUpdating;
            String old;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = Signin.unmask(s.toString());
                String mascara = "";

                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }

                int i = 0;
                for (char m : "##/##/####".toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }

                isUpdating = true;
                bdate.setText(mascara);
                bdate.setSelection(mascara.length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}