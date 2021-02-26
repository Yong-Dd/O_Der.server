package com.yongdd.o_der_re.server;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogIn extends AppCompatActivity {


    TextView emailText, pwText;
    ConstraintLayout loadingLayout;

    final String TAG = "logIn";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        emailText = findViewById(R.id.emailText);
        pwText = findViewById(R.id.pwText);
        loadingLayout = findViewById(R.id.loadingLayout);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                //로그인 준비
                String email = emailText.getText().toString().trim();
                String password = pwText.getText().toString().trim();

                if(isValidEmail(email)){
                    //로딩 레이아웃 설정
                    loadingLayout.setVisibility(View.VISIBLE);
                    ProgressBar proBar = (ProgressBar) findViewById(R.id.progressBar);
                    if (proBar != null) {
                        proBar.setIndeterminate(true);
                        proBar.setIndeterminateTintList(ColorStateList.valueOf(Color.rgb(43,144,217)));
                    }
                    //로그인 진행
                    logIn(email,password);
                }else{
                    Toast.makeText(LogIn.this,"이메일 형식을 확인해주세요.\n(xxx@order.com)",Toast.LENGTH_LONG).show();;
                }
            }
        });
    }

    public static boolean isValidEmail(String email) {
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@oder.com";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()) {
            err = true; }
        return err;
    }

    private void logIn(String email, String password){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG,"logIn completed");

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    if (user != null) {
                                        Toast.makeText(getApplicationContext(),"로그인 완료.",Toast.LENGTH_SHORT).show();

                                        String name = user.getDisplayName();

                                       if(name==null){
                                           name = "";

                                           loadingLayout.setVisibility(View.GONE);
                                           Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                           intent.putExtra("name",name);
                                           startActivity(intent);
                                           overridePendingTransition(R.anim.page_slide_in_left,R.anim.page_slide_out_right);

                                       }else{
                                           loadingLayout.setVisibility(View.GONE);

                                           Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                           intent.putExtra("name",name);
                                           startActivity(intent);
                                           overridePendingTransition(R.anim.page_slide_in_left,R.anim.page_slide_out_right);
                                       }
                                    }


                                }else{
                                    Log.d(TAG,"logIn failed");
                                    loadingLayout.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),"아이디와 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"fail : "+e);
            }
        });
    }




}
