package com.jssai.warehousepick.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jssai.warehousepick.Model.WarehousePickItem;
import com.jssai.warehousepick.R;
import com.jssai.warehousepick.SimpleScannerActivity;
import com.jssai.warehousepick.WarehouseDetailPage;

public class WarehouseListAdapter extends RecyclerView.Adapter<WarehouseListAdapter.WarehouseListHolder> implements Filterable {

    private final Context context;
    private List<WarehousePickItem> warehousePickItems;
    private List<WarehousePickItem> warehousePickItemsFiltered;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    warehousePickItemsFiltered = warehousePickItems;
                } else {
                    List<WarehousePickItem> filteredList = new ArrayList<>();
                    for (WarehousePickItem item : warehousePickItems) {
                        if (item.getNo().toLowerCase().contains(charString.toLowerCase()) || item.getSales_Order_No().contains(charSequence)) {
                            filteredList.add(item);
                        }
                    }
                    warehousePickItemsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = warehousePickItemsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                warehousePickItemsFiltered = (ArrayList<WarehousePickItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class WarehouseListHolder extends RecyclerView.ViewHolder {
        public TextView tvPickOrderNo, tvSalesOrderNo;

        public WarehouseListHolder(View view) {
            super(view);
            tvPickOrderNo = view.findViewById(R.id.tvPickOrderNo);
            tvSalesOrderNo = view.findViewById(R.id.tvSalesOrderNo);
        }
    }

    public WarehouseListAdapter(Context context, List<WarehousePickItem> warehousePickItems) {
        this.context = context;
        this.warehousePickItems = warehousePickItems;
        this.warehousePickItemsFiltered = warehousePickItems;
    }

    @NonNull
    @Override
    public WarehouseListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.warehouse_item, parent, false);
        return new WarehouseListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WarehouseListHolder holder, int position) {
        WarehousePickItem warehousePickItem = warehousePickItemsFiltered.get(position);
        holder.tvPickOrderNo.setText(warehousePickItem.getNo());
        holder.tvSalesOrderNo.setText(warehousePickItem.getSales_Order_No());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WarehouseDetailPage.class);
                intent.putExtra("item", (Serializable) warehousePickItemsFiltered.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return warehousePickItemsFiltered.size();
    }
}