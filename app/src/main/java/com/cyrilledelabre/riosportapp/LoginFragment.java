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

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
/**
 * A simple {@link Fragment} subclass that implements FacebookLogin connection page.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class LoginFragment extends Fragment{
    private final String LOG_TAG = LoginFragment.class.getSimpleName();

    private TextView mTextDetails;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d(LOG_TAG, "onSuccess");
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            mTextDetails.setText(constructWelcomeMessage(profile));

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


    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
    return   inflater.inflate(R.layout.fragment_login, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupTextDetails(view);
        setupLoginButton(view);

        setupTokenTracker();
        setupProfileTracker();

        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
//        Profile profile = Profile.getCurrentProfile();
  //      mTextDetails.setText(constructWelcomeMessage(profile));
    }

    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setupTextDetails(View view) {
        mTextDetails = (TextView) view.findViewById(R.id.text_details);
    }

    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d(LOG_TAG, "setupTokentreacker" + currentAccessToken);
            }
        };
    }

    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d(LOG_TAG, "setupTokentreacker" + currentProfile);
                mTextDetails.setText(constructWelcomeMessage(currentProfile));
            }
        };
    }

    private void setupLoginButton(View view) {
        LoginButton mButtonLogin = (LoginButton) view.findViewById(R.id.login_button);
        mButtonLogin.setFragment(this);
        mButtonLogin.setReadPermissions("user_friends");
        mButtonLogin.registerCallback(mCallbackManager, mFacebookCallback);
    }

    private String constructWelcomeMessage(Profile profile) {
        StringBuffer stringBuffer = new StringBuffer();
        if (profile != null) {
            stringBuffer.append("Welcome " + profile.getName());
        }
        return stringBuffer.toString();
    }




    //Custom Functions
    protected boolean saveProfile(Profile profile)
    {
        if( profile != null) {
            Log.e(LOG_TAG, profile.getFirstName());
            //save Profile on the Bus
            //TODO see how save profile
            //de.greenrobot.event.EventBus.getDefault().postSticky(profile);
            return true;
        }
        return false;

    }

    protected void launchHomeScreen()
    {
        Intent intent = new Intent(getActivity(),MainActivity.class);
        startActivity(intent);
    }


}
