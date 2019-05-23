package com.jssai.warehousepick;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jssai.warehousepick.Model.BinListResponse;
import com.jssai.warehousepick.Model.LoginResponse;
import com.jssai.warehousepick.services.WebService;
import com.jssai.warehousepick.services.WorkerResultReceiver;

import static com.jssai.warehousepick.services.WebService.SHOW_RESULT;

public class MenuActivity extends AppCompatActivity implements WorkerResultReceiver.Receiver {

    private WorkerResultReceiver mWorkerResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mWorkerResultReceiver = new WorkerResultReceiver(new Handler());
        mWorkerResultReceiver.setReceiver(this);
        //getBinList();
    }

    private void getBinList() {
        WebService.enqueueWork(this, mWorkerResultReceiver, WebService.GET_WAREHOUSE_LIST, null);
    }

    public void moveToWarehousList(View view) {
        //finish();
        Intent intent = new Intent(this, WarehousepickListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_main, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.logout:
                logout();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        WebService.logout(this);
        finish();
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case SHOW_RESULT:
                if (resultData != null) {
                    String json = resultData.getString("response");
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
            BinListResponse binListResponse = gson.fromJson(json, BinListResponse.class);
            Log.d("", binListResponse.toString());
        } catch (JsonSyntaxException e) {
            Toast.makeText(this, "Server error occurred", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            Toast.makeText(this, "Server error occurred", Toast.LENGTH_LONG).show();
        }

    }
}
