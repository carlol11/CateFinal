package com.cate.cate.models.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Memory implements Parcelable {
    private int id;
    private Patient patient;
    private HashMap<String, String> variables;

    public static final String C_ID = "id";
    public static final String C_PATIENT = "patient";
    public static final String C_VARIABLE = "variable";
    public static final String C_VALUE = "value";

    public static final String TABLE_NAME = "Memory";

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Decision createFromParcel(Parcel in) {
                    return new Decision(in);
                }

                public Decision[] newArray(int size) {
                    return new Decision[size];
                }
            };


    /**
     * Constructor.
     * @param in parcel to be read.
     */
    public Memory(Parcel in){
        readFromParcel(in);
    }

    public Memory() {
        this.variables = new HashMap<>();
    }

    /**
     * @see Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes each field into the parcel.
     * @see Parcelable#writeToParcel(Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(patient, 0);
        dest.writeMap(variables);
    }

    /**
     * Reads back each field in the order that it was written to the parcel.
     * @param in parcel to be read
     */
    public void readFromParcel(Parcel in){
        id = in.readInt();
        patient = in.readParcelable(Patient.class.getClassLoader());
        in.readMap(variables, (new HashMap<String, String>()).getClass().getClassLoader());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public HashMap<String, String> getVariables() {
        return variables;
    }

    public void setVariables(HashMap<String, String> variables) {
        this.variables = variables;
    }
}
