package com.chat.zipchat.clone.CustomText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


/**
 * Created by client on 1/3/2017.
 */

@SuppressLint("AppCompatCustomView")
public class CustomRegularTextView extends AppCompatTextView {

    public CustomRegularTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomRegularTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Regular.ttf");
        setTypeface(tf, 1);

    }

}
