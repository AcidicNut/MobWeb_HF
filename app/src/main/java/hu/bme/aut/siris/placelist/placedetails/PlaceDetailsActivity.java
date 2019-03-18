package hu.bme.aut.siris.placelist.placedetails;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import hu.bme.aut.siris.R;
import hu.bme.aut.siris.placelist.placedetails.adapter.PlacePagerAdapter;

public class PlaceDetailsActivity extends AppCompatActivity {
    private static final String TAG = "PlaceDetailsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        ViewPager vpProfile = findViewById(R.id.vpPlace);
        vpProfile.setAdapter(new PlacePagerAdapter(getSupportFragmentManager(), this));
    }
}