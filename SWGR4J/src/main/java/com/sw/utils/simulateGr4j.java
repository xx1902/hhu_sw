package com.sw.utils;

/**
 * @author Marchino
 * @date 2024/6/15 23:37
 * @description gr4j模型的模拟
 */

public class simulateGr4j {
    public simulateGr4j(){

    }
    public static double[] Gr4jSimulate(int dataLength, double x1, double x2, double x3, double x4,
                                        double upperTankRatio, double lowerTankRatio, int maxDayDelay,
                                        double[] UH1, double[] UH2, double[] Pn, double[] En){
        double S0 = upperTankRatio * x1; //产流水库初始土壤含水量=比例*产流水库容量
        double R0 = lowerTankRatio * x3; //汇流水库初始土壤含水量=比例*汇流水库容量
        double tempS = S0;  //用S_TEMP存储当前产流水库储量
        double tempR = R0; //用R_TEMP存储当前汇流水库储量
        double[] S = new double[dataLength]; //产流水库逐日水量
        double[] R = new double[dataLength]; //汇流水库逐日水量
        double[] Ps = new double[dataLength];
        double[] Es = new double[dataLength];
        double[] perc = new double[dataLength];
        double[] pr = new double[dataLength];
        double[] F = new double[dataLength];
        double[][] UHFast = new double[dataLength][maxDayDelay];
        double[][] UHSlow = new double[dataLength][maxDayDelay * 2];

        for (int i = 0; i < dataLength; i++) {
            S[i] = tempS;
            R[i] = tempR;
            //TODO 计算PS， ES

            //更新上层水库蓄水量
            tempS = S[i] - Es[i] + Ps[i];

            //TODO 计算产流水库渗透perc

            //TODO 计算产流总量pr

            //更新当前产流水库水量，作为次日产流水库水量
            tempS = tempS - perc[i];


            //汇流计算
            F[i] = Calculate.calF(x2, x3, R[i]);

            //计算地表水汇流，将产流量按照90%(快速)和10%(慢速)划分
            //快速地表径流汇流使用单位线UH1；慢速地表径流汇流使用单位线UH2
            double tempRFast = 0.9 * pr[i];
            double tempRSlow = 0.1 * pr[i];
            if (i == 0) {
                for (int j = 0; j < maxDayDelay; j++) {
                    UHFast[0][j] = tempRFast * UH1[j];//第1时段产流量在时间上的分配
                    UHSlow[0][j] = tempRSlow * UH2[j];// 第1时段产流量在时间上的分配
                }
            } else {
                for (int j = 0; j < maxDayDelay - 1; j++) {
                    UHFast[i][j] = tempRFast * UH1[j];
                    UHFast[i][j] += UHFast[i - 1][j + 1];
                }
                for (int j = 0; j < maxDayDelay * 2 - 1; j++) {
                    UHSlow[i][j] = tempRSlow * UH2[j];
                    UHSlow[i][j] += UHSlow[i - 1][j + 1];
                }
            }





        }
        return null;
    }
}
