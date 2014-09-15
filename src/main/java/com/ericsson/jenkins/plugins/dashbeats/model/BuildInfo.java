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

import hudson.model.Result;

import java.util.Date;

/**
 * BuildInfo representing information about jobs to be displayed in DashBeats.
 *
 * Created by ekongto on 2014-09-11.
 */
public class BuildInfo {
    private Date date;
    private String job;
    private int buildNumber;
    private String result;
    private int successes;
    private int failures;
    private int unstables;
    private int aborts;

    /**
     * Constructor of build info
     *
     * @param date
     * @param job
     * @param buildNumber
     * @param result
     */
    public BuildInfo(Date date, String job, int buildNumber, String result) {
        this.date = date;
        this.job = job;
        this.buildNumber = buildNumber;
        this.result = result;
    }

    /**
     * Get date of build
     * @return
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set date of build
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Get job name
     * @return
     */
    public String getJob() {
        return job;
    }

    /**
     * Set job name
     * @return
     */
    public int getBuildNumber() {
        return buildNumber;
    }

    /**
     * Set the build number
     * @param buildNumber
     */
    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    /**
     * Get build result
     * @return
     */
    public String getResult() {
        return result;
    }

    /**
     * Set build result
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Get number of successes
     * @return
     */
    public int getSuccesses() {
        return successes;
    }

    /**
     * Set number of successes
     * @param successes
     */
    public void setSuccesses(int successes) {
        this.successes = successes;
    }

    /**
     * Get number of failures
     * @return
     */
    public int getFailures() {
        return failures;
    }

    /**
     * Set number of failures
     * @param failures
     */
    public void setFailures(int failures) {
        this.failures = failures;
    }

    /**
     * Get number of unstables
     * @return
     */
    public int getUnstables() {
        return unstables;
    }

    /**
     * Set number of unstables
     * @param unstables
     */
    public void setUnstables(int unstables) {
        this.unstables = unstables;
    }

    /**
     * Get number of aborts
     * @return
     */
    public int getAborts() {
        return aborts;
    }

    /**
     * Set number of aborts
     * @param aborts
     */
    public void setAborts(int aborts) {
        this.aborts = aborts;
    }

    /**
     * Get totol builds
     * @return
     */
    public int getTotal() {
        return successes + failures + unstables + aborts;
    }

    /**
     * Get rate of failures/total
     * @return
     */
    public double getRateOfFailure() {
        float total = getTotal() == 0f ? 1f : getTotal() ;
        float fails = getFailures();
        return Math.round(100 * fails / total);
    }

    /**
     * Increment the counter of the build info respectively according the build result
     *
     * @param result
     */
    public void incrementBuildResult(String result) {
        if (Result.SUCCESS.toString().equals(result)) {
            this.setSuccesses(this.getSuccesses() + 1);
        } else if (Result.FAILURE.toString().equals(result)) {
            this.setFailures(this.getFailures()+1);
        } else if (Result.UNSTABLE.toString().equals(result)) {
            this.setUnstables(this.getUnstables() + 1);
        } else if (Result.ABORTED.toString().equals(result)) {
            this.setAborts(this.getAborts() + 1);
        }
    }

    /**
     * Overrides to print the content of the build info
     * @return
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("")
                .append(job).append(": ")
                .append(" #").append(buildNumber)
                .append(" ").append(result).append(" --- ")
                .append(" total(").append(getTotal()).append(")")
                .append(" successes(").append(getSuccesses()).append(")")
                .append(" failures(").append(getFailures()).append(")")
                .append(" unstables(").append(getUnstables()).append(")")
                .append(" aborts(").append(getAborts()).append(")")
                .append(" failure rate(").append(getRateOfFailure()).append(")")
                .append("");
        return sb.toString();
    }

}
