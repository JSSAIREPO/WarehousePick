package com.jssai.warehousepick.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jssai.warehousepick.Model.Bin_Contents_List;
import com.jssai.warehousepick.R;
import com.jssai.warehousepick.WarehouseBinCodeListActivity;

import java.util.ArrayList;

public class WarehouseBinListAdapter extends RecyclerView.Adapter<WarehouseBinListAdapter.WarehouseListHolder> implements Filterable {

    private final Activity activity;
    private ArrayList<Bin_Contents_List> bin_contents_lists;
    private ArrayList<Bin_Contents_List> bin_contents_listsFiltered;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    bin_contents_listsFiltered = bin_contents_lists;
                } else {
                    ArrayList<Bin_Contents_List> filteredList = new ArrayList<>();
                    for (Bin_Contents_List item : bin_contents_lists) {
                        if (item.getItem_No().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                    bin_contents_listsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = bin_contents_listsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                bin_contents_listsFiltered = (ArrayList<Bin_Contents_List>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class WarehouseListHolder extends RecyclerView.ViewHolder {
        public TextView tvItemNo, tvBinCode, tvQty;

        public WarehouseListHolder(View view) {
            super(view);
            tvItemNo = view.findViewById(R.id.tvItemNo);
            tvBinCode = view.findViewById(R.id.tvBinCode);
            tvQty = view.findViewById(R.id.tvQty);
        }
    }

    public WarehouseBinListAdapter(Activity context, ArrayList<Bin_Contents_List> bin_contents_lists) {
        this.activity = context;
        this.bin_contents_lists = bin_contents_lists;
        this.bin_contents_listsFiltered = bin_contents_lists;
    }

    @NonNull
    @Override
    public WarehouseListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bin_item, parent, false);
        return new WarehouseListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WarehouseListHolder holder, int position) {
        Bin_Contents_List binContentsList = bin_contents_listsFiltered.get(position);
        holder.tvItemNo.setText(binContentsList.getItem_No());
        holder.tvBinCode.setText(binContentsList.getBin_Code());
        holder.tvQty.setText(binContentsList.getQty_per_Unit_of_Measure());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WarehouseBinCodeListActivity) activity).onItemSelected(binContentsList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bin_contents_listsFiltered.size();
    }
}