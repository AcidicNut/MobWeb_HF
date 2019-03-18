package hu.bme.aut.siris.placelist.placedetails.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hu.bme.aut.siris.R;
import hu.bme.aut.siris.placelist.adapter.PlacesAdapter;

public class MainPlaceFragment extends Fragment {
    private static final String TAG = "MainPlaceFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.place_main, container, false);

        TextView tvName = rootView.findViewById(R.id.tvName);
        TextView tvAddress = rootView.findViewById(R.id.tvAddress);
        TextView tvPhoneNumber = rootView.findViewById(R.id.tvPhoneNumber);
        TextView tvWebsite = rootView.findViewById(R.id.tvWebsite);
        TextView tvRating = rootView.findViewById(R.id.tvRating);

        tvName.setText(PlacesAdapter.getItem().name);
        tvAddress.setText(PlacesAdapter.getItem().address);
        tvPhoneNumber.setText(PlacesAdapter.getItem().phoneNumber);
        tvWebsite.setText(PlacesAdapter.getItem().websiteUri == null? "" : Uri.decode(PlacesAdapter.getItem().websiteUri.toString()));
        String rating = PlacesAdapter.getItem().rating < 0? "" : "" + PlacesAdapter.getItem().rating;
        tvRating.setText(rating);

        return rootView;
    }

}