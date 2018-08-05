package tk.cavinc.checklist.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String dateToStr(String mask,Date date){
        SimpleDateFormat format = new SimpleDateFormat(mask);
        return format.format(date);
    }

}