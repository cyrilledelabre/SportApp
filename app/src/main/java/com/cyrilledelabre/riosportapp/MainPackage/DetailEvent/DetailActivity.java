package com.cyrilledelabre.riosportapp.MainPackage.DetailEvent;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.SettingsActivity;
import com.cyrilledelabre.riosportapp.utils.Utils;

public class DetailActivity extends ActionBarActivity {


    private final String LOG_TAG = DetailActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //we use the delegate to inflate the layout
        setContentView(R.layout.activity_detail_event);

        if (savedInstanceState == null) {

            DetailFragment fragment = new DetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.event_detail_container, fragment)
                    .commit();

        }

    }

  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_account:
                new AlertDialog.Builder(getApplicationContext()).setTitle(null)
                        .setMessage(getString(R.string.clear_account_message))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Utils.saveEmailAccount(getApplicationContext(), null);
                                dialog.cancel();
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();

                break;
            case R.id.action_reload:
                //mEventListFragment.reload();
                break;
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }



}
