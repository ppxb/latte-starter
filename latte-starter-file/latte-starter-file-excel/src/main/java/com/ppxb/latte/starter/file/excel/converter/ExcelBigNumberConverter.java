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



package com.ppxb.latte.starter.file.excel.converter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.ppxb.latte.starter.core.constant.StringConstants;

public class ExcelBigNumberConverter implements Converter<Long> {

    private static final int MAX_LENGTH = 15;

    @Override
    public Class<Long> supportJavaTypeKey() {
        return Long.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Long convertToJavaData(ReadCellData<?> cellData,
                                  ExcelContentProperty contentProperty,
                                  GlobalConfiguration globalConfiguration) {
        return Convert.toLong(cellData.getData());
    }

    @Override
    public WriteCellData<Object> convertToExcelData(Long value,
                                                    ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        if (value == null) {
            return new WriteCellData<>(StringConstants.EMPTY);
        }

        String strValue = String.valueOf(value);
        if (strValue.length() > MAX_LENGTH) {
            return new WriteCellData<>(strValue);
        }

        WriteCellData<Object> cellData = new WriteCellData<>(NumberUtil.toBigDecimal(value));
        cellData.setType(CellDataTypeEnum.NUMBER);
        return cellData;
    }
}
