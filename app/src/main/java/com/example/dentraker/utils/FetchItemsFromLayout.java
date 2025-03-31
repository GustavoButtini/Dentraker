package com.example.dentraker.utils;


import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

public class FetchItemsFromLayout {

    public static List<Button> getButtonsFromRoot(ConstraintLayout ly){
        List<Button> li = new ArrayList<>();
        for (int i = 0; i < ly.getChildCount();i++){
            if(ly.getChildAt(i) instanceof Button){
                li.add((Button) ly.getChildAt(i));
            }
        }
        return li;
    };
}
