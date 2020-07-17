package com.example.tripwithyou;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Sign_up extends AppCompatActivity {

    private final String BASE_URL = "http://13.125.246.30/";
    private Call<Retrofit_result> userList;
    private Retrofit mRetrofit;
    private Retrofit_sign_up Retrofit_sign_up;
    private Gson mGson;
    private EditText eid, epw, enick, ename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        eid = (EditText) findViewById(R.id.email_edit);
        epw = (EditText) findViewById(R.id.pw_edit);
        enick = (EditText) findViewById(R.id.nickname_edit);
        ename = (EditText) findViewById(R.id.name_edit);

        //회원가입 버튼 클릭시
        Button sign_up = (Button) findViewById(R.id.sign_up_bt);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRetrofitInit();
                calluserlist();
            }
        });
    }
    //레트로핏 객체생성
    protected void setRetrofitInit() {
        mGson = new GsonBuilder().setLenient().create();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .build();
        Retrofit_sign_up = mRetrofit.create(Retrofit_sign_up.class);
    }

    protected void calluserlist() {
        String id = eid.getText().toString();
        String pw = epw.getText().toString();
        String nick = enick.getText().toString();
        String grade = "1";
        String name = ename.getText().toString();

        userList = Retrofit_sign_up.getlist(id, pw, nick, grade,name);
        userList.enqueue(mRetrofitCallback);
    }

    protected Callback<Retrofit_result> mRetrofitCallback = new Callback<Retrofit_result>() {
        @Override
        public void onResponse(Call<Retrofit_result> call, Response<Retrofit_result> response) {
            Retrofit_result retrofit_result = response.body();
            String result = retrofit_result.getResult();
            Log.d("결과", result);
            if (result.equals("empty")) {
                Log.d("계정", "공백");
                Toast.makeText(Sign_up.this, "공백이 있는 항목이 있습니다.", Toast.LENGTH_SHORT).show();
            } else if (result.equals("cant")) {
                Log.d("계정", "사용 불가");
                Toast.makeText(Sign_up.this, "사용할 수 없는 아이디입니다.", Toast.LENGTH_SHORT).show();
            } else if (result.equals("success")) {
                Log.d("계정", "가입 완료");
                Toast.makeText(Sign_up.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else if (result.contains("fail")) {
                Log.d("계정", "가입 실패");
                Toast.makeText(Sign_up.this, "회원가입을 실패했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        @Override
        public void onFailure(Call<Retrofit_result> call, Throwable t) {
            t.printStackTrace();
        }
    };
}