package com.sw.utils;

import com.sw.unit.Cal;
import com.sw.unit.DataReader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marchino
 * @date 2024/6/15 22:34
 * @description 数据计算
 */

public class Calculate {

    public double[][] getPnAndEn(){
        //加载gr4j模型的状态变量和流域大小
        List<Double> otherParas = com.sw.unit.DataReader.colRead("./others.txt");
        double area = otherParas.get(0);//测试流域面积的大小（km2）
        double upperTankRadio = otherParas.get(1);//上层产流水库初始填充
        double lowerTankRadio = otherParas.get(2);//下层产流水库初始填充

        //gr4j_Parameter参数读入
        List<Double> para = com.sw.unit.DataReader.colRead("./GR4J_Parameter.txt");
        double x1 = para.get(0);//产流水库容量
        double x2 = para.get(1);//地下水交换系数
        double x3 = para.get(2);//流域水库容量
        double x4 = para.get(3);//单位线回流时间

        //加载Gr4j模型参数,
        List<String[]> data = DataReader.colAndRowRead("./inputData.txt");
        List<Double> P = new ArrayList<>();
        List<Double> E = new ArrayList<>();
        List<Double> Qobs = new ArrayList<>();

        //将数据存到集合中
        for (String[] datum : data) {
            P.add(Double.parseDouble(datum[0]));
            E.add(Double.parseDouble(datum[1]));
            Qobs.add(Double.parseDouble(datum[2]));
        }

        int nStep = data.size();//观测数据的长度

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


    public void production_Store(double[][] PnAndEn,double S,double X1){
        //获取Pn和En
        double Pn = PnAndEn[0][];
        double En = PnAndEn[1][];
        double Ps;//产流水库增加水量
        double Es;//产流水库消耗水量
        if(Pn != 0){//Pn≠0，净降雨将填充产流水库水量  calPs

        }
    }
}
