package com.example.daygram_copy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class EditorActivity extends AppCompatActivity {
    private static int week;//int型星期数
    private FrameLayout buttonBack;//小横条
    private LinearLayout editorFooterDone;
    private TextView buttonEditDone;//完成按钮
    private TextView buttonInsertTime;//插入时间按钮
    private TextView buttonDelete;
    private EditText editorText;//编辑界面
    private String TimeString;//当前时间
    private TextView editorHeaderTitle;//头部标题
    private String TitleTime;//头部时间
    private String year_string;//字符串型年份
    private String month_string;//字符串型月份
    private String day;//字符串型天数
    private int year;//int型年份
    private int month;//int型月份
    private int day_int;//int型天数
    private String tag="EditorActivity";//方便调试
    private String week_day;//字符型星期数
    private String value;//填入的内容

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.both_editor);
        //隐藏标题和全屏显示
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //初始化控件
        initView();
        //为控件初始化并添加监听器
        setListener();
        //点击小圈圈获得当前时间
        CirceTime();
        //设置标题时间
        TitleTime();
        //判断数据库中是否有当天的数据
        ExitData();

    }
    /*
    * 以下为各类方法
    * */

    //判断数据库中是否有当天的数据
    private void ExitData() {
        List<Diary> diaries=DataSupport.findAll(Diary.class);
        for(Diary diary:diaries){
            if(year_string.equals(diary.getYear())&&month_string.equals(diary.getMonth())&&day.equals(diary.getDay())){
                editorText.setText(diary.getContent());//如果有，则填入数据
                break;//更新
            }
        }
    }
    //左下角添加小时钟
    private void CirceTime() {
        Calendar cal= Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
        Date currentLocalTime=cal.getTime();
        DateFormat time= new SimpleDateFormat("HH:mm a");
        TimeString=time.format(currentLocalTime)+" ";
    }
    //设置标题时间
    private void TitleTime() {
        //通过主页面传过来的天数
        Intent intent=getIntent();
        String day_int_string=intent.getStringExtra("day_time1");//从列表2传过来的日子
        if(day_int_string==null) {
            day = intent.getStringExtra("day_time");//从列表1传过来的日子
            day_int=Integer.parseInt(day);
            day_int+=1;//获得int类型的日子数，并且记得后面要+1
          //再把string类型的天数+1
        }else {
            day_int=Integer.parseInt(day_int_string);
        }
        year_string=intent.getStringExtra("year_time");
        month_string=intent.getStringExtra("month_time");

        //更新字符型年月日
        year=Integer.parseInt(year_string);
        month=Integer.parseInt(month_string);
        day=String.valueOf(day_int);

        CalculateWeekDay(year,month,day_int);//获得星期数
        change_week_month();
        //拼接起来
        TitleTime=week_day+"/"+month_string+" "+day_int+"/"+year;//设置格式
        editorHeaderTitle.setText(TitleTime);
    }
    //初始化控件
    private void initView() {
        buttonBack=findViewById(R.id.buttonBack);
        buttonEditDone=findViewById(R.id.buttonEditDone);
        buttonInsertTime=findViewById(R.id.buttonInsertTime);
        buttonDelete=findViewById(R.id.buttonDelete);
        editorText=findViewById(R.id.editorText);
        editorHeaderTitle=findViewById(R.id.editorHeaderTitle);
        editorFooterDone=findViewById(R.id.editorFooterDone);
    }
    //为控件初始化并添加监听器
    private void setListener() {
        BackHome backhome=new BackHome();
        InsertTime insertTime=new InsertTime();
        InsertValue insertValue=new InsertValue();
        DeleteData deleteData=new DeleteData();

        buttonBack.setOnClickListener(backhome);
        buttonEditDone.setOnClickListener(insertValue);
        buttonInsertTime.setOnClickListener(insertTime);
        buttonDelete.setOnClickListener(deleteData);
        editorText.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    editorText.setCursorVisible(true);// 再次点击显示光标
                    editorFooterDone.setVisibility(View.VISIBLE);
                    buttonBack.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });
    }
    //通过年月日获得星期数
    public static void  CalculateWeekDay(int y, int m, int d) {
        if (m == 1 || m == 2) {
            m += 12;
            y--;
        }
        int iWeek = (d + 2 * m + 3 * (m + 1) / 5 + y + y / 4 - y / 100 + y / 400) % 7;
        switch (iWeek) {
            case 0:
                week=1;
                break;
            case 1:
                week=2;
                break;
            case 2:
                week=3;
                break;
            case 3:
                week=4;
                break;
            case 4:
                week=5;
                break;
            case 5:
                week=6;
                break;
            case 6:
                week=7;
                break;
        }
    }
    //将月份和星期数转换为英文
    private void change_week_month() {
        switch (week){
            case 1:
                week_day="Monday";
                break;
            case 2:
                week_day="Tuesday";
                break;
            case 3:
                week_day="Wednesday";
                break;
            case 4:
                week_day="Thursday";
                break;
            case 5:
                week_day="Friday";
                break;
            case 6:
                week_day="Saturday";
                break;
            case 7:
                week_day="Sunday";
                break;
            default:
                week_day="星期数出错";
                break;

        }
        switch (month){
            case 1:
                month_string="January";
                break;
            case 2:
                month_string="February";
                break;
            case 3:
                month_string="March";
                break;
            case 4:
                month_string="April";
                break;
            case 5:
                month_string="May";
                break;
            case 6:
                month_string="June";
                break;
            case 7:
                month_string="July";
                break;
            case 8:
                month_string="August";
                break;
            case 9:
                month_string="September";
                break;
            case 10:
                month_string="October";
                break;
            case 11:
                month_string="November";
                break;
            case 12:
                month_string="December";
                break;
            default:
                month_string="月份错误";
                break;

        }
    }

    /*
    * 以下为各种监听器类
    * */
    //回到主页类
    class BackHome implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //回到主界面
            Intent intent=new Intent(EditorActivity.this,MainActivity.class);
            intent.putExtra("month",String.valueOf(month));
            intent.putExtra("year",year_string);
            startActivity(intent);

                     //DataSupport.deleteAll(Diary.class);//删除所有
        }
    }
    //数据插入数据库
    class InsertValue implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            boolean exit=false;
            value=editorText.getText().toString();//获取内容
            Diary diarys=new Diary();
            List<Diary> diaries=DataSupport.findAll(Diary.class);
            if(value.length()!=0){
                //判断是否库中是否有同年月日的数据
                for(Diary diary:diaries){
                    if(year_string.equals(diary.getYear())&&month_string.equals(diary.getMonth())&&day.equals(diary.getDay())){
                        diarys.setContent(value);
                        diarys.updateAll("year=? and month=? and day=?",year_string,month_string,day);
                        exit=true;
                        break;//更新
                    }
                }
                //如果没有再添加
                if(exit==false){
                    diarys.setContent(value);
                    diarys.setDay(day);
                    diarys.setYear(year_string);
                    diarys.setMonth(month_string);
                    Log.d(tag,"week_day:"+week_day);
                    diarys.setWeek(week_day);
                    diarys.save();//第一次存储
                }
            }
            //回到主界面
            Intent intent=new Intent(EditorActivity.this,MainActivity.class);
            intent.putExtra("month",String.valueOf(month));
            intent.putExtra("year",year_string);
            startActivity(intent);
        }
    }
    //插入时间
    class InsertTime implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            editorText.append(TimeString);
        }
    }
    //删除类
    class DeleteData implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            editorText.setText("");//情况
            DataSupport.deleteAll(Diary.class,"year=? and month=? and day=?",year_string,month_string,day);
        }
    }


}
