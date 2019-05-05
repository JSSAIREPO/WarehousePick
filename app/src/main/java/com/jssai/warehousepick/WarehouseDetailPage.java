package com.jssai.warehousepick;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.jssai.warehousepick.Adapter.WarehouseItemsAdapter;
import com.jssai.warehousepick.Model.ErrorResponse;
import com.jssai.warehousepick.Model.WarehousePickItem;
import com.jssai.warehousepick.services.WebService;
import com.jssai.warehousepick.services.WorkerResultReceiver;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jssai.warehousepick.services.WebService.GET_WAREHOUSE_DETAILS;
import static com.jssai.warehousepick.services.WebService.POST_REGISTER_WAREHOUSE_PICK;
import static com.jssai.warehousepick.services.WebService.SHOW_RESULT;

public class WarehouseDetailPage extends AppCompatActivity implements WorkerResultReceiver.Receiver {

    public static int EDIT_ITEM = 100;
    @BindView(R.id.tvPickOrderNo)
    AppCompatTextView tvPickOrderNo;

    @BindView(R.id.tvSalesOrderNo)
    AppCompatTextView tvSalesOrderNo;

    @BindView(R.id.tvCustomerNo)
    AppCompatTextView tvCustomerNo;

    @BindView(R.id.tvCustomerName)
    AppCompatTextView tvCustomerName;

    @BindView(R.id.rvWarehouseItems)
    RecyclerView rvWarehouseItems;

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;

    WarehousePickItem warehousePickItem;
    ArrayList<WarehousePickItem> warehouseItems = new ArrayList<>();
    HashMap<String, WarehousePickItem> warehouseItemsHashMap = new HashMap<>();
    private WarehouseItemsAdapter warehouseItemsAdapter;
    private WorkerResultReceiver mWorkerResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_detail_page);
        getData();
        initViews();
        warehouseItemsAdapter = new WarehouseItemsAdapter(this, warehouseItems);
        rvWarehouseItems.setAdapter(warehouseItemsAdapter);
        mWorkerResultReceiver = new WorkerResultReceiver(new Handler());
        mWorkerResultReceiver.setReceiver(this);
        setData();
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.warehouse_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.registerPick:
                showProgress();
                registerWarehousePickItem();
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private void registerWarehousePickItem() {
        HashMap<String, String> params = new HashMap<>();
        params.put("PickNo", warehousePickItem.getNo());
        WebService.enqueueWork(this, mWorkerResultReceiver, WebService.POST_REGISTER_WAREHOUSE_PICK, params);
    }


    private void loadData() {
        showProgress();
        showDataFromBackground(WarehouseDetailPage.this, mWorkerResultReceiver);
    }

    private void setData() {
        tvPickOrderNo.setText(warehousePickItem.getNo());
        tvSalesOrderNo.setText(warehousePickItem.getSales_Order_No());
        tvCustomerNo.setText(warehousePickItem.getCustomer_No());
        tvCustomerName.setText(warehousePickItem.getCustomer_Name());
    }

    private void getData() {
        warehousePickItem = (WarehousePickItem) getIntent().getSerializableExtra("item");
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(warehousePickItem.getNo());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        rvWarehouseItems.setHasFixedSize(true);
        rvWarehouseItems.setLayoutManager(new LinearLayoutManager(this));
        rvWarehouseItems.setItemAnimator(new DefaultItemAnimator());
    }

    private void showDataFromBackground(WarehouseDetailPage mainActivity, WorkerResultReceiver mWorkerResultReceiver) {
        WebService.enqueueWork(mainActivity, mWorkerResultReceiver, WebService.GET_WAREHOUSE_DETAILS, null);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        hideProgess();
        switch (resultCode) {
            case SHOW_RESULT:
                int requestCode = resultData.getInt("requestCode");
                switch (requestCode) {
                    case GET_WAREHOUSE_DETAILS:
                        String warehouseDetails = resultData.getString("response");
                        parseWarehouseDetails(warehouseDetails);
                        break;
                    case POST_REGISTER_WAREHOUSE_PICK:
                        String registerPickResponse = resultData.getString("response");
                        parseRegisterPickResponse(registerPickResponse);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void parseRegisterPickResponse(String registerPickResponse) {
        if (registerPickResponse != null && registerPickResponse.length() > 0) {
            ErrorResponse error = null;
            try {
                JSONObject errorObject = new JSONObject(registerPickResponse);
                String Type = errorObject.getString("Type");
                String Message = errorObject.getString("Message");
                error = new ErrorResponse(Type, Message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (error != null) {
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Server error occurred!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Server error occurred!", Toast.LENGTH_LONG).show();
        }
    }

    private void parseWarehouseDetails(String json) {
        warehouseItems.clear();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String No = jsonObject.getString("No");
                String Key = jsonObject.getString("Key");
                int Activity_Type = jsonObject.getInt("Activity_Type");
                boolean Activity_TypeSpecified = jsonObject.getBoolean("Activity_TypeSpecified");
                String Sales_Order_No = jsonObject.getString("Line_No");
                String Customer_No = jsonObject.getString("Customer_No");
                String Customer_Name = jsonObject.getString("Customer_Name");
                int Line_No = jsonObject.getInt("Line_No");
                boolean Line_NoSpecified = jsonObject.getBoolean("Line_NoSpecified");
                String Item_No = jsonObject.getString("Item_No");
                String Unit_of_Measure_Code = jsonObject.getString("Unit_of_Measure_Code");
                String Description = jsonObject.getString("Description");
                String Description_2 = jsonObject.getString("Description_2");
                long Quantity = jsonObject.getLong("Quantity");
                boolean QuantitySpecified = jsonObject.getBoolean("QuantitySpecified");
                long Qty_Base = jsonObject.getLong("Qty_Base");
                boolean Qty_BaseSpecified = jsonObject.getBoolean("Qty_BaseSpecified");
                String Bin_Code = jsonObject.getString("Bin_Code");
                String Zone_Code = jsonObject.getString("Zone_Code");
                int Action_Type = jsonObject.getInt("Action_Type");
                boolean Action_TypeSpecified = jsonObject.getBoolean("Action_TypeSpecified");
                long Qty_to_Handle = jsonObject.getLong("Qty_to_Handle");
                WarehousePickItem warehousePickItem = new WarehousePickItem(No, Action_Type, Activity_TypeSpecified, Description, Activity_Type, Customer_Name, Customer_No, Line_NoSpecified, Quantity, Sales_Order_No, Description_2, Qty_BaseSpecified, Bin_Code, Action_TypeSpecified, Line_No, Zone_Code, Item_No, QuantitySpecified, Unit_of_Measure_Code, Qty_Base, Key, Qty_to_Handle);
                if (No.equalsIgnoreCase(this.warehousePickItem.getNo())) {
                    warehouseItems.add(warehousePickItem);
                    warehouseItemsHashMap.put(warehousePickItem.getItem_No(), warehousePickItem);
                }
                AppController.pickItemsHashMap = warehouseItemsHashMap;
            }
        } catch (JSONException e) {
            Log.e("error", e.getMessage());
        }
        warehouseItemsAdapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (data != null && requestCode == EDIT_ITEM) {
            WarehousePickItem editedItem = (WarehousePickItem) data.getSerializableExtra("item");
            int position = data.getIntExtra("position", -1);
            if (position != -1) {
                warehouseItems.set(position, editedItem);
                warehouseItemsAdapter.notifyDataSetChanged();
            }
        }
    }

    public void openBarcode(View view) {
        Intent intent = new Intent(this, SimpleScannerActivity.class);
        intent.putExtra("purpose", SimpleScannerActivity.SCAN_PICK_ITEM);
        startActivity(intent);
    }
}
