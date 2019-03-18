package hu.bme.aut.siris.addplace;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteConstraintException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import hu.bme.aut.siris.MainActivity;
import hu.bme.aut.siris.R;
import hu.bme.aut.siris.addplace.adapter.InfoWindowAdapter;
import hu.bme.aut.siris.addplace.adapter.PlaceAutoCompleteAdapter;
import hu.bme.aut.siris.placelist.data.PlaceItem;
import hu.bme.aut.siris.addplace.models.PlaceInfo;

public class AddPlaceMapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "AddPlaceMapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int PERMISSION_CODE = 401;
    private static final int DEFAULT_ZOOM = 15;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -160), new LatLng(70, 130)  //Random ertekek, arra az esetre ha nem kapunk permissiont.
    );
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private boolean LocationPermission = false;
    private GoogleMap map;
    private AutoCompleteTextView searchText;
    private ImageView Gps;
    private PlaceAutoCompleteAdapter mplaceAutoCompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private ImageView addPlace;
    private Location mLastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: AddPlaceMapsActivity");
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        setContentView(R.layout.activity_add_place_maps);
        searchText = findViewById(R.id.search_edit_text);
        Gps = findViewById(R.id.ic_gps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        addPlace = findViewById(R.id.ic_add_place);
        //getLocationPermission();
    }
    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: van map");
        map = googleMap;

        if (LocationPermission) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }else{
            getLocationPermission();
            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: engedely szerzes");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if ((ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            LocationPermission = true;
            initMap();
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: AddPlaceMapsActivity");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LocationPermission = false;

        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult == PackageManager.PERMISSION_DENIED)
                            return;
                    }
                    LocationPermission = true;
                    getDeviceLocation();
                    initMap();
                    init();
                }
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: keszul a map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "Van map");
                map = googleMap;
            }
        });
    }

    private void getDeviceLocation() {
        Log.d(TAG, " getDeviceLocation: kideritem hol laksz");

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (LocationPermission) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Task<Location> location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, " onComplete: tudom hol laksz");
                        map.setMyLocationEnabled(true);
                        mLastKnownLocation = task.getResult();
                        //Location currentLocation =  task.getResult();
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        Log.d(TAG, " onComplete: nem tudom hol laksz");
                        map.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }
            });
        }
    }

    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        Log.d(TAG, " moveCamera: kamera mozgatasa, lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        map.clear();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        map.setInfoWindowAdapter(new InfoWindowAdapter(AddPlaceMapsActivity.this));
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                try {
                    if (marker.isInfoWindowShown()) {
                        marker.hideInfoWindow();
                    } else
                        marker.showInfoWindow();
                } catch (NullPointerException e) {
                    Log.e(TAG, "setOnInfoWindowClickListener: onInfoWindowClick: NullPointerException");
                }
            }
        });
        if (placeInfo != null) {
            try {
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Name: " + placeInfo.getName() + "\n" +
                        "Phone number: " + placeInfo.getPhoneNumber() + "\n" +
                        "Website: " + placeInfo.getWebsiteUri() + "\n" +
                        "Price rating: " + placeInfo.getRating() + "\n";

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);
                map.addMarker(markerOptions);
            } catch (NullPointerException e) {
                Log.e(TAG, "moveCamera: NullPointerException");
            }
        } else {
            map.addMarker(new MarkerOptions().position(latLng));
        }


        getRidOfKeyboard();
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, " moveCamera: kamera mozgatasa, lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
        if (!title.equals(getString(R.string.current_location))) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            map.addMarker(markerOptions);
        }

        getRidOfKeyboard();
    }

    private void init() {
        Log.d(TAG, "init: MapActivity");

        GeoDataClient mGeoDataClient = Places.getGeoDataClient(this);
        mplaceAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, mGeoDataClient,
                LAT_LNG_BOUNDS, null);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        searchText.setAdapter(mplaceAutoCompleteAdapter);
        searchText.setOnItemClickListener(autocompleteClickListener);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    locate();
                }
                return false;
            }
        });

        Gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "addPlace.setOnClickListener: onClick");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PlaceItem item = new PlaceItem();
                        try {
                            item.address = mPlace.getAddress();
                            item.latitude = mPlace.getLatLng().latitude;
                            item.longitude = mPlace.getLatLng().longitude;
                            item.name = mPlace.getName();
                            item.phoneNumber = mPlace.getPhoneNumber();
                            item.placeid = mPlace.getId();
                            item.rating = mPlace.getRating();
                            item.websiteUri = mPlace.getWebsiteUri();
                            item.id = MainActivity.getPlaceListDatabase().placeItemDao().insert(item);
                            item.note = "";
                            Snackbar.make(searchText, "Sikeresen hozzáadtad! ", Snackbar.LENGTH_LONG).show();
                        } catch (NullPointerException e) {
                            Log.d(TAG, "Add item NullPointerException");
                        } catch (SQLiteConstraintException e) {
                            Log.e(TAG, "addPlace.setOnClickListener: onClick: SQLiteConstraintException: " + e.getMessage());
                            Snackbar.make(searchText, "Már felvetted egyszer! ", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }).start();
            }
        });
    }

    private void locate() {
        Log.d(TAG, "locate: hely keresese");
        String searchStr = searchText.getText().toString();
        Geocoder geocoder = new Geocoder(AddPlaceMapsActivity.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchStr, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "locate: megvan " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    private void getRidOfKeyboard() {
        Log.d(TAG, "getRidOfKeyboard: Bill elrejtese");
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private AdapterView.OnItemClickListener autocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            getRidOfKeyboard();

            final AutocompletePrediction item = mplaceAutoCompleteAdapter.getItem(position);
            assert item != null;
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeBufferPendingResult
                    = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);

            placeBufferPendingResult.setResultCallback(updatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> updatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "Hely lekerdezese sikertelen " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try {
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                mPlace.setId(place.getId());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                mPlace.setRating(place.getRating());
                mPlace.setLatLng(place.getLatLng());

                Log.d(TAG, "Hely reszletei: " + mPlace.toString());
            } catch (NullPointerException e) {
                Log.d(TAG, "Hely reszeletei, NullPointerException " + e.getMessage());
            }
            moveCamera(new LatLng(Objects.requireNonNull(place.getViewport()).getCenter().latitude, place.getViewport().getCenter().longitude),
                    DEFAULT_ZOOM, mPlace);
            places.release();
        }
    };
}