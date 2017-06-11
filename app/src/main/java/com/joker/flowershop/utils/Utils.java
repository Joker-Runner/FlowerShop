package com.joker.flowershop.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by joker on 5/7 0007.
 */

public class Utils {

    /**
     * 从文件中读取内容
     *
     * @param context
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String getJson(Context context, String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        AssetManager assetManager = context.getAssets();
        BufferedReader bf = new BufferedReader(new InputStreamReader(
                assetManager.open(fileName)));
        String line;
        while ((line = bf.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    /**
     * 提取市/县
     *
     * @param city
     * @return
     */
    public static String extractLocation(final String city) {
        return city.substring(0, city.length() - 1);
//        return district.contains("县") ? district.substring(0, district.length() - 1) : city.substring(0, city.length() - 1);
    }
}
