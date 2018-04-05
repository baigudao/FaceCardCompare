//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cvr.device;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

import static com.taisau.facecardcompare.util.Constant.LIB_DIR;

public class Utility {
    static final String[] nation = new String[]{"汉", "蒙古", "回", "藏", "维吾尔", "苗", "彝", "壮", "布依", "朝鲜", "满", "侗", "瑶", "白", "土家", "哈尼", "哈萨克", "傣", "黎", "傈僳", "佤", "畲", "高山", "拉祜", "水", "东乡", "纳西", "景颇", "克尔克孜", "土", "达斡尔", "仫佬", "羌", "布朗", "撒拉", "毛南", "仡佬", "锡伯", "阿昌", "普米", "塔吉克", "怒", "乌兹别克", "俄罗斯", "鄂温克", "德昂", "保安", "裕固", "京", "塔塔尔", "独龙", "鄂伦春", "赫哲", "门巴", "珞巴", "基诺", "穿青衣", "其他", "外国血统中国籍人士"};

    public Utility() {
    }

    @SuppressLint({"DefaultLocale"})
    protected static String AnalyzeSAM(byte[] sambuffer) {
        byte[] samdate = new byte[4];
        System.arraycopy(sambuffer, 4, samdate, 0, 4);
        byte[] samtenid = new byte[4];
        System.arraycopy(sambuffer, 8, samtenid, 0, 4);
        String samid = String.format("%02d.%02d-%08d-%010d", Byte.valueOf(sambuffer[0]), Byte.valueOf(sambuffer[2]), Long.valueOf(getLong(samdate)), Long.valueOf(getLong(samtenid)));
        return samid;
    }

    protected static void PersonInfoUtoG(byte[] cCardData, IDCardInfo ici) throws Exception {
        byte[] newmsg = new byte[cCardData.length + 2];
        newmsg[0] = -1;
        newmsg[1] = -2;

        for(int str = 0; str < cCardData.length; ++str) {
            newmsg[str + 2] = cCardData[str];
        }

        String var23 = new String(newmsg, "UTF-16");
        String Name = var23.substring(0, 15).replace("\u0000", "").trim();
        ici.setPeopleName(Name);
        String Sex = var23.substring(15, 16).replace("\u0000", "").trim();
        ici.setSex(Sex.equals("1")?"男":"女");
        String people = var23.substring(16, 18).replace("\u0000", "").trim();
        people = nation[Integer.parseInt(people) - 1];
        ici.setPeople(people);
        int birthYeah = Integer.parseInt(var23.substring(18, 22).replace("\u0000", "").trim());
        int birthMonth = Integer.parseInt(var23.substring(22, 24).replace("\u0000", "").trim());
        int birthDay = Integer.parseInt(var23.substring(24, 26).replace("\u0000", "").trim());
        Calendar cl = Calendar.getInstance();
        cl.clear();
        cl.set(birthYeah, birthMonth - 1, birthDay);
        Date BirthDay = cl.getTime();
        ici.setBirthDay(BirthDay);
        String addr = var23.substring(26, 61).replace("\u0000", "").trim();
        ici.setAddr(addr);
        String id = var23.substring(61, 79).replace("\u0000", "").trim();
        ici.setIDCard(id);
        String dep = var23.substring(79, 94).replace("\u0000", "").trim();
        ici.setDepartment(dep);
        String sDateYeah = var23.substring(94, 98).replace("\u0000", "").trim();
        String sDateMonth = var23.substring(98, 100).replace("\u0000", "").trim();
        String sDateDay = var23.substring(100, 102).replace("\u0000", "").trim();
        String StrartDate = String.format("%s.%s.%s", sDateYeah, sDateMonth, sDateDay);
        ici.setStrartDate(StrartDate);
        String eDateYeah = var23.substring(102, 106).replace("\u0000", "").trim();
        String eDateMonth = var23.substring(106, 108).replace("\u0000", "").trim();
        String eDateDay = var23.substring(108, 110).replace("\u0000", "").trim();
        String EndDate = String.format("%s.%s.%s", eDateYeah, eDateMonth, eDateDay);
        ici.setEndDate(EndDate);
    }

    protected static int PersonInfoPic(String filepath, byte[] wltdata, byte[] bmpdata) throws Exception {
        FileInputStream fis = null;
        FileOutputStream fos=null;
        boolean ret = true;

        try {
            int ret1 = IDCReaderSDK.getInstance().Init(LIB_DIR);
            if(ret1 != 0) {
                return -1;
            }

            ret1 = IDCReaderSDK.getInstance().unpack(wltdata);
            if(ret1 == 1) {
                File file=new File(filepath);
                if (!file.exists())
                    file.createNewFile();
                fis = new FileInputStream(LIB_DIR+"/zp.bmp");
                fos = new FileOutputStream(filepath);
                /*bmpdata = new byte[fis.available()];
                fis.read(bmpdata);*/
                int byteread = 0;
                byte[] buffer = new byte[1024];
                while (( byteread = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteread);
                }
                return 0;
            }
        } catch (Exception var9) {
            return -3;
        } finally {
            if(fis != null) {
                fis.close();
            }

        }

        return -2;
    }

    private static long getLong(byte[] buf) {
        long i = 0L;
        long tmp = 0L;

        for(int j = 0; j < buf.length; ++j) {
            tmp = (long)((buf[j] & 255) << j * 8);
            i |= tmp;
        }

        return i;
    }
}
