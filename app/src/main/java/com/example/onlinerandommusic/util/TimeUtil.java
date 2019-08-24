package com.example.onlinerandommusic.util;

public class TimeUtil {
    public static String format(int ms){
        int s = ms / 1000; // 总秒数
        int m = s / 60; // 分钟数
        int ss = s % 60;// 除分钟数的秒数

        String res;
        if (m < 10)
            res = "0" + m + ":";
        else
            res = m + ":";
        if (ss < 10)
            res += ("0" + ss);
        else
            res += ss;

        return res;
    }
}
