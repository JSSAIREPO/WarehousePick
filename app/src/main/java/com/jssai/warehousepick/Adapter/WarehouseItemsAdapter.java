package com.jssai.warehousepick.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import com.jssai.warehousepick.Model.WarehousePickItem;
import com.jssai.warehousepick.R;
import com.jssai.warehousepick.WarehouseDetailPage;
import com.jssai.warehousepick.WarehouseItemEditPage;

public class WarehouseItemsAdapter extends RecyclerView.Adapter<WarehouseItemsAdapter.WarehouseListHolder> {

    private final Activity context;
    private List<WarehousePickItem> warehousePickItems;

    public class WarehouseListHolder extends RecyclerView.ViewHolder {

        public TextView tvItemNo, tvDescription, tvZone, tvBinCode, tvQuantity;

        public WarehouseListHolder(View view) {
            super(view);
            tvItemNo = view.findViewById(R.id.tvItemNo);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvZone = view.findViewById(R.id.tvZone);
            tvBinCode = view.findViewById(R.id.tvBinCode);
            tvQuantity = view.findViewById(R.id.tvQuantity);
        }
    }

    public WarehouseItemsAdapter(Activity context, List<WarehousePickItem> warehousePickItems) {
        this.context = context;
        this.warehousePickItems = warehousePickItems;
    }

    @NonNull
    @Override
    public WarehouseListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.warehouse_detail_item, parent, false);
        return new WarehouseListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WarehouseListHolder holder, int position) {
        WarehousePickItem warehousePickItem = warehousePickItems.get(position);
        holder.tvItemNo.setText(warehousePickItem.getItem_No());
        holder.tvDescription.setText(warehousePickItem.getDescription());
        holder.tvZone.setText(warehousePickItem.getZone_Code());
        holder.tvBinCode.setText(warehousePickItem.getBin_Code());
        holder.tvQuantity.setText("" + warehousePickItem.getQty_to_Handle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WarehouseItemEditPage.class);
                intent.putExtra("item", (Serializable) warehousePickItems.get(position));
                intent.putExtra("position", position);
                ((Activity) context).startActivityForResult(intent, WarehouseDetailPage.EDIT_ITEM);
            }
        });
    }

    @Override
    public int getItemCount() {
        return warehousePickItems.size();
    }
}