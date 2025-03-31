package com.example.dentraker;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dentraker.utils.DBControll;
import com.example.dentraker.utils.PopUpCreators;
import com.example.dentraker.utils.SQLERRORS;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public StrictMode.ThreadPolicy basePolicy;
    private ConstraintLayout main;
    //Buttons ClickListeners
    private View.OnClickListener changePass = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopUpCreators.createChangePassPopUp(v,main,getApplicationContext());
        }
    };
    private View.OnClickListener goToSignIn = (v) -> {
        startActivity(new Intent(MainActivity.this,Signin.class));
        finish();
    };
    private View.OnClickListener tryLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String mail = mailLogin.getText().toString();
            String pass = passLogin.getText().toString();
            if(mail.equals("Nome de Usuario") || pass.equals("Senha")){
                PopUpCreators.createErrorPopup(v,main,getApplicationContext(),"Use os campos para digitar seu nome de usuario e sua senha !");
            }else {
                JSONObject data;
                if (!mail.isEmpty() && !pass.isEmpty()) {
                    try {
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
                        data = DBControll.loginUser(mail, pass);
                        if(data.has("Error")){
                            PopUpCreators.createErrorPopup(v,main,getApplicationContext(),data.getString("Error"));
                            return;
                        }
                        if (DBControll.checkFetchError(data.toString(), SQLERRORS.FETCH_ERROR) && DBControll.checkFetchError(data.toString(), SQLERRORS.WRONG_PASS)) {
                            System.out.println("login correto!");
                            Globals globals = (Globals) getApplication();
                            globals.setUser(data);
                            startActivity(new Intent(MainActivity.this, CallsPage.class));
                            finish();
                        } else {
                            PopUpCreators.createErrorPopup(v,main,getApplicationContext(),"Nome de usuario e(ou) senha incorreto(s) !");;
                        }
                    } catch (IOException | JSONException e) {
                        switch (e.getClass().getName()){
                            case "java.net.SocketTimeoutException":
                                PopUpCreators.createErrorPopup(v,main,getApplicationContext(),"Não foi possivel conectar, tente novamente mais tarde !");
                                break;
                            case "org.json.JSONError":
                                PopUpCreators.createErrorPopup(v,main,getApplicationContext(),"Erro na aplicação, entre em contato com o suporte para mais informações !");
                        }
                    } finally {
                        StrictMode.setThreadPolicy(basePolicy);
                    }
                } else {
                    PopUpCreators.createErrorPopup(v,main,getApplicationContext(),"Insira seu Nome de Usuario e Senha !");;
                }
            }
        }
    };
    private View.OnClickListener passFieldControl = new View.OnClickListener() {
        // Code 129 - PasswordText
        // Code 1 - InputText
        @Override
        public void onClick(View v) {
                if (passLogin.getInputType() == 129) {
                    passLogin.setInputType(1);
                    seePass.setCompoundDrawablesWithIntrinsicBounds(slashEye, null, null, null);
                } else {
                    passLogin.setInputType(129);
                    seePass.setCompoundDrawablesWithIntrinsicBounds(commonEye, null, null, null);
                }
            };
    };
    //FocusListeners
    private View.OnFocusChangeListener firstFocusController = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(mailLogin.hasFocus()){
                if(!mailIsFocusedOnTime){
                    mailLogin.setText("");
                    mailIsFocusedOnTime = true;
                }
            }else if(passLogin.hasFocus()){
                if(!passIsFocusedOnetime){
                    passLogin.setInputType(129);
                    passLogin.setText("");
                    passIsFocusedOnetime = true;
                }
            }
        }
    };
    //Set Class Variables
    private Button seePass;
    private EditText mailLogin,passLogin;
    private Drawable slashEye,commonEye;
    private boolean passIsFocusedOnetime,mailIsFocusedOnTime = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set Method variables
        Button tryLog, goToSign, changePassbtn;
        //Set XML connection and Policies controllers
        super.onCreate(savedInstanceState);
        basePolicy = StrictMode.getThreadPolicy();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //Setup the elements of XML to this class
        changePassbtn = (Button) findViewById(R.id.changePass);
        tryLog = (Button) findViewById(R.id.login);
        goToSign = (Button) findViewById(R.id.createAccount);
        seePass = (Button) findViewById(R.id.passButton);
        mailLogin = (EditText) findViewById(R.id.userLogin);
        passLogin = (EditText) findViewById(R.id.passLogin);
        main = (ConstraintLayout) findViewById(R.id.main);
        //Set used Icons
        slashEye = ContextCompat.getDrawable(getApplicationContext(), R.drawable.eye_slash_solid);
        commonEye = ContextCompat.getDrawable(getApplicationContext(), R.drawable.eye_solid);
        //Set click listeners
        changePassbtn.setOnClickListener(changePass);
        tryLog.setOnClickListener(tryLogin);
        seePass.setOnClickListener(passFieldControl);
        goToSign.setOnClickListener(goToSignIn);
        //Set Focus Listeners
        passLogin.setOnFocusChangeListener(firstFocusController);
        mailLogin.setOnFocusChangeListener(firstFocusController);
        //Apply base layout configs
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
}