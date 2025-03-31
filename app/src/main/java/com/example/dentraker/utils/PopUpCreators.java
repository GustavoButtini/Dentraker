package com.example.dentraker.utils;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.os.Handler;
import androidx.annotation.LayoutRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.dentraker.R;
import com.example.dentraker.components.PassChangePopUp;

import java.util.Arrays;
import java.util.List;

public class PopUpCreators {
    private static Drawable userFields;
    private static boolean isUp = false;
    @SuppressLint("ClickableViewAccessibility")
    private static void createPopUpBase(View v, ConstraintLayout main, Context appContext, @LayoutRes int id,String msg){
        List<ConstraintLayout> layouts = Arrays.asList(main);
        Drawable darkbg = ContextCompat.getDrawable(appContext,R.drawable.dark_main_bg);
        Drawable whitebg = ContextCompat.getDrawable(appContext,R.drawable.whitebg);
        userFields = ContextCompat.getDrawable(appContext,R.drawable.white_light_border_bg);
        LayoutInflater inflater = (LayoutInflater) appContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(id,null);
        int size = ViewGroup.LayoutParams.WRAP_CONTENT;
        final PopupWindow popUp = new PopupWindow(popUpView,size,size,true);
        popUp.showAtLocation(v, Gravity.CENTER,0,0);
        Button btn = (Button) popUpView.findViewById(R.id.dismisspopup);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id != R.layout.popup_error){
                    EditText mail = (EditText)popUpView.findViewById(R.id.emailToSend);
                    TextView txt = (TextView)popUpView.findViewById(R.id.changepasstxt) ;
                    if(PassChangePopUp.checkMail(mail)) {
                        txt.setText("Achamos seu Email !, verifique sua caixa de entrada !");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                popUp.dismiss();
                            }
                        },2000);
                    }else{
                        txt.setText("Esse email não é valido, insira um email valido");
                    }
                }
                else{
                    popUp.dismiss();
                }

            }
        });
        if(id == R.layout.popup_error && msg != null){
            TextView txt = (TextView) popUpView.findViewById(R.id.errormsg);
            txt.setText(msg);
        }
        main.setBackground(darkbg);
        changeButtonColors(layouts,darkbg);
        isUp = true;
        popUpView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popUp.dismiss();
                return true;
            }
        });
        popUp.setOnDismissListener(()->{
            main.setBackground(ContextCompat.getDrawable(appContext,R.drawable.whitebg));
            changeButtonColors(layouts,whitebg);
            isUp = false;
        });
    }
    private static void changeButtonColors(List<ConstraintLayout> li,Drawable color){
        for(ConstraintLayout cl:li){
            for(int i = 0; i<cl.getChildCount();i++){
                changeButtonColors(cl);
            }
        }
    }
    private static void changeButtonColors(ConstraintLayout li){
        for(int i = 0; i< li.getChildCount();i++){
            if(li.getChildAt(i) instanceof ConstraintLayout){
                li.getChildAt(i).setBackgroundResource(0);
                changeButtonColors((ConstraintLayout) li.getChildAt(i));
            }else if(li.getChildAt(i) instanceof EditText || li.getChildAt(i) instanceof Button){
                if(isUp && li.getChildAt(i).getId() != R.id.passButton){
                    li.getChildAt(i).setBackground(userFields);
                }else{
                    li.getChildAt(i).setBackgroundResource(0);
                }
            }

        }
    }
    public static void createChangePassPopUp(View v, ConstraintLayout main,Context appContext){
        createPopUpBase(v,main,appContext,R.layout.popup_change_pass,null);
    }
    public static void createErrorPopup(View v, ConstraintLayout main,Context appContext, String errorMsg){
        createPopUpBase(v,main,appContext,R.layout.popup_error,errorMsg);
    }
}
