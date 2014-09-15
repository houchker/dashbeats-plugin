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


import com.ericsson.jenkins.plugins.dashbeats.model.*;
import com.sonyericsson.jenkins.plugins.bfa.statistics.Statistics;
import hudson.model.Result;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * Created by ekongto on 2014-09-08.
 */
public class DashBeatsStoreTest {

    private DashBeatsStore store;
    private StatisticsMockFactory factory;

    @Before
    public void setUp() {
        store = new DashBeatsStore();
        factory = new StatisticsMockFactory();
    }

    /**
     * GIVEN an empty stats store
     * WHEN storing the stat into the statistics store
     * THEN the stat is stored it successfully
     * and the size is increased to 1
     */
    @Test
    public void shouldStoreStatsSuccessfully() throws Exception {
        Assert.assertEquals(0, store.size());
        Statistics stat = factory.createStatistics(new Date(), "jobTest1", 1, Result.SUCCESS.toString());
        store.store(stat, factory.createFailureCauses());
        Assert.assertEquals(1, store.size());
    }

    /**
     * GIVEN a stats store with 1 stat
     * WHEN clearing the store
     * THEN the stat is cleared it successfully
     * and the size is 0
     */
    @Test
    public void shouldClearStatsStoreSuccessfully() throws Exception {
        Assert.assertEquals(0, store.size());
        Statistics stat = factory.createStatistics(new Date(), "jobTest1", 1, Result.SUCCESS.toString());
        store.store(stat, factory.createFailureCauses());
        Assert.assertEquals(1, store.size());
        store.clear();
        Assert.assertEquals(0, store.size());
    }

    /**
     * GIVEN 24 stats objects stored in the statistics store
     * WHEN creating a StatsSummary
     * THEN the data is compiled successfully from the statistics store
     * and the stats summary is updated including the welcome data
     * and the common failure causes data
     * and the latest failed builds data
     * and the latest builds data
     * and the top failed builds data
     */
    @Test
    public void shouldCreateSummarySuccessfully() throws Exception {

        //GIVEN
        JSONObject jsonObject = new JSONObject();

        List<Statistics> statsList = factory.createStatisticsBatch();
        for (Statistics stat: statsList) {
            store.store(stat, factory.createFailureCauses());
        }

        Assert.assertEquals(24, store.size());

        //WHEN
        StatsSummary summary = store.createSummary();
        Assert.assertNotNull(summary);

        //THEN
        Welcome welcome = summary.getWelcome();
        List<FaultCauseInfo> cfc = summary.getCommonFaultCauses();
        List<BuildInfo> lfb = summary.getLatestFailedBuilds();
        List<BuildInfo> lb = summary.getLatestBuilds();
        List<BuildInfo> tfb = summary.getTopFailedJobs();

        Assert.assertNotNull(welcome);
        Assert.assertNotNull(cfc);
        Assert.assertNotNull(lfb);
        Assert.assertNotNull(lb);
        Assert.assertNotNull(tfb);

        Assert.assertEquals(1, cfc.size());
        Assert.assertEquals(5, lfb.size());
        Assert.assertEquals(5, lb.size());
        Assert.assertEquals(5, tfb.size());

        // Assert Common Fault Causes
        Assert.assertEquals("causeId-1", cfc.get(0).getCauseId());
        Assert.assertEquals("cause1", cfc.get(0).getCauseName());
        Assert.assertEquals(6, cfc.get(0).getFailures());

        // Assert latest failed builds
        Assert.assertEquals("jobTestF", lfb.get(0).getJob());
        Assert.assertEquals(Result.FAILURE.toString(), lfb.get(0).getResult());
        Assert.assertEquals("jobTestE", lfb.get(1).getJob());
        Assert.assertEquals(Result.FAILURE.toString(), lfb.get(1).getResult());
        Assert.assertEquals("jobTestD", lfb.get(2).getJob());
        Assert.assertEquals(Result.FAILURE.toString(), lfb.get(2).getResult());
        Assert.assertEquals("jobTestC", lfb.get(3).getJob());
        Assert.assertEquals(Result.FAILURE.toString(), lfb.get(3).getResult());
        Assert.assertEquals("jobTestB", lfb.get(4).getJob());
        Assert.assertEquals(Result.FAILURE.toString(), lfb.get(4).getResult());

        // Assert latest builds
        Assert.assertEquals("jobTestF", lb.get(0).getJob());
        Assert.assertEquals(Result.UNSTABLE.toString(), lb.get(0).getResult());
        Assert.assertEquals("jobTestE", lb.get(1).getJob());
        Assert.assertEquals(Result.UNSTABLE.toString(), lb.get(1).getResult());
        Assert.assertEquals("jobTestD", lb.get(2).getJob());
        Assert.assertEquals(Result.UNSTABLE.toString(), lb.get(2).getResult());
        Assert.assertEquals("jobTestC", lb.get(3).getJob());
        Assert.assertEquals(Result.UNSTABLE.toString(), lb.get(3).getResult());
        Assert.assertEquals("jobTestB", lb.get(4).getJob());
        Assert.assertEquals(Result.UNSTABLE.toString(), lb.get(4).getResult());

        // Assert top failed jobs, not ordered by date
        Assert.assertEquals(Result.UNSTABLE.toString(), tfb.get(0).getResult());
        Assert.assertEquals(Result.UNSTABLE.toString(), tfb.get(1).getResult());
        Assert.assertEquals(Result.UNSTABLE.toString(), tfb.get(2).getResult());
        Assert.assertEquals(Result.UNSTABLE.toString(), tfb.get(3).getResult());
        Assert.assertEquals(Result.UNSTABLE.toString(), tfb.get(4).getResult());

    }

}
