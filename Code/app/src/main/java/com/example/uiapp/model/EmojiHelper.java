package com.example.uiapp.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.example.uiapp.R;

public class EmojiHelper {

    /**
     * Returns the appropriate drawable for a given mood name.
     *
     * @param context Application context
     * @param moodName Name of the mood
     * @return Drawable resource for the mood
     */
    public static Drawable getMoodDrawable(Context context, String moodName) {
        switch (moodName.toLowerCase()) {
            case "happy":
                return ContextCompat.getDrawable(context, R.drawable.happy);
            case "sad":
                return ContextCompat.getDrawable(context, R.drawable.sad_emoji);
            case "fear":
                return ContextCompat.getDrawable(context, R.drawable.fear);
            case "disgust":
                return ContextCompat.getDrawable(context, R.drawable.disgust);
            case "anger":
                return ContextCompat.getDrawable(context, R.drawable.anger);
            case "confused":
                return ContextCompat.getDrawable(context, R.drawable.confused);
            case "shame":
                return ContextCompat.getDrawable(context, R.drawable.shame);
            case "surprised":
                return ContextCompat.getDrawable(context, R.drawable.surprised);
            case "tired":
                return ContextCompat.getDrawable(context, R.drawable.tired);
            case "anxious":
                return ContextCompat.getDrawable(context, R.drawable.anxious);
            case "proud":
                return ContextCompat.getDrawable(context, R.drawable.proud);
            default:
                return ContextCompat.getDrawable(context, R.drawable.happy); // Fallback emoji
        }
    }
}
