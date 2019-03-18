package hu.bme.aut.siris.addplace.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import hu.bme.aut.siris.R;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View window;
    private final String TAG = "InfoWindowAdapter";

    public InfoWindowAdapter(Context c){
        window = LayoutInflater.from(c).inflate(R.layout.info_window, null);
    }

    private void setWindowText(Marker marker, View view){
        Log.d(TAG, " setWindowText ");
        String title = marker.getTitle();
        TextView textViewTitle = view.findViewById(R.id.title);

        if (!title.equals("")){
            textViewTitle.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView textViewSnippet = view.findViewById(R.id.snippet);

        if (!snippet.equals("")){
            textViewSnippet.setText(snippet);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Log.d(TAG, " getInfoWindow ");
        setWindowText(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Log.d(TAG, " getInfoContents ");
        setWindowText(marker, window);
        return window;
    }
}