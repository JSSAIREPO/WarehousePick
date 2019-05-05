package com.jssai.warehousepick.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.jssai.warehousepick.Model.WS_eSignOrder;
import com.jssai.warehousepick.R;

/**
 * Created by Pragadees on 17/11/16.
 */

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.ViewHolder> {

    ArrayList<WS_eSignOrder> mDataset;
    Handler handler;
    String entryNo;


    public SalesAdapter(ArrayList<WS_eSignOrder> mDataset, Handler handler) {
        this.mDataset = mDataset;
        this.handler = handler;
    }

    public void updateData(ArrayList<WS_eSignOrder> mDataset) {
        this.mDataset = mDataset;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.saleslistitem_table_copy, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        WS_eSignOrder WSeSignOrder = mDataset.get(position);
        holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        if(entryNo!=null&&!entryNo.equals("0")){
            if(entryNo.equals(WSeSignOrder.Entry_No)){
                holder.cardView.setCardBackgroundColor(Color.parseColor("#5d027fc7"));
            }
        }else {
            if(position==0){
                holder.cardView.setCardBackgroundColor(Color.parseColor("#5d027fc7"));
            }
        }

        // holder.Entry_No.setText(WSeSignOrder.Entry_No);
        holder.Sell_to_Customer_No.setText(WSeSignOrder.Sell_to_Customer_No.trim());
        holder.No.setText(WSeSignOrder.No.trim());
        holder.Ship_to_Code.setText(WSeSignOrder.Ship_to_Code.trim());
        holder.Ship_to_Name.setText(WSeSignOrder.Ship_to_Name.trim());


        String[] datesplit = WSeSignOrder.Shipment_Date.split("T");
        String time = datesplit[1].substring(0, 5);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm");
        Date date = new Date();
        try {
            date = dateFormat.parse(datesplit[0] + time);
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            holder.Shipment_Date.setText(format.format(date).trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.Sell_to_Customer_Name.setText(WSeSignOrder.Sell_to_Customer_Name.trim());
        holder.Customer_PO_No.setText(WSeSignOrder.Customer_PO_No.trim());
        holder.Ship_To_PO_No.setText(WSeSignOrder.Ship_To_PO_No.trim());
        //holder.Mobile_Comment.setText(WSeSignOrder.Mobile_Comment.trim());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("pos", position);
                message.setData(bundle);
                message.sendToTarget();
            }
        });
        holder.cardView.setTag(position);


    }

    public void setCursor(String  entryNo){
        this.entryNo = entryNo;
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Sell_to_Customer_No, No, Ship_to_Code, Ship_to_Name, Shipment_Date, Sell_to_Customer_Name, Customer_PO_No, Ship_To_PO_No, Mobile_Comment;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            // Entry_No =(TextView)itemView.findViewById(R.id.Entry_No);
            Sell_to_Customer_No = (TextView) itemView.findViewById(R.id.Sell_to_Customer_No);
            No = (TextView) itemView.findViewById(R.id.No);
            Ship_to_Code = (TextView) itemView.findViewById(R.id.Ship_to_Code);
            Ship_to_Name = (TextView) itemView.findViewById(R.id.Ship_to_Name);
            Shipment_Date = (TextView) itemView.findViewById(R.id.Shipment_Date);
            Sell_to_Customer_Name = (TextView) itemView.findViewById(R.id.Sell_to_Customer_Name);
            Customer_PO_No = (TextView) itemView.findViewById(R.id.Customer_PO_No);
            Ship_To_PO_No = (TextView) itemView.findViewById(R.id.Ship_To_PO_No);
            //Mobile_Comment =(TextView)itemView.findViewById(R.id.Mobile_Comment);
            cardView = (CardView) itemView.findViewById(R.id.salesListCard);
        }
    }
}
