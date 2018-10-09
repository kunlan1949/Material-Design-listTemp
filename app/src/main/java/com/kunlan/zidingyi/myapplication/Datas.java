package com.kunlan.zidingyi.myapplication;

import org.litepal.crud.LitePalSupport;

public class Datas extends LitePalSupport {
    private int id;
    private String TempData;
    private String TimeData;

    public String getTempData() {
        return TempData;
    }

    public void setTempData(String tempData) {
        TempData = tempData;
    }

    public String getTimeData() {
        return TimeData;
    }

    public void setTimeData(String timeData) {
        TimeData = timeData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}