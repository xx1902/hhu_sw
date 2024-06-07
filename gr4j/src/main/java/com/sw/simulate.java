package com.sw;

/**
 * @author Marchino
 * @date 2024/6/3 14:26
 * @description
 */

public class simulate {
    public double[] simulate_gr4j(int nStep,double x1,double x2,double x3,double x4,double upperTankRatio,double lowerTankRatio,int maxDayDelay,double UH1,double UH2,double[] Pn,double[] En){
        double S0 = upperTankRatio * x1;
        double R0 = upperTankRatio * x3;
        double S_temp = S0;
        double R_temp = R0;

        double[] S = new double[nStep];
        double[] R = new double[nStep];

        double[] Ps = new double[nStep];
        double[] Es = new double[nStep];
        double[] Perc = new double[nStep];
        double[] Pr = new double[nStep];
        double[] F = new double[nStep];
        double[] Qr = new double[nStep];
        double[] Qd = new double[nStep];
        double[] Q = new double[nStep];

        double[][] UH_fast = new double[nStep][maxDayDelay];
        double[][] UH_slow = new double[nStep][2*maxDayDelay];


        for (int i = 0; i < nStep; i++) {
            S[i] = S_temp;
            R[i] = R_temp;
            //使用Pn[i]和En[i]来进行判断
            if (Pn[i] != 0){
                Ps[i] = x1 * (1 - (Math.pow(2,S[i] / x1))) * Math.tanh(Pn[i] / x1) / (1 + S[i] / x1 * Math.tanh(Pn[i] / x1));
                Es[i] = 0;
            }
            if(En[i] != 0){
                Ps[i] = 0;
                Es[i] = (S[i] * (2 - (S[i] / x1)) * Math.tanh(En[i] / x1)) / (1 + (1 - S[i] / x1) * Math.tanh(En[i] / x1));
            }

            S_temp = S_temp - Es[i] + Ps[i];


            Perc[i] = S_temp * (1 - (1 + Math.pow((4.0 / 9.0 * Math.pow((S_temp / x1),4)),-0.25)));


            Pr[i] = Perc[i] + (Pn[i] - Ps[i]);

            S_temp = S_temp - Perc[i];

            //汇流计算
            double R_fast;
            double R_slow;
            R_fast = 0.9 * Pr[i];
            R_slow = 0.1 * Pr[i];

            //进行汇流计算
            if (i ==0){
                for (int j = 0; j < maxDayDelay; j++) {}
                UH_fast[i][maxDayDelay] = R_fast * UH1;
                UH_slow[i][maxDayDelay] = R_slow * UH2;
            }
            else{
                UH_fast[i][maxDayDelay] = R_fast * UH1;
                for (int j = 0; j < maxDayDelay - 1; j++) {
                    //第二时段总汇流=第二时段产流量当前汇流+第一时段产流量错峰汇流
                    UH_fast[i][j] = UH_fast[i][j] + UH_fast[i-1][j+1];
                }

                UH_slow[i][maxDayDelay] = R_slow * UH2;
                for (int j = 0; j < 2 * maxDayDelay - 1; j++) {
                    UH_slow[i][j] = UH_slow[i][j] + UH_slow[i-1][j+1];
                }
            }

            //计算地表水和地下水之间的交互
            F[i] = x2 * Math.pow((R[i] / x3),3.5);

            R_temp = Math.max(0,R_temp + UH_fast[i][1] * F[i]);

            Qr[i] = R_temp * (1 - Math.pow(Math.pow(R_temp / x3,4),-0.25));

            R_temp = R_temp - Qr[i];

            Qd[i] = Math.max(0, UH_slow[i][1] + F[i]);

            Q[i] = Qr[i] + Qd[i];

        }
        return Q;
    }
}
