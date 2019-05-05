package com.jssai.warehousepick.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import com.jssai.warehousepick.Util.Constants;
import com.jssai.warehousepick.Util.GlobalMethods;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebService extends JobIntentService {
    private static final String TAG = "WebService";
    public static final String RECEIVER = "receiver";
    public static final int SHOW_RESULT = 123;
    private ResultReceiver mResultReceiver;
    public static HashMap<String, String> params = new HashMap<>();

    public static final int GET_WAREHOUSE_LIST = 1;
    public static final int GET_WAREHOUSE_DETAILS = 2;
    public static final int POST_RECORD_PICK_LINES = 3;
    public static final int POST_REGISTER_WAREHOUSE_PICK = 4;
    public static final int GET_LOGIN = 5;

    //http://76.117.133.4
    public static final String URL_GET_WAREHOUSE_LIST = "service.asmx/getWareHouseList";
    public static final String URL_GET_WAREHOUSE_DETAILS = "service.asmx/getWareHouseDetails";
    public static final String URL_POST_RECORD_PICK_LINES = "service.asmx/postRecordPickLines";
    public static final String URL_REGISTER_WAREHOUSE_PICK = "service.asmx/postRegisterWHPick";
    public static final String URL_LOGIN = "service.asmx/getLoginInfo?";
    //public String IP = "76.117.133.4";
    public String IP = "", DOMAIN = "", PORTNO, INSTANCENAME, COMPANYNAME, IPNAME;
    public static String strUser, strPwd;

    public static void enqueueWork(Context context, WorkerResultReceiver workerResultReceiver, int request_code, HashMap<String, String> params) {
        WebService.params = params;
        Intent intent = new Intent(context, WebService.class);
        intent.putExtra(RECEIVER, workerResultReceiver);
        intent.putExtra("purpose", request_code);
        enqueueWork(context, WebService.class, 1, intent);
    }

    public static void logout(Context context) {
        SharedPreferences sharedPreferences = GlobalMethods.getSharedPrefrence(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.USER, "");
        editor.putString(Constants.PASSWORD, "");
        editor.putBoolean(Constants.REMEMBERME, false);
        editor.apply();
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "onHandleWork() called with: intent = [" + intent + "]");
        int purpose = intent.getExtras().getInt("purpose");
        switch (purpose) {
            case GET_WAREHOUSE_LIST:
                mResultReceiver = intent.getParcelableExtra(RECEIVER);
                getCall(URL_GET_WAREHOUSE_LIST, purpose);
                break;
            case GET_WAREHOUSE_DETAILS:
                mResultReceiver = intent.getParcelableExtra(RECEIVER);
                getCall(URL_GET_WAREHOUSE_DETAILS, purpose);
                break;
            case GET_LOGIN:
                String username = params.get("username");
                String password = params.get("password");
                mResultReceiver = intent.getParcelableExtra(RECEIVER);
                login(URL_LOGIN, purpose, username, password);
                break;
            case POST_RECORD_PICK_LINES:
                mResultReceiver = intent.getParcelableExtra(RECEIVER);
                postCall(URL_POST_RECORD_PICK_LINES, purpose, params);
                break;
            case POST_REGISTER_WAREHOUSE_PICK:
                mResultReceiver = intent.getParcelableExtra(RECEIVER);
                postCall(URL_REGISTER_WAREHOUSE_PICK, purpose, params);
                break;
            default:
                break;
        }
    }

    private void getCall(String url, int purpose) {
        IP = GlobalMethods.getIp(this);
        DOMAIN = GlobalMethods.getDomainName(this);
        COMPANYNAME = GlobalMethods.getCompanyName(this);
        PORTNO = GlobalMethods.getPortNo(this);
        INSTANCENAME = GlobalMethods.getInstanceName(this);
        IPNAME = GlobalMethods.getIPName(this);


        url = "http://" + IP + "/" + DOMAIN + "/" + url;
        SharedPreferences sharedPreferences = GlobalMethods.getSharedPrefrence(getApplicationContext());
        strUser = sharedPreferences.getString(Constants.USER, "");
        strPwd = sharedPreferences.getString(Constants.PASSWORD, "");
        url = url + "?strUser=" + strUser + "&strPwd=" + strPwd + "&PortNo=" + PORTNO + "&CompanyName=" + COMPANYNAME + "&IP=" + IPNAME + "&InstanceName=" + INSTANCENAME;
        url = url.replace(" ", "%20");
        DefaultHttpClient httpclient = new DefaultHttpClient();
/*        httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
        httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new NTCredentials("Administrator", "Rameela1", "", ""));*/
        HttpGet request = new HttpGet();
        URI website = null;
        try {
            website = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        request.setURI(website);
        HttpResponse response = null;
        try {
            response = httpclient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String json = entityToString(response.getEntity());
            Bundle bundle = new Bundle();
            bundle.putString("response", json);
            bundle.putInt("purpose", purpose);
            bundle.putInt("requestCode", purpose);
            mResultReceiver.send(SHOW_RESULT, bundle);
            Log.d("", "");
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void login(String url, int purpose, String userId, String password) {
        IP = GlobalMethods.getIp(this);
        DOMAIN = GlobalMethods.getDomainName(this);
        COMPANYNAME = GlobalMethods.getCompanyName(this);
        PORTNO = GlobalMethods.getPortNo(this);
        INSTANCENAME = GlobalMethods.getInstanceName(this);
        IPNAME = GlobalMethods.getIPName(this);

        url = "http://" + IP + "/" + DOMAIN + "/" + url + "strUser=" + userId + "&strPwd=" + password + "&PortNo=" + PORTNO + "&CompanyName=" + COMPANYNAME + "&IP=" + IPNAME + "&InstanceName=" + INSTANCENAME;
        DefaultHttpClient httpclient = new DefaultHttpClient();
/*        httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
        httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new NTCredentials("Administrator", "Rameela1", "", ""));*/
        HttpGet request = new HttpGet();
        URI website = null;
        url = url.replace(" ", "%20");
        try {
            website = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        request.setURI(website);
        HttpResponse response = null;
        try {
            response = httpclient.execute(request);
            try {
                //BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String json = entityToString(response.getEntity());
                Bundle bundle = new Bundle();
                bundle.putString("response", json);
                bundle.putInt("purpose", purpose);
                bundle.putInt("requestCode", purpose);
                mResultReceiver.send(SHOW_RESULT, bundle);
                Log.d("", "");
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    private void postCall(String url, int requestCode, HashMap<String, String> params) {
        IP = GlobalMethods.getIp(this);
        DOMAIN = GlobalMethods.getDomainName(this);
        COMPANYNAME = GlobalMethods.getCompanyName(this);
        PORTNO = GlobalMethods.getPortNo(this);
        INSTANCENAME = GlobalMethods.getInstanceName(this);
        IPNAME = GlobalMethods.getIPName(this);

        url = "http://" + IP + "/" + DOMAIN + "/" + url;
        OkHttpClient client = new OkHttpClient();

        FormBody.Builder builder = new FormBody.Builder();
        SharedPreferences sharedPreferences = GlobalMethods.getSharedPrefrence(getApplicationContext());
        final String strUser = sharedPreferences.getString(Constants.USER, "");
        final String strPwd = sharedPreferences.getString(Constants.PASSWORD, "");
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
        builder.add("strUser", strUser);
        builder.add("strPwd", strPwd);
        builder.add("PortNo", PORTNO);
        builder.add("CompanyName", COMPANYNAME);
        builder.add("IP", IPNAME);
        builder.add("InstanceName", INSTANCENAME);

        FormBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            Log.e("response", response.toString());
            Bundle bundle = new Bundle();
            bundle.putString("response", response.body().string());
            bundle.putInt("code", response.code());
            bundle.putInt("requestCode", requestCode);
            mResultReceiver.send(SHOW_RESULT, bundle);
            Log.d("", "");
            // Do something with the response.
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public static String entityToString(HttpEntity entity) throws IOException {
        InputStream is = entity.getContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder str = new StringBuilder();

        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                str.append(line + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return str.toString();
    }
}
