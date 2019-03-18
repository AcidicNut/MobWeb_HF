package hu.bme.aut.siris;

import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import hu.bme.aut.siris.addplace.AddPlaceMapsActivity;
import hu.bme.aut.siris.placelist.data.PlaceItem;
import hu.bme.aut.siris.placelist.data.PlaceListDatabase;
import hu.bme.aut.siris.placelist.PlacesListActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "AddPlaceMapsActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String DATABASE_NAME = "Places_db";
    private static PlaceListDatabase placeListDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: MainActivity");
        placeListDatabase = Room.databaseBuilder(getApplicationContext(), PlaceListDatabase.class, DATABASE_NAME).build();
        setContentView(R.layout.activity_main);
        if (servicesVersionCheck()){
            init();
        }
    }
    public boolean servicesVersionCheck(){
        Log.d(TAG, "servicesVersionCheck: google services version check");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS){
            Log.d(TAG, "works");
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "fixable error");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Button btn = findViewById(R.id.btn_places);
            Snackbar s = Snackbar.make(btn, "Error", Snackbar.LENGTH_SHORT);
            s.show();
        }
        return false;
    }
    private void init(){
        Log.d(TAG, "init: MainActivity");
        Button btnPlaces = findViewById(R.id.btn_places);
        Button btnAddPlace = findViewById(R.id.btn_addplace);
        new Thread(new Runnable() {
            @Override
            public void run() {
                PlaceItem item =new PlaceItem();
                //Mar 3 oraja nem jovok ra miert, de valamiert az adatbazis elso elemet nem rakja bele a relativelayoutba....
                try {
                    item.address = "BugFix";
                    item.latitude = 0;
                    item.longitude = 0;
                    item.name = "BugFix";
                    item.phoneNumber = "BugFix";
                    item.placeid = "BugFix";
                    item.rating =5;
                    item.websiteUri = null;
                    item.id = MainActivity.placeListDatabase.placeItemDao().insert(item);
                }catch(NullPointerException e){
                    Log.d(TAG, "Add item bugfix NullPointerException");
                }catch(SQLiteConstraintException e){
                    Log.e(TAG, "addPlace.setOnClickListener: onClick: bugfix SQLiteConstraintException: " + e.getMessage());
                }
            }
        }) .start();
        btnPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PlacesListActivity.class));
            }
        });

        btnAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddPlaceMapsActivity.class));
            }
        });
    }

    public static PlaceListDatabase getPlaceListDatabase(){ return placeListDatabase; }
}