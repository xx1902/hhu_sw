package com.sw.utils;

/**
 * @author Marchino
 * @date 2024/6/15 22:34
 * @description 数据计算
 */

public class Calculate {

    public double[] getPnAndEn(double P,double E){
        double Pn = 0.0;//净降雨量
        double En = 0.0;//净蒸发量
        if(P >= E){//当日降雨量大于等于当日蒸发量
            Pn = P - E;
            En = 0;
        }
        if(P < E){//当日降雨量小于当日蒸发量
            Pn = 0;
            En = E - P;
        }
        double[] PnAndEn = new double[2];
        PnAndEn[0] = Pn;
        PnAndEn[1] = En;
        return PnAndEn;
    }


    public void production_Store(double Pn,double En){//根据En，Pn分析产流水库水量S变化
        if(Pn != 0){//Pn≠0，净降雨将填充产流水库水量  calPs

        }
    }
    
    /***
     * @description 单位线计算UH
     * @param: maxDayDelay
    * @param: SH
     * @return double[]
     * @author Marchino
     * @date 23:21 2024/6/15
     */
    public double[] calUH(int maxDayDelay, double[] SH){
        double[] UH = new double[maxDayDelay];
        for (int i = 0; i < maxDayDelay; i++) {
            if (i == 0) {
                UH[i] = SH[i];
            } else {
                UH[i] = SH[i] - SH[i - 1];
            }
        }
        return UH;
        
    }

    /***
     * @description 单位线F计算
     * @param: x2 gr4j模型参数
    * @param: x3 gr4j模型参数
     * @param: R 汇流水库水量
     * @return double
     * @author Marchino
     * @date 23:23 2024/6/15
     */
    public double calF(double x2, double x3, double R){
        return Math.pow(x2 * (R / x3), 3.5);
    }


}
