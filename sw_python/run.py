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
from mytools import *
from evaluate import *
from scipy.optimize import differential_evolution  # 差分进化优化函数
from pyswarm import pso  # 导入粒子群优化函数

# str = 'data'
str = 'demo_136006A'
# 加载GR4J模型参数
para = np.loadtxt(str + '/GR4J_Parameter.txt')
x1 = para[0]  # x1: 产流水库容量 (mm)
x2 = para[1]  # x2: 地下水交换系数 (mm)
x3 = para[2]  # x3: 汇流水库容量 (mm)
x4 = para[3]  # x4: 单位线汇流时间 (天)

# 加载GR4J其他参数
other_para = np.loadtxt(str + '/others.txt')
area = other_para[0]  # 流域面积(km2)
upperTankRatio = other_para[1]  # 产流水库初始填充率 S0/x1
lowerTankRatio = other_para[2]  # 汇流水库初始填充率 R0/x3

# 加载数据文件
data = np.loadtxt(str + '/inputData.txt')
P = data[:, 0]  # 第二列: 日降雨量(mm)
E = data[:, 1]  # 第三列: 蒸散发量(mm)
Qobs = data[:, 2]  # 第四列: 流域出口观测流量(ML/day)

Qobs_mm = Qobs*86.4/area  # 将径流量单位从ML/day转化为mm/spt 需要将m3/s转化为mm/d。转化公式为Qobs*86.4/area

# -----第二次代码-----
nStep = data.shape[0]   # 观测数据的长度

Pn = np.zeros(nStep)    # 存储有效降雨量
En = np.zeros(nStep)    # 存储剩余的蒸发能力
Ps = np.zeros(nStep)  # Ps：中间变量，记录净雨补充土壤含水量
Es = np.zeros(nStep)  # Es: 中间变量，记录剩余蒸发能力消耗土壤含水量
Perc = np.zeros(nStep)  # Perc: 中间变量，记录产流水库壤中流产流量
Pr = np.zeros(nStep)  # Pr: 记录产流总量

# 根据输入参数x4计算S曲线以及单位线，这里假设单位线长度UH1为10，UH2为20; 即x4取值不应该大于10
maxDayDelay = 10
# 定义几个数组以存储SH1, UH1, SH2, UH2
SH1 = np.zeros(maxDayDelay)             # 定义第1条单位线的累积曲线
UH1 = np.zeros(maxDayDelay)             # 定义第1条单位线
SH2 = np.zeros(2 * maxDayDelay)         # 定义第2条单位线的累积曲线
UH2 = np.zeros(2 * maxDayDelay)         # 定义第2条单位线


# -------------------mytool导入函数------------
# 计算SH1以及SH2，由于i是从0开始的，为避免第一个数值为0，我们在函数钟使用i+1
for i in range(maxDayDelay):
    SH1[i] = SH1_CURVE(i, x4)

for i in range(2 * maxDayDelay):
    SH2[i] = SH2_CURVE(i, x4)

# 计算UH1以及UH2
for i in range(maxDayDelay):
    if i == 0:
        UH1[i] = SH1[i]
    else:
        UH1[i] = SH1[i] - SH1[i - 1]

for i in range(2 * maxDayDelay):
    if i == 0:
        UH2[i] = SH2[i]
    else:
        UH2[i] = SH2[i] - SH2[i - 1]

# 逐日计算En及Pn值，En及Pn为GR4J模型的输入，可以提前计算出来
for i in range(nStep):
    if P[i] >= E[i]: # 若当日降雨量大于等于当日蒸发量，净降雨量Pn = P - E，净蒸发能力En = 0
        Pn[i] = P[i] - E[i]
        En[i] = 0
    else: # 若当日降雨量小于当日蒸发量，净降雨量Pn = 0，净蒸发能力En = E - P
        Pn[i] = 0
        En[i] = E[i] - P[i]

# Q值的计算
from simulate import *

Q = simulate_gr4j(nStep, x1, x2, x3, x4, upperTankRatio, lowerTankRatio, maxDayDelay, UH1, UH2, Pn, En)

# ------------精度评估和绘图----------------
evaluate_gr4j_model(nStep, Qobs_mm, Q)

print("GR4J Simulation Finished")

# # ------------- 差分进化优化 -------------
# # 定义目标函数：计算并返回NSE
def objective_function(params):
    x1, x2, x3, x4 = params
    Q = simulate_gr4j(nStep, x1, x2, x3, x4, upperTankRatio, lowerTankRatio, maxDayDelay, UH1, UH2, Pn, En)
    NSE = evaluate_gr4j_model(nStep, Qobs_mm, Q, plot=False)
    return -NSE  # 由于差分进化算法是最小化问题，因此返回负的NSE

# 设定参数边界
bounds = [(10, 700), (-5.5, 3.5), (20, 400), (1.0, 2.5)]

# 使用差分进化算法优化参数
result = differential_evolution(objective_function, bounds, maxiter=100)
best_params = result.x

print("优化后的参数:", best_params)

# 用优化后的参数运行GR4J模型
Q_optimized = simulate_gr4j(nStep, best_params[0], best_params[1], best_params[2], best_params[3], upperTankRatio, lowerTankRatio, maxDayDelay, UH1, UH2, Pn, En)
NSE_optimized = evaluate_gr4j_model(nStep, Qobs_mm, Q_optimized, plot=True)

print("优化后的NSE:", NSE_optimized)

# 保存优化后的参数到文件
np.savetxt(str + '/GR4J_Parameter_Optimized.txt', best_params)


# ------------------ 粒子群 ----------------
# 定义GR4J模型的目标函数
# 定义目标函数：计算并返回NSE
def objective_function(params):
    x1, x2, x3, x4 = params
    Q = simulate_gr4j(nStep, x1, x2, x3, x4, upperTankRatio, lowerTankRatio, maxDayDelay, UH1, UH2, Pn, En)
    NSE = evaluate_gr4j_model(nStep, Qobs_mm, Q, plot=False)
    return -NSE  # 由于粒子群算法是最小化问题，因此返回负的NSE

# 设定参数边界
bounds = [(10, 700), (-5.5, 3.5), (20, 400), (1.0, 2.5)]

# 提取下界（lb）和上界（ub）
lb = [bound[0] for bound in bounds]
ub = [bound[1] for bound in bounds]

# 使用粒子群算法优化参数
best_params, best_NSE = pso(objective_function, lb, ub, maxiter=100)

print("优化后的参数:", best_params)

# 用优化后的参数运行GR4J模型
Q_optimized = simulate_gr4j(nStep, best_params[0], best_params[1], best_params[2], best_params[3], upperTankRatio, lowerTankRatio, maxDayDelay, UH1, UH2, Pn, En)
NSE_optimized = evaluate_gr4j_model(nStep, Qobs_mm, Q_optimized, plot=True)

print("优化后的NSE:", NSE_optimized)

# 保存优化后的参数到文件
np.savetxt(str + '/GR4J_Parameter_Optimized2.txt', best_params)
