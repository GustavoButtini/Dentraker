package com.example.dentraker.utils;

public enum SQLERRORS {
    WRONG_PASS("{\"Error\":\"wrongpass\"}"),
    FETCH_ERROR("{\"Error\":\"fetcherror\"}");
    private String errormsg;
    SQLERRORS(String errormsg){
        this.errormsg = errormsg;
    }
    public String getErrormsg(){
        return this.errormsg;
    }
}
