package com.sw;

import com.sw.utils.Calculate;
import com.sw.utils.DataReader;
import com.sw.utils.simulateGr4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marchino
 * @date 2024/6/15 22:37
 * @description 运行
 */

public class run {
    public static void main(String[] args) {
        //gr4j_Parameter参数读入
        List<Double> para = DataReader.colRead("./GR4J_Parameter.txt");
        double X1 = para.get(0);//产流水库容量
        double X2 = para.get(1);//地下水交换系数
        double X3 = para.get(2);//流域水库容量
        double X4 = para.get(3);//单位线回流时间

        //加载各个测试流域基础信息
        List<Double> otherParas = DataReader.colRead("./others.txt");
        double AREA = otherParas.get(0);//测试流域面积的大小（km2）
        double UPPER_TANK_RADIO = otherParas.get(1);//上层产流水库初始填充
        double LOWER_TANK_RADIO = otherParas.get(2);//下层产流水库初始填充

        //加载对应流域站点的降雨、蒸散发、径流观测数据
        List<String[]> data = DataReader.colAndRowRead("./inputData.txt");
        List<Double> P = new ArrayList<>();//降雨
        List<Double> E = new ArrayList<>();//蒸散发
        List<Double> QOBS = new ArrayList<>();//径流观测值
        for (String[] datum : data) {
            P.add(Double.parseDouble(datum[0]));
            E.add(Double.parseDouble(datum[1]));
            QOBS.add(Double.parseDouble(datum[2]) * 86.4 / AREA);
        }



        //观测数据的长度
        int DATA_LENGTH = data.size();

        int maxDayDelay = 10;//根据参数x4计算S曲线和单位线 ，假设但危险长度UH1为10天， UH2为20天

        //计算SH1和SH2
        double[] SH1 = Calculate.sh1Curve(X4, maxDayDelay);
        double[] SH2= Calculate.sh2Curve(X4, maxDayDelay * 2);

        double[] UH1 = Calculate.calUH(maxDayDelay, SH1);
        double[] UH2 = Calculate.calUH(maxDayDelay * 2, SH2);

        double[] Pn = new double[DATA_LENGTH];//存储有效降雨量
        double[] En = new double[DATA_LENGTH];//存储剩余的蒸发能力

        for (int i = 0; i < DATA_LENGTH; i++) {
            if (P.get(i) > E.get(i)) {//如果当日降雨量大于蒸发， 则有效降雨量为P[i] - E[i]
                Pn[i] = P.get(i) - E.get(i);
                En[i] = 0;
            } else {//有效降雨量为0, 还有E[i] - P[i]的蒸发未满足, 需要消耗土壤含水量
                Pn[i] = 0;
                En[i] = E.get(i) - P.get(i);
            }
        }
        double[] simulate = simulateGr4j.simulate(DATA_LENGTH, X1, X2, X3, X4, UPPER_TANK_RADIO, LOWER_TANK_RADIO, 10, UH1, UH2, Pn, En);
        for (double v : simulate) {
            System.out.println(v);
        }
    }


}
