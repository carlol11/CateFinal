package com.cate.cate.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Random;

import com.cate.cate.R;
import com.cate.cate.adapters.CallAdapter;
import com.cate.cate.database.DatabaseAdapter;
import com.cate.cate.fragments.EmotionEnum;
import com.cate.cate.interfaces.ECAActivity;

import com.cate.cate.models.models.Decision;
import com.cate.cate.models.models.Patient;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DecisionsActivity extends ECAActivity {

    // All Variables here
    private boolean hasSpoken;
    Button button1,button2,button3,yesQuit,goBack,notUser;
    TextView txt, quitText;
    public int DECISION_BRANCH = 1;
    public ArrayList<Decision> bound_btn_decision;
    Decision current_decision, previous_decision;
    DatabaseAdapter getBetterDb;
    Patient patient;
    EditText editText;
    ListView listView;
    ArrayList<String> names;
    ArrayList<String> phones;
    ArrayList<Integer> random_decisions;
    ImageView imageView;
    ImageView exitButton;
    FrameLayout placeholderECA;
    int[] cats= new int[]{R.drawable.cat1, R.drawable.cat2, R.drawable.cat3, R.drawable.cat5};
    int[] dogs= new int[]{R.drawable.dog1, R.drawable.dog2, R.drawable.dog3 ,R.drawable.dog4 ,R.drawable.dog5};
    Random r = new Random();
    int[] doing_message = new int[]{20, 74, 78, 83};
    int[] doing_speech = new int[]{20, 22, 74, 83};
    HashMap<String, String> memory = new HashMap<>();
    int randomNumber;
    public static final String PREFS_NAME = "CateSharedPref";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int run = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.happy_activity);

        //All Button Assignments

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        yesQuit = (Button) findViewById(R.id.yesQuit);
        goBack = (Button) findViewById(R.id.goBack);
        notUser = (Button) findViewById(R.id.notUser);
        listView = (ListView) findViewById(R.id.listview);
        imageView = (ImageView) findViewById(R.id.imageView);
        exitButton = (ImageView) findViewById(R.id.exitButton);
        editText = (EditText) findViewById(R.id.editText);
        placeholderECA = (FrameLayout) findViewById(R.id.placeholderECA);
        Typeface quicksand = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.ttf");
        txt = (TextView) findViewById(R.id.messageCate);
        quitText = (TextView) findViewById(R.id.quitCate);
        button1.setTypeface(quicksand);
        button2.setTypeface(quicksand);
        button3.setTypeface(quicksand);
        txt.setTypeface(quicksand);
        quitText.setTypeface(quicksand);
        yesQuit.setTypeface(quicksand);
        goBack.setTypeface(quicksand);
        notUser.setTypeface(quicksand);
        integrateECA();

        yesQuit.setVisibility(View.INVISIBLE);
        goBack.setVisibility(View.INVISIBLE);
        quitText.setVisibility(View.INVISIBLE);
        notUser.setVisibility(View.INVISIBLE);

        exitButton.setImageResource(R.drawable.exitnew);
        names = new ArrayList<String>();
        phones = new ArrayList<String>();
        random_decisions = new ArrayList<Integer>();

        random_decisions.addAll(Arrays.asList(302, 590, 591, 400, 419, 459, 493, 519, 604));
        randomNumber = r.nextInt(random_decisions.size());

        // Opening the Database
        getBetterDb = new DatabaseAdapter(DecisionsActivity.this);
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Getting the Patient

        patient = getIntent().getParcelableExtra("patient");
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //To check if there is a previous patient using the application
        if(patient != null){
            editor.putInt("patientID", patient.getPatientID());
            editor.commit();
        }

        //To get the patient in the SharedPreferences if there is no patient.
        if(patient == null){
            patient = getBetterDb.getPatient(sharedPreferences.getInt("patientID", 0));
        }

        getBetterDb.updatePatient(patient);
        memory = getBetterDb.getMemory(patient.getPatientID()).getVariables();
        DECISION_BRANCH = getIntent().getIntExtra("branch", 1);
        bound_btn_decision = new ArrayList<>();

        //If Patient is new then go to initial conversations
        if(patient.isNew()) {
            current_decision = getBetterDb.getDecision(DECISION_BRANCH);
            previous_decision = current_decision;
        }
        //Else Cate will generate a random topic
        else {
            current_decision = getBetterDb.getDecision(random_decisions.get(randomNumber));
            random_decisions.remove(randomNumber);
            previous_decision = current_decision;
        }

        //Depends on the user's previous emotion. See CSV File

        if (current_decision.getId() == 600 && !random_decisions.isEmpty()){
            randomNumber = r.nextInt(random_decisions.size());
            current_decision = getBetterDb.getDecision(random_decisions.get(randomNumber));
            random_decisions.remove(randomNumber);
            if(current_decision.getId() == 302 && patient.previous_emotion != EmotionEnum.HAPPY) {
                randomNumber = r.nextInt(random_decisions.size());
                current_decision = getBetterDb.getDecision(random_decisions.get(randomNumber));
                random_decisions.remove(randomNumber);
            }
            if(current_decision.getId() == 590 && patient.previous_emotion != EmotionEnum.WORRIED){
                randomNumber = r.nextInt(random_decisions.size());
                current_decision = getBetterDb.getDecision(random_decisions.get(randomNumber));
                random_decisions.remove(randomNumber);
            }
            if(current_decision.getId() == 591 && patient.previous_emotion != EmotionEnum.CONCERNED){
                randomNumber = r.nextInt(random_decisions.size());
                current_decision = getBetterDb.getDecision(random_decisions.get(randomNumber));
                random_decisions.remove(randomNumber);
            }
        }
        // To Change the question
        changeQuestion();
        // To Get all contacts from the Mobile Phone
        getContacts();

        // If User pressed the exit button

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txt.getVisibility() == View.VISIBLE) {
                   if (bound_btn_decision.size() == 3) {
                       button1.setVisibility(View.INVISIBLE);
                       button2.setVisibility(View.INVISIBLE);
                       button3.setVisibility(View.INVISIBLE);
                   } else if (bound_btn_decision.size() == 2) {
                       button1.setVisibility(View.INVISIBLE);
                       button2.setVisibility(View.INVISIBLE);
                   } else
                       button1.setVisibility(View.INVISIBLE);

                   txt.setVisibility(View.INVISIBLE);
                   yesQuit.setVisibility(View.VISIBLE);
                   goBack.setVisibility(View.VISIBLE);
                   quitText.setVisibility(View.VISIBLE);
                   notUser.setVisibility(View.VISIBLE);

                   ecaFragment.sendToECAToSpeak("Are you sure you want to stop our conversation?", EmotionEnum.WORRIED);
               }else{
                   if(bound_btn_decision.size() == 3) {
                       button1.setVisibility(View.VISIBLE);
                       button2.setVisibility(View.VISIBLE);
                       button3.setVisibility(View.VISIBLE);
                   }else if(bound_btn_decision.size() == 2){
                       button1.setVisibility(View.VISIBLE);
                       button2.setVisibility(View.VISIBLE);
                   }
                   else
                       button1.setVisibility(View.VISIBLE);

                   txt.setVisibility(View.VISIBLE);
                   yesQuit.setVisibility(View.INVISIBLE);
                   goBack.setVisibility(View.INVISIBLE);
                   quitText.setVisibility(View.INVISIBLE);
                   notUser.setVisibility(View.INVISIBLE);
                   ecaFragment.sendToECAToSpeak(current_decision.getSpeech());
               }
            }
        });
        // User pressed Yes on stopping the conversation

        yesQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bound_btn_decision.size() == 3) {
                    button1.setVisibility(View.VISIBLE);
                    button2.setVisibility(View.VISIBLE);
                    button3.setVisibility(View.VISIBLE);
                }else if(bound_btn_decision.size() == 2){
                    button1.setVisibility(View.VISIBLE);
                    button2.setVisibility(View.VISIBLE);
                }
                else
                    button1.setVisibility(View.VISIBLE);

                txt.setVisibility(View.VISIBLE);
                yesQuit.setVisibility(View.INVISIBLE);
                goBack.setVisibility(View.INVISIBLE);
                quitText.setVisibility(View.INVISIBLE);
                notUser.setVisibility(View.INVISIBLE);
                current_decision = getBetterDb.getDecision(129);
                changeQuestion();
            }
        });
        //Goes back to the last question flashed
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bound_btn_decision.size() == 3) {
                    button1.setVisibility(View.VISIBLE);
                    button2.setVisibility(View.VISIBLE);
                    button3.setVisibility(View.VISIBLE);
                }else if(bound_btn_decision.size() == 2){
                    button1.setVisibility(View.VISIBLE);
                    button2.setVisibility(View.VISIBLE);
                }
                else
                    button1.setVisibility(View.VISIBLE);

                txt.setVisibility(View.VISIBLE);
                yesQuit.setVisibility(View.INVISIBLE);
                goBack.setVisibility(View.INVISIBLE);
                quitText.setVisibility(View.INVISIBLE);
                //added this ^
                notUser.setVisibility(View.INVISIBLE);
                ecaFragment.sendToECAToSpeak(current_decision.getSpeech());
            }
        });
        //Goes back to the patient lists when pressed.

        notUser.setText("Sorry but I'm not "+ patient.getFirstName());
        notUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                quitText.setText("Oh, I'm sorry. Let me take you back");
                ecaFragment.sendToECAToSpeak("Oh, I'm sorry. Let me take you back");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(DecisionsActivity.this, PatientListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 5000);

            }
        });



        // Instantiating the list of Phone numbers

        CallAdapter cAdapter = new CallAdapter(this, names, phones);
        listView.setAdapter(cAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: Call upon item click
                Toast.makeText(DecisionsActivity.this, "Calling: " + names.get(position), Toast.LENGTH_SHORT).show();
                String dial = "tel:" + phones.get(position);
                ecaFragment.sendToECAToSpeak("Calling, " + names.get(position));
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        });
        //Serves as the First button from the top.

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_decision.getEdditext() && editText.getVisibility() == View.VISIBLE) {
                    memory.put(current_decision.getVariable(), editText.getText().toString());
                    getBetterDb.updateMemory(patient.getPatientID(), current_decision.getVariable(), editText.getText().toString());
                }

                Decision decision = (bound_btn_decision.size() >= 1) ? bound_btn_decision.get(0) : null;

                if (decision != null) {
                    // do update patient point
                    // logic for other functionalities, check for id
                    current_decision = Decision.shallowCopy(decision);

                    //To check if it is an edit text
                    if(editText.getVisibility() == View.INVISIBLE)
                        save(button1.getText().toString() + "\n");
                    else
                        save(editText.getText().toString());
                    changeQuestion();
                }

            }
        });
        //Serves as the second button
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Decision decision = (bound_btn_decision.size() >= 2) ? bound_btn_decision.get(1) : null;

                if (decision != null) {
                    // do update patient point

                    current_decision = Decision.shallowCopy(decision);
                    save(button2.getText().toString() + "\n");

                    changeQuestion();
                }

            }
        });
        //Serves as the third button
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Decision decision = (bound_btn_decision.size() >= 3) ? bound_btn_decision.get(2) : null;

                if (decision != null) {
                    // do update patient point

                    current_decision = Decision.shallowCopy(decision);
                    save(button3.getText().toString() + "\n");

                    changeQuestion();
                }

            }
        });
    }
    //Changes the buttons, questions, and speech of Cate.

    public void changeQuestion() {
        bound_btn_decision.clear();
        editText.getText().clear();

        boolean specific;
        boolean isIng;

        //To change the layout if edittext is true

        if(current_decision.getEdditext()){
            editText.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) button1.getLayoutParams();
            params.removeRule(RelativeLayout.BELOW);
            params.addRule(RelativeLayout.BELOW, R.id.editText);
        }
        else {
            editText.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) button1.getLayoutParams();
            params.removeRule(RelativeLayout.BELOW);
            params.addRule(RelativeLayout.BELOW, R.id.messageCate);
        }

        //Determine previous
        if(current_decision.getId() == 2)
            patient.previous_emotion = EmotionEnum.HAPPY;
        if(current_decision.getId() == 3)
            patient.previous_emotion = EmotionEnum.WORRIED;
        if(current_decision.getId() == 4)
            patient.previous_emotion = EmotionEnum.CONCERNED;


        //Randomness

        if(random_decisions.isEmpty()){
            random_decisions.addAll(Arrays.asList(302, 590, 591, 400, 419, 493));
        }

        if (current_decision.getId() == 600 && !random_decisions.isEmpty()){
            randomNumber = r.nextInt(random_decisions.size());
            current_decision = getBetterDb.getDecision(random_decisions.get(randomNumber));
            random_decisions.remove(randomNumber);
            if(current_decision.getId() == 302 && patient.previous_emotion != EmotionEnum.HAPPY) {
                randomNumber = r.nextInt(random_decisions.size());
                current_decision = getBetterDb.getDecision(random_decisions.get(randomNumber));
                random_decisions.remove(randomNumber);
            }
            if(current_decision.getId() == 590 && patient.previous_emotion != EmotionEnum.WORRIED){
                randomNumber = r.nextInt(random_decisions.size());
                current_decision = getBetterDb.getDecision(random_decisions.get(randomNumber));
                random_decisions.remove(randomNumber);
            }
            if(current_decision.getId() == 591 && patient.previous_emotion != EmotionEnum.CONCERNED){
                randomNumber = r.nextInt(random_decisions.size());
                current_decision = getBetterDb.getDecision(random_decisions.get(randomNumber));
                random_decisions.remove(randomNumber);
            }
        }


        //Add Reminder

        if(current_decision.getId() == 111 || current_decision.getId() == 508 || current_decision.getId() == 615)
            addEventToCalendar();

        //Making a call

        if (current_decision.getId() == 110 || current_decision.getId() == 452 || current_decision.getId() == 616) {
            listView.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) button1.getLayoutParams();
            params.removeRule(RelativeLayout.BELOW);
            params.addRule(RelativeLayout.BELOW, R.id.listview);
        }else if(!current_decision.getEdditext()){
            listView.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) button1.getLayoutParams();
            params.removeRule(RelativeLayout.BELOW);
            params.addRule(RelativeLayout.BELOW, R.id.messageCate);
        }

        //Picture Game/Cats/Dogs

        if(current_decision.getId() == 378){
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.malacanang);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) placeholderECA.getLayoutParams();
            params.height = 360;
            params.width = 360;
        }else if(current_decision.getId() == 381){
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.manilacityhall);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) placeholderECA.getLayoutParams();
            params.height = 360;
            params.width = 360;
        }else if(current_decision.getId() == 385){
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.aranetanew);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) placeholderECA.getLayoutParams();
            params.height = 360;
            params.width = 360;
        }else if(current_decision.getId() == 388){
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.luneta);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) placeholderECA.getLayoutParams();
            params.height = 360;
            params.width = 360;
        }else if(current_decision.getId() == 392){
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.manilacathedral);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) placeholderECA.getLayoutParams();
            params.height = 360;
            params.width = 360;

        }else if(current_decision.getId() == 532){
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.monumento);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) placeholderECA.getLayoutParams();
            params.height = 360;
            params.width = 360;

        }else if(current_decision.getId() == 536){
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.pinatubo);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) placeholderECA.getLayoutParams();
            params.height = 360;
            params.width = 360;
        }else if(current_decision.getId() == 543){
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.jonesbridge);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) placeholderECA.getLayoutParams();
            params.height = 360;
            params.width = 360;
        }else if(current_decision.getId() == 547){
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.quiapo);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) placeholderECA.getLayoutParams();
            params.height = 360;
            params.width = 360;
        }else if(current_decision.getId() == 551){
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ust);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) placeholderECA.getLayoutParams();
            params.height = 360;
            params.width = 360;
        }else if(current_decision.getId() == 524 || current_decision.getId() == 526){
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(cats[r.nextInt(cats.length)]);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) placeholderECA.getLayoutParams();
            params.height = 360;
            params.width = 360;
        }else if(current_decision.getId() == 525 || current_decision.getId() == 527) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(dogs[r.nextInt(dogs.length)]);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) placeholderECA.getLayoutParams();
            params.height = 360;
            params.width = 360;
        }else if(!current_decision.getEdditext()){
            imageView.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) placeholderECA.getLayoutParams();
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.width = 880;
        }

        //For memory, changes the labels inside the <?> to what the user stated or typed from the previous questions.

        for (Map.Entry<String, String> e : memory.entrySet()) {
            if (e.getKey().equalsIgnoreCase("<activity>")) {
                isIng = e.getValue().toLowerCase().lastIndexOf("ing") >= e.getValue().length() - 3;
                specific = false;
                for (int id : doing_message)
                    if (id == current_decision.getId()) { // change message to doing <activity>
                        current_decision.setQuestion(current_decision.getQuestion().replaceAll("(?i)<activity>", ((isIng) ? "" : "doing ") + e.getValue()));
                        specific = true;
                        break;
                    }

                for (int id : doing_speech)
                    if (id == current_decision.getId()) { // change message to doing <activity>
                        current_decision.setSpeech(current_decision.getSpeech().replaceAll("(?i)<activity>", ((isIng) ? "" : "doing ") + e.getValue()));
                        specific = true;
                        break;
                    }

                if (!specific) {
                    current_decision.setQuestion(current_decision.getQuestion().replaceAll("(?i)<activity>", e.getValue()));
                    current_decision.setSpeech(current_decision.getSpeech().replaceAll("(?i)<activity>",  e.getValue()));
                }
            } else {
                current_decision.setSpeech(current_decision.getSpeech().replaceAll("(?i)" + e.getKey(), e.getValue()));
                current_decision.setQuestion(current_decision.getQuestion().replaceAll("(?i)" + e.getKey(), e.getValue()));
            }
        }

        if (!memory.containsKey("<activity>")) {
            current_decision.setQuestion(current_decision.getQuestion().replaceAll("(?i)<activity>", patient.getHobby()));
            current_decision.setSpeech(current_decision.getSpeech().replaceAll("(?i)<activity>",  patient.getHobby()));
        }

        current_decision.setSpeech(current_decision.getSpeech().replaceAll("(?i)<Name>", patient.getFirstName()));
        current_decision.setQuestion(current_decision.getQuestion().replaceAll("(?i)<Name>", patient.getFirstName()));

            if(patient.previous_emotion == EmotionEnum.HAPPY) {
                current_decision.setSpeech(current_decision.getSpeech().replaceAll("(?i)<emotion>", "happy"));
                current_decision.setQuestion(current_decision.getQuestion().replaceAll("(?i)<emotion>", "happy"));
            }
            if(patient.previous_emotion == EmotionEnum.WORRIED){
                current_decision.setSpeech(current_decision.getSpeech().replaceAll("(?i)<emotion>", "sad"));
                current_decision.setQuestion(current_decision.getQuestion().replaceAll("(?i)<emotion>", "sad"));
            }
            if(patient.previous_emotion == EmotionEnum.CONCERNED){
                current_decision.setSpeech(current_decision.getSpeech().replaceAll("(?i)<emotion>", "angry"));
                current_decision.setQuestion(current_decision.getQuestion().replaceAll("(?i)<emotion>", "angry"));
            }


            txt.setText(current_decision.getQuestion());
            ecaFragment.sendToECAToSpeak(current_decision.getSpeech(), current_decision.getEmotion());

        // Changes the button choices
        if (current_decision.getChoices().size() >= 1 && !current_decision.getChoices().get(0).equals("0")) {
            for (String s_id : current_decision.getChoices())
                bound_btn_decision.add(getBetterDb.getDecision(Integer.parseInt(s_id)));

            switch (bound_btn_decision.size()) {
                case 1:
                    button1.setText(bound_btn_decision.get(0).getLabel());
                    button2.setVisibility(View.INVISIBLE);
                    button3.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    button1.setText(bound_btn_decision.get(0).getLabel());
                    button2.setText(bound_btn_decision.get(1).getLabel());
                    button2.setVisibility(View.VISIBLE);
                    button3.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    button1.setText(bound_btn_decision.get(0).getLabel());
                    button2.setText(bound_btn_decision.get(1).getLabel());
                    button3.setText(bound_btn_decision.get(2).getLabel());
                    button2.setVisibility(View.VISIBLE);
                    button3.setVisibility(View.VISIBLE);
                    break;
            }
            previous_decision = current_decision;
            }
            else{
            // Goes back to the main page.
                patient.setNew(false);
                getBetterDb.updatePatient(patient);;
                getBetterDb.closeDatabase();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(DecisionsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 4000);

        }
    }
    //Function to open and add from the calendar
    private void addEventToCalendar() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, "Call Family/Friend");
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis());
        intent.putExtra(CalendarContract.Events.ALL_DAY, true);

        startActivity(intent);
    }
    // Gets all contacts from the phonebook
    private void getContacts(){

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        names.add(name);
                        phones.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }

    }
    //This serves as the logs of the user per button choice. (See the folder "Elderly Logs" to view)
    public void save(String choice){
        String question;
        question = txt.getText().toString()+ "\n";

        try {
            File path = Environment.getExternalStorageDirectory();

            File dir = new File(path + "/Elderly Logs/");
            dir.mkdirs();

            String fileName = patient.getFirstName() + " " + patient.getLastName() + ".txt";

            File file = new File(dir, fileName);

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("( " + Calendar.getInstance().getTime().toString() + " )" + '\n');
            bw.write("Question: " + question);
            bw.write("Answer: " + choice + "\n");
            bw.close();

        }catch (Exception e){

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (!hasSpoken) {
                //Welcome message
                hasSpoken = true;
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hasSpoken", hasSpoken);

      //  outState.putParcelable("patient", patient);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

     //   patient = savedInstanceState.getParcelable("patient");

    }

    @Override
    public void onBackPressed(){
        ecaFragment.sendToECAToSpeak("Are you sure you want to stop our conversation?", EmotionEnum.WORRIED);
        if(bound_btn_decision.size() == 3) {
            button1.setVisibility(View.INVISIBLE);
            button2.setVisibility(View.INVISIBLE);
            button3.setVisibility(View.INVISIBLE);
        }else if(bound_btn_decision.size() == 2){
            button1.setVisibility(View.INVISIBLE);
            button2.setVisibility(View.INVISIBLE);
        }
        else
            button1.setVisibility(View.INVISIBLE);

        txt.setVisibility(View.INVISIBLE);
        yesQuit.setVisibility(View.VISIBLE);
        goBack.setVisibility(View.VISIBLE);
        quitText.setVisibility(View.VISIBLE);
        notUser.setVisibility(View.VISIBLE);
    }

}
