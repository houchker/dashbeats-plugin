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


import com.ericsson.jenkins.plugins.dashbeats.db.SummaryMockFactory;
import com.ericsson.jenkins.plugins.dashbeats.json.JsonFactory;
import com.ericsson.jenkins.plugins.dashbeats.model.StatsSummary;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.List;

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
    private DashBeatsClient client;
    private JsonFactory jsonFactory;

    @Before
    public void setUp() {
        startDate = new Date();
        lastDate = new Date();
        buildCount = 1;
        this.url = DashBeatsPublisher.DEFAULT_URL;
        this.authToken = DashBeatsPublisher.DEFAULT_AUTH_TOKEN;
        this.statsSummary = SummaryMockFactory.createSummary(startDate, lastDate);
        client = Mockito.mock(DashBeatsClient.class);
        jsonFactory = new JsonFactory(authToken);
        this.publisher = new DashBeatsPublisher(url, client, jsonFactory);
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
        //GIVEN
        JSONObject json = jsonFactory.createWelcome();
        //WHEN
        Mockito.when(client.post(url + DashBeatsPublisher.WELCOME_PATH, json)).thenReturn(204);
        int code = publisher.publish(WidgetType.WELCOME, statsSummary);
        //THEN
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
        //GIVEN
        List<JSONObject> data = jsonFactory.createCommonFaultCauses(statsSummary);
        JSONObject jsonObject = jsonFactory.createJson();
        jsonObject.put("items", data);
        //WHEN
        Mockito.when(client.post(url + DashBeatsPublisher.COMMON_FAULT_CAUSES_PATH, jsonObject)).thenReturn(204);
        int code = publisher.publish(WidgetType.COMMON_FAULT_CAUSES, statsSummary);
        //THEN
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
        //GIVEN
        List<JSONObject> data = jsonFactory.createLatestFailedBuilds(statsSummary);
        JSONObject jsonObject = jsonFactory.createJson();
        jsonObject.put("items", data);
        //WHEN
        Mockito.when(client.post(url + DashBeatsPublisher.LATEST_FAILED_BUILDS_PATH, jsonObject)).thenReturn(204);
        int code = publisher.publish(WidgetType.LATEST_FAILED_BUILDS, statsSummary);
        //THEN
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
        //GIVEN
        List<JSONObject> data = jsonFactory.createLatestBuilds(statsSummary);
        JSONObject jsonObject = jsonFactory.createJson();
        jsonObject.put("items", data);
        //WHEN
        Mockito.when(client.post(url + DashBeatsPublisher.LATEST_BUILDS_PATH, jsonObject)).thenReturn(204);
        int code = publisher.publish(WidgetType.LATEST_BUILD, statsSummary);
        //THEN
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
        //GIVEN
        List<JSONObject> data = jsonFactory.createTopFailedJobs(statsSummary);
        JSONObject jsonObject = jsonFactory.createJson();
        jsonObject.put("items", data);
        //WHEN
        Mockito.when(client.post(url + DashBeatsPublisher.TOP_FAILED_JOBS_PATH, jsonObject)).thenReturn(204);
        int code = publisher.publish(WidgetType.TOP_FAILED_JOBS, statsSummary);
        //THEN
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
        //WHEN
        Mockito.when(client.ping(url)).thenReturn(200);
        int code = publisher.ping();
        //THEN
        Assert.assertEquals(200, code);
    }
}
