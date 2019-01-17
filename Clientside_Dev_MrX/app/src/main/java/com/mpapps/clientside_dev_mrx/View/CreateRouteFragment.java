package com.mpapps.clientside_dev_mrx.View;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mpapps.clientside_dev_mrx.Models.TravelMode;
import com.mpapps.clientside_dev_mrx.R;
import com.mpapps.clientside_dev_mrx.Services.CurrentGameInstance;

import java.util.Map;

public class CreateRouteFragment extends DialogFragment
{
    private OnCreateRouteListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_create_route, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        RadioGroup travelModeGroup = view.findViewById(R.id.createroute_fragment_radiogroup);
        Button createRouteBtn = view.findViewById(R.id.createroute_fragment_btn);
        createRouteBtn.setOnClickListener(view1 ->
        {
            int selectedId = travelModeGroup.getCheckedRadioButtonId();
            TravelMode travelMode = TravelMode.driving;
            switch (selectedId) {
                case R.id.createroute_fragment_bicycling:
                    travelMode = TravelMode.bicycling;
                    break;
                case R.id.createroute_fragment_walking:
                    travelMode = TravelMode.walking;
                    break;
            }
            LatLng misterXLocation = null;
            Map<String, Boolean> tempPlayers = CurrentGameInstance.getInstance().getGameModel().getValue().getPlayers();
            for (String s : tempPlayers.keySet()) {
                if(tempPlayers.get(s)){
                    misterXLocation = CurrentGameInstance.getInstance().getPlayerLocations().getValue().get(s);
                    break;
                }
            }
            if(misterXLocation == null)
                Toast.makeText(getContext(), getString(R.string.toast_no_location_available) + "Mister X", Toast.LENGTH_SHORT).show();
            else
                listener.OnCreateRoute(misterXLocation, travelMode);


            dismiss();
        });
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnCreateRouteListener) {
            listener = (OnCreateRouteListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCodeInputListener");
        }
    }

    public interface OnCreateRouteListener{
        void OnCreateRoute(LatLng latLng, TravelMode travelMode);
    }
}
