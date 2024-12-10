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



package com.ppxb.latte.starter.extension.tenant.autoconfigure;

import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import com.ppxb.latte.starter.extension.tenant.enums.TenantIsolationLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(PropertiesConstants.TENANT)
public class TenantProperties {

    private boolean enabled = true;

    private TenantIsolationLevel isolationLevel = TenantIsolationLevel.LINE;

    private String tenantIdColumn = "tenant_id";

    private String tenantIdHeader = "X-Tenant-Id";

    private Long superTenantId = -1L;

    private List<String> ignoreTables;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public TenantIsolationLevel getIsolationLevel() {
        return isolationLevel;
    }

    public void setIsolationLevel(TenantIsolationLevel isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

    public String getTenantIdColumn() {
        return tenantIdColumn;
    }

    public void setTenantIdColumn(String tenantIdColumn) {
        this.tenantIdColumn = tenantIdColumn;
    }

    public String getTenantIdHeader() {
        return tenantIdHeader;
    }

    public void setTenantIdHeader(String tenantIdHeader) {
        this.tenantIdHeader = tenantIdHeader;
    }

    public Long getSuperTenantId() {
        return superTenantId;
    }

    public void setSuperTenantId(Long superTenantId) {
        this.superTenantId = superTenantId;
    }

    public List<String> getIgnoreTables() {
        return ignoreTables;
    }

    public void setIgnoreTables(List<String> ignoreTables) {
        this.ignoreTables = ignoreTables;
    }
}
