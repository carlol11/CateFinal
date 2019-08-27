package com.cate.cate.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cate.cate.R;
import com.cate.cate.adapters.PatientsAdapter;
import com.cate.cate.database.DatabaseAdapter;
import com.cate.cate.interfaces.ECAActivity;
import com.cate.cate.models.models.Patient;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The PatientsListActivity serves as the activity allowing
 * the user to view the patient list from a given school.
 * The user can view the list, search for patients, and
 * is given the option to select the patient or be redirected
 * to the module allowing for new patients to be added.
 *
 * @author Katrina Lacsamana
 */

/*
    This code is from Geebee and reused and redesigned for Cate

    -Edward Valdez
 */

public class PatientListActivity extends ECAActivity{
    /**
     * Used as a flag whether the ECA has spoken.
     */
    private boolean hasSpoken;

    /**
     * Keeps all the patients of the school.
     */
    private ArrayList<Patient> patients = null;

    /**
     * Patient chosen by the user.
     */
    private Patient chosenPatient = null;


    /**
     * Initializes views and other activity objects.
     *
     * @see android.app.Activity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        final Typeface quicksand = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.ttf");

        final DatabaseAdapter cateDB = new DatabaseAdapter(PatientListActivity.this);
        try {
            cateDB.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        patients = new ArrayList<>();
        patients = cateDB.getPatientsFromSchool(getSchoolPreferences());


        final EditText inputSearch = (EditText)findViewById(R.id.search_input);
        inputSearch.setTypeface(quicksand);

        final PatientsAdapter patientsAdapter = new PatientsAdapter(PatientListActivity.this, patients, quicksand);
        ListView patientListView = (ListView)findViewById(R.id.patientListView);
        patientListView.setAdapter(patientsAdapter);

        integrateECA();

        if(savedInstanceState == null){
            hasSpoken = false;
        } else {
            hasSpoken = savedInstanceState.getBoolean("hasSpoken");
        }

        TextView patientDetailsView = (TextView)findViewById(R.id.patientDetailsTV);
        patientDetailsView.setTypeface(quicksand);
        patientDetailsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);
            }
        });

        final Button selectPatientButton = (Button)findViewById(R.id.selectPatientButton);
        selectPatientButton.setTypeface(quicksand);
        selectPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(PatientListActivity.this, DecisionsActivity.class);
                    cateDB.updatePatient(chosenPatient);
                    intent.putExtra("patient", chosenPatient);
                    startActivity(intent);
                    finish();
                    cateDB.closeDatabase();
            }
        });

        Button addNewPatientButton = (Button)findViewById(R.id.addPatientButton);
        addNewPatientButton.setTypeface(quicksand);
        addNewPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientListActivity.this, AddPatientActivity.class);
                intent.putExtra("schoolID", getSchoolPreferences());
                startActivity(intent);
                finish();
            }
        });




        patientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chosenPatient = patients.get(position);
                String patientInfo = "First Name: " + chosenPatient.getFirstName() +
                        "\nLast Name: " + chosenPatient.getLastName() +
                        "\nBirthdate: " + chosenPatient.getBirthday() +
                        "\nGender: " + chosenPatient.getGenderString();
                TextView patientInfoView = (TextView) findViewById(R.id.patientDetailsTV);
                patientInfoView.setTypeface(quicksand);
                patientInfoView.setText(patientInfo);

                selectPatientButton.setEnabled(true);
                selectPatientButton.setTextColor(Color.WHITE);

                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);

                ecaFragment.sendToECAToSpeak("Are you " + chosenPatient.getFirstName() +
                        " " + chosenPatient.getLastName() + "?");

            }
        });


        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                patientsAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                patientsAdapter.filter(s.toString());
            }
        });
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
                ecaFragment.sendToECAToSPeak(R.string.patient_list_instruction);
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

    /**
     * Called when the activity has detected the user's press of the back key.
     * Starts {@link MainActivity} and ends the current activity.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PatientListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Gets the schoolID of the preferred school stored in device storage.
     * @return ID of the preferred school stored in device storage via Settings.
     */
    private int getSchoolPreferences(){
        int schoolID = 1; //default schoolID
        byte[] byteArray = new byte[6];
        try{
            FileInputStream fis = openFileInput("SchoolIDPreferences");
            fis.read(byteArray, 0, 6);
            fis.close();
        } catch(IOException e){
            return 1;
        }

        ByteBuffer b = ByteBuffer.wrap(byteArray);
        schoolID = b.getInt();

        return schoolID;
    }
}
