package com.jssai.warehousepick.Model;

import java.io.Serializable;

public class Binlist implements Serializable {

    private String Key;

    private String Location_Code;

    private String Zone_Code;

    private String Bin_Code;

    private String Item_No;

    private int AvailableQuantity;

    private boolean AvailableQuantitySpecified;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getLocation_Code() {
        return Location_Code;
    }

    public void setLocation_Code(String location_Code) {
        Location_Code = location_Code;
    }

    public String getZone_Code() {
        return Zone_Code;
    }

    public void setZone_Code(String zone_Code) {
        Zone_Code = zone_Code;
    }

    public String getBin_Code() {
        return Bin_Code;
    }

    public void setBin_Code(String bin_Code) {
        Bin_Code = bin_Code;
    }

    public String getItem_No() {
        return Item_No;
    }

    public void setItem_No(String item_No) {
        Item_No = item_No;
    }

    public int getAvailableQuantity() {
        return AvailableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        AvailableQuantity = availableQuantity;
    }

    public boolean isAvailableQuantitySpecified() {
        return AvailableQuantitySpecified;
    }

    public void setAvailableQuantitySpecified(boolean availableQuantitySpecified) {
        AvailableQuantitySpecified = availableQuantitySpecified;
    }

    @Override
    public String toString() {
        return "Binlist{" +
                "Key='" + Key + '\'' +
                ", Location_Code='" + Location_Code + '\'' +
                ", Zone_Code='" + Zone_Code + '\'' +
                ", Bin_Code='" + Bin_Code + '\'' +
                ", Item_No='" + Item_No + '\'' +
                ", AvailableQuantity=" + AvailableQuantity +
                ", AvailableQuantitySpecified=" + AvailableQuantitySpecified +
                '}';
    }
}
