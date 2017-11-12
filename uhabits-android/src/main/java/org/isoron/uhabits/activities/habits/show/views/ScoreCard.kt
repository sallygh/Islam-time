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
import org.isoron.uhabits.widgets.*

class ScoreCard(context: Context,
                habit: Habit,
                prefs: Preferences,
                private val taskRunner: TaskRunner,
                private val widgetUpdater: WidgetUpdater
) : HabitCard(context, habit) {

    val chart = ScoreChart(context)
    val title = RegularSizeTextView(context, R.string.score)
    val spinner = TimeBucketSizeSpinner(context, prefs).apply {
        onBucketSizeSelected = {
            widgetUpdater.updateWidgets()
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

    public override fun refreshData() {
        taskRunner.execute(object : Task {
            override fun doInBackground() {
                val scores = when (spinner.bucketSize) {
                    1 -> habit.scores.toList()
                    else -> habit.scores.groupBy(spinner.truncateField)
                }
                chart.setScores(scores)
                chart.setBucketSize(spinner.bucketSize)
            }

            override fun onPreExecute() {
                val color = PaletteUtils.getColor(context, habit.color)
                title.setTextColor(color)
                chart.setColor(color)
            }
        })
    }
}
