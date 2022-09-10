package com.example.beaconfinalapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.beaconfinalapp.PlacesHelper.PlaceHelper;
import com.example.beaconfinalapp.R;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder> {

    public List<PlaceHelper> itemsList;


    public PlacesAdapter(List<PlaceHelper> mItemList){
        this.itemsList = mItemList;
    }

    @Override
    public PlacesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacesAdapter.MyViewHolder holder, final int position) {
        final PlaceHelper item = itemsList.get(position);
        holder.name.setText(item.getPlaceName3());
        holder.price.setText(item.getPlaceAddress());

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name,price;
        public LinearLayout itemLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            price = itemView.findViewById(R.id.tvAddress);
            itemLayout =  itemView.findViewById(R.id.itemLayout);
        }
    }
}
