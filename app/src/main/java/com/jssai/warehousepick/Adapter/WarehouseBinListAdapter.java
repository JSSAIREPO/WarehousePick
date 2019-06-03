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

import com.jssai.warehousepick.Model.Binlist;
import com.jssai.warehousepick.R;
import com.jssai.warehousepick.WarehouseBinCodeListActivity;

import java.util.ArrayList;

public class WarehouseBinListAdapter extends RecyclerView.Adapter<WarehouseBinListAdapter.WarehouseListHolder> implements Filterable {

    private final Activity activity;
    private ArrayList<Binlist> binlists;
    private ArrayList<Binlist> binlistsFiltered;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    binlistsFiltered = binlists;
                } else {
                    ArrayList<Binlist> filteredList = new ArrayList<>();
                    for (Binlist item : binlists) {
                        if (item.getItem_No().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                    binlistsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = binlistsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                binlistsFiltered = (ArrayList<Binlist>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class WarehouseListHolder extends RecyclerView.ViewHolder {
        public TextView tvItemNo, tvBinCode, tvLotNumber, tvQty;

        public WarehouseListHolder(View view) {
            super(view);
            tvItemNo = view.findViewById(R.id.tvItemNo);
            tvBinCode = view.findViewById(R.id.tvBinCode);
            tvQty = view.findViewById(R.id.tvQty);
            tvLotNumber = view.findViewById(R.id.tvLotNumber);
        }
    }

    public WarehouseBinListAdapter(Activity context, ArrayList<Binlist> Binlists) {
        this.activity = context;
        this.binlists = Binlists;
        this.binlistsFiltered = Binlists;
    }

    @NonNull
    @Override
    public WarehouseListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bin_item, parent, false);
        return new WarehouseListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WarehouseListHolder holder, int position) {
        Binlist binlist = binlistsFiltered.get(position);
        holder.tvItemNo.setText(binlist.getItem_No());
        holder.tvBinCode.setText(binlist.getBin_Code());
        holder.tvQty.setText("" + binlist.getAvailableQuantity());
        holder.tvLotNumber.setText("" + binlist.getLotNumber());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WarehouseBinCodeListActivity) activity).onItemSelected(binlist);
            }
        });
    }

    @Override
    public int getItemCount() {
        return binlistsFiltered.size();
    }
}