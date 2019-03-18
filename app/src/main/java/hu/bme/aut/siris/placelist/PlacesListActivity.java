package hu.bme.aut.siris.placelist;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.List;

import hu.bme.aut.siris.MainActivity;
import hu.bme.aut.siris.R;
import hu.bme.aut.siris.placelist.adapter.PlacesAdapter;
import hu.bme.aut.siris.placelist.data.PlaceItem;

public class PlacesListActivity extends AppCompatActivity implements
        PlacesAdapter.PlaceItemClickListener {
    private static final String TAG = "PlacesListActivity";
    private PlacesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initRecyclerView();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView");
        RecyclerView recyclerView = findViewById(R.id.MainRecyclerView);
        adapter = new PlacesAdapter(this);
        loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("StaticFieldLeak")
    private void loadItemsInBackground() {
        Log.d(TAG, "loadItemsInBackground");
        new AsyncTask<Void, Void, List<PlaceItem>>() {

            @Override
            protected List<PlaceItem> doInBackground(Void... voids) {
                Log.d(TAG, "doInBackground");
                return MainActivity.getPlaceListDatabase().placeItemDao().getAll();
            }

            @Override
            protected void onPostExecute(List<PlaceItem> placeItems) {
                Log.d(TAG, "onPostExecute");
                adapter.update(placeItems);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onItemChanged(final PlaceItem item) {
        Log.d(TAG, "onItemChanged");
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                Log.d(TAG, "doInBackground");
                MainActivity.getPlaceListDatabase().placeItemDao().update(item);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccessful) {
                Log.d(TAG, "onPostExecute PlaceItem update was successful");
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onItemRemoved(final PlaceItem item) {
        Log.d(TAG, "onItemRemovedl");
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                Log.d(TAG, "doInBackground");
                MainActivity.getPlaceListDatabase().placeItemDao().deleteItem(item);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccessful) {
                adapter.deleteItem(item);
                Log.d(TAG, "doInBackground PlaceItem delete was successful");
            }
        }.execute();
    }
}