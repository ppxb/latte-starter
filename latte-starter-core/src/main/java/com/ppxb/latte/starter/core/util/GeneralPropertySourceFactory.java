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

import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;

/**
 * 通用属性源工厂类，支持加载 YAML 和 Properties 格式的配置文件。
 * <p>
 * 该类扩展了 {@link DefaultPropertySourceFactory}，增加了对 YAML 格式配置文件的支持。
 * 工作流程如下：
 * <ul>
 * <li>检查配置文件扩展名</li>
 * <li>对于 .yml 或 .yaml 文件，使用 {@link YamlPropertySourceLoader} 加载</li>
 * <li>其他格式的文件，使用默认的属性源工厂加载</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>{@code
 * @Configuration
 * 
 * @PropertySource(value = "classpath:custom.yml", factory = GeneralPropertySourceFactory.class)
 *                       public class CustomConfiguration {
 *                       // ...
 *                       }
 *                       }</pre>
 *
 * @author ppxb
 * @see DefaultPropertySourceFactory
 * @see YamlPropertySourceLoader
 * @since 1.0.0
 */
public class GeneralPropertySourceFactory extends DefaultPropertySourceFactory {

    /**
     * 创建属性源。
     * <p>
     * 该方法根据资源文件的扩展名选择合适的加载器：
     * <ul>
     * <li>对于 .yml 或 .yaml 文件，使用 {@link YamlPropertySourceLoader}</li>
     * <li>对于其他格式文件，委托给父类处理</li>
     * </ul>
     *
     * @param name            属性源名称，可以为空
     * @param encodedResource 编码的资源对象
     * @return 创建的属性源
     * @throws IOException 如果加载资源时发生IO错误
     */
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) throws IOException {
        Resource resource = encodedResource.getResource();
        String resourceName = resource.getFilename();
        if (CharSequenceUtil.isNotBlank(resourceName) && CharSequenceUtil.endWithAny(resourceName, ".yml", ".yaml")) {
            return new YamlPropertySourceLoader().load(resourceName, resource).getFirst();
        }
        return super.createPropertySource(name, encodedResource);
    }
}
