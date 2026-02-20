package org.fenixedu.spaces.domain.occupation;

import org.fenixedu.spaces.domain.occupation.config.ExplicitConfig;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(FenixFrameworkRunner.class)
public class OccupationTest {

    @Test
    public void testExtractYears() {
        List<String> years0 = Occupation.extractYears(List.of()).toList();
        Assert.assertTrue(years0.isEmpty());

        DateTime start1 = new DateTime(2023, 1, 1, 10, 0);
        DateTime end1 = new DateTime(2023, 1, 1, 11, 0);
        Interval interval1 = new Interval(start1, end1);

        List<String> years1 = Occupation.extractYears(List.of(interval1)).toList();
        Assert.assertTrue(years1.contains("2023"));
        Assert.assertFalse(years1.contains("2024"));
        Assert.assertEquals(1, years1.size());

        DateTime start2 = new DateTime(2025, 1, 1, 10, 0);
        DateTime end2 = new DateTime(2027, 1, 1, 11, 0);
        Interval interval2 = new Interval(start2, end2);

        List<String> years2 = Occupation.extractYears(List.of(interval1, interval2)).toList();
        Assert.assertTrue(years2.contains("2023"));
        Assert.assertTrue(years2.contains("2024"));
        Assert.assertTrue(years2.contains("2025"));
        Assert.assertTrue(years2.contains("2026"));
        Assert.assertTrue(years2.contains("2027"));
        Assert.assertFalse(years2.contains("2028"));
        Assert.assertEquals(5, years2.size());
    }

    @Test
    public void testUpdateYearsIndex() {
        DateTime start = new DateTime(2023, 1, 1, 10, 0);
        DateTime end = new DateTime(2024, 1, 1, 10, 0);
        Interval interval = new Interval(start, end);
        ExplicitConfig config = new ExplicitConfig(null, Collections.singletonList(interval));

        Occupation occupation = new Occupation(null, "Subject", "Description", config);
        // Occupation constructor calls setConfig, which calls updateYearsIndex
        Assert.assertEquals("2023 2024", occupation.getYearsIndex());

        DateTime start2 = new DateTime(2027, 1, 1, 10, 0);
        DateTime end2 = new DateTime(2027, 1, 1, 11, 0);
        Interval interval2 = new Interval(start2, end2);
        ExplicitConfig config2 = new ExplicitConfig(null, Arrays.asList(interval, interval2));

        occupation.setConfig(config2);
        Assert.assertEquals("2023 2024 2025 2026 2027", occupation.getYearsIndex());
    }

    @Test
    public void testMatchesYearsIndex() {
        DateTime start = new DateTime(2023, 1, 1, 10, 0);
        DateTime end = new DateTime(2023, 1, 1, 11, 0);
        Interval interval = new Interval(start, end);
        ExplicitConfig config = new ExplicitConfig(null, List.of(interval));

        Occupation occupation = new Occupation(null, "Subject", "Description", config);

        // Match
        Interval matchInterval = new Interval(new DateTime(2023, 5, 1, 10, 0), new DateTime(2023, 5, 1, 11, 0));
        Assert.assertTrue(occupation.matchesYearsIndex(List.of(matchInterval)));

        // No match
        Interval noMatchInterval = new Interval(new DateTime(2024, 1, 1, 10, 0), new DateTime(2024, 1, 1, 11, 0));
        Assert.assertFalse(occupation.matchesYearsIndex(List.of(noMatchInterval)));

        // Empty years index should return true (as per implementation)
        occupation.setYearsIndex(null);
        Assert.assertTrue(occupation.matchesYearsIndex(List.of(noMatchInterval)));
    }

    @Test
    public void testOverlaps() {
        DateTime start = new DateTime(2023, 1, 1, 10, 0);
        DateTime end = new DateTime(2023, 1, 1, 12, 0);
        Interval occupationInterval = new Interval(start, end);
        ExplicitConfig config = new ExplicitConfig(null, Collections.singletonList(occupationInterval));

        Occupation occupation = new Occupation(null, "Subject", "Description", config);

        // Overlaps in time and year
        Interval overlappingInterval = new Interval(new DateTime(2023, 1, 1, 11, 0), new DateTime(2023, 1, 1, 13, 0));
        Assert.assertTrue(occupation.overlaps(overlappingInterval));

        // Overlaps in year but not in time
        Interval nonOverlappingTimeInterval = new Interval(new DateTime(2023, 1, 1, 13, 0), new DateTime(2023, 1, 1, 14, 0));
        Assert.assertFalse(occupation.overlaps(nonOverlappingTimeInterval));

        // Overlaps in time (hypothetically) but not in year
        // matchesYearsIndex will return false
        Interval nonOverlappingYearInterval = new Interval(new DateTime(2024, 1, 1, 11, 0), new DateTime(2024, 1, 1, 13, 0));
        Assert.assertFalse(occupation.overlaps(nonOverlappingYearInterval));

        // test with empty years index, should only check time overlap
        occupation.setYearsIndex(null);
        Assert.assertTrue(occupation.overlaps(overlappingInterval));
        Assert.assertFalse(occupation.overlaps(nonOverlappingTimeInterval));
        Assert.assertFalse(occupation.overlaps(nonOverlappingYearInterval));
    }
}
