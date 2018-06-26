package com.abani.nanodegree.android.popularmoviesapp.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.abani.nanodegree.android.popularmoviesapp.R;
import com.abani.nanodegree.android.popularmoviesapp.constants.Constants;

import java.text.SimpleDateFormat;
import java.util.Random;

public class CommonUtils {

    private static SimpleDateFormat responseFormatter, formatterToDisplay;

    public static String formatWithParenthesis(String data){
        if (data.isEmpty() || data == null){
            return "";
        }
        return "("+data+")";
    }

    public static String getVideoThumbnailURL(String key) {
        if (key.isEmpty() || key == null){
            return "";
        }
        return Constants.VIDEO_THUMBNAIL_BASE_URL + key + Constants.THUMBNAIL_QUALITY;
    }

    public static SimpleDateFormat getResponseFormatter() {
        responseFormatter = new SimpleDateFormat(Constants.RESPONSE_DATE_FORMAT);
        return responseFormatter;
    }

    public static SimpleDateFormat getFormatterToDisplay() {
        formatterToDisplay = new SimpleDateFormat(Constants.FORMAT_TO_DISPLAY);
        return formatterToDisplay;
    }

    /*
    * For making clickable part of the Textview
    * for viewing more or viewing less
    */
    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new TextClickableSpan(false){
                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, tv.getText().toString(), Constants.VIEW_LESS, false);
                    } else {
                        makeTextViewResizable(tv, 3, tv.getText().toString(), Constants.VIEW_MORE, true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    /*
    * To make Textview content shorter to look better
    * and resize when user clicks on View more
    */
    public static void makeTextViewResizable(final TextView tv, final int maxLine, String contentText, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(contentText);
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else if (maxLine == -1){
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex).toString();
                }
                tv.setText(text);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(
                        addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                viewMore), TextView.BufferType.SPANNABLE);
            }
        });

    }

    public static int getRandomMaterialColor(Context context){
        int random = new Random().nextInt(6);
        switch (random){
            case 0: return ContextCompat.getColor(context, R.color.material_color_1);
            case 1: return ContextCompat.getColor(context, R.color.material_color_2);
            case 2: return ContextCompat.getColor(context, R.color.material_color_3);
            case 3: return ContextCompat.getColor(context, R.color.material_color_4);
            case 4: return ContextCompat.getColor(context, R.color.material_color_5);
            default: return ContextCompat.getColor(context, R.color.material_color_6);
        }
    }
}
