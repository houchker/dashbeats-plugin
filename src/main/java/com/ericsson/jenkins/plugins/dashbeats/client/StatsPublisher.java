/*
 * The MIT License
 *
 * Copyright 2014 Ericsson. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ericsson.jenkins.plugins.dashbeats.client;

import com.ericsson.jenkins.plugins.dashbeats.model.StatsSummary;
import com.sonyericsson.jenkins.plugins.bfa.statistics.Statistics;

/**
 * Interface defining publish methods
 *
 * Created by ekongto on 2014-09-10.
 */
public interface StatsPublisher {

    /**
     * Ping the DashBeats server
     *
     * @return response code
     */
    public int ping();

    /**
     * Publish all stats to the receiver
     *
     * @param stat
     */
    public void publish(StatsSummary stat);

    /**
     * Publish stats to a specified widget on DashBeats server
     *
     * @param type
     * @param summary
     * @return
     */
    public int publish(WidgetType type, StatsSummary summary);
}
