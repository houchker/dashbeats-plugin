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
package com.ericsson.jenkins.plugins.dashbeats.client;

import com.ericsson.jenkins.plugins.dashbeats.json.JsonFactory;
import com.ericsson.jenkins.plugins.dashbeats.model.StatsSummary;
import com.sonyericsson.jenkins.plugins.bfa.statistics.Statistics;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * This Class is responsible to publish data to the DashBeats server. It publish
 * Jenkins BFA Stats to every widget within the dashboard.
 * There are 5 widgets defined:
 * <ul>
 * <li>Welcome</li>
 * <li>Common Fault Causes</li>
 * <li>Latest Builds</li>
 * <li>Latest Failed Builds</li>
 * <li>Top Failed Jobs</li>
 * </ul>
 * Created by ekongto on 2014-09-08.
 */
public class DashBeatsPublisher implements StatsPublisher {

    /* The url of DashBeats */
    private String url;
    /* Json Factory */
    private JsonFactory jsonFactory;

    /* The url of the welcome widget on DashBeats */
    private String welcomeUrl;
    /* The url of the common fault causes widget on DashBeats */
    private String commonFaultCausesUrl;
    /* The url of the latest builds widget on DashBeats */
    private String latestBuildsUrl;
    /* The url of the latest failed builds widget on DashBeats */
    private String latestFailedBuildsUrl;
    /* The url of the top failed jobs widget on DashBeats */
    private String topFailedJobsUrl;

    /*The Rest client used to post data to DashBeats */
    private RestClient client;

    /**
     * Constructor, instantiate a rest client and widget's urls.
     *
     * @param url
     * @param jsonFactory
     */
    public DashBeatsPublisher(String url, DashBeatsRestClient client, JsonFactory jsonFactory) {
        this.url = url;
        this.jsonFactory = jsonFactory;
        this.client = client;

        this.welcomeUrl = url + "/widgets/welcome";
        this.commonFaultCausesUrl = url + "/widgets/common_fault_causes";
        this.latestBuildsUrl = url + "/widgets/latest_builds";
        this.latestFailedBuildsUrl = url + "/widgets/latest_failed_builds";
        this.topFailedJobsUrl = url + "/widgets/top_failed_jobs";
    }

    /**
     * Ping the DashBeats server
     *
     * @return response code
     */
    public int ping() {
        return client.ping(url);
    }

    /**
     * Publish stats to all widgets on DashBeats server
     *
     * @param summary
     */
    public void publish(StatsSummary summary) {
        publish(WidgetType.WELCOME, summary);
        publish(WidgetType.COMMON_FAULT_CAUSES, summary);
        publish(WidgetType.LATEST_FAILED_BUILDS, summary);
        publish(WidgetType.LATEST_BUILD, summary);
        publish(WidgetType.TOP_FAILED_JOBS, summary);
    }

    /**
     * Publish stats to a specified widget on DashBeats server
     *
     * @param type
     * @param summary
     * @return
     */
    public int publish(WidgetType type, StatsSummary summary) {

        int code = 400;
        List<JSONObject> data;

        switch (type) {
            case WELCOME:
                JSONObject json = jsonFactory.createWelcome();
                code = publishWelcome(json);
                break;
            case COMMON_FAULT_CAUSES:
                data = jsonFactory.createCommonFaultCauses(summary);
                code = publishCommonFaultCauses(data);
                break;
            case LATEST_FAILED_BUILDS:
                data = jsonFactory.createLatestFailedBuilds(summary);
                code = publishLatestFailedBuilds(data);
                break;
            case LATEST_BUILD:
                data = jsonFactory.createLatestBuilds(summary);
                code = publishLatestBuilds(data);
                break;
            case TOP_FAILED_JOBS:
                data = jsonFactory.createTopFailedJobs(summary);
                code = publishTopFailedJobs(data);
                break;
            default:
                break;
        }

        return code;
    }

    /**
     * Send data to the welcome widget
     *
     * @param jsonObject
     * @return response code
     */
    private int publishWelcome(JSONObject jsonObject) {
        return client.post(welcomeUrl, jsonObject);
    }

    /**
     * Send data to the common fault causes widget
     *
     * @param data
     * @return response code
     */
    private int publishCommonFaultCauses(List<JSONObject> data) {
        JSONObject jsonObject = jsonFactory.createJson();
        jsonObject.put("items", data);
        return client.post(commonFaultCausesUrl, jsonObject);
    }

    /**
     * Send data to the latest builds widgets
     *
     * @param data
     * @return response code
     */
    private int publishLatestBuilds(List<JSONObject> data) {
        JSONObject jsonObject = jsonFactory.createJson();
        jsonObject.put("items", data);
        return client.post(latestBuildsUrl, jsonObject);
    }

    /**
     * Send data to the latest failed builds widget
     *
     * @param data
     * @return response code
     */
    private int publishLatestFailedBuilds(List<JSONObject> data) {
        JSONObject jsonObject = jsonFactory.createJson();
        jsonObject.put("items", data);
        return client.post(latestFailedBuildsUrl, jsonObject);
    }

    /**
     * Send data to the top failed jobs widget
     * @param data
     * @return response code
     */
    private int publishTopFailedJobs(List<JSONObject> data) {
        JSONObject jsonObject = jsonFactory.createJson();
        jsonObject.put("items", data);
        return client.post(topFailedJobsUrl, jsonObject);
    }

}
