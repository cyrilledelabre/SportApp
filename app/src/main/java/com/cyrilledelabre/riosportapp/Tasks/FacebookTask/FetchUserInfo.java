package com.cyrilledelabre.riosportapp.Tasks.FacebookTask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.cyrilledelabre.riosportapp.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cyrilledelabre on 23/05/15.
 */

public class FetchUserInfo extends AsyncTask<Void, Void, Boolean> {
    private final String LOG_TAG = FetchUserInfo.class.getSimpleName();


    private String email;
    private String name;
    private String id;
    private AccessToken mAccessToken;
    private Context mContext;

    private final static boolean SUCCESS = true;
    private final static boolean FAILURE = false;
    private boolean success = FAILURE;

    public FetchUserInfo(AccessToken accessToken, Context context)
    {
        mAccessToken = accessToken;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Boolean doInBackground(Void... params) {


        GraphRequest request = GraphRequest.newMeRequest(
                mAccessToken,
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
            Log.e(LOG_TAG, "JSON Exception : " + e);
        }
        return FAILURE;
    }
    @Override
    protected void onPostExecute(Boolean success) {
        if(success) {

            //save local info about facebook user///
            Utils.saveProfileName(mContext, name);
            //Toast.makeText(mContext, name + " is connected !", Toast.LENGTH_SHORT).show();
        }
    }


}