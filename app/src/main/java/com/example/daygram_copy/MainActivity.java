package com.example.daygram_copy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static int week;//星期数
    private TextView buttonTodayEntry;//添加日记按钮
    private TextView labelYear;//显示的年份
    private TextView labelMonth;//显示的月份
    private String year,month;//年和月的String类型
    private ListView entryList;//点状listview
    private List<Circle> circleList=new ArrayList<>();
    private List<DiaryShow> diaryList=new ArrayList<>();
    private String tag="MainActivity";//方便调试
    private List<Month> monthList=new ArrayList<>();//存储月份
    private List<Month> yearList=new ArrayList<>();//存储年数
    private RecyclerView recyccler_view_month;//月份的横向列表
    private RecyclerView recyccler_view_year;//年份的横向列表
    private LinearLayout bottom_menum;//底部菜单
    private int year_int,month_int;//年和月的int类型
    private int year_today,month_today,day_today;//今天int类型的年月日
    private String month_Eng;//英文版本月份
    private ListView DiaryList;//详细信息的listView
    private RelativeLayout buttonMonthly;//切换listView形式
    private boolean iscontent=false;//判断是否有内容

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //隐藏标题栏和全屏显示
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //初始化控件
        initView();
        //为控件初始化并添加监听器
        setListener();
        //设置主界面的时间
        setTime();
        //初始化列表1
        StartListView();
        //初始化列表2
        StartDiaryListView();
        //RecyclerView的监听事件
        bottom_recyclerview_month();
        bottom_recyclerview_year();
        //底部月份和日期的监听事件
        selectMonth_Year();

    }
    /*
     *以下是各类方法
     * */
    //生成日记内容列表
    private void StartDiaryListView() {
        initDiaries();
        DiaryAdapter adapter2=new DiaryAdapter(MainActivity.this,R.layout.diary_item,diaryList);
        ListView listView2=findViewById(R.id.DiaryListView);
        listView2.setAdapter(adapter2);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,EditorActivity.class);
                DiaryShow diary=diaryList.get(position);
                //Log.d(tag,"传过去的日子为:"+diary.getDay());
                //将主界面的年月日传过去
                intent.putExtra("day_time1",diary.getDay());
                intent.putExtra("year_time",year);
                intent.putExtra("month_time",month);
//                Log.d(tag,"传过去的年"+year);
//                Log.d(tag,"传过去的月"+month);
                startActivity(intent);

            }
        });
    }
    //生成点状数据
    private void initFruits(){//引用图片
        int monthNum;//一个月的天数
//        Log.d(tag,"今天是"+month_today);
//        Log.d(tag,"设置的今天是"+month_int);
//        Log.d(tag,"设置的年份是"+year_int);
        //判断一个月该有多少天
        if(year_int<year_today||(year_int==year_today&&month_int<month_today)){
             monthNum=getMonthOfDay(year_int,month_int);
        }else if(year_int==year_today&&month_int==month_today){
            monthNum=day_today;
        } else{
            monthNum=0;
        }
        circleList.clear();//先清空之前的数据，方便刷新
        for(int i=1;i<=monthNum;i++){
            //获取星期数
            CalculateWeekDay(year_int,month_int,i);
            change_month();
            CalculateContent(year,month_Eng,String.valueOf(i));
            if(week==7&&iscontent==true){//如果是星期天并且有内容，将点设置为红书
                Circle circle=new Circle(R.drawable.diary_red);
                circleList.add(circle);
                continue;
            }else if(week==7&&iscontent!=true){//如果是星期天但是没有内容,设置为红点
                Circle circle=new Circle(R.drawable.red);
                circleList.add(circle);
            }else if(week!=7&&iscontent==true){//如果不是星期天但是有内容，设置为蓝书
                Circle circle=new Circle(R.drawable.diary);
                circleList.add(circle);
            }else{//其他情况为黑点
                Circle circle=new Circle(R.drawable.dark);
                circleList.add(circle);
            }

        }
        //更新字符型年和月
        year=String.valueOf(year_int);
        month=String.valueOf(month_int);
//        Log.d(tag,"当前的年份"+year);
//        Log.d(tag,"当前的月份"+month);
    }
    //生成日志列表数据
    private void initDiaries(){
        List<Diary> diaries=DataSupport.findAll(Diary.class);
        diaryList.clear();//每次前清空
        change_month();//将月份转换成英文
        //从数据库里面查数据
        for(Diary diary:diaries){
            if(year.equals(diary.getYear())&&month_Eng.equals(diary.getMonth())){
             DiaryShow diaryshow=new DiaryShow(diary.getDay(),diary.getWeek(),diary.getContent());
             diaryList.add(diaryshow);
            }
        }
        //对diaryList进行升序排序
        Collections.sort(diaryList, new Comparator<DiaryShow>() {
            @Override
            public int compare(DiaryShow o1, DiaryShow o2) {
                if(Integer.parseInt(o1.getDay())>Integer.parseInt(o2.getDay())){
                    return 1;
                }else if(Integer.parseInt(o1.getDay())==Integer.parseInt(o2.getDay())){
                    return 0;
                }else
                    return -1;
            }
        });
    }
    //将月份变为英文
    private void change_month() {
        switch (month_int){
            case 1:
                month_Eng="January";
                break;
            case 2:
                month_Eng="February";
                break;
            case 3:
                month_Eng="March";
                break;
            case 4:
                month_Eng="April";
                break;
            case 5:
                month_Eng="May";
                break;
            case 6:
                month_Eng="June";
                break;
            case 7:
                month_Eng="July";
                break;
            case 8:
                month_Eng="August";
                break;
            case 9:
                month_Eng="September";
                break;
            case 10:
                month_Eng="October";
                break;
            case 11:
                month_Eng="November";
                break;
            case 12:
                month_Eng="December";
                break;
            default:
                month_Eng="月份错误";
                break;

        }
    }
    //通过年和月获得每个月的长度
    public static int getMonthOfDay(int year,int month){
        int day = 0;
        if(year%4==0&&year%100!=0||year%400==0){
            day = 29;
        }else{
            day = 28;
        }
        switch (month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return day;

        }
        return 0;
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
    //通过年月日查看当天是否有内容
    private void CalculateContent(String year,String month,String day){
        iscontent=false;
        List<Diary> diaries=DataSupport.findAll(Diary.class);
        for(Diary diary:diaries){
            if(year.equals(diary.getYear())&&month.equals(diary.getMonth())&&day.equals(diary.getDay())){
                iscontent=true;
                break;
            }
        }
    }
    private void selectMonth_Year() {
        //选择月份
        labelMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottom_menum.setVisibility(View.INVISIBLE);
                recyccler_view_month.setVisibility(View.VISIBLE);
            }
        });
        //选择年份
        labelYear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                bottom_menum.setVisibility(View.INVISIBLE);
                recyccler_view_year.setVisibility(View.VISIBLE);
            }
        });
    }
    private void bottom_recyclerview_month() {
        initMonth();
        //initYear();
        final RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recyccler_view_month);
        //layoutManager用于指定RecyclerView的布局方式，这里指定了线性布局(LinearLayoutManager),实现和ListView类似的效果
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        //通过layoutManager来管理布局
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        //MonthAdapter adapter=new MonthAdapter(yearList);
        MonthAdapter adapter=new MonthAdapter(monthList);
        adapter.onClickListener(new MonthAdapter.onItemClick() {
            @Override
            public void onClick(View v, int i) {
                //Toast.makeText(MainActivity.this,"点击了"+(i+1)+"月",Toast.LENGTH_SHORT).show();
                month_int=i+1;
                String sMonth=(i+1)+" 月";
                labelMonth.setText(sMonth);
                StartListView();//更新列表
                StartDiaryListView();
                recyccler_view_month.setVisibility(View.INVISIBLE);
                bottom_menum.setVisibility(View.VISIBLE);


            }
        });
        recyclerView.setAdapter(adapter);
    }
    private void bottom_recyclerview_year(){
        initYear();
        final RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recyccler_view_year);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        //通过layoutManager来管理布局
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        MonthAdapter adapter=new MonthAdapter(yearList);
        adapter.onClickListener(new MonthAdapter.onItemClick() {
            @Override
            public void onClick(View v, int i) {
                //Toast.makeText(MainActivity.this,"点击了"+(i+1)+"月",Toast.LENGTH_SHORT).show();
                year_int=i+2011;
                year=String.valueOf(year_int);
                labelYear.setText(year);
                StartListView();//更新列表
                StartDiaryListView();
                recyccler_view_year.setVisibility(View.INVISIBLE);
                bottom_menum.setVisibility(View.VISIBLE);

            }
        });
        recyclerView.setAdapter(adapter);
    }
    //月份数据生成函数
    private void initMonth(){
        Month JAN=new Month("1月");
        monthList.add(JAN);
        Month FEB=new Month("2月");
        monthList.add(FEB);
        Month MAR=new Month("3月");
        monthList.add(MAR);
        Month APR=new Month("4月");
        monthList.add(APR);
        Month MAY=new Month("5月");
        monthList.add(MAY);
        Month JUN=new Month("6月");
        monthList.add(JUN);
        Month JUL=new Month("7月");
        monthList.add(JUL);
        Month AUG=new Month("8月");
        monthList.add(AUG);
        Month SEP=new Month("9月");
        monthList.add(SEP);
        Month OCT=new Month("10月");
        monthList.add(OCT);
        Month NOV=new Month("11月");
        monthList.add(NOV);
        Month DEC=new Month("12月");
        monthList.add(DEC);
    }
    //年份数据生成函数
    private void initYear(){
        Month sixth=new Month("2011");
        yearList.add(sixth);
        Month seventh=new Month("2012");
        yearList.add(seventh);
        Month eighth=new Month("2013");
        yearList.add(eighth);
        Month first=new Month("2014");
        yearList.add(first);
        Month second=new Month("2015");
        yearList.add(second);
        Month third=new Month("2016");
        yearList.add(third);
        Month fourth=new Month("2017");
        yearList.add(fourth);
        Month fifth=new Month("2018");
        yearList.add(fifth);
        if(year_today==2019){
            Month tenth=new Month("2019");
            yearList.add(tenth);
        }
    }
    //设置系统当前时间
    private void setTime() {
        Intent intent=getIntent();

        Calendar c=Calendar.getInstance();
        year_int=c.get(Calendar.YEAR);
        month_int=c.get(Calendar.MONTH);
        day_today=c.get(Calendar.DAY_OF_MONTH);
        month_int+=1;
        year_today=year_int;
        month_today=month_int;
        if(intent.getStringExtra("month")==null&&intent.getStringExtra("year")==null){//没有数据传过来
            year=String.valueOf(year_int);
            month=String.valueOf(month_int);
            month+=" 月";
        }else{
            month=intent.getStringExtra("month");
            month_int=Integer.parseInt(month);
            month+=" 月";
            year=intent.getStringExtra("year");
            year_int=Integer.parseInt(year);
        }

        //设置当前的系统时间
        labelYear.setText(year);
        labelMonth.setText(month);
    }
    //生成ListView
    private void StartListView() {
        initFruits();
        CircleAdapter adapter=new CircleAdapter(MainActivity.this, R.layout.circle_item,circleList);
        entryList.setAdapter(adapter);
        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,EditorActivity.class);
                //注意这里position为int类型，要转换为String类型!!!!!!
                String position_string=String.valueOf(position);
                //将主界面的年月日传过去
                intent.putExtra("day_time",position_string);
                intent.putExtra("year_time",year);
                intent.putExtra("month_time",month);
//                Log.d(tag,"传过去的年"+year);
//                Log.d(tag,"传过去的月"+month);
                startActivity(intent);
            }
        });
    }
    private void setListener() {
        ToEditor toEditor=new ToEditor();
        buttonTodayEntry.setOnClickListener(toEditor);
        CheckList checkList=new CheckList();
        buttonMonthly.setOnClickListener(checkList);
    }
    private void initView() {
        buttonTodayEntry=(TextView)findViewById(R.id.buttonTodayEntry);
        labelYear=findViewById(R.id.labelYear);
        labelMonth=findViewById(R.id.labelMonth);
        entryList=findViewById(R.id.entryListView);
        recyccler_view_month=findViewById(R.id.recyccler_view_month);
        recyccler_view_year=findViewById(R.id.recyccler_view_year);
        bottom_menum=findViewById(R.id.bottom_menum);
        DiaryList=findViewById(R.id.DiaryListView);
        buttonMonthly=findViewById(R.id.buttonMonthly);
    }
    /*
     * 以下为各种监听器类
     * */
    //点击"+"号进入当天编辑页面
    class ToEditor implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(MainActivity.this,EditorActivity.class);
            //把当前天数传过去
            Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int year=c.get(Calendar.YEAR);
            int month=c.get(Calendar.MONTH);
            day-=1;//后面要+1，这里先-1
            month+=1;
            String day_string=String.valueOf(day);
            String month_string=String.valueOf(month);
            String year_string=String.valueOf(year);
            intent.putExtra("day_time",day_string);
            intent.putExtra("year_time",year_string);
            intent.putExtra("month_time",month_string);
            startActivity(intent);
        }
    }
    //切换列表显示方式
    class CheckList implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(entryList.getVisibility()==View.VISIBLE){
                entryList.setVisibility(View.INVISIBLE);
                DiaryList.setVisibility(View.VISIBLE);
            }else{
                entryList.setVisibility(View.VISIBLE);
                DiaryList.setVisibility(View.INVISIBLE);
            }
        }
    }



}
