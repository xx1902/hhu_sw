package com.sw;

import com.sw.utils.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Marchino
 * @date 2024/6/15 22:37
 * @description 运行
 */

public class run {
    public static void main(String[] args) {

        Result result1 = getResult("./data/demo_124002A/");
        extracted(result1.maxDayDelay, result1.DATA_LENGTH, result1.UPPER_TANK_RADIO, result1.LOWER_TANK_RADIO,
                result1.Pn, result1.En, result1.QOBS, "output124002A.xlsx");

        Result result2 = getResult("./data/demo_136006A/");
        extracted(result2.maxDayDelay, result2.DATA_LENGTH, result2.UPPER_TANK_RADIO, result2.LOWER_TANK_RADIO, result2.Pn, result2.En, result2.QOBS, "output136006A.xlsx");

        Result result3 = getResult("./data/demo_146010A/");
        extracted(result3.maxDayDelay, result3.DATA_LENGTH, result3.UPPER_TANK_RADIO, result3.LOWER_TANK_RADIO, result3.Pn, result3.En, result3.QOBS, "output146010A.xlsx");

        Result result4 = getResult("./data/demo_234203/");
        extracted(result4.maxDayDelay, result4.DATA_LENGTH, result4.UPPER_TANK_RADIO, result4.LOWER_TANK_RADIO, result4.Pn, result4.En, result4.QOBS, "output234203A.xlsx");

        Result result5 = getResult("./data/demo_312061/");//X
        extracted(result5.maxDayDelay, result5.DATA_LENGTH, result5.UPPER_TANK_RADIO, result5.LOWER_TANK_RADIO, result5.Pn, result5.En, result5.QOBS, "output312061.xlsx");

        Result result6 = getResult("./data/demo_314214/");
        extracted(result6.maxDayDelay, result6.DATA_LENGTH, result6.UPPER_TANK_RADIO, result6.LOWER_TANK_RADIO, result6.Pn, result6.En, result6.QOBS, "output314214.xlsx");

        Result result7 = getResult("./data/demo_401017/");
        extracted(result7.maxDayDelay, result7.DATA_LENGTH, result7.UPPER_TANK_RADIO, result7.LOWER_TANK_RADIO, result7.Pn, result7.En, result7.QOBS, "output401017.xlsx");

        Result result8 = getResult("./data/demo_401217/");//X
        extracted(result8.maxDayDelay, result8.DATA_LENGTH, result8.UPPER_TANK_RADIO, result8.LOWER_TANK_RADIO, result8.Pn, result8.En, result8.QOBS, "output401217.xlsx");
//
        Result result9 = getResult("./data/demo_405263/");//X
        extracted(result9.maxDayDelay, result9.DATA_LENGTH, result9.UPPER_TANK_RADIO, result9.LOWER_TANK_RADIO, result9.Pn, result9.En, result9.QOBS, "output405263.xlsx");

        Result result10 = getResult("./data/demo_919005A/");
        extracted(result10.maxDayDelay, result10.DATA_LENGTH, result10.UPPER_TANK_RADIO, result10.LOWER_TANK_RADIO, result10.Pn, result10.En, result10.QOBS, "output919005A.xlsx");





    }

    private static Result getResult(String filePath) {
        //加载各个测试流域基础信息
        List<Double> otherParas = DataReader.colRead(filePath+ "others.txt");
        double AREA = otherParas.get(0);//测试流域面积的大小（km2）
        double UPPER_TANK_RADIO = otherParas.get(1);//上层产流水库初始填充
        double LOWER_TANK_RADIO = otherParas.get(2);//下层产流水库初始填充

        //加载对应流域站点的降雨、蒸散发、径流观测数据
        List<String[]> data = DataReader.colAndRowRead(filePath + "inputData.txt");
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
        Result result = new Result(UPPER_TANK_RADIO, LOWER_TANK_RADIO, DATA_LENGTH, QOBS, maxDayDelay, Pn, En);
        return result;
    }

    private static class Result {
        public final double UPPER_TANK_RADIO;
        public final double LOWER_TANK_RADIO;
        public final int DATA_LENGTH;
        public final double[] QOBS;
        public final int maxDayDelay;
        public final double[] Pn;
        public final double[] En;

        public Result(double UPPER_TANK_RADIO, double LOWER_TANK_RADIO, int DATA_LENGTH, double[] QOBS, int maxDayDelay, double[] Pn, double[] En) {
            this.UPPER_TANK_RADIO = UPPER_TANK_RADIO;
            this.LOWER_TANK_RADIO = LOWER_TANK_RADIO;
            this.DATA_LENGTH = DATA_LENGTH;
            this.QOBS = QOBS;
            this.maxDayDelay = maxDayDelay;
            this.Pn = Pn;
            this.En = En;
        }
    }

    private static void extracted(int maxDayDelay, int DATA_LENGTH, double UPPER_TANK_RADIO, double LOWER_TANK_RADIO, double[] Pn, double[] En, double[] QOBS, String filePath) {
        WriteExcel writeExcel = new WriteExcel();
        List<DataRow> dataRows = new ArrayList<>();
        for (double x1 = 10; x1 <= 700.1; x1 += 10) {
            System.out.println(filePath + "   x1 = " + x1);
            for (double x2 = -5.5; x2 <= 3.51; x2 += 1) {
                for (double x3 = 20; x3 <=400.1; x3 += 10) {
                    double NSE = 0;

                    for (double x4 = 1.0; x4 <= 2.51; x4 += 0.4) {

                        //计算SH1和SH2
                        double[] SH1 = Calculate.sh1Curve(x4, maxDayDelay);
                        double[] SH2 = Calculate.sh2Curve(x4, maxDayDelay * 2);

                        double[] UH1 = Calculate.calUH(maxDayDelay, SH1);
                        double[] UH2 = Calculate.calUH(maxDayDelay * 2, SH2);
                        double[] Q = simulateGr4j.simulate(DATA_LENGTH, x1, x2, x3, x4, UPPER_TANK_RADIO, LOWER_TANK_RADIO, maxDayDelay, UH1, UH2, Pn, En);
                        NSE = simulateGr4j.evaluateGR4JModel2(DATA_LENGTH, QOBS, Q);

                        if (NSE > 0.79) {
                            break;
                        }
                    }
                    if (NSE <= 0.79) {
                        continue;
                    }

                    for (double x4 = 2.0; x4 <= 2.51 ; x4 += 0.1) {
                        //计算SH1和SH2
                        double[] SH1 = Calculate.sh1Curve(x4, maxDayDelay);
                        double[] SH2 = Calculate.sh2Curve(x4, maxDayDelay * 2);

                        double[] UH1 = Calculate.calUH(maxDayDelay, SH1);
                        double[] UH2 = Calculate.calUH(maxDayDelay * 2, SH2);
                        double[] Q = simulateGr4j.simulate(DATA_LENGTH, x1, x2, x3, x4, UPPER_TANK_RADIO, LOWER_TANK_RADIO, maxDayDelay, UH1, UH2, Pn, En);
                        NSE = simulateGr4j.evaluateGR4JModel2(DATA_LENGTH, QOBS, Q);
                        if (NSE > 0.8){
                            dataRows.add(new DataRow(x1, x2, x3, x4, NSE));
                        }
                    }

                }
            }


        }
        writeExcel.writeExcel(dataRows, filePath);
    }

}


