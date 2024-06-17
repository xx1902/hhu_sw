package com.sw;

import com.sw.utils.Calculate;
import com.sw.utils.DataReader;
import com.sw.utils.simulateGr4j;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.sw.utils.simulateGr4j.evaluateGR4JModel;
import static com.sw.utils.simulateGr4j.simulate;

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
        //观测数据的长度
        int DATA_LENGTH = data.size();

        double[] QOBS = new double[DATA_LENGTH];
        int k = 0;
        for (String[] datum : data) {
            P.add(Double.parseDouble(datum[0]));
            E.add(Double.parseDouble(datum[1]));
            QOBS[k++] = Double.parseDouble(datum[2]) * 86.4 / AREA;
        }

        int maxDayDelay = 10;//根据参数x4计算S曲线和单位线 ，假设但危险长度UH1为10天， UH2为20天


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


        for (double x1 = 270; x1 <= 300; x1 += 0.5) {
            for (double x2 = 1.6; x2 <= 1.9; x2 += 0.05) {
                for (double x3 = 30; x3 <=35; x3 += 0.5) {
                    double NSE = 0;

                    for (double x4 = 2.23; x4 <= 2.61; x4 += 0.01) {

                        //计算SH1和SH2
                        double[] SH1 = Calculate.sh1Curve(x4, maxDayDelay);
                        double[] SH2 = Calculate.sh2Curve(x4, maxDayDelay * 2);

                        double[] UH1 = Calculate.calUH(maxDayDelay, SH1);
                        double[] UH2 = Calculate.calUH(maxDayDelay * 2, SH2);
                        double[] Q = simulate(DATA_LENGTH, x1, x2, x3, x4, UPPER_TANK_RADIO, LOWER_TANK_RADIO, maxDayDelay, UH1, UH2, Pn, En);
                        NSE = simulateGr4j.evaluateGR4JModel2(DATA_LENGTH, QOBS, Q);

                        if (NSE > 0.835) {
                            System.out.println("X1: " + x1 + " X2: " + x2 + " X3: " + x3 + "  x4: " + x4 + " NSE: " + NSE);
                        }


                    }
                }
            }

        }
    }

}


