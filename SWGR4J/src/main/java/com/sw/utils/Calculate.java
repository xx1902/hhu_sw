package com.sw.utils;

/**
 * @author Marchino
 * @date 2024/6/15 22:34
 * @description 数据计算
 */

public class Calculate {

    public double[] getPnAndEn(double P,double E){
        double Pn = 0.0;
        double En = 0.0;
        if(P > E){
            Pn = P - E;
            En = 0;
        }
        if(P < E){
            Pn = 0;
            En = E - P;
        }
        double[] PnAndEn = new double[2];
        PnAndEn[0] = Pn;
        PnAndEn[1] = En;
        return PnAndEn;
    }

}
