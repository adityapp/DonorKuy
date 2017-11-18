package com.example.axce.donorkuy.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.axce.donorkuy.Activity.QrActivity;
import com.example.axce.donorkuy.Model.Barang;
import com.example.axce.donorkuy.Model.BarangBasket;
import com.example.axce.donorkuy.R;

import java.util.ArrayList;

/**
 * Created by AXCE on 16/11/2017.
 */

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.ViewHolder> {
    private ArrayList<BarangBasket> dataSet;
    private BasketAdapter.OnItemClickListener onItemClickListener;
    private Context context;

    public BasketAdapter(Context context) {
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View parent;
        public TextView nama;
        public TextView koin;
        public Button btnBuka;
        public ImageView image;
        public ImageButton clear;

        public ViewHolder(View itemView) {
            super(itemView);
            nama = (TextView) itemView.findViewById(R.id.basket_nama_produk);
            koin = (TextView) itemView.findViewById(R.id.basket_coin);
            btnBuka = (Button) itemView.findViewById(R.id.basket_btn);
            image = (ImageView) itemView.findViewById(R.id.basket_image);
            clear = itemView.findViewById(R.id.hapus_btn);
        }
    }

    public void setOnItemClickListener(BasketAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public BasketAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_basket, parent, false);
        BasketAdapter.ViewHolder viewHolder = new BasketAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BasketAdapter.ViewHolder holder, final int position) {
        holder.nama.setText(dataSet.get(position).getNama());
        String coins = Long.toString(dataSet.get(position).getKoin());
        holder.koin.setText(coins);
        Glide.with(context).load(dataSet.get(position).getUrl()).into(holder.image);

        holder.btnBuka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BarangBasket selectedBarang = dataSet.get(position);
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, selectedBarang);
                }
            }
        });

        holder.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarangBasket selectedBarang = dataSet.get(position);
                Log.d("cariID", selectedBarang.getId()+" "+selectedBarang.getNama());
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClearClick(position, selectedBarang);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("dataset_TAG", "getItemCount: " + dataSet.size());
        return dataSet.size();
    }

    public void setList(ArrayList<BarangBasket> dataSet) {
        this.dataSet = dataSet;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, BarangBasket object);

        void onItemClearClick(int position, BarangBasket object);
    }
}
