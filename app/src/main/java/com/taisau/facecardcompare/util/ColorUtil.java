package com.taisau.facecardcompare.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ds  on 2017/1/18 13:44
 */

public class ColorUtil {
    /**
     * Get the random color.
     *
     * @return
     */
    public static String getRandomColor() {
        List<String> colorList = new ArrayList<String>();
        colorList.add("#303F9F");
        colorList.add("#FF4081");
        colorList.add("#59dbe0");
        colorList.add("#f57f68");
        colorList.add("#87d288");
        colorList.add("#990099");
        colorList.add("#7baaf7");
        colorList.add("#4dd0e1");
        colorList.add("#4db6ac");
        colorList.add("#aed581");
        colorList.add("#fdd835");
        colorList.add("#f2a600");
        colorList.add("#ff8a65");
        colorList.add("#f48fb1");
        colorList.add("#7986cb");
        colorList.add("#FF69B4");
        colorList.add("#a934c7");
        colorList.add("#c77034");
        colorList.add("#c7b334");
        colorList.add("#34c7ba");
        return colorList.get((int) (Math.random() * colorList.size()));
    }
}
