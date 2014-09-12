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

import com.ericsson.jenkins.plugins.dashbeats.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class create a mock summary instance
 *
 * Created by ekongto on 2014-09-12.
 */
public class SummaryMockFactory {

    /**
     * Create a mocked summary for testing purpose
     * @param startDate
     * @param lastDate
     * @return
     */
    public static StatsSummary createSummary(Date startDate, Date lastDate) {
        StatsSummary summary = new StatsSummary(startDate, lastDate, 1);
        summary.setWelcome(createWelcome(startDate, lastDate));
        summary.setCommonFailureCauses(createCommonFaultCauses());
        summary.setLatestFailedBuilds(createLatestFailedBuilds());
        summary.setLatestBuilds(createLatestBuilds());
        summary.setTopFailedJobs(createTopFailedJobs());
        return summary;
    }

    /**
     * Create a mock welcome
     * @param startDate
     * @param lastDate
     * @return
     */
    private static Welcome createWelcome(Date startDate, Date lastDate) {
        return new Welcome("DashBeats", startDate, lastDate);
    }

    /**
     * Create a list of mocked common fault causes for testing purpose
     * @return
     */
    private static List<FaultCauseInfo> createCommonFaultCauses() {
        List<FaultCauseInfo> list = new ArrayList<FaultCauseInfo>();
        FaultCauseInfo faultCause1 = createFaultCauseInfo("error1");
        FaultCauseInfo faultCause2 = createFaultCauseInfo("error2");
        FaultCauseInfo faultCause3 = createFaultCauseInfo("error3");
        FaultCauseInfo faultCause4 = createFaultCauseInfo("error4");
        FaultCauseInfo faultCause5 = createFaultCauseInfo("error5");
        FaultCauseInfo faultCause6 = createFaultCauseInfo("error6");
        list.add(faultCause1);
        list.add(faultCause2);
        list.add(faultCause3);
        list.add(faultCause4);
        list.add(faultCause5);
        list.add(faultCause6);
        return list;
    }

    /** Create a list of mocked latest builds for testing purpose
     *
     * @return
     */
    private static List<BuildInfo> createLatestBuilds() {
        List<BuildInfo> list = new ArrayList<BuildInfo>();
        BuildInfo buildInfo1 = createBuildInfo("testJob1", 1, "FAILED");
        BuildInfo buildInfo2 = createBuildInfo("testJob1", 2, "SUCCESS");
        BuildInfo buildInfo3 = createBuildInfo("testJob2", 1, "FAILED");
        BuildInfo buildInfo4 = createBuildInfo("testJob2", 2, "UNSTABLE");
        BuildInfo buildInfo5 = createBuildInfo("testJob3", 1, "ABORTED");
        BuildInfo buildInfo6 = createBuildInfo("testJob4", 2, "FAILED");
        list.add(buildInfo1);
        list.add(buildInfo2);
        list.add(buildInfo3);
        list.add(buildInfo4);
        list.add(buildInfo5);
        list.add(buildInfo6);
        return list;
    }

    /**
     * Create a list of mocked latest failed builds for testing purpose
     * @return
     */
    private static List<BuildInfo> createLatestFailedBuilds() {
        List<BuildInfo> list = new ArrayList<BuildInfo>();
        BuildInfo buildInfo1 = createBuildInfo("testJob1", 1, "FAILED");
        BuildInfo buildInfo2 = createBuildInfo("testJob1", 2, "FAILED");
        BuildInfo buildInfo3 = createBuildInfo("testJob2", 1, "FAILED");
        BuildInfo buildInfo4 = createBuildInfo("testJob2", 2, "FAILED");
        BuildInfo buildInfo5 = createBuildInfo("testJob3", 1, "FAILED");
        BuildInfo buildInfo6 = createBuildInfo("testJob3", 2, "FAILED");
        list.add(buildInfo1);
        list.add(buildInfo2);
        list.add(buildInfo3);
        list.add(buildInfo4);
        list.add(buildInfo5);
        list.add(buildInfo6);
        return list;
    }

    /**
     * Create a list of mocked top failed jobs for testing purpose
     * @return
     */
    private static List<BuildInfo> createTopFailedJobs() {
        List<BuildInfo> list = new ArrayList<BuildInfo>();
        BuildInfo buildInfo1 = createBuildInfo("testJob1", 1, "FAILED");
        BuildInfo buildInfo2 = createBuildInfo("testJob1", 2, "FAILED");
        BuildInfo buildInfo3 = createBuildInfo("testJob2", 1, "FAILED");
        BuildInfo buildInfo4 = createBuildInfo("testJob2", 2, "FAILED");
        BuildInfo buildInfo5 = createBuildInfo("testJob3", 1, "FAILED");
        BuildInfo buildInfo6 = createBuildInfo("testJob3", 2, "FAILED");
        list.add(buildInfo1);
        list.add(buildInfo2);
        list.add(buildInfo3);
        list.add(buildInfo4);
        list.add(buildInfo5);
        list.add(buildInfo6);
        return list;
    }

    /**
     * Create a mocked fault cause info
     * @param faultCause
     * @return
     */
    private static FaultCauseInfo createFaultCauseInfo(String faultCause) {
        return new FaultCauseInfo(new Date(), faultCause);
    }

    /**
     * Create a mocked build info
     * @param job
     * @param number
     * @param result
     * @return
     */
    private static BuildInfo createBuildInfo(String job, int number, String result) {
        BuildInfo buildInfo = new BuildInfo(new Date(), job, number, result);
        buildInfo.setAborts(1);
        buildInfo.setFailures(3);
        buildInfo.setSuccesses(2);
        buildInfo.setUnstables(0);
        buildInfo.setResult(result);
        return buildInfo;
    }
}
