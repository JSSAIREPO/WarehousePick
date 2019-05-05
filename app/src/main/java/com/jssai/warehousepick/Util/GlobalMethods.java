package com.jssai.warehousepick.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.jssai.warehousepick.Model.WS_eSignOrder;
import com.jssai.warehousepick.Model.User;
import com.jssai.warehousepick.services.WebService;

/**
 * Created by Pragadees on 16/11/16.
 */

public class GlobalMethods {

    public static SharedPreferences getSharedPrefrence(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PPREFERENCES_PERMANENT, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static ProgressDialog getProgressDialog(@NonNull Context context, @Nullable String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        if (message != null) {
            progressDialog.setMessage(message);
        } else {
            progressDialog.setMessage("Loading..,");
        }
        return progressDialog;
    }

    public static ArrayList<WS_eSignOrder> getESignOrders(String responseBody) throws JSONException {
        JSONObject jsonObject = new JSONObject(responseBody);
        String array = jsonObject.getString("value");
        ArrayList<WS_eSignOrder> WSeSignOrders = new ArrayList<>();
        if (!array.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<WS_eSignOrder>>() {
            }.getType();
            WSeSignOrders = gson.fromJson(array, type);
        }
        return WSeSignOrders;
    }

    public static User getUser(Context context) {
        SharedPreferences sharedPreferences = getSharedPrefrence(context);
        User user = new User(sharedPreferences.getString(Constants.USER, ""), sharedPreferences.getString(Constants.PASSWORD, ""));
        return user;
    }

    public static String getOdataUrl(Context context, String page) throws MalformedURLException {
        SharedPreferences sharedPreferences = getSharedPrefrence(context);
        String ip = getIp(sharedPreferences);
        String databasename = getDatabase(sharedPreferences);
        String companyname = getCompanyName(context);
        String url = "";
        String odataPort = sharedPreferences.getString(Constants.OPORT, "8048");
        String baseUrl = "/" + databasename + "/OData/Company('" + companyname + "')/" + page + "?$format=json";
//        if(!TextUtils.isEmpty(ip)){
//            url = "http://"+ip+":"+odataPort+""+baseUrl;
//        }else
        if (!TextUtils.isEmpty(ip)) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http");
            builder.encodedAuthority(ip + ":" + odataPort);
            builder.appendPath(databasename);
            builder.appendPath("OData");
            builder.appendPath("Company('" + companyname + "')");
            builder.appendPath(page);
            url = builder.build().toString() + "?$format=json";
            return url;
        }
        {
            throw new MalformedURLException(url);
        }


    }

    public static String getSOAP(Context context, String page) throws MalformedURLException {
        SharedPreferences sharedPreferences = getSharedPrefrence(context);
        String ip = getIp(sharedPreferences);
        String soapPort = sharedPreferences.getString(Constants.SPORT, "8047");
        String databasename = getDatabase(sharedPreferences);
        String companyname = getCompanyName(context);
        String url = "";
        String baseUrl = "/" + databasename + "/WS/" + companyname + "/Page/" + page + "";
//        if(!TextUtils.isEmpty(ip)){
//        url = "http://"+ip+":"+soapPort+""+baseUrl;
//        }else {
//            throw new  MalformedURLException(url);
//        }
        if (!TextUtils.isEmpty(ip)) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http");
            builder.encodedAuthority(ip + ":" + soapPort);
            builder.appendPath(databasename);
            builder.appendPath("WS");
            builder.appendPath(companyname);
            builder.appendPath("Page");
            builder.appendPath(page);
            url = builder.build().toString();
            return url;
        }
        {
            throw new MalformedURLException(url);
        }

    }

    public static String getRESTURL(Context context, String URL) throws MalformedURLException {
        String apiURL = "";
        SharedPreferences sharedPreferences = getSharedPrefrence(context);
        String ip = getIp(sharedPreferences);
        apiURL = "http://" + ip + URL;
        return apiURL;
    }

    public static String getSOAP9(Context context, String page) throws MalformedURLException {
        SharedPreferences sharedPreferences = getSharedPrefrence(context);
        String ip = sharedPreferences.getString(Constants.IP9, "");
        String soapPort = sharedPreferences.getString(Constants.PORT9, "7047");
        String databasename = sharedPreferences.getString(Constants.DATABASENAME9, "");
        String companyname = sharedPreferences.getString(Constants.COMPANYNAME9, "");
        String url = "";
        String baseUrl = "/" + databasename + "/WS/" + companyname + "/Page/" + page + "";
//        if(!TextUtils.isEmpty(ip)){
//        url = "http://"+ip+":"+soapPort+""+baseUrl;
//        }else {
//            throw new  MalformedURLException(url);
//        }
        if (!TextUtils.isEmpty(ip)) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http");
            builder.encodedAuthority(ip + ":" + soapPort);
            builder.appendPath(databasename);
            builder.appendPath("WS");
            builder.appendPath(companyname);
            if (!page.equalsIgnoreCase("Get_PDF_Report"))
                builder.appendPath("Page");
            else
                builder.appendPath("Codeunit");
            builder.appendPath(page);
            url = builder.build().toString();
            return url;
        }
        {
            throw new MalformedURLException(url);
        }

    }

    public static String getPDFSOAP(Context context) throws MalformedURLException {
        SharedPreferences sharedPreferences = getSharedPrefrence(context);
        String ip = sharedPreferences.getString(Constants.IP9, "");
        String soapPort = sharedPreferences.getString(Constants.PDF_PORT, "");
        String url = "";

        if (!TextUtils.isEmpty(ip)) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http");
            builder.encodedAuthority(ip + ":" + soapPort);
            builder.appendPath("WebService.asmx");
            url = builder.build().toString();
            return url;
        }
        {
            throw new MalformedURLException(url);
        }

    }


    public static String getIp(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(Constants.IP, "");
    }

    public static String getIp(Context context) {
        SharedPreferences sharedPreferences = getSharedPrefrence(context);
        return sharedPreferences.getString(Constants.IP, "");
    }

    public static String getDomainName(Context context) {
        SharedPreferences sharedPreferences = getSharedPrefrence(context);
        return sharedPreferences.getString(Constants.DOMAIN_NAME, "");
    }

    public static String getCompanyName(Context context) {
        SharedPreferences sharedPreferences = getSharedPrefrence(context);
        return sharedPreferences.getString(Constants.COMPANYNAME, "");
    }

    public static String getPortNo(Context context) {
        SharedPreferences sharedPreferences = getSharedPrefrence(context);
        return sharedPreferences.getString(Constants.PORT, "");
    }

    public static String getInstanceName(Context context) {
        SharedPreferences sharedPreferences = getSharedPrefrence(context);
        return sharedPreferences.getString(Constants.INSTANCENAME, "");
    }

    public static String getIPName(Context context) {
        SharedPreferences sharedPreferences = getSharedPrefrence(context);
        return sharedPreferences.getString(Constants.IPNAME, "");
    }

    public static String getDatabase(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(Constants.DATABASENAME, "");
    }
}
