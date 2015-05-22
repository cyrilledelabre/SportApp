package com.cyrilledelabre.riosportapp.MainPackage.Login;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrilledelabre.riosportapp.MainPackage.MainActivity;
import com.cyrilledelabre.riosportapp.R;
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

import org.json.JSONException;
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

    private FetchUserEmail mUserEmailTask;


    private static Profile profile;
    private static AccessToken accessToken;


    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.e(LOG_TAG, "onSuccess : LOGIN SUCCESS");
            accessToken = loginResult.getAccessToken();
            profile = Profile.getCurrentProfile();
            //wait until getting the response
           constructAndSave();

        }


        @Override
        public void onCancel() {
            Log.d(LOG_TAG, "onCancel");

            Utils.displayNetworkErrorMessage(getActivity().getApplicationContext());
        }

        @Override
        public void onError(FacebookException e) {
            Log.d(LOG_TAG, "onError " + e);
            Utils.displayNetworkErrorMessage(getActivity().getApplicationContext());

        }
    };



    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( savedInstanceState == null)
        {
            mCallbackManager = CallbackManager.Factory.create();
            setupTokenTracker();
            setupProfileTracker();
            mTokenTracker.startTracking();
            mProfileTracker.startTracking();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
        mUserEmailTask.cancel(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //facebook
        setupLoginButton(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        constructAndSave();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCallbackManager.onActivityResult(requestCode, resultCode, data))
            return;
        else
            getActivity().finish();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
        mUserEmailTask.cancel(true);
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
                Log.d(LOG_TAG, "setupProfileTracker" + currentProfile);
                profile = currentProfile;
                constructAndSave();
            }
        };
    }

    private void setupLoginButton(View view) {
        LoginButton mButtonLogin = (LoginButton) view.findViewById(R.id.login_button);
        mButtonLogin.setFragment(this);
        mButtonLogin.setReadPermissions(Arrays.asList("public_profile, email, user_friends"));
        mButtonLogin.registerCallback(mCallbackManager, mFacebookCallback);
    }

    private void constructAndSave() {
        profile = Profile.getCurrentProfile();
        if (profile != null) {
            //update Token and Profile
            accessToken = AccessToken.getCurrentAccessToken();
            Context myContext = getActivity().getApplicationContext();
            if(Utils.getTokenAccess(myContext) != accessToken.getToken())
            {
                Log.v(LOG_TAG, "executing mUserEmailtask accessToken != savedAccessToken");
                mUserEmailTask = new FetchUserEmail();
                mUserEmailTask.execute();

            }
            else
            {
                goMainActivityIntent();
            }


        }
    }



    private class FetchUserEmail extends AsyncTask<String, Integer, Boolean> {

        private String email;
        private String name;
        private String id;

        private final static boolean SUCCESS = true;
        private final static boolean FAILURE = false;
        private boolean success = FAILURE;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... params) {


            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            success = saveUserData(response);
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields","id,name,link");
            request.setParameters(parameters);
            request.executeAndWait();

            return success;

        }

        private boolean saveUserData(GraphResponse response)
        {
            //Log.v(LOG_TAG,response.toString());
            try{
                //email = response.getJSONObject().get("email").toString();
                name = response.getJSONObject().get("name").toString();
                //id = response.getJSONObject().get("id").toString();
                 return SUCCESS;
            }catch(JSONException e)
            {
                Log.e(LOG_TAG,"JSON Exception : " + e);
            }
            return FAILURE;
        }
        @Override
        protected void onPostExecute(Boolean success) {
            if(success) {
                Context myContext = getActivity().getApplicationContext();
                Utils.saveTokenAccess(myContext, accessToken.getToken());
                Utils.saveProfileName(myContext, name);
                //TODO make better all
                Toast.makeText(myContext, name + " is connected !", Toast.LENGTH_SHORT).show();

                goMainActivityIntent();

            }
        }


    }

    private void goMainActivityIntent()
    {

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish(); // Call once you redirect to another activity
    }
}
