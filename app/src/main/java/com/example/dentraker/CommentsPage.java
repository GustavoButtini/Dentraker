package com.example.dentraker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dentraker.utils.DBControll;
import com.example.dentraker.utils.PopUpCreators;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;

public class CommentsPage extends AppCompatActivity {
    private StrictMode.ThreadPolicy basepolicy;
    private EditText commentary;
    private LinearLayout commentaries;
    private Button comment;
    private ImageButton backpage;
    private Globals globals;
    private Globals.Call call;
    private Globals.User user;
    private ConstraintLayout main;
    private boolean isfirstfocus = false;
    @SuppressLint("SetTextI18n")
    private void setComments(LinearLayout layout){
        try{
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
            Object fetchresult = DBControll.getCommentsWithNames(call.getId());
            if(fetchresult.getClass() == JSONArray.class){
                JSONArray commentslist = (JSONArray) fetchresult;
                for(int i = 0; i < commentslist.length(); i++){
                    JSONObject lineData = commentslist.getJSONObject(i);
                    System.out.println(lineData);
                    LayoutInflater inflater = LayoutInflater.from(this);
                    TextView lineView = (TextView) inflater.inflate(R.layout.comment_view,main,false);
                    lineView.setText(lineData.getString("nome_usuario") + " - " + lineData.getString("datacomentario") + "\n"+lineData.getString("comentario"));
                    lineView.setMaxLines(2);
                    layout.addView(lineView);
                }
            }else if(fetchresult.getClass() == JSONObject.class){
                JSONObject obj = (JSONObject) fetchresult;
                if(obj.has("Error")){
                    PopUpCreators.createErrorPopup(main,main,this,"Erro ao coletar os dados no servidor");
                }
                if(obj.has("NoData")){
                    System.out.println("Has no Data");
                    layout.setGravity(Gravity.CENTER);
                    TextView errorline = new TextView(this);
                    errorline.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    errorline.setTextColor(0xee000000);
                    errorline.setTextSize(18);
                    errorline.setText("Sem comentarios nesse ponto, comente logo abaixo !");
                    errorline.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    layout.addView(errorline);
                }
            }
        }catch(IOException dbe){
            System.out.println(dbe);
            startActivity(new Intent(CommentsPage.this,CallData.class));
            finish();
        }catch(JSONException jse){
            System.out.println(jse);
            startActivity(new Intent(CommentsPage.this,CallData.class));
            finish();
        }finally {
            StrictMode.setThreadPolicy(basepolicy);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comments_page);
        basepolicy = StrictMode.getThreadPolicy();
        globals = (Globals) getApplication();
        call = globals.getCall();
        user = globals.getUser();
        main = (ConstraintLayout) findViewById(R.id.main);
        commentaries = (LinearLayout) findViewById(R.id.commentslayout);
        commentary = (EditText) findViewById(R.id.commentary);
        comment = (Button) findViewById(R.id.comment);
        backpage = (ImageButton) findViewById(R.id.backpage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setComments(commentaries);
        commentary.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus && !isfirstfocus){
                commentary.setText("");
                isfirstfocus = true;
            }
        });
        comment.setOnClickListener((v)->{
            if(commentary.length() > 0 && !commentary.getText().toString().equals("Comentario")){
                try {
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
                    JSONObject data = new JSONObject();
                    data.put("idUsu",user.getIdUsuario());
                    data.put("comentario",commentary.getText().toString());
                    data.put("datacomentario", LocalDate.now());
                    data.put("idChamado",call.getId());
                    JSONObject res = DBControll.createComment(data);
                    if(res.has("Sucess")){
                        Intent intent = getIntent();
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0,0);
                    }else{
                        PopUpCreators.createErrorPopup(main,main,this,res.getString("Error"));
                    }
                }catch(JSONException jsE){
                    PopUpCreators.createErrorPopup(main,main,this,"Erro em criar seu comentario, tente novamente");
                }catch(IOException dbE){
                    PopUpCreators.createErrorPopup(main,main,this,"Erro ao conectar ao banco de dados");
                }finally {
                    StrictMode.setThreadPolicy(basepolicy);
                }
            }
        });
        backpage.setOnClickListener((v)->{
            startActivity(new Intent(CommentsPage.this,CallData.class));
            finish();
        });
    }
}