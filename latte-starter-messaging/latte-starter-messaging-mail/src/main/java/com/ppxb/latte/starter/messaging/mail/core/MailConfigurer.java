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



package com.ppxb.latte.starter.messaging.mail.core;

import com.ppxb.latte.starter.core.validation.ValidationUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public interface MailConfigurer {

    MailConfig getMailConfig();

    default void apply(MailConfig mailConfig, JavaMailSenderImpl sender) {
        String protocolLowerCase = mailConfig.getProtocol().toLowerCase();
        ValidationUtils.throwIfNotEqual(MailConfig.DEFAULT_PROTOCOL, protocolLowerCase, "邮件配置错误：不支持的邮件发送协议: %s"
            .formatted(mailConfig.getProtocol()));
        sender.setProtocol(mailConfig.getProtocol());

        ValidationUtils.throwIfBlank(mailConfig.getHost(), "邮件配置错误：服务器地址不能为空");
        sender.setHost(mailConfig.getHost());

        ValidationUtils.throwIfNull(mailConfig.getPort(), "邮件配置错误：服务器端口不能为空");
        sender.setPort(mailConfig.getPort());

        ValidationUtils.throwIfBlank(mailConfig.getUsername(), "邮件配置错误：用户名不能为空");
        sender.setUsername(mailConfig.getUsername());

        ValidationUtils.throwIfBlank(mailConfig.getPassword(), "邮件配置错误：密码不能为空");
        sender.setPassword(mailConfig.getPassword());

        if (mailConfig.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(mailConfig.getDefaultEncoding().name());
        }
        if (!mailConfig.getProperties().isEmpty()) {
            sender.setJavaMailProperties(mailConfig.toJavaMailProperties());
        }
    }
}
