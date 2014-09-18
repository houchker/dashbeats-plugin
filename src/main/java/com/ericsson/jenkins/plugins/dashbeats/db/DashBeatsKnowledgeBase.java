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
import com.sonyericsson.jenkins.plugins.bfa.model.FailureCause;
import com.sonyericsson.jenkins.plugins.bfa.statistics.Statistics;
import com.thoughtworks.xstream.XStream;
import hudson.Extension;
import hudson.Util;
import hudson.XmlFile;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.XStream2;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(DashBeatsKnowledgeBase.class.getName());

    private static final String DEFAULT_URL = "http://localhost:3030";
    private static final String DEFAULT_AUTH_TOKEN = "YOUR_AUTH_TOKEN";

    /* URL of the DashBeats server */
    private String url = DEFAULT_URL;
    /* DashBeats authorization token require to publish stats */
    private String authToken = DEFAULT_AUTH_TOKEN;
    /* DashBeats Statistics Aggregrator */
    private transient DashBeatsStore store;
    /* DashBeats publisher */
    private transient DashBeatsPublisher publisher;
    /* When starting up, the causes list is not yet reloaded
     * Use this flag to reload statsStore at first build event only once */
    private transient boolean isReloaded = false;
    /* BFA statistics store, marked as transient as it persists on demand, not with BFA config */
    private transient Map<String, Statistics> statsStore;

    private static final String DASHBEATS_STORE_FILENAME = "dashbeats-plugin-store.xml";
    private static final XStream XSTREAM = new XStream2();

    static {
        XSTREAM.alias("DashBeatsStore", DashBeatsStore.class);
    }

    /**
     * Default constructor.
     */
    @DataBoundConstructor
    public DashBeatsKnowledgeBase(String url, String authToken) {
        super();
        this.url = url;
        this.authToken = authToken;
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
        this.store = new DashBeatsStore();
        this.publisher = new DashBeatsPublisher(url, new DashBeatsRestClient(), new JsonFactory(authToken));
        this.statsStore = new HashMap<String, Statistics>();
        this.publisher.publishWelcome();
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
        if (!isReloaded) {
            isReloaded = true;
            loadStore(getCauses());
        }
        // use job name and build number as unique key
        String key = stat.getProjectName() + "#" + stat.getBuildNumber();
        // update DashBeatsStore and publish only for new stats
        if (!statsStore.containsKey(key)) {
            statsStore.put(key, stat);
            // persist the stats store into file
            saveStore();
            // update the DashBeats store
            store.update(stat, getCauses());
            // create the stat summary to be published
            StatsSummary statSummary = store.createSummary();
            // publish
            publisher.publish(statSummary);
        }
    }

    /**
     * This method returns all stats from the store. The filter is ignored.
     *
     * @param filter
     * @param limit
     * @return
     * @throws Exception
     */
    @Override
    public List<Statistics> getStatistics(GraphFilterBuilder filter, int limit)
            throws Exception {
        List<Statistics> list = new ArrayList<Statistics>();
        for (Statistics stat : statsStore.values()) {
            if (list.size() >= limit) {
                break;
            }
            if(StatsFilterUtil.isIncluded(stat, filter)) {
                list.add(stat);
            }
        }
        return list;
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
                new DashBeatsPublisher(paramUrl, new DashBeatsRestClient(), new JsonFactory(paramAuthToken)).publishWelcome();
            } catch (Exception e) {
                return FormValidation.error(e, Messages.DashBeats_ConnectionError());
            }
            return FormValidation.ok(Messages.DashBeats_ConnectionOK());
        }
    }

    /**
     * Get the jenkins root dir
     *
     * @return
     */
    private String getJenkinsRootDir() {
        return Jenkins.getInstance().getRootDir().getAbsolutePath();
    }

    /**
     * Load all stats from the store, if existed, at start up
     *
     * @param causes
     */
    private void loadStore(final Collection<FailureCause> causes) {
        File storeFile = new File(getJenkinsRootDir(), DASHBEATS_STORE_FILENAME);
        LOGGER.info("Read DashBeats store from file : {}", storeFile.getAbsolutePath());
        if (storeFile.exists()) {
            try {
                statsStore = (Map<String, Statistics>) new XmlFile(XSTREAM, storeFile).read();
            } catch (IOException e) {
                LOGGER.error("Failed to read DashBeats store from file : {}", e);
            }
        }
        // update build info, failed builds and fault cause stores from statsStore
        for (Statistics stats : statsStore.values()) {
            store.update(stats, causes);
        }
    }

    /**
     * Save to file stats store
     */
    private void saveStore() {
        File storeFile = new File(getJenkinsRootDir(), DASHBEATS_STORE_FILENAME);
        try {
            new XmlFile(XSTREAM, storeFile).write(statsStore);
        } catch (IOException e) {
            LOGGER.error("Failed to write DashBeats store to file : {}", e);
        }
    }

}
