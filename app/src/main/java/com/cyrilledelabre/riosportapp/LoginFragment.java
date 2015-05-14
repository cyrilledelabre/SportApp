package com.cyrilledelabre.riosportapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrilledelabre.riosportapp.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass that implements FacebookLogin connection page.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class LoginFragment extends Fragment  {
    private final String LOG_TAG = LoginFragment.class.getSimpleName();

    private TextView mTextDetails;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;

    private Profile profile;
    private AccessToken accessToken;


    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {


            mProfileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile profile1, Profile profile2) {
                    Log.v("facebook - profile", profile2.getFirstName());

                    profile = profile2;
                    mProfileTracker.stopTracking();
                }
            };
            mProfileTracker.startTracking();


            Log.d(LOG_TAG, "onSuccess");
            accessToken = loginResult.getAccessToken();
            profile = Profile.getCurrentProfile();
            graphRequest(loginResult);
           constructWelcomeMessage(profile, accessToken);

        }


        @Override
        public void onCancel() {
            Log.d(LOG_TAG, "onCancel");
        }

        @Override
        public void onError(FacebookException e) {
            Log.d(LOG_TAG, "onError " + e);
        }
    };

    private void graphRequest(LoginResult loginResult)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        Log.v("LoginActivity", response.toString());
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }


    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        setupTokenTracker();
        setupProfileTracker();
        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
    return   inflater.inflate(R.layout.fragment_login, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //google
        setupGoogleButton(view);

        //facebook
        setupTextDetails(view);
        setupLoginButton(view);


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume");
        profile = Profile.getCurrentProfile();
        constructWelcomeMessage(profile, accessToken);
    }

    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCallbackManager.onActivityResult(requestCode, resultCode, data))
            return;

    }

    private void setupTextDetails(View view) {
        mTextDetails = (TextView) view.findViewById(R.id.text_details);
    }

    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d(LOG_TAG, "setupTokenTracker" + currentAccessToken);
                accessToken = currentAccessToken;
            }
        };
    }

    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d(LOG_TAG, "setupTokentreacker" + currentProfile);
                profile = currentProfile;
            }
        };
    }

    private void setupLoginButton(View view) {
        LoginButton mButtonLogin = (LoginButton) view.findViewById(R.id.login_button);
        mButtonLogin.setFragment(this);
        mButtonLogin.setReadPermissions(Arrays.asList("public_profile, email, user_friends"));

        mButtonLogin.registerCallback(mCallbackManager, mFacebookCallback);
    }

    private void setupGoogleButton(View view) {
        //view.findViewById(R.id.plus_sign_in_button).setOnClickListener(new View.OnClickListener());

    }



   /* public void onClick(View view) {
        Log.e(LOG_TAG, "onClick");

        if (view.getId() == R.id.plus_sign_in_button && !LoginActivity.mGoogleApiClient.isConnecting()) {
            LoginActivity.setmSignInClicked(true);
            LoginActivity.mGoogleApiClient.connect();
        }
    }*/

    private void constructWelcomeMessage(Profile profile, AccessToken accessToken) {

        this.profile = profile;
        Log.v(LOG_TAG,"constructWelcomeMessage");
        if (profile != null) {

            Toast.makeText(getActivity(), profile.getName()+"is connected!", Toast.LENGTH_LONG).show();
            Utils.saveEmailAccount(getActivity().getApplicationContext(), accessToken.toString());
            Utils.saveProfileName(getActivity().getApplicationContext(), profile);

            //de.greenrobot.event.EventBus.getDefault().postSticky(profile);


            //launchHomeScreen();

        }else{
            Log.v(LOG_TAG,"constructWelcomeMessage : profile null");

        }
    }

    private void launchHomeScreen()
    {
        Intent intent = new Intent(getActivity(),MainActivity.class);
        startActivity(intent);
    }


}
