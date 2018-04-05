package com.taisau.facecardcompare.util;

import android.content.Context;
import android.text.format.DateFormat;

import com.taisau.facecardcompare.model.HistoryList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by Administrator on 2017/8/14 0014.
 */

public class ExcelUtils {
    public static WritableFont arial14font = null;

    public static WritableCellFormat arial14format = null;
    public static WritableFont arial10font = null;
    public static WritableCellFormat arial10format = null;
    public static WritableFont arial12font = null;
    public static WritableCellFormat arial12format = null;

    public final static String UTF8_ENCODING = "UTF-8";
    public final static String GBK_ENCODING = "GBK";

    public static void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14,
                    WritableFont.BOLD);
            arial14font.setColour(jxl.format.Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW);
            arial10font = new WritableFont(WritableFont.ARIAL, 10,
                    WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(jxl.format.Colour.LIGHT_BLUE);
            arial12font = new WritableFont(WritableFont.ARIAL, 12);
            arial12format = new WritableCellFormat(arial12font);
            arial12format.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void initExcel(String fileName, String[] colName) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("比对历史记录表", 0);
            sheet.addCell(new Label(0, 0, fileName,
                    arial14format));
            sheet.addCell(new Label(0, 0, "id", arial10format));
            sheet.addCell(new Label(1, 0, "姓名", arial10format));
            sheet.addCell(new Label(2, 0, "身份证号", arial10format));
            sheet.addCell(new Label(3, 0, "性别", arial10format));
            sheet.addCell(new Label(4, 0, "民族", arial10format));
            sheet.addCell(new Label(5, 0, "生日", arial10format));
            sheet.addCell(new Label(6, 0, "住址", arial10format));
            sheet.addCell(new Label(7, 0, "发行单位", arial10format));
            sheet.addCell(new Label(8, 0, "有效期", arial10format));
            sheet.addCell(new Label(9, 0, "现场照本地位置", arial10format));
            sheet.addCell(new Label(10, 0, "身份证照片位置", arial10format));
            sheet.addCell(new Label(11, 0, "抓拍时间", arial10format));
            sheet.addCell(new Label(12, 0, "结果", arial10format));
            sheet.addCell(new Label(13, 0,"分数", arial12format));
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    @SuppressWarnings("unchecked")
       public static <T> void writeObjListToExcel(List<T> objList,
                                               String fileName, Context c) {
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                in = new FileInputStream(new File(fileName));
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(new File(fileName),
                        workbook);
                WritableSheet sheet = writebook.getSheet(0);
                for (int j = 0; j < objList.size(); j++) {
                    ArrayList<String> list = (ArrayList<String>) objList.get(j);
                    for (int i = 0; i < list.size(); i++) {
                        sheet.addCell(new Label(i, j + 1, list.get(i),
                                arial12format));
                    }
                }
                writebook.write();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    public static  void writeHistoryListToExcel(List<HistoryList> objList,
                                               String fileName, Context c) {
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                in = new FileInputStream(new File(fileName));
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(new File(fileName),
                        workbook);
                WritableSheet sheet = writebook.getSheet(0);
                /**
                 *     @Id
                long id;
                 @NotNull
                 String person_name;
                 @NotNull
                 String id_card; String sex; String ethnic; String birthday; String address; String card_release_org; String valid_time;
                 String face_path; String card_path; String face_fea_path; String card_fea_path; Date time; String com_status; float score;
                 */
                for (int j = 0; j < objList.size(); j++) {
                    HistoryList temp=  objList.get(j);
                    sheet.addCell(new Label(0, j + 1,""+temp.getId(), arial12format));
                    sheet.addCell(new Label(1, j + 1,""+temp.getPerson_name(), arial12format));
                    sheet.addCell(new Label(2, j + 1,""+temp.getId_card(), arial12format));
                    sheet.addCell(new Label(3, j + 1,""+temp.getSex(),arial12format));
                    sheet.addCell(new Label(4, j + 1,""+temp.getEthnic(), arial12format));
                    sheet.addCell(new Label(5, j + 1,""+temp.getBirthday(), arial12format));
                    sheet.addCell(new Label(6, j + 1,""+temp.getAddress(), arial12format));
                    sheet.addCell(new Label(7, j + 1,""+temp.getCard_release_org(), arial12format));
                    sheet.addCell(new Label(8, j + 1,""+temp.getValid_time(), arial12format));
                    sheet.addCell(new Label(9, j + 1,""+temp.getFace_path(), arial12format));
                    sheet.addCell(new Label(10, j + 1,""+temp.getCard_path(), arial12format));
                    sheet.addCell(new Label(11, j + 1, DateFormat.format("yyyy-MM-dd HH:mm:ss",temp.getTime()).toString(), arial12format));
                    sheet.addCell(new Label(12, j + 1,""+temp.getCom_status(), arial12format));
                    sheet.addCell(new Label(13, j + 1,""+temp.getScore(), arial12format));
                }
                writebook.write();
                System.out.println("finish write excel");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

  /*  public static  List<T> read2DB(File f, Context con) {
        ArrayList<T> billList = new ArrayList<T>();
        try {
            Workbook course = null;
            course = Workbook.getWorkbook(f);
            Sheet sheet = course.getSheet(0);

            Cell cell = null;
            for (int i = 1; i < sheet.getRows(); i++) {
                BillObject tc = new BillObject();
                cell = sheet.getCell(1, i);
                tc.setFood(cell.getContents());
                cell = sheet.getCell(2, i);
                tc.setClothes(cell.getContents());
                cell = sheet.getCell(3, i);
                tc.setHouse(cell.getContents());
                cell = sheet.getCell(4, i);
                tc.setVehicle(cell.getContents());
                Log.d("gaolei", "Row"+i+"---------"+tc.getFood() + tc.getClothes()
                        + tc.getHouse() + tc.getVehicle());
                billList.add(tc);

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return billList;
    }*/

    public static Object getValueByRef(Class cls, String fieldName) {
        Object value = null;
        fieldName = fieldName.replaceFirst(fieldName.substring(0, 1), fieldName
                .substring(0, 1).toUpperCase());
        String getMethodName = "get" + fieldName;
        try {
            Method method = cls.getMethod(getMethodName);
            value = method.invoke(cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
