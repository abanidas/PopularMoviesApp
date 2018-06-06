package com.abani.nanodegree.android.popularmoviesapp.utils;

public class CommonUtils {

    public static String formatWithParenthesis(String data){
        if (data.isEmpty() || data == null){
            return "";
        }
        return "("+data+")";
    }
}
