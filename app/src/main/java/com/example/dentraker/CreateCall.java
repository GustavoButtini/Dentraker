package com.example.dentraker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dentraker.utils.DBControll;
import com.example.dentraker.utils.PopUpCreators;
import com.example.dentraker.utils.ServerMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

import java.util.Arrays;
import java.util.List;

public class CreateCall extends AppCompatActivity {
    private File img = null;
    private ConstraintLayout main;
    private LinearLayout imageGatter;
    private boolean isImageGathered = false;
    private StrictMode.ThreadPolicy basepolicy;
    private EditText cep,rua,bairro,numero,obs;
    private List<EditText> fields;
    private Uri pictureUri;
    private Globals globals;
    private Globals.User user;
    ActivityResultLauncher<String> galleryLauncher;
    ActivityResultLauncher<Uri> cameraLauncher;
    private boolean checkFields(){
        int filled = 0;
        for (EditText e: fields) {
            if(e.length() > 0){
                filled += 1;
            }else{
                e.setError("Preencha este campo !");
            }
        }
        return filled >= 4;
    }
    //Utillities
    private void cepFormater(Editable txt){
        cep.removeTextChangedListener(cepTextFormatter);
        String text = txt.toString().replace("-","");
        StringBuilder textBuilder = new StringBuilder(text);
        if(text.length() > 5){
            textBuilder.insert(5,"-");
        }
        txt.clear();
        txt.append(textBuilder);
        cep.setSelection(txt.length());
        cep.addTextChangedListener(cepTextFormatter);
    }
    private void openCamera(){
        img = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),System.currentTimeMillis()+".jpg");
        pictureUri = FileProvider.getUriForFile(this,getPackageName()+".provider",img);
        if(pictureUri != null){
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                img.delete();
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},100);
            }else{
                cameraLauncher.launch(pictureUri);
            }
        }
    }
    private void openImageChoices(){
        CharSequence[] options = {"Camera","Galeria","Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha como irá caputrar a imagem !");
        builder.setItems(options,(dialog,which) -> {
            switch (which){
                case 0:
                    openCamera();
                    break;
                case 1:
                    galleryLauncher.launch("image/*");
                    break;
                case 2:
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }
    @Override
    public void onRequestPermissionsResult(int code, String[] permissionsList, int[] grantResultsList){
        super.onRequestPermissionsResult(code,permissionsList,grantResultsList);
        if(code == 100){
            if(grantResultsList.length > 0 && grantResultsList[0] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            }else{
                PopUpCreators.createErrorPopup(main,main,this,"Precisamos da sua permisão para utilizar a camera !");
            }
        }
    }
    //Listeners
    private final TextWatcher cepTextFormatter = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        public void afterTextChanged(Editable s) {
            cepFormater(s);
        }
    };
    private final View.OnFocusChangeListener cepUsageControll = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            String truecep = cep.getText().toString().replace("-","");
            int ceptruelength = truecep.length();
            System.out.println(ceptruelength);
            if(!hasFocus && ceptruelength == 8){
                try{
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
                    URL url = new URL("https://viacep.com.br/ws/"+truecep+"/json");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputline;
                    StringBuilder obj = new StringBuilder();
                    JSONObject locationdata = null;
                    while((inputline=rd.readLine()) !=null){
                        obj.append(inputline);
                    }
                    try{
                        locationdata = new JSONObject(obj.toString());
                        if(locationdata.has("erro")){
                            cep.setError("Esse CEP não foi encontrado, tente novamente !");
                        }else{
                            if(locationdata.has("logradouro")){
                                rua.setText(locationdata.getString("logradouro"));
                                bairro.setText(locationdata.getString("bairro"));
                            }
                        }
                    }catch(JSONException e){
                        cep.setError(e.toString());
                    }

                }catch (IOException e){
                    cep.setError(e.toString());
                }
            }else if(ceptruelength != 8 && ceptruelength != 0){
                cep.setError("Insira 8 numeros no campo de CEP !");
            }
        }
    };
    private final View.OnClickListener imageGatherListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!isImageGathered){

            }
        }
    };
    private final View.OnClickListener sendDataToAPI = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(checkFields()){
                if(img != null){
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
                    String res = ServerMethods.saveImageOnServer(img);
                    if(res.equals("conerror")){
                        PopUpCreators.createErrorPopup(main,main,getApplicationContext(),"Não conseguimos inserir a foto no nosso servidor, aguarde para tentar novamente !");
                    }else{
                        try{
                            JSONObject imgData = new JSONObject(res);
                            if(imgData.has("Sucess")){
                                String path = "\\\\" + imgData.getString("Sucess").replace("/","\\\\");
                                JSONObject values = new JSONObject();
                                System.out.println(path);
                                values.put("idUsu",user.getIdUsuario());
                                values.put("rua",rua.getText().toString());
                                values.put("bairro",bairro.getText().toString());
                                values.put("numero",numero.getText().toString());
                                values.put("anexo",path);
                                values.put("data",LocalDate.now());
                                values.put("observacoes",obs.getText().toString());
                                System.out.println(values.toString());
                                JSONObject result = DBControll.createCall(values);
                                if(result.has("Sucess")){
                                    startActivity(new Intent(CreateCall.this,CallsPage.class));
                                    finish();
                                }
                            }else{
                                PopUpCreators.createErrorPopup(main,main,getApplicationContext(),"Houve algum erro com a sua imagem !");
                            }
                        }catch (JSONException | IOException e){
                            System.out.println(e);
                        }
                    }
                }
                StrictMode.setThreadPolicy(basepolicy);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageButton backpage;
        ImageView image;
        Button createCall;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_call);
        imageGatter = (LinearLayout) findViewById(R.id.picturelayout);
        image = (ImageView) findViewById(R.id.picture);
        cep = (EditText) findViewById(R.id.cep);
        rua = (EditText) findViewById(R.id.rua);
        bairro = (EditText) findViewById(R.id.bairro);
        numero = (EditText) findViewById(R.id.numero);
        obs = (EditText) findViewById(R.id.obs);
        createCall = (Button) findViewById(R.id.createcall);
        main = (ConstraintLayout) findViewById(R.id.main);
        backpage = (ImageButton) findViewById(R.id.backpage);
        globals = (Globals) getApplication();
        fields = Arrays.asList(rua,bairro,numero,obs);
        user = globals.getUser();
        basepolicy = StrictMode.getThreadPolicy();
        cep.setOnFocusChangeListener(cepUsageControll);
        cep.addTextChangedListener(cepTextFormatter);
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(),sucess->{
            if(sucess && pictureUri != null){
                image.setImageURI(pictureUri);
            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),result->{
            if (result != null) {
                image.setImageURI(result);
            }});
        imageGatter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChoices();
            }
        });
        backpage.setOnClickListener((v)->{
            startActivity(new Intent(CreateCall.this,CallsPage.class));
            finish();
        });
        createCall.setOnClickListener(sendDataToAPI);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}