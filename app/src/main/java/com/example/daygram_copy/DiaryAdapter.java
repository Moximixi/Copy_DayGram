package com.example.daygram_copy;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/*
* 日记显示列表适配器
* */
public class DiaryAdapter extends ArrayAdapter {
    private int resourceId;
    public DiaryAdapter(Context context, int textViewResourceId, List<DiaryShow> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DiaryShow diaryShow = (DiaryShow) getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.day=(TextView)view.findViewById(R.id.diary_day);
            viewHolder.week=(TextView)view.findViewById(R.id.diary_week);
            viewHolder.content=(TextView)view.findViewById(R.id.diary_content);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }

        viewHolder.day.setText(diaryShow.getDay());
        viewHolder.week.setText(diaryShow.getWeek());
        viewHolder.content.setText(diaryShow.getContent());
        //判断是否为星期天
        if(viewHolder.week.getText().toString().equals("Sunday")){
            viewHolder.week.setTextColor(Color.rgb(255, 0, 0));
        }else{
            viewHolder.week.setTextColor(Color.rgb(0, 51, 0));
        }
        return view;
    }
    class ViewHolder{
        TextView day;
        TextView week;
        TextView content;
    }
}

