package tk.cavinc.checklist.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.PageOrientation;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import tk.cavinc.checklist.data.models.ArhiveHeadModel;
import tk.cavinc.checklist.data.models.ArhiveItemCommentModel;
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
    private ArrayList<ArhiveItemCommentModel> mComment;


    public StoreXlsFile(Context context, String outPath, String fileName,
                        ArrayList<ArhiveHeadModel> prepareData,ArrayList<ArhiveItemCommentModel> comment){
        mContext = context;
        mOutPath = outPath;
        mFileName = fileName;
        mData = prepareData;
        mComment = comment;
    }


    private int current_offset_y;

    public void write(){
        File output = new File(mOutPath,mFileName);
        Log.d("STORE","FILE : "+output.getAbsolutePath());

        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("ru", "RU"));

        WritableWorkbook wworkbook;
        try {
            wworkbook = Workbook.createWorkbook(output,wbSettings);
            WritableSheet wsheet = wworkbook.createSheet("First Sheet", 0);
            wsheet.getSettings().setOrientation(PageOrientation.PORTRAIT); // портретная ориентация
            wsheet.getSettings().setFitWidth(1);

            createHead(wsheet);
            createBody(wsheet);

            if (mComment.size() != 0) {
                createComment(wsheet);
            }

            wworkbook.write();
            wworkbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createComment(WritableSheet sheet) throws WriteException {
        sheet.addRowPageBreak(current_offset_y+5);
        int ofset_y = current_offset_y+6;

        WritableFont times14font = new WritableFont(WritableFont.TIMES,14,WritableFont.BOLD);
        WritableCellFormat times14Boldformat = new WritableCellFormat(times14font);
        times14Boldformat.setAlignment(Alignment.CENTRE);

        WritableFont times11font = new WritableFont(WritableFont.TIMES,11,WritableFont.BOLD);
        WritableCellFormat times11boldformat = new WritableCellFormat(times11font);
        times11boldformat.setBorder(Border.ALL, BorderLineStyle.THIN);
        times11boldformat.setAlignment(Alignment.CENTRE);
        times11boldformat.setVerticalAlignment(VerticalAlignment.CENTRE);



        sheet.addCell(new Label(0,ofset_y,"ОБНАРУЖЕННЫЕ НЕСООТВЕТСТВИЯ И ПРИНЯТЫЕ МЕРЫ",times14Boldformat));
        sheet.mergeCells(0,ofset_y,6,ofset_y);
        ofset_y +=1;

        sheet.addCell(new Label(0,ofset_y,"Объект инспекции ",times11boldformat));
        sheet.addCell(new Label(1,ofset_y,"Замечания",times11boldformat));
        sheet.mergeCells(1,ofset_y,3,ofset_y);
        sheet.addCell(new Label(4,ofset_y,"Цех",times11boldformat));
        sheet.mergeCells(4,ofset_y,5,ofset_y);
        sheet.addCell(new Label(6,ofset_y,"Принятые меры",times11boldformat));
        sheet.mergeCells(6,ofset_y,8,ofset_y);
        ofset_y +=1;

        WritableCellFormat times11format = new WritableCellFormat(times11font);
        times11format.setBorder(Border.ALL, BorderLineStyle.THIN);

        times11format.setWrap(true); // перенос по словам

        times11format.setAlignment(Alignment.LEFT);
        times11format.setVerticalAlignment(VerticalAlignment.TOP);

        for (ArhiveItemCommentModel l : mComment) {
            sheet.addCell(new Label(0,ofset_y,l.getTitle(),times11format));
            sheet.addCell(new Label(1,ofset_y,l.getComment(),times11format));
            sheet.mergeCells(1,ofset_y,3,ofset_y);
            sheet.addCell(new Label(4,ofset_y,l.getGroupTitle(),times11format));
            sheet.mergeCells(4,ofset_y,5,ofset_y);
            sheet.addCell(new Label(6,ofset_y," ",times11format));
            sheet.mergeCells(6,ofset_y,8,ofset_y);
            ofset_y +=1;
        }
    }

    private void createHead(WritableSheet sheet) throws WriteException {
        WritableFont times14font = new WritableFont(WritableFont.TIMES,14,WritableFont.BOLD,true);
        WritableCellFormat times14format = new WritableCellFormat(times14font);
        times14format.setAlignment(Alignment.CENTRE);

        //times14format.setShrinkToFit(true); // растянуть до размера ?

        WritableFont times11font = new WritableFont(WritableFont.TIMES,11,WritableFont.BOLD,true);
        WritableCellFormat times11format = new WritableCellFormat(times11font);
        times11format.setBorder(Border.ALL, BorderLineStyle.THIN);
        times11format.setAlignment(Alignment.CENTRE);
        times11format.setVerticalAlignment(VerticalAlignment.CENTRE);

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

        // Настройки отображения
        sheet.mergeCells(0,0,6,0);
        sheet.mergeCells(0,2,0,3);

        sheet.mergeCells(1,2,6,2);

        CellView cv = new CellView();
        cv.setAutosize(true);
        sheet.setColumnView(0,cv);  // выставили ширину колонки ?
        sheet.setRowView(0,456); // выстоа первой строки от балды
    }

    private void createBody(WritableSheet sheet) throws WriteException {
        WritableFont times11font = new WritableFont(WritableFont.TIMES,11);
        WritableCellFormat times11format = new WritableCellFormat(times11font);
        times11format.setBorder(Border.ALL, BorderLineStyle.THIN);

        times11format.setWrap(true); // перенос по словам

        times11format.setAlignment(Alignment.LEFT);
        times11format.setVerticalAlignment(VerticalAlignment.TOP);

        WritableCellFormat times11formatCenter = new WritableCellFormat(times11font);
        times11formatCenter.setBorder(Border.ALL, BorderLineStyle.THIN);
        times11formatCenter.setAlignment(Alignment.CENTRE);
        times11formatCenter.setVerticalAlignment(VerticalAlignment.CENTRE);


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
                     sheet.addCell(new Label(ofset_x, ofset_y, val, times11formatCenter));
                 }
                 ofset_x += 1;
                }
                ofset_y +=1;
            }

        }
        current_offset_y = createFooter(sheet,ofset_y+2);
    }

    private int createFooter(WritableSheet sheet,int ofset_y) throws WriteException {
        WritableFont times11font = new WritableFont(WritableFont.TIMES,11);
        WritableCellFormat times11format = new WritableCellFormat(times11font);
        times11format.setBorder(Border.ALL, BorderLineStyle.THIN);

        sheet.addCell(new Label(0,ofset_y,"Ответственный",times11format));
        sheet.addCell(new Label(1,ofset_y,"Подпись",times11format));
        sheet.mergeCells(1,ofset_y,2,ofset_y);
        ofset_y +=1;
        sheet.addCell(new Label(0,ofset_y,"Заведующий производством ",times11format));
        sheet.addCell(new Label(1,ofset_y," ",times11format));
        sheet.mergeCells(1,ofset_y,2,ofset_y);
        ofset_y += 1;
        sheet.addCell(new Label(0,ofset_y,"Старший ночной смены",times11format));
        sheet.addCell(new Label(1,ofset_y,"",times11format));
        sheet.mergeCells(1,ofset_y,2,ofset_y);
        ofset_y +=1;
        return ofset_y;
    }

}
