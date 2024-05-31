package com.sw.unit;

/**
 * @author Marchino
 * @date 2024/5/31 15:12
 * @description
 */

public class Cal {
    public static double pow(double c, double x){
        return Math.pow(x, c);
    }
    public static double sh1Curve(double t, double x4){
        double sh = 0;
        if (t <= 0){
            sh = 0.0;
        } else if (t < x4) {
            sh = pow(2.5, (t / x4));
        }else if (t >= x4){
            sh = 1.0;
        }
        return sh;
    }

    public static double sh2Curve(int t, double x4){
        double sh = 0.0;
        if (t <= 0.0){
            sh = 0.0;
        }else if (t <= x4){
            sh = 0.5 * pow(2.5, t / x4);
        }else if (t < 2 * x4){
            sh = 1 - 0.5 * pow(2.5, (2 - t / x4));
        }else if (t >= 2 * x4){
            sh = 1.0;
        }
        return sh;
    }
}
