package com.taisau.facecardcompare.util;

import android.util.Log;


import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jxl.Cell;
import jxl.Image;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.biff.RowsExceededException;

/**
 * Created by whx on 2017-10-20
 * Excel导入导出工具类
 */
public class ExcelUtils2 {
    /**
     * @param list       数据源
     * @param fieldMap   类的英文属性和Excel中的中文列名的对应关系 例：{id=编号}
     *                   如果需要的是引用对象的属性，则英文属性使用类似于EL表达式的格式
     *                   如：list中存放的都是student，student中又有college属性，而我们需要学院名称，则可以这样写
     *                   fieldMap.put("college.collegeName","学院名称")
     * @param sheetName  工作表的名称
     * @param pageNum    页码（0表示新建一个Excel;大于0,如果Excel存在，则插入一个新表，如果不存在，着创建一个Excel）
     * @param sheetSize  每个工作表中记录的最大个数
     * @param targetFile Excel绝对路径
     * @throws ExcelException 自定义异常
     * @Description : 导出Excel（可以导出到本地文件系统，也可以导出到浏览器，可自定义工作表大小）
     */
    public static <T> void listToExcel(List<T> list, LinkedHashMap<String, String> fieldMap,
                                       String sheetName, int pageNum, int sheetSize,
                                       File targetFile) throws ExcelException {
        if (list == null || list.size() == 0) {
            throw new ExcelException("数据源中没有任何数据");
        }
        if (sheetSize < 1 || sheetSize > 65535) {
            sheetSize = 65535;
        }
        WritableWorkbook wwb;
        try {
            if (pageNum != 0) {
                //  Excel获得文件
                Workbook wb = Workbook.getWorkbook(targetFile);
                // 打开文件
                wwb = Workbook.createWorkbook(targetFile, wb);
            } else {
                // 打开文件
                wwb = Workbook.createWorkbook(targetFile);
            }
            // 因为2003的Excel一个工作表最多可以有65536条记录，除去列头剩下65535条
            // 所以如果记录太多，需要放到多个工作表中，其实就是个分页的过程
            // 1.计算一共有多少个工作表
            double sheetNum = Math.ceil(list.size() / Integer.valueOf(sheetSize).doubleValue());
            // 2.创建相应的工作表,并向其中填充数据
            for (int i = 0; i < sheetNum; i++) {
                // 如果只有一个工作表的情况
                if (sheetNum == 1) {
                    WritableSheet sheet = wwb.createSheet(sheetName, pageNum);
                    // 向工作表中填充数据
                    fillSheet(sheet, list, fieldMap, 0, list.size() - 1);
                    // 有多个工作表的情况
                } else {
                    WritableSheet sheet = wwb.createSheet(sheetName + (i + 1), i);
                    // 获取开始索引和结束索引
                    int firstIndex = i * sheetSize;
                    int lastIndex = (i + 1) * sheetSize - 1 > list.size() - 1 ? list.size() - 1 : (i + 1) * sheetSize - 1;
                    // 填充工作表
                    fillSheet(sheet, list, fieldMap, firstIndex, lastIndex);
                }
            }
            wwb.write();
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
            // 如果是ExcelException,则直接抛出
            if (e instanceof ExcelException) {
                throw (ExcelException) e;
                // 否则将其他异常包装成ExcelException再抛出
            } else {
                throw new ExcelException("导出Excel失败");
            }
        }
    }

    /**
     * @param list       数据源
     * @param fieldMap   类的英文属性和Excel中的中文列名的对应关系 例：{id=编号}
     *                   如果需要的是引用对象的属性，则英文属性使用类似于EL表达式的格式
     *                   如：list中存放的都是student，student中又有college属性，而我们需要学院名称，则可以这样写
     *                   fieldMap.put("college.collegeName","学院名称")
     * @param sheetName  工作表的名称
     * @param pageNum    页码（0表示新建一个Excel;大于0,如果Excel存在，则插入一个新表，如果不存在，着创建一个Excel）
     * @param targetFile Excel绝对路径
     * @throws ExcelException 自定义异常
     * @Description : 导出Excel（可以导出到本地文件系统，也可以导出到浏览器，工作表大小为2003支持的最大值）
     */
    public static <T> void listToExcel(List<T> list, LinkedHashMap<String, String> fieldMap,
                                       String sheetName, int pageNum, File targetFile) throws ExcelException {
        listToExcel(list, fieldMap, sheetName, pageNum, 65535, targetFile);
    }
    /**
     * @param list 数据源
     * @param fieldMap 类的英文属性和Excel中的中文列名的对应关系
     * @param sheetName 工作表的名称
     * @param sheetSize 每个工作表中记录的最大个数
     * @param response 使用response可以导出到浏览器
     * @throws ExcelException 自定义异常
     * @Description : 导出Excel（导出到浏览器，可以自定义工作表的大小）
     *//*
    public static <T> void listToExcel(List<T> list, LinkedHashMap<String, String> fieldMap, String sheetName, int sheetSize, HttpServletResponse response) throws ExcelException {
// 文件名默认设置为当前时间：年月日时分秒
        String fileName = DateFormatUtils.format(new Date(), "yyyyMMddhhmmss");
// 设置response头信息
        response.reset();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xls");
// 创建工作簿并发送到浏览器
        try {
            OutputStream os = response.getOutputStream();
            listToExcel(list, fieldMap, sheetName, sheetSize, os);
        } catch (Exception e) {
            e.printStackTrace();
// 如果是ExcelException,则直接抛出
            if (e instanceof ExcelException) {
                throw (ExcelException) e;
            } else {
// 否则将其他异常包装成ExcelException再抛出
                throw new ExcelException("导出excel失败");
            }
        }
    }*/
    /**
     * @param list 数据源
     * @param fieldMap 类的英文属性和Excel中的中文列名的对应关系
     * @param sheetName 工作表的名称
     * @param response 使用response可以导出到浏览器
     * @throws ExcelException 自定义异常
     * @Description : 导出Excel（导出到浏览器，工作表的大小是2003支持的最大值:65535）
     *//*
    public static <T> void listToExcel(List<T> list, LinkedHashMap<String, String> fieldMap,
     String sheetName, HttpServletResponse response) throws ExcelException {
        listToExcel(list, fieldMap, sheetName, 65535, response);
    }*/

    /**
     * @param sheet      工作表
     * @param list       数据源数据
     * @param fieldMap   中英文属性对照关系map
     * @param firstIndex 开始索引
     * @param lastIndex  结束索引
     * @throws Exception 异常
     * @Description : 向工作表中填充数据
     */
    private static <E> void fillSheet(WritableSheet sheet, List<E> list, LinkedHashMap<String, String> fieldMap,
                                      int firstIndex, int lastIndex) throws Exception {
        //定义单元格格式
        WritableCellFormat arial12format = new WritableCellFormat();
        arial12format.setVerticalAlignment(VerticalAlignment.CENTRE);
// 定义存放英文字段名和中文字段名的数组
        int size = fieldMap.size();
        String[] enFields = new String[size];
        String[] cnFields = new String[size];
// 填充数组
        int count = 0;
        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            enFields[count] = entry.getKey();
            cnFields[count] = entry.getValue();
            count++;
        }
// 填充表头
        for (int i = 0; i < cnFields.length; i++) {
            Label label = new Label(i, 0, cnFields[i], arial12format);
            sheet.addCell(label);
        }
        //是否包含图片
        boolean containPicture = false;
// 填充内容
        int rowNo = 1;
        for (int index = firstIndex; index <= lastIndex; index++) {
            E item = list.get(index);
            for (int i = 0; i < enFields.length; i++) {
                Object objValue = getFieldValueByNameSequence(enFields[i], item);
                String fieldValue = objValue == null ? "" : objValue.toString();
                if (fieldValue.contains(".png")) {
                    WritableImage wImage = new WritableImage(i, rowNo, 1, 1, new File(fieldValue));
                    sheet.addImage(wImage);
//                    sheet.setColumnView(i, 20); //根据内容自动设置列宽
//                    sheet.setRowView(i, 3000, false); //设置行高
                    containPicture = true;
                } else {
                    Label label = new Label(i, rowNo, fieldValue, arial12format);
                    sheet.addCell(label);
                }

            }
            rowNo++;
        }
        if (containPicture) {
            setColumnAutoSizeContainPicture(sheet);
        } else {
            // 设置自动列宽
            setColumnAutoSize(sheet, 5);
        }
    }

    /**
     * @param fieldNameSequence 带路径的属性名或简单属性名
     * @param o                 对象
     * @return 属性值
     * @throws Exception 异常
     * @Description : 根据带路径或不带路径的属性名获取属性值
     * 即接受简单属性名，如userName等，又接受带路径的属性名，如student.department.name等
     */
    private static Object getFieldValueByNameSequence(String fieldNameSequence, Object o) throws Exception {
        Object value;
// 将fieldNameSequence进行拆分
        String[] attributes = fieldNameSequence.split("\\.");
        if (attributes.length == 1) {
            value = getFieldValueByName(fieldNameSequence, o);
        } else {
// 根据属性名获取属性对象
            Object fieldObj = getFieldValueByName(attributes[0], o);
            String subFieldNameSequence = fieldNameSequence.substring(fieldNameSequence.indexOf(".") + 1);
            value = getFieldValueByNameSequence(subFieldNameSequence, fieldObj);
        }
        return value;
    }

    /**
     * @param fieldName 字段名
     * @param o         对象
     * @return 字段值
     * @Description : 根据字段名获取字段值
     */
    private static Object getFieldValueByName(String fieldName, Object o) throws Exception {
        Object value;
        Field field = getFieldByName(fieldName, o.getClass());
        if (field != null) {
            field.setAccessible(true);
            value = field.get(o);
        } else {
            throw new ExcelException(o.getClass().getSimpleName() + "类不存在字段名" + fieldName);
        }
        return value;
    }

    /**
     * @param fieldName 字段名
     * @param clazz     包含该字段的类
     * @return 字段
     * @Description : 根据字段名获取字段
     */
    private static Field getFieldByName(String fieldName, Class<?> clazz) {
// 拿到本类所有的字段
        Field[] selfFields = clazz.getDeclaredFields();
// 如果本类中存放该字段，则返回
        for (Field field : selfFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
// 否则，查看父类中是否存在此字段，如果有则返回
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null && superClazz != Object.class) {
            return getFieldByName(fieldName, superClazz);
        }
// 如果本来和父类都没有该字段，则返回null
        return null;
    }

    /**
     * @param ws 表
     * @Description : 设置工作表自动列宽和首行加粗
     */
    private static void setColumnAutoSize(WritableSheet ws, int extraWith) {
// 获取本列的最宽单元格的宽度
        for (int i = 0; i < ws.getColumns(); i++) {
            int colWith = 0;
            for (int j = 0; j < ws.getRows(); j++) {
                String content = ws.getCell(i, j).getContents();
                int cellWith = content.length();
                if (colWith < cellWith) {
                    colWith = cellWith;
                }
            }
// 设置单元格的宽度为最宽宽度+额外宽度
            ws.setColumnView(i, colWith + extraWith);
        }
    }

    /**
     * @param ws 表
     * @Description : 设置工作表自动列宽和首行加粗
     */
    private static void setColumnAutoSizeContainPicture(WritableSheet ws) {
// 获取本列的最宽单元格的宽度
        for (int i = 0; i < ws.getColumns(); i++) {
            ws.setColumnView(i, 20); //根据内容自动设置列宽
            for (int j = 1; j < ws.getRows(); j++) {
                try {
                    ws.setRowView(j, 3000, false); //设置行高
                } catch (RowsExceededException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * @param is             要导入Excel的输入流
     * @param sheetName      导入的工作表名称
     * @param entityClass    List中对象的类型（Excel中的每一行都要转化为该类型的对象）
     * @param fieldMap       类的英文属性和Excel中的中文列名的对应关系 例：{id=编号}
     * @param uniqueFields   指定业务主键组合（即复合主键），这些列的组合不能重复
     * @return List
     * @throws ExcelException 自定义异常
     * @Description 将Excel转化成实体对象List
     */
    public static <T> List<T> excelToList(InputStream is, String sheetName, Class<T> entityClass,
                                          LinkedHashMap<String, String> fieldMap, String[] uniqueFields
                                          ) throws ExcelException {
        return excelToList(is, sheetName, entityClass, fieldMap, uniqueFields, -1, null);
    }
//    /**
//     * @param is             要导入Excel的输入流
//     * @param sheetName      导入的工作表名称
//     * @param entityClass    List中对象的类型（Excel中的每一行都要转化为该类型的对象）
//     * @param fieldMap       类的英文属性和Excel中的中文列名的对应关系 例：{id=编号}
//     * @param uniqueFields   指定业务主键组合（即复合主键），这些列的组合不能重复
//     * @param pictureColumns 包含图片的列，没有图片传负数
//     * @param copyPath       图片保存路径(仅包含父路径，图片名称默认使用第一个列的列名)，没有传null
//     * @return List
//     * @throws ExcelException 自定义异常
//     * @Description 将Excel转化成实体对象List
//     */
//    public static <T> List<T> excelToList(InputStream is, String sheetName, Class<T> entityClass,
//                                          LinkedHashMap<String, String> fieldMap, String[] uniqueFields,
//                                          int pictureColumns, String copyPath) throws ExcelException {
//        return excelToList(is, sheetName, entityClass, fieldMap, uniqueFields, pictureColumns, copyPath);
//    }

    /**
     * @param is             要导入Excel的输入流
     * @param sheetName      导入的工作表名称
     * @param entityClass    List中对象的类型（Excel中的每一行都要转化为该类型的对象）
     * @param fieldMap       类的英文属性和Excel中的中文列名的对应关系 例：{id=编号}
     * @param uniqueFields   指定业务主键组合（即复合主键），这些列的组合不能重复
     * @param pictureColumns 包含图片的列，没有图片传负数
     * @param copyPath       图片保存路径(仅包含父路径，图片名称默认使用第一个列的列名)，没有传null
     * @return List
     * @throws ExcelException 自定义异常
     * @Description 将Excel转化成实体对象List
     */
    public static <T> List<T> excelToList(InputStream is, String sheetName, Class<T> entityClass,
                                          LinkedHashMap<String, String> fieldMap, String[] uniqueFields,
                                          int pictureColumns, String copyPath) throws ExcelException {
        Log.e("excelToList", "excelToList:           1");
// 定义要返回的list
        List<T> resultList = new ArrayList<>();
        try {
// 根据excel数据源创建WorkBook
            Workbook wb = Workbook.getWorkbook(is);
// 获取工作表
            Sheet sheet = wb.getSheet(sheetName);
// 获取工作表的有效行数
            Log.e("excelToList", "excelToList:          2");
            int realRows = 0;
            for (int i = 0; i < sheet.getRows(); i++) {
                int nullCols = 0;
                for (int j = 0; j < sheet.getColumns(); j++) {
                    Cell CurrentCell = sheet.getCell(j, i);
                    if (CurrentCell == null || "".equals(CurrentCell.getContents())) {
                        nullCols++;
                    }
                }
                if (nullCols == sheet.getColumns()) {
                    break;
                } else {
                    realRows++;
                }
            }
            Log.e("excelToList", "excelToList:           3");
// 如果Excel中没有任何数据则提示错误信息
            if (realRows <= 1) {
                throw new ExcelException("Excel文件中没有任何数据");
            }
            Cell[] firstRow = sheet.getRow(0);
            String[] excelFieldNames = new String[firstRow.length];
// 获取Excel的列名
            for (int i = 0; i < firstRow.length; i++) {
                excelFieldNames[i] = firstRow[i].getContents().trim();
            }
// 判断需要的字段在Excel中是否都存在
            boolean isExist = true;
            List<String> excelFieldList = Arrays.asList(excelFieldNames);
            for (String cnName : fieldMap.values()) {
                if (!excelFieldList.contains(cnName)) {
                    isExist = false;
                    break;
                }
            }
// 如果有列名不存在或不匹配，则抛出异常并提示错误
            if (!isExist) {
                throw new ExcelException("Excel中缺少必要的字段，或字段名称有误");
            }
            Log.e("excelToList", "excelToList:           4");
// 将列名和列号放入Map中，这样通过列名就可以拿到列号
            LinkedHashMap<String, Integer> colMap = new LinkedHashMap<>();
            for (int i = 0; i < excelFieldNames.length; i++) {
                colMap.put(excelFieldNames[i], firstRow[i].getColumn());
            }
            if (uniqueFields != null && uniqueFields.length > 0) {
// 判断是否有重复行
// 1.获取uniqueFields指定的列
                Cell[][] uniqueCells = new Cell[uniqueFields.length][];
                for (int i = 0; i < uniqueFields.length; i++) {
                    int col = colMap.get(uniqueFields[i]);
                    uniqueCells[i] = sheet.getColumn(col);
                }
// 2.从指定列中寻找重复行
                for (int i = 1; i < realRows; i++) {
                    int nullCols = 0;
                    int length = uniqueFields.length;
                    for (int j = 0; j < length; j++) {
                        Cell currentCell = uniqueCells[j][i];
                        String currentContent = currentCell.getContents().trim();
                        Cell sameCell = sheet.findCell(currentContent, currentCell.getColumn(), currentCell.getRow() + 1, currentCell.getColumn(), uniqueCells[j][realRows - 1].getRow(), true);
                        if (sameCell != null) {
                            nullCols++;
                        }
                    }
// 复合主键，意味着这些列的组合不能重复，
// 只有当所有的列都有重复的时候，才被认为是有重复行
                    if (nullCols == length) {
                        throw new Exception("Excel中有重复行，请检查");
                    }
                }
            }
            Log.e("excelToList", "excelToList:           5");
            String firstContent = "test";
// 将sheet转换为list
            for (int i = 1; i < realRows; i++) {
// 新建要转换的对象
                T entity = entityClass.newInstance();
// 给对象中的字段赋值
                for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
// 获取英文字段名
                    String enNormalName = entry.getKey();
// 获取中文字段名
                    String cnNormalName = entry.getValue();
// 根据中文字段名获取列号
                    int col = colMap.get(cnNormalName);
                    String content;

                    if (pictureColumns >= 0 && col == pictureColumns) {
                        //获取图片、并保存
                        /*//返回指定位置的图像,索引从0开始  */
                        if (copyPath != null) {
                        Image image = sheet.getDrawing(i - 1);
                        Log.e("excelToList", "excelToList: imagePath=" + image.getImageFile().getAbsolutePath());
                        File toFile = new File(copyPath + File.separator + firstContent + ".png");
                        FileUtils.copyFile(image.getImageFile(), toFile, true);
                        content = toFile.getAbsolutePath();
                        } else {
                            content = "";
                        }
                    } else {
                        // 获取当前单元格中的内容
                        content = sheet.getCell(col, i).getContents().trim();
                        if (col == 0) {
                            firstContent = content;
                        }
                    }
                    // 给对象赋值
                    setFieldValueByName(enNormalName, content, entity);

                }
                resultList.add(entity);
            }

        } catch (Exception e) {
            e.printStackTrace();
// 如果是ExcelException,则直接抛出
            if (e instanceof ExcelException) {
                throw (ExcelException) e;
            } else {
// 否则将其包装成ExcelException,再将其抛出
                throw new ExcelException("导入ExceL失败");
            }
        }
        return resultList;
    }

    /**
     * @param fieldName  字段名
     * @param fieldValue 字段值
     * @param o          对象
     * @Description 根据字段名给对象的字段赋值
     */
    private static void setFieldValueByName(String fieldName, Object fieldValue, Object o) throws Exception {
// if (fieldName.equals("id") || fieldName.equals("operatorId")) {
// return;
// }
        Field field = getFieldByName(fieldName, o.getClass());
        if (field != null) {
            field.setAccessible(true);
// 获取字段类型
            Class<?> fieldType = field.getType();
// 根据字段类型给字段赋值
            if (String.class == fieldType) {
                field.set(o, String.valueOf(fieldValue));
            } else if (Integer.TYPE == fieldType || Integer.class == fieldType) {
                field.set(o, Integer.valueOf(fieldValue.toString()));
            } else if (Long.TYPE == fieldType || Long.class == fieldType) {
                field.set(o, Long.valueOf(fieldValue.toString()));
            } else if (Float.TYPE == fieldType || Float.class == fieldType) {
                field.set(o, Float.valueOf(fieldValue.toString()));
            } else if (Short.TYPE == fieldType || Short.class == fieldType) {
                field.set(o, Short.valueOf(fieldValue.toString()));
            } else if (Double.TYPE == fieldType || Double.class == fieldType) {
                field.set(o, Double.valueOf(fieldValue.toString()));
            } else if (Character.TYPE == fieldType) {
                if ((fieldValue != null) && (fieldValue.toString().length() > 0)) {
                    field.set(o, fieldValue.toString().charAt(0));
                }
            } else if (Date.class == fieldType) {
                String dateStr = fieldValue.toString();
// String[] split = dateStr.split("/");
// StringBuilder sb = new StringBuilder();
// int i = 0;
// for (String str : split) {
// if (i == split.length - 1) {
// sb.append("20" + str);
// continue;
// }
// sb.append(str).append("/");
// i++;
// }
// dateStr = sb.toString();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
                Date parseDate = formatter.parse(dateStr);
//Date parseDate = DateUtils.parseDate(fieldValue.toString(), new String[] { "MM/dd/yyyy HH:mm:ss" });
                field.set(o, parseDate);
            } else if (BigDecimal.class == fieldType) {
                field.set(o, new BigDecimal(fieldValue.toString()));
            } else {
                field.set(o, fieldValue);
            }
        } else {
            throw new ExcelException(o.getClass().getSimpleName() + "类不存在字段名" + fieldName);
        }
    }


// List<Student> students = new ArrayList<Student>();
// Student s1 = new Student();
// s1.setId(1);
// s1.setName("Tom");
// s1.setScore(78);
// students.add(s1);
//
// Student s2 = new Student();
// s2.setId(2);
// s2.setName("Hanks");
// s2.setScore(56);
// students.add(s2);
//
// Student s3 = new Student();
// s3.setId(3);
// s3.setName("jerry");
// s3.setScore(99);
// students.add(s3);
//
// LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
// fieldMap.put("id", "编号");
// fieldMap.put("name", "姓名");
// fieldMap.put("score", "分数");
// File osFile = new File("e:/test.xls");
// FileOutputStream fos = new FileOutputStream(osFile);
// listToExcel(students, fieldMap, "studentScore", fos);
}
