package com.psocha.goclock;

import com.psocha.goclock.time.ByoyomiPlayerTimeStatus;
import com.psocha.goclock.time.CanadianPlayerTimeStatus;
import com.psocha.goclock.time.SuddenDeathPlayerTimeStatus;

import org.junit.Assert;
import org.junit.Test;

public class PlayerTimeStatusTest {

    @Test
    public void testSuddenDeath() {
        SuddenDeathPlayerTimeStatus clock = new SuddenDeathPlayerTimeStatus(80);
        Assert.assertEquals(80, clock.getOriginalTime());
        Assert.assertEquals(80, clock.getTime());
        Assert.assertEquals("1:20", clock.timeDescription());
        Assert.assertEquals("01:20", clock.clockTime());

        for (int i = 0; i < 41; i++) {
            clock.decrementSecond();
            Assert.assertTrue(!clock.isTimedOut());
        }
        Assert.assertEquals(39, clock.getTime());
        Assert.assertEquals(80, clock.getOriginalTime());
        Assert.assertEquals("00:39", clock.clockTime());

        clock.registerMove();
        Assert.assertEquals(39, clock.getTime());

        for (int i = 0; i < 38; i++) {
            clock.decrementSecond();
            Assert.assertTrue(!clock.isTimedOut());
        }
        Assert.assertEquals(1, clock.getTime());
        Assert.assertEquals("00:01", clock.clockTime());

        clock.decrementSecond();
        Assert.assertEquals(0, clock.getTime());
        Assert.assertEquals("00:00", clock.clockTime());
        Assert.assertTrue(clock.isTimedOut());
    }

    @Test
    public void testByoyomi() {
        ByoyomiPlayerTimeStatus clock = new ByoyomiPlayerTimeStatus(10, 3, 9);
        Assert.assertEquals("0:10 + 3*0:09", clock.timeDescription());

        for (int i = 0; i < 8; i++) {
            clock.registerMove();
            clock.decrementSecond();
            Assert.assertTrue(!clock.isTimedOut());
        }
        Assert.assertEquals(2, clock.getMainTime());
        Assert.assertEquals("00:02", clock.clockTime());

        for (int i = 0; i < 2; i++) {
            clock.decrementSecond();
        }
        Assert.assertTrue(!clock.isTimedOut());
        Assert.assertEquals("00:09", clock.clockTime());
        Assert.assertEquals(9, clock.getPeriodTime());
        Assert.assertEquals(3, clock.getNumPeriods());

        for (int i = 0; i < 5; i++) {
            clock.decrementSecond();
        }
        Assert.assertTrue(!clock.isTimedOut());
        Assert.assertEquals("00:04", clock.clockTime());
        Assert.assertEquals(4, clock.getPeriodTime());
        Assert.assertEquals(3, clock.getNumPeriods());

        clock.registerMove();
        Assert.assertTrue(!clock.isTimedOut());
        Assert.assertEquals("00:09", clock.clockTime());
        Assert.assertEquals(9, clock.getPeriodTime());
        Assert.assertEquals(3, clock.getNumPeriods());

        for (int i = 0; i < 20; i++) {
            clock.decrementSecond();
        }
        Assert.assertTrue(!clock.isTimedOut());
        Assert.assertEquals("00:07", clock.clockTime());
        Assert.assertEquals(7, clock.getPeriodTime());
        Assert.assertEquals(1, clock.getNumPeriods());

        clock.registerMove();
        Assert.assertTrue(!clock.isTimedOut());
        Assert.assertEquals("00:09", clock.clockTime());
        Assert.assertEquals(9, clock.getPeriodTime());
        Assert.assertEquals(1, clock.getNumPeriods());

        for (int i = 0; i < 9; i++) {
            clock.decrementSecond();
        }
        Assert.assertTrue(clock.isTimedOut());
        Assert.assertEquals("00:00", clock.clockTime());
    }

    @Test
    public void testCanadian() {
        CanadianPlayerTimeStatus clock = new CanadianPlayerTimeStatus(60, 60, 6);
        Assert.assertEquals("1:00 + 1:00/6", clock.timeDescription());
        Assert.assertEquals(60, clock.getMainTime());
        Assert.assertEquals("01:00", clock.clockTime());

        for (int i = 0; i < 59; i++) {
            clock.decrementSecond();
            Assert.assertTrue(!clock.isTimedOut());
        }
        Assert.assertEquals(1, clock.getMainTime());
        Assert.assertEquals("00:01", clock.clockTime());

        clock.decrementSecond();
        Assert.assertEquals(60, clock.getPeriodTime());
        Assert.assertEquals(6, clock.getStonesLeft());
        Assert.assertEquals("01:00", clock.clockTime());

        for (int i = 0; i < 5; i++) {
            clock.registerMove();
        }
        for (int i = 0; i < 56; i++) {
            clock.decrementSecond();
        }
        Assert.assertTrue(!clock.isTimedOut());
        Assert.assertEquals(4, clock.getPeriodTime());
        Assert.assertEquals(1, clock.getStonesLeft());
        Assert.assertEquals("00:04", clock.clockTime());

        clock.registerMove();
        Assert.assertTrue(!clock.isTimedOut());
        Assert.assertEquals(60, clock.getPeriodTime());
        Assert.assertEquals(6, clock.getStonesLeft());
        Assert.assertEquals("01:00", clock.clockTime());

        for (int i = 0; i < 59; i++) {
            clock.decrementSecond();
        }
        Assert.assertTrue(!clock.isTimedOut());
        Assert.assertEquals(1, clock.getPeriodTime());
        Assert.assertEquals(6, clock.getStonesLeft());
        Assert.assertEquals("00:01", clock.clockTime());

        clock.decrementSecond();
        Assert.assertTrue(clock.isTimedOut());
        Assert.assertEquals("00:00", clock.clockTime());
    }

}
