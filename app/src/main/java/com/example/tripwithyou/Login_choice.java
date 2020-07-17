package com.example.tripwithyou;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Login_choice extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_choice);
        login_click();

    }

    public void login_click(){
        Button button = (Button)findViewById(R.id.email_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login_email.class);
                startActivity(intent);
                Log.d("버튼", "이메일 계정으로 이용하기 버튼 클릭");
            }
        });
    }
}
