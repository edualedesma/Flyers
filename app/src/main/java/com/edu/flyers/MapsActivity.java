package com.edu.flyers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.edu.flyers.model.Ruta;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Double mLatitude, mLongitude;

    private List<Address> addressesOrigin = null;
    private List<Address> addressesDestination = null;

    private String address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras= getIntent().getExtras();
        if (getIntent().hasExtra("address") ) {
            address = extras.getString("address");

            Toast.makeText(getApplicationContext(),
                    "Dirección: " + address, Toast.LENGTH_LONG)
                    .show();
        }

        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(LocationServices.API).build();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Obtenemos la última localización conocida
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
            Toast toast = Toast.makeText(getApplicationContext(), "Latitud: " + mLatitude + " Longitud: " + mLongitude + "\n", Toast.LENGTH_SHORT);
            toast.show();

            // Position en the map
            LatLng myLocation = new LatLng(mLatitude, mLongitude);
            //mMap.addMarker(new MarkerOptions().position(myLocation).title("Mi ubicación"));
            mMap.setMyLocationEnabled(true);
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 20));
            //Location.distanceBetween(mLatitude, mLongitude, -10, 50);

            /*Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addressesOrigin = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                addressesDestination = geocoder.getFromLocationName(address, 1);
                Log.d("MapsActivity", "Mi destino es: " + addressesDestination.get(0).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            PeticionesRestAPI peticiones = RutaRestAdapter.getInstanced().create(PeticionesRestAPI.class);

            String origen = mLatitude + "," + mLongitude;

            ;

            peticiones.rutaAsync(origen, address, "now", "car", new Callback<Ruta>() {
                @Override
                public void success(Ruta ruta, Response response) {
                    Log.d("MapsActivity", ruta.getRoutes().get(0).getLegs().get(0).getEndAddress());
                    Polyline line;
                    for (int i=0; i<ruta.getRoutes().get(0).getLegs().get(0).getSteps().size(); i++) {

                        line = mMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(ruta.getRoutes().get(0).getLegs().get(0).getSteps().get(i).getStartLocation().getLat(),
                                                ruta.getRoutes().get(0).getLegs().get(0).getSteps().get(i).getStartLocation().getLng()),
                                     new LatLng(ruta.getRoutes().get(0).getLegs().get(0).getSteps().get(i).getEndLocation().getLat(),
                                                ruta.getRoutes().get(0).getLegs().get(0).getSteps().get(i).getEndLocation().getLng()))
                                .width(8)
                                .color(Color.BLUE));
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("MapsActivity", "Ha fallado la petición a la API");
                }
            });



        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Se llama al padre para que no se pierda del ciclo de vida
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    // Se llama al padre para que no se pierda del ciclo de vida
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
}
