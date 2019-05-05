package com.jssai.warehousepick;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.jssai.warehousepick.Adapter.SalesAdapter;
import com.jssai.warehousepick.Model.WS_eSignOrder;
import com.jssai.warehousepick.Util.Constants;
import com.jssai.warehousepick.Util.GlobalMethods;
import com.jssai.warehousepick.Util.NavionUrl;
import com.jssai.warehousepick.Util.NetworkService;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesOrderListActivity extends AppCompatActivity implements Runnable {

    @BindView(R.id.salesRecycler)
    RecyclerView salesRecycler;
//    @BindView(R.id.salesSearch)
//    EditText salesSearch;

    SalesAdapter salesAdapter;
    ArrayList<WS_eSignOrder> WSeSignOrders;
    ArrayList<WS_eSignOrder> WSeSignOrdersOriginal;
    ClickHandler clickHandler;
    ProgressBar pageProgress;
    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.filter_spinner)
    Spinner filterSpinner;
    boolean filterasec= true;
    String clicked;
    SalesAdapter.ViewHolder viewHolder;
    Thread thread;

    @Override
    protected void onStop() {
        super.onStop();
        WSeSignOrders = new ArrayList<>();
        WSeSignOrdersOriginal = new ArrayList<>();
        salesAdapter.updateData(WSeSignOrders);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_list);
        ButterKnife.bind(this);
        if(savedInstanceState!= null){
            clicked = savedInstanceState.getString("Clicked","0");
        }

        pageProgress = (ProgressBar) findViewById(R.id.pageprogress);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WSeSignOrdersOriginal = new ArrayList<>();
        WSeSignOrders = new ArrayList<>();
        clickHandler = new ClickHandler(new WeakReference<SalesOrderListActivity>(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        salesRecycler.setLayoutManager(linearLayoutManager);
        salesAdapter = new SalesAdapter(WSeSignOrders, clickHandler);
        salesRecycler.setAdapter(salesAdapter);

        ArrayList<String> filters = new ArrayList<>();
        Field[] fields = WS_eSignOrder.class.getFields();
        for(Field f :fields){
            if(!(f.getName().contains("string")||f.getName().contains("String")||f.getName().contains("Key")||f.getName().contains("$change")||f.getName().contains("serialVersionUID")||f.getName().contains("ETag")||f.getName().contains("Driver_Signature_Signed")||f.getName().contains("Shipping_Clerk_Signature_Sgd")||f.getName().contains("Receiver_Signature_Signed")||f.getName().contains("Picture1_Signed")||f.getName().contains("Picture2_Signed")||f.getName().contains("Picture3_Signed"))){
            filters.add(f.getName().replace("_"," "));
            }
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,filters);
        filterSpinner.setAdapter(arrayAdapter);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!search.getText().toString().equals("")) {String filter = filterSpinner.getSelectedItem().toString().trim();
                    String filterField = filter.replace(" ", "_");
                    WSeSignOrders = WSeSignOrdersOriginal;
                    ArrayList<WS_eSignOrder> filteredList = new ArrayList<WS_eSignOrder>();
                    try {
                        int index = 0;
                        for (WS_eSignOrder ws_eSignOrder : WSeSignOrdersOriginal) {
                            String value = WS_eSignOrder.class.getField(filterField).get(ws_eSignOrder).toString();
                            if (value.toLowerCase().contains(search.getText().toString().toLowerCase()))
                                filteredList.add(WSeSignOrders.get(index));
                            index++;
                        }
                        WSeSignOrders = filteredList;
                        if(WSeSignOrders!= null) {
                            salesAdapter.updateData(filteredList);

                        }
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }else {
                    WSeSignOrders = WSeSignOrdersOriginal;
                    if(WSeSignOrders!= null) {
                        salesAdapter.updateData(WSeSignOrdersOriginal);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("Clicked",clicked);
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        salesAdapter = new SalesAdapter(WSeSignOrders, clickHandler);
        salesRecycler.setAdapter(salesAdapter);
        salesAdapter.notifyDataSetChanged();
        pageProgress.setVisibility(View.VISIBLE);
        String url = null;

        try {
            url = GlobalMethods.getOdataUrl(getApplicationContext(),"WS_eSignOrder");
            NetworkService.Get(url, new GetDataHandler(new WeakReference<SalesOrderListActivity>(this)), this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(this,"Please Configure IP in login Screen",Toast.LENGTH_LONG).show();
        }

    }


    public static class GetDataHandler extends Handler {
        private WeakReference<SalesOrderListActivity> salesOrderActivityWeakReference;

        public GetDataHandler(WeakReference<SalesOrderListActivity> salesOrderActivityWeakReference) {
            this.salesOrderActivityWeakReference = salesOrderActivityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SalesOrderListActivity salesOrderListActivity = salesOrderActivityWeakReference.get();
            if (salesOrderListActivity != null) {
                salesOrderListActivity.pageProgress.setVisibility(View.INVISIBLE);
                Bundle bundle = msg.getData();
                String response = bundle.getString(Constants.MESSAGE);
                try {
                    salesOrderListActivity.WSeSignOrders = GlobalMethods.getESignOrders(response);
                    salesOrderListActivity.WSeSignOrdersOriginal = salesOrderListActivity.WSeSignOrders;
                    if(salesOrderListActivity.WSeSignOrders!= null) {
                        salesOrderListActivity.salesAdapter = new SalesAdapter(salesOrderListActivity.WSeSignOrders, salesOrderListActivity.clickHandler);
                        salesOrderListActivity.salesRecycler.setAdapter(salesOrderListActivity.salesAdapter);
                        salesOrderListActivity.salesAdapter.notifyDataSetChanged();
                        salesOrderListActivity.salesAdapter.setCursor(salesOrderListActivity.clicked);
                        //salesOrderListActivity.salesAdapter.setCursor(salesOrderListActivity.clicked);
                    }else {
                        Toast.makeText(salesOrderListActivity,"Something Went Wrong",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    @Override
    public void run() {
//        viewHolder = (SalesAdapter.ViewHolder) salesRecycler.findViewHolderForAdapterPosition(clicked);
//        if(viewHolder == null){
//            try {
//                thread.sleep(1000);
//               if(thread.isAlive()){
//                   thread.interrupt();
//               }
//                thread = new Thread(SalesOrderListActivity.this);
//                thread.start();
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }else {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    CardView cardView = (CardView) viewHolder.itemView.findViewWithTag(clicked);
//                    cardView.setCardBackgroundColor(Color.parseColor("#5d027fc7"));
//                }
//            });
//        }
    }

    public void setCursor(){

//        thread = new Thread(this);
//        thread.start();

//       new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                if(viewHolder == null){
//                    try {
//                        Thread.sleep(1000);
//                        viewHolder = (SalesAdapter.ViewHolder) salesRecycler.findViewHolderForAdapterPosition(clicked);
//                        if(viewHolder== null){
//                            if(Thread.currentThread().isAlive()){
//                                Thread.currentThread().start();
//                            }
//                        }else {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    CardView cardView = (CardView) viewHolder.itemView.findViewWithTag(clicked);
//                                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
//                                }
//                            });
//                        }
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                CardView cardView = (CardView) viewHolder.itemView.findViewWithTag(clicked);
//                                cardView.setCardBackgroundColor(Color.BLUE);
//                            }
//                        });
//
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }else {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            CardView cardView = (CardView) viewHolder.itemView.findViewWithTag(clicked);
//                            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
//                        }
//                    });
//                }
//            }
//        }).start();
    }
    public void clearFilter(View view){
        salesAdapter.updateData(WSeSignOrdersOriginal);
        search.setText("");
        filterSpinner.setSelection(0);
    }
    public static class ClickHandler extends Handler {
        private WeakReference<SalesOrderListActivity> salesOrderActivityWeakReference;

        public ClickHandler(WeakReference<SalesOrderListActivity> salesOrderActivityWeakReference) {
            this.salesOrderActivityWeakReference = salesOrderActivityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SalesOrderListActivity salesOrderListActivity = salesOrderActivityWeakReference.get();
            if (salesOrderListActivity != null) {
                Bundle bundle = msg.getData();
                int pos = bundle.getInt("pos");
                Intent intent = new Intent(salesOrderListActivity, SalesOrderActivity.class);
                WS_eSignOrder WSeSignOrder = salesOrderListActivity.WSeSignOrders.get(pos);
                intent.putExtra(Constants.MESSAGE, WSeSignOrder);
                salesOrderListActivity.clicked =WSeSignOrder.Entry_No;
                salesOrderListActivity.startActivity(intent);

            }
        }
    }
    public void scrollDown(View view){
        salesRecycler.scrollToPosition(salesAdapter.getItemCount()-1);
    }
    public void scrollUp(View view){
        salesRecycler.scrollToPosition(0);
    }


    public void sortList(View view){
        TextView textView =(TextView)view;
        String field = textView.getText().toString().trim();
        if(field.equals("Sales Order Number")){
           field = "No";
        }
        field = field.replace(" ","_");
        if(filterasec) {
            filterasec= false;
            WSeSignOrders = WS_eSignOrder.sortDescending(WSeSignOrders, field);
            salesAdapter.updateData(WSeSignOrders);
        }else {
            filterasec= true;
            WSeSignOrders = WS_eSignOrder.sortAscending(WSeSignOrders, field);
            salesAdapter.updateData(WSeSignOrders);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.logout:
                SharedPreferences sharedPreferences = GlobalMethods.getSharedPrefrence(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                break;
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }
}
