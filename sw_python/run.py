# -*- coding: utf-8 -*-
"""
Version: 1.0
Date: 2024-04-11

Change log:
- V1.0 (2024-04-11): 初始版本

This code is released under the GNU General Public License (GPL) version 3.
see <http://www.gnu.org/licenses/>.
"""

import numpy as np

# 加载GR4J模型参数
para = np.loadtxt('data/GR4J_Parameter.txt')
x1 = para[0]  # x1: 产流水库容量 (mm)
x2 = para[1]  # x2: 地下水交换系数 (mm)
x3 = para[2]  # x3: 汇流水库容量 (mm)
x4 = para[3]  # x4: 单位线汇流时间 (天)

# 加载GR4J其他参数
other_para = np.loadtxt('data/others.txt')
area = other_para[0]  # 流域面积(km2)
upperTankRatio = other_para[1]  # 产流水库初始填充率 S0/x1
lowerTankRatio = other_para[2]  # 汇流水库初始填充率 R0/x3

# 加载数据文件
data = np.loadtxt('data/inputData.txt')
P = data[:, 0]  # 第二列: 日降雨量(mm)
E = data[:, 1]  # 第三列: 蒸散发量(mm)
Qobs = data[:, 2]  # 第四列: 流域出口观测流量(ML/day)

Qobs_mm = Qobs  # 将径流量单位从ML/day转化为mm/spt

print('{}{}{}'.format(area, upperTankRatio, lowerTankRatio))