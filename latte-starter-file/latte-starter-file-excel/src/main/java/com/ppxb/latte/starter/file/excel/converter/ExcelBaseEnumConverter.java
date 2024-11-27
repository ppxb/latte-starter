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
import cn.hutool.core.util.ClassUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.ppxb.latte.starter.core.constant.StringConstants;
import com.ppxb.latte.starter.core.enums.BaseEnum;

public class ExcelBaseEnumConverter implements Converter<BaseEnum<Integer>> {

    @Override
    public Class<BaseEnum> supportJavaTypeKey() {
        return BaseEnum.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public BaseEnum<Integer> convertToJavaData(ReadCellData<?> cellData,
                                               ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        return this.getEnum(BaseEnum.class, Convert.toStr(cellData.getData()));
    }

    @Override
    public WriteCellData<String> convertToExcelData(BaseEnum<Integer> value,
                                                    ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) throws Exception {
        return new WriteCellData<>(value == null ? StringConstants.EMPTY : value.getDescription());
    }

    private BaseEnum<Integer> getEnum(Class<?> enumType, String description) {
        Object[] enumConstants = enumType.getEnumConstants();
        for (Object enumConstant : enumConstants) {
            if (ClassUtil.isAssignable(BaseEnum.class, enumType)) {
                BaseEnum<Integer> baseEnum = (BaseEnum<Integer>)enumConstant;
                if (baseEnum.getDescription().equals(description)) {
                    return baseEnum;
                }
            }
        }
        return null;
    }
}
