import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.property.LoopMergeProperty;
import com.sw.utils.WriteExcel;

import java.util.ArrayList;
import java.util.List;

public class WriteExcelWithEasyExcel {
    public static void main(String[] args) {
        WriteExcel writeExcel = new WriteExcel();
        writeExcel.writeExcel(null, "output.xlsx");
    }

}
