package com.thesoftparrot.fbauth;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

public abstract class AuthBaseFragment extends Fragment {

    public AuthBaseFragment() {}


    private static final int REQUEST_CODE_PHONE_AUTH = 7654;
    private static final int REQUEST_CODE_EMAIL_AUTH = 8769;

    private static final List<AuthUI.IdpConfig> mPhoneNumberProvider = Collections.singletonList(
            new AuthUI.IdpConfig.PhoneBuilder().build()
    );

    private static final List<AuthUI.IdpConfig> mEmailProvider = Collections.singletonList(
            new AuthUI.IdpConfig.EmailBuilder().build()
    );

    protected abstract void authSuccessful(String userId);
    protected abstract void authFailed(String error);

    protected void signInByPhoneNumber(){

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(mPhoneNumberProvider)
                        .build(),
                REQUEST_CODE_PHONE_AUTH);
    }

    protected void signInByEmail(){

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(mEmailProvider)
                        .build(),
                REQUEST_CODE_EMAIL_AUTH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && data != null){
            if(requestCode == REQUEST_CODE_PHONE_AUTH || requestCode == REQUEST_CODE_EMAIL_AUTH){ // Auth via Phone Number
                IdpResponse response = IdpResponse.fromResultIntent(data);

                if(response != null){ // Successful
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();

                    if (user != null)
                        authSuccessful(user.getUid());
                    else
                        authFailed("Unable to Authenticate...");

                }else { // Failed
                    authFailed("Something went wrong while authenticating...");
                }

            }
        }
    }
}
