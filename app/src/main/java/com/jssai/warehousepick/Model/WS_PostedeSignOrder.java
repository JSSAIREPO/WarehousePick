package com.jssai.warehousepick.Model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Pragadees on 15/11/16.
 */

public class WS_PostedeSignOrder implements Serializable {

    public String Key,Sales_Shipment_No,Entry_No,Sell_to_Customer_No, No,Ship_to_Code, Ship_to_Name, Shipment_Date, Sell_to_Customer_Name, Mobile_Comment,Document_Type,Driver_Signature_Signed, Shipping_Clerk_Signature_Sgd,Receiver_Signature_Signed, Picture1_Signed,Picture2_Signed,Picture3_Signed, e_Sign_Entry_No;

    public String getSales_Shipment_No() {
        return Sales_Shipment_No;
    }

    public void setSales_Shipment_No(String sales_Shipment_No) {
        this.Sales_Shipment_No = sales_Shipment_No;
    }
}
