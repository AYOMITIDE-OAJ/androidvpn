package com.oajstudios.evpn;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class CustomTxtSemibold extends androidx.appcompat.widget.AppCompatTextView {

public CustomTxtSemibold(Context context) {
        super(context);
        init();
        }

public CustomTxtSemibold(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        }

public CustomTxtSemibold(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        }

public void init() {
        if (!isInEditMode()){
        Typeface normalTypeface = Typeface.createFromAsset(getContext().getAssets(), "Montserrat-SemiBold.ttf");
        setTypeface(normalTypeface);
        }
        }
}
