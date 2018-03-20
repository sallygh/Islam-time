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

import static org.isoron.uhabits.models.Checkmark.*;

/**
 * Class that models the influence that one habit has over another.
 */
public class Influence
{
    private final int baseValues[];

    private final int otherValues[];

    public Influence(final Habit baseHabit,
                     final Habit otherHabit,
                     final long fromTimestamp,
                     final long toTimestamp)
    {
        CheckmarkList baseChecks = baseHabit.getCheckmarks();
        CheckmarkList otherChecks = otherHabit.getCheckmarks();
        baseValues = baseChecks.getValues(fromTimestamp, toTimestamp);
        otherValues = otherChecks.getValues(fromTimestamp, toTimestamp);
    }

    public double getAfter()
    {
        if (baseValues.length == 0) return 0f;

        long neg = 0;
        long pos = 0;

        for (int i = 0; i < baseValues.length; i++)
        {
            if (baseValues[i] != CHECKED_EXPLICITLY) continue;
            if (otherValues[i] == CHECKED_EXPLICITLY) pos++;
            else neg++;
        }

        return ((double) pos) / (pos + neg);
    }

    public double getBefore()
    {
        if (baseValues.length == 0) return 0f;

        long neg = 0;
        long pos = 0;

        for (int i = 0; i < baseValues.length; i++)
        {
            if (baseValues[i] == CHECKED_EXPLICITLY) continue;
            if (otherValues[i] == CHECKED_EXPLICITLY) pos++;
            else neg++;
        }

        return ((double) pos) / (pos + neg);
    }

    public double getDifference()
    {
        return getAfter() - getBefore();
    }
}
