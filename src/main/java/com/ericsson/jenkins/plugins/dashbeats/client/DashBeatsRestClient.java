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

import net.sf.json.JSONObject;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * Implementation of a Rest client to push data to DashBeats server
 * <p/>
 * Created by ekongto on 2014-09-08.
 */
public class DashBeatsRestClient implements RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashBeatsRestClient.class.getName());

    private static final int BAD_REQUEST = 400;

    private Client client;

    /**
     * Constructor of the rest client
     */
    public DashBeatsRestClient() {
        client = ClientBuilder.newClient().register(JacksonFeature.class);
    }

    /**
     * Post method to publish data to Dashing Rest API.
     * Dashng API expect to receive string in json format.
     * <p/>
     * <p>e.g. Welcome widget expects :
     * <br>{"text": "Welcome"}
     * <p/>
     * <p>e.g. Common Failure Cause widget expect :
     * <br>{"items":
     * {"label": "Connection Error", "value": "20"},
     * {"label": "NullPointer Exception", "value": "44"}]}
     *
     * @param url
     * @param jsonObject
     * @return response code
     */
    public int post(String url, JSONObject jsonObject) {

        try {
            Response response = client
                    .target(url)
                    .request()
                    .post(Entity.json(jsonObject));

            return response.getStatus();
        } catch (Exception e) {
            LOGGER.info("Exception caught while doing a post to DashBeast server:" + e);
        }

        return BAD_REQUEST;
    }

    /**
     * Ping the server by doing a simple http get
     *
     * @param url
     * @return response code
     */
    @Override
    public int ping(String url) {

        try {
            Response response = client
                    .target(url)
                    .request()
                    .get();

            return response.getStatus();
        } catch (Exception e) {
            LOGGER.info("Exception caught while doing a ping to DashBeast server:" + e);
        }

        return BAD_REQUEST;
    }
}