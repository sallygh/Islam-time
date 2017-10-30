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

package org.isoron.uhabits.activities.habits.show.views

import android.content.*
import android.view.ViewGroup.LayoutParams.*
import android.widget.*
import org.isoron.uhabits.*
import org.isoron.uhabits.activities.common.views.*
import org.isoron.uhabits.core.models.*
import org.isoron.uhabits.core.tasks.*
import org.isoron.uhabits.utils.*

class FrequencyCard(
        context: Context,
        habit: Habit,
        private val taskRunner: TaskRunner
) : HabitCard(context, habit) {

    private val title = RegularSizeTextView(context, R.string.frequency)
    private val chart = FrequencyChart(context)

    init {
        orientation = LinearLayout.VERTICAL
        addView(title, LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            setMargins(0, 0, 0, dp(12f).toInt())
        })
        addView(chart, LayoutParams(MATCH_PARENT, dp(200f).toInt()))
    }

    public override fun refreshData() {
        taskRunner.execute(object : Task {
            override fun onPreExecute() {
                val color = PaletteUtils.getColor(context, habit.color)
                title.setTextColor(color)
                chart.setColor(color)
            }

            override fun doInBackground() {
                chart.setFrequency(habit.repetitions.weekdayFrequency)
            }
        })
    }
}
