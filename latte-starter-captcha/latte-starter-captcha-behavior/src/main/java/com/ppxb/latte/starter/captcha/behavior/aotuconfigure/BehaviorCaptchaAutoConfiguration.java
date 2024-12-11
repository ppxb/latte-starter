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



package com.ppxb.latte.starter.captcha.behavior.aotuconfigure;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.anji.captcha.model.common.Const;
import com.anji.captcha.service.CaptchaService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;
import com.anji.captcha.util.ImageUtils;
import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 行为验证码自动配置类
 *
 * @author ppxb
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(BehaviorCaptchaProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.CAPTCHA_BEHAVIOR, name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
public class BehaviorCaptchaAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BehaviorCaptchaAutoConfiguration.class);

    private final BehaviorCaptchaProperties properties;

    public BehaviorCaptchaAutoConfiguration(BehaviorCaptchaProperties properties) {
        this.properties = properties;
    }

    /**
     * 初始化验证码底图
     *
     * @param jigsaw   滑块验证码图片路径
     * @param picClick 点选验证码图片路径
     */
    private static void initializeBaseMap(String jigsaw, String picClick) {
        ImageUtils
            .cacheBootImage(getResourcesImagesFile(jigsaw + "/original/*.png"), getResourcesImagesFile(jigsaw + "/slidingBlock/*.png"), getResourcesImagesFile(picClick + "/*.png"));
    }

    /**
     * 获取资源文件并转换为 Base64 编码
     * <p>
     * 从指定路径加载图片资源，并将其转换为 Base64 编码的字符串
     *
     * @param path 资源路径
     * @return 图片名称和Base64编码的映射
     */
    private static Map<String, String> getResourcesImagesFile(String path) {
        Map<String, String> imgMap = new HashMap<>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resolver.getResources(path);
            for (Resource resource : resources) {
                String imageName = resource.getFilename();
                byte[] imageValue = FileUtil.readBytes(resource.getFile());
                imgMap.put(imageName, Base64.encode(imageValue));
            }
        } catch (IOException e) {
            log.error("Failed to read image files from path: {}", path, e);
        }
        return imgMap;
    }

    /**
     * 配置验证码服务
     *
     * @return 验证码服务实例
     */
    @Bean
    @DependsOn("captchaCacheService")
    @ConditionalOnMissingBean
    public CaptchaService getCaptchaService() {
        Properties config = new Properties();

        // 基础配置
        config.put(Const.CAPTCHA_CACHETYPE, properties.getCacheType().name().toLowerCase());
        config.put(Const.CAPTCHA_TYPE, properties.getType().getCodeValue());
        config.put(Const.CAPTCHA_WATER_MARK, properties.getWaterMark());
        config.put(Const.CAPTCHA_FONT_TYPE, properties.getFontType());
        config.put(Const.CAPTCHA_FONT_SIZE, properties.getFontSize());
        config.put(Const.CAPTCHA_FONT_STYLE, properties.getFontStyle());
        config.put(Const.CAPTCHA_WORD_COUNT, 4);

        // 干扰项配置
        config.put(Const.CAPTCHA_INTERFERENCE_OPTIONS, properties.getInterferenceOptions());
        config.put(Const.CAPTCHA_SLIP_OFFSET, properties.getSlipOffset());
        config.put(Const.CAPTCHA_AES_STATUS, String.valueOf(properties.getEnableAes()));

        // 水印配置
        config.put(Const.CAPTCHA_WATER_FONT, properties.getWaterFont());

        // 缓存配置
        config.put(Const.CAPTCHA_CACAHE_MAX_NUMBER, properties.getCacheNumber());
        config.put(Const.CAPTCHA_TIMING_CLEAR_SECOND, properties.getTimingClear());

        // 历史数据清理配置
        config.put(Const.HISTORY_DATA_CLEAR_ENABLE, properties.getHistoryDataClearEnable());

        // 请求频率限制配置
        config.put(Const.REQ_FREQUENCY_LIMIT_ENABLE, properties.getReqFrequencyLimitEnable());
        config.put(Const.REQ_GET_LOCK_LIMIT, properties.getReqGetLockLimit());
        config.put(Const.REQ_GET_LOCK_SECONDS, properties.getReqGetLockSeconds());
        config.put(Const.REQ_GET_MINUTE_LIMIT, properties.getReqGetMinuteLimit());
        config.put(Const.REQ_CHECK_MINUTE_LIMIT, properties.getReqCheckMinuteLimit());
        config.put(Const.REQ_VALIDATE_MINUTE_LIMIT, properties.getReqVerifyMinuteLimit());

        // 底图路径配置
        config.put(Const.ORIGINAL_PATH_JIGSAW, CharSequenceUtil.emptyIfNull(properties.getJigsawBaseMapPath()));
        config.put(Const.ORIGINAL_PATH_PIC_CLICK, CharSequenceUtil.emptyIfNull(properties.getPicClickBaseMapPath()));

        if (CharSequenceUtil.startWith(properties.getJigsawBaseMapPath(), "classpath:") || CharSequenceUtil
            .startWith(properties.getPicClickBaseMapPath(), "classpath:")) {
            // 自定义 resources 目录下初始化底图
            config.put(Const.CAPTCHA_INIT_ORIGINAL, true);
            initializeBaseMap(properties.getJigsawBaseMapPath(), properties.getPicClickBaseMapPath());
        }
        return CaptchaServiceFactory.getInstance(config);
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Latte Starter] - Auto Configuration 'Captcha-Behavior' completed initialization.");
    }
}
