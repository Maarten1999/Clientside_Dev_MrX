package com.mpapps.clientside_dev_mrx.View;

import android.arch.lifecycle.AndroidViewModel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mpapps.clientside_dev_mrx.R;

public class NewGameActivity extends AppCompatActivity
{

    AndroidViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);


    }
}
