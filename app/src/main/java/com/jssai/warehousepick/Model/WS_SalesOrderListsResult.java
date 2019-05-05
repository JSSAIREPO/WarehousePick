package com.jssai.warehousepick.Model;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.Hashtable;

/**
 * Created by Pragadees on 25/01/17.
 */

public class WS_SalesOrderListsResult implements KvmSerializable {
    WS_SalesOrderLists WS_SalesOrderLists; //here goes Customer class ofcourse
    String ErrorMessage;
    @Override
    public Object getProperty(int i) {
        switch(i){
            case 0: return WS_SalesOrderLists;
            case 1: return ErrorMessage;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 2;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch(i){
            case 0: WS_SalesOrderLists=(WS_SalesOrderLists)o;break;//Customer class
            case 1: ErrorMessage=(String)o;break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch(i){
            case 0: propertyInfo.name="WS_SalesOrderLists"; propertyInfo.type=WS_SalesOrderLists.class;break; //Customer class
            case 1: propertyInfo.name="ErrorMessage"; propertyInfo.type=PropertyInfo.STRING_CLASS;break;

        }
    }

}
