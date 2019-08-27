package com.cate.cate.models.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cate.cate.fragments.EmotionEnum;

import java.util.ArrayList;
import java.util.Arrays;

public class Decision implements Parcelable {

    private static final String TAG = "Decision";

    /**
     * ID of the patient
     */
    private int id;

    /**
     * First name of the patient
     */
    private ArrayList<String> choices=new ArrayList<String>();

    /**
     * Last name of the patient
     */
    private String label;

    private String question;

    private String speech;
    private EmotionEnum emotion;

    private Boolean init = false;
    private Boolean edditext = false;
    private String variable = "";

    public final static String C_ID = "id";
    public final static String C_CHOICES = "choices";
    public final static String C_LABEL = "label";
    public final static String C_QUESTION = "question";
    public final static String C_INIT = "init";
    public final static String C_SPEECH = "speech";
    public final static String C_EMOTION = "emotion";
    public final static String C_EDITTEXT = "edittext";
    public final static String C_VARIABLE = "variable";

    public final static String TABLE_NAME = "decisions";

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
    public Decision(Parcel in){
        readFromParcel(in);
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
        dest.writeStringList(choices);
        dest.writeString(label);
        dest.writeString(question);
        dest.writeInt(init ? 1 : 0);
        dest.writeString(speech);
        dest.writeString(emotion.toString());
        dest.writeInt(edditext ? 1 : 0);
        dest.writeString(variable);
    }

    /**
     * Reads back each field in the order that it was written to the parcel.
     * @param in parcel to be read
     */
    public void readFromParcel(Parcel in){
        id = in.readInt();
        in.readStringList(choices);
        label = in.readString();
        question = in.readString();
        init = in.readInt() == 1;
        speech = in.readString();
        emotion = EmotionEnum.valueOf(in.readString());
        edditext = in.readInt() == 1;
        variable = in.readString();
    }


    public Decision(int id, String choices, String label, String question, boolean init, String speech, EmotionEnum emotion, boolean edditext, String variable) {
        this.id = id;
        this.label = label;
        this.question = question;
        this.init = init;
        this.speech = speech;
        this.choices = new ArrayList<>();
        this.emotion = emotion;
        this.edditext = edditext;
        this.variable = variable;

        this.choices.addAll(Arrays.asList(choices.split(",")));
    }

    public static Decision shallowCopy(Decision decision) {
        StringBuilder choices = new StringBuilder();
        for(int i = 0; i < decision.choices.size(); i++) {
            choices.append(decision.choices.get(i));
            if (i < decision.choices.size() - 1)
                choices.append(",");
        }

        return new Decision(
                decision.id, choices.toString(), decision.label, decision.question, decision.init, decision.speech,
                decision.emotion, decision.edditext, decision.variable
        );
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<String> choices) {
        this.choices = choices;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Boolean getInit() {
        return init;
    }

    public void setInit(Boolean init) {
        this.init = init;
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }

    public EmotionEnum getEmotion() {
        return emotion;
    }

    public void setEmotion(EmotionEnum emotion) {
        this.emotion = emotion;
    }

    public Boolean getEdditext() {
        return edditext;
    }

    public void setEdditext(Boolean edditext) {
        this.edditext = edditext;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }
}
