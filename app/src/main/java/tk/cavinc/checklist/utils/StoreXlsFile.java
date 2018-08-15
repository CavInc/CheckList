package tk.cavinc.checklist.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by cav on 15.08.18.
 */

public class StoreXlsFile {

    private Context mContext;
    private String mOutPath;
    private String mFileName;

    public StoreXlsFile(Context context, String outPath, String fileName){
        mContext = context;
        mOutPath = outPath;
        mFileName = fileName;
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

            Number number = new Number(3, 4, 3.1459);
            wsheet.addCell(number);
            wworkbook.write();
            wworkbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    private void createHead(WritableSheet sheet) throws WriteException {
        WritableFont times14font = new WritableFont(WritableFont.TIMES,14,WritableFont.BOLD,true);
        WritableCellFormat times14format = new WritableCellFormat(times14font);

        times14format.setShrinkToFit(true); // растянуть до размера ?

        WritableFont times11font = new WritableFont(WritableFont.TIMES,11,WritableFont.BOLD,true);
        WritableCellFormat times11format = new WritableCellFormat(times11font);

        times11format.setShrinkToFit(true); // растянуть до размера ?


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

}
