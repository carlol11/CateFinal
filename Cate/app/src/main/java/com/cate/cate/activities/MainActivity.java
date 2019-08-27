package com.cate.cate.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cate.cate.R;
import com.cate.cate.database.DatabaseAdapter;
import com.cate.cate.interfaces.ECAActivity;
import com.cate.cate.models.models.Patient;

import java.sql.SQLException;

/*
    This code is from Geebee and reused and redesigned for Cate

    -Edward Valdez
 */

public class MainActivity extends ECAActivity{
    /**
     * Used as a flag whether the ECA has spoken.
     */
    private boolean hasSpoken;

    /**
     * Initializes views and other activity objects.
     *
     * @see android.app.Activity#onCreate(Bundle)
     */


    Patient patient;
    DatabaseAdapter cateDB;
    public static final String PREFS_NAME = "CateSharedPref";
    public static final int PREFS_VERSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startButton = (Button)findViewById(R.id.startButton);
        Button updateButton = (Button) findViewById(R.id.updateButton);
        Typeface quicksand = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.ttf");
        startButton.setTypeface(quicksand);
        TextView title = (TextView) findViewById(R.id.TitleCate);
        title.setTypeface(quicksand);

        integrateECA();


        if(savedInstanceState == null){
            hasSpoken = false;

            DatabaseAdapter cateDB = new DatabaseAdapter(MainActivity.this);
            try {
                cateDB.createDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
                finish(); //exit app if database creation fails
            }
        } else {
            hasSpoken = savedInstanceState.getBoolean("hasSpoken");
        }

        cateDB = new DatabaseAdapter(MainActivity.this);
        try {
            cateDB.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, PREFS_VERSION);
        int patientID = sharedPreferences.getInt("patientID", 999);

        if(patientID != 999)
            patient = cateDB.getPatient(patientID);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(patient == null) {
                    Intent intent = new Intent(MainActivity.this, PatientListActivity.class);
                    cateDB.closeDatabase();
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(MainActivity.this, DecisionsActivity.class);
                    cateDB.closeDatabase();
                    startActivity(intent);
                }
            }
        });
/*        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });*/

    }

    /**
     * Called when the current Window of the activity gains or loses focus.
     * @param hasFocus whether the window of this activity has focus
     *
     * @see android.app.Activity#onWindowFocusChanged(boolean)
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            if(!hasSpoken){
                //Welcome message
                ecaFragment.sendToECAToSPeak(R.string.app_intro);
                //ecaFragment.sendToECAToEmote(ECAFragment.Emotion.HAPPY, 2);
                hasSpoken = true;
            }
        }
    }

    /**
     * Saves {@link #hasSpoken} inside {@code outState}
     *
     * @see android.app.Activity#onSaveInstanceState(Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hasSpoken", hasSpoken);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}
