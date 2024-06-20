package com.sw.utils;

import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer;

/**
 * @author Marchino
 * @date 2024/6/15 23:37
 * @description gr4j模型的模拟
 */

public class simulateGr4j {
    static final double[] LOWER_BOUNDS = {10.0, -5.5, 20.0, 1.0}; // 参数下界
    static final double[] UPPER_BOUNDS = {700.0, 3.5, 400.0, 2.5}; // 参数上界
    public simulateGr4j(){

    }
    public static double[] simulate(int dataLength, double x1, double x2, double x3, double x4,
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
        double[] Qr = new double[dataLength];
        double[] Qd = new double[dataLength];
        double[] Q = new double[dataLength];

        for (int i = 0; i < dataLength; i++) {
            S[i] = tempS;
            R[i] = tempR;
            //计算PS， ES
            Ps[i] = Calculate.calPs(Pn[i], En[i], x1, S[i]);
            Es[i] = Calculate.calEs(Pn[i], En[i], x1, S[i]);

            //更新上层水库蓄水量
            tempS = S[i] - Es[i] + Ps[i];

            //计算产流水库渗透perc
            perc[i] = Calculate.calPerc(x1,tempS);

            //计算产流总量pr
            pr[i] = perc[i] + (Pn[i] - Ps[i]);

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

            //更新汇流水库水量变化
            tempR = Math.max(0, tempR + UHFast[i][1] + F[i]);

            //计算汇流水库快速流出流量
            Qr[i] = Calculate.calQr(tempR, x3);

            //再次更新汇流水库水量变化
            tempR = tempR - Qr[i];

            //计算汇流水库慢速流出流量
            Qd[i] = Math.max(0, UHSlow[i][1] + F[i]);

            Q[i] = Qr[i] + Qd[i];


        }
        return Q;
    }

    /***
     * @description
     * @param: nStep
     * @param: Qobs_mm  实测径流
     * @param: Q 模拟径流
     * @param: plot
     * @return double
     * @author Marchino
     * @date 14:03 2024/6/17
     */
    public static double evaluateGR4JModel(int nStep, double[] Q, double[] Qobs_mm) {
        // 精度评估
        int count = 0;  // 计数器：记录总天数
        double Q_accum = 0.0;  // 记录累计径流量
        double Q_ave = 0.0;  // 记录平均径流量
        double NSE = 0.0;  // 记录纳什效率系数
        double Q_diff1 = 0.0;
        double Q_diff2 = 0.0;

        for (int i = 365; i < nStep; i++) {
            count++;
            Q_accum += Qobs_mm[i];
        }

        Q_ave = Q_accum / count;  // 计算观测流量平均值

        for (int i = 365; i < nStep; i++) {
            Q_diff1 += Math.pow(Q[i] - Qobs_mm[i], 2);  // 计算Nash-Sutcliffe指数分子
            Q_diff2 += Math.pow(Q[i] - Q_ave, 2);  // 计算Nash-Sutcliffe指数分母
        }

        NSE = 1 - Q_diff1 / Q_diff2;

        return NSE;
    }

    public static double evaluateGR4JModel2(int nStep, double[] Qobs_mm, double[] Q) {
        int count = 0;
        double Q_accum = 0.0;
        double Q_ave = 0.0;
        double NSE = 0.0;
        double Q_diff1 = 0.0;
        double Q_diff2 = 0.0;

        for (int i = 365; i < nStep; i++) {
            count++;
            Q_accum += Qobs_mm[i];
        }

        Q_ave = Q_accum / count;

        for (int i = 365; i < nStep; i++) {
            Q_diff1 += Math.pow(Qobs_mm[i] - Q[i], 2);
            Q_diff2 += Math.pow(Qobs_mm[i] - Q_ave, 2);
        }

        NSE = 1 - Q_diff1 / Q_diff2;

        return NSE;
    }

}
