package com.example.axce.donorkuy.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.axce.donorkuy.Barang;
import com.example.axce.donorkuy.R;

import java.util.ArrayList;

/**
 * Created by AXCE on 12/10/2017.
 */

public class BerkahAdapter extends RecyclerView.Adapter<BerkahAdapter.ViewHolder> {
    private ArrayList<Barang> dataSet;
    private OnItemClickListener onItemClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View parent;
        public TextView nama;
        public TextView koin;
        public Button btnTukar;

        public ViewHolder(View itemView) {
            super(itemView);
            nama = (TextView) itemView.findViewById(R.id.berkah_nama_produk);
            koin = (TextView) itemView.findViewById(R.id.berkah_coin);
            btnTukar = (Button) itemView.findViewById(R.id.berkah_btn);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public BerkahAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_produk, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.nama.setText(dataSet.get(position).getNama());
        String coins = Integer.toString(dataSet.get(position).getKoin());
        holder.koin.setText(coins);

        holder.btnTukar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Barang selectedBarang = dataSet.get(position);
                if(onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, selectedBarang);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("dataset_TAG", "getItemCount: " + dataSet.size());
        return dataSet.size();
    }

    public void setList(ArrayList<Barang> dataSet){
        this.dataSet = dataSet;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, Barang object);
    }
}
