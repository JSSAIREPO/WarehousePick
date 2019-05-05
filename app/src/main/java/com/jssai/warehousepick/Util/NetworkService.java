package com.jssai.warehousepick.Util;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jssai.warehousepick.AndroidBmpUtil;
import com.jssai.warehousepick.WS_GetPDFContent;
import eu.masconsult.android_ntlm.NTLMSchemeFactory;

// #1 Nov-Dec 2017 ,To get SSNO<To get sales shipment number from API>
public class NetworkService extends IntentService {
    public static FirebaseAnalytics mFirebaseAnalytics;
    static Gson gson = new Gson();


    public NetworkService() {
        super("NetworkService");
// Obtain the FirebaseAnalytics instance.
    }

    public static void Get(String Url, Handler handler, Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("url", Url);
        Messenger messenger = new Messenger(handler);
        intent.putExtra("handler", messenger);
        intent.setAction("GET");
        intent.putExtra("ip", GlobalMethods.getIp(context));
        Log.i(Constants.TAG, "GET " + Url);
        context.startService(intent);
    }

    public static void Put(String Url, String object, String etag, Handler handler, Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("url", Url);
        Messenger messenger = new Messenger(handler);
        intent.putExtra("handler", messenger);
        intent.putExtra("object", object);
        intent.setAction("PUT");
        intent.putExtra("Etag", etag);
        intent.putExtra("ip", GlobalMethods.getIp(context));
        Log.i(Constants.TAG, "POST " + Url + object.toString());
        context.startService(intent);
    }


    public static void SOAPTEST(String url, Context context, Handler handler) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("url", url);
        Messenger messenger = new Messenger(handler);
        intent.putExtra("handler", messenger);
        intent.setAction("SOAP_TEST");
        Log.i(Constants.TAG, "HEAD " + url);
        intent.putExtra("ip", GlobalMethods.getIp(context));
        context.startService(intent);
    }


    public static void SOAP(@NonNull String methodName, @NonNull Context context, @NonNull Handler handler, @NonNull Class aClass, Object object, Map<String, String> params) {
//        mFirebaseAnalytics = FirebaseAnalytics.getInstanceName(context);
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("methodname", methodName);
        if (object != null) {
            intent.putExtra("objecto", (Serializable) object);
        }
        if (params != null) {
            intent.putExtra("params", (Serializable) params);
        }
        intent.putExtra("class", aClass);
        Messenger messenger = new Messenger(handler);
        intent.putExtra("handler", messenger);
        intent.setAction("SOAP");
        intent.putExtra("ip", GlobalMethods.getIp(context));
        context.startService(intent);
    }

    public static void UploadPictureSOAP(@NonNull String methodName, @NonNull Context context, @NonNull Handler handler, @NonNull Class aClass, Object object, Map<String, String> params, String uri, String type, String key) {
//        mFirebaseAnalytics = FirebaseAnalytics.getInstanceName(context);
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("methodname", methodName);
        if (object != null) {
            intent.putExtra("objecto", (Serializable) object);
        }
        if (params != null) {
            intent.putExtra("params", (Serializable) params);
        }
        intent.putExtra("uri", uri.toString());
        intent.putExtra("class", aClass);
        Messenger messenger = new Messenger(handler);
        intent.putExtra("handler", messenger);
        intent.setAction("SOAP Picture");
        intent.putExtra("ip", GlobalMethods.getIp(context));
        intent.putExtra("type", type);
        intent.putExtra("key", key);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String url = intent.getStringExtra("url");
            final Bundle intent_bundle = intent.getExtras();
            final Messenger messenger = (Messenger) intent_bundle.get("handler");
            final Message otherMessage = Message.obtain();
            final String object = intent.getStringExtra("object");
            final String ip = intent.getStringExtra("ip");
            SharedPreferences sharedPreferences = GlobalMethods.getSharedPrefrence(getApplicationContext());
            final String username = sharedPreferences.getString(Constants.USER, "");
            final String password = sharedPreferences.getString(Constants.PASSWORD, "");
            final DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
            httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new NTCredentials(username, password, "", ""));
            Bundle logBundle = new Bundle();
            switch (action) {
                case "GET":
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                HttpUriRequest httpget = new HttpGet(url);
                                HttpResponse getResponse = httpclient.execute(httpget);
                                processData(getResponse, messenger);
                            } catch (IOException e) {
                                e.printStackTrace();
                                otherMessage.arg1 = 0;
                                try {
                                    messenger.send(otherMessage);
                                } catch (RemoteException e1) {
                                    e1.printStackTrace();
                                }

                            }
                        }
                    }).start();
                    break;
                case "PUT":
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String etag = intent.getStringExtra("Etag");
                                HttpPut httpPut = new HttpPut(url);
                                httpPut.addHeader("Content-Type", "application/json");
                                httpPut.addHeader("If-Match", etag);
                                // httpPut.addHeader("If-Match" ,"W/\"'16%3BUcMAAACHxQAAAAAA6%3B8720440%3B'\"");
                                httpPut.setEntity(new StringEntity(object, "UTF-8"));
                                HttpResponse getResponse = httpclient.execute(httpPut);
                                processData(getResponse, messenger);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case "SOAP_TEST":
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //String namespace = "urn:microsoft-dynamics-schemas/page/simpletable";
                            String namespace = "urn:microsoft-dynamics-schemas/page/ws_postsaleshipmentt";
                            // String namespace = "urn:microsoft-dynamics-schemas/page/ws_picturetest";
                            // String namespace = "urn:microsoft-dynamics-schemas/page/ws_salesshipments";

                            //String url = "http://76.174.207.208:7047/DynamicsNAV90/WS/YGD/Page/SimpleTable";
                            //String url = "http://45.51.149.249:7047/DynamicsNAV/WS/YGD/Page/WS_PostSalesOrder";
                            // String url = "http://45.51.149.249:7047/DynamicsNAV/WS/YGD/Page/WS_PictureTest";
//                            String url = "http://45.51.149.249:7047/DynamicsNAV/WS/Youngstown%20Grape%20Dist./Page/WS_PostSalesOrder";
                            String url = "http://45.51.149.249:7047/DynamicsNAV/WS/JSSAIApp/Page/WS_PostSaleShipmentt";
                            //String soap_action = "urn:microsoft-dynamics-schemas/page/simpletable:Read";
                            String soap_action = "urn:microsoft-dynamics-schemas/page/ws_postsaleshipmentt";
                            //String soap_action = "urn:microsoft-dynamics-schemas/page/ws_salesshipments";
                            // String soap_action = "urn:microsoft-dynamics-schemas/page/ws_picturetest";
                            // String method_name = "PrintShipment";
                          //  String method_name = "Read";
                            // String method_name = "PrintBOLL";
                            //String method_name = "ReadMultiple";
                            // String method_name = "GetItemPicture";
                            // String method_name = "ApptoMobile";
//                             String method_name = "PostOrder";
//                             String method_name = "PrintBOL";
                            String method_name = "PostSalesShipmentt";
                            String great;
                            try {
                                SoapObject request = new SoapObject(namespace, method_name);
                                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                envelope.dotNet = true;
                                envelope.implicitTypes = true;
                                envelope.setOutputSoapObject(request);

//                                SimpleTable simpleTable = new SimpleTable();
//                                simpleTable.setProperty(0,"3");
//                                simpleTable.setProperty(1,"Android3");
//                                simpleTable.setProperty(2,"Android33");
//                                Log.d("xxxx"," "+simpleTable.toString());

//                                WS_eSignOrder ws_eSignOrder = new WS_eSignOrder();
//                                ws_eSignOrder.setProperty(0,"12;UcMAAACHxQ==6;8720440;");
//                                ws_eSignOrder.setProperty(1,197);
//                                ws_eSignOrder.setProperty(10,"From Android");
//                               // ws_eSignOrder.setProperty(11,"hi");

//
//                                PropertyInfo objekt = new PropertyInfo();
//                                objekt.setName("WS_eSignOrder");
//                                objekt.setValue(ws_eSignOrder);
//                                objekt.setType(ws_eSignOrder.getClass());
//                                request.addProperty(objekt);

//
//                                PropertyInfo objekt = new PropertyInfo();
//                                objekt.setName("SimpleTable");
//                                objekt.setValue(simpleTable);
//                                objekt.setType(simpleTable.getClass());
//                                request.addProperty(objekt);


//                                Read read = new Read();
//                                read.setProperty(0,"1");
                                //request.addProperty("No","1");
                                //request.addProperty("Primary_Key","1");


//                                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                                String filename = "Shipping.png";
//                                File file = new File(dir, filename);
//                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                                Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
//                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                                byte[] bytes = byteArrayOutputStream.toByteArray();
//                                String base64String = Base64.encodeToString(bytes,0,bytes.length,0);
//                                request.addProperty("precPictureTest","300;U8MAAACJATEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=8;552988500;v ");
//                                request.addProperty("no","1");
//                                request.addProperty("picture1",base64String);
                                //

                                // request.addProperty("eSignRec","12;UcMAAACHoQ8=6;8825450;");
//                                request.addProperty("driverSign","dfdsf");
                                //   envelope.addMapping(namespace,"SendPicture",new SendPicture().getClass());

//                                request.addProperty("shippingSign","");
//                                request.addProperty("receiverSign","");
//                                request.addProperty("picture1","");
//                                request.addProperty("picture2","");
//                                request.addProperty("picture3","");
//                                request.addProperty("comment","From Android");


//                                PropertyInfo objekt = new PropertyInfo();
//                                objekt.setName("SimpleTable");
//                                objekt.setValue(simpleTable);
//                                objekt.setType(simpleTable.getClass());
//                                request.addProperty(objekt);
//
//                                PropertyInfo objekt = new PropertyInfo();
//                                objekt.setName("precSalesinvoice");
//                                objekt.setValue("12;UcMAAACHoQ8=6;8755790;");
//                                objekt.setType(PropertyInfo.STRING_CLASS);
//                                request.addProperty(objekt);

//                                request.addProperty("Document_Type", 1);
//                                request.addProperty("No", "H2");
////
//                                PropertyInfo objekt = new PropertyInfo();
//                                objekt.setName("precSalesShipment");
//                                objekt.setValue("300;bgAAAACJ/1NTODgxOTgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=8;582702870;");
//                                objekt.setType(PropertyInfo.STRING_CLASS);
//                                request.addProperty(objekt);
//                                PropertyInfo objekt2 = new PropertyInfo();
//                                objekt.setName("shipmentNo");
//                                objekt.setValue("SS88198");
//                                objekt.setType(PropertyInfo.STRING_CLASS);
//                                request.addProperty(objekt2);



//                                PropertyInfo objekt = new PropertyInfo();
//                                objekt.setName("precSalesshipment");
//                                objekt.setValue("300;bgAAAACJ/1NTODIyNjUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=8;553023510;");
//                                objekt.setType(PropertyInfo.STRING_CLASS);
//                                request.addProperty(objekt);
//
//                                request.addProperty("precPictureTest","300;U8MAAACJATEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=8;552675110;");
//                                request.addProperty("no","1");
//                                request.addProperty("picture1"," ");

//                                PropertyInfo objekt = new PropertyInfo();
//                                objekt.setName("precSalesorder");
//                                objekt.setValue("300;U8MAAACJATEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=8;552674780;");
//                                objekt.setName("no");
//                                objekt.setType(PropertyInfo.STRING_CLASS);
//                                request.addProperty(objekt);
//
//                                PropertyInfo objekt1 = new PropertyInfo();
//                                objekt.setName("driverSign");
//                                objekt.setValue("");
//                                objekt.setType(PropertyInfo.STRING_CLASS);
//                                request.addProperty(objekt1);

                                request.addProperty("precSalesShipment", "300;bgAAAACJ/1NTODgxOTgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=8;582702870;");
                                request.addProperty("shipmentNo", "SS88198");

//                                NtlmTransport ntlm = new NtlmTransport(url, "administrator", "Topgun123", "", "");
                                NtlmTransport ntlm = new NtlmTransport(url, "administrator", "Rameela1", "", "");
                                ntlm.debug = true;
                                ntlm.call(soap_action, envelope);// Receive Error here!
                                Object d = envelope.getResponse();
//                                SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
//                                great = d.toString();
//                                System.out.println(great);
                                // envelope.addMapping(namespace,"WS_SalesOrderListsResponse", WS_SalesOrderListsResponse.class);
                                // envelope.addMapping(namespace,"WS_SalesOrderListsResult", WS_SalesOrderListsResult.class);


//                                 int i = result.getPropertyCount();
//                                for (int j = 0; j < i ; j++) {
//                                    PropertyInfo pi = new PropertyInfo();
//                                    result.getPropertyInfo(j, pi);
//                                    Class aClass = com.jssai.warehousepick.Model.SimpleTable.class;
//                                    Object oo = aClass.newInstance();
//                                    Field field = aClass.getField(pi.name);
//                                    field.setAccessible(true);
//                                    field.set(oo,pi.getValue().toString());
//                                    Log.d("ssdf","sdfs");
//
//                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                great = e.toString();
                                System.out.println(great);
                            }


                        }
                    }).start();
                    break;
                case "SOAP":
                    String methodName = intent.getStringExtra("methodname");
                    Class aClass = (Class) intent.getSerializableExtra("class");
                    String url_soap = "";
                    try {
                        url_soap = getUrl(aClass);
                        String nameSpace = getnameSpace(aClass);
                        String actionUrl;
                        if(!methodName.equalsIgnoreCase("PDFConverter"))
                            actionUrl = nameSpace + ":" + methodName;
                        else
                            actionUrl = nameSpace + methodName;
                        SoapObject request = new SoapObject(nameSpace, methodName);
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.dotNet = true;
                        envelope.implicitTypes = true;
                        envelope.setOutputSoapObject(request);
                        Object o = intent.getSerializableExtra("objecto");
                        if (o != null) {
                            PropertyInfo objekt = new PropertyInfo();
                            objekt.setName(o.getClass().getSimpleName());
                            objekt.setValue(o);
                            objekt.setType(o.getClass());
                            request.addProperty(objekt);
                        }
                        Map<String, String> params = (Map<String, String>) intent.getSerializableExtra("params");
                        if (params != null) {
                            for (Map.Entry<String, String> entry : params.entrySet()) {
                                request.addProperty(entry.getKey(), entry.getValue());
                            }
                        }

                        NtlmTransport ntlm = new NtlmTransport(url_soap, username, password, "", "");
                        ntlm.debug = true;
                        ntlm.call(actionUrl, envelope); // Receive Error here!
                        //Log.e("dump response: " ,ntlm.responseDump);
                        //Object oooo = envelope.getResponse();
                        if(methodName.equalsIgnoreCase("PDFConverter")){
                            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
                            if (result == null) {
                                Message message = Message.obtain();
                                message.arg1 = 3;
                                messenger.send(message);
                                stopSelf();
                            }else{
                                Log.i("test","test");
                                Message message = Message.obtain();
                                message.arg1 = 1;
                                Bundle bundle = new Bundle();
                                WS_GetPDFContent pdfContent = new WS_GetPDFContent();
                                pdfContent.setMessage(result.toString());
                                bundle.putSerializable(Constants.MESSAGE, (Serializable) pdfContent);
                                message.setData(bundle);
                                messenger.send(message);
                            }

                        }else if(methodName.equalsIgnoreCase("PostedShipment")){
                            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
                            Message message = Message.obtain();
                            message.arg1 = 1;
                            Bundle bundle = new Bundle();
                            if (result != null)
                            bundle.putString("BOLPdf",result.toString());
                            message.setData(bundle);
                            messenger.send(message);
                            stopSelf();
                        } else{
                            SoapObject result = (SoapObject) envelope.getResponse();
                            if (result == null) {
                                Message message = Message.obtain();
                                message.arg1 = 3;
                                messenger.send(message);
                                stopSelf();
                            }
                            if (result != null) {
                                int i = result.getPropertyCount();
                                Object finalObject = new Object();
                                Object oo = aClass.newInstance();
                                ArrayList<Object> objects = new ArrayList<Object>();
                                for (int j = 0; j < i; j++) {
                                    Object testObject = result.getProperty(j);
                                    if (testObject instanceof SoapObject) {
                                        SoapObject soapObject = (SoapObject) result.getProperty(j);
                                        int l = soapObject.getPropertyCount();
                                        Object ooo = aClass.newInstance();
                                        for (int k = 0; k < l; k++) {
                                            PropertyInfo pi = new PropertyInfo();
                                            soapObject.getPropertyInfo(k, pi);
                                            Field field = null;
                                            try {
                                                field = aClass.getField(pi.name);
                                            } catch (NoSuchFieldException e) {
                                                e.printStackTrace();
                                                continue;
                                            }

                                            field.setAccessible(true);
                                            field.set(ooo, pi.getValue().toString());
                                        }
                                        objects.add(ooo);
                                        finalObject = objects;
                                    } else {
                                        PropertyInfo pi = new PropertyInfo();
                                        result.getPropertyInfo(j, pi);
                                        Field field = null;
                                        try {
                                            field = aClass.getField(pi.name);
                                        } catch (NoSuchFieldException e) {
                                            e.printStackTrace();
                                            continue;
                                        }
                                        field.setAccessible(true);
                                        field.set(oo, pi.getValue().toString());
                                        finalObject = oo;

                                    }
                                }
                                Message message = Message.obtain();
                                message.arg1 = 1;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.MESSAGE, (Serializable) oo);
                                message.setData(bundle);
                                messenger.send(message);
                            }

                        }


                    } catch (SoapFault soapFault) {
                        logBundle.putString("MethodName", methodName);
                        logBundle.putString("url", url_soap);
                        logBundle.putString("Error", soapFault.faultstring);
                        soapFault.printStackTrace();
                        Message message = Message.obtain();
                        message.arg1 = 0;
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.MESSAGE, soapFault.faultstring);
                        message.setData(bundle);
                        try {
                            messenger.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } catch (XmlPullParserException e) {
                        logBundle.putString("MethodName", methodName);
                        logBundle.putString("url", url_soap);
                        logBundle.putString("Error", "XmlPullParserException " + e.getClass());
                        e.printStackTrace();
                        Message message = Message.obtain();
                        message.arg1 = 0;
                        try {
                            messenger.send(message);
                        } catch (RemoteException e1) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        logBundle.putString("MethodName", methodName);
                        logBundle.putString("url", url_soap);
                        logBundle.putString("Error", "IOException " + e.getClass());
                        Message message = Message.obtain();
                        message.arg1 = 0;
                        try {
                            messenger.send(message);
                        } catch (RemoteException e1) {
                            e.printStackTrace();
                        }
                    } catch (IllegalAccessException e) {
                        logBundle.putString("MethodName", methodName);
                        logBundle.putString("url", url_soap);
                        logBundle.putString("Error", "IllegalAccessException " + e.getClass());
                        e.printStackTrace();
                        Message message = Message.obtain();
                        message.arg1 = 0;
                        try {
                            messenger.send(message);
                        } catch (RemoteException e1) {
                            e.printStackTrace();
                        }
                    } catch (InstantiationException e) {
                        logBundle.putString("MethodName", methodName);
                        logBundle.putString("url", url_soap);
                        logBundle.putString("Error", "InstantiationException " + e.getClass());
                        e.printStackTrace();
                        Message message = Message.obtain();
                        message.arg1 = 0;
                        try {
                            messenger.send(message);
                        } catch (RemoteException e1) {
                            e.printStackTrace();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        logBundle.putString("MethodName", methodName);
                        logBundle.putString("url", url_soap);
                        logBundle.putString("Error", "RemoteException " + e.getClass());
                        Message message = Message.obtain();
                        message.arg1 = 0;
                        try {
                            messenger.send(message);
                        } catch (RemoteException e1) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        logBundle.putString("MethodName", methodName);
                        logBundle.putString("url", url_soap);
                        String s = e.getMessage();
                        String ss = e.getMessage();
                        logBundle.putString("Error", "Exception " + e.getClass().toString());
                        Message message = Message.obtain();
                        message.arg1 = 0;
                        try {
                            messenger.send(message);
                        } catch (RemoteException e1) {
                            e.printStackTrace();
                        }
                    } finally {
                        try{
                            mFirebaseAnalytics.logEvent("NetworkService", logBundle);
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                    break;

                case "SOAP Picture":
                    String methodNamePicture = intent.getStringExtra("methodname");
                    Class aClassPicture = (Class) intent.getSerializableExtra("class");
                    String url_soap_picture = "";
                    try {
                        url_soap_picture = getUrl(aClassPicture);
                        String nameSpace = getnameSpace(aClassPicture);
                        String actionUrl = nameSpace + ":" + methodNamePicture;
                        SoapObject request = new SoapObject(nameSpace, methodNamePicture);
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.dotNet = true;
                        envelope.implicitTypes = true;
                        Object o = intent.getSerializableExtra("objecto");
                        if (o != null) {
                            PropertyInfo objekt = new PropertyInfo();
                            objekt.setName(o.getClass().getSimpleName());
                            objekt.setValue(o);
                            objekt.setType(o.getClass());
                            request.addProperty(objekt);
                        }
                        String type = intent.getStringExtra("type");
                        String uri = intent.getStringExtra("uri");
                        String key = intent.getStringExtra("key");
                        Map<String, String> params = processImage(type, uri, key);
                        if (params != null) {
                            for (Map.Entry<String, String> entry : params.entrySet()) {
                                request.addProperty(entry.getKey(), entry.getValue());
                            }
                        }
                        envelope.setOutputSoapObject(request);
                        NtlmTransport ntlm = new NtlmTransport(url_soap_picture, username, password, "", "");
                        ntlm.debug = true;
                        ntlm.call(actionUrl, envelope); // Receive Error here!
//                        Object oooo = envelope.getResponse();
                        SoapObject result = (SoapObject) envelope.getResponse();
                        if (result == null) {
                            Message message = Message.obtain();
                            message.arg1 = 3;
                            messenger.send(message);
                            stopSelf();
                        }
                        if (result != null) {
                            int i = result.getPropertyCount();
                            Object finalObject = new Object();
                            Object oo = aClassPicture.newInstance();
                            ArrayList<Object> objects = new ArrayList<Object>();
                            for (int j = 0; j < i; j++) {
                                Object testObject = result.getProperty(j);
                                if (testObject instanceof SoapObject) {
                                    SoapObject soapObject = (SoapObject) result.getProperty(j);
                                    int l = soapObject.getPropertyCount();
                                    Object ooo = aClassPicture.newInstance();
                                    for (int k = 0; k < l; k++) {
                                        PropertyInfo pi = new PropertyInfo();
                                        soapObject.getPropertyInfo(k, pi);
                                        Field field = null;
                                        try {
                                            field = aClassPicture.getField(pi.name);
                                        } catch (NoSuchFieldException e) {
                                            e.printStackTrace();
                                            continue;
                                        }

                                        field.setAccessible(true);
                                        field.set(ooo, pi.getValue().toString());
                                    }
                                    objects.add(ooo);
                                    finalObject = objects;
                                } else {
                                    PropertyInfo pi = new PropertyInfo();
                                    result.getPropertyInfo(j, pi);
                                    Field field = null;
                                    try {
                                        field = aClassPicture.getField(pi.name);
                                    } catch (NoSuchFieldException e) {
                                        e.printStackTrace();
                                        continue;
                                    }
                                    field.setAccessible(true);
                                    field.set(oo, pi.getValue().toString());
                                    finalObject = oo;

                                }
                            }
                            Message message = Message.obtain();
                            message.arg1 = 1;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.MESSAGE, (Serializable) oo);
                            message.setData(bundle);
                            messenger.send(message);
                            Log.d("sdfds", finalObject.toString());
                        }


                    } catch (SoapFault soapFault) {
                        soapFault.printStackTrace();
                        logBundle.putString("MethodName", methodNamePicture);
                        logBundle.putString("url", url_soap_picture);
                        logBundle.putString("Error", soapFault.faultstring);
                        Message message = Message.obtain();
                        message.arg1 = 0;
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.MESSAGE, soapFault.faultstring);
                        message.setData(bundle);
                        try {
                            messenger.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                        logBundle.putString("MethodName", methodNamePicture);
                        logBundle.putString("url", url_soap_picture);
                        logBundle.putString("Error", "XmlPullParserException " + e.getClass());
                        Message message = Message.obtain();
                        message.arg1 = 0;
                        try {
                            messenger.send(message);
                        } catch (RemoteException e1) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        logBundle.putString("MethodName", methodNamePicture);
                        logBundle.putString("url", url_soap_picture);
                        logBundle.putString("Error", "IOException " + e.getClass());
                        e.printStackTrace();
                        Message message = Message.obtain();
                        message.arg1 = 0;
                        try {
                            messenger.send(message);
                        } catch (RemoteException e1) {
                            e.printStackTrace();
                        }
                    } catch (IllegalAccessException e) {
                        logBundle.putString("MethodName", methodNamePicture);
                        logBundle.putString("url", url_soap_picture);
                        logBundle.putString("Error", "IllegalAccessException " + e.getClass());
                        e.printStackTrace();
                        Message message = Message.obtain();
                        message.arg1 = 0;
                        try {
                            messenger.send(message);
                        } catch (RemoteException e1) {
                            e.printStackTrace();
                        }
                    } catch (InstantiationException e) {
                        logBundle.putString("MethodName", methodNamePicture);
                        logBundle.putString("url", url_soap_picture);
                        logBundle.putString("Error", "InstantiationException " + e.getClass());
                        e.printStackTrace();
                        Message message = Message.obtain();
                        message.arg1 = 0;
                        try {
                            messenger.send(message);
                        } catch (RemoteException e1) {
                            e.printStackTrace();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        logBundle.putString("MethodName", methodNamePicture);
                        logBundle.putString("url", url_soap_picture);
                        logBundle.putString("Error", "RemoteException " + e.getClass());
                        Message message = Message.obtain();
                        message.arg1 = 0;
                        try {
                            messenger.send(message);
                        } catch (RemoteException e1) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        logBundle.putString("MethodName", methodNamePicture);
                        logBundle.putString("url", url_soap_picture);
                        logBundle.putString("Error", "Exception " + e.getClass());
                        Message message = Message.obtain();
                        message.arg1 = 0;
                        try {
                            messenger.send(message);
                        } catch (RemoteException e1) {
                            e.printStackTrace();
                        }
                    }
                    break;


            }
        }
    }

    public String getnameSpace(Class aClass) {
        String name = aClass.getSimpleName();
        switch (name) {
            case "WS_eSignOrder":
                return NavionUrl.WS_eSignOrder.NAMESPACE;
            case "WS_PostSalesOrder":
                return "urn:microsoft-dynamics-schemas/page/ws_postsalesorder";
            case "WS_GetPDFContent":
                return "http://tempuri.org/";
            case "WS_SalesShipment":
                return "urn:microsoft-dynamics-schemas/page/ws_salesshipment";
            case "WS_PostedeSignOrder": // #1 Begin To get SSNO
                return NavionUrl.WS_eSignOrder.POSTED_ESIGN_ORDER_NAMESPACE;
            // #1 End
            default:
                return "";


        }
    }


    public String getUrl(Class aClass) throws MalformedURLException {
        String name = aClass.getSimpleName();
        switch (name) {
            case "WS_eSignOrder":
                return GlobalMethods.getSOAP(getApplicationContext(), "WS_eSignOrder");
            case "WS_PostSalesOrder":
                return GlobalMethods.getSOAP9(getApplicationContext(), "WS_PostSalesOrder");
            case "WS_GetPDFContent":
                return GlobalMethods.getPDFSOAP(getApplicationContext());
            case "WS_SalesShipment":
                return GlobalMethods.getSOAP9(getApplicationContext(),"WS_SalesShipment");
            case "WS_PostedeSignOrder": // #1 Begin To get SSNO
                return GlobalMethods.getSOAP(getApplicationContext(),"WS_PostedeSignOrder");
            case "WS_WarehousePickList": // #1 Begin To get SSNO
                return "http://76.117.133.4:7047/DynamicsNAVR2/WS/PDP%20LLC%20USA/Page/WS_Warehousepicklist";
            // #1 End
            default:
                return "";


        }
    }

    public void processData(HttpResponse response, Messenger messenger) {
        int responseCode = response.getStatusLine().getStatusCode();
        String responseBody = "";
        if (responseCode != 0) {
            int firstDigit = Integer.parseInt(Integer.toString(responseCode).substring(0, 1));
            try {
                switch (firstDigit) {
                    case 2:
                        responseBody = EntityUtils.toString(response.getEntity());
                        Log.i(Constants.TAG, "responseBody =>>>>>>>>>>>>>" + responseBody + "Status Code ==>" + responseCode);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(Constants.MESSAGE, responseBody);
                        Message message = new Message();
                        message.arg1 = 1;
                        message.setData(bundle1);
                        messenger.send(message);
                        break;
                    case 4:
                        responseBody = EntityUtils.toString(response.getEntity());
                        Log.i(Constants.TAG, "responseBody =>>>>>>>>>>>>>" + responseBody + "Status Code ==>" + responseCode);
                        Bundle errorBundle = new Bundle();
                        errorBundle.putString(Constants.MESSAGE, responseBody);
                        Message errorMessage = new Message();
                        errorMessage.arg1 = 0;
                        errorMessage.setData(errorBundle);
                        messenger.send(errorMessage);
                        break;
                    default:
                        Log.i(Constants.TAG, "responseBody =>>>>>>>>>>>>>" + responseBody + "Status Code ==>" + responseCode);
                        Bundle defaultBundle = new Bundle();
                        defaultBundle.putString(Constants.MESSAGE, "Something Went Wrong !!!!");
                        Message defaultMessage = new Message();
                        defaultMessage.arg1 = 0;
                        defaultMessage.setData(defaultBundle);
                        messenger.send(defaultMessage);

                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public HashMap<String, String> processImage(String type, String uri, String Key) {
        HashMap<String, String> params = new HashMap<>();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uri));
//            bitmap = bitmap.createScaledBitmap(bitmap, 900, 500,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bos);
        String base64 = Base64.encodeToString(bos.toByteArray(), 0, bos.toByteArray().length, 0);

        switch (type) {
            case "Picture1":
                params.put("eSignRec", Key);
                params.put("driverSign", "");
                params.put("shippingSign", "");
                params.put("receiverSign", "");
                params.put("picture1", base64);
                params.put("picture2", "");
                params.put("picture3", "");
                params.put("comment", "");
                break;
            case "Picture2":
                params.put("eSignRec", Key);
                params.put("driverSign", "");
                params.put("shippingSign", "");
                params.put("receiverSign", "");
                params.put("picture1", "");
                params.put("picture2", base64);
                params.put("picture3", "");
                params.put("comment", "");
                break;
            case "Picture3":
                params.put("eSignRec", Key);
                params.put("driverSign", "");
                params.put("shippingSign", "");
                params.put("receiverSign", "");
                params.put("picture1", "");
                params.put("picture2", "");
                params.put("picture3", base64);
                params.put("comment", "");
                break;
        }
        return params;
    }


}
