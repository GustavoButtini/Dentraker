package com.example.dentraker.components;

import android.util.Patterns;
import android.widget.EditText;

public class PassChangePopUp {
    public static boolean checkMail(EditText ed) {
        String mail = ed.getText().toString().trim();
        return Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }
}
