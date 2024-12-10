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

import cn.hutool.core.text.CharSequenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class SqlInjectionUtils {

    private static final Logger log = LoggerFactory.getLogger(SqlInjectionUtils.class);

    private static final Pattern SQL_SYNTAX_PATTERN = Pattern
        .compile("(insert|delete|update|select|create|drop|truncate|grant|alter|deny|revoke|call|execute|exec|declare|show|rename|set)" + "\\s+.*(into|from|set|where|table|database|view|index|on|cursor|procedure|trigger|for|password|union|and|or)|(select\\s*\\*\\s*from\\s+)|(and|or)\\s+.*", Pattern.CASE_INSENSITIVE);

    private static final Pattern SQL_COMMENT_PATTERN = Pattern
        .compile("'.*(or|union|--|#|/\\*|;)", Pattern.CASE_INSENSITIVE);

    private static final String SQL_SYNTAX_KEYWORD = "and |exec |peformance_schema|information_schema|extractvalue|updatexml|geohash|gtid_subset|gtid_subtract|insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|or |+|--";

    private static final String[] SQL_FUNCTION_PATTERN = new String[] {"chr\\s*\\(", "mid\\s*\\(", " char\\s*\\(",
        "sleep\\s*\\(", "user\\s*\\(", "show\\s+tables", "user[\\s]*\\([\\s]*\\)", "show\\s+databases",
        "sleep\\(\\d*\\)", "sleep\\(.*\\)",};

    private static final String MESSAGE_TEMPLATE = "SQL 注入检查: 检查值=>{}<=存在 SQL 注入关键字, 关键字=>{}<=";

    private SqlInjectionUtils() {
    }

    public static boolean check(String value) {
        return check(value, null);
    }

    public static boolean check(String value, String customKeyword) {
        if (CharSequenceUtil.isBlank(value)) {
            return false;
        }
        // 处理是否包含 SQL 注释字符 || 检查是否包含 SQL 注入敏感字符
        if (SQL_COMMENT_PATTERN.matcher(value).find() || SQL_SYNTAX_PATTERN.matcher(value).find()) {
            log.warn("SQL 注入检查: 检查值=>{}<=存在 SQL 注释字符或 SQL 注入敏感字符", value);
            return true;
        }
        // 转换成小写再进行比较
        value = value.toLowerCase().trim();
        // 检查是否包含 SQL 语法关键字
        if (checkKeyword(value, SQL_SYNTAX_KEYWORD.split("\\|"))) {
            return true;
        }
        // 检查是否包含自定义关键字
        if (CharSequenceUtil.isNotBlank(customKeyword) && checkKeyword(value, customKeyword.split("\\|"))) {
            return true;
        }
        // 检查是否包含 SQL 注入敏感字符
        for (String pattern : SQL_FUNCTION_PATTERN) {
            if (Pattern.matches(".*" + pattern + ".*", value)) {
                log.warn(MESSAGE_TEMPLATE, value, pattern);
                return true;
            }
        }
        return false;
    }

    private static boolean checkKeyword(String value, String[] keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                log.warn(MESSAGE_TEMPLATE, value, keyword);
                return true;
            }
        }
        return false;
    }
}
