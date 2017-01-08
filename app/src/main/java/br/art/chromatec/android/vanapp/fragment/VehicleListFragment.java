package br.art.chromatec.android.vanapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import br.art.chromatec.android.vanapp.R;
import br.art.chromatec.android.vanapp.model.Vehicle;
import br.art.chromatec.android.vanapp.service.VehicleLocationService;
import br.art.chromatec.android.vanapp.service.VehicleServices;

public class VehicleListFragment extends Fragment {
    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> listItems;
    private VehicleLocationService vehicleLocationService;
    private OnFragmentInteractionListener mListener;
    private VehicleServices vehicleServices;

    public interface OnFragmentInteractionListener {
        //TODO:Nao é uma URI. É preciso ajustar para o objeto que irá para a Activity
        void onFragmentInteraction(Uri uri);
    }

    public VehicleListFragment() {
        this.listItems = new ArrayList();
        this.vehicleLocationService = new VehicleLocationService();
        this.vehicleServices = new VehicleServices();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_vehicle_list, container, false);
        ListView vehiclesListView = (ListView)layout.findViewById(R.id.listView);
        Bundle bundle = getActivity().getIntent().getExtras();
        //Vehicle vehicle = bundle.getParcelable("Veiculo1");


//        this.listItems.add(vehicle.getId());
//        this.listItems.add(vehicle.getCity());
//        this.listItems.add(vehicle.getDestination());
        this.listItems.add("Van1");
        this.listItems.add("Van2");
        this.listItems.add("Van3");
        this.itemsAdapter = new ArrayAdapter(
                vehiclesListView.getContext(),
                android.R.layout.simple_list_item_1,
                this.listItems);
        vehiclesListView.setAdapter(this.itemsAdapter);

        return vehiclesListView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            this.mListener = (OnFragmentInteractionListener) context;
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }

    //TODO:Nao é uma URI. É preciso ajustar para o objeto que irá para a Activity
    public void onButtonPressed(Uri uri) {
        if (this.mListener != null) {
            this.mListener.onFragmentInteraction(uri);
        }
    }
}
