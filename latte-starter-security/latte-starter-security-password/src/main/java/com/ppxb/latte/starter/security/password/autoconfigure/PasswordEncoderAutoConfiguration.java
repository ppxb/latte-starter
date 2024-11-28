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



package com.ppxb.latte.starter.security.password.autoconfigure;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import com.ppxb.latte.starter.core.validation.CheckUtils;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoConfiguration
@EnableConfigurationProperties(PasswordEncoderProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.SECURITY_PASSWORD, name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
public class PasswordEncoderAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(PasswordEncoderAutoConfiguration.class);

    private final PasswordEncoderProperties properties;

    public PasswordEncoderAutoConfiguration(PasswordEncoderProperties properties) {
        this.properties = properties;
    }

    @Bean
    public PasswordEncoder passwordEncoder(List<PasswordEncoder> passwordEncoderList) {
        Map<String, PasswordEncoder> encoderMap = new HashMap<>();
        encoderMap.put("bcrypt", new BCryptPasswordEncoder());
        encoderMap.put("pbkdf2", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoderMap.put("scrypt", SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoderMap.put("argon2", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        if (CollUtil.isNotEmpty(passwordEncoderList)) {
            passwordEncoderList.forEach(encoder -> {
                String simpleName = encoder.getClass().getSimpleName();
                encoderMap.put(CharSequenceUtil.removeSuffix(simpleName, "PasswordEncoder").toLowerCase(), encoder);
            });
        }
        String encodingId = properties.getEncodingId();
        CheckUtils.throwIf(!encoderMap.containsKey(encodingId), "{} is not found in idToPasswordEncoder.", encodingId);
        return new DelegatingPasswordEncoder(encodingId, encoderMap);
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Latte Starter] - Auto Configuration 'Security-PasswordEncoder' completed initialization.");
    }
}
