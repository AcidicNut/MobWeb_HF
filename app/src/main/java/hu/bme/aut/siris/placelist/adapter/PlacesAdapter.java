package hu.bme.aut.siris.placelist.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.siris.R;
import hu.bme.aut.siris.placelist.data.PlaceItem;
import hu.bme.aut.siris.placelist.placedetails.PlaceDetailsActivity;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder>{
    private static final String TAG = "PlacesAdapter";
    private final List<PlaceItem> items;
    private static PlaceItem item;
    private PlaceItemClickListener listener;

    public PlacesAdapter(PlaceItemClickListener listener) {
        this.listener = listener;
        items = new ArrayList<>();
    }

    public interface PlaceItemClickListener{
        void onItemChanged(PlaceItem item);
        void onItemRemoved(PlaceItem item);
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Log.d(TAG, "onCreateViewHolder");
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate( R.layout.item_place_list, parent, false);
        return new PlacesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder placesViewHolder, int position) {
        Log.d(TAG, "onBindViewHolder");
        PlaceItem item = items.get(position);
        placesViewHolder.nameTextView.setText(item.name);
        placesViewHolder.item = item;
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return items.size();
    }

    class PlacesViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageButton removeButton;
        PlaceItem item;

        PlacesViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.PlaceItemNameTextView);
            removeButton = itemView.findViewById(R.id.PlaceItemRemoveButton);

            nameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlacesAdapter.item = item;
                    Intent placeIntent = new Intent(nameTextView.getContext(), PlaceDetailsActivity.class);
                    nameTextView.getContext().startActivity(placeIntent);
                }
            });

            removeButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemRemoved( item );
                    deleteItem(item);
                }
            });
        }

    }

    public void deleteItem(PlaceItem item){
        Log.d(TAG, "deleteItem");
        int index = items.indexOf(item);
        items.remove( item );
        notifyItemRemoved(index);
        notifyDataSetChanged();
    }

    public void addItem(PlaceItem item) {
        Log.d(TAG, "addItem");
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void update(List<PlaceItem> placeItems) {
        Log.d(TAG, "update");
        items.clear();
        items.addAll(placeItems);
        notifyDataSetChanged();
    }

    public static PlaceItem getItem(){ return item; }
}