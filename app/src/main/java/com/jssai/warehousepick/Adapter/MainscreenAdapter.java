package com.jssai.warehousepick.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jssai.warehousepick.R;

/**
 * Created by Pragadees on 26/11/16.
 */

public class MainscreenAdapter extends RecyclerView.Adapter<MainscreenAdapter.ViewHolder> {
    int[] imageList ;
    String[] titles;
    Handler handler;

    public MainscreenAdapter(int[] imageList, String[] titles,Handler handler) {
        this.imageList = imageList;
        this.handler = handler;
        this.titles = titles;
    }

    @Override
    public MainscreenAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.main_screen_item,null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MainscreenAdapter.ViewHolder holder, final int position) {

        holder.imageView.setImageResource(imageList[position]);
        holder.textView.setText(titles[position]);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = handler.obtainMessage();
                Bundle  bundle = new Bundle();
                bundle.putInt("pos",position);
                message.setData(bundle);
                message.sendToTarget();
            }
        });



    }

    @Override
    public int getItemCount() {
        return imageList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView =(ImageView)itemView.findViewById(R.id.icon);
            textView =(TextView) itemView.findViewById(R.id.title);
            cardView =(CardView)itemView.findViewById(R.id.mainCard);
        }
    }
}
