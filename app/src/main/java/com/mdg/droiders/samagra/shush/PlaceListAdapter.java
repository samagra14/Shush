package com.mdg.droiders.samagra.shush;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.PlaceBuffer;

/**
 * Created by samagra on 20/7/17.
 * An adapter showing the list of places selected by the user.
 */

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceListViewholder> {

    private Context mContext;
    private PlaceBuffer mPlaces;

    /**
     * Constructor to initialise the adapter using context and the db cursor.
     * @param mContext {@link Context} Passing in the context from the activity.
     * @param mPlaces {@link PlaceBuffer} A buffer for storing a list of places selected by the user.
     */
    public PlaceListAdapter(Context mContext,PlaceBuffer mPlaces) {
        this.mContext = mContext;
        this.mPlaces = mPlaces;

    }

    /**
     * Called when recycler view needs a new view holder of the given type o represent data.
     * @param parent The view group to which the parent will be added.
     * @param viewType The viewType of the new view.
     * @return A new PlaceViewHolder that adds a new view with the place_card_layout
     */
    @Override
    public PlaceListViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.place_card_layout,parent,false);
        return new PlaceListViewholder(view);
    }

    /**
     * Binds the data from a particular position in the cursor to the corresponding viewholder.
     *
     * @param holder The PlaceListViewHolder instance corresponding to the current position.
     * @param position The current position that needs to be updated with the data.
     */
    @Override
    public void onBindViewHolder(PlaceListViewholder holder, int position) {
        String placeName = mPlaces.get(position).getName().toString();
        String placeAddress = mPlaces.get(position).getAddress().toString();
        holder.nameTextview.setText(placeName);
        holder.placeTextview.setText(placeAddress);

    }

    public void swapPlaces(PlaceBuffer newPlaces){
        mPlaces = newPlaces;
        if (mPlaces!= null)
            //force the recyclerview to reload.
            this.notifyDataSetChanged();

    }

    /**
     * Returns the number of items in the cursor.
     *
     * @return The number of items in the cursor so that the recycler-view becomes aware of the total
     *      number of elements.
     */
    @Override
    public int getItemCount() {
        if (mPlaces==null)return 0;

        return mPlaces.getCount();
    }

    /**
     * View holder class for the recycler View.
     */
    class PlaceListViewholder extends RecyclerView.ViewHolder{

        TextView nameTextview, placeTextview;

        public PlaceListViewholder(View itemView) {
            super(itemView);
            nameTextview = itemView.findViewById(R.id.name_text_view);
            placeTextview = itemView.findViewById(R.id.address_text_view);
        }
    }
}
