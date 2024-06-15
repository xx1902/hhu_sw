package com.sw;

/**
 * @author Marchino
 * @date 2024/6/3 14:26
 * @description
 */

public class simulate {
    public double[] simulateGr4j(int nStep, double x1, double x2, double x3, double x4, double upperTankRatio, double lowerTankRatio, int maxDayDelay, double[] UH1, double[] UH2, double[] Pn, double[] En){
        double S0 = upperTankRatio * x1;
        double tempS = S0;
        double[] S= new double[nStep];
        double[] Ps = new double[nStep];
        double[] Es = new double[nStep];
        double[] Pr = new double[nStep];
        double RFast, RSlow;
        double[][] UHFast = new double[nStep][maxDayDelay];
        double[][] UHSlow = new double[nStep][maxDayDelay];
        double tempR = 0;
        double[] Qr = new double[nStep];
        double[] Qd = new double[nStep];
        double[] Q = new double[nStep];
        double[] F = new double[nStep];
        double[] R = new double[nStep];

        double[] perc = new double[nStep];
        for (int i = 0; i < nStep; i++) {
            S[i] = tempS;
            R[i] = tempR;
            if (Pn[i] != 0) {
                Pn[i] = x1 * (1 - (Math.pow(S[i] / x1, 2))) * Math.tanh(Pn[i] / x1) / (1 + S[i] / x1 * Math.tanh(Pn[i] / x1));
                Es[i] = 0;
            }
            if (En[i] != 0) {
                Ps[i] = 0;
                Es[i] = (S[i] * (2 - (S[i] / x1)) * Math.tanh(En[i] / x1)) / (1 + (1 - S[i] / x1) * Math.tanh(En[i] / x1));
            }

            tempS = tempS - Es[i] + Ps[i];
            perc[i] = tempS * Math.pow(1 - (1 + (4.0 / 9.0 * Math.pow(tempS / x1, 4))), -0.25);
            Pr[i] = perc[i] + (Pn[i] - Ps[i]);

            tempS = tempS - perc[i];

            F[i] = Math.pow(x2 * (R[i] / x3), 3.5);

            RFast = 0.9 * Pr[i];
            RSlow = 0.1 * Pr[i];

            if (i == 0) {
                for (int j = 0; j < maxDayDelay; j++) {
                    UHFast[0][j] = RFast * UH1[j];
                    UHSlow[0][j] = RSlow * UH2[j];
                }
            } else {
                for (int j = 0; j < maxDayDelay - 1; j++) {
                    UHFast[i][j] = RFast * UH1[j];
                    UHFast[i][j] += UHFast[i - 1][j + 1];
                }
                for (int j = 0; j < maxDayDelay * 2 - 1; j++) {
                    UHSlow[i][j] = RSlow * UH2[j];
                    UHSlow[i][j] += UHSlow[i - 1][j + 1];
                }
            }

//            tempR = Math.max(0, tempR + UHFast[i, 1] + F[i]);
//            Qr[i] =  tempR * (1 - Math.pow(1 + Math.pow(tempR / x3, 4), -0.25 ));
//            Qd[i] = Math.max(0, UHSlow[i, 1] + F[i]);
//            Q[i] = Qr[i] + Qd[i];

            tempR = Math.max(0, tempR + UHFast[i][1] + F[i]);
            Qr[i] = tempR * (1 - Math.pow(1 + Math.pow(tempR / x3, 4), -0.25));
            Qd[i] = Math.max(0, UHSlow[i][1] + F[i]);
            Q[i] = Qr[i] + Qd[i];


        }

        return Q;
    }
}
