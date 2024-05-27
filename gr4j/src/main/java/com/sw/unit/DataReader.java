package com.sw.unit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataReader {

    /***
     * @description 读取参数
     * @param: filePath
     * @return java.util.List<java.lang.String [ ]>
     * @author Marchino
     * @date 14:17 2024/5/27
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