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
package com.ericsson.jenkins.plugins.dashbeats.json;

import com.ericsson.jenkins.plugins.dashbeats.model.*;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JsonFactory creates json objects by extracting data from a stat summary.
 *
 * Created by ekongto on 2014-09-10.
 */
public class JsonFactory {

    /* The DashBeats authorization token */
    private String authToken;

    /**
     * Constructor, instantiate a json factory.
     *
     * @param authToken
     */
    public JsonFactory(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Create a skeleton of a json obect having the auth_token parameter.
     *
     * @return a json object
     */
    public JSONObject createJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("auth_token", authToken);
        return jsonObject;
    }

    /**
     * create a welcome json object to be published to DashBeats server
     *
     * @return
     */
    public JSONObject createWelcome() {
        String welcome = "DashBeats statistics update at " + new Date();
        JSONObject jsonObject = createJson();
        jsonObject.put("text", welcome);
        return jsonObject;
    }

    /**
     * create a 'common fault causes' json object to be published to DashBeats server
     *
     * @return
     */
    public List<JSONObject> createCommonFaultCauses(StatsSummary summary) {
        List<JSONObject> list = new ArrayList<JSONObject>();
        JSONObject jsonObject;
        for (FaultCauseInfo data : summary.getCommonFaultCauses()) {
            jsonObject = createJson();
            StringBuilder jsonContent = new StringBuilder("")
                    .append(data.getFaultCause()).append("  ")
                    .append(" failures(").append(data.getFailures()).append(")");
            jsonObject.put("label", jsonContent.toString());
            list.add(jsonObject);
        }
        return list;
    }

    /**
     * create a 'latest failed builds' json object to be published to DashBeats server
     *
     * @return
     */
    public List<JSONObject> createLatestFailedBuilds(StatsSummary summary) {
        List<JSONObject> list = new ArrayList<JSONObject>();
        JSONObject jsonObject;
        for (BuildInfo data : summary.getLatestFailedBuilds()) {
            jsonObject = createJson();
            StringBuilder jsonContent = new StringBuilder("")
                    .append(data.getJob())
                    .append("  build#").append(data.getBuildNumber())
                    .append("  failures(").append(data.getFailures()).append(")");
            jsonObject.put("label", jsonContent.toString());
            list.add(jsonObject);
        }
        return list;
    }

    /**
     * create a 'latest builds' json object to be published to DashBeats server
     *
     * @return
     */
    public List<JSONObject> createLatestBuilds(StatsSummary summary) {
        List<JSONObject> list = new ArrayList<JSONObject>();
        JSONObject jsonObject;
        for (BuildInfo data : summary.getLatestBuilds()) {
            jsonObject = createJson();
            StringBuilder jsonContent = new StringBuilder("")
                    .append(data.getJob())
                    .append("  build#").append(data.getBuildNumber())
                    .append("  ").append(data.getResult())
                    .append("  total(").append(data.getTotal()).append(")")
                    .append("  successes(").append(data.getSuccesses()).append(")")
                    .append("  failures(").append(data.getFailures()).append(")")
                    .append("  unstables(").append(data.getUnstables()).append(")")
                    .append("  aborts(").append(data.getAborts()).append(")")
                    .append("  fail rate(").append(data.getRateOfFailure()).append("%)");
            jsonObject.put("label", jsonContent.toString());
            list.add(jsonObject);
        }
        return list;

    }

    /**
     * create a 'top failed jobs' json object to be published to DashBeats server
     *
     * @return
     */
    public List<JSONObject> createTopFailedJobs(StatsSummary summary) {
        List<JSONObject> list = new ArrayList<JSONObject>();
        JSONObject jsonObject;
        for (BuildInfo data : summary.getTopFailedJobs()) {
            jsonObject = createJson();
            StringBuilder jsonContent = new StringBuilder("")
                    .append(data.getJob())
                    .append("  build#").append(data.getBuildNumber())
                    .append("  failures(").append(data.getFailures()).append(")");
            jsonObject.put("label", jsonContent.toString());
            list.add(jsonObject);
        }
        return list;
    }
}
