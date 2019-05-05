package com.jssai.warehousepick.Model;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Pragadees on 22/11/16.
 */

public class Read implements KvmSerializable {

    String No;

    @Override
    public Object getProperty(int i) {
        switch (i){
            case 0:
                return No;
            default:
                return null;
        }

    }

    @Override
    public int getPropertyCount() {
        return 1;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i){
            case 0:
                No = (String)o;
        }

    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch(i) {

            case 0:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "No";
                break;
            default:
                break;
        }

    }
}
