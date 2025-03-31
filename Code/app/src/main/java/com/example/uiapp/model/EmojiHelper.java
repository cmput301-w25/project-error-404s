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
                return ContextCompat.getDrawable(context, R.drawable.disgust_emoji);
            case "disgust":
                return ContextCompat.getDrawable(context, R.drawable.image_4);
            case "anger":
                return ContextCompat.getDrawable(context, R.drawable.image_5);
            case "confused":
                return ContextCompat.getDrawable(context, R.drawable.image_6);
            case "shame":
                return ContextCompat.getDrawable(context, R.drawable.image_7);
            case "surprised":
                return ContextCompat.getDrawable(context, R.drawable.image_10);
            case "tired":
                return ContextCompat.getDrawable(context, R.drawable.image_11);
            case "anxious":
                return ContextCompat.getDrawable(context, R.drawable.image_12);
            case "proud":
                return ContextCompat.getDrawable(context, R.drawable.image_9);
            default:
                return ContextCompat.getDrawable(context, R.drawable.happy); // Fallback emoji
        }
    }
}
