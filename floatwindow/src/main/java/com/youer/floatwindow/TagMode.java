package com.youer.floatwindow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import android.support.annotation.IntDef;

/**
 * @author youer
 * @date 2022/1/24
 */
public class TagMode {
    @IntDef({Mode.REPLACE, Mode.LAST}) //限定为MAN,WOMEN
    @Retention(RetentionPolicy.SOURCE) //表示注解所存活的时间,在运行时,而不会存在. class 文件.
    public @interface Mode { //接口，定义新的注解类型
        // 替换原有相同tag的window
        int REPLACE = 1;
        // 保持原有相同tag的window
        int LAST = 2;
    }
}