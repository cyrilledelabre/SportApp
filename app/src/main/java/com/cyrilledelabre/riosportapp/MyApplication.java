package com.cyrilledelabre.riosportapp;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by cyrilledelabre on 13/05/15.
 */
public class MyApplication extends Application {
        //class not used :: has to be enabled in the manifest
    public void onCreate(){
        super.onCreate();
        //printHaskKey();
    }



    public void printHaskKey()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.cyrilledelabre.riosportapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

}
