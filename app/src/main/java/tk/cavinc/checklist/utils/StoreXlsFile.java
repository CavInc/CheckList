package tk.cavinc.checklist.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import tk.cavinc.checklist.data.models.ArhiveHeadModel;
import tk.cavinc.checklist.data.models.ArhiveItemModel;
import tk.cavinc.checklist.ui.activitys.ArhiveActivity;

/**
 * Created by cav on 15.08.18.
 */

public class StoreXlsFile {

    private final ArrayList<ArhiveHeadModel> mData;
    private Context mContext;
    private String mOutPath;
    private String mFileName;


    public StoreXlsFile(Context context, String outPath, String fileName, ArrayList<ArhiveHeadModel> prepareData){
        mContext = context;
        mOutPath = outPath;
        mFileName = fileName;
        mData = prepareData;
    }



    public void write(){
        File output = new File(mOutPath,mFileName);
        Log.d("STORE","FILE : "+output.getAbsolutePath());

        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("ru", "RU"));

        WritableWorkbook wworkbook;
        try {
            wworkbook = Workbook.createWorkbook(output);
            WritableSheet wsheet = wworkbook.createSheet("First Sheet", 0);

            createHead(wsheet);
            createBody(wsheet);

            wworkbook.write();
            wworkbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createHead(WritableSheet sheet) throws WriteException {
        WritableFont times14font = new WritableFont(WritableFont.TIMES,14,WritableFont.BOLD,true);
        WritableCellFormat times14format = new WritableCellFormat(times14font);

        //times14format.setShrinkToFit(true); // растянуть до размера ?

        WritableFont times11font = new WritableFont(WritableFont.TIMES,11,WritableFont.BOLD,true);
        WritableCellFormat times11format = new WritableCellFormat(times11font);
        times11format.setBorder(Border.ALL, BorderLineStyle.THIN);

        //times11format.setShrinkToFit(true); // растянуть до размера ?


        sheet.addCell(new Label(0,0,"ЛИСТ ПРОВЕРКИ ПРОИЗВОДСТВЕННОГО ЦЕХА",times14format));
        sheet.addCell(new Label(0,2,"Объект инспекции",times11format));
        sheet.addCell(new Label(1,2,"Время",times11format));
        sheet.addCell(new Label(1,3,"09-00",times11format));
        sheet.addCell(new Label(2,3,"13-00",times11format));
        sheet.addCell(new Label(3,3,"17-00",times11format));
        sheet.addCell(new Label(4,3,"21-00",times11format));
        sheet.addCell(new Label(5,3,"01-00",times11format));
        sheet.addCell(new Label(6,3,"05-00",times11format));

    }

    private void createBody(WritableSheet sheet) throws WriteException {
        WritableFont times11font = new WritableFont(WritableFont.TIMES,11);
        WritableCellFormat times11format = new WritableCellFormat(times11font);
        times11format.setBorder(Border.ALL, BorderLineStyle.THIN);

        WritableCellFormat times11BGformat = new WritableCellFormat(times11font);
        times11BGformat.setBorder(Border.ALL,BorderLineStyle.THIN);
        times11BGformat.setBackground(Colour.LIME);

        WritableFont times11Boldfont = new WritableFont(WritableFont.TIMES,11,WritableFont.BOLD,true);
        WritableCellFormat times11Boldformat = new WritableCellFormat(times11Boldfont);

        int ofset_y = 4;
        for (int i = 0 ; i< mData.size(); i++){
            //Log.d("STORE",mData.get(i).getId()+" "+mData.get(i).getTitle());
            sheet.addCell(new Label(0,ofset_y,mData.get(i).getTitle(),times11Boldformat));
            ofset_y += 1; // сдвиг на элементы

            ArrayList<ArrayList<ArhiveItemModel>> items = mData.get(i).getItems();
            for (int j = 0; j< items.size(); j++) {
                ArrayList<ArhiveItemModel> l1 = items.get(j);
                int ofset_x = 1;
                for (int k = 0; k< l1.size(); k++){
                 if (k == 0) {
                     sheet.addCell(new Label(0,ofset_y,l1.get(k).getTitle(),times11format));
                 }
                 String val = "*";
                 if (l1.get(k).isCheck()) {
                     val = "Ok";
                 }
                 if (l1.get(k).isPhoto()) {
                     sheet.addCell(new Label(ofset_x,ofset_y,val,times11BGformat));
                 } else {
                     sheet.addCell(new Label(ofset_x, ofset_y, val, times11format));
                 }
                 ofset_x += 1;
                }
                ofset_y +=1;
            }

        }
        createFooter(sheet,ofset_y+2);
    }

    private void createFooter(WritableSheet sheet,int ofset_y) throws WriteException {
        WritableFont times11font = new WritableFont(WritableFont.TIMES,11);
        WritableCellFormat times11format = new WritableCellFormat(times11font);
        times11format.setBorder(Border.ALL, BorderLineStyle.THIN);

        sheet.addCell(new Label(0,ofset_y,"Ответсвенный",times11format));
        sheet.addCell(new Label(1,ofset_y,"Подпись",times11format));
        ofset_y +=1;
        sheet.addCell(new Label(0,ofset_y," ",times11format));
        sheet.addCell(new Label(1,ofset_y," ",times11format));
    }

}
