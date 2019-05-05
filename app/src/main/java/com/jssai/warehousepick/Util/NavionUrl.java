package com.jssai.warehousepick.Util;

/**
 * Created by Pragadees on 16/11/16.
 */

public class NavionUrl {
    public final static String ESIGNATURE_URL = "http://76.174.207.208:7048/JSSAIApp/OData/Company('YGD')/WS_eSignOrder?$format=json";
    public class ESIGNATURE{
        public final static String NAMESPACE = "urn:microsoft-dynamics-schemas/page/esignorder";
        public final static String ESIGNATURE_URL_SOAP ="http://76.174.207.208:7047/JSSAIApp/WS/YGD/Page/WS_eSignOrder";
        public final static String METHOD_Read ="Read";
        public final static String METHOD_CREATE ="Create";
        public final static String METHOD_READ_MULTIPLE ="ReadMultiple";
    }
    public class WS_eSignOrder {
        public final static String NAMESPACE = "urn:microsoft-dynamics-schemas/page/ws_esignorder";
        public final static String POSTED_ESIGN_ORDER_NAMESPACE = "urn:microsoft-dynamics-schemas/page/ws_postedesignorder";
        public final static String URL_SOAP ="http://76.174.207.208:7047/JSSAIApp/WS/YGD/Page/WS_eSignOrderr";
        public final static String METHOD_Read ="Read";
        public final static String METHOD_CREATE ="Create";
        public final static String METHOD_READ_MULTIPLE ="ReadMultiple";
        public final static String GETPICTURE ="GetPicture";
        public final static String SALESSHIPMENT ="PostedShipment";
        public final static String SENDPICTURE =/*"GetPicturess"*/"SendPicture";
        public final static String POSTEDESIGNORDER ="PostedeSignOrder";
    }

}
