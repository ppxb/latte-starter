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



package com.ppxb.latte.starter.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HtmlUtil;
import com.ppxb.latte.starter.core.constant.StringConstants;
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import net.dreamlu.mica.ip2region.core.IpInfo;

import java.util.Objects;
import java.util.Set;

public class IpUtils {

    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    private static final String LOCALHOST_IPV4 = "127.0.0.1";

    private IpUtils() {
    }

    public static String getIpv4Address(String ip) {
        if (isInnerIpv4(ip)) {
            return "内网IP";
        }

        Ip2regionSearcher ip2regionSearcher = SpringUtil.getBean(Ip2regionSearcher.class);
        IpInfo ipInfo = ip2regionSearcher.memorySearch(ip);
        if (null == ipInfo) {
            return null;
        }
        Set<String> regionSet = CollUtil.newLinkedHashSet(ipInfo.getCountry(), ipInfo.getRegion(), ipInfo
            .getProvince(), ipInfo.getCity(), ipInfo.getIsp());
        regionSet.removeIf(Objects::isNull);
        return String.join(StringConstants.PIPE, regionSet);
    }

    public static boolean isInnerIpv4(String ip) {
        String ipAddress = LOCALHOST_IPV6.equals(ip) ? LOCALHOST_IPV4 : HtmlUtil.cleanHtmlTag(ip);
        return NetUtil.isInnerIP(ipAddress);
    }
}
