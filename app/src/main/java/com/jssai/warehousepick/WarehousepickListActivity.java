package com.jssai.warehousepick;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.List;

import com.jssai.warehousepick.Adapter.WarehouseListAdapter;
import com.jssai.warehousepick.Model.WarehousePickItem;
import com.jssai.warehousepick.Util.Constants;
import com.jssai.warehousepick.Util.GlobalMethods;
import com.jssai.warehousepick.services.WebService;
import com.jssai.warehousepick.services.WorkerResultReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jssai.warehousepick.services.WebService.SHOW_RESULT;

public class WarehousepickListActivity extends AppCompatActivity implements WorkerResultReceiver.Receiver {

    private static final String TAG = "WarehousepickListActivity";
    @BindView(R.id.rvWareHouseList)
    RecyclerView rvWareHouseList;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.etSearch)
    AppCompatEditText etSearch;
    WarehouseListAdapter warehouseListAdapter;
    private ArrayList<WarehousePickItem> warehousePickItems = new ArrayList<>();
    private WorkerResultReceiver mWorkerResultReceiver;
    HashMap<String, WarehousePickItem> pickItemHashMap = new HashMap<>();
    private static final int PERMISSION_REQUESTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehousepicklist);
        initViews();
        warehouseListAdapter = new WarehouseListAdapter(this, warehousePickItems);
        rvWareHouseList.setAdapter(warehouseListAdapter);
        mWorkerResultReceiver = new WorkerResultReceiver(new Handler());
        mWorkerResultReceiver.setReceiver(this);
        if (allPermissionsGranted()) {
            onPermissionGranted();
        } else {
            getRuntimePermissions();
        }
        //loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.pick_list_menu, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.refresh:
                loadData();
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

    private void initViews() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Warehouse List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                warehouseListAdapter.getFilter().filter(str);
            }
        });
        rvWareHouseList.setHasFixedSize(true);
        rvWareHouseList.setLayoutManager(new LinearLayoutManager(this));
        rvWareHouseList.setItemAnimator(new DefaultItemAnimator());
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
    }

    private void loadData() {
        showProgress();
        showDataFromBackground(WarehousepickListActivity.this, mWorkerResultReceiver);
    }

    private void showDataFromBackground(WarehousepickListActivity mainActivity, WorkerResultReceiver mWorkerResultReceiver) {
        WebService.enqueueWork(mainActivity, mWorkerResultReceiver, WebService.GET_WAREHOUSE_LIST, null);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        hideProgess();
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
        warehousePickItems.clear();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String Key = jsonObject.getString("Key");
                String No = jsonObject.getString("No");
                String sales_order_no = jsonObject.getString("Order_No");
                String Customer_No = jsonObject.getString("Customer_No");
                String Customer_Name = jsonObject.getString("Customer_Name");
                WarehousePickItem warehousePickItem = new WarehousePickItem(Key, No, sales_order_no, Customer_No, Customer_Name);
                warehousePickItems.add(warehousePickItem);
                pickItemHashMap.put(No, warehousePickItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppController.pickHashMap = pickItemHashMap;
        if (warehousePickItems == null || warehousePickItems.size() <= 0) {
            Toast.makeText(this, "Server error occured", Toast.LENGTH_LONG).show();
        }
        warehouseListAdapter.notifyDataSetChanged();
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

    public void onPermissionGranted() {
        loadData();
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Log.i("", "Permission granted!");
        if (allPermissionsGranted()) {
            onPermissionGranted();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i("", "Permission granted: " + permission);
            return true;
        }
        Log.i("", "Permission NOT granted: " + permission);
        return false;
    }

    public void openBarcode(View view) {
        Intent intent = new Intent(this, SimpleScannerActivity.class);
        intent.putExtra("purpose", SimpleScannerActivity.SCAN_PICK_LIST);
        startActivity(intent);
    }
}