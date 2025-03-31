package com.example.dentraker;

import android.app.Application;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Globals extends Application {
    public static class User{
        private int idUsuario;
        private String name;
        private String username;
        private String password;
        private String bdate;
        private String email;
        private String phone;
        private boolean isInsp;
        private int inspCode;
        private Iterator<String> userKeys;
        public void setUserData(JSONObject user){
            try{
                idUsuario = user.getInt("idUsu");
                name = user.getString("nome");
                username = user.getString("username");
                password = user.getString("senha");
                email = user.getString("email");
                bdate = user.getString("nascimento");
                phone = user.getString("telefone");
                isInsp = user.getString("eInspetor").equals("1");
                if(isInsp){
                    inspCode=user.getInt("codInspetor");
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        public String getData(String value){
            switch (value){
                case "name":
                    return name;
                case "username":
                    return username;
                case "password":
                    return password;
                case "bdate":
                    return bdate;
                case "email":
                    return email;
                case "phone":
                    return phone;
            }
            return null;
        }
        public int getInspCode(){
            return inspCode;
        }
        public int getIdUsuario(){
            return idUsuario;
        }
        public boolean getIsInsp() {return isInsp;}
    }
    public static class Call{
       private int usuario,id;
       private String rua,bairro,numero,coordenadas,anexo,data,obs,status;
       public void setCallData(JSONObject call){
           try{
               id = call.getInt("idChamado");
               usuario = call.getInt("idUsu");
               rua = call.getString("rua");
               bairro = call.getString("bairro");
               numero = call.getString("numero");
               anexo = call.getString("anexo");
               data = call.getString("datachamado");
               obs = call.getString("observacoes");
               status = call.getString("statuschamado");
           }catch(JSONException e){
               e.printStackTrace();
           }
       }
       public String getValue(String val){
           switch (val){
               case "rua":
                   return rua;
               case "bairro":
                   return bairro;
               case "numero":
                   return numero;
               case "anexo":
                   return anexo;
               case "data":
                   return data;
               case "obs":
                   return obs;
               case "status":
                   return status;
               default:
                   throw new Error("Não foi possivel identificar esse campo de Chamado");
           }
       }
       public int getUsuario(){
           return usuario;
       }
       public int getId(){return id;}
   }
    private User usuario;
    private Call chamado;
    public User getUser(){
        return usuario;
    }
    public void setUser(JSONObject user){
        if(usuario == null){
            usuario = new User();
            usuario.setUserData(user);
        }else{
            usuario.setUserData(user);
        }
    }
    public void destroyUser(){
        usuario = null;
    }
    public Call getCall(){
        return chamado;
    }
    public void setCall(JSONObject call){
        if(chamado == null){
            chamado = new Call();
            chamado.setCallData(call);
        }else{
            chamado.setCallData(call);
        }
    }
    public void destroyCall(){
        chamado = null;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        usuario = new User();
        chamado = new Call();
    }
}
