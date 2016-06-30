package com.wusongyuan.customviewstudio.bean;

/**********************
 * @author: wusongyuan
 * @date: 2016-06-30
 * @desc:
 **********************/
public class GiftBean {
    int[] startXY;
    int[] endXY;
    int duration;
    int stayTime;

    public int[] getStartXY() {
        return startXY;
    }

    public void setStartXY(int[] startXY) {
        this.startXY = startXY;
    }

    public int[] getEndXY() {
        return endXY;
    }

    public void setEndXY(int[] endXY) {
        this.endXY = endXY;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStayTime() {
        return stayTime;
    }

    public void setStayTime(int stayTime) {
        this.stayTime = stayTime;
    }
}
