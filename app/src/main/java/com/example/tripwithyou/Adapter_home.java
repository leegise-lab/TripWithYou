package com.example.tripwithyou;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class Adapter_home extends RecyclerView.Adapter<Adapter_home.MyViewHolder> {

    private Context context;
    private ArrayList<Recycler_DTO_home> datalist = new ArrayList<>();

    @Override
    //뷰홀더 생성
    //화면에 뿌려주고 holder에 연결
    public Adapter_home.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //뷰생성 (아이템 레이아웃 기반)
        //inflater는 뷰를 실제 객체화하는 용도
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_home_itemview, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Adapter_home.MyViewHolder holder, int position) {
        //각 위치에 문자열 세팅
        final Recycler_DTO_home data = datalist.get(position);
        holder.city.setText(data.getCity());
        holder.randmark.setText(data.getRandmark());
        //Recycler_DTO_home dto;
        //Adapter_home_horizontal adapter = new Adapter_home_horizontal(context, datalist.get(position));

        Adapter_home_horizontal adapter = new Adapter_home_horizontal(context, data.getImg());
        holder.rv.setHasFixedSize(true);
        holder.rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.rv.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView city;
        TextView randmark;
        RecyclerView rv;

        public MyViewHolder(View itemview) {
            super(itemview);

            city = (TextView)itemview.findViewById(R.id.city_text);
            randmark = (TextView)itemview.findViewById(R.id.randmark_text);
            rv = (RecyclerView)itemview.findViewById(R.id.recyclerview);
        }

    }
    public Adapter_home(Context context, ArrayList<Recycler_DTO_home> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

}






