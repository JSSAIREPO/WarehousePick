package com.jssai.warehousepick.Model;

import android.text.format.DateFormat;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Pragadees on 15/11/16.
 */

public class WS_eSignOrder implements Serializable {

    public static String Ship_To_PO_No_String ="Ship_To_PO_No";
    public static String Customer_PO_No_String ="Customer_PO_No";
    public static String ETag_String ="ETag";
    public static String Ship_to_Name_String ="Ship_to_Name";
    public static String Entry_No_string ="Entry_No";
    public static String Mobile_Comment_string = "Mobile_Comment";
    public static String Sell_to_Customer_No_string ="Sell_to_Customer_No";
    public static String No_string ="No";
    public static String Shipment_Date_string = "Shipment_Date";
    public static String Ship_to_Code_string = "Ship_to_Code";
    public static String Sell_to_Customer_Name_string ="Sell_to_Customer_Name";
    public static String Key_string = "Key";
    public static String ORDER_NO ="OrderNo";

    public String Key;
    public String Ship_To_PO_No;
    public String Customer_PO_No;
    public String ETag;
    public String Ship_to_Name;
    public String Entry_No;
    public String Mobile_Comment;
    public String Sell_to_Customer_No ;
    public String No;
    public String Shipment_Date;
    public String Ship_to_Code;
    public String Sell_to_Customer_Name;
    public String Driver_Signature_Signed;
    public String Shipping_Clerk_Signature_Sgd;
    public String Receiver_Signature_Signed;
    public String Picture1_Signed;
    public String Picture2_Signed;
    public String Picture3_Signed;
    public String Document_Type;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getShip_To_PO_No ()
    {
        return Ship_To_PO_No;
    }

    public void setShip_To_PO_No (String Ship_To_PO_No)
    {
        this.Ship_To_PO_No = Ship_To_PO_No;
    }

    public String getCustomer_PO_No ()
    {
        return Customer_PO_No;
    }

    public void setCustomer_PO_No (String Customer_PO_No)
    {
        this.Customer_PO_No = Customer_PO_No;
    }

    public String getETag ()
    {
        return ETag;
    }

    public void setETag (String ETag)
    {
        this.ETag = ETag;
    }

    public String getShip_to_Name ()
    {
        return Ship_to_Name;
    }

    public void setShip_to_Name (String Ship_to_Name)
    {
        this.Ship_to_Name = Ship_to_Name;
    }

    public String getEntry_No ()
    {
        return Entry_No;
    }

    public void setEntry_No (String Entry_No)
    {
        this.Entry_No = Entry_No;
    }

    public String getMobile_Comment ()
    {
        return Mobile_Comment;
    }

    public void setMobile_Comment (String Mobile_Comment)
    {
        this.Mobile_Comment = Mobile_Comment;
    }

    public String getSell_to_Customer_No ()
    {
        return Sell_to_Customer_No;
    }

    public void setSell_to_Customer_No (String Sell_to_Customer_No)
    {
        this.Sell_to_Customer_No = Sell_to_Customer_No;
    }


    public String getNo ()
    {
        return No;
    }

    public void setNo (String No)
    {
        this.No = No;
    }

    public String getShipment_Date ()
    {
        return Shipment_Date;
    }

    public void setShipment_Date (String Shipment_Date)
    {
        this.Shipment_Date = Shipment_Date;
    }

    public String getShip_to_Code ()
    {
        return Ship_to_Code;
    }

    public void setShip_to_Code (String Ship_to_Code)
    {
        this.Ship_to_Code = Ship_to_Code;
    }

    public String getSell_to_Customer_Name ()
    {
        return Sell_to_Customer_Name;
    }

    public void setSell_to_Customer_Name (String Sell_to_Customer_Name)
    {
        this.Sell_to_Customer_Name = Sell_to_Customer_Name;
    }
    public static ArrayList<WS_eSignOrder> sortAscending(ArrayList<WS_eSignOrder> data,String field){
        SortAsce sortAsce = new SortAsce(field);
        Collections.sort(data,sortAsce);
        return data;
    }
    public static ArrayList<WS_eSignOrder> sortDescending(ArrayList<WS_eSignOrder> data,String field){
        SortDesc sortDesc = new SortDesc(field);
        Collections.sort(data,sortDesc);
        return data;
    }
    public static class SortAsce implements Comparator<WS_eSignOrder> {
        String field;

        public SortAsce(String field) {
            this.field = field;
        }

        @Override
        public int compare(WS_eSignOrder lhs, WS_eSignOrder rhs) {
            try {
                if(field.equals("Shipment_Date")){
                    SimpleDateFormat  foramt =  new SimpleDateFormat("MM-dd-yyyy");
                    Date datelhs = foramt.parse(WS_eSignOrder.class.getField(field).get(lhs).toString());
                    Date dateRhs = foramt.parse(WS_eSignOrder.class.getField(field).get(rhs).toString());
                    return datelhs.compareTo(dateRhs);
                } else {
                    String lhsValue = WS_eSignOrder.class.getField(field).get(lhs).toString().toLowerCase();
                    String rhaValue = WS_eSignOrder.class.getField(field).get(rhs).toString().toLowerCase();
                    return lhsValue.compareTo(rhaValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
    public static class SortDesc implements Comparator<WS_eSignOrder> {
        String field;

        public SortDesc(String field) {
            this.field = field;
        }

        @Override
        public int compare(WS_eSignOrder lhs, WS_eSignOrder rhs) {
            try {
                if(field.equals("Shipment_Date")){
                    SimpleDateFormat  foramt =  new SimpleDateFormat("MM-dd-yyyy");
                    Date datelhs = foramt.parse(WS_eSignOrder.class.getField(field).get(lhs).toString());
                    Date dateRhs = foramt.parse(WS_eSignOrder.class.getField(field).get(rhs).toString());
                    return dateRhs.compareTo(datelhs);
                } else {
                    String lhsValue = WS_eSignOrder.class.getField(field).get(lhs).toString().toLowerCase();
                    String rhaValue = WS_eSignOrder.class.getField(field).get(rhs).toString().toLowerCase();
                    return rhaValue.compareTo(lhsValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }


}
