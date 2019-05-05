package com.jssai.warehousepick;

import java.io.Serializable;

/**
 * Created by Pragadees on 12/02/17.
 */

public class WS_SalesShipment implements Serializable {

    public String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
