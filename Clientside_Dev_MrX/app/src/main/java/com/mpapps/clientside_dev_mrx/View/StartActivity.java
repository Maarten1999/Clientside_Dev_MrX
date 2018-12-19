package com.mpapps.clientside_dev_mrx.View;

import android.content.Intent;
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

public class StartActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        FirebaseMessagingService.getToken();

        ArrayList<String> strings = new ArrayList<>();
        strings.add("cmTf5n1za40:APA91bEkE0bp4CDAGuFTdZ6YuUyB5IP-zeH61edPA2K1g0g4pnXAwta-Kjjm7VGYih33B9xPgTH5jKbKiDtFImM0rt62HQ5LiX5CcE92aG_KH9P7i3CnQa6MIbZX6Uq7ulIBKKa2XcOm");

        Requests.createMessagingGroup("volley_test", strings, getApplicationContext());

        createSignInIntent();
    }

    private void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build());

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
            } else {
                //Inloggen is mislukt
                createSignInIntent();
            }
        }
    }
}
