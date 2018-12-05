package com.example.daygram_copy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
/*
* 年和月适配器
* */
public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {
    private List<Month> MonthList;
    private onItemClick listener;


    public interface onItemClick{
        void onClick(View v,int i);
    }

    public void onClickListener(onItemClick listener){
        this.listener = listener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView MonthName;
        public ViewHolder(View view){
            super(view);
            MonthName=view.findViewById(R.id.month_name);
        }
    }

    public MonthAdapter(List<Month> monthList){
        MonthList=monthList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.month_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.MonthName.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            int position=holder.getAdapterPosition();
            listener.onClick(view,position);
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Month month=MonthList.get(position);
        holder.MonthName.setText(month.getMonth());
    }
    @Override
    public int getItemCount(){
        return MonthList.size();
    }


}
