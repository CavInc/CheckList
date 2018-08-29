package tk.cavinc.checklist.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import tk.cavinc.checklist.data.manager.DataManager;
import tk.cavinc.checklist.data.models.CountTimeModel;

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

    public static Bitmap getPicSize(String currentImageFile,int targetW,int targetH){


        // Читаем с inJustDecodeBounds=true для определения размеров
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentImageFile, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // resized = Bitmap.createScaledBitmap(yourBitmap,(int)(yourBitmap.getWidth()*0.8), (int)(yourBitmap.getHeight()*0.8), true);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bitmap = BitmapFactory.decodeFile(currentImageFile, bmOptions);

        return bitmap;
    }

    // изменили изображение и сохранили
    public static void resizeImgFile(String currentImageFile) {
        Bitmap bitmap = null;
        Bitmap resized = null;
        try {
            bitmap = BitmapFactory.decodeFile(currentImageFile);

            resized = Bitmap.createScaledBitmap(bitmap,
                    (int) (bitmap.getWidth() * 0.6),
                    (int) (bitmap.getHeight() * 0.6), true);


            try {
                FileOutputStream fos = new FileOutputStream(currentImageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);

                fos.flush();
                fos.close();
            } catch (Exception e) {
                Log.e("MyLog", e.toString());
            }

        } finally {
            if (resized != null) {
                resized.recycle();
                resized = null;
            }
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        }

    }

    // проверяем перешло ли время в следующем дне через 9:00
    public static boolean testData(String workData,Date currentData){
        Date wd = null;
        Date cd = null;
        String time = Utils.dateToStr("HH",currentData);

        try {
            wd = strToDate("yyyy-MM-dd",workData);
            cd = strToDate("yyyy-MM-dd",dateToStr("yyyy-MM-dd",currentData));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (wd.after(cd)) {
            Log.d("UT","AFTER");
        }
        if (wd.before(cd)) {
            Log.d("UT","BEFORE");
            if (Integer.parseInt(time)>=9){
                Log.d("UT","GET 9 UP");
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

   public static boolean getCountTwo(ArrayList<CountTimeModel> rec, String time, DataManager dataManager) {
        int countQuestion = 0;
        int countRec = 0 ;

        countQuestion = dataManager.getPrefManager().getCountWorkTime(time);
        int id = rec.indexOf(new CountTimeModel(time,0));
        if (id != -1){
            countRec = rec.get(id).getCount();
        } else {
            return false;
        }
        if (countQuestion > countRec) return false;
        return true;
    }

}