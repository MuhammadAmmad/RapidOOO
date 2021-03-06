package com.wangjiegulu.rapidooo;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public class AgeConversion {

    public static String conversionAge(Integer age) {
        if (null == age || age < 0) {
            return "unknown";
        }
        return age + " years old";
    }

    public static Integer inverseConversionAge(String ageDesc) {
        if (null == ageDesc) {
            return -1;
        }
        return Integer.valueOf(ageDesc.split(" ")[0]);
    }

}
