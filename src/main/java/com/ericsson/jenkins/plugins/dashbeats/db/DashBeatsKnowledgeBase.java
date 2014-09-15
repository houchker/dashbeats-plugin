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
package com.ericsson.jenkins.plugins.dashbeats.db;

import com.ericsson.jenkins.plugins.dashbeats.Messages;
import com.ericsson.jenkins.plugins.dashbeats.client.DashBeatsPublisher;
import com.ericsson.jenkins.plugins.dashbeats.client.DashBeatsRestClient;
import com.ericsson.jenkins.plugins.dashbeats.json.JsonFactory;
import com.ericsson.jenkins.plugins.dashbeats.model.StatsSummary;
import com.sonyericsson.jenkins.plugins.bfa.db.KnowledgeBase;
import com.sonyericsson.jenkins.plugins.bfa.db.LocalFileKnowledgeBase;
import com.sonyericsson.jenkins.plugins.bfa.graphs.GraphFilterBuilder;
import com.sonyericsson.jenkins.plugins.bfa.statistics.Statistics;
import hudson.Extension;
import hudson.Util;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DashBeatsKnowledgeBase : Main class of the plugin.
 *
 * Implement of the Build-Failure-Analyser KnowledgeBase. Along with the DashBeatsDescriptor,
 * This class is the extension of the BFA plugin, which will add DashBeats item in the drop down
 * list in the configuration (in addition to Local Jenkins, MongoDB). Whenever the saveStatistics
 * is called, it will publish data to the DashBeats server.
 *
 *
 * Created by ekongto on 2014-09-03.
 */
public class DashBeatsKnowledgeBase extends LocalFileKnowledgeBase {

    private static final String DEFAULT_URL = "http://localhost:3030";
    private static final String DEFAULT_AUTH_TOKEN = "YOUR_AUTH_TOKEN";

    /* URL of the DashBeats server */
    private String url = DEFAULT_URL;
    /* DashBeats authorization token require to publish stats */
    private String authToken = DEFAULT_AUTH_TOKEN;
    /* DashBeats Statistics Aggregrator */
    private DashBeatsStore store;
    /* DashBeats publisher */
    private DashBeatsPublisher publisher;

    /**
     * Default constructor.
     */
    @DataBoundConstructor
    public DashBeatsKnowledgeBase(String url, String authToken) {
        this.url = url;
        this.authToken = authToken;
        this.store = new DashBeatsStore();
        this.publisher = new DashBeatsPublisher(url, new DashBeatsRestClient(), new JsonFactory(authToken));
    }

    /**
     * Get the DashBeats URL
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the DashBeats URL
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get the DashBeats auth_token
     * @return auth_token
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Set the Dashing auth_token
     *
     * @param authToken
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public void start() {
        //when the BFA starts
    }

    @Override
    public void stop() {
        //when the BFA stops
    }

    /**
     * Force to be true as it allows to call the saveStatistics method.
     *
     * @return always true
     */
    @Override
    public boolean isStatisticsEnabled() {
        return true;
    }

    /**
     * Force to be true as it allows to call the saveStatistics method.
     *
     * @return always true
     */
    @Override
    public boolean isSuccessfulLoggingEnabled() {
        return true;
    }

    /**
     * This method is called to save the statistics, instead it will store them into a map,
     * aggregate them into a summary and publish the summary to DashBeats server.
     * However, the statistics are NOT persisted.
     *
     * @param stat
     * @throws Exception
     */
    @Override
    public void saveStatistics(Statistics stat) throws Exception {
        store.store(stat);
        StatsSummary statSummary = store.createSummary();
        publisher.publish(statSummary);
    }

    /**
     * This method is not implemented as statistics are not saved into a storage.
     * @param filter
     * @param limit
     * @return
     * @throws Exception
     */
    @Override
    public List<Statistics> getStatistics(GraphFilterBuilder filter, int limit)
            throws Exception {
        return new ArrayList<Statistics>();
    }

    /**
     * Get the descriptor instance associated with this class.
     * @return
     */
    @Override
    public Descriptor<KnowledgeBase> getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(DashBeatsDescriptor.class);
    }

    /**
     * Descriptor for {@link LocalFileKnowledgeBase}.
     */
    @Extension
    public static class DashBeatsDescriptor extends KnowledgeBaseDescriptor {

        @Override
        public String getDisplayName() {
            return "DashBeats";
        }

        /**
         * Convenience method for jelly.
         * @return the default url.
         */
        public String getDefaultUrl() {
            return DEFAULT_URL;
        }

        /**
         * Convenience method for jelly.
         * @return the default auth token.
         */
        public String getDefaultAuthToken() {
            return DEFAULT_AUTH_TOKEN;
        }

        /**
         * Checks that the url is not empty and does not contain space.
         *
         * @param value the pattern to check.
         * @return {@link hudson.util.FormValidation#ok()} if everything is well.
         */
        public FormValidation doCheckUrl(@QueryParameter("value") String value) {
            if (Util.fixEmpty(value) == null) {
                return FormValidation.error("Please provide a host name!");
            } else {
                Matcher m = Pattern.compile("\\s").matcher(value);
                if (m.find()) {
                    return FormValidation.error("Host name contains white space!");
                }
                return FormValidation.ok();
            }
        }

        /**
         * Checks that the authorization token is not empty and does not contain space.
         *
         * @param value the pattern to check.
         * @return {@link hudson.util.FormValidation#ok()} if everything is well.
         */
        public FormValidation doCheckAuthToken(@QueryParameter("value") String value) {
            if (Util.fixEmpty(value) == null) {
                return FormValidation.error("Please provide an authorization token!");
            } else {
                Matcher m = Pattern.compile("\\s").matcher(value);
                if (m.find()) {
                    return FormValidation.error("authorization token contains white space!");
                }
                return FormValidation.ok();
            }
        }

        /**
         * Tests if the provided parameters can connect to the DashBeats.
         * @param paramUrl the host name.
         * @param paramAuthToken the token.
         * @return {@link FormValidation#ok() } if can be done,
         *         {@link FormValidation#error(java.lang.String) } otherwise.
         */
        public FormValidation doTestConnection(
                @QueryParameter("url") final String paramUrl,
                @QueryParameter("authToken") final String paramAuthToken) {

            try {
                new DashBeatsPublisher(paramUrl, new DashBeatsRestClient(), new JsonFactory(paramAuthToken)).ping();
            } catch (Exception e) {
                return FormValidation.error(e, Messages.DashBeats_ConnectionError());
            }
            return FormValidation.ok(Messages.DashBeats_ConnectionOK());
        }
    }
}
