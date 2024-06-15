package com.sw;

import com.sw.utils.DataReader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marchino
 * @date 2024/6/15 22:37
 * @description 运行
 */

public class run {
    public static void main(String[] args) {

    }
    static {
        //gr4j_Parameter参数读入
        List<Double> para = DataReader.colRead("./GR4J_Parameter.txt");
        double X1 = para.get(0);//产流水库容量
        double X2 = para.get(1);//地下水交换系数
        double X3 = para.get(2);//流域水库容量
        double X4 = para.get(3);//单位线回流时间

        //加载对应流域站点的降雨、蒸散发、径流观测数据
        List<String[]> data = DataReader.colAndRowRead("./inputData.txt");
        List<Double> P = new ArrayList<>();//降雨
        List<Double> E = new ArrayList<>();//蒸散发
        List<Double> QOBS = new ArrayList<>();//径流观测值
        for (String[] datum : data) {
            P.add(Double.parseDouble(datum[0]));
            E.add(Double.parseDouble(datum[1]));
            QOBS.add(Double.parseDouble(datum[2]));
        }

        //加载各个测试流域基础信息
        List<Double> otherParas = DataReader.colRead("./others.txt");
        double AREA = otherParas.get(0);//测试流域面积的大小（km2）
        double UPPER_TANK_RADIO = otherParas.get(1);//上层产流水库初始填充
        double LOWER_TANK_RADIO = otherParas.get(2);//下层产流水库初始填充

        //观测数据的长度
        int DATA_LENGTH = data.size();
    }

}
