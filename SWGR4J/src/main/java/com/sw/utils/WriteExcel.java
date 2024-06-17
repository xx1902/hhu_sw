package com.sw.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marchino
 * @date 2024/6/17 21:04
 * @description
 */

public class WriteExcel {

    public void writeExcel(List<DataRow> dataRows, String fileName) {

        ExcelWriter excelWriter = EasyExcel.write(fileName).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").head(DataRow.class).build();
        excelWriter.write(dataRows, writeSheet);
        excelWriter.finish();

        System.out.println("Excel file written successfully.");

    }

}
