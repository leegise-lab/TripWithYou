package com.example.tripwithyou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Fragment_enjoy extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_enjoy);
        uploadbt();

    }

    public void uploadbt() {
        //관리자 권한이면 랜드마크 정보 등록하는 버튼 나오게. 유저 정보는 로그인할 때 쉐어드에 저장
        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        String grade = sp.getString("grade", null);
        if (grade.equals("0")) {
            Button bt = (Button) findViewById(R.id.upload_bt);
            bt.setVisibility(View.VISIBLE);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Fragment_enjoy.this, Upload_enjoy.class);
                    startActivity(intent);
                }
            });
        }
    }

}
