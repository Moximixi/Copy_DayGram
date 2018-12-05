package com.example.daygram_copy;
/*
* 日志显示列表的适配类型
* */
public class DiaryShow {
    private String day;
    private String Week;
    private String Content;

    public DiaryShow(String day,String Week,String Content){
        this.day=day;
        this.Week=Week;
        this.Content=Content;
    }

    public String getDay() {
        return day;
    }

    public String getWeek() {
        return Week;
    }

    public String getContent() {
        return Content;
    }
}
