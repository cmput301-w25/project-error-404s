package com.example.mood_pulse.ui;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.example.mood_pulse.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TestClass {
    private TextView dateTime, dateTv, timeTv, warningText;
    private ImageView happyIcon, sadIcon, angerIcon, fearIcon, disgustIcon, confusedIcon, shameIcon, surprisedIcon, tiredIcon,anxiousIcon, proudIcon, boredIcon;
    private AppCompatButton aloneBTN, wth1PersonBTN, wth2personBTN, crowdBTN;
    private String selectedEmotion = "";
    private String storeInput = "";
    private String socialSituation = "";
    private Date date;


    private Activity activity;
    public TestClass(Activity activity) {
        this.activity = activity;

        happyIcon = activity.findViewById(R.id.happyIcon);
        sadIcon = activity.findViewById(R.id.sadIcon);
        angerIcon = activity.findViewById(R.id.angerIcon);
        fearIcon = activity.findViewById(R.id.fearIcon);
        disgustIcon = activity.findViewById(R.id.disgustIcon);
        confusedIcon = activity.findViewById(R.id.confusedIcon);
        shameIcon = activity.findViewById(R.id.ShapeIcon);
        surprisedIcon = activity.findViewById(R.id.surprisedIcon);
        tiredIcon = activity.findViewById(R.id.tiredIcon);
        anxiousIcon = activity.findViewById(R.id.anxiousIcon);
        proudIcon = activity.findViewById(R.id.proudIcon);
        boredIcon = activity.findViewById(R.id.boredIcon);

        aloneBTN = activity.findViewById(R.id.aloneBtn);
        wth1PersonBTN = activity.findViewById(R.id.with1personBTN);
        wth2personBTN = activity.findViewById(R.id.with2personBTN);
        crowdBTN = activity.findViewById(R.id.crowdBTN);
    }

    public void emotionClickListners(){
        happyIcon.setOnClickListener(v -> { selectedEmotion = "Happy"; });
        sadIcon.setOnClickListener(v -> { selectedEmotion = "Sad"; });
        angerIcon.setOnClickListener(v -> { selectedEmotion = "Angry"; });
        fearIcon.setOnClickListener(v -> { selectedEmotion = "Fear"; });
        disgustIcon.setOnClickListener(v -> { selectedEmotion = "Disgust"; });
        confusedIcon.setOnClickListener(v -> { selectedEmotion = "Confused"; });
        shameIcon.setOnClickListener(v -> { selectedEmotion = "Shame"; });
        surprisedIcon.setOnClickListener(v -> { selectedEmotion = "Surprised"; });
        tiredIcon.setOnClickListener(v -> { selectedEmotion = "Tired"; });
        anxiousIcon.setOnClickListener(v -> { selectedEmotion = "Anxious"; });
        proudIcon.setOnClickListener(v -> { selectedEmotion = "Proud"; });
        boredIcon.setOnClickListener(v -> { selectedEmotion = "Bored"; });
    }

    public void socialSituationListeners(Context con) {
        aloneBTN.setOnClickListener(v -> { socialSituation = "Alone"; Toast.makeText(con, socialSituation, Toast.LENGTH_SHORT).show();});
        wth1PersonBTN.setOnClickListener(v -> { socialSituation = "With 1 other person"; });
        wth2personBTN.setOnClickListener(v -> { socialSituation = "With 2 other people"; });
        crowdBTN.setOnClickListener(v -> { socialSituation = "With a crowd"; });
    }

        public void liveTime(){
            Calendar calendar = Calendar.getInstance();
            date = calendar.getTime();
            // do format
            SimpleDateFormat formatDate = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
            String formattedDate = formatDate.format((date));

            SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String formatedTime = time.format(date);

            SimpleDateFormat wordsFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            String wordsDate = wordsFormat.format(date).toString();

            String leftDate = wordsDate + ", " + formattedDate;

            // show string  dateTv
            dateTv = activity.findViewById(R.id.dateTv);
            dateTv.setText(leftDate);
            // shows the time on the right
            timeTv = activity.findViewById(R.id.timeTv);
            timeTv.setText(formatedTime);
        }

        public void textWatcherNote(EditText writeHereET, Context context) {
            writeHereET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    storeInput = charSequence.toString();
                    boolean printErr = false;

                    if (charSequence.length() > 20) {
                        writeHereET.setText(charSequence.subSequence(0, 20));
                        writeHereET.setSelection(20);
                        if (!printErr) {
                            printErr = true;
                            Toast.makeText(context, selectedEmotion, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        printErr = false;
                        storeInput = charSequence.toString();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        public String getInput() {
        return storeInput;
        }

    public String getEmotion() {
        return selectedEmotion;
    }

    public boolean submitValidation(Context context) {
        if (selectedEmotion.isEmpty()){
            Toast.makeText(context, "Cannot submit with empty emotion", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public String getSocialSituation() {
        return socialSituation;
    }

    public Date getDate() {
        return date;
    }

}
