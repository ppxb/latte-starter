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



package com.ppxb.latte.starter.data.core.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.DbUtil;
import cn.hutool.db.meta.Column;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.Table;
import cn.hutool.db.meta.TableType;
import com.ppxb.latte.starter.core.exception.BusinessException;
import com.ppxb.latte.starter.data.core.enums.DatabaseType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MetaUtils {

    private MetaUtils() {
    }

    public static DatabaseType getDatabaseTypeOrDefault(DataSource dataSource, DatabaseType defaultValue) {
        DatabaseType databaseType = getDatabaseType(dataSource);
        return null == databaseType ? defaultValue : databaseType;
    }

    public static DatabaseType getDatabaseType(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();
            return DatabaseType.get(databaseProductName);
        } catch (SQLException e) {
            throw new BusinessException(e);
        }
    }

    public static List<Table> getTables(DataSource dataSource) {
        return getTables(dataSource, null);
    }

    public static List<Table> getTables(DataSource dataSource, String tableName) {
        List<Table> tables = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            String catalog = MetaUtil.getCatalog(conn);
            String schema = MetaUtil.getSchema(conn);
            final DatabaseMetaData metaData = conn.getMetaData();
            try (final ResultSet rs = metaData.getTables(catalog, schema, tableName, Convert
                .toStrArray(TableType.TABLE))) {
                if (rs != null) {
                    String name;
                    while (rs.next()) {
                        name = rs.getString("TABLE_NAME");
                        if (CharSequenceUtil.isNotBlank(name)) {
                            final Table table = Table.create(name);
                            table.setCatalog(catalog);
                            table.setSchema(schema);
                            table.setComment(MetaUtil.getRemarks(metaData, catalog, schema, name));
                            tables.add(table);
                        }
                    }
                }
            }
            return tables;
        } catch (Exception e) {
            throw new DbRuntimeException("Get tables error", e);
        } finally {
            DbUtil.close(conn);
        }
    }

    public static Collection<Column> getColumns(DataSource dataSource, String tableName) {
        Table table = MetaUtil.getTableMeta(dataSource, tableName);
        return table.getColumns();
    }
}
