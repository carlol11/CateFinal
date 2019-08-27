package com.cate.cate.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cate.cate.fragments.EmotionEnum;
import com.cate.cate.models.models.Decision;
import com.cate.cate.models.models.Memory;
import com.cate.cate.models.models.Patient;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The DatabaseAdapter class contains methods which serve as database queries.
 * The original class used as a basis for this current version was
 * created by Mike Dayupay for HPI Generation module of the GetBetter project.
 *
 * @author Mike Dayupay
 * @author Mary Grace Malana
 */

/*
    This code is from Geebee and reused and redesigned for Cate

    -Edward Valdez
 */

public class DatabaseAdapter {

    /**
     * Used to identify the source of a log message
     */
    protected static final String TAG = "DatabaseAdapter";

    /**
     * use as the database of the app
     */
    private SQLiteDatabase cateDB;

    /**
     * Contains the helper class of the database
     */
    private DatabaseHelper cateDatabaseHelper;

    /**
     * Table name of the symptoms in the database
     */
    private static final String SYMPTOM_LIST = "tbl_symptom_list";

    /**
     * Table name of the symptom families in the database
     */
    private static final String SYMPTOM_FAMILY = "tbl_symptom_family";

    /**
     * Creates a new instance of {@link DatabaseHelper}.
     * @param context current context.
     */
    public DatabaseAdapter(Context context) {
        cateDatabaseHelper  = new DatabaseHelper(context);
    }

    /**
     * Creates the database using the helper class
     *
     * @return a reference of itself
     * @throws SQLException if a database error occured
     */
    public DatabaseAdapter createDatabase() throws SQLException {

        try {
            cateDatabaseHelper.createDatabase();
        }catch (IOException ioe) {
            Log.e(TAG, ioe.toString() + "UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    /**
     * Opens the database for read or write unless the database problem occurs
     * that limits the user from writing to the database.
     *
     * @return a reference of itself
     * @throws SQLException if a database error occured
     *
     * @see #openDatabaseForRead()
     */
    public DatabaseAdapter openDatabaseForRead() throws SQLException {

        try {
            cateDatabaseHelper.openDatabase();
            cateDatabaseHelper.close();
            cateDB = cateDatabaseHelper.getReadableDatabase();
        }catch (SQLException sqle) {
            Log.e(TAG, "open >>" +sqle.toString());
            throw sqle;
        }
        return this;
    }

    /**
     * Opens the database for read or write. Method call may fail
     * if a database problem occurs.
     *
     * @return a reference of itself
     * @throws SQLException if a database error occured
     *
     * @see #openDatabaseForRead()
     */
    public DatabaseAdapter openDatabaseForWrite() throws SQLException {

        try {
            cateDatabaseHelper.openDatabase();
            cateDatabaseHelper.close();
            cateDB = cateDatabaseHelper.getWritableDatabase();
        }catch (SQLException sqle) {
            Log.e(TAG, "open >>" +sqle.toString());
            throw sqle;
        }
        return this;
    }

    /**
     * Closes the database
     */
    public void closeDatabase() {
        cateDatabaseHelper.close();
    }


    /**
     * Insert {@code patient} to the database.
     * @param patient Patient to be added to the database.
     */

    public void insertPatient(Patient patient){
        ContentValues values = new ContentValues();
        int row;

        values.put(Patient.C_FIRST_NAME, patient.getFirstName());
        values.put(Patient.C_LAST_NAME, patient.getLastName());
        values.put(Patient.C_BIRTHDAY, patient.getBirthday());
        values.put(Patient.C_HOBBY, patient.getHobby());
        values.put(Patient.C_GENDER, patient.getGender());
        values.put(Patient.C_SCHOOL_ID, patient.getSchoolId());
        values.put(Patient.C_HANDEDNESS, patient.getHandedness());
        values.put(Patient.C_REMARKS_STRING, patient.getRemarksString());
        values.put(Patient.C_REMARKS_AUDIO, patient.getRemarksAudio());
        values.put(Patient.C_NEW_USER, patient.isNew() ? 1 : 0);
        values.put(Patient.C_SCORE, patient.getScore());
        values.put(Patient.C_PREVIOUS_EMOTION, (patient.previous_emotion != null) ? patient.previous_emotion.toString() : "HAPPY");

        row = (int) cateDB.insert(Patient.TABLE_NAME, null, values);
        Log.d(TAG, "insertPatient Result: " + row);
    }

    public ArrayList<Patient> getPatientsFromSchool(int schoolID){

//        Cursor co = cateDB.rawQuery("SELECT * FROM random_decision", null);
//        co.moveToFirst();
//        String s1 = co.getString(co.getColumnIndex("question"));
//        co.close();

        ArrayList<Patient> patients = new ArrayList<>();
        Cursor c = cateDB.query(Patient.TABLE_NAME, null, Patient.C_SCHOOL_ID + " = " + schoolID, null, null, null, Patient.C_LAST_NAME +" ASC");
        if(c.moveToFirst()){
            do{
                patients.add(new Patient(c.getInt(c.getColumnIndex(Patient.C_PATIENT_ID)),
                        c.getString(c.getColumnIndex(Patient.C_FIRST_NAME)),
                        c.getString(c.getColumnIndex(Patient.C_LAST_NAME)),
                        c.getString(c.getColumnIndex(Patient.C_BIRTHDAY)),
                        c.getInt(c.getColumnIndex(Patient.C_GENDER)),
                        c.getInt(c.getColumnIndex(Patient.C_SCHOOL_ID)),
                        c.getInt(c.getColumnIndex(Patient.C_HANDEDNESS)),
                        c.getString(c.getColumnIndex(Patient.C_REMARKS_STRING)),
                        c.getBlob(c.getColumnIndex(Patient.C_REMARKS_AUDIO)),
                        c.getInt(c.getColumnIndex(Patient.C_SCORE)),
                        c.getString(c.getColumnIndex(Patient.C_HOBBY)),
                        c.getInt(c.getColumnIndex(Patient.C_NEW_USER)),
                        EmotionEnum.valueOf(c.getString(c.getColumnIndex(Patient.C_PREVIOUS_EMOTION)))
                ));


            }while(c.moveToNext());
        }
        c.close();

        return patients;
    }

    public Patient getPatient(int patientID){
        Patient patient = null;
        Cursor c = cateDB.query(Patient.TABLE_NAME, null, Patient.C_PATIENT_ID + " = " + patientID, null, null, null, Patient.C_PATIENT_ID +" ASC");
        if(c.moveToFirst()){
                patient = new Patient(c.getInt(c.getColumnIndex(Patient.C_PATIENT_ID)),
                        c.getString(c.getColumnIndex(Patient.C_FIRST_NAME)),
                        c.getString(c.getColumnIndex(Patient.C_LAST_NAME)),
                        c.getString(c.getColumnIndex(Patient.C_BIRTHDAY)),
                        c.getInt(c.getColumnIndex(Patient.C_GENDER)),
                        c.getInt(c.getColumnIndex(Patient.C_SCHOOL_ID)),
                        c.getInt(c.getColumnIndex(Patient.C_HANDEDNESS)),
                        c.getString(c.getColumnIndex(Patient.C_REMARKS_STRING)),
                        c.getBlob(c.getColumnIndex(Patient.C_REMARKS_AUDIO)),
                        c.getInt(c.getColumnIndex(Patient.C_SCORE)),
                        c.getString(c.getColumnIndex(Patient.C_HOBBY)),
                        c.getInt(c.getColumnIndex(Patient.C_NEW_USER)),
                        EmotionEnum.valueOf(c.getString(c.getColumnIndex(Patient.C_PREVIOUS_EMOTION)))
                );
        }
        c.close();

        return patient;
    }

    public Patient getLatestPatient(){
        Patient patient = null;
        Cursor c = cateDB.query(Patient.TABLE_NAME, null, null, null, null, null, Patient.C_PATIENT_ID +" DESC");
        if(c.moveToFirst()){
            patient = new Patient(c.getInt(c.getColumnIndex(Patient.C_PATIENT_ID)),
                    c.getString(c.getColumnIndex(Patient.C_FIRST_NAME)),
                    c.getString(c.getColumnIndex(Patient.C_LAST_NAME)),
                    c.getString(c.getColumnIndex(Patient.C_BIRTHDAY)),
                    c.getInt(c.getColumnIndex(Patient.C_GENDER)),
                    c.getInt(c.getColumnIndex(Patient.C_SCHOOL_ID)),
                    c.getInt(c.getColumnIndex(Patient.C_HANDEDNESS)),
                    c.getString(c.getColumnIndex(Patient.C_REMARKS_STRING)),
                    c.getBlob(c.getColumnIndex(Patient.C_REMARKS_AUDIO)),
                    c.getInt(c.getColumnIndex(Patient.C_SCORE)),
                    c.getString(c.getColumnIndex(Patient.C_HOBBY)),
                    c.getInt(c.getColumnIndex(Patient.C_NEW_USER)),
                    EmotionEnum.valueOf(c.getString(c.getColumnIndex(Patient.C_PREVIOUS_EMOTION)))
            );
        }
        c.close();

        return patient;
    }

    public Memory getMemory(int patientID){

        Memory memory = new Memory();

        Cursor c = cateDB.query(Memory.TABLE_NAME, null, Memory.C_PATIENT + " = " + patientID, null, null, null, Memory.C_ID +" ASC");
        if(c.moveToFirst()){
            memory.setPatient(getPatient(patientID));
            memory.setId(c.getInt(c.getColumnIndex(Memory.C_ID)));

            do{
                memory.getVariables().put(c.getString(c.getColumnIndex(Memory.C_VARIABLE)), c.getString(c.getColumnIndex(Memory.C_VALUE)));
            }while(c.moveToNext());
        }
        c.close();

        return memory;
    }

    public void updateMemory(int patientID, String variable, String value) {
        Cursor c = cateDB.query(Memory.TABLE_NAME, null, Memory.C_PATIENT + " = " + patientID + " AND " + Memory.C_VARIABLE + " = \"" + variable + "\"", null, null, null, null);
        if (c.moveToFirst()) {
            // UPDATE
            ContentValues values = new ContentValues();

            values.put(Memory.C_VALUE, value);

            cateDB.update(Memory.TABLE_NAME, values, Memory.C_PATIENT + " = " + patientID + " AND " + Memory.C_VARIABLE + " = \"" + variable + "\"", null);
        } else {
            // INSERT
            ContentValues values = new ContentValues();

            values.put(Memory.C_VALUE, value);
            values.put(Memory.C_VARIABLE, variable);
            values.put(Memory.C_PATIENT, patientID);

            cateDB.insert(Memory.TABLE_NAME, null, values);

        }
    }

    public Decision getDecision (int id) {
        Decision decision = null;

        Cursor c = cateDB.query(Decision.TABLE_NAME, null, Decision.C_ID + " = " + id, null, null, null, null);
        if (c.moveToFirst()) {
            decision = new Decision(
                    c.getInt(c.getColumnIndex(Decision.C_ID)),
                    c.getString(c.getColumnIndex(Decision.C_CHOICES)),
                    c.getString(c.getColumnIndex(Decision.C_LABEL)),
                    c.getString(c.getColumnIndex(Decision.C_QUESTION)),
                    c.getInt(c.getColumnIndex(Decision.C_INIT)) == 1,
                    c.getString(c.getColumnIndex(Decision.C_SPEECH)),
                    EmotionEnum.valueOf(c.getString(c.getColumnIndex(Decision.C_EMOTION))),
                    c.getInt(c.getColumnIndex(Decision.C_EDITTEXT)) == 1,
                    c.getString(c.getColumnIndex(Decision.C_VARIABLE))
            );
        }

        return decision;
    }

    public Decision getInitialDecision() {
        Decision decision = null;

        Cursor c = cateDB.query(Decision.TABLE_NAME, null, Decision.C_INIT + " = 1", null, null, null, null);
        if (c.moveToFirst()) {
            decision = new Decision(
                    c.getInt(c.getColumnIndex(Decision.C_ID)),
                    c.getString(c.getColumnIndex(Decision.C_CHOICES)),
                    c.getString(c.getColumnIndex(Decision.C_LABEL)),
                    c.getString(c.getColumnIndex(Decision.C_QUESTION)),
                    c.getInt(c.getColumnIndex(Decision.C_INIT)) == 1,
                    c.getString(c.getColumnIndex(Decision.C_SPEECH)),
                    EmotionEnum.valueOf(c.getString(c.getColumnIndex(Decision.C_EMOTION))),
                    c.getInt(c.getColumnIndex(Decision.C_EDITTEXT)) == 1,
                    c.getString(c.getColumnIndex(Decision.C_VARIABLE))
            );
        }

        return decision;
    }

    public boolean updatePatient(Patient patient){
        ContentValues values = new ContentValues();
        int row;

        values.put(Patient.C_FIRST_NAME, patient.getFirstName());
        values.put(Patient.C_LAST_NAME, patient.getLastName());
        values.put(Patient.C_BIRTHDAY, patient.getBirthday());
        values.put(Patient.C_HOBBY, patient.getHobby());
        values.put(Patient.C_GENDER, patient.getGender());
        values.put(Patient.C_SCHOOL_ID, patient.getSchoolId());
        values.put(Patient.C_HANDEDNESS, patient.getHandedness());
        values.put(Patient.C_REMARKS_STRING, patient.getRemarksString());
        values.put(Patient.C_REMARKS_AUDIO, patient.getRemarksAudio());
        values.put(Patient.C_NEW_USER, patient.isNew() ? 1 : 0);
        values.put(Patient.C_SCORE, patient.getScore());
        values.put(Patient.C_PREVIOUS_EMOTION, (patient.previous_emotion != null) ? patient.previous_emotion.toString() : "HAPPY");

        row = (int) cateDB.update(Patient.TABLE_NAME, values, Patient.C_PATIENT_ID + " = " + patient.getPatientID(), null);
        return true;

    }
}
