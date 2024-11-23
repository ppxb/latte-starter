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



package com.ppxb.latte.starter.web.autoconfigure.response;

import com.feiniaojin.gracefulresponse.ExceptionAliasRegister;
import com.feiniaojin.gracefulresponse.advice.*;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.BeforeControllerAdviceProcess;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.ControllerAdvicePredicate;
import com.feiniaojin.gracefulresponse.advice.lifecycle.response.ResponseBodyAdvicePredicate;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseFactory;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseStatusFactoryImpl;
import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import com.ppxb.latte.starter.core.util.GeneralPropertySourceFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.parsers.ReturnTypeParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GlobalResponseProperties.class)
@PropertySource(value = "classpath:default-web.yml", factory = GeneralPropertySourceFactory.class)
public class GlobalResponseAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GlobalResponseAutoConfiguration.class);

    private final GlobalResponseProperties globalResponseProperties;

    public GlobalResponseAutoConfiguration(GlobalResponseProperties globalResponseProperties) {
        this.globalResponseProperties = globalResponseProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public GrNotVoidResponseBodyAdvice grNotVoidResponseBodyAdvice() {
        GrNotVoidResponseBodyAdvice advice = new GrNotVoidResponseBodyAdvice();
        CopyOnWriteArrayList<ResponseBodyAdvicePredicate> predicates = new CopyOnWriteArrayList<>();
        predicates.add(advice);
        advice.setPredicates(predicates);
        advice.setResponseBodyAdviceProcessor(advice);
        return advice;
    }

    @Bean
    @ConditionalOnMissingBean
    public GrVoidResponseBodyAdvice grVoidResponseBodyAdvice() {
        GrVoidResponseBodyAdvice advice = new GrVoidResponseBodyAdvice();
        CopyOnWriteArrayList<ResponseBodyAdvicePredicate> predicates = new CopyOnWriteArrayList<>();
        predicates.add(advice);
        advice.setPredicates(predicates);
        advice.setResponseBodyAdviceProcessor(advice);
        return advice;
    }

    @Bean
    @ConditionalOnMissingBean
    public BeforeControllerAdviceProcess beforeControllerAdviceProcess() {
        return new BeforeControllerAdviceProcessImpl(globalResponseProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public FrameworkExceptionAdvice frameworkExceptionAdvice(BeforeControllerAdviceProcess adviceProcess) {
        FrameworkExceptionAdvice advice = new FrameworkExceptionAdvice();
        advice.setRejectStrategy(new DefaultRejectStrategyImpl());
        advice.setControllerAdviceProcessor(advice);
        advice.setBeforeControllerAdviceProcess(adviceProcess);
        advice.setControllerAdviceHttpProcessor(advice);
        return advice;
    }

    @Bean
    @ConditionalOnMissingBean
    public DataExceptionAdvice dataExceptionAdvice(BeforeControllerAdviceProcess adviceProcess) {
        DataExceptionAdvice advice = new DataExceptionAdvice();
        advice.setRejectStrategy(new DefaultRejectStrategyImpl());
        advice.setControllerAdviceProcessor(advice);
        advice.setBeforeControllerAdviceProcess(adviceProcess);
        advice.setControllerAdviceHttpProcessor(advice);
        return advice;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultGlobalExceptionAdvice defaultGlobalExceptionAdvice(BeforeControllerAdviceProcess adviceProcess) {
        DefaultGlobalExceptionAdvice advice = new DefaultGlobalExceptionAdvice();
        advice.setRejectStrategy(new DefaultRejectStrategyImpl());
        CopyOnWriteArrayList<ControllerAdvicePredicate> predicates = new CopyOnWriteArrayList<>();
        predicates.add(advice);
        advice.setPredicates(predicates);
        advice.setControllerAdviceProcessor(advice);
        advice.setBeforeControllerAdviceProcess(adviceProcess);
        advice.setControllerAdviceHttpProcessor(advice);
        return advice;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultValidationExceptionAdvice defaultValidationExceptionAdvice(BeforeControllerAdviceProcess adviceProcess) {
        DefaultValidationExceptionAdvice advice = new DefaultValidationExceptionAdvice();
        advice.setRejectStrategy(new DefaultRejectStrategyImpl());
        advice.setControllerAdviceProcessor(advice);
        advice.setBeforeControllerAdviceProcess(adviceProcess);
        advice.setControllerAdviceHttpProcessor(advice);
        return advice;
    }

    @Bean
    @ConditionalOnProperty(prefix = PropertiesConstants.WEB_RESPONSE, name = "i18n", havingValue = "true")
    public GrI18nResponseBodyAdvice grI18nResponseBodyAdvice() {
        GrI18nResponseBodyAdvice advice = new GrI18nResponseBodyAdvice();
        CopyOnWriteArrayList<ResponseBodyAdvicePredicate> predicates = new CopyOnWriteArrayList<>();
        predicates.add(advice);
        advice.setPredicates(predicates);
        advice.setResponseBodyAdviceProcessor(advice);
        return advice;
    }

    @Bean
    @ConditionalOnProperty(prefix = PropertiesConstants.WEB_RESPONSE, name = "i18n", havingValue = "true")
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n", "i18n/empty-messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.CHINA);
        return messageSource;
    }

    @Bean
    @ConditionalOnMissingBean
    public ResponseFactory responseFactory() {
        return new DefaultResponseFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResponseStatusFactory responseStatusFactory() {
        return new DefaultResponseStatusFactoryImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionAliasRegister exceptionAliasRegister() {
        return new ExceptionAliasRegister();
    }

    @Bean
    @ConditionalOnMissingBean
    public AdviceSupport adviceSupport() {
        return new AdviceSupport();
    }

    @Bean
    @ConditionalOnClass(ReturnTypeParser.class)
    @ConditionalOnMissingBean
    public ApiDocGlobalResponseHandler apiDocGlobalResponseHandler() {
        return new ApiDocGlobalResponseHandler(globalResponseProperties);
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Latte Starter] - Auto Configuration 'Web-Global Response' completed initialization.");
    }
}
