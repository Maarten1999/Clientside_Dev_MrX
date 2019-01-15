package com.mpapps.clientside_dev_mrx.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mpapps.clientside_dev_mrx.FirebaseMessagingService;
import com.mpapps.clientside_dev_mrx.R;
import com.mpapps.clientside_dev_mrx.Volley.Requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BootActivity extends AppCompatActivity
{
    private SharedPreferences sharedPref;
    private static final int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);

        //get sharedPref
        sharedPref = this.getSharedPreferences(getString(R.string.sharedPreferences), Context.MODE_PRIVATE);

        FirebaseMessagingService firebaseMessagingService = new FirebaseMessagingService(this);
        firebaseMessagingService.getToken();


        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            createSignInIntent();
        else{
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
        }
    }

    private void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .enableAnonymousUsersAutoUpgrade()
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.drawable.mister_x_title_full)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                //Succesvol ingelogd
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseMessaging.getInstance().setAutoInitEnabled(true);
                String displayname = user.getDisplayName();
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("displayname", displayname);
                editor.commit();

                Intent intent = new Intent(this, StartActivity.class);
                startActivity(intent);
            } else {
                //Inloggen is mislukt
                createSignInIntent();
            }
        }
    }
}
