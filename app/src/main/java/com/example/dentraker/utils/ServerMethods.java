package com.example.dentraker.utils;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ServerMethods {
    private static final String serverBoundary = "----WebKitFormBoundary" + System.currentTimeMillis();
    private static HttpURLConnection connectToServer(String function) throws IOException {
        URL url = new URL("http://192.168.15.9/dentraker/serverMethods/"+function);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        if(function.equals("imageSave")){
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("accept","application/json");
            con.setRequestProperty("Content-Type","multipart/form-data; boundary="+serverBoundary);
        }
        return con;
    }
    public static LatLng getAddressCoords(String address) throws IOException{
        URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address="+address+",Botucatu - SP&key=AIzaSyBnRHIXn7ehorQBGtOFUR_qboAGqKkmAKQ");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        JSONObject res = null;
        StringBuilder resbuilder = new StringBuilder();
        String resline;
        while((resline= rd.readLine()) != null){
            resbuilder.append(resline);
        }
        try{
            res = new JSONObject(resbuilder.toString());
            System.out.println(res.getString("status"));
            if(res.has("results") && res.getString("status").equals("OK")){
                JSONArray resarray = new JSONArray(res.getString("results"));
                res = resarray.getJSONObject(0);
                JSONObject finalres = res.getJSONObject("geometry").getJSONObject("location");
                System.out.println(finalres.get("lat"));
                return new LatLng((double) finalres.get("lat"),(double) finalres.get("lng"));
            }else{
                return null;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }finally {
            con.disconnect();
            rd.close();
        }
        return null;
    };
    public static String saveImageOnServer(File img){
        HttpURLConnection con;
        StringBuilder response;
        try{
            con = connectToServer("imageSave");
            try(OutputStream sender = con.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(sender, StandardCharsets.UTF_8), true)){
                    writer.append("--").append(serverBoundary).append("\r\n");
                    writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"").append(img.getName()).append("\"").append("\r\n");
                    writer.append("Content-Type: ").append(Files.probeContentType(img.toPath())).append("\r\n");
                    writer.append("\r\n").flush();
                    Files.copy(img.toPath(), sender);
                    sender.flush();
                    writer.append("\r\n").flush();
                    writer.append("--").append(serverBoundary).append("--").append("\r\n").flush();
                response = new StringBuilder();
                BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputline;
                while((inputline = rd.readLine()) != null){
                    response.append(inputline);
                }
            }
        }catch(IOException e){
            System.out.println(e);
            return "conerror";
        }
        return response.toString();
    }
}
