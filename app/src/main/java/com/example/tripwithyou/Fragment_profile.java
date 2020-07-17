package com.example.tripwithyou;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Fragment_profile extends Fragment {
    public Fragment_profile(){ }
    ViewGroup view;
    TextView nick, name;
    ImageView img;
    private static String BASE_URL = "http://13.125.246.30/";
    private Call<Retrofit_result_profile> userList;
    private Retrofit mRetrofit;
    private Retrofit_set_profile retrofitInterface;
    private Gson mGson;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //쉐어드로 로그인 상태 확인인
        SharedPreferences sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String result = sp.getString("id", null); //s : 키에 담긴 값, s1 = 키에 아무값이 없으면 그 다음으로 넣어줄 값
        Log.d("SharedPreferences", ""+result);
        //로그인이 안된 상태
        if (result == null) {
            Log.d("null","비 로그인 상태");
            view = (ViewGroup) inflater.inflate(R.layout.fragment_profile_login, container, false);
            //비 로그인시 로그인 버튼 클릭
            Button bt = (Button)view.findViewById(R.id.profile_btn);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("버튼", "로그인 버튼 클릭");
                    Intent intent = new Intent(getActivity(), Login_choice.class);
                    startActivity(intent);
                }
            });
        }
        //로그인 된 상태
        else if (result != null) {
            Log.d("not null","로그인 상태");
            view = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);

            //로그인 시 프로필 수정 버튼 클릭
            Button bt = (Button)view.findViewById(R.id.profile_edit);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("버튼", "프로필 수정 버튼 클릭");
                    Intent intent = new Intent(getActivity(), Edit_profile.class);
                    startActivity(intent);
                }
            });
//            set_profile();
            logout();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume","실행");
        SharedPreferences sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String login = sp.getString("id",null);
        if (login == null) {
        } else if (login != null) {
            set_profile();
        }
    }

    //로그아웃 버튼 클릭
    public void logout() {
        Button button = (Button)view.findViewById(R.id.logout_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("로그아웃","버튼 클릭");
                SharedPreferences sp = getActivity().getSharedPreferences("login", getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

                Toast.makeText(getActivity(), "로그아웃 하였습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //프로필 보여주는 부분
    public void set_profile(){
        Log.d("프로필", "프로필 정보 세팅 완료");
        setRetrofitInit();
        calluserlist();

    }
    //레트로핏 객체생성
    protected void setRetrofitInit() {
        mGson = new GsonBuilder().setLenient().create();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .build();
        retrofitInterface = mRetrofit.create(Retrofit_set_profile.class);
    }

    protected void calluserlist() {
        SharedPreferences sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String id = sp.getString("id",null);
        userList = retrofitInterface.getlist(id);
        userList.enqueue(mRetrofitCallback);
    }
    protected Callback<Retrofit_result_profile> mRetrofitCallback = new Callback<Retrofit_result_profile>() {
        @Override
        public void onResponse(Call<Retrofit_result_profile> call, Response<Retrofit_result_profile> response) {

            img = getView().findViewById(R.id.profile_img);
            name = getView().findViewById(R.id.name);
            nick = getView().findViewById(R.id.nick);

            Retrofit_result_profile retrofit_result_profile = response.body();
            String setimg = retrofit_result_profile.getImg();
            String setnick = retrofit_result_profile.getNick();
            String setname = retrofit_result_profile.getName();
            String setgrade = retrofit_result_profile.getGrade();

            Log.d("img", ""+setimg);
            Log.d("nick", ""+setnick);
            Log.d("name", ""+setname);

            //db에 img 주소가 없을때 기본 이미지로 설정
            if (setimg == "" || setimg == null) {
                Log.d("setimg", "빈 셋img"+setimg);
//                Glide.with(getActivity()).load(R.drawable.ic_account_circle_black_24dp).into(img);
                name.setText(setname);
                nick.setText(setnick);
            } else if (setimg != "") {
                Log.d("setimg", setimg);
                //이미지뷰 동그랗게 만들기
                img.setBackground(new ShapeDrawable(new OvalShape()));
                img.setClipToOutline(true);
                //Glide을 이용해서 이미지뷰에 url에 있는 이미지를 세팅해줌
                Glide.with(getActivity()).load(setimg).into(img);
                name.setText(setname);
                nick.setText(setnick);
            }

            //쉐어드에 유저 정보 임시시저장
            SharedPreferences sp = getActivity().getSharedPreferences("user",Context.MODE_PRIVATE);
            SharedPreferences sp2 = getActivity().getSharedPreferences("login",Context.MODE_PRIVATE);
            String setid = sp2.getString("id", null);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("id",setid);
            editor.putString("img",setimg);
            editor.putString("nick",setnick);
            editor.putString("name",setname);
            editor.putString("grade",setgrade);
            editor.commit();

        }

        @Override
        public void onFailure(Call<Retrofit_result_profile> call, Throwable t) {
            t.printStackTrace();
        }
    };
}


