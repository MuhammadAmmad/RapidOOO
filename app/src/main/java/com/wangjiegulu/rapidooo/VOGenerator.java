package com.wangjiegulu.rapidooo;

import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOConversion;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.BOGenerator;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.PetBO;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.User_BO;

/**
 * Generate VOs from BOs
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 10/04/2018.
 */
@OOOs(suffix = VOGenerator.VO_SUFFIX, fromSuffix = BOGenerator.BO_SUFFIX, ooosPackages = {
        VOGenerator.PACKAGE_BO
}, ooos = {
        @OOO(id = "userVO", from = User_BO.class),
        @OOO(from = User_BO.class/*, suffix = VOGenerator.VO_SUFFIX_USER*/,
                fromSuffix = BOGenerator.BO_SUFFIX_USER,
                conversion = {
                        @OOOConversion(
                                fieldName = "gender",
                                targetFieldName = "genderDesc",
                                targetType = String.class,
                                conversionMethodName = "conversionGender",
                                inverseConversionMethodName = "inverseConversionGender",
                                replace = false
                        ),
                        @OOOConversion(
                                fieldName = "age",
                                targetFieldName = "ageDes",
                                targetType = String.class,
                                conversionMethodName = "conversionAge",
                                conversionMethodClass = AgeConversion.class,
                                replace = true
                        )
                }
        ),
        @OOO(from = PetBO.class,
                conversion = {
                        @OOOConversion(
                                fieldName = "ownerUser",
                                targetFieldName = "ownerUser",
                                targetTypeId = "userVO",
                                replace = true
                        )
                }
        )
})
public class VOGenerator {
    public static final String VO_SUFFIX = "VO";
    //    public static final String VO_SUFFIX_USER = "_VO";
    public static final String PACKAGE_BO = "com.wangjiegulu.rapidooo.depmodule.bll.xbo";

    public static String conversionGender(Integer gender) {
        if (null == gender) {
            return "unknown";
        }
        switch (gender) {
            case 0:
                return "female";
            case 1:
                return "male";
            default:
                return "unknown";
        }
    }

    public static Integer inverseConversionGender(String genderDesc) {
        switch (genderDesc) {
            case "male":
                return 1;
            case "female":
                return 0;
            default:
                return -1;
        }
    }

}
