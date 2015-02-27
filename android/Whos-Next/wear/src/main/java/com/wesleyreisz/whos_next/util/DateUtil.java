package com.wesleyreisz.whos_next.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wesleyreisz on 10/21/14.
 */
public class DateUtil {
    public static String formatDateForDisplay(String input){
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("MMM dd, yyyy").format(date);
    }
}
