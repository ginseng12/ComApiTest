package apitest.utils;

/**
 * @author lulu
 * @data 2022/1/6 10:48 上午
 * 通用工具类
 *  * @author pc
 *  *
 * 参考https://www.jianshu.com/p/2ba3c0bd3eb6
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelToMapUtils {

    public static List<Map<String, Object>> importExcel(String filepath, int index) {
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(new FileInputStream(filepath));
            Sheet sheet = workbook.getSheetAt(index);
            Row row = sheet.getRow(0);
            List<String> keys = new ArrayList<String>();
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);
                keys.add(String.valueOf(getValue(cell)));
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row currentRow = sheet.getRow(i);
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                for (int j = 0; j < currentRow.getLastCellNum(); j++) {
                    map.put(keys.get(j), getValue(currentRow.getCell(j)));
                }
                mapList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("excel解析出错");
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mapList;
    }

    private static Object getValue(Cell cell) {
        if(cell==null) {
            return "";
        }
        if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            NumberFormat nf = NumberFormat.getInstance();
            return nf.format(cell.getNumericCellValue());
            //return cell.getNumericCellValue();
        } else {
            return String.valueOf(cell.getStringCellValue());
        }
    }

    public static void main(String[] args) {
        String dir=System.getProperty("user.dir");
        String path=dir+File.separator+"data"+File.separator+"apitest2.xlsx";
        List<Map<String, Object>> listmapList= ExcelToMapUtils.importExcel(path,1);
        System.out.println(listmapList);
    }
}
