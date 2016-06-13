package com.edu.flyers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

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
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
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
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
            //Location.distanceBetween(mLatitude, mLongitude, -10, 50);

            PeticionesRestAPI peticiones = RutaRestAdapter.getInstanced().create(PeticionesRestAPI.class);

            final String origen = mLatitude + "," + mLongitude;

            peticiones.rutaAsync(origen, address, "now", "car", new Callback<Ruta>() {
                @Override
                public void success(Ruta ruta, Response response) {
                    Log.d("MapsActivity", ruta.getStatus());

                    if (ruta.getRoutes().size() > 0) {

                        int stepsSize = ruta.getRoutes().get(0).getLegs().get(0).getSteps().size();

                        // Get polyline from steps
                        ArrayList<String> polylineList = new ArrayList<String>();
                        for (int i = 0; i < stepsSize; i++) {
                            polylineList.add(ruta.getRoutes().get(0).getLegs().get(0).getSteps().get(i).getPolyline().getPoints());
                        }

                        Polyline polyline;
                        PolylineOptions line = new PolylineOptions().width(8)
                                                                        .color(Color.BLUE);

                        IconGenerator iconFactory = new IconGenerator(getBaseContext());

                        ArrayList<LatLng> listaLL = new ArrayList<LatLng>();
                        for (int i = 0; i < polylineList.size(); i++) {
                            listaLL = (ArrayList<LatLng>) PolyUtil.decode(polylineList.get(i));
                            if (i == 0) {
                                addIcon(iconFactory, "Origen", listaLL.get(i));
                            }
                            for (int j = 0; j < listaLL.size(); j++) {
                                line.add(listaLL.get(j));
                            }
                        }

                        addIcon(iconFactory, "Destino", listaLL.get(listaLL.size()-1));

                        polyline = mMap.addPolyline(line);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("MapsActivity", "Ha fallado la petición a la API");
                }
            });
        }
    }

    private void addIcon(IconGenerator iconFactory, CharSequence text, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        mMap.addMarker(markerOptions);
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
