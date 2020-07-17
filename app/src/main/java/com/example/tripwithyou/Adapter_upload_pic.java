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

public class Adapter_upload_pic extends RecyclerView.Adapter<Adapter_upload_pic.MyViewHolder> {

    private Context context;
    private ArrayList<Recycler_DTO_upload> datalist = new ArrayList<>();

    @Override
    //뷰홀더 생성
    //화면에 뿌려주고 holder에 연결
    public Adapter_upload_pic.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //뷰생성 (아이템 레이아웃 기반)
        //inflater는 뷰를 실제 객체화하는 용도
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_uploadpic_itemview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Adapter_upload_pic.MyViewHolder holder, int position) {
        //각 위치에 문자열 세팅
        Recycler_DTO_upload data = datalist.get(position);
//        holder.image.setImageURI(Uri.parse(data.getImg()));
        Log.d("onBindView","실행");
        Glide.with(holder.itemView.getContext()).load(data.getImg()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

//    public void additem(Recycler_DTO_upload dto) {
//        datalist.add(dto);
//    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        public MyViewHolder(View itemview) {
            super(itemview);
            Log.d("MyViewHolder", "실행");
            image = (ImageView)itemview.findViewById(R.id.upload_pic);
        }

    }

    //생성자
    public Adapter_upload_pic(Context context, ArrayList<Recycler_DTO_upload> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

}






