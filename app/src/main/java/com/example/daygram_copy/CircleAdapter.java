package com.example.daygram_copy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;


import java.util.List;
/*自定义适配器*/
public class CircleAdapter extends ArrayAdapter {
    private int resourceId;
    public CircleAdapter(Context context, int textViewResourceId, List<Circle> objects){
        super(context,textViewResourceId,objects);
        //将ListView下面的布局文件存起来
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Circle circle = (Circle) getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            //使用LyaoutInflate为子项传入布局
            view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            //将控件进行缓存
            viewHolder.CircleImage=(ImageView)view.findViewById(R.id.cicrle_image);
            view.setTag(viewHolder);
        }else {//如果有布局文件的话
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        //设置数据
        viewHolder.CircleImage.setImageResource(circle.getImageId());
        return view;
    }
    //缓存类
    class ViewHolder{
        ImageView CircleImage;
    }
}
