package com.jssai.warehousepick.Model;

/**
 * Created by Pragadees on 22/11/16.
 */

public class SimpleTable {

    public String Key;
    public String No;
    public String Desc1;
    public String Desc2;


    public SimpleTable(String key, String no, String desc1, String desc2) {
        Key = key;
        No = no;
        Desc1 = desc1;
        Desc2 = desc2;
    }

    public SimpleTable() {
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getDesc1() {
        return Desc1;
    }

    public void setDesc1(String desc1) {
        Desc1 = desc1;
    }

    public String getDesc2() {
        return Desc2;
    }

    public void setDesc2(String desc2) {
        Desc2 = desc2;
    }


}
