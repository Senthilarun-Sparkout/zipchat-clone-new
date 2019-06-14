package com.chat.zipchat.clone.CustomText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

@SuppressLint("AppCompatCustomView")
public class CustomBoldEditView extends AppCompatEditText {

    public CustomBoldEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomBoldEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomBoldEditView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-SemiBold.ttf");
        setTypeface(tf, 1);

    }
}