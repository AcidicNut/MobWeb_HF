package hu.bme.aut.siris.placelist.placedetails.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

import hu.bme.aut.siris.MainActivity;
import hu.bme.aut.siris.R;
import hu.bme.aut.siris.placelist.adapter.PlacesAdapter;

public class NotePlaceFragment extends Fragment {
    private static final String TAG = "NotePlaceFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate( R.layout.place_note, container, false);

        final EditText tvNote = rootView.findViewById(R.id.tvNote);
        tvNote.setText(PlacesAdapter.getItem().note);

        Button btnSave = rootView.findViewById(R.id.btnSave);
        Button btnRoute = rootView.findViewById(R.id.btnRoute);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "btnSave.setOnClickListener");
                PlacesAdapter.getItem().note = tvNote.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.getPlaceListDatabase().placeItemDao().update(PlacesAdapter.getItem());
                        View view = Objects.requireNonNull(getActivity()).getCurrentFocus();
                        if (view != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }).start();
                Toast.makeText(tvNote.getContext(), "Sikeresen elmentetted! ", Toast.LENGTH_SHORT).show();
            }
        });

        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "btnRoute.setOnClickListener");
                Uri gmmIntentUri = Uri.parse("geo:" + PlacesAdapter.getItem().latitude + "," + PlacesAdapter.getItem().longitude + "?q=" + PlacesAdapter.getItem().address);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        return rootView;
    }
}