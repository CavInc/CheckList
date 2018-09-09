package tk.cavinc.checklist.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import tk.cavinc.checklist.R;
import tk.cavinc.checklist.data.manager.DataManager;
import tk.cavinc.checklist.data.models.CountTimeModel;
import tk.cavinc.checklist.ui.activitys.QuestionActivity;

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

    // установка будильника о то что не завершили все.
    public static void startAlarm(){

    }

    public static void setNotification(Context context,String longData,String shortData,
                                       String cp,String tag){
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;

        Intent intent = new Intent(context, QuestionActivity.class);
        intent.putExtra(ConstantManager.WORK_DATA_LONG,longData);
        intent.putExtra(ConstantManager.WORK_DATA,shortData);
        intent.putExtra(ConstantManager.WORK_TIME,cp);
        intent.putExtra(ConstantManager.WORK_ID_TAG,tag);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pi = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);


        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(pi)
                .setSmallIcon(R.drawable.ic_warning_black_24dp)
                .setTicker("Предупреждение !!!")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Предупреждение")
                .setContentText("Не заполненна проверка за : "+cp)
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);


        if (Build.VERSION.SDK_INT < 16){
            notification = builder.getNotification(); // до API 16
        }else{
            notification = builder.build();
        }
        notificationManager.notify(1, notification);

    }


}