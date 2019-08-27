package com.cate.cate.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cate.cate.R;
import com.cate.cate.database.DatabaseAdapter;
import com.cate.cate.fragments.EmotionEnum;
import com.cate.cate.interfaces.ECAActivity;
import com.cate.cate.models.models.Patient;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * The AddPatientActivity serves as the activity containing
 * functionality for adding new patients.
 *
 * @author Katrina Lacsamana
 */

/*
    This code is from Geebee and reused and edited for Cate

    -Edward Valdez
 */

public class AddPatientActivity extends ECAActivity{
    /**
     * First name of the patient
     */
    private String firstName = null;

    /**
     * Last name of the patient
     */
    private String lastName = null;

    /**
     * Birthdate of the patient
     */
    private String birthDate = null;

    private String hobby = null;

    /**
     * Gender of the patient. Gender = 0, else Gender = 1
     */
    private int gender;

    /**
     * Handedness of the patient (Not Used for Cate).
     */
    private int handedness;

    /**
     * Patient that will be created and added to the database
     */
    private Patient patient = null;

    private ArrayList<Patient> patients = null;

    /**
     * Textview for the questions that is shown to the user
     */
    private TextView questionView;

    /**
     * Where the user enters {@link AddPatientActivity#firstName} and {@link AddPatientActivity#lastName}
     */
    private EditText editText;

    /**
     * Where the user enters {@link AddPatientActivity#birthDate}
     */
    private DatePicker datePicker;

    /**
     *Serves as the index counter for questions
     */
    private int questionCounter;

    /**
     * contains string resource of questions shown in {@link AddPatientActivity#questionView}
     */
    private final int[] questions = {R.string.first_name, R.string.last_name, R.string.birthdate,
                                    R.string.gender, R.string.hobbies};

    /**
     * contains the fragment that asks for remarks from the assistant
     */

    int index;
    DatabaseAdapter cateDB;

    /**
     * Initializes views and other activity objects.
     *
     * @see android.app.Activity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        questionView = (TextView)findViewById(R.id.questionView);
        editText = (EditText)findViewById(R.id.newPatientStringInput);
        datePicker = (DatePicker)findViewById(R.id.newPatientDatePicker);
        datePicker.setMaxDate(new Date().getTime());
        datePicker.updateDate(1959, 0 , 1);
        final RadioButton radioButton0 = (RadioButton)findViewById(R.id.radioButton1);
        final RadioButton radioButton1 = (RadioButton)findViewById(R.id.radioButton2);
        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.newPatientRadioChoice);

        integrateECA();
        final Typeface quicksand = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.ttf");
        questionView.setTypeface(quicksand);
        radioButton0.setTypeface(quicksand);
        radioButton1.setTypeface(quicksand);
        editText.setTypeface(quicksand);

        cateDB = new DatabaseAdapter(AddPatientActivity.this);
        try {
            cateDB.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }



        if (savedInstanceState != null){
            questionCounter = savedInstanceState.getInt("questionCounter");
        } else {
            questionCounter = 0;
        }

        setQuestion(questions[questionCounter]);
        editText.setVisibility(View.VISIBLE);

        Button cancelButton = (Button)findViewById(R.id.cancelNewPatientButton);
        cancelButton.setTypeface(quicksand);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patient = null;
                questionCounter = 0;
                Intent intent = new Intent(AddPatientActivity.this, PatientListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button saveButton = (Button)findViewById(R.id.saveNewPatientButton);
        saveButton.setTypeface(quicksand);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(questionCounter){
                    case 0:
                        firstName = getEditText();
                        editText.setText("");
                        break;
                    case 1:
                        lastName = getEditText();
                        editText.setVisibility(View.GONE);
                        datePicker.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        birthDate = getSelectedDate();
                        datePicker.setVisibility(View.GONE);
                        radioGroup.setVisibility(View.VISIBLE);
                        radioButton0.setText(R.string.male);
                        radioButton1.setText(R.string.female);
                        break;
                    case 3:
                        int selectedID = radioGroup.getCheckedRadioButtonId();
                        if(selectedID == radioButton0.getId()){
                            gender = 0;
                        } else if(selectedID == radioButton1.getId()){
                            gender = 1;
                        }
                        radioGroup.check(R.id.radioButton1);
                        radioButton0.setText(R.string.right_handed);
                        radioButton1.setText(R.string.left_handed);
                        radioButton0.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handedness = 0;
                            }
                        });
                        radioButton1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handedness = 1;
                            }
                        });
                        radioGroup.setVisibility(View.GONE);
                        editText.setVisibility(View.VISIBLE);
                        editText.setText("");
                        break;
                    case 4:
                        editText.setVisibility(View.GONE);
                        hobby = getEditText();



                        patient = new Patient(firstName, lastName, birthDate, gender, getIntent().getIntExtra("schoolID", 1), handedness, 0, hobby, 1, EmotionEnum.HAPPY);
                        String patientDetails = "First Name: " + patient.getFirstName() +
                                                "\nLast Name: " + patient.getLastName() +
                                                "\nBirthdate: " + patient.getBirthday() +
                                                "\nHobby: " + patient.getHobby() +
                                                "\nGender: " + patient.getGenderString();

                        questionView.setText(patientDetails);

                        ecaFragment.sendToECAToSPeak(R.string.add_patient_confirm);
                        break;
                    case 5:
                        savePatientToDatabase(patient);

                        Intent intent = new Intent(AddPatientActivity.this, DecisionsActivity.class);
                        intent.putExtra("patient", cateDB.getLatestPatient());
                        cateDB.closeDatabase();
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
                questionCounter++;
                if(questionCounter<5){
                    setQuestion(questions[questionCounter]);
                }
            }
        });

    }

    /**
     * Called when the activity has detected the user's press of the back key.
     * Starts {@link PatientListActivity} and ends the current activity.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddPatientActivity.this, PatientListActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Saves {@link #questionCounter} inside {@code outState}
     *
     * @see android.app.Activity#onSaveInstanceState(Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("questionCounter", questionCounter);
    }

    /**
     * Display question on screen based on resID parameter
     * @param resID string resource ID of the question
     */
    private void setQuestion(int resID){
        questionView.setText(resID);
        ecaFragment.sendToECAToSpeak(getResources().getString(resID));
    };

    /**
     * @return the text from {@link #editText}
     */
    private String getEditText(){
        return editText.getText().toString();
    };


    /**
     * Gets the String of the birthdate of patient in the format of MM/DD/YYYY
     * @return the date from {@link #datePicker}
     */
    private String getSelectedDate(){
        return (datePicker.getMonth() + 1 + "/" + datePicker.getDayOfMonth() + "/" + datePicker.getYear());
    }

    /**
     * Saves patient to database.
     * @param patient to be saved to the database
     */
    private void savePatientToDatabase(Patient patient){
        DatabaseAdapter cateDB = new DatabaseAdapter(AddPatientActivity.this);
        try {
            cateDB.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        patient.printPatient();
        cateDB.insertPatient(patient);

        cateDB.closeDatabase();
    }
}

