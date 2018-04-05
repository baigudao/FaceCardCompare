package com.taisau.facecardcompare.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ds on 2017/12/19.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StringUtil {
    private final static String[] CHINA_NUMBER = {"一", "二", "三", "四", "五",
            "六", "七", "八", "九", "十"};
    private final static String[] CHINA_MONTH = {"一月", "二月", "三月", "四月", "五月",
            "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static SimpleDateFormat dateFormater = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private final static SimpleDateFormat dateFormater2 = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.CHINA);
    private final static SimpleDateFormat dateFormater3 = new SimpleDateFormat(
            "HH:mm:ss", Locale.CHINA);
    private final static SimpleDateFormat dateFormater4 = new SimpleDateFormat(
            "HH:mm", Locale.CHINA);
    private static SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy/MM/dd HH:mm");
    private static String today = "今天";
    private static String tomorrow = "明天";
    private static String[] weeks = new String[]{"周日", "周一", "周二", "周三",
            "周四", "周五", "周六"};
    private static SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
    private static SimpleDateFormat ampm = new SimpleDateFormat("EEE aa hh:mm",
            Locale.CHINA);
    private static SimpleDateFormat ampm1 = new SimpleDateFormat("aa",
            Locale.CHINA);
    private static SimpleDateFormat ampm2 = new SimpleDateFormat("aa hh:mm",
            Locale.CHINA);
    private static SimpleDateFormat date = new SimpleDateFormat("yyyy年MM月dd日",
            Locale.CHINA);
    private static SimpleDateFormat current = new SimpleDateFormat(
            "MM月dd日EEE aa HH:mm", Locale.CHINA);
    private static SimpleDateFormat outyear = new SimpleDateFormat(
            "yyyy年MM月dd日 aa HH:mm", Locale.CHINA);
    /**
     * 正则表达式:验证汉字(1-9个汉字)  {1,9} 自定义区间
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5]{1,9}$";

    /**
     * 正则表达式:验证身份证
     */
    public static final String REGEX_ID_CARD = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";

    public static long getTimeFromString(String str) {
        long t = 0;
        try {
            Date d = date.parse(str);
            t = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 判断是否为null或空值
     *
     * @param str String
     * @return true or false
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0
                || "null".equals(str.trim());
    }

    /**
     *      * 校验银行卡卡号      * @param cardId      * @return      
     *
     * @return
     */

    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId
                .substring(0, cardId.length() - 1));
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     *      * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位      * @param nonCheckCodeCardId
     *      * @return      
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null
                || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("//d+")) {
            throw new IllegalArgumentException("Bank card code must be number!");
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * 返回一位小数.如果有小数
     *
     * @param str
     * @return
     */
    public static String getTwoPoint(String str) {
        // int index = str.indexOf(".");
        // if (index == -1)
        // return str;
        try {
            double d = Double.parseDouble(str);
            // str = str.substring(0, index + 2);
            return String.format("%.2f", d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.00";
    }

    /**
     * 判断str1和str2是否相同
     *
     * @param str1 str1
     * @param str2 str2
     * @return true or false
     */
    public static boolean equals(String str1, String str2) {
        return str1 == str2 || str1 != null && str1.equals(str2);
    }

    /**
     * 判断str1和str2是否相同(不区分大小写)
     *
     * @param str1 str1
     * @param str2 str2
     * @return true or false
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 != null && str1.equalsIgnoreCase(str2);
    }

    /**
     * 判断字符串str1是否包含字符串str2
     *
     * @param str1 源字符串
     * @param str2 指定字符串
     * @return true源字符串包含指定字符串，false源字符串不包含指定字符串
     */
    public static boolean contains(String str1, String str2) {
        return str1 != null && str1.contains(str2);
    }

    /**
     * 判断字符串是否为空，为空则返回一个空值，不为空则返回原字符串
     *
     * @param
     * @return 判断后的字符串
     */
    public static String getString(Object obj) {
        if (obj == null) {
            return "";
        }
        String str = obj.toString();
        return isNullOrEmpty(str) ? "" : str;
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.parse(sdate);
        } catch (ParseException e) {
            return new Date(toLong(sdate) * 1000L);
        }
    }

    public static String parseTime(Date d) {
        return outyear.format(d);
    }

    public static String parseDate(Date d) {
        return date.format(d);
    }

    public static String parseDate(Date d, Date now) {
        if (isToday(d, now)) {
            return today + ampm.format(d);
        } else if (isTomorrow(d, now)) {
            return tomorrow + ampm.format(d);
        } else if (isNextYear(d, now) || isOldYear(d, now)) {
            return outyear.format(d);
        } else {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(d);
            int m1 = cal1.get(Calendar.MONTH);
            int d1 = cal1.get(Calendar.DAY_OF_MONTH);
            return (m1 + 1) + "月" + d1 + "日" + ampm.format(d);
        }
    }

    public static String getStartTime(Date d) {
        Date now = new Date(System.currentTimeMillis());
        if (isToday(d, now)) {
            return today + ampm.format(d);
        } else if (isTomorrow(d, now)) {
            return tomorrow + ampm.format(d);
        } else if (isNextYear(d, now) || isOldYear(d, now)) {
            return outyear.format(d);
        } else {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(d);
            int m1 = cal1.get(Calendar.MONTH);
            int d1 = cal1.get(Calendar.DAY_OF_MONTH);
            return (m1 + 1) + "月" + d1 + "日" + ampm.format(d);
        }
    }

    public static boolean isNextWeek(Date d, Date now) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d);
        cal2.setTime(now);
        int week1 = cal1.get(Calendar.WEEK_OF_YEAR);
        int week2 = cal2.get(Calendar.WEEK_OF_YEAR);
        return week1 == week2 + 1;
    }

    public static boolean isToday(Date d, Date now) {
        String nowDate = dateFormater2.format(now);
        String timeDate = dateFormater2.format(d);
        return nowDate.equals(timeDate);
    }

    public static boolean isTomorrow(Date d, Date now) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d);
        cal2.setTime(now);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        return day1 == day2 + 1;
    }

    public static String format(long duration) {
        return formatter.format(new Date(duration));
    }

    public static Date toDateTime(String sdate) {
        try {
            return dateFormater2.parse(sdate);
        } catch (ParseException e) {
        }
        return null;
    }

    public static String convertimeStumpToDate2(String time) {
        try {
            return dateFormater2.format(new Date(toLong(time) * 1000L));
        } catch (Exception e) {
            return null;
        }

    }

    public static String convertTimeStumpToDate(String time) {
        try {
            return dateFormater.format(new Date(toLong(time) * 1000L));
        } catch (Exception e) {
            return null;
        }
    }

    public static long getNowTime() {
        try {
            String now = dateFormater2.format(new Date());
            return dateFormater2.parse(now).getTime();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return 0;
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String toFriendlyTimeStr(String sdate) {
        Calendar post = Calendar.getInstance();
        post.setTimeInMillis(toLong(sdate) * 1000L);

        Date time = post.getTime();

        String ftime = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        // 判断是否是同一天
        String curDate = dateFormater2.format(cal.getTime());
        String paramDate = dateFormater2.format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                int min = (int) ((cal.getTimeInMillis() - time.getTime()) / 60000);
                if (min != 0)
                    ftime = min + "分钟前";
                else
                    ftime = "刚刚";
            } else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天" + dateFormater4.format(time);
        } else if (days == 2) {
            ftime = "前天" + dateFormater4.format(time);
        } else {
            ftime = dateFormater2.format(time);
        }
        // else if (days > 30 && days <= 365) {
        // int m = days / 30;
        // int d = days - m * 30;
        // if (d == 0)
        // ftime = m + "个月前";
        // else
        // ftime = m + "个月" + d + "天前";
        // } else if (days > 365) {
        // int y = days / 365;
        // ftime = y + "年前";
        // }
        return ftime;
    }

    public static String getCurrentTimeStr() {
        return getTimeStr(System.currentTimeMillis());
    }

    public static String getNextDayStr() {
        return getTimeStr(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
    }

    public static String getTimeStr(long time) {
        return dateFormater2.format(new Date(time));
    }

    public static String getCurrentDate(Date time) {
        return current.format(time);
    }

    public static String getOldDate(Date time) {
        return date.format(time);
    }

    public static String getNextDate(Date time) {
        return outyear.format(time);
    }

    public static String getTime() {
        return dateFormater3.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 以友好的方式显示时间
     *
     * @param
     * @return
     */
    public static String getChineseTime(long time) {
        if (time == 604800000) {
            return "一周";
        } else if (time == 86400000) {
            return "一天";
        }
        int yearCount = 0;
        int monthCount = 0;
        int dayCount = 0;
        int hourCount = 0;
        int minuteCount = 0;
        int secondCount = 0;
        yearCount = (int) time / (86400000 * 365);
        time = (int) time % (86400000 * 365);
        monthCount = (int) time / (86400000 * 30);
        time = (int) time % (86400000 * 30);
        dayCount = (int) time / (86400000);
        time = (int) time % (86400000);
        hourCount = (int) time / (3600000);
        time = (int) time % (3600000);
        minuteCount = (int) time / (60000);
        time = (int) time % (60000);
        secondCount = (int) time / (1000);
        String message = "";
        if (yearCount != 0) {
            message += yearCount + "年";
        }
        if (monthCount != 0) {
            message += monthCount + "月";
        }
        if (dayCount != 0) {
            message += dayCount + "天";
        }
        if (hourCount != 0) {
            message += hourCount + "小时";
        }
        if (minuteCount != 0) {
            message += minuteCount + "分钟";
        }
        if (secondCount != 0) {
            message += secondCount + "秒";
        }
        if (isNullOrEmpty(message)) {
            message = "即时";
        }
        return message;
    }

    public static String toFriendlyNumStr(long l) {
        if (l < 100)
            return l + "";
        else if (l < 1000)
            return l / 100 + "百";
        else if (l < 10000)
            return l / 1000 + "千";
        else if (l < 1000000)
            return l / 10000 + "万";
        else
            return "百万之上";
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.format(today);
            String timeDate = dateFormater2.format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str.replace("+", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static double toDouble(String str, double defValue) {
        try {
            return Double.parseDouble(str.replace("+", ""));
        } catch (Exception e) {
            return defValue;
        }
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
            try {
                return dateFormater.parse(obj).getTime() / 1000;
            } catch (ParseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toPLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
            try {
                return sdf.parse(obj).getTime();
            } catch (ParseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return 0;
    }

    /**
     */
    public static String getStringForDate(Date date) {
        try {
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * HTML化字符串
     *
     * @param str
     * @return
     */
    public static Spanned fromHtml(String str) {
        if (!isEmpty(str)) {
            return Html.fromHtml(str);
        } else
            return Html.fromHtml("");
    }

    /**
     * 判断两个字符串是否相等
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean isEquals(String a, String b) {
        return a.compareTo(b) == 0;
    }

    /**
     * 判断是否手机号
     *
     * @param inputStr
     * @return
     */
    public static boolean isPhoneNumber(String inputStr) {
        if (inputStr == null) {
            return false;
        }
        return inputStr
                .matches("^((13[0-9])|(15[^4,\\D])|(14[57])|(17[0-9])|(18[0-9]))\\d{8}$");
    }

    public static String getChineseNumber(int i) {
        if (i == 10) {
            return CHINA_NUMBER[9];
        }
        String value = String.valueOf(i);
        char[] array = value.toCharArray();
        value = "";
        for (char c : array) {
            value += CHINA_NUMBER[Integer.parseInt(String.valueOf(c)) - 1];
        }
        return value;
    }

    /**
     * 判读年份是不是闰年
     */
    public static boolean isRunNian(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * 根据 value 的值长度判断是否在前面补 0 , 用于格式化日期
     */
    public static String getFormatValue(String value) {
        if (value == null) {
            return "";
        } else {
            return value.length() == 1 ? "0" + value : value;
        }
    }

    public static String getFormatDate(Date date) {
        return dateFormater2.format(date);
    }

    public static String getOnePoint(String str) {
        if (StringUtil.isNullOrEmpty(str)) {
            return "0";
        }
        try {
            float d = Float.parseFloat(str);
            // str = str.substring(0, index + 2);
            str = String.format("%.1f", d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getDate() {
        Calendar c = Calendar.getInstance();
        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));

        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
                + mins);
        return sbBuffer.toString();
    }

    // 身份证号码验证：start

    /**
     * 功能：身份证的有效验证
     *
     * @param IDStr 身份证号
     * @return 有效：返回"" 无效：返回String信息
     * @throws ParseException
     */
    public static String IDCardValidate(String IDStr) throws ParseException {
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = {"1", "0", "x", "9", "8", "7", "6", "5", "4",
                "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2"};
        String Ai = "";
        // ================ 号码的长度 15位或18位 ================
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            errorInfo = "身份证号码长度应该为15位或18位。";
            return errorInfo;
        }
        // =======================(end)========================

        // ================ 数字 除最后以为都为数字 ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (isNumeric(Ai) == false) {
            errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
            return errorInfo;
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {
            errorInfo = "身份证生日无效。";
            return errorInfo;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                || (gc.getTime().getTime() - s.parse(
                strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
            errorInfo = "身份证生日不在有效范围。";
            return errorInfo;
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "身份证月份无效";
            return errorInfo;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "身份证日期无效";
            return errorInfo;
        }
        // =====================(end)=====================

        // ================ 地区码时候有效 ================
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "身份证地区编码错误。";
            return errorInfo;
        }
        // ==============================================

        // ================ 判断最后一位的值 ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];

        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {

            if (Ai.equals(IDStr.toLowerCase()) == false) {
                // if(modValue==2){
                // strVerifyCode="X";
                // Ai = Ai + strVerifyCode;
                // if (Ai.equals(IDStr) == true) {
                // return "";
                // }
                // }
                errorInfo = "身份证无效，不是合法的身份证号码";
                return errorInfo;
            }
        } else {
            return "";
        }
        // =====================(end)=====================
        return "";
    }

    /**
     * 功能：判断字符串是否为数字
     *
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 功能：判断字符串是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isPassword(String str) {
        Pattern pattern = Pattern.compile("[0-9]{6}");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 检查密码
     *
     * @param pass
     * @return
     */

    public static boolean checkpass(String pass) {
        return pass.length() < 6 || pass.length() > 20 || isNumOrLetter(pass);
    }

    private static boolean isNumOrLetter(String pass) {
        Pattern pat = Pattern.compile("[\\da-zA-Z]{6,20}");
        Pattern patno = Pattern.compile(".*\\d.*");
        Pattern paten = Pattern.compile(".*[a-zA-Z].*");
        Matcher mat = pat.matcher(pass);
        Matcher matno = patno.matcher(pass);
        Matcher maten = paten.matcher(pass);
        return !(mat.matches() && matno.matches() && maten.matches());
    }

    /**
     * 功能：设置地区编码
     *
     * @return Hashtable 对象
     */
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 验证日期字符串是否是YYYY-MM-DD格式
     *
     * @param str
     * @return
     */
    public static boolean isDataFormat(String str) {
        boolean flag = false;
        // String
        // regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
        String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern pattern1 = Pattern.compile(regxStr);
        Matcher isNo = pattern1.matcher(str);
        if (isNo.matches()) {
            flag = true;
        }
        return flag;
    }

    public static String getDistance(int distance) {
        if (distance < 1000) {
            return String.valueOf(1);
        }
        return String.format("%.2f", distance / 1000.00);
    }

    public static String getDuration(int duration) {
        if (duration < 3600) {
            return String.format("约%d分%n", duration / 60);
        } else if (duration > 3600 && duration < 3600 * 24) {
            int n = duration / 3600;
            return String.format("约%d小时%d分%n", duration / 3600,
                    (duration - 3600 * n) / 60);
        }
        int m = duration / (3600 * 24);
        return String.format("约%d天%d小时%n", duration / (3600 * 24),
                (duration - 3600 * m * 24) / 3600);
    }

    public static boolean isToday(long t) {
        boolean b = false;
        Date time = new Date(t);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.format(today);
            String timeDate = dateFormater2.format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    @SuppressLint("DefaultLocale")
    public static String getLen(long length) {
        if (length > 0 && length < 1024) {
            return "1kb";
        } else if (length < 1024 * 1024) {
            return length / 1024 + "kb";
        } else if (length < 1024 * 1024 * 1024) {
            return String.format("%.1fM", length / (1024 * 1024.0));
        } else if (length >= 1024 * 1024 * 1024) {
            return String.format("%.1fG", length / (1024 * 1024 * 1024.0));
        }
        return "0kb";
    }

    public static String getGreetingsTime() {
        int hour = Integer.parseInt(dateFormater4.format(new Date(System
                .currentTimeMillis())));
        if (hour > 3 && hour <= 6)
            return "凌晨好";
        if (hour > 6 && hour <= 8)
            return "早晨好";
        if (hour > 8 && hour <= 11)
            return "早上好";
        if (hour > 11 && hour <= 13)
            return "中午好";
        if (hour > 13 && hour <= 17)
            return "下午好";
        if (hour > 17 && hour <= 19)
            return "傍晚好";
        if (hour > 19 && hour < 23)
            return "晚上好";
        if (hour == 23 || (hour <= 3 && hour >= 0))
            return "深夜好";
        return null;
    }

    public static String getFriendTime(String time) {
        if (isToday(time)) {
            return dateFormater3.format(toDate(time));
        }
        return dateFormater2.format(toDate(time));
    }

    /**
     * 实现文本复制功能
     *
     * @param content
     */
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        if (Build.VERSION.SDK_INT >= 11) {
            ClipboardManager cmb = (ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(content.trim());
        } else {
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(content.trim());
        }
    }

    /**
     * 实现粘贴功能
     *
     * @param context
     * @return
     */
    public static String paste(Context context) {
        // 得到剪贴板管理器
        if (Build.VERSION.SDK_INT >= 11) {
            ClipboardManager cmb = (ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            return cmb.getText().toString().trim();
        } else {
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            return cmb.getText().toString().trim();
        }
    }

    public static boolean isNextYear(Date date, Date now) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);
        cal2.setTime(now);
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        return year1 > year2;
    }

    public static boolean isOldYear(Date date, Date now) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);
        cal2.setTime(now);
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        return year1 < year2;
    }

    public static String getYearMonth(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        int year1 = cal1.get(Calendar.YEAR);
        int month = cal1.get(Calendar.MONTH);
        return year1 + "年" + (month + 1) + "月";
    }

    public static String getMonth(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        int month = cal1.get(Calendar.MONTH);
        return CHINA_MONTH[month];
    }

    public static String getAPTime(Date time) {
        return ampm.format(time);
    }

    public static String getDateFromStart(long start) {
        long len = Math.abs(start - System.currentTimeMillis()) / (60 * 1000);
        if (len > (24 * 60 * 365)) {
            return len / (24 * 60 * 365) + "年";
        } else if (len > (24 * 60 * 30)) {
            return len / (24 * 60 * 30) + "月";
        } else if (len > (24 * 60 * 7)) {
            return len / (24 * 60 * 7) + "周";
        } else if (len > (24 * 60))
            return len / (24 * 60) + "天";
        else if (len > 60)
            return len / 60 + "小时";
        else
            return len != 0 ? len + "分钟" : "1分钟";
    }

    public static String getDurationFromStartEnd(long start, long end) {
        Date s = new Date(start);
        Date e = new Date(end);
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(s);
        cal2.setTime(e);
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 == year2) {
            return current.format(s) + "至" + current.format(e);
        }
        return outyear.format(s) + "至" + outyear.format(e);
    }

    public static String getFriendTime(long time) {
        String msg = "";
        time *= 1000;
        long m = (System.currentTimeMillis() - time) / (60 * 1000);
        if (isToday(time)) {
            if (m <= 1) {
                msg = "刚刚";
            } else if (m < 60) {
                msg = m + "分钟前";
            } else if (m >= 60) {
                String now = ampm1.format(new Date(System.currentTimeMillis()));
                String send = ampm1.format(new Date(time));
                if (now.equals(send)) {
                    msg = m / 60 + "小时前";
                } else {
                    msg = today + " " + ampm2.format(new Date(time));
                }
            }
        } else if (isYesterday(time)) {
            msg = "昨天 " + ampm2.format(new Date(time));
        } else if (m < 24 * 60 * 7) {
            msg = ampm.format(new Date(time));
        } else if (isOldYear(new Date(time), new Date())) {
            msg = date.format(new Date(time));
        } else {
            msg = current.format(new Date(time));
        }
        return msg;
    }

    private static boolean isYesterday(long time) {
        long m = System.currentTimeMillis() - time;
        if (m > 0 && !isToday(time)) {
            long h = m / (1000 * 60 * 60 * 24);
            if (h == 0) {
                return true;
            }
        }
        return false;
    }

    //时间戳转为字符串
    public static String getYearMonthDay(long time) {
        String Strtime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Strtime = sdf.format(new Date(time * 1000L));
        return Strtime;
    }

    //字符串转为年月日的时间
    public static String getTime(String time) {
        String Strtime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d;
        try {
            d = sdf.parse(time);
            long l = d.getTime();
            String str = String.valueOf(l);
            Strtime = str.substring(0, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Strtime;
    }

    /**
     * 计算汉字或字符位数
     *
     * @param str
     * @return
     */
    public static int calculateCharacterLen(String str) {
        int m = 0;
        char arr[] = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char c = arr[i];
            if ((c >= 0x0391 && c <= 0xFFE5))  //中文字符,一个中文相当于三个字符
                m = m + 3;
            else if ((c >= 0x0000 && c <= 0x00FF)) //英文字符  
                m = m + 1;
        }
        return m;
    }

    /**
     * To string string.
     *
     * @param is the is
     * @return the string
     */
    public static String toString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static int calculateLength(String etstring) {
        // 这里也可以使用getBytes,更准确嘛
        int varlength = etstring.getBytes(Charset.forName("UTF-8")).length;// 编码根据自己的需求，注意u8中文占3个字节...
        Log.d("TextChanged", "varlength = " + varlength);
        return varlength;
    }

    public static boolean isTelephone(String phone) {
        String regex = "((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    //匹配字母
    public static boolean isNumberLetterHanzi(String s) {
        String regex = "[\\a-zA-Z]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        return m.matches();
    }

    //匹配汉字
    public static boolean isLetterHanzi(String s) {//true
        String regex="[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        return m.find();
    }

    public static String toTrimAllContinueline(String content) {
        String[] contArray = content.split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = contArray.length; i < size; i++) {
            String s = contArray[i];
            if (s.trim().length() != 0) {
                sb.append(s.trim());
                if (i != size - 1) {
                    sb.append("\n\n");
                }
            }
        }
        return sb.toString();
    }

    /*判断字符串是否包含字母或数字*/
    public static boolean isLetterDigit(String str) {
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        int i = 0;
        for (i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {   //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            }
            if (Character.isLetter(str.charAt(i))) {  //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        return isDigit && isLetter;
    }


    public static final String[] ValCodeArr = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
    public static final String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
    // 身份证的最小出生日期,1900年1月1日
    private static final Date MINIMAL_BIRTH_DATE = new Date(-2209017600000L);
    private static final String BIRTH_DATE_FORMAT = "yyyyMMdd";
    private final static int NEW_CARD_NUMBER_LENGTH = 18;
    private final static int OLD_CARD_NUMBER_LENGTH = 15;
    private final static String LENGTH_ERROR = "身份证长度必须为18位！";/*15或者*/
    private final static String NUMBER_ERROR = "18位身份证都应该前17位应该都为数字！";/*15位身份证都应该为数字，*/
    private final static String DATE_ERROR = "身份证日期验证无效！";
    private final static String AREA_ERROR = "身份证地区编码错误!";
    private final static String CHECKCODE_ERROR = "身份证最后一位校验码有误！";
    //是否需要返回自动补全成的身份证
    private static boolean isNeedReturn_AutoCard = false;

    /**
     * 身份证校验
     *
     * @param idcardNumber 需要验证的身份证
     * @return 身份证无误返回传入的身份证号
     */
    public static String validate_effective(String idcardNumber) {
        String Ai = idcardNumber.trim();
//        System.out.println(Ai.length() != 15);
        if (/*Ai.length() == 15 |*/ Ai.length() != 18) {
            //如果为15位则自动补全到18位
          /*  if (Ai.length() == OLD_CARD_NUMBER_LENGTH) {
                Ai = contertToNewCardNumber(Ai);
            }*/
//        } else {
            return LENGTH_ERROR;
        }
        // 身份证号的前15,17位必须是阿拉伯数字
        for (int i = 0; i < NEW_CARD_NUMBER_LENGTH - 1; i++) {
            char ch = Ai.charAt(i);
            if (ch < '0' || ch > '9') {
                return NUMBER_ERROR;
            }
        }
        //校验身份证日期信息是否有效 ，出生日期不能晚于当前时间，并且不能早于1900年
        try {
            Date birthDate = getBirthDate(Ai);
            if (null == birthDate) {
                return DATE_ERROR;
            }
            if (!birthDate.before(new Date())) {
                return DATE_ERROR;
            }
            if (!birthDate.after(MINIMAL_BIRTH_DATE)) {
                return DATE_ERROR;
            }
            /**
             * 出生日期中的年、月、日必须正确,比如月份范围是[1,12],日期范围是[1,31]，还需要校验闰年、大月、小月的情况时，
             * 月份和日期相符合
             */
            String birthdayPart = getBirthDayPart(Ai);
            String realBirthdayPart = createBirthDateParser().format(birthDate);
            if (!birthdayPart.equals(realBirthdayPart)) {
                return DATE_ERROR;
            }
        } catch (Exception e) {
            return DATE_ERROR;
        }
        //校验地区码是否正确
        Hashtable<String, String> h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            return AREA_ERROR;
        }
        //校验身份证最后一位 身份证校验码
        if (!calculateVerifyCode(Ai).equals(String.valueOf(Ai.charAt(NEW_CARD_NUMBER_LENGTH - 1)))) {
            return CHECKCODE_ERROR;
        }
        return isNeedReturn_AutoCard == false ? idcardNumber : Ai;
    }

    /**
     * 把15位身份证号码转换到18位身份证号码<br>
     * 15位身份证号码与18位身份证号码的区别为：<br>
     * 1、15位身份证号码中，"出生年份"字段是2位，转换时需要补入"19"，表示20世纪<br>
     * 2、15位身份证无最后一位校验码。18位身份证中，校验码根据根据前17位生成
     *
     * @return
     */
    private static String contertToNewCardNumber(String oldCardNumber) {
        StringBuilder buf = new StringBuilder(NEW_CARD_NUMBER_LENGTH);
        buf.append(oldCardNumber.substring(0, 6));
        buf.append("19");
        buf.append(oldCardNumber.substring(6));
        buf.append(calculateVerifyCode(buf));
        return buf.toString();
    }

    /**
     * 计算最后一位校验码  加权值%11
     * （1）十七位数字本体码加权求和公式 S = Sum(Ai * Wi), i = 0, ... , 16 ，先对前17位数字的权求和
     * Ai:表示第i位置上的身份证号码数字值 Wi:表示第i位置上的加权因子 Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4
     * （2）计算模 Y = mod(S, 11)
     * （3）通过模得到对应的校验码 Y: 0 1 2 3 4 5 6 7 8 9 10 校验码: 1 0 X 9 8 7 6 5 4 3 2
     *
     * @param cardNumber
     * @return
     */
    private static String calculateVerifyCode(CharSequence cardNumber) {
        int sum = 0;
        for (int i = 0; i < NEW_CARD_NUMBER_LENGTH - 1; i++) {
            char ch = cardNumber.charAt(i);
            sum += (ch - '0') * Integer.parseInt(Wi[i]);
        }
        return ValCodeArr[sum % 11];
    }

    private static Date getBirthDate(String idcard) {
        Date cacheBirthDate = null;
        try {
            cacheBirthDate = createBirthDateParser().parse(getBirthDayPart(idcard));
        } catch (Exception e) {
            throw new RuntimeException("身份证的出生日期无效");
        }
        return new Date(cacheBirthDate.getTime());
    }

    private static SimpleDateFormat createBirthDateParser() {
        return new SimpleDateFormat(BIRTH_DATE_FORMAT);
    }

    private static String getBirthDayPart(String idcardnumber) {
        return idcardnumber.substring(6, 14);
    }
    public static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                Log.d("ds>>> ", "Unable to create external cache directory");
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                Log.d("ds>>> ", "Can't create \".nomedia\" file in application external cache directory");
            }
        }
        return appCacheDir;
    }

}
