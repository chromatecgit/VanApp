package br.art.chromatec.android.vanapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.art.chromatec.android.vanapp.fragment.MapFragment;
import br.art.chromatec.android.vanapp.R;
import br.art.chromatec.android.vanapp.fragment.VehicleListFragment;
import br.art.chromatec.android.vanapp.model.Vehicle;
import br.art.chromatec.android.vanapp.service.VehicleServices;
import br.art.chromatec.android.vanapp.util.ViewPagerAdapter;

import static android.util.Log.i;

/**
 * Created by Chromatec on 15/12/2016.
 */
public class MainActivity extends AppCompatActivity implements VehicleListFragment.OnFragmentInteractionListener {

    private double[] coordinates = new double[2];
    private final int CODE_PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 10;
    private Location userLocation;
    private static final String COORDINATES = "coordinates";
    private static final String JSON_MACTIVITY_RESPONSE = "JSON_MACTIVITY_RESPONSE";
    private static final String TAG_SERVICE_CALLED = "SERVICE_CALLED";
    private static final String TAG_SERVICE_ANSWERED = "SERVICE_ANSWERED";
    private static final String TAG_PROGRESS_DIALOG_CALLED = "PROGRESS_DIALOG_CALLED";
    private ViewPager mViewPager;
    private VehicleServices vehicleServices;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //TODO: Codigo que verifica se o usuario permitiu a obtencao de sua localizacao

        boolean isLocationAllowed = checkPermissionLocation(MainActivity.this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        if (isLocationAllowed) {
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

            /** Recupera a localizacao do usuario */
            this.userLocation = getUserCurrentLocation(isLocationAllowed);
            if (this.userLocation != null) {
                this.coordinates[0] = this.userLocation.getLatitude();
                this.coordinates[1] = this.userLocation.getLongitude();
            } else {
                this.coordinates[0] = -23.5745063;
                this.coordinates[1] = -46.7271792;
            }

            /** Recupera os dados dos veiculos disponiveis */
            this.vehicleServices = new VehicleServices();
            i(TAG_SERVICE_CALLED, "Servico de Veiculos foi chamado");
            final AsyncTask<String, Void, JSONArray> vTask = this.vehicleServices.execute();
            Log.i(TAG_PROGRESS_DIALOG_CALLED, "Progress Dialog iniciado");
            //TODO: Colocar essa lógica na aba de Lista de Veículos

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ProgressDialog progressDialog = null;
                        while(jsonArray == null) {
                            progressDialog = ProgressDialog.show(MainActivity.this, "Executando serviço...","Buscando veículos disponíveis", true);
                            progressDialog.setCancelable(true);
                        }
                        i(TAG_SERVICE_ANSWERED,"Servico de Veiculos foi finalizado");
                        i(JSON_MACTIVITY_RESPONSE, jsonArray.toString());
                        jsonArray = vTask.get();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
            //TODO: Transferir para um metodo separado de parse (jsonObjToVehicle
            //TODO: Decidir se permito a exibicao da tela somente com o retorno do servico (e com timeout)

            final List<Vehicle> vehicles = new ArrayList<>();
            try {
                for (int i = 0; i <= jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    Vehicle vehicle = new Vehicle();

                    vehicle.setId((String) jsonObject.get("id"));
                    vehicle.setState((String) jsonObject.get("state"));
                    vehicle.setCity((String) jsonObject.get("city"));
                    vehicle.setDestination((String) jsonObject.get("destination"));
                    vehicle.setPassengerCapacity((int) jsonObject.get("passengerCapacity"));
                    vehicle.setAvailable((boolean) jsonObject.get("isAvailable"));

                    vehicles.add(vehicle);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            /* LIST */
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            VehicleListFragment vehicleListFragment = new VehicleListFragment();

            Bundle bundleListView = new Bundle();
            for (int i = 0; i <= vehicles.size(); i++) {
                bundleListView.putParcelable("Veiculo" + (i + 1), vehicles.get(0));
            }

            vehicleListFragment.setArguments(bundleListView);

            /* MAP */
            MapFragment mapFragment = new MapFragment();
            Bundle mapBundle = new Bundle();
            mapBundle.putDoubleArray(COORDINATES, this.coordinates);
            mapFragment.setArguments(mapBundle);

            viewPagerAdapter.addFragment(vehicleListFragment, "Lista");
            viewPagerAdapter.addFragment(mapFragment, "Mapa");

            this.mViewPager.setAdapter(viewPagerAdapter);
            tabLayout.setupWithViewPager(mViewPager);

        } else {
            //TODO: Exibir mensagem de que nao foi possivel obter localizacao
        }

    }

    private Location getUserCurrentLocation(boolean isAllowed) {
        if (isAllowed) {
            LocationManager locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(Context.LOCATION_SERVICE);
            Location location = null;
            try {
                String provider = checkProviders(locationManager);
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        coordinates[0] = location.getLongitude();
                        coordinates[1] = location.getLatitude();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        //TODO: Exibir mensagem de que nao ha provedores disponiveis
                    }
                };
                locationManager.requestLocationUpdates(provider, 5000, 10, locationListener);
                location = locationManager.getLastKnownLocation(provider);

            } catch (SecurityException e) {
                e.printStackTrace();
            }
            return location;
        }
        return null;
    }

    private boolean checkPermissionLocation(Activity activity) {
        int permission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {

            //TODO: Exibir a mensagem abaixo, mas apresentar uma tela onde seja possivel pedir para tentar novamente
            Context context = getApplicationContext();
            CharSequence text = "Não foi possível obter sua localização!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    CODE_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
        return false;
    }

    private String checkProviders(LocationManager locationManager) {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CODE_PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    userLocation = getUserCurrentLocation(checkPermissionLocation(MainActivity.this));
                    if (userLocation != null) {
                        coordinates[0] = userLocation.getLatitude();
                        coordinates[1] = userLocation.getLongitude();
                    }

                }
            }
        }
    }

    public static Intent newIntent(Context packageContext, boolean isUserAllowed) {
        if (isUserAllowed) {
            return (new Intent(packageContext, MainActivity.class));
        }
        return null;
    }

    @Override
    public void onFragmentInteraction(Uri paramUri) {
        //TODO: Do something
    }


}
