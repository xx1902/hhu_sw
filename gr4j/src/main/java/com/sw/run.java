package com.sw;


import com.sw.unit.Cal;
import com.sw.unit.DataReader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marchino
 * @version 1.0
 * @date 2024/5/27 14:09
 * @description 读取流域径流数据，流域参数数据， 模型初始状态量数据
 */

public class run {
    public static void main(String[] args) {


        //加载gr4j模型的状态变量和流域大小
        List<Double> otherParas = DataReader.colRead("./others.txt");
        double area = otherParas.get(0);//测试流域面积的大小（km2）
        double upperTankRadio = otherParas.get(1);//上层产流水库初始填充
        double lowerTankRadio = otherParas.get(2);//下层产流水库初始填充

        //gr4j_Parameter参数读入
        List<Double> para = DataReader.colRead("./GR4J_Parameter.txt");
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


        int maxDayDelay = 10;//根据参数x4计算S曲线和单位线 ，假设但危险长度UH1为10天， UH2为20天
        double[] SH1 = new double[maxDayDelay];//定义第一条单位线的积累曲线
        double[] UH1 = new double[maxDayDelay];//定义第一条单位线

        double[] SH2 = new double[2 * maxDayDelay];//定义第一条单位线的积累曲线
        double[] UH2 = new double[2 * maxDayDelay];//定义第二条单位线

        //计算SH1和SH2
        for (int i = 0; i < maxDayDelay; i++) {
            SH1[i] = Cal.sh1Curve(i, x4);

        }
        for (int i = 0; i < 2 * maxDayDelay; i++) {
            SH2[i] = Cal.sh2Curve(i, x4);

        }

        //计算UH1和UH2
        for (int i = 0; i < maxDayDelay; i++) {
            if(i == 0){
                UH1[i] = SH1[i];
            }else {
                UH1[i] = SH1[i] - SH1[i - 1];
            }
        }
        for (int i = 0; i < 2 * maxDayDelay; i++) {
            if(i == 0){
                UH2[i] = SH2[i];
            }else {
                UH2[i] = SH2[i] - SH2[i - 1];
            }
            System.out.println(UH2[i]);

        }

        for (int i = 0; i < nStep; i++) {
            if (P.get(i) > E.get(i)) {//如果当日降雨量大于蒸发， 则有效降雨量为P[i] - E[i]
                Pn[i] = P.get(i) - E.get(i);
                En[i] = 0;
            } else {//有效降雨量为0, 还有E[i] - P[i]的蒸发未满足, 需要消耗土壤含水量
                Pn[i] = 0;
                En[i] = E.get(i) - P.get(i);
            }
        }


    }
}
