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



package com.ppxb.latte.starter.log.core.dao.impl;

import com.ppxb.latte.starter.log.core.dao.LogDao;
import com.ppxb.latte.starter.log.core.model.LogRecord;

import java.util.LinkedList;
import java.util.List;

public class LogDaoDefaultImpl implements LogDao {

    private final List<LogRecord> logRecords = new LinkedList<>();

    private int capacity = 100;

    private boolean reverse = true;

    @Override
    public List<LogRecord> list() {
        synchronized (this.logRecords) {
            return List.copyOf(this.logRecords);
        }
    }

    @Override
    public void add(LogRecord record) {
        synchronized (this.logRecords) {
            while (this.logRecords.size() >= this.capacity) {
                this.logRecords.remove(this.reverse ? this.capacity - 1 : 0);
            }
            if (this.reverse) {
                this.logRecords.addFirst(record);
            } else {
                this.logRecords.add(record);
            }
        }
    }

    public void setCapacity(int capacity) {
        synchronized (this.logRecords) {
            this.capacity = capacity;
        }
    }

    public void setReverse(boolean reverse) {
        synchronized (this.logRecords) {
            this.reverse = reverse;
        }
    }
}
