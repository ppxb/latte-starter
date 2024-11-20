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



package com.ppxb.latte.starter.core.constant;

public class StringConstants {

    /**
     * 空字符串 {@code ""}
     */
    public static final String EMPTY = "";

    /**
     * 空格符 {@code " "}
     */
    public static final String SPACE = " ";

    /**
     * 制表符 {@code "\t"}
     */
    public static final String TAB = "	";

    /**
     * 空 JSON {@code "{}"}
     */
    public static final String EMPTY_JSON = "{}";

    /**
     * 点 {@code "."}
     */
    public static final String DOT = ".";

    /**
     * 双点 {@code ".."}
     * <p>
     * 作为指向上级文件夹的路径，如：{@code "../path"}
     * </p>
     */
    public static final String DOUBLE_DOT = "..";

    /**
     * 逗号 {@code ","}
     */
    public static final String COMMA = ",";

    /**
     * 中文逗号 {@code "，"}
     */
    public static final String CHINESE_COMMA = "，";

    /**
     * 冒号 {@code ":"}
     */
    public static final String COLON = ":";

    /**
     * 分号 {@code ";"}
     */
    public static final String SEMICOLON = ";";

    /**
     * 问号 {@code "?"}
     */
    public static final String QUESTION_MARK = "?";

    /**
     * 下划线 {@code "_"}
     */
    public static final String UNDERLINE = "_";

    /**
     * 减号（连接符） {@code "-"}
     */
    public static final String DASHED = "-";

    /**
     * 加号 {@code "+"}
     */
    public static final String PLUS = "+";

    /**
     * 等号 {@code "="}
     */
    public static final String EQUALS = "=";

    /**
     * 星号 {@code "*"}
     */
    public static final String ASTERISK = "*";

    /**
     * 斜杠 {@code "/"}
     */
    public static final String SLASH = "/";

    /**
     * 反斜杠 {@code "\\"}
     */
    public static final String BACKSLASH = "\\";

    /**
     * 管道符 {@code "|"}
     */
    public static final String PIPE = "|";

    /**
     * 艾特 {@code "@"}
     */
    public static final String AT = "@";

    /**
     * 与符号 {@code "&"}
     */
    public static final String AMP = "&";

    /**
     * 花括号（左） <code>"{"</code>
     */
    public static final String DELIM_START = "{";

    /**
     * 花括号（右） <code>"}"</code>
     */
    public static final String DELIM_END = "}";

    /**
     * 中括号（左） {@code "["}
     */
    public static final String BRACKET_START = "[";

    /**
     * 中括号（右） {@code "]"}
     */
    public static final String BRACKET_END = "]";

    /**
     * 圆括号（左） {@code "("}
     */
    public static final String ROUND_BRACKET_START = "(";

    /**
     * 圆括号（右） {@code ")"}
     */
    public static final String ROUND_BRACKET_END = ")";

    /**
     * 双引号 {@code "\""}
     */
    public static final String DOUBLE_QUOTES = "\"";

    /**
     * 单引号 {@code "'"}
     */
    public static final String SINGLE_QUOTE = "'";

    /**
     * 回车符 {@code "\r"}
     */
    public static final String CR = "\r";

    /**
     * 换行符 {@code "\n"}
     */
    public static final String LF = "\n";

    /**
     * 路径模式 {@code "/**"}
     */
    public static final String PATH_PATTERN = "/**";

    /**
     * 路径模式（仅匹配当前目录） {@code "/*"}
     */
    public static final String PATH_PATTERN_CURRENT_DIR = "/*";

    /**
     * HTML 不间断空格转义 {@code "&nbsp;" -> " "}
     */
    public static final String HTML_NBSP = "&nbsp;";

    /**
     * HTML And 符转义 {@code "&amp;" -> "&"}
     */
    public static final String HTML_AMP = "&amp;";

    /**
     * HTML 双引号转义 {@code "&quot;" -> "\""}
     */
    public static final String HTML_QUOTE = "&quot;";

    /**
     * HTML 单引号转义 {@code "&apos" -> "'"}
     */
    public static final String HTML_APOS = "&apos;";

    /**
     * HTML 小于号转义 {@code "&lt;" -> "<"}
     */
    public static final String HTML_LT = "&lt;";

    /**
     * HTML 大于号转义 {@code "&gt;" -> ">"}
     */
    public static final String HTML_GT = "&gt;";

    private StringConstants() {
    }
}
