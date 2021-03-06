package com.jssai.warehousepick;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.jssai.warehousepick.Adapter.WarehouseBinListAdapter;
import com.jssai.warehousepick.Model.BinListResponse;
import com.jssai.warehousepick.Model.Binlist;
import com.jssai.warehousepick.services.WebService;
import com.jssai.warehousepick.services.WorkerResultReceiver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import static com.jssai.warehousepick.services.WebService.SHOW_RESULT;

public class WarehouseBinCodeListActivity extends AppCompatActivity implements WorkerResultReceiver.Receiver {

    RecyclerView rvBinCodes;
    WarehouseBinListAdapter binListAdapter;
    BinListResponse binListResponse;
    ArrayList<Binlist> binlist = new ArrayList<>();
    private WorkerResultReceiver mWorkerResultReceiver;
    ProgressDialog progressBar;
    String itemNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_bin_code_list);
        itemNo = getIntent().getStringExtra("itemNo");
        initViews();
        mWorkerResultReceiver = new WorkerResultReceiver(new Handler());
        mWorkerResultReceiver.setReceiver(this);
        getData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        rvBinCodes = findViewById(R.id.rvBinCodes);
        rvBinCodes.setHasFixedSize(true);
        rvBinCodes.setLayoutManager(new LinearLayoutManager(this));
        rvBinCodes.setItemAnimator(new DefaultItemAnimator());
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Loading Bin Lists...");
        binListAdapter = new WarehouseBinListAdapter(this, binlist);
        rvBinCodes.setAdapter(binListAdapter);
    }

    private void getData() {
        progressBar.show();
        WebService.enqueueWork(this, mWorkerResultReceiver, WebService.GET_BIN_LIST, null);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        progressBar.dismiss();
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
        Gson gson = new Gson();
        BinListResponse binListResponse = gson.fromJson(json, BinListResponse.class);
        binlist.clear();
        binlist.addAll(new ArrayList<Binlist>(Arrays.asList(binListResponse.getBinlist())));
        binListAdapter.notifyDataSetChanged();
    }

    public void onItemSelected(Binlist binlist) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("binContentsList", (Serializable) binlist);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
