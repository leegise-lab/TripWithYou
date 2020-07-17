package com.example.tripwithyou;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Adapter_home_horizontal extends RecyclerView.Adapter<Adapter_home_horizontal.MyViewHolder> {

    private Context context;
    private String[] img;
    //private ArrayList<Recycler_DTO_home> datalist;

    /*
    * 1) 이미지 분리 (어쨌든 이 값을 어딘가의 DTO에서 갖고 있어야하잖아)
    * 2) 분리된 이미지 배열을 어딘가의 DTO에 저장을 해 : recylcer_DTO_home
    * 3) 리싸이클러뷰를 만들어 (생성만 해)
    * 4) 생성한 리싸이클러뷰안에 !이미지 배열을 저장해놨던 DTO안의! 이미지 배열을 가지고와
    * 5) 가지고온 이미지 배열을 바탕으로 ImageView 혹은 ScrollView를 생성해
    * 6) 실행!
    * */
    @Override
    //뷰홀더 생성
    //화면에 뿌려주고 holder에 연결
    public Adapter_home_horizontal.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //뷰생성 (아이템 레이아웃 기반)
        //inflater는 뷰를 실제 객체화하는 용도
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_home_horizontal_itemview, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Adapter_home_horizontal.MyViewHolder holder, int position) {
        //각 위치에 문자열 세팅
        //final Recycler_DTO_home data = datalist.get(position);

//        Glide.with(holder.image.getContext()).load(img[position]).into(holder.image);
        Log.d("온바인뷰","실행");
        Glide.with(holder.itemView.getContext()).load(img[position]).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return (null != img ? img.length : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        public MyViewHolder(View itemview) {
            super(itemview);
            Log.d("마이뷰홀더","실행");

            image = (ImageView)itemview.findViewById(R.id.landmark_img);
            Log.d("","실행");
        }

    }
    public Adapter_home_horizontal(Context context, String[] img) {
        this.context = context;
        this.img = img;
    }

}






