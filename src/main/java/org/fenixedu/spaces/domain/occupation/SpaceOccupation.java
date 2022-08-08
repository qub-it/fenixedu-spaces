package org.fenixedu.spaces.domain.occupation;

import java.util.List;

import org.joda.time.Interval;

public interface SpaceOccupation {

    public List<Interval> getIntervals();

    public String getSubject();

    default public String getDescription() {
        return null;
    }

}
