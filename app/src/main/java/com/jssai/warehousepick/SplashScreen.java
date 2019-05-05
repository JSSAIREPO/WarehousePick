package com.jssai.warehousepick;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.HashMap;

import com.google.gson.JsonSyntaxException;
import com.jssai.warehousepick.Model.LoginResponse;
import com.jssai.warehousepick.Util.Constants;
import com.jssai.warehousepick.Util.GlobalMethods;
import com.jssai.warehousepick.services.WebService;
import com.jssai.warehousepick.services.WorkerResultReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.masconsult.android_ntlm.NTLMSchemeFactory;

import static com.jssai.warehousepick.services.WebService.SHOW_RESULT;

public class SplashScreen extends AppCompatActivity implements WorkerResultReceiver.Receiver {

    @BindView(R.id.versionText)
    TextView versionText;
    SharedPreferences sharedPreferences;
    String username, password, ip;
    private WorkerResultReceiver mWorkerResultReceiver;
    private boolean rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        sharedPreferences = GlobalMethods.getSharedPrefrence(getApplicationContext());
        username = sharedPreferences.getString(Constants.USER, "");
        password = sharedPreferences.getString(Constants.PASSWORD, "");
        ip = sharedPreferences.getString(Constants.IP, "");
        rememberMe = sharedPreferences.getBoolean(Constants.REMEMBERME, false);
        mWorkerResultReceiver = new WorkerResultReceiver(new Handler());
        mWorkerResultReceiver.setReceiver(this);
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(ip)) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    moveToLoginPage();
                }
            }, 3000);

        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (rememberMe) {
                        login();
                    } else {
                        finish();
                        moveToLoginPage();
                    }
                }
            }, 1500);
        }
    }

    private void login() {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        WebService.enqueueWork(this, mWorkerResultReceiver, WebService.GET_LOGIN, params);
    }

    private void moveToLoginPage() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        if (!version.isEmpty()) {
            versionText.setText("v" + version);
        }
/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = GlobalMethods.getSharedPrefrence(getApplicationContext());
                boolean remeberMe = sharedPreferences.getBoolean(Constants.REMEMBERME, false);
                if (remeberMe) {
                    final String username = sharedPreferences.getString(Constants.USER, "");
                    final String password = sharedPreferences.getString(Constants.PASSWORD, "");
                    if (!username.equals("")) {
                        if (!password.equals("")) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
                                    defaultHttpClient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
                                    defaultHttpClient.getCredentialsProvider().setCredentials(
                                            new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                                            new NTCredentials(username, password, "", ""));
                                    try {
                                        String url = GlobalMethods.getOdataUrl(getApplicationContext(), "WS_eSignOrder");
                                        HttpGet httpHead = new HttpGet(url);
                                        httpHead.addHeader("Content-Type", "application/json");
                                        HttpResponse httpResponse = defaultHttpClient.execute(httpHead);
                                        String responseBody = EntityUtils.toString(httpResponse.getEntity());
                                        String responseCode = String.valueOf(httpResponse.getStatusLine().getStatusCode());
                                        Log.i(Constants.TAG, "responseBody =>>>>>>>>>>>>>" + responseBody + "Status Code ==>" + responseCode);
                                        Bundle bundle1 = new Bundle();
                                        bundle1.putInt("STATUSCODE", httpResponse.getStatusLine().getStatusCode());
                                        bundle1.putString(Constants.MESSAGE, responseBody);
                                        Message message = new LoginHandler(Looper.getMainLooper(), new WeakReference<SplashScreen>(SplashScreen.this)).obtainMessage();
                                        message.setData(bundle1);
                                        message.sendToTarget();
                                    } catch (MalformedURLException e) {
                                        Intent intent = new Intent(SplashScreen.this, Login.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                        e.printStackTrace();
                                    } catch (final ClientProtocolException e) {
                                        SplashScreen.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                e.printStackTrace();
                                                Intent intent = new Intent(SplashScreen.this, Login.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                                Toast.makeText(SplashScreen.this, "Something Went Wrong...", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    } catch (final IOException e) {
                                        SplashScreen.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                FirebaseCrash.logcat(Log.ERROR, Constants.TAG, "Ip Error");
                                                FirebaseCrash.report(e);
                                                e.printStackTrace();
                                                Intent intent = new Intent(SplashScreen.this, Login.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                                Toast.makeText(SplashScreen.this, "Something Went Wrong...", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    }

                                }
                            }).start();


                        } else {

                        }

                    } else {


                    }
                } else {
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

            }
        }, 500);*/

        // NetworkService.SOAP("ReadMultiple",this,new Handler(), WS_AppConfigs.class,null,null);
//        if(!userName.equals("")){
//            Intent intent = new Intent(this,MainScreen.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            finish();
//        }else {
//            Intent intent = new Intent(this,Login.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            finish();
//        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case SHOW_RESULT:
                if (resultData != null) {
                    String json = resultData.getString("response");
                    json = json.replace("[", "");
                    json = json.replace("]", "");
                    parseResponse(json);
                } else {
                    logout();
                }
                break;
            default:
                break;
        }
    }

    private void logout() {
        WebService.logout(this);
        finish();
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void parseResponse(String json) {
        Gson gson = new Gson();
        try {
            LoginResponse loginResponse = gson.fromJson(json, LoginResponse.class);
            if (loginResponse != null && !TextUtils.isEmpty(loginResponse.getKey())) {
                moveToWarehouseListPage();
            } else {
                logout();
                Toast.makeText(this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (IllegalStateException e) {
            logout();
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        } catch (JsonSyntaxException e) {
            logout();
        }

    }

    private void moveToWarehouseListPage() {
        finish();
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }


    static class LoginHandler extends Handler {
        WeakReference<SplashScreen> splashScreenWeakReference;

        public LoginHandler(Looper looper, WeakReference<SplashScreen> splashScreenWeakReference) {
            super(looper);
            this.splashScreenWeakReference = splashScreenWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SplashScreen splashScreen = splashScreenWeakReference.get();
            if (splashScreen != null) {
                Bundle bundle = msg.getData();
                int statusCode = bundle.getInt("STATUSCODE");
                String responseBody = bundle.getString(Constants.MESSAGE, "");
                if (responseBody == null) {
                    responseBody = "";
                }
                if (statusCode != 0) {
                    int firstDigit = Integer.parseInt(Integer.toString(statusCode).substring(0, 1));
                    switch (firstDigit) {
                        case 2:
                            Intent intent = new Intent(splashScreen.getApplicationContext(), WarehousepickListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            splashScreen.startActivity(intent);
                            splashScreen.finish();
                            break;
                        case 4:
                            if (statusCode == 401) {
                                Toast.makeText(splashScreen, "Username and password doesn't match ", Toast.LENGTH_LONG).show();
                                Intent loginIntent = new Intent(splashScreen, Login.class);
                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                splashScreen.startActivity(loginIntent);
                                splashScreen.finish();
                            }
                            if (statusCode == 400) {
                                if (!responseBody.isEmpty()) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseBody);
                                        JSONObject errorObject = jsonObject.getJSONObject("odata.error");
                                        JSONObject messageObject = errorObject.getJSONObject("message");
                                        String message = messageObject.getString("value");
                                        Toast.makeText(splashScreen, message, Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(splashScreen, "Something Went Wrong....", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(splashScreen, "Something Went Wrong....", Toast.LENGTH_LONG).show();
                                }
                            }
                            break;
                        default:
                            String message = bundle.getString(Constants.MESSAGE);
                            if (message != null) {
                                Toast.makeText(splashScreen, message, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(splashScreen, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            }

                    }
                }
            }
        }
    }

}
