package com.jssai.warehousepick.Model;

import java.io.Serializable;

public class WarehousePickItem implements Serializable {

    private String No;

    private int Action_Type;

    private boolean Activity_TypeSpecified;

    private String Description;

    private int Activity_Type;

    private String Customer_Name;

    private String Customer_No;

    private boolean Line_NoSpecified;

    private long Quantity;

    private String Sales_Order_No;

    private String Description_2;

    private boolean Qty_BaseSpecified;

    private String Bin_Code;

    private boolean Action_TypeSpecified;

    private int Line_No;

    private String Zone_Code;

    private String Item_No;

    private boolean QuantitySpecified;

    private String Unit_of_Measure_Code;

    private long Qty_Base;

    private String Key;

    private long Qty_to_Handle;

    private String LotNumber;

    public WarehousePickItem(String key, int activity_type, boolean activity_typeSpecified, String no, String sales_order_no, String customer_no, String customer_name) {
        this.Key = key;
        this.Activity_Type = activity_type;
        this.Activity_TypeSpecified = activity_typeSpecified;
        this.No = no;
        this.Sales_Order_No = sales_order_no;
        this.Customer_No = customer_no;
        this.Customer_Name = customer_name;
    }

    public WarehousePickItem(String no, int action_Type, boolean activity_TypeSpecified, String description, int activity_Type, String customer_Name, String customer_No, boolean line_NoSpecified, long quantity, String sales_Order_No, String description_2, boolean qty_BaseSpecified, String bin_Code, boolean action_TypeSpecified, int line_No, String zone_Code, String item_No, boolean quantitySpecified, String unit_of_Measure_Code, long qty_Base, String key, long Qty_to_Handle) {
        No = no;
        Action_Type = action_Type;
        Activity_TypeSpecified = activity_TypeSpecified;
        Description = description;
        Activity_Type = activity_Type;
        Customer_Name = customer_Name;
        Customer_No = customer_No;
        Line_NoSpecified = line_NoSpecified;
        Quantity = quantity;
        Sales_Order_No = sales_Order_No;
        Description_2 = description_2;
        Qty_BaseSpecified = qty_BaseSpecified;
        Bin_Code = bin_Code;
        Action_TypeSpecified = action_TypeSpecified;
        Line_No = line_No;
        Zone_Code = zone_Code;
        Item_No = item_No;
        QuantitySpecified = quantitySpecified;
        Unit_of_Measure_Code = unit_of_Measure_Code;
        Qty_Base = qty_Base;
        Key = key;
        this.Qty_to_Handle = Qty_to_Handle;
    }

    public WarehousePickItem(String key, String no, String sales_order_no, String customer_no, String customer_name) {
        No = no;
        Key = key;
        Customer_Name = customer_name;
        Customer_No = customer_no;
        Sales_Order_No = sales_order_no;
    }

    public String getNo() {
        if (No == null || No.equalsIgnoreCase("null")) {
            return "";
        }
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public int getAction_Type() {
        return Action_Type;
    }

    public void setAction_Type(int action_Type) {
        Action_Type = action_Type;
    }

    public boolean isActivity_TypeSpecified() {
        return Activity_TypeSpecified;
    }

    public void setActivity_TypeSpecified(boolean activity_TypeSpecified) {
        Activity_TypeSpecified = activity_TypeSpecified;
    }

    public String getDescription() {
        if (Description == null || Description.equalsIgnoreCase("null")) {
            return "";
        }
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getActivity_Type() {
        return Activity_Type;
    }

    public void setActivity_Type(int activity_Type) {
        Activity_Type = activity_Type;
    }

    public String getCustomer_Name() {
        if (Customer_Name == null || Customer_Name.equalsIgnoreCase("null")) {
            return "";
        }
        return Customer_Name;
    }

    public void setCustomer_Name(String customer_Name) {
        Customer_Name = customer_Name;
    }

    public String getCustomer_No() {
        if (Customer_No == null || Customer_No.equalsIgnoreCase("null")) {
            return "";
        }
        return Customer_No;
    }

    public void setCustomer_No(String customer_No) {
        Customer_No = customer_No;
    }

    public boolean getLine_NoSpecified() {
        return Line_NoSpecified;
    }

    public void setLine_NoSpecified(boolean line_NoSpecified) {
        Line_NoSpecified = line_NoSpecified;
    }

    public long getQuantity() {
        return Quantity;
    }

    public void setQuantity(long quantity) {
        Quantity = quantity;
    }

    public String getSales_Order_No() {
        if (Sales_Order_No == null || Sales_Order_No.equalsIgnoreCase("null")) {
            return "";
        }
        return Sales_Order_No;
    }

    public void setSales_Order_No(String sales_Order_No) {
        Sales_Order_No = sales_Order_No;
    }

    public String getDescription_2() {
        if (Description_2 == null || Description_2.equalsIgnoreCase("null")) {
            return "";
        }
        return Description_2;
    }

    public void setDescription_2(String description_2) {
        Description_2 = description_2;
    }

    public boolean isQty_BaseSpecified() {
        return Qty_BaseSpecified;
    }

    public void setQty_BaseSpecified(boolean qty_BaseSpecified) {
        Qty_BaseSpecified = qty_BaseSpecified;
    }

    public String getBin_Code() {
        if (Bin_Code == null || Bin_Code.equalsIgnoreCase("null")) {
            return "";
        }
        return Bin_Code;
    }

    public void setBin_Code(String bin_Code) {
        Bin_Code = bin_Code;
    }

    public boolean isAction_TypeSpecified() {
        return Action_TypeSpecified;
    }

    public void setAction_TypeSpecified(boolean action_TypeSpecified) {
        Action_TypeSpecified = action_TypeSpecified;
    }

    public int getLine_No() {
        return Line_No;
    }

    public void setLine_No(int line_No) {
        Line_No = line_No;
    }

    public String getZone_Code() {
        if (Zone_Code == null || Zone_Code.equalsIgnoreCase("null")) {
            return "";
        }
        return Zone_Code;
    }

    public void setZone_Code(String zone_Code) {
        Zone_Code = zone_Code;
    }

    public String getItem_No() {
        if (Item_No == null || Item_No.equalsIgnoreCase("null")) {
            return "";
        }
        return Item_No;
    }

    public void setItem_No(String item_No) {
        Item_No = item_No;
    }

    public boolean isQuantitySpecified() {
        return QuantitySpecified;
    }

    public void setQuantitySpecified(boolean quantitySpecified) {
        QuantitySpecified = quantitySpecified;
    }

    public String getUnit_of_Measure_Code() {
        if (Unit_of_Measure_Code == null || Unit_of_Measure_Code.equalsIgnoreCase("null")) {
            return "";
        }
        return Unit_of_Measure_Code;
    }

    public void setUnit_of_Measure_Code(String unit_of_Measure_Code) {
        Unit_of_Measure_Code = unit_of_Measure_Code;
    }

    public long getQty_Base() {
        return Qty_Base;
    }

    public void setQty_Base(long qty_Base) {
        Qty_Base = qty_Base;
    }

    public String getKey() {
        if (Key == null || Key.equalsIgnoreCase("null")) {
            return "";
        }
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public long getQty_to_Handle() {
        return Qty_to_Handle;
    }

    public void setQty_to_Handle(long qty_to_Handle) {
        Qty_to_Handle = qty_to_Handle;
    }

    public String getLotNumber() {
        return LotNumber;
    }

    public void setLotNumber(String lotNumber) {
        LotNumber = lotNumber;
    }
}
