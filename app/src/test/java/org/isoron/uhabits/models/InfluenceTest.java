/*
 * Copyright (C) 2016 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.uhabits.models;

import org.isoron.uhabits.*;
import org.isoron.uhabits.utils.*;
import org.junit.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.number.IsCloseTo.*;


public class InfluenceTest extends BaseUnitTest
{
    private static final double E = 1e-6;

    private Habit baseHabit;

    private Habit otherHabit;

    private Influence influence;

    @Override
    public void setUp()
    {
        super.setUp();
        baseHabit = modelFactory.buildHabit();
        otherHabit = modelFactory.buildHabit();
    }

    @Test
    public void test()
    {
        toggleRepetitions(15, 10, 5, 15);
        long today = DateUtils.getStartOfToday();
        long day = DateUtils.millisecondsInOneDay;
        influence =
            new Influence(baseHabit, otherHabit, today - 44 * day, today);

        assertThat(influence.getBefore(), closeTo(0.25, E));
        assertThat(influence.getAfter(), closeTo(0.60, E));
        assertThat(influence.getDifference(), closeTo(0.35, E));
    }

    protected void toggleRepetitions(int no_no,
                                     int yes_no,
                                     int no_yes,
                                     int yes_yes)
    {
        RepetitionList baseReps = baseHabit.getRepetitions();
        RepetitionList otherReps = otherHabit.getRepetitions();

        long current = DateUtils.getStartOfToday();
        long day = DateUtils.millisecondsInOneDay;

        current -= day * no_no;

        for (int i = 0; i < yes_no; i++, current -= day)
            baseReps.toggleTimestamp(current);

        for (int i = 0; i < no_yes; i++, current -= day)
            otherReps.toggleTimestamp(current);

        for (int i = 0; i < yes_yes; i++, current -= day)
        {
            baseReps.toggleTimestamp(current);
            otherReps.toggleTimestamp(current);
        }
    }
}