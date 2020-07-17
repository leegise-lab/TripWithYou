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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Fragment_home extends Fragment implements MainActivity.OnBackPressedListener {
    ViewGroup view;
    MainActivity activity;
    long backKeyPressedTime;
    Toast toast;
    private Fragment_enjoy fragment_enjoy;
    retrofit_home retrofitInterface;
    private Adapter_home adapter;
    private ArrayList<Recycler_DTO_home> list = new ArrayList<>();
//    private FragmentManager fragmentManager = getFragmentManager();
    public Fragment_home(){}

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
        adapter = new Adapter_home(getActivity(), list);
        recyclerView.setAdapter(adapter);
        activity = (MainActivity) getActivity();
        toast = Toast.makeText(getContext(),"한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT);
        Log.d("홈","홈 화면 onCreate");

        //홈화면 버튼 클릭이벤트
        hotelclick();
        enjoyclick();
        foodclick();

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
//        initDataset();
    }

    //리사이클러뷰에 데이터넣는거  지금은 하드코딩중
//    private void initDataset() {
//        //for Test
//        Log.d("initDataset","리사이클러뷰에 추가 실행행");
//        list.add(new Recycler_DTO_home("서울","경복궁",""));
//        list.add(new Recycler_DTO_home("대전","한빛탑",""));
//        list.add(new Recycler_DTO_home("대구","스파크랜드",""));
//        list.add(new Recycler_DTO_home("부산","더베이101",""));
//    }

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
                    Intent intent = new Intent(getActivity(), Fragment_enjoy.class);
                    startActivity(intent);
            }
        });
    }
    public void foodclick(){
        //음식점 클릭이벤트
        Button hotel = (Button)view.findViewById(R.id.enjoy);
        hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("버튼", "음식점 버튼 클릭 완료");
                Intent intent = new Intent(getActivity(), Fragment_food.class);
                startActivity(intent);
            }
        });
    }

    public void getrcvinfo() {
        SharedPreferences sp = getActivity().getSharedPreferences("where", Context.MODE_PRIVATE);
        String where = String.valueOf(sp.getAll().values());
        //어디로 가는지 설정 안해뒀으면
        if (where.equals("") || where.equals(null)) {
            //랜덤으로 관광지 뜨게 하기


        }
        //어디로 가는지 설정해뒀으면
        else if (!where.equals("") || !where.equals(null)) {
            //설정해둔 곳의 정보만 가져오기
        }

    }
}
