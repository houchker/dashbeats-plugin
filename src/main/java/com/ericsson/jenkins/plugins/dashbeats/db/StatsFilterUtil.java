package com.ericsson.jenkins.plugins.dashbeats.db;

import com.sonyericsson.jenkins.plugins.bfa.graphs.GraphFilterBuilder;
import com.sonyericsson.jenkins.plugins.bfa.statistics.Statistics;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by ekongto on 2014-09-18.
 */
public class StatsFilterUtil {

    // hide constructor
    private StatsFilterUtil() {

    }

    /**
     * Try to match 2 texts. Matcher is used to make a pattern to match the text.
     *
     * @param matcher
     * @param text
     * @return
     */
    public static boolean isMatched(String matcher, String text) {
        if (matcher == null) {
            return true;
        }
        Pattern pattern;
        pattern = Pattern.compile(".*" + matcher + ".*");
        return pattern.matcher(text).find();
    }

    /**
     * Check if the stat is to be excluded regarding the filter
     *
     * @param stat
     * @param filter
     * @return
     */
    public static boolean isExcluded(Statistics stat, GraphFilterBuilder filter) {
        if (filter.getExcludeResult() != null && isMatched(filter.getExcludeResult(), stat.getResult())) {
            return true;
        }
        return false;
    }

    /**
     * Check if the stats is to be included regarding the filter
     *
     * @param stat
     * @param filter
     * @return
     */
    public static boolean isIncluded(Statistics stat, GraphFilterBuilder filter) {
        // exclude matching result
        if (isExcluded(stat, filter)) {
            return false;
        }
        // matching criteria
        if (!isMatched(filter.getProjectName(), stat.getProjectName())) {
            return false;
        }
        // matching result
        if (!isMatched(filter.getResult(), stat.getResult())) {
            return false;
        }
        //matching master name
        if (!isMatched(filter.getMasterName(), stat.getMaster())) {
            return false;
        }
        // matching slave name
        if (!isMatched(filter.getSlaveName(), stat.getSlaveHostName())) {
            return false;
        }
        // build number is within the filter
        if (filter.getBuildNumbers() != null && !filter.getBuildNumbers().contains(stat.getBuildNumber())) {
            return false;
        }
        // matching date range
        if (filter.getSince() != null && !filter.getSince().before(stat.getStartingTime())) {
            return false;
        }
        return true;
    }
}
