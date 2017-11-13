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
import org.isoron.uhabits.core.preferences.*
import org.isoron.uhabits.core.tasks.*
import org.isoron.uhabits.utils.*

class BarCard(
        context: Context,
        habit: Habit,
        prefs: Preferences,
        private val taskRunner: TaskRunner
) : HabitCard(context, habit) {

    val chart = BarChart(context)
    val title = RegularSizeTextView(context, R.string.history)
    val spinner = TimeBucketSizeSpinner(context, prefs).apply {
        onBucketSizeSelected = {
            refreshData()
        }
    }

    init {
        addView(RelativeLayout(context).apply {
            addAtTopRight(spinner, width = WRAP_CONTENT, height = dp(22f).toInt())
            addAtTop(title) { it.setMargins(0, 0, 0, dp(12f).toInt()) }
            addBelow(chart, title, height = dp(220f).toInt())
        }, LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }

    override fun refreshData() {
        taskRunner.execute(object : Task {
            override fun doInBackground() {
                val checkmarks = habit.checkmarks.getCountBy(spinner.truncateField)
                chart.setCheckmarks(checkmarks)
                chart.setBucketSize(spinner.bucketSize)
            }

            override fun onPreExecute() {
                val color = PaletteUtils.getColor(context, habit.color)
                title.setTextColor(color)
                chart.setColor(color)
                chart.setTarget(habit.targetValue)
            }
        })
    }
}
