package org.fenixedu.spaces.domain;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

@RunWith(FenixFrameworkRunner.class)
public class SpaceTest {

    private Information createInformation(DateTime validFrom, DateTime validUntil) {
        Information info = new Information();

        info.setValidFrom(validFrom);
        info.setValidUntil(validUntil);

        return info;
    }

    @Test
    public void testActiveSpace_shouldReturnTrue() {
        Space space = new Space(createInformation(DateTime.now().minusYears(1), DateTime.now().plusYears(1)));

        Assert.assertTrue(space.isActive());
    }

    @Test
    public void testDeletedSpace_shouldReturnFalse() {
        Space space = new Space(createInformation(DateTime.now().minusYears(1), DateTime.now().plusYears(1)));

        space.delete();

        Assert.assertFalse(space.isActive());
    }

    @Test
    public void testExpiredInformation_shouldReturnFalse() {
        Space space = new Space(createInformation(DateTime.now().minusYears(2), DateTime.now().minusYears(1)));

        Assert.assertFalse(space.isActive());
    }

    @Test
    public void testFutureInformation_shouldReturnFalse() {
        Space space = new Space(createInformation(DateTime.now().plusYears(1), DateTime.now().plusYears(2)));

        Assert.assertFalse(space.isActive());
    }
}
