package com.aashdit.ipms.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aashdit.ipms.R;

/**
 * Created by Manabendu on 17/06/20
 */
@SuppressLint("AppCompatCustomView")
public class MonSemiBold extends TextView {
    public MonSemiBold(Context context) {
        super(context);
    }

    public MonSemiBold(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context,attrs);
    }

    public MonSemiBold(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context,attrs);
    }

    public MonSemiBold(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setCustomFont(context,attrs);
    }

    private void setCustomFont(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MonSemiBold);
        String customFont = a.getString(R.styleable.MonSemiBold_MonSemiBoldCustom);
        setCustomFont(context, customFont);
        a.recycle();
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean setCustomFont(Context ctx, String asset) {
        try {
            Typeface tf;
            tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/Montserrat-SemiBold.ttf");

            if (tf != null) {
                setTypeface(tf);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("", "Could not get typeface: " + e.getMessage());
            return false;
        }

        return true;
    }
}
