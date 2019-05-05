package com.jssai.warehousepick;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

import com.jssai.warehousepick.Model.ErrorResponse;
import com.jssai.warehousepick.Model.WarehousePickItem;
import com.jssai.warehousepick.services.WebService;
import com.jssai.warehousepick.services.WorkerResultReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jssai.warehousepick.services.WebService.SHOW_RESULT;

public class WarehouseItemEditPage extends AppCompatActivity implements WorkerResultReceiver.Receiver {

    @BindView(R.id.etItemNo)
    AppCompatEditText etItemNo;

    @BindView(R.id.etBinCode)
    AppCompatEditText etBinCode;

    @BindView(R.id.etQuantity)
    AppCompatEditText etQuantity;

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;

    WarehousePickItem warehousePickItem;
    int position;
    private WorkerResultReceiver mWorkerResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_item_edit_page);
        ButterKnife.bind(this);
        getData();
        initViews();
        setData();
        mWorkerResultReceiver = new WorkerResultReceiver(new Handler());
        mWorkerResultReceiver.setReceiver(this);
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(warehousePickItem.getItem_No());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    public void showProgress() {
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        });
    }

    public void hideProgess() {
        swipeLayout.setRefreshing(false);
    }

    private void setData() {
        etItemNo.setText(warehousePickItem.getItem_No());
        etBinCode.setText(warehousePickItem.getBin_Code().toUpperCase());
        etQuantity.setText("" + warehousePickItem.getQty_to_Handle());
    }

    private void getData() {
        warehousePickItem = (WarehousePickItem) getIntent().getSerializableExtra("item");
        position = getIntent().getIntExtra("position", -1);
    }

    public void submit(View view) {
        showProgress();
        updatePickItem();
    }

    private void updatePickItem() {
        warehousePickItem.setQty_to_Handle(Long.parseLong(etQuantity.getText().toString()));
        warehousePickItem.setBin_Code(etBinCode.getText().toString());
        HashMap<String, String> params = new HashMap<>();
        params.put("PickNumber", warehousePickItem.getNo());
        params.put("PickLineNumber", "" + warehousePickItem.getLine_No());
        params.put("BinCode", warehousePickItem.getBin_Code());
        params.put("QuantityToHandle", "" + warehousePickItem.getQty_to_Handle());
        WebService.params = params;
        WebService.enqueueWork(WarehouseItemEditPage.this, mWorkerResultReceiver, WebService.POST_RECORD_PICK_LINES, params);
    }

    public void cancel(View view) {
        finish();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        hideProgess();
        switch (resultCode) {
            case SHOW_RESULT:
                if (resultData != null) {
                    String json = resultData.getString("response");
                    int code = resultData.getInt("code");
                    if (code == 200) {
                        try {
                            JSONObject errorObject = new JSONObject(json);
                            String Type = errorObject.getString("Type");
                            String Message = errorObject.getString("Message");
                            Toast.makeText(this, Message, Toast.LENGTH_LONG).show();
                            if (!TextUtils.isEmpty(Message) && Message.equalsIgnoreCase("Record Updated")) {
                                Intent intent = new Intent();
                                intent.putExtra("item", (Serializable) warehousePickItem);
                                intent.putExtra("position", position);
                                setResult(WarehouseDetailPage.EDIT_ITEM, intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Server error occurred!", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(this, "Server error occurred!", Toast.LENGTH_LONG).show();
                    }


                }
                break;
            default:
                break;
        }

    }
}