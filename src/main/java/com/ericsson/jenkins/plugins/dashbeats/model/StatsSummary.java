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
package com.ericsson.jenkins.plugins.dashbeats.model;

import java.util.*;

/**
 * StatsSummary composing of all information to be display on the DashBeats UI.
 * There are 5 widgets on DashBeats UI:
 * <ul>
 * <li>Welcome</li>
 * <li>Common Fault Causes</li>
 * <li>Latest Builds</li>
 * <li>Latest Failed Builds</li>
 * <li>Top Failed Jobs</li>
 * </ul>
 *
 * Created by ekongto on 2014-09-11.
 */
public class StatsSummary {

    public static final int MAX_PER_LIST = 5;
    public static final String PRODUCT_NAME = "DashBeats";

    private Date startDate;
    private Date lastDate;
    private int buildCount = 0;

    private Welcome welcome;
    private List<FaultCauseInfo> commonFailureCauses;
    private List<BuildInfo> latestFailedBuilds;
    private List<BuildInfo> latestBuilds;
    private List<BuildInfo> topFailedJobs;

    /**
     * Constructor of stats summary
     * @param startDate
     * @param lastDate
     * @param buildCount
     */
    public StatsSummary(Date startDate, Date lastDate, int buildCount) {
        this.startDate = startDate;
        this.lastDate = lastDate;
        this.buildCount = buildCount;
    }

    /**
     * Get the start date of data being displayable
     * @return
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Set the start date of data being displayable
     * @param startDate
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Get the last date of data being displayable
     * @return
     */
    public Date getLastDate() {
        return lastDate;
    }

    /**
     * Set the last date of data being displayable
     * @param lastDate
     */
    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    /**
     * Get the build count
     * @return
     */
    public int getBuildCount() {
        return buildCount;
    }

    /**
     * set the build count
     * @param buildCount
     */
    public void setBuildCount(int buildCount) {
        this.buildCount = buildCount;
    }

    /**
     * Get the welcome object to be displayed on the welcome widget
     * @return
     */
    public Welcome getWelcome() {
        return welcome;
    }

    /**
     * Set the welcome object to be displayed on the respective widget
     * @param welcome
     */
    public void setWelcome(Welcome welcome) {
        this.welcome = welcome;
    }

    /**
     * Get the common fault cause list to be displayed on the respective widget
     * @return
     */
    public List<FaultCauseInfo> getCommonFaultCauses() {
        return commonFailureCauses;
    }

    /**
     * Set the common fault cause list to be displayed on the respective widget
     * @param commonFailureCauses
     */
    public void setCommonFailureCauses(List<FaultCauseInfo> commonFailureCauses) {
        this.commonFailureCauses = commonFailureCauses;
    }

    /**
     * Get the latest failed build list to be displayed on the respective widget
     * @return
     */
    public List<BuildInfo> getLatestFailedBuilds() {
        return latestFailedBuilds;
    }

    /**
     * Set the latest failed build list to be displayed on the respective widget
     * @param latestFailedBuilds
     */
    public void setLatestFailedBuilds(List<BuildInfo> latestFailedBuilds) {
        this.latestFailedBuilds = latestFailedBuilds;
    }

    /**
     * Get the latest build list to be displayed on the respective widget
     * @return
     */
    public List<BuildInfo> getLatestBuilds() {
        return latestBuilds;
    }

    /**
     * Set the latest build list to be displayed on the respective widget
     * @param latestBuilds
     */
    public void setLatestBuilds(List<BuildInfo> latestBuilds) {
        this.latestBuilds = latestBuilds;
    }

    /**
     * Get the top failed job list to be displayed on the respective widget
     * @return
     */
    public List<BuildInfo> getTopFailedJobs() {
        return topFailedJobs;
    }

    /**
     * Set the top failed job list to be displayed on the respective widget
     * @param topFailedJobs
     */
    public void setTopFailedJobs(List<BuildInfo> topFailedJobs) {
        this.topFailedJobs = topFailedJobs;
    }
}
