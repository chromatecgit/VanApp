package br.art.chromatec.android.vanapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.art.chromatec.android.vanapp.R;

public class MapFragment extends Fragment {

    private GoogleMap googleMap;
    private MapView mapView;
    private double[] coordinates = new double[2];
    private static final String COORDINATES = "coordinates";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        throws SecurityException {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        Bundle mapBundle = getArguments();
        this.coordinates = mapBundle.getDoubleArray(COORDINATES);

        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                LatLng phoneSpot = new LatLng(0.0, 0.0);

                if (coordinates != null && coordinates.length > 0) {
                    phoneSpot = new LatLng(coordinates[0], coordinates[1]);
                } else {coordinates[0] = -23.5745063;
                    coordinates[1] = -46.7271792;

                }

                // TODO: 1 - Criar o layout e a atividade que irão conter o mapa e a lista de vans
                // TODO: 2 - Criar o código de envio do JSON para o Java
                // TODO: 3 - Criar o código que recebe as informações do Android e manda a mensagem de que ele está ou não na área permitida
                CameraUpdate center = CameraUpdateFactory.newLatLng(phoneSpot);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                googleMap.moveCamera(center);
                googleMap.animateCamera(zoom);
                googleMap.addMarker(
                        new MarkerOptions()
                                .position(phoneSpot)
                                .title("Client Location"));

                // For showing a move to my location button
                try {
                    googleMap.setMyLocationEnabled(true);
                } catch (SecurityException se) {
                    throw se;
                }

                // For dropping a marker at a point on the Map
                //LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(phoneSpot).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(phoneSpot).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return mapView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public static Intent newIntent(Context packageContext, double[] extraCoordinates) {
        Intent i = new Intent(packageContext, MapFragment.class);
        i.putExtra(COORDINATES, extraCoordinates);
        return i;
    }
}
