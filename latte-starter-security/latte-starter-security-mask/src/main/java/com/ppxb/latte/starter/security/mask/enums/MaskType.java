package com.ppxb.latte.starter.security.mask.enums;

import cn.hutool.core.text.CharSequenceUtil;
import com.ppxb.latte.starter.core.constant.StringConstants;
import com.ppxb.latte.starter.security.mask.strategy.IMaskStrategy;

public enum MaskType implements IMaskStrategy {

    CUSTOM {
        @Override
        public String mask(String str, char character, int left, int right) {
            return CharSequenceUtil.replace(str, left, str.length() - right, character);
        }
    },

    MOBILE_PHONE {
        @Override
        public String mask(String str, char character, int left, int right) {
            return CharSequenceUtil.replace(str, 3, str.length() - 4, character);
        }
    },

    FIXED_PHONE {
        @Override
        public String mask(String str, char character, int left, int right) {
            return CharSequenceUtil.replace(str, 4, str.length() - 2, character);
        }
    },

    EMAIL {
        @Override
        public String mask(String str, char character, int left, int right) {
            int index = str.indexOf(StringConstants.AT);
            if (index <= 1) {
                return str;
            }
            return CharSequenceUtil.replace(str, 1, index, character);
        }
    },

    ID_CARD {
        @Override
        public String mask(String str, char character, int left, int right) {
            return CharSequenceUtil.replace(str, 1, str.length() - 2, character);
        }
    },

    BANK_CARD {
        @Override
        public String mask(String str, char character, int left, int right) {
            String cleanStr = CharSequenceUtil.cleanBlank(str);
            if (cleanStr.length() < 9) {
                return cleanStr;
            }
            final int length = cleanStr.length();
            final int endLength = length % 4 == 0 ? 4 : length % 4;
            final int midLength = length - 4 - endLength;
            final StringBuilder buffer = new StringBuilder();
            buffer.append(cleanStr, 0, 4);
            for (int i = 0; i < midLength; ++i) {
                if (i % 4 == 0) {
                    buffer.append(StringConstants.SPACE);
                }
                buffer.append(character);
            }
            buffer.append(StringConstants.SPACE).append(cleanStr, length - endLength, length);
            return buffer.toString();
        }
    },

    CAR_LICENSE {
        @Override
        public String mask(String str, char character, int left, int right) {
            // 普通车牌
            int length = str.length();
            if (length == 7) {
                return CharSequenceUtil.replace(str, 3, 6, character);
            }
            // 新能源车牌
            if (length == 8) {
                return CharSequenceUtil.replace(str, 3, 7, character);
            }
            return str;
        }
    },

    CHINESE_NAME {
        @Override
        public String mask(String str, char character, int left, int right) {
            return CharSequenceUtil.replace(str, 1, str.length(), character);
        }
    },

    PASSWORD {
        @Override
        public String mask(String str, char character, int left, int right) {
            return CharSequenceUtil.repeat(character, str.length());
        }
    },

    ADDRESS {
        @Override
        public String mask(String str, char character, int left, int right) {
            int length = str.length();
            return CharSequenceUtil.replace(str, length - 8, length, character);
        }
    },

    IPV4 {
        @Override
        public String mask(String str, char character, int left, int right) {
            return CharSequenceUtil.subBefore(str, StringConstants.DOT, false) + String
                    .format(".%s.%s.%s", character, character, character);
        }
    },

    IPV6 {
        @Override
        public String mask(String str, char character, int left, int right) {
            return CharSequenceUtil.subBefore(str, StringConstants.COLON, false) + String
                    .format(":%s:%s:%s:%s:%s:%s:%s", character, character, character, character, character, character, character);
        }
    }
}