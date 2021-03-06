package com.thesoftparrot.fbauth;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

public abstract class AuthBaseActivity extends AppCompatActivity {

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

    protected void signInByEmailAndPassword(String email, String password){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if(authResult != null){
                        FirebaseUser user = authResult.getUser();

                        if(user != null){
                            String userId = user.getUid();
                            authSuccessful(userId);
                        }
                    }else {
                        authFailed("Unable to authenticate via given email and password");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        authFailed(e.getMessage());
                    }
                });
    }

    protected String getUserId(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user != null)
            return user.getUid();

        authFailed("Please SignIn to proceed...");

        return null;
    }

    protected void signOut(Class target){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();

        Intent intent = new Intent(AuthBaseActivity.this, target);
        startActivity(intent);
        finish();
    }

    protected void registerNewUser(String email, String password){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if(authResult != null){
                        FirebaseUser user = authResult.getUser();

                        if(user != null){
                            String userId = user.getUid();
                            authSuccessful(userId);
                        }
                    }else {
                        authFailed("Unable to Register via given email and password");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        authFailed(e.getMessage());
                    }
                });
    }

    protected void signInAnonymously(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth
                .signInAnonymously()
                .addOnSuccessListener(authResult -> {
                    if(authResult != null){
                        FirebaseUser user = authResult.getUser();

                        if(user != null){
                            String userId = user.getUid();
                            authSuccessful(userId);
                        }
                    }else {
                        authFailed("Unable to Authenticate Anonymously");
                    }
                })
                .addOnFailureListener(e -> authFailed(e.getMessage()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data != null){
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
