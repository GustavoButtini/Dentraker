package com.example.dentraker.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DBControll {
    private static String baseUrl="http://192.168.15.9/dentraker/";
    private static BufferedReader rd;
    //Metodos Publicos
    public static JSONObject loginUser(String user, String pass) throws IOException {
        byte[] params = ("username="+user+"&pass="+pass).getBytes(StandardCharsets.UTF_8);
        return returnPostResultAsJson(setPostCon("http://192.168.15.9/dentraker/usuario/login",params),params);
    }
    public static JSONObject createUser(JSONObject formData) throws IOException{
        byte[] postdata = settingParamData(formData).getBytes(StandardCharsets.UTF_8);
        return DBControll.returnPostResultAsJson(DBControll.setPostCon(baseUrl+"insert/usuario",postdata), postdata);
    };
    public static boolean checkFetchError(String msg,SQLERRORS erro){
        return !msg.equals(erro.getErrormsg());
    }
    public static Object getPoints(Integer id) throws IOException{
        HttpURLConnection con;
        if(id == null){
            con = DBControll.setGETCon("http://192.168.15.9/dentraker/chamados");
        }
        else{
            con = DBControll.setGETCon("http://192.168.15.9/dentraker/chamados/usuario/"+id);
        }
        return DBControll.getValueGet(con);
    }
    public static Object getComments(Integer id) throws  IOException{
        HttpURLConnection con;
        if(id == null){
            con = setGETCon("http://192.168.15.9/dentraker/comentarios/chamados/");
        }
        else{
            con = setGETCon("http://192.168.15.9/dentraker/comentarios/chamado/"+id);
        }
        return DBControll.getValueGet(con);
    }
    public static Object getCommentsWithNames(Integer id)throws IOException{
        return getValueGet(setGETCon(baseUrl+"comentarios/comentadores/chamado/"+id));
    }
    public static JSONObject setCallStatus(String status,Integer callid) throws IOException{
        byte[] params = ("id="+callid+"&status="+status).getBytes(StandardCharsets.UTF_8);
        return returnPostResultAsJson(setPostCon(baseUrl+"chamados/changestatus",params),params);
    }
    public static JSONObject createCall(JSONObject data) throws IOException {
        byte[] postdata = settingParamData(data).getBytes();
        return returnPostResultAsJson(setPostCon(baseUrl+"insert/chamado",postdata),postdata);
    };
    public static JSONObject createComment(JSONObject data) throws IOException{
        byte[] params = settingParamData(data).getBytes(StandardCharsets.UTF_8);
        return returnPostResultAsJson(setPostCon(baseUrl+"insert/comment",params),params);
    };
    //Metodos Privados
    private static String settingParamData(JSONObject fullData){
        Iterator<String> keys = fullData.keys();
        StringBuilder bl = new StringBuilder();
        while(keys.hasNext()){
            String key = keys.next();
            try{
                bl = bl.append(key).append("=").append(fullData.getString(key)).append("&");
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        bl.replace(bl.length()-1,bl.length()+1,"");
        return bl.toString();
    };
    private static HttpURLConnection setPostCon(String urlStr, byte[] paramsStr) throws  IOException{
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("accept","application/json");
        con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        con.setRequestProperty("charset","utf-8");
        con.setRequestProperty("Content-Length",Integer.toString(paramsStr.length));
        con.setDoOutput(true);
        con.setInstanceFollowRedirects(false);
        return con;
    }
    private static JSONObject returnPostResultAsJson(HttpURLConnection con, byte[] params) throws IOException{
        try(DataOutputStream dos = new DataOutputStream(con.getOutputStream())){
            dos.write(params);
            rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputline;
            StringBuilder bf = new StringBuilder();
            JSONObject res = new JSONObject();
            int cont = 0;
            while ((inputline = rd.readLine()) != null) {
                bf.append(inputline);
            }
            try {
                res = (JSONObject) new JSONObject(bf.toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }finally {
                rd.close();
                con.disconnect();
            }
            return (res);
        }
    }
    private static HttpURLConnection setGETCon(String urlStr) throws IOException{
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(2000);
        con.setReadTimeout(2000);
        con.setRequestProperty("accept", "application/json");
        con.setDoOutput(true);
        con.setInstanceFollowRedirects(false);
        return con;
    }
    private static Object getValueGet(HttpURLConnection con) throws IOException{
        rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputline;
        StringBuffer bf = new StringBuffer();
        JSONArray res = null;
        while ((inputline = rd.readLine()) != null){
            bf.append(inputline);
        }
        try{
            return new JSONArray(bf.toString());
        }catch(JSONException notarray){
            try{
                return new JSONObject(bf.toString());
            }catch(JSONException notjson){
                return null;
            }
        }
    }
}