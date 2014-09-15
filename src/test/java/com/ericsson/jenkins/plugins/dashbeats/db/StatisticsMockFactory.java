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

import com.sonyericsson.jenkins.plugins.bfa.model.FailureCause;
import com.sonyericsson.jenkins.plugins.bfa.model.FoundFailureCause;
import com.sonyericsson.jenkins.plugins.bfa.model.indication.BuildLogIndication;
import com.sonyericsson.jenkins.plugins.bfa.model.indication.FoundIndication;
import com.sonyericsson.jenkins.plugins.bfa.model.indication.Indication;
import com.sonyericsson.jenkins.plugins.bfa.statistics.FailureCauseStatistics;
import com.sonyericsson.jenkins.plugins.bfa.statistics.Statistics;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Result;
import org.mockito.Mockito;

import java.util.*;

/**
 * This class create mock statistics.
 *
 * Created by ekongto on 2014-09-10.
 */
public class StatisticsMockFactory {

    /**
     * Create mock of statistic object
     *
     * @param date
     * @param job
     * @param buildNumber
     * @param result
     * @return
     * @throws Exception
     */
    public Statistics createStatistics(Date date, String job, int buildNumber, String result) throws Exception {

        List<FoundFailureCause> causes = createFoundCauses();

        long duration = 100000;
        List<String> triggerCauses = new LinkedList<String>();
        for (Object o : createFoundCauses()) {
            triggerCauses.add("userIdCause");
        }
        String nodeName = "nodeName";
        int timeZoneOffset = TimeZone.getDefault().getRawOffset();
        String master = "";

        List<FailureCauseStatistics> failureCauseStatistics = new LinkedList<FailureCauseStatistics>();
        List<String> causeIds = new LinkedList<String>();
        for (FoundFailureCause cause : causes) {
            FailureCauseStatistics stats = new FailureCauseStatistics(cause.getId(), cause.getIndications());
            failureCauseStatistics.add(stats);
            causeIds.add(cause.getId());
        }

        master = "MasterName";
        Cause.UpstreamCause uc = Mockito.mock(Cause.UpstreamCause.class);
        Statistics.UpstreamCause suc = new Statistics.UpstreamCause(uc);
        Statistics stat = new Statistics(job, buildNumber, date, duration, triggerCauses, nodeName,
                master, timeZoneOffset, result, suc, failureCauseStatistics);

        return stat;
    }

    public List<Statistics> createStatisticsBatch() throws Exception {
        List<Statistics> list = new ArrayList<Statistics>();
        StatisticsMockFactory factory = new StatisticsMockFactory();
        Statistics statA1 = factory.createStatistics(new Date(), "jobTestA", 1, Result.SUCCESS.toString());
        Statistics statA2 = factory.createStatistics(new Date(), "jobTestA", 2, Result.FAILURE.toString());
        Statistics statA3 = factory.createStatistics(new Date(), "jobTestA", 3, Result.ABORTED.toString());
        Statistics statA4 = factory.createStatistics(new Date(), "jobTestA", 4, Result.UNSTABLE.toString());
        Statistics statB1 = factory.createStatistics(new Date(), "jobTestB", 1, Result.SUCCESS.toString());
        Statistics statB2 = factory.createStatistics(new Date(), "jobTestB", 2, Result.FAILURE.toString());
        Statistics statB3 = factory.createStatistics(new Date(), "jobTestB", 3, Result.ABORTED.toString());
        Statistics statB4 = factory.createStatistics(new Date(), "jobTestB", 4, Result.UNSTABLE.toString());
        Statistics statC1 = factory.createStatistics(new Date(), "jobTestC", 1, Result.SUCCESS.toString());
        Statistics statC2 = factory.createStatistics(new Date(), "jobTestC", 2, Result.FAILURE.toString());
        Statistics statC3 = factory.createStatistics(new Date(), "jobTestC", 3, Result.ABORTED.toString());
        Statistics statC4 = factory.createStatistics(new Date(), "jobTestC", 4, Result.UNSTABLE.toString());
        Statistics statD1 = factory.createStatistics(new Date(), "jobTestD", 1, Result.SUCCESS.toString());
        Statistics statD2 = factory.createStatistics(new Date(), "jobTestD", 2, Result.FAILURE.toString());
        Statistics statD3 = factory.createStatistics(new Date(), "jobTestD", 3, Result.ABORTED.toString());
        Statistics statD4 = factory.createStatistics(new Date(), "jobTestD", 4, Result.UNSTABLE.toString());
        Statistics statE1 = factory.createStatistics(new Date(), "jobTestE", 1, Result.SUCCESS.toString());
        Statistics statE2 = factory.createStatistics(new Date(), "jobTestE", 2, Result.FAILURE.toString());
        Statistics statE3 = factory.createStatistics(new Date(), "jobTestE", 3, Result.ABORTED.toString());
        Statistics statE4 = factory.createStatistics(new Date(), "jobTestE", 4, Result.UNSTABLE.toString());
        Statistics statF1 = factory.createStatistics(new Date(), "jobTestF", 1, Result.SUCCESS.toString());
        Statistics statF2 = factory.createStatistics(new Date(), "jobTestF", 2, Result.FAILURE.toString());
        Statistics statF3 = factory.createStatistics(new Date(), "jobTestF", 3, Result.ABORTED.toString());
        Statistics statF4 = factory.createStatistics(new Date(), "jobTestF", 4, Result.UNSTABLE.toString());

        list.add(statA1);
        list.add(statA2);
        list.add(statA3);
        list.add(statA4);
        list.add(statB1);
        list.add(statB2);
        list.add(statB3);
        list.add(statB4);
        list.add(statC1);
        list.add(statC2);
        list.add(statC3);
        list.add(statC4);
        list.add(statD1);
        list.add(statD2);
        list.add(statD3);
        list.add(statD4);
        list.add(statE1);
        list.add(statE2);
        list.add(statE3);
        list.add(statE4);
        list.add(statF1);
        list.add(statF2);
        list.add(statF3);
        list.add(statF4);

        return list;
    }

    public List<FailureCause> createFailureCauses() {
        List<String> categories = new ArrayList<String>();
        categories.add("category1");
        categories.add("category2");
        categories.add("category3");
        List<FailureCause> list = new ArrayList<FailureCause>();
        FailureCause cause1 = new FailureCause("cause1", "cause1 description");
        cause1.setId("causeId-1");
        cause1.setCategories(categories);
        FailureCause cause2 = new FailureCause("cause2", "cause2 description");
        cause2.setId("causeId-2");
        cause2.setCategories(categories);
        FailureCause cause3 = new FailureCause("cause3", "cause3 description");
        cause3.setId("causeId-3");
        cause3.setCategories(categories);
        list.add(cause1);
        list.add(cause2);
        list.add(cause3);
        return list;
    }

    /**
     * Create mocks of foundCauses
     * @return
     */
    private List<FoundFailureCause> createFoundCauses() {
        //create a list of Failure Cause Category
        List<String> FailureCauseCategoryList = new ArrayList<String>();
        FailureCauseCategoryList.add("Failure cause category 1");
        FailureCauseCategoryList.add("Failure cause category 2");
        FailureCauseCategoryList.add("Failure cause category 3");

        //create 2 failure cause indications
        Indication indication1 = new BuildLogIndication(".*Indication1.*");
        Indication indication2 = new BuildLogIndication(".*Indication2.*");
        Indication indication3 = new BuildLogIndication(".*Indication3.*");

        //create a failure cause associated to categories and indications above
        FailureCause failureCause =
                new FailureCause("Failure cause name", "Failure cause description", "comment");
        failureCause.setId("causeId-1");
        failureCause.setCategories(FailureCauseCategoryList);
        failureCause.addIndication(indication1);
        failureCause.addIndication(indication2);
        failureCause.addIndication(indication3);

        AbstractBuild build = Mockito.mock(AbstractBuild.class);
        FoundIndication foundIndication1 = new FoundIndication(build, "pattern1", "matching file", "matching string");
        FoundIndication foundIndication2 = new FoundIndication(build, "pattern2", "matching file", "matching string 2");

        //create a found failure cause wrapping the failure cause above
        FoundFailureCause foundFailureCause = new FoundFailureCause(failureCause);
        foundFailureCause.addIndication(foundIndication1);
        foundFailureCause.addIndication(foundIndication2);

        //create a list of failure causes having the failure cause above
        List<FoundFailureCause> foundFailureCauses = new ArrayList<FoundFailureCause>();
        foundFailureCauses.add(foundFailureCause);

        return foundFailureCauses;
    }

}
