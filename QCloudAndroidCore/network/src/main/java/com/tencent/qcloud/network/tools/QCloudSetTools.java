package com.tencent.qcloud.network.tools;

import android.text.TextUtils;
import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudSetTools {

    public static String joinSemicolon(Set<String> values) {

        if (values == null) {
            return "";
        }

        // 这里也需要先按字典顺序进行排序
        Set<String> set = new TreeSet<>(values);

        Log.i("TAG", set.toString());
        StringBuilder str  = new StringBuilder();
        for (String value : set) {
            if (!TextUtils.isEmpty(str.toString())) {
                str.append(";");
            }
            str.append(value);
        }

        return str.toString();
    }

    /**
     * 将set中所有的值转化为小写
     *
     * @param set
     */
    public static Set<String> getLowerCase(Set<String> set) {


        if (set != null && set.size() > 0) {

            Set<String> lowerSet = new HashSet<>();

            for (String key : set) {
                if (key != null) {
                    lowerSet.add(key.toLowerCase());

                }
            }

            return lowerSet;
        }

        return null;
    }
}
