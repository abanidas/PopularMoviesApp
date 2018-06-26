package com.abani.nanodegree.android.popularmoviesapp.utils;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class TextClickableSpan extends ClickableSpan {

    private boolean underlinedText = false;

    public TextClickableSpan(boolean underlinedText) {
        this.underlinedText = underlinedText;
    }

    @Override
    public void updateDrawState(TextPaint ds) {

        ds.setUnderlineText(underlinedText);
        ds.setColor(Color.DKGRAY);
    }

    @Override
    public void onClick(View widget) {

    }
}
