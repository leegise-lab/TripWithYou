package com.example.tripwithyou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Fragment_home extends Fragment implements MainActivity.OnBackPressedListener {
    ViewGroup view;
    MainActivity activity;
    long backKeyPressedTime;
    Toast toast;
    private Call<Retrofit_result_home> userList;
    Retrofit_home retrofitInterface;
    private Adapter_home adapter;
    private ArrayList<Recycler_DTO_home> datalist = new ArrayList<>();
    private ArrayList<Recycler_DTO_home_horizontal> datalist_horizontal = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        Log.d("onCreateView", "fragment_home 실행");
        view = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.home_rcv);
        //아이템 보여주는거 크기 일정하게
        recyclerView.setHasFixedSize(true);
        //리사이클러뷰에 리사이클러뷰 매니저붙임
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new Adapter_home(getActivity(), datalist);
        recyclerView.setAdapter(adapter);
        activity = (MainActivity) getActivity();
        toast = Toast.makeText(getContext(),"한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT);
        Log.d("홈","홈 화면 onCreate");

        hotelclick();
        enjoyclick();


        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);
        Log.d("onActivityCreated","fragment_home 실행");
    }

    @Override
    public void onCreate(@Nullable Bundle saveInstanceState){
        Log.d("onCreate","fragment_home 실행");
        super.onCreate(saveInstanceState);
        getRcvinfo();
    }


    @Override
    public void onBack(){ }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onBackPressed(){
        //터치간 시간을 줄이거나 늘리고 싶다면 2000을 원하는 시간으로 변경해서 사용하시면 됩니다.
        if(System.currentTimeMillis() > backKeyPressedTime + 2000){
            backKeyPressedTime = System.currentTimeMillis();
            toast.show();
            return;
        } if(System.currentTimeMillis() <= backKeyPressedTime + 2000){
            getActivity().finish();
            toast.cancel();
        }
    }
    public void hotelclick(){
        //호텔버튼 클릭이벤트
        Button hotel = (Button)view.findViewById(R.id.hotel);
        hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("버튼", "호텔 버튼 클릭 완료");
                Intent intent = new Intent(getActivity(), Fragment_hotel.class);
                startActivity(intent);
            }
        });
    }
    public void enjoyclick(){
        //즐길거리 클릭이벤트
        Button hotel = (Button)view.findViewById(R.id.enjoy);
        hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("버튼", "즐길거리 버튼 클릭 완료");
//                Intent intent = new Intent(getActivity(), sample.class);
                //관리자 권한이면 랜드마크 정보 등록하는 버튼 나오게
                Intent intent = new Intent(getActivity(), Fragment_enjoy.class);
                startActivity(intent);
            }
        });
    }
    public void getRcvinfo() {
        Log.d("getRCVinfo", "리사이클러뷰 세팅");
        SharedPreferences sp = getActivity().getSharedPreferences("where", Context.MODE_PRIVATE);
        String where = String.valueOf(sp.getAll().values());
        Log.d("where", where);
        //어디로 가는지 설정 안해뒀으면
        if (where.equals("") || where.equals(null) || where.equals("[]")) {
            //랜덤으로 관광지 뜨게 하기
            setRetrofitInit();
            calluserlist();
        }
        //어디로 가는지 설정해뒀으면
        else if (!where.equals("") || !where.equals(null)) {
            //설정해둔 곳의 정보만 가져오기
        }

    }

    //레트로핏 부분
    protected void setRetrofitInit() {
        Gson mGson = new GsonBuilder().setLenient().create();
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("http://13.125.246.30/")
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .build();

        retrofitInterface = mRetrofit.create(Retrofit_home.class);
    }
    protected void calluserlist() {
        String send = "send";
        userList = retrofitInterface.getlist(send);
        userList.enqueue(mRetrofitCallback);
    }
    protected Callback<Retrofit_result_home> mRetrofitCallback = new Callback<Retrofit_result_home>() {
        @Override
        public void onResponse(Call<Retrofit_result_home> call, Response<Retrofit_result_home> response) {
            Retrofit_result_home retrofit_result = response.body();
            System.out.println("결과값:"+response.body().toString());
            String result = retrofit_result.getResult();
            //JSONArray json = retrofit_result.getArray();
            //Log.d("json", ""+json);

            Log.d("결과", result);
            if (result.contains("success")) {
                //관리자가 즐길거리 업로드해놓은 정보 홈화면에 뿌리는 부분
                List<Recycler_DTO_home_Vertical> resultList = retrofit_result.getArray();
                if(resultList != null && !resultList.equals("") && resultList.size() > 0){
                    for(int i = 0; i < resultList.size(); i++){
                        String country = resultList.get(i).getCountry();
                        String photo = resultList.get(i).getPhoto();
                        String[] photoArray = photo.split(", ");
                        String landmark = resultList.get(i).getName();
                        datalist.add(new Recycler_DTO_home(country, landmark, photoArray));
                        datalist_horizontal.add(new Recycler_DTO_home_horizontal(photoArray));
                    }
                } else {
                    System.out.println("리스트가 없습니다.");
                }
            } else if (result.contains("fail")) {

            }
        }
        @Override
        public void onFailure(Call<Retrofit_result_home> call, Throwable t) {
            t.printStackTrace();
        }
    };
}
