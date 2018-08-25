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

    public static String pathToData(String date) {
        Date dt = null;
        try {
            dt = strToDate("yyyy-MM-dd",date);
        } catch (ParseException e) {

        }
        return dateToStr("ddMMyyyy",dt);
    }

}