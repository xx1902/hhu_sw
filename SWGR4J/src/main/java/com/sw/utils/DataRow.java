package com.sw.utils;

public class DataRow {
        private double x1;
        private double x2;
        private double x3;
        private double x4;
        private double NSE;

        public DataRow(double x1, double x2, double x3, double x4, double NSE) {
            this.x1 = x1;
            this.x2 = x2;
            this.x3 = x3;
            this.x4 = x4;
            this.NSE = NSE;
        }

        public double getX1() {
            return x1;
        }

        public void setX1(double x1) {
            this.x1 = x1;
        }

        public double getX2() {
            return x2;
        }

        public void setX2(double x2) {
            this.x2 = x2;
        }

        public double getX3() {
            return x3;
        }

        public void setX3(double x3) {
            this.x3 = x3;
        }

        public double getX4() {
            return x4;
        }

        public void setX4(double x4) {
            this.x4 = x4;
        }

        public double getNSE() {
            return NSE;
        }

        public void setNSE(double NSE) {
            this.NSE = NSE;
        }
    }