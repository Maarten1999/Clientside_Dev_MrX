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

public class MisterXCodeInputFragment extends DialogFragment
{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_input_misterx_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        EditText codeInput = view.findViewById(R.id.misterxcode_input_fragment_edittext);
        Button checkCodeButton = view.findViewById(R.id.misterxcode_input_fragment_btn);
        checkCodeButton.setOnClickListener(view1 -> {
            if(codeInput.getText().toString().length() < 1){
                Toast.makeText(getContext(), "Enter a code", Toast.LENGTH_SHORT).show();
            }else{
                String misterxCode = codeInput.getText().toString();
                //TODO Firebase request for checking misterx code

                //dismiss();
            }
        });
    }
}
