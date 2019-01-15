package com.mpapps.clientside_dev_mrx.View;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mpapps.clientside_dev_mrx.R;

public class MisterXCodeFragment extends DialogFragment
{
    String code;

    public static MisterXCodeFragment newInstance(String code)
    {
        MisterXCodeFragment misterXCodeFragment = new MisterXCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("MisterX_code", code);
        misterXCodeFragment.setArguments(bundle);
        return misterXCodeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_misterx_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        code = getArguments().getString("MisterX_code");
        TextView codeTextView = view.findViewById(R.id.misterxcode_fragment_code);
        codeTextView.setText(code);
    }
}
