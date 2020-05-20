package com.swufe.happybirthday;

public class RateItem {
    private int id;
    private String curName;
    private String curRate;

    public RateItem() {//空构造函数
        this.curName = "";
        this.curRate = "";
    }

    public RateItem(String curRate,String curName) {//构造函数
        this.curName = curName;
        this.curRate = curRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurName() {
        return curName;
    }

    public void setCurName(String curName) {
        this.curName = curName;
    }

    public String getCurRate() {
        return curRate;
    }

    public void setCurRate(String curRate) {
        this.curRate = curRate;
    }
}
