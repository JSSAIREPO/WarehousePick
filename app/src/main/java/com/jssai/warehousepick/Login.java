package com.jssai.warehousepick;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
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

public class Login extends AppCompatActivity implements WorkerResultReceiver.Receiver {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    @BindView(R.id.user)
    EditText userName;
    @BindView(R.id.password)
    EditText password_edittext;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;
    View alertView;
    @BindView(R.id.rememberMeSwitch)
    SwitchCompat rememberMeSwitch;
    FirebaseAnalytics firebaseAnalytics;
    Bundle logBundle = new Bundle();
    private WorkerResultReceiver mWorkerResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // NetworkService.SOAPCREATE("READ",this,new Handler(),null, WS_eSignOrder.class,null);
        ButterKnife.bind(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In....");
        progressDialog.setCancelable(false);
        mWorkerResultReceiver = new WorkerResultReceiver(new Handler());
        mWorkerResultReceiver.setReceiver(this);
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!userName.getText().equals("")) {
                    userName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!password_edittext.getText().equals("")) {
                    password_edittext.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        password_edittext.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                int id = event.getAction();
//                password_edittext.requestFocus();
//                switch (id) {
//                    case MotionEvent.ACTION_DOWN:
//
//                        if (event.getRawX() >= (password_edittext.getRight() - password_edittext.getCompoundDrawables()[2].getBounds().width())) {
//                            password_edittext.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                        } else {
//                            return false;
//                        }
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        if (event.getRawX() >= (password_edittext.getRight() - password_edittext.getCompoundDrawables()[2].getBounds().width())) {
//                            password_edittext.setInputType(129);
//                        } else {
//                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                            password_edittext.setSelection(password_edittext.getText().length());
//                            return false;
//                        }
//                        break;
//                }
//
//                return true;
//
//            }
//        });

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        alertView = inflater.inflate(R.layout.ip_configure_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Configure IP");
        builder.setCancelable(false);
        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", null);
        builder.setView(alertView);
        alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                final EditText editText = alertView.findViewById(R.id.ip);
                final EditText etDomainName = alertView.findViewById(R.id.etDomainName);
                final EditText etCompanyName = alertView.findViewById(R.id.etCompanyName);
                final EditText etPort = alertView.findViewById(R.id.etPort);
                final EditText etInstanceName = alertView.findViewById(R.id.etInstanceName);
                final EditText etIPName = alertView.findViewById(R.id.ipName);

                final SharedPreferences sharedPreferences = GlobalMethods.getSharedPrefrence(getApplicationContext());

                editText.setText(sharedPreferences.getString(Constants.IP, ""));
                etDomainName.setText(sharedPreferences.getString(Constants.DOMAIN_NAME, ""));
                etCompanyName.setText(sharedPreferences.getString(Constants.COMPANYNAME, ""));
                etPort.setText(sharedPreferences.getString(Constants.PORT, ""));
                etInstanceName.setText(sharedPreferences.getString(Constants.INSTANCENAME, ""));
                etIPName.setText(sharedPreferences.getString(Constants.IPNAME, ""));

                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ip = editText.getText().toString();
                        String domainName = etDomainName.getText().toString();
                        String companyName = etCompanyName.getText().toString();
                        String port = etPort.getText().toString();
                        String instanceName = etInstanceName.getText().toString();
                        String ipName = etIPName.getText().toString();

                        if (!TextUtils.isEmpty(ip)) {
                            if (!TextUtils.isEmpty(ip)) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constants.IP, ip);
                                editor.apply();
                            } else {
                                editText.setError("Please Enter a Valid Ip");
                            }
                        }
                        if (!TextUtils.isEmpty(ipName)) {
                            if (!TextUtils.isEmpty(ipName)) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constants.IPNAME, ipName);
                                editor.apply();
                            } else {
                                editText.setError("Please Enter a Valid IP Name");
                            }
                        }
                        if (!TextUtils.isEmpty(domainName)) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.DOMAIN_NAME, domainName);
                            editor.apply();
                        } else {
                            editText.setError("Please Enter a Valid Domain Name");
                        }
                        if (!TextUtils.isEmpty(companyName)) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.COMPANYNAME, companyName);
                            editor.apply();
                        } else {
                            editText.setError("Please Enter a Valid Domain Name");
                        }
                        if (!TextUtils.isEmpty(port)) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.PORT, port);
                            editor.apply();
                        } else {
                            editText.setError("Please Enter a Valid Domain Name");
                        }
                        if (!TextUtils.isEmpty(instanceName)) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.INSTANCENAME, instanceName);
                            editor.apply();
                        } else {
                            editText.setError("Please Enter a valid Instance Name");
                        }

                        alertDialog.dismiss();
                    }
                });
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


            }
        });
    }

    public boolean checkPermision() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permission Required");
                builder.setMessage("Permission for accessing the camera and writing files to storage are required for proper functioning of the app, PLease give the permission when dialog pops up");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(Login.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                    }
                });
                builder.show();

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    login(null);

                } else {
                    Toast.makeText(this, "You can enable permission later in Settings", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void showIpConfig(View view) {
        alertDialog.show();
    }

    public void showIpConfig() {
        alertDialog.show();
    }

    public void login(View view) {
        if (checkPermision()) {
            InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm1.hideSoftInputFromWindow(password_edittext.getWindowToken(), 0);
            final String username = userName.getText().toString();
            final String password = password_edittext.getText().toString();
            SharedPreferences sharedPreferences = GlobalMethods.getSharedPrefrence(this);
            String ip = sharedPreferences.getString(Constants.IP, "");
            String domainName = sharedPreferences.getString(Constants.DOMAIN_NAME, "");
            if (!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(domainName)) {
                if (!username.equals("")) {
                    if (!password.equals("")) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("password", password);
                        showProgess();
                        WebService.enqueueWork(this, mWorkerResultReceiver, WebService.GET_LOGIN, params);
                    } else {
                        password_edittext.setError("Please Enter Password");
                    }
                } else {
                    userName.setError("Please Enter UserName");

                }
            } else {
                if (!TextUtils.isEmpty(ip)) {
                    Toast.makeText(this, "Please Config the IP Address", Toast.LENGTH_LONG).show();
                } else if (!TextUtils.isEmpty(domainName)) {
                    Toast.makeText(this, "Please Config the Domain Name", Toast.LENGTH_LONG).show();
                }
                showIpConfig();
            }
        }

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        hideProgess();
        switch (resultCode) {
            case SHOW_RESULT:
                if (resultData != null) {
                    String json = resultData.getString("response");
                    json = json.replace("[", "");
                    json = json.replace("]", "");
                    parseResponse(json);
                }
                break;
            default:
                break;
        }
    }

    private void parseResponse(String json) {
        try {
            Gson gson = new Gson();
            LoginResponse loginResponse = gson.fromJson(json, LoginResponse.class);
            if (loginResponse != null && !TextUtils.isEmpty(loginResponse.getKey())) {
                moveToMainPage();
            } else {
                Toast.makeText(this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (JsonSyntaxException e) {
            Toast.makeText(this, "Server error occurred", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            Toast.makeText(this, "Server error occurred", Toast.LENGTH_LONG).show();
        }

    }

    private void moveToMainPage() {
        SharedPreferences sharedPreferences = GlobalMethods.getSharedPrefrence(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.USER, userName.getText().toString());
        editor.putString(Constants.PASSWORD, password_edittext.getText().toString());
        editor.putBoolean(Constants.REMEMBERME, rememberMeSwitch.isChecked());
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void hideProgess() {
        progressDialog.dismiss();
    }

    private void showProgess() {
        progressDialog.show();
    }


/*    static class LoginHandler extends Handler {
        WeakReference<Login> loginWeakReference;

        public LoginHandler(Looper looper, WeakReference<Login> loginWeakReference) {
            super(looper);
            this.loginWeakReference = loginWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Login login = loginWeakReference.get();
            if (login != null) {
                login.progressDialog.dismiss();
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
                            SharedPreferences sharedPreferences = GlobalMethods.getSharedPrefrence(login.getApplicationContext());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.USER, login.userName.getText().toString());
                            editor.putString(Constants.PASSWORD, login.password_edittext.getText().toString());
                            editor.putBoolean(Constants.REMEMBERME, login.rememberMeSwitch.isChecked());
                            editor.commit();
                            Intent intent = new Intent(login.getApplicationContext(), WarehousepickListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            login.startActivity(intent);
                            login.finish();
                            break;
                        case 4:
                            if (statusCode == 401) {
                                Toast.makeText(login, "Username and password doesn't match ", Toast.LENGTH_LONG).show();
                            }
                            if (statusCode == 400) {
                                if (!responseBody.isEmpty()) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseBody);
                                        JSONObject errorObject = jsonObject.getJSONObject("odata.error");
                                        JSONObject messageObject = errorObject.getJSONObject("message");
                                        String message = messageObject.getString("value");
                                        Toast.makeText(login, message, Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(login, "Something Went Wrong....", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(login, "Something Went Wrong....", Toast.LENGTH_LONG).show();
                                }
                            }
                            if (statusCode == 404) {
                                if (!responseBody.isEmpty()) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseBody);
                                        JSONObject errorObject = jsonObject.getJSONObject("odata.error");
                                        JSONObject messageObject = errorObject.getJSONObject("message");
                                        String message = messageObject.getString("value");
                                        Toast.makeText(login, message, Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(login, "Please check Ip Config", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(login, "Please check Ip Config", Toast.LENGTH_LONG).show();
                                }
                            }
                            break;
                        default:
                            String message = bundle.getString(Constants.MESSAGE);
                            if (message != null) {
                                Toast.makeText(login, message, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(login, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            }

                    }
                }
            }
        }
    }*/
}
