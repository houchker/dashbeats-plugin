/*
 * The MIT License
 *
 * Copyright 2012 Sony Mobile Communications AB. All rights reserved.
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
/*
 * The MIT License
 *
 * Copyright 2012 Sony Mobile Communications AB. All rights reserved.
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
package com.ericsson.jenkins.plugins.dashbeats.db;


import com.ericsson.jenkins.plugins.dashbeats.client.DashBeatsPublisher;
import com.ericsson.jenkins.plugins.dashbeats.client.DashBeatsRestClient;
import com.ericsson.jenkins.plugins.dashbeats.client.WidgetType;
import com.ericsson.jenkins.plugins.dashbeats.json.JsonFactory;
import com.ericsson.jenkins.plugins.dashbeats.model.StatsSummary;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Created by ekongto on 2014-09-08.
 */
public class DashBeatsPublisherTest {

    private Date startDate;
    private Date lastDate;
    private int buildCount;
    private String url;
    private String authToken;
    private StatsSummary statsSummary;
    private DashBeatsPublisher publisher;

    @Before
    public void setUp() {
        startDate = new Date();
        lastDate = new Date();
        buildCount = 1;
        this.url = "http://localhost:3030";
        this.authToken = "YOUR_AUTH_TOKEN";
        this.statsSummary = SummaryMockFactory.createSummary(startDate, lastDate);
        this.publisher = new DashBeatsPublisher(url, new DashBeatsRestClient(), new JsonFactory(authToken));
    }

    /**
     * GIVEN a url of the DashBeats server
     * and a valid authorization token
     * and a valid welcome message
     * WHEN sending it to DashBeats server
     * THEN the server receives it successfully
     * and the return a successful code 204
     */
    @Test
    public void shouldSendWelcomeSuccessfully() {
        int code = publisher.publish(WidgetType.WELCOME, statsSummary);
        Assert.assertEquals(204, code);
    }

    /**
     * GIVEN a url of the DashBeats server
     * and a valid authorization token
     * and a valid 'common fault causes' content
     * WHEN sending it to DashBeats server
     * THEN the server receives it successfully
     * and the return a successful code 204
     */
    @Test
    public void shouldSendCommonFaultCausesSuccessfully() throws Exception {
        int code = publisher.publish(WidgetType.COMMON_FAULT_CAUSES, statsSummary);
        Assert.assertEquals(204, code);
    }

    /**
     * GIVEN a url of the DashBeats server
     * and a valid authorization token
     * and a valid 'latest failed builds' content
     * WHEN sending it to DashBeats server
     * THEN the server receives it successfully
     * and the return a successful code 204
     */
    @Test
    public void shouldSendLatestFailedBuildsSuccessfully() throws Exception {
        int code = publisher.publish(WidgetType.LATEST_FAILED_BUILDS, statsSummary);
        Assert.assertEquals(204, code);
    }

    /**
     * GIVEN a url of the DashBeats server
     * and a valid authorization token
     * and a valid 'latest failed builds' content
     * WHEN sending it to DashBeats server
     * THEN the server receives it successfully
     * and the return a successful code 204
     */
    @Test
    public void shouldSendLatestBuildsSuccessfully() throws Exception {
        int code = publisher.publish(WidgetType.LATEST_BUILD, statsSummary);
        Assert.assertEquals(204, code);
    }

    /**
     * GIVEN a url of the DashBeats server
     * and a valid authorization token
     * and a valid 'latest failed builds' content
     * WHEN sending it to DashBeats server
     * THEN the server receives it successfully
     * and the return a successful code 204
     */
    @Test
    public void shouldSendTopFailedJobsSuccessfully() throws Exception {
        int code = publisher.publish(WidgetType.TOP_FAILED_JOBS, statsSummary);
        Assert.assertEquals(204, code);
    }

    /**
     * GIVEN a url of the DashBeats server
     * WHEN doing a Http Get
     * THEN the server respond successfully
     * and the return code is OK
     */
    @Test
    public void testPing() {
        int code = publisher.ping();
        Assert.assertEquals(200, code);
    }
}
