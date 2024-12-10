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



package com.ppxb.latte.starter.extension.tenant.handler;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.ppxb.latte.starter.extension.tenant.config.TenantDataSource;
import com.ppxb.latte.starter.extension.tenant.config.TenantDataSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class DefaultTenantDataSourceHandler implements TenantDataSourceHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultTenantDataSourceHandler.class);

    private final DynamicRoutingDataSource dynamicRoutingDataSource;

    private final DefaultDataSourceCreator dataSourceCreator;

    private final TenantDataSourceProvider tenantDataSourceProvider;

    public DefaultTenantDataSourceHandler(TenantDataSourceProvider tenantDataSourceProvider,
                                          DynamicRoutingDataSource dynamicRoutingDataSource,
                                          DefaultDataSourceCreator dataSourceCreator) {
        this.tenantDataSourceProvider = tenantDataSourceProvider;
        this.dynamicRoutingDataSource = dynamicRoutingDataSource;
        this.dataSourceCreator = dataSourceCreator;
    }

    @Override
    public void changeDataSource(String dataSourceName) {
        if (!this.containsDataSource(dataSourceName)) {
            TenantDataSource tenantDataSource = tenantDataSourceProvider.getByTenantId(dataSourceName);
            if (null == tenantDataSource) {
                throw new IllegalArgumentException("Data source [%s] configuration not found"
                    .formatted(dataSourceName));
            }
            DataSource datasource = this.createDataSource(tenantDataSource);
            dynamicRoutingDataSource.addDataSource(dataSourceName, datasource);
            log.info("Load data source: {}", dataSourceName);
        }
        DynamicDataSourceContextHolder.push(dataSourceName);
        log.info("Change data source: {}", dataSourceName);
    }

    @Override
    public boolean containsDataSource(String dataSourceName) {
        return CharSequenceUtil.isNotBlank(dataSourceName) && dynamicRoutingDataSource.getDataSources()
            .containsKey(dataSourceName);
    }

    @Override
    public DataSource createDataSource(TenantDataSource tenantDataSource) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        dataSourceProperty.setPoolName(tenantDataSource.getPoolName());
        dataSourceProperty.setDriverClassName(tenantDataSource.getDriverClassName());
        dataSourceProperty.setUrl(tenantDataSource.getUrl());
        dataSourceProperty.setUsername(tenantDataSource.getUsername());
        dataSourceProperty.setPassword(tenantDataSource.getPassword());
        return dataSourceCreator.createDataSource(dataSourceProperty);
    }

    @Override
    public void removeDataSource(String dataSourceName) {
        dynamicRoutingDataSource.removeDataSource(dataSourceName);
    }
}
