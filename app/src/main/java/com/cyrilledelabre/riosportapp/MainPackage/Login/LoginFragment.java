package com.cyrilledelabre.riosportapp.MainPackage.Login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrilledelabre.riosportapp.MainPackage.MainActivity;
import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.Tasks.ApiTask.AuthorizationCheckTask;
import com.cyrilledelabre.riosportapp.Tasks.FacebookTask.FetchUserInfo;
import com.cyrilledelabre.riosportapp.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

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

    private FetchUserInfo mUserTask;

    private Context mContext;
    private static Profile mProfile;
    private static AccessToken mAccessToken;

    private Button mDismissButton;

    /**
     * for the authorization with a google account
     */
    private AuthorizationCheckTask mAuthTask;
    private String mEmailAccount;
    /**
     * Activity result indicating a return from the Google account selection intent.
     */
    private static final int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 2222;


    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.e(LOG_TAG, "onSuccess : LOGIN SUCCESS");
            //mAccessToken = loginResult.getAccessToken();
            //mProfile = Profile.getCurrentProfile();
            //wait until getting the response
           goMainActivityIntent();

        }

        @Override
        public void onCancel() {
            Log.e(LOG_TAG, "onCancel");
            Utils.displayNetworkErrorMessage(getActivity().getApplicationContext());
        }

        @Override
        public void onError(FacebookException e) {
            Log.e(LOG_TAG, "onError " + e);
            Utils.displayNetworkErrorMessage(getActivity().getApplicationContext());

        }
    };
    public LoginFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext =getActivity().getApplicationContext();
        mEmailAccount = Utils.getEmailAccount(mContext);
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
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LoginButton mButtonLogin = (LoginButton) view.findViewById(R.id.login_button);
        mDismissButton = (Button) view.findViewById(R.id.dismissButton);

        if(Utils.getTokenAccess(mContext) == null)
        {
            mButtonLogin.setFragment(this);
            mButtonLogin.setReadPermissions(Arrays.asList("public_profile, email, user_friends"));
            mButtonLogin.registerCallback(mCallbackManager, mFacebookCallback);

            mDismissButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goMainActivityIntent();
                }
            });
        }
        else
        {
            mButtonLogin.setVisibility(View.GONE);
            mDismissButton.setVisibility(View.GONE);
            goMainActivityIntent();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Utils.getTokenAccess(mContext) != null)
        {
            goMainActivityIntent();
        }

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
        //account picker
        if (requestCode == ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION && resultCode == Activity.RESULT_OK) {
            mEmailAccount = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            goMainActivityIntent();
            return;
        }
        getActivity().finish();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
        if (mAuthTask != null) {
            mAuthTask.cancel(true);
            mAuthTask = null;
        }
    }

    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.e(LOG_TAG, "setupTokenTracker");
                mAccessToken = currentAccessToken;
                Utils.saveTokenAccess(mContext, mAccessToken.getToken());
            }
        };
    }

    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.e(LOG_TAG, "setupProfileTracker ");
                mProfile = currentProfile;
                //Fetch new User Info
                mUserTask = new FetchUserInfo(mAccessToken, mContext);
                mUserTask.execute();
            }
        };
    }

    private void constructAndSave() {
        Log.e(LOG_TAG, "constructAndSave");
        if (mProfile != null) {
            goMainActivityIntent();
        }
    }




    private void goMainActivityIntent()
    {
        if (mEmailAccount !=null) {
            performAuthCheck(mEmailAccount);
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish(); // Call once you redirect to another activity
        } else {
            selectAccount();
        }

    }




    @Override
    public void onPause() {
        super.onPause();
    }



    private void selectAccount() {
        //Get the email account from
        Account[] accounts = Utils.getGoogleAccounts(mContext.getApplicationContext());

        int numOfAccount = accounts.length;
        switch (numOfAccount) {
            case 0:
                // No accounts registered, nothing to do.
                Toast.makeText(mContext.getApplicationContext(), R.string.toast_no_google_accounts_registered,
                        Toast.LENGTH_LONG).show();
                break;
            case 1:
                mEmailAccount = accounts[0].name;
                performAuthCheck(mEmailAccount);
                break;
            default:
                // More than one Google Account is present, a chooser is necessary.
                // Invoke an {@code Intent} to allow the user to select a Google account.
                Intent accountSelector = AccountPicker.newChooseAccountIntent(null, null,
                        new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false,
                        getString(R.string.select_account_for_access), null, null, null);
                startActivityForResult(accountSelector, ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION);
        }
    }

    /*
     * Schedule the authorization check.
     */
    private void performAuthCheck(String email) {
        // Cancel previously running tasks.
        if (mAuthTask != null) {
            mAuthTask.cancel(true);
        }
        // Start task to check authorization.
            mAuthTask = new AuthorizationCheckTask(getActivity());
        try{
            mAuthTask.execute(email).get();
        }catch(Exception e)
        {
            Log.e(LOG_TAG,"Error PerformAuthTask" + e);
        }
    }
}
