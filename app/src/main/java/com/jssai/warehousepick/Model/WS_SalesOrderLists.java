package com.jssai.warehousepick.Model;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.Hashtable;

/**
 * Created by Pragadees on 25/01/17.
 */

public class WS_SalesOrderLists implements KvmSerializable {
    String key;
    String No;
    String Bill_to_Customer_No;
    String Bill_to_Name;
    String Posting_Date;

    @Override
    public Object getProperty(int i) {
        switch (i){
            case 0: return key;
            case 1: return No;
            case 2: return Bill_to_Customer_No;
            case 3: return Bill_to_Name;
            case 4: return Posting_Date;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 5;
    }

    @Override
    public void setProperty(int i, Object o) {
      switch (i){
          case 0: key =(String)o;break;
          case 1: No =(String)o;break;
          case 2: Bill_to_Customer_No =(String)o;break;
          case 3: Bill_to_Name =(String)o;break;
          case 4: Posting_Date =(String)o;break;
      }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
       switch (i){
           case 0: propertyInfo.name="key"; propertyInfo.type=PropertyInfo.STRING_CLASS;break;
           case 1: propertyInfo.name="No"; propertyInfo.type=PropertyInfo.STRING_CLASS;break;
           case 2: propertyInfo.name="Bill_to_Customer_No"; propertyInfo.type=PropertyInfo.STRING_CLASS;break;
           case 3: propertyInfo.name="Bill_to_Name"; propertyInfo.type=PropertyInfo.STRING_CLASS;break;
           case 4: propertyInfo.name="Posting_Date"; propertyInfo.type=PropertyInfo.STRING_CLASS;break;
       }
    }
}
