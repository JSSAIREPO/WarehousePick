package com.jssai.warehousepick.Model;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Pragadees on 25/01/17.
 */

public class WS_SalesOrderListsResponse implements KvmSerializable {
    WS_SalesOrderListsResult ws_salesOrderListsResult;
    @Override
    public Object getProperty(int i) {
        switch (i){
            case 0: return ws_salesOrderListsResult;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 1;
    }

    @Override
    public void setProperty(int i, Object o) {
    switch (i){
        case 0: ws_salesOrderListsResult = (WS_SalesOrderListsResult)o;break;
    }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
     switch (i){
         case 0: propertyInfo.name="WS_SalesOrderListsResult";
                 propertyInfo.type=WS_SalesOrderListsResult.class;
                 break;
     }
    }
}
