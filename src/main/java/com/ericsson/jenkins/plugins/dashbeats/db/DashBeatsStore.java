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
import com.sonyericsson.jenkins.plugins.bfa.model.FailureCause;
import com.sonyericsson.jenkins.plugins.bfa.statistics.FailureCauseStatistics;
import com.sonyericsson.jenkins.plugins.bfa.statistics.Statistics;
import hudson.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class stores statistics, builds info and fault causes. Builds info and fault causes
 * are information compiled from the BFA statistics. Whenever a statistic object is stored,
 * the builds info and the fault causes are updated.
 * <p/>
 * Created by ekongto on 2014-09-11.
 */
public class DashBeatsStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashBeatsStore.class.getName());

    /* Build Info store using a HashMap to avoid duplicates */
    private Map<String, BuildInfo> buildInfoStore;
    /* Latest failed build store using a HashMap to avoid duplicates */
    private Map<String, BuildInfo> latestFailedBuildStore;
    /* Fault Cause Info store using a HashMap to avoid duplicates*/
    private Map<String, FaultCauseInfo> faultCauseInfoStore;

    private Date startDate;
    private Date lastDate;

    /**
     * Default constructor, instantiates statsStore, buildInfoStore and faultCauseInfoStore collections
     */
    public DashBeatsStore() {
        this.buildInfoStore = new HashMap<String, BuildInfo>();
        this.latestFailedBuildStore = new HashMap<String, BuildInfo>();
        this.faultCauseInfoStore = new HashMap<String, FaultCauseInfo>();
        this.startDate = new Date();
        this.lastDate = new Date();
    }

    /**
     * Get the size of the build info store, which contains builds info per job.
     *
     * @return
     */
    public int size() {
        return buildInfoStore.size();
    }

    /**
     * Clear all stores of builds and common fault causes
     */
    public void clear() {
        buildInfoStore.clear();
        latestFailedBuildStore.clear();
        faultCauseInfoStore.clear();
    }

    /**
     * Update the DashBeats store from a statistics object. At the same time, it update
     * the build info store and the fault cause info store., by compiling the statistics store.
     *
     * @param stats
     */
    public void update(final Statistics stats, final Collection<FailureCause> causes) {
        updateBuildInfoStore(stats);
        updateLatestFailedBuildStore(stats);
        updateFaultCauseInfoStore(stats, causes);
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
        StatsSummary summary = new StatsSummary(startDate, lastDate, buildInfoStore.size());
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
    private void updateBuildInfoStore(final Statistics stats) {
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
        LOGGER.info("Added a build info : {}", buildInfo);
    }

    /**
     * Update the latest failed build store by compiling data from StatsStore
     */
    private void updateLatestFailedBuildStore(final Statistics stats) {
        Date date = stats.getStartingTime();
        String job = stats.getProjectName();
        int buildNumber = stats.getBuildNumber();
        String result = stats.getResult();
        BuildInfo buildInfo = null;
        if (Result.FAILURE.toString().equals(stats.getResult())) {
            if (latestFailedBuildStore.containsKey(job)) {
                buildInfo = latestFailedBuildStore.get(job);
                buildInfo.incrementBuildResult(result);
                if (buildNumber > buildInfo.getBuildNumber()) {
                    buildInfo.setDate(date);
                    buildInfo.setResult(result);
                    buildInfo.setBuildNumber(buildNumber);
                }
            } else {
                buildInfo = new BuildInfo(date, job, buildNumber, result);
                buildInfo.incrementBuildResult(result);
                latestFailedBuildStore.put(job, buildInfo);
            }
        }
        LOGGER.info("Added a latest failed build : {}", buildInfo);
    }

    /**
     * Update the fault cause store by compiling data from statistics store
     */
    private void updateFaultCauseInfoStore(final Statistics stats, final Collection<FailureCause> causes) {
        // For every stat in the store, get the failure indications and store into
        // a map by ensuring the uniqueness. In case the same failure cause is already stored,
        // increment the number of failures and set the most recent date
        Date date = stats.getStartingTime();
        FaultCauseInfo commonFaultCause = null;
        if (Result.FAILURE.toString().equals(stats.getResult())) {
            LOGGER.debug("iterating failures cause statistics... size[{}]", stats.getFailureCauseStatisticsList().size());
            for (FailureCauseStatistics fcs : stats.getFailureCauseStatisticsList()) {
                String causeId = fcs.getId();
                FailureCause cause = findFailureCause(causeId, causes);
                // if already existed, then increment counter and update date if more recent
                // update the name and categories, they may have changed
                if (faultCauseInfoStore.containsKey(causeId)) {
                    commonFaultCause = faultCauseInfoStore.get(causeId);
                    commonFaultCause.setCauseName(cause.getName());
                    commonFaultCause.setCategories(cause.getCategories());
                    commonFaultCause.setFailures(commonFaultCause.getFailures() + 1);
                    if (date.after(commonFaultCause.getDate())) {
                        faultCauseInfoStore.put(causeId, commonFaultCause);
                    }
                } else {
                    commonFaultCause = new FaultCauseInfo(date, causeId);
                    commonFaultCause.setCauseName(cause.getName());
                    commonFaultCause.setCategories(cause.getCategories());
                    faultCauseInfoStore.put(causeId, commonFaultCause);
                }
                LOGGER.info("Added a common fault : {}", commonFaultCause);
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
        for (BuildInfo latestFailedBuild : latestFailedBuildStore.values()) {
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
        Map<Integer, List<BuildInfo>> latestBuilds = new TreeMap<Integer, List<BuildInfo>>();
        for (BuildInfo buildInfo : buildInfoStore.values()) {
            if (!latestBuilds.containsKey(buildInfo.getFailures())) {
                latestBuilds.put(buildInfo.getFailures(), new ArrayList<BuildInfo>());
            }
            latestBuilds.get(buildInfo.getFailures()).add(buildInfo);
        }
        // use reverse order to have most recent on top
        latestBuilds = ((TreeMap) latestBuilds).descendingMap();
        // prepare a list with a MAX number of fault causes
        List<BuildInfo> list = new ArrayList<BuildInfo>();
        for (List<BuildInfo> item : latestBuilds.values()) {
            for (BuildInfo buildInfo: item) {
                if (list.size() < StatsSummary.MAX_PER_LIST) {
                    list.add(buildInfo);
                } else {
                    break;
                }
            }
        }
        return list;
    }

    /**
     * Find the Failure Cause by Id from BFA Failure Causes
     *
     * @param causeId
     * @param causes
     * @return failure cause
     */
    private FailureCause findFailureCause(String causeId, Collection<FailureCause> causes) {
        for (FailureCause cause : causes) {
            if (cause.getId().equals(causeId)) {
                return cause;
            }
        }
        return null;
    }
}
