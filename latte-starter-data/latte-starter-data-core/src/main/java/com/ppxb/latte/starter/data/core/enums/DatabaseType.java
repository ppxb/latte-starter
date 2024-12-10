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



package com.ppxb.latte.starter.data.core.enums;

import com.ppxb.latte.starter.data.core.function.ISqlFunction;

import java.io.Serializable;

public enum DatabaseType implements ISqlFunction {

    MYSQL("MySQL") {
        @Override
        public String findInSet(Serializable value, String set) {
            return "find_in_set('%s', %s) <> 0".formatted(value, set);
        }
    },

    POSTGRE_SQL("PostgreSQL") {
        @Override
        public String findInSet(Serializable value, String set) {
            return "(select position(',%s,' in ','||%s||',')) <> 0".formatted(value, set);
        }
    };

    private final String database;

    DatabaseType(String database) {
        this.database = database;
    }

    public static DatabaseType get(String database) {
        for (DatabaseType type : DatabaseType.values()) {
            if (type.database.equalsIgnoreCase(database)) {
                return type;
            }
        }
        return null;
    }

    public String getDatabase() {
        return database;
    }
}
