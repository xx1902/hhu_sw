package com.sw.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Marchino
 * @date 2024/6/15 22:34
 * @description 数据读取
 */

public class DataReader {
    /***
     * @description 读取参数格式如:   1.5	6.5	0.9393
     * @param: filePath
     * @return java.util.List<java.lang.String[]>
     * @author Marchino
     * @date 22:51 2024/6/15
     */
    public static List<String[]> colAndRowRead(String filePath) {
        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\t");
                data.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    /***
     * @description 读取参数, 格式如
     *                      120.5
     *                      0.6
     *                      0.7
     * @param: filePath
     * @return java.util.List<java.lang.Double>
     * @author Marchino
     * @date 22:53 2024/6/15
     */
    public static List<Double> colRead(String filePath) {
        List<Double> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                double value = Double.parseDouble(line);
                data.add(value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
