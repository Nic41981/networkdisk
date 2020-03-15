package com.dy.networkdisk.web.tool;

public class StringUtil {

    public static boolean isNull(String str){
        return str == null || str.length() == 0;
    }

    public static boolean inLengthRange(String str,Integer min,Integer max){
        boolean result = true;
        if (min != null){
            result = str.length() >= min;
        }
        if (max != null && result){
            result = str.length() <= max;
        }
        return result;
    }
}
