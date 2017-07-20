package com.mdg.droiders.samagra.shush;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by samagra on 20/7/17.
 */

public class PlaceListAdapter extends RecyclerView.Adapter {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class PlaceListViewholder extends RecyclerView.ViewHolder{

        TextView nameTextview, placeTextview;

        public PlaceListViewholder(View itemView) {
            super(itemView);
            nameTextview = itemView.findViewById(R.id.name_text_view);
            placeTextview = itemView.findViewById(R.id.address_text_view);
        }
    }
}
