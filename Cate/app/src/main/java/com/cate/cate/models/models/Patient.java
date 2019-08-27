package com.cate.cate.models.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.cate.cate.fragments.EmotionEnum;

/**
 * The Patient class represents a Patient and implements Parcelable
 * to allow for the object to be passed between activities.
 * The class contains patient information.
 *
 * @author Mary Grace Malana
 * @since 24/03/2016
 */
public class Patient implements Parcelable {

    /**
     * Used to identify the source of a log message
     */
    private static final String TAG = "Patient";

    /**
     * ID of the patient
     */
    private int patientID;

    /**
     * First name of the patient
     */
    private String firstName;

    /**
     * Last name of the patient
     */
    private String lastName;

    /**
     * Birthday of the patient
     */
    private String birthday;

    private String hobby;

    /**
     * Gender of the patient. 0 for MALE, 1 for FEMALE
     */
    private int gender;

    /**
     * ID of the school of the patient
     */
    private int schoolId;
    private boolean isNew;

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    /**
     * Handedness of the patient. 0 for RIGHT, 1 for LEFT (Not Used for Cate)
     */
    private int handedness;

    /**
     * keeps track of the user's previous emotion
     */
    public EmotionEnum previous_emotion;

    private int score;

    /**
     * Remark regarding the patient (Not Used for Cate)
     */
    private String remarksString;

    /**
     * Audio recording of the remark
     * regarding the patient (Not Used for Cate).
     */
    private byte[] remarksAudio;

    /**
     * Database column name for storing {@link #patientID}.
     */
    public final static String C_PATIENT_ID = "patient_id";

    /**
     * Database column name for storing {@link #firstName}.
     */
    public final static String C_FIRST_NAME = "first_name";

    /**
     * Database column name for storing {@link #lastName}.
     */
    public final static String C_LAST_NAME = "last_name";

    /**
     * Database column name for storing {@link #birthday}.
     */
    public final static String C_BIRTHDAY = "birthday";

    public final static String C_HOBBY = "hobby";

    /**
     * Database column name for storing {@link #gender}.
     */
    public final static String C_GENDER = "gender";

    /**
     * Database column name for storing {@link #schoolId}.
     */
    public final static String C_SCHOOL_ID = "school_id";
    public final static String C_PREVIOUS_EMOTION = "previous_emotion";

    /**
     * Database column name for storing {@link #handedness} (Not Used for Cate).
     */
    public final static String C_HANDEDNESS = "handedness";

    public final static String C_SCORE = "score";

    public final static String C_NEW_USER = "new_user";

    /**
     * Database column name for storing {@link #remarksString} (Not Used for Cate).
     */
    public final static String C_REMARKS_STRING = "remarks_string";

    /**
     * Database column name for storing {@link #remarksAudio} (Not Used for Cate).
     */
    public final static String C_REMARKS_AUDIO = "remarks_audio";

    public final static String C_PHONE = "phone";

    /**
     * Database table name for patient.
     */
    public final static String TABLE_NAME = "tbl_patient";

    /**
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays
     */
    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Patient createFromParcel(Parcel in) {
                    return new Patient(in);
                }

                public Patient[] newArray(int size) {
                    return new Patient[size];
                }
            };


    /**
     * Constructor. Patient constructor used after getting
     * the Patient info from the database.
     *  @param patientID {@link #patientID}
     * @param firstName {@link #firstName}
     * @param lastName {@link #lastName}
     * @param birthday {@link #birthday}
     * @param gender {@link #gender}
     * @param schoolId {@link #schoolId}(Not Used for Cate)
     * @param handedness {@link #handedness}(Not Used for Cate)
     * @param remarksString {@link #remarksString}(Not Used for Cate)
     * @param remarksAudio {@link #remarksAudio}(Not Used for Cate)
     * @param score
     * @param hobby
     * @param isNew
     */
    public Patient(int patientID, String firstName, String lastName, String birthday, int gender, int schoolId, int handedness, String remarksString, byte[] remarksAudio, int score, String hobby, int isNew, EmotionEnum previous_emotion) {
        this.patientID = patientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.gender = gender;
        this.schoolId = schoolId;
        this.handedness = handedness;
        this.remarksString = remarksString;
        this.remarksAudio = remarksAudio;
        this.previous_emotion = previous_emotion;

        this.score = score;
        this.hobby = hobby;
        this.isNew = isNew == 1;
    }

    /**
     * Constructor. Patient constructor used for inserting the HPI
     * to the database.
     *
     * @param firstName {@link #firstName}
     * @param lastName {@link #lastName}
     * @param birthday {@link #birthday}
     * @param gender {@link #gender}
     * @param schoolId {@link #schoolId}
     * @param handedness {@link #handedness}(Not Used for Cate)
     */
    public Patient(String firstName, String lastName, String birthday, int gender, int schoolId, int handedness, int score, String hobby, int isNew, EmotionEnum previous_emotion) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.gender = gender;
        this.schoolId = schoolId;
        this.handedness = handedness;
        this.score = score;
        this.hobby = hobby;
        this.isNew = isNew == 1;
        this.previous_emotion = previous_emotion;
    }

    /**
     * Constructor.
     * @param in parcel to be read.
     */
    public Patient(Parcel in){
        readFromParcel(in);
    }

    /**
     * @see Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    public EmotionEnum getPrevious_emotion() {
        return previous_emotion;
    }

    public void setPrevious_emotion(EmotionEnum previous_emotion) {
        this.previous_emotion = previous_emotion;
    }

    /**
     * Writes each field into the parcel.
     * @see Parcelable#writeToParcel(Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(patientID);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(birthday);
        dest.writeInt(gender);
        dest.writeInt(schoolId);
        dest.writeInt(handedness);
        dest.writeInt(score);
        dest.writeString(hobby);
        dest.writeString(remarksString);
        dest.writeByteArray(remarksAudio);
        dest.writeInt(isNew ? 1 : 0);
        dest.writeString(previous_emotion.name());
    }

    /**
     * Reads back each field in the order that it was written to the parcel.
     * @param in parcel to be read
     */
    public void readFromParcel(Parcel in){
        patientID = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        birthday = in.readString();
        gender = in.readInt();
        schoolId = in.readInt();
        handedness = in.readInt();
        score = in.readInt();
        hobby = in.readString();
        remarksString = in.readString();
        remarksAudio = in.createByteArray();
        isNew = in.readInt() == 1;
        previous_emotion = EmotionEnum.valueOf(in.readString());
    }

    /**
     * Gets {@link #patientID}.
     * @return {@link #patientID}
     */
    public int getPatientID() {
        return patientID;
    }

    /**
     * Gets {@link #firstName}.
     * @return {@link #firstName}
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets {@link #lastName}.
     * @return {@link #lastName}
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets {@link #birthday}.
     * @return {@link #birthday}
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * Gets {@link #gender}.
     * @return {@link #gender}
     */
    public int getGender() {
        return gender;
    }

    /**
     * Gets {@link #schoolId}.
     * @return {@link #schoolId}
     */
    public int getSchoolId() {
        return schoolId;
    }

    /**
     * Gets {@link #handedness}.
     * @return {@link #handedness}(Not Used for Cate)
     */
    public int getHandedness() {
        return handedness;
    }


    public int getScore() {
        return score;
    }

    public String getHobby() {
        return hobby;
    }

    /**
     * Gets string value of {@link #gender}.
     * @return string value of {@link #gender}
     */
    public String getGenderString(){
        if(gender == 0){
            return "Male";
        } else{
            return "Female";
        }
    }

    /**
     * Gets string value of {@link #handedness}.
     * @return string value of {@link #handedness}(Not Used for Cate)
     */
    public String getHandednessString(){
        if(handedness == 0){
            return "Right-Handed";
        }else{
            return "Left-Handed";
        }
    }


    /**
     * Gets {@link #remarksString}.
     * @return {@link #remarksString}(Not Used for Cate)
     */
    public String getRemarksString() {
        return remarksString;
    }

    /**
     * Gets {@link #remarksAudio}.
     * @return {@link #remarksAudio}(Not Used for Cate)
     */
    public byte[] getRemarksAudio() {
        return remarksAudio;
    }

    /**
     * Sets {@link #remarksString}
     * @param remarksString new value(Not Used for Cate)
     */
    public void setRemarksString(String remarksString) {
        this.remarksString = remarksString;
    }

    /**
     * Sets {@link #remarksAudio}
     * @param remarksAudio new value(Not Used for Cate)
     */
    public void setRemarksAudio(byte[] remarksAudio) {
        this.remarksAudio = remarksAudio;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Print about the {@link Patient} attributes and
     * corresponding value.
     * @return Patient info string
     */
    public void printPatient(){
        Log.d(TAG, "patientID: " + patientID + ", firstName: " + firstName +
                ", lastName: " + lastName + ", birthday: " + birthday +
                ", gender: " + gender + ", schoolId: " + schoolId +
                ", handedness: " + handedness +
                ", remarkString: " + remarksString +
                ", remarksAudio: " + remarksAudio);
    }
}
