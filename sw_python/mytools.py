# -*- coding: utf-8 -*-
# 模块3  2
# mytools.py文件

def SH1_CURVE(t, x4):
    if t <= 0.0:
        sh = 0.0
    elif t < x4:
        sh = (t / x4) ** 2.5
    elif t >= x4:
        sh = 1.0
    return sh

def SH2_CURVE(t, x4):
    if t <= 0.0:
        sh = 0.0
    elif t <= x4:
        sh = 0.5 * (t / x4) ** 2.5
    elif t < 2 * x4:
        sh = 1 - 0.5 * (2 - t / x4) ** 2.5
    elif t >= 2 * x4:
        sh = 1.0
    return sh