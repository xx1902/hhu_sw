package com.sw.utils;

import com.sw.unit.DataReader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marchino
 * @date 2024/6/15 22:34
 * @description 数据计算
 */

public class Calculate {

    public double[][] getPnAndEn(int nStep,List<Double> P, List<Double> E){
//        //加载gr4j模型的状态变量和流域大小
//        List<Double> otherParas = DataReader.colRead("./others.txt");
//        double area = otherParas.get(0);//测试流域面积的大小（km2）
//        double upperTankRadio = otherParas.get(1);//上层产流水库初始填充
//        double lowerTankRadio = otherParas.get(2);//下层产流水库初始填充
//
//        //gr4j_Parameter参数读入
//        List<Double> para = DataReader.colRead("./GR4J_Parameter.txt");
//        double x1 = para.get(0);//产流水库容量
//        double x2 = para.get(1);//地下水交换系数
//        double x3 = para.get(2);//流域水库容量
//        double x4 = para.get(3);//单位线回流时间
//
//        //加载Gr4j模型参数,
//        List<String[]> data = DataReader.colAndRowRead("./inputData.txt");
//        List<Double> P = new ArrayList<>();
//        List<Double> E = new ArrayList<>();
//        List<Double> Qobs = new ArrayList<>();
//
//        //将数据存到集合中
//        for (String[] datum : data) {
//            P.add(Double.parseDouble(datum[0]));
//            E.add(Double.parseDouble(datum[1]));
//            Qobs.add(Double.parseDouble(datum[2]));
//        }
//
//        int nStep = data.size();//观测数据的长度

        double[] Pn = new double[nStep];//存储有效降雨量
        double[] En = new double[nStep];//存储剩余的蒸发能力


        for (int i = 0; i < nStep; i++) {
            if (P.get(i) > E.get(i)) {//如果当日降雨量大于蒸发， 则有效降雨量为P[i] - E[i]
                Pn[i] = P.get(i) - E.get(i);
                En[i] = 0;
            } else {//有效降雨量为0, 还有E[i] - P[i]的蒸发未满足, 需要消耗土壤含水量
                Pn[i] = 0;
                En[i] = E.get(i) - P.get(i);
            }
        }

        double[][] PnAndEn = new double[2][nStep];//使用二维数组来存放Pn[]和En[]
        for (int i = 0; i < nStep; i++) {
            PnAndEn[0][i] = Pn[i];
        }
        for (int i = 0; i < nStep; i++) {
            PnAndEn[1][i] = En[i];
        }
        return PnAndEn;
    }

    /**
     * @param Pn 净降雨量
     * @param En 净蒸发量
     * @param x1 产流水库容量
     * @param S  产流水库逐日水量
     * @return
     */
    public double CalPs(double Pn, double En, double x1, double S) {
        double Ps;
        if(En == 0){
            Ps = x1 * (1 - Math.pow((S / x1),2)) * Math.tanh(Pn / x1) / (1 + S / x1 * Math.tanh(Pn / x1));
        }else {
            Ps = 0;
        }
        return Ps;
    }


    /**
     * @param Pn 净降雨量
     * @param En 净蒸发量
     * @param x1 产流水库容量
     * @param S  产流水库逐日水量
     * @return
     */
    public double CalEs(double Pn, double En, double x1, double S) {
        double Es;
        if(Pn == 0){
            Es = (S * (2 - (S / x1)) * Math.tanh(En / x1)) / (1 + (1 - S / x1) * Math.tanh(En / x1));
        }else{
            Es = 0;
        }
        return Es;
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

    /***
     * @description 汇流计算Qr
     * @param: tempR
     * @param: x3
     * @return double
     * @author Marchino
     * @date 23:29 2024/6/15
     */
    public double calQr(double tempR, double x3){
        return tempR * (1 - Math.pow(1 + Math.pow(tempR / x3, 4), -0.25));
    }

    public double calPs(){
        if(en = 0){
            return 0
        }
    }



}
