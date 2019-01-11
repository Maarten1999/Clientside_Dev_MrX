package com.mpapps.clientside_dev_mrx.View;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mpapps.clientside_dev_mrx.R;

public class JoinGameFragment extends DialogFragment
{
    public static JoinGameFragment newInstance(){
        return new JoinGameFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_join_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        EditText codeInput = view.findViewById(R.id.joingame_fragment_edittext);
        Button joinGameButton = view.findViewById(R.id.joingame_fragment_btn);
        joinGameButton.setOnClickListener(view1 -> {
            if(codeInput.getText().toString().length() < 4){
                Toast.makeText(getContext(), "Invalid gamecode, 4 characters needed.", Toast.LENGTH_SHORT).show();
            }else{
                String gameCode = codeInput.getText().toString();
                //TODO Firebase request for joining topic
                dismiss();
            }
        });
    }
}
