package tk.cavinc.checklist.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String dateToStr(String mask,Date date){
        SimpleDateFormat format = new SimpleDateFormat(mask);
        return format.format(date);
    }

    public static Date strToDate(String mask,String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(mask);
        return format.parse(date);
    }

}