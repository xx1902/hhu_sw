package com.sw;


import com.sw.unit.DataReader;

import java.io.File;
import java.util.List;

/**
 * @author Marchino
 * @date 2024/5/27 14:09
 * @description 读取流域径流数据，流域参数数据， 模型初始状态量数据
 * @version 1.0
 */

public class run {
    public static void main(String[] args) {
        //加载模型参数, 第一列逐日降雨量， 第二列
        List<String[]> paras = DataReader.colAndRowRead("./inputData.txt");

        //加载gr4j模型的状态变量和流域大小
        List<Double> otherParas = DataReader.colRead("./others.txt");
        double area = otherParas.get(0);//测试流域面积的大小（km2）
        double upperTankRadio = otherParas.get(1);//上层产流水库初始填充
        double lowerTankRadio = otherParas.get(2);//下层产流水库初始填充

        //TODO gr4j_Parameter参数读入

        //test
        for (String[] para : paras) {
            for (String s : para) {
                System.out.print(s + "\t\t");
            }
            System.out.println(" ");
        }
    }
}
