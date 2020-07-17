package com.example.tripwithyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login_email extends AppCompatActivity {

    private static String BASE_URL = "http://13.125.246.30/";
    private Call<Retrofit_result> userList;
    private Retrofit mRetrofit;
    private Retrofit_login retrofitInterface;
    private Gson mGson;

    private EditText eid,epw;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_email);
        eid = (EditText)findViewById(R.id.email_edit);
        epw = (EditText)findViewById(R.id.pw_edit);

        login();
        signup();

    }
    //회원가입버튼 클릭 이벤트
    public void signup() {
        TextView textView = (TextView)findViewById(R.id.sign_up);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("버튼", "회원가입 버튼 클릭");
                Intent sign_up = new Intent(getApplicationContext(), Sign_up.class);
                startActivity(sign_up);
            }
        });
    }

    //로그인 버튼 클릭이벤트
    public void login() {
        Button button = (Button)findViewById(R.id.login_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("버튼", "로그인 버튼 클릭");
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
        retrofitInterface = mRetrofit.create(Retrofit_login.class);
    }
    protected void calluserlist() {
        String id = eid.getText().toString();
        String pw = epw.getText().toString();
        userList = retrofitInterface.getlist(id, pw);
        userList.enqueue(mRetrofitCallback);
    }
    protected Callback<Retrofit_result> mRetrofitCallback = new Callback<Retrofit_result>() {
        @Override
        public void onResponse(Call<Retrofit_result> call, Response<Retrofit_result> response) {
            Retrofit_result retrofit_result = response.body();
            String result = retrofit_result.getResult();
            Log.d("결과", result);
            if (result.equals("success")) {
                Log.d("로그인", "로그인 완료");

                //sp 써서 로그인.
                SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);

                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                SharedPreferences.Editor editor = sp.edit();
                String id = eid.getText().toString(); // 사용자가 입력한 저장할 데이터
                editor.putString("id",id);
                editor.commit();


                //메인으로 보냄
                Intent intent = new Intent(Login_email.this, MainActivity.class);
                startActivity(intent);
                //다른 액티비티 전부 종료
                ActivityCompat.finishAffinity(Login_email.this);
                Toast.makeText(Login_email.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
            } else if (result.contains("fail")) {
                Log.d("로그인", "로그인 실패");
                Toast.makeText(Login_email.this, "아이디 혹은 비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onFailure(Call<Retrofit_result> call, Throwable t) {
            t.printStackTrace();
        }
    };
}
