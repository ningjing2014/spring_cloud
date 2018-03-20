package com.ln.xproject.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ln.xproject.base.exception.ExceptionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by ning on 16-12-7.
 */
@Slf4j
public class POIUtils {

    /**
     * 获取workBook
     * 
     * @param is
     * @return
     */
    public static Workbook getWorkbook(InputStream is) {
        Workbook book = null;
        try {
            book = new XSSFWorkbook(is);
        } catch (Exception e) {
            try {
                book = new HSSFWorkbook(is);
            } catch (IOException ex) {
                log.error("解析Excel异常", ex);
                throw ExceptionUtils.commonError("解析Excel异常");
            }
        }
        return book;
    }

    /**
     * 获取单元格中的字符值
     * 
     * @param cell
     * @return
     */
    public static String getStringCellValue(Cell cell) {
        String ret;
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                ret = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                ret = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                ret = null;
                break;
            case Cell.CELL_TYPE_FORMULA:
                Workbook wb = cell.getSheet().getWorkbook();
                CreationHelper crateHelper = wb.getCreationHelper();
                FormulaEvaluator evaluator = crateHelper.createFormulaEvaluator();
                ret = getStringCellValue(evaluator.evaluateInCell(cell));
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date theDate = cell.getDateCellValue();
                    ret = DateUtils.format(theDate, "yyyy-MM-dd");
                } else {
                    ret = NumberToTextConverter.toText(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_STRING:
                ret = cell.getRichStringCellValue().getString();
                break;
            default:
                ret = null;
        }
        if (StringUtils.isBlank(ret)) {
            return null;
        }
        return StringUtils.trim(ret);
    }

}
