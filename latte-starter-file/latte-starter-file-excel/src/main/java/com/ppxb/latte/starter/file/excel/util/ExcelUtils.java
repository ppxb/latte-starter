/*
 * MIT License
 *
 * Copyright (c) 2024 ppxb
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */



package com.ppxb.latte.starter.file.excel.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.ppxb.latte.starter.core.exception.BaseException;
import com.ppxb.latte.starter.file.excel.converter.ExcelBigNumberConverter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class ExcelUtils {

    private static final Logger log = LoggerFactory.getLogger(ExcelUtils.class);

    private ExcelUtils() {
    }

    public static <T> void export() {
    }

    public static <T> void export(List<T> list,
                                  String fileName,
                                  String sheetName,
                                  Set<String> excludeColumnFieldNames,
                                  Class<T> clazz,
                                  HttpServletResponse response) {
        try {
            String exportFileName = URLUtil.encode("%s_%s.xlsx".formatted(fileName, DateUtil
                .format(new Date(), DatePattern.PURE_DATETIME_PATTERN)));
            response.setHeader("Content-Disposition", "attachment;filename=" + exportFileName);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            EasyExcelFactory.write(response.getOutputStream(), clazz)
                .autoCloseStream(false)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerConverter(new ExcelBigNumberConverter())
                .sheet(sheetName)
                .excludeColumnFieldNames(excludeColumnFieldNames)
                .doWrite(list);
        } catch (Exception e) {
            log.error("Export excel occurred an error: {}. fileName: {}.", e.getMessage(), fileName, e);
            throw new BaseException("导出 Excel 出现错误");
        }
    }
}