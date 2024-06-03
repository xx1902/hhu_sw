# -*- coding: utf-8 -*-
import numpy as np

def simulate_gr4j(nStep, x1, x2,x3,x4, upperTankRatio, lowerTankRatio, maxDayDelay, UH1, UH2, Pn, En):
    # 定义一些产汇流计算需要用到的数值及数组
    S0 = upperTankRatio * x1            # 定义产流水库初识土壤含水量 = 初识土壤含水比例 * 产流水库容量
    S_TEMP = S0                         # 用S_temp存储当前产流水库储量

    S = np.zeros(nStep)                 # 产流水库数组，每天的土壤含水量
    Ps = np.zeros(nStep)                # 产流中间变量，指每天有多少水补充土壤含水量
    Es = np.zeros(nStep)                # 产流中间变量，指每天土壤含水量还有多少消耗需要用来蒸发
    Perc = np.zeros(nStep)              # 产流中间变量，指每天土壤含水量通过下渗产生多少直接径流
    Pr = np.zeros(nStep)                # GR4j 总产流量

    for i in range(nStep):
        S[i] = S_TEMP
        # 使用Pn[i] 和 En[i]来进行判断
        if Pn[i] != 0:  # 净雨量大于0，此时一部分净雨形成地面径流，一部分下渗
            Ps[i] = x1 * (1 - ((S[i] / x1) ** 2)) * np.tanh(Pn[i] / x1) / (1 + S[i] / x1 * np.tanh(Pn[i] / x1))
            Es[i] = 0
        if En[i] != 0:  # 净蒸发能力大于0，此时土壤中水分一部分用于消耗
            Ps[i] = 0
            Es[i] = (S[i] * (2 - (S[i] / x1)) * np.tanh(En[i] / x1)) / (1 + (1 - S[i] / x1) * np.tanh(En[i] / x1))

        # 更新上层水库蓄水量
        S_TEMP = S[i] - Es[i] + Ps[i]
        Ratio_TEMP = S_TEMP / x1

        # 计算产流水库渗漏
        Perc[i] = S_TEMP * (1 - (1 + (4.0 / 9.0 * (S_TEMP / x1)) ** 4) ** (-0.25))

        # 计算总产流量
        Pr[i] = Perc[i] + (Pn[i] - Ps[i])

        # 更新当前产流水库水量，作为次日产流水库水量
        S_TEMP = S_TEMP - Perc[i]
