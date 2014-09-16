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

import com.sonyericsson.jenkins.plugins.bfa.graphs.GraphFilterBuilder;
import com.sonyericsson.jenkins.plugins.bfa.statistics.Statistics;
import hudson.model.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ekongto on 2014-09-16.
 */
public class DashBeatsKnowledgeBaseTest {

    private String url;
    private String authToken;

    private DashBeatsKnowledgeBase kb;
    private StatisticsMockFactory factory;

    @Rule
    public JenkinsRule rule = new JenkinsRule();

    @Before
    public void setUp() {
        this.url = "http://localhost:3030";
        this.authToken = "YOUR_AUTH_TOKEN";
        kb = new DashBeatsKnowledgeBase(url, authToken);
        factory = new StatisticsMockFactory();
    }

    /**
     * GIVEN an empty DashBeats store
     * WHEN updating the store using statistics object
     * THEN the store is updated successfully
     * and the size is increased to 1
     */
    @Test
    public void shouldStartSuccessfully() throws Exception {
        Statistics stat = factory.createStatistics(new Date(), "jobTest1", 1, Result.SUCCESS.toString());
        kb.start();
        Assert.assertEquals(true, kb.isStatisticsEnabled());
        Assert.assertEquals(true, kb.isSuccessfulLoggingEnabled());
        Assert.assertEquals(url, kb.getUrl());
        Assert.assertEquals(authToken, kb.getAuthToken());
    }

    /**
     * GIVEN an empty DashBeats store
     * WHEN updating the store using statistics object
     * THEN the store is updated successfully
     * and the size is increased to 1
     */
    @Test
    public void shouldSaveStatisticsSuccessfully() throws Exception {
        Statistics stat1 = factory.createStatistics(new Date(), "jobTest1", 1, Result.SUCCESS.toString());
        kb.saveStatistics(stat1);
        GraphFilterBuilder filter = new GraphFilterBuilder();
        filter.setProjectName("jobTest1");
        Assert.assertEquals(1, kb.getStatistics(filter, 1).size());
    }

    /**
     * GIVEN a DashBeats store with 1 statistics objects
     * and a filter
     * WHEN get statistics using the date in the filter
     * THEN the return list includes statistics object matching the filer
     */
    @Test
    public void shouldGetMatchingDateStatistics() throws Exception {
        Statistics stat1 = factory.createStatistics(new Date(), "jobTest1", 1, Result.SUCCESS.toString());
        List<Integer> buildList = new ArrayList<Integer>();
        buildList.add(1);
        kb.saveStatistics(stat1);
        GraphFilterBuilder filter = new GraphFilterBuilder();
        filter.setBuildNumbers(buildList);
        // not valid date
        filter.setSince(new Date());
        filter.setProjectName("jobTest1");
        filter.setResult(Result.SUCCESS.toString());
        Assert.assertEquals(0, kb.getStatistics(filter, 1).size());
    }

    /**
     * GIVEN a DashBeats store with 1 statistics objects
     * and a filter
     * WHEN get statistics using the jobname in the filter
     * THEN the return list includes statistics object matching the filer
     */
    @Test
    public void shouldGetMatchingJobNameStatistics() throws Exception {
        Statistics stat1 = factory.createStatistics(new Date(), "jobTest1", 1, Result.SUCCESS.toString());
        kb.saveStatistics(stat1);
        GraphFilterBuilder filter = new GraphFilterBuilder();
        // not valid job name
        filter.setSince(new Date(stat1.getStartingTime().getTime()-1000));
        filter.setProjectName("jobTest2");
        filter.setResult(Result.SUCCESS.toString());
        Assert.assertEquals(0, kb.getStatistics(filter, 1).size());
    }

    /**
     * GIVEN a DashBeats store with 1 statistics objects
     * and a filter
     * WHEN get statistics using the result in the filter
     * THEN the return list includes statistics object matching the filer
     */
    @Test
    public void shouldGetMatchingResultStatistics() throws Exception {
        Statistics stat1 = factory.createStatistics(new Date(), "jobTest1", 1, Result.SUCCESS.toString());
        kb.saveStatistics(stat1);
        GraphFilterBuilder filter = new GraphFilterBuilder();
        // not valid result
        filter.setProjectName("jobTest1");
        filter.setResult(Result.FAILURE.toString());
        filter.setSince(new Date(stat1.getStartingTime().getTime() - 1000));
        Assert.assertEquals(0, kb.getStatistics(filter, 1).size());
    }

    /**
     * GIVEN a DashBeats store with 1 statistics objects
     * and a filter
     * WHEN get statistics using the result in the filter
     * THEN the return list includes statistics object matching the filer
     */
    @Test
    public void shouldGetMatchingBuildNumberStatistics() throws Exception {
        Statistics stat1 = factory.createStatistics(new Date(), "jobTest1", 1, Result.SUCCESS.toString());
        List<Integer> buildList = new ArrayList<Integer>();
        kb.saveStatistics(stat1);
        GraphFilterBuilder filter = new GraphFilterBuilder();
        // not build number
        buildList.add(2);
        filter.setBuildNumbers(buildList);
        filter.setSince(new Date(stat1.getStartingTime().getTime()-1000));
        filter.setProjectName("jobTest2");
        filter.setResult(Result.SUCCESS.toString());
        Assert.assertEquals(0, kb.getStatistics(filter, 1).size());
    }

    /**
     * GIVEN a DashBeats store with 1 statistics objects
     * and a filter
     * WHEN get statistics using the all matching filter
     * THEN the return list includes statistics object matching the filer
     */
    @Test
    public void shouldGetAllMatchingStatistics() throws Exception {
        Statistics stat1 = factory.createStatistics(new Date(), "jobTest1", 1, Result.SUCCESS.toString());
        List<Integer> buildList = new ArrayList<Integer>();
        kb.saveStatistics(stat1);
        GraphFilterBuilder filter = new GraphFilterBuilder();
        // all valid fields
        buildList.add(1);
        filter.setBuildNumbers(buildList);
        filter.setProjectName("jobTest1");
        filter.setResult(Result.SUCCESS.toString());
        filter.setSince(new Date(stat1.getStartingTime().getTime() - 1000));
        Assert.assertEquals(1, kb.getStatistics(filter, 1).size());
    }
}
