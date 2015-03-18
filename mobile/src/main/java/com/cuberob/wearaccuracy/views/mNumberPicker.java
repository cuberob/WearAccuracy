package com.cuberob.wearaccuracy.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;


public class mNumberPicker extends NumberPicker {

    public mNumberPicker(Context context) {
        super(context);
    }

    public mNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttributeSet(attrs);
    }

    public mNumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        processAttributeSet(attrs);
    }
    private void processAttributeSet(AttributeSet attrs) {
        this.setMinValue(attrs.getAttributeIntValue(null, "min", 0));
        this.setMaxValue(attrs.getAttributeIntValue(null, "max", 150));
        this.setValue(attrs.getAttributeIntValue(null, "value", 0));
    }
}