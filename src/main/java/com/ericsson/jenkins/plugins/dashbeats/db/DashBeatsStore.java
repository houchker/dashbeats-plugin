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

import com.ericsson.jenkins.plugins.dashbeats.model.BuildInfo;
import com.ericsson.jenkins.plugins.dashbeats.model.FaultCauseInfo;
import com.ericsson.jenkins.plugins.dashbeats.model.StatsSummary;
import com.ericsson.jenkins.plugins.dashbeats.model.Welcome;
import com.sonyericsson.jenkins.plugins.bfa.model.indication.FoundIndication;
import com.sonyericsson.jenkins.plugins.bfa.statistics.FailureCauseStatistics;
import com.sonyericsson.jenkins.plugins.bfa.statistics.Statistics;
import hudson.model.Result;

import java.util.*;

/**
 * This class stores statistics, builds info and fault causes. Builds info and fault causes
 * are information compiled from the BFA statistics. Whenever a statistic object is stored,
 * the builds info and the fault causes are updated.
 * <p/>
 * Created by ekongto on 2014-09-11.
 */
public class DashBeatsStore {

    /* BFA statistics store */
    private List<Statistics> statsStore;
    /* Build Info store using a HashMap to avoid duplicates */
    private Map<String, BuildInfo> buildInfoStore;
    /* Fault Cause Info store using a HashMap to avoid duplicates*/
    private Map<String, FaultCauseInfo> faultCauseInfoStore;

    private Date startDate;
    private Date lastDate;

    /**
     * Default constructor, instantiates statsStore, buildInfoStore and faultCauseInfoStore collections
     */
    public DashBeatsStore() {
        this.statsStore = new ArrayList<Statistics>();
        this.buildInfoStore = new HashMap<String, BuildInfo>();
        this.faultCauseInfoStore = new HashMap<String, FaultCauseInfo>();
        this.startDate = new Date();
        this.lastDate = new Date();
    }

    /**
     * Get the number of statistics in the store
     *
     * @return number of statistics
     */
    public int size() {
        return statsStore.size();
    }

    /**
     * Clear all stores, including statsStore, buildInfoStore and faultCauseStore
     */
    public void clear() {
        statsStore.clear();
    }

    /**
     * Store a statistics object into the statistics store. At the same time, it update
     * the build info store and the fault cause info store., by compiling the statistics store.
     *
     * @param stats
     */
    public void store(Statistics stats) {
        statsStore.add(stats);
        updateBuildInfoStore(stats);
        updateFaultCauseInfoStore(stats);
        Date date = stats.getStartingTime();
        if (startDate.after(date)) {
            startDate = date;
        }
        if (lastDate.before(date)) {
            lastDate = date;
        }
    }

    /**
     * Create a summary composing commonFailedBuilds, latestFailedBuilds, latestBuilds and topFailedJobs.
     *
     * @return
     */
    public StatsSummary createSummary() {
        Welcome welcome = new Welcome(StatsSummary.PRODUCT_NAME, startDate, lastDate);
        StatsSummary summary = new StatsSummary(startDate, lastDate, size());
        summary.setCommonFailureCauses(getCommonFaultCauses());
        summary.setLatestFailedBuilds(getLatestFailedBuilds());
        summary.setLatestBuilds(getLatestBuilds());
        summary.setTopFailedJobs(getTopFailedJobs());
        summary.setWelcome(welcome);
        return summary;
    }

    /**
     * Update the Build Info store by compiling data from StatsStore
     */
    private void updateBuildInfoStore(Statistics stats) {
        Date date = stats.getStartingTime();
        String job = stats.getProjectName();
        int buildNumber = stats.getBuildNumber();
        String result = stats.getResult();
        BuildInfo buildInfo = null;
        if (buildInfoStore.containsKey(job)) {
            buildInfo = buildInfoStore.get(job);
            buildInfo.incrementBuildResult(result);
            if (buildNumber > buildInfo.getBuildNumber()) {
                buildInfo.setDate(date);
                buildInfo.setResult(result);
                buildInfo.setBuildNumber(buildNumber);
            }
        } else {
            buildInfo = new BuildInfo(date, job, buildNumber, result);
            buildInfo.incrementBuildResult(result);
            buildInfoStore.put(job, buildInfo);
        }
    }

    /**
     * Update the fault cause store by compiling data from statistics store
     */
    private void updateFaultCauseInfoStore(Statistics stats) {
        // For every stat in the store, get the failure indications and store into
        // a map by ensuring the uniqueness. In case the same failure cause is already stored,
        // increment the number of failures and set the most recent date
        String cause = "";
        Date date = stats.getStartingTime();
        if (Result.FAILURE.toString().equals(stats.getResult())) {
            for (FailureCauseStatistics fcs : stats.getFailureCauseStatisticsList()) {
                for (FoundIndication indication : fcs.getIndications()) {
                    cause = indication.getMatchingString();
                    FaultCauseInfo commonFaultCause = null;

                    // already existed, then increment counter and update date if more recent
                    if (faultCauseInfoStore.containsKey(cause)) {
                        commonFaultCause = faultCauseInfoStore.get(cause);
                        commonFaultCause.setFailures(commonFaultCause.getFailures() + 1);
                        if (date.after(commonFaultCause.getDate())) {
                            faultCauseInfoStore.put(cause, commonFaultCause);
                        }
                    } else {
                        commonFaultCause = new FaultCauseInfo(date, cause);
                        faultCauseInfoStore.put(cause, commonFaultCause);
                    }

                }
            }
        }
    }

    /**
     * Get common fault causes, ordered by most frequent failures
     *
     * @return
     */
    private List<FaultCauseInfo> getCommonFaultCauses() {
        // once all failure causes are gathered and unique, sort them
        Map<Integer, FaultCauseInfo> commonFaultCauses = new TreeMap<Integer, FaultCauseInfo>();
        for (FaultCauseInfo commonFaultCause : faultCauseInfoStore.values()) {
            commonFaultCauses.put(commonFaultCause.getFailures(), commonFaultCause);
        }
        // use reverse order to have most recent on top
        commonFaultCauses = ((TreeMap) commonFaultCauses).descendingMap();
        // prepare a list with a MAX number
        List<FaultCauseInfo> list = new ArrayList<FaultCauseInfo>();
        for (FaultCauseInfo item : commonFaultCauses.values()) {
            if (list.size() < StatsSummary.MAX_PER_LIST) {
                list.add(item);
            }
        }
        return list;
    }

    /**
     * Get latest failed builds, ordered by most recent failed builds
     *
     * @return
     */
    private List<BuildInfo> getLatestFailedBuilds() {
        // once all failed builds are gathered and unique, sort them
        Map<Date, BuildInfo> latestFailedBuilds = new TreeMap<Date, BuildInfo>();
        for (BuildInfo latestFailedBuild : buildInfoStore.values()) {
            if (Result.FAILURE.toString().equals(latestFailedBuild.getResult())) {
                latestFailedBuilds.put(latestFailedBuild.getDate(), latestFailedBuild);
            }
        }
        // use reverse order to have most recent on top
        latestFailedBuilds = ((TreeMap) latestFailedBuilds).descendingMap();
        // prepare a list with a MAX number
        List<BuildInfo> list = new ArrayList<BuildInfo>();
        for (BuildInfo item : latestFailedBuilds.values()) {
            if (list.size() < StatsSummary.MAX_PER_LIST) {
                list.add(item);
            }
        }
        return list;
    }

    /**
     * Get latest builds, ordered by most recent date
     *
     * @return
     */
    private List<BuildInfo> getLatestBuilds() {
        // once all builds info are gathered and unique, sort them
        Map<Date, BuildInfo> latestBuilds = new TreeMap<Date, BuildInfo>();
        for (BuildInfo latestBuild : buildInfoStore.values()) {
            latestBuilds.put(latestBuild.getDate(), latestBuild);
        }
        // use reverse order to have most recent on top
        latestBuilds = ((TreeMap) latestBuilds).descendingMap();
        // prepare a list with a MAX number of fault causes
        List<BuildInfo> list = new ArrayList<BuildInfo>();
        for (BuildInfo item : latestBuilds.values()) {
            if (list.size() < StatsSummary.MAX_PER_LIST) {
                list.add(item);
            }
        }
        return list;
    }

    /**
     * Get the Top failed Jobs, ordered by most failed jobs
     *
     * @return
     */
    private List<BuildInfo> getTopFailedJobs() {
        // once all builds info are gathered and unique, sort them
        Map<Integer, BuildInfo> latestBuilds = new TreeMap<Integer, BuildInfo>();
        for (BuildInfo latestBuild : buildInfoStore.values()) {
            latestBuilds.put(latestBuild.getFailures(), latestBuild);
        }
        // use reverse order to have most recent on top
        latestBuilds = ((TreeMap) latestBuilds).descendingMap();
        // prepare a list with a MAX number of fault causes
        List<BuildInfo> list = new ArrayList<BuildInfo>();
        for (BuildInfo item : latestBuilds.values()) {
            if (list.size() < StatsSummary.MAX_PER_LIST) {
                list.add(item);
            }
        }
        return list;
    }
}
