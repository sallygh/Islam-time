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
import android.os.Build.VERSION.*
import android.os.Build.VERSION_CODES.*
import android.widget.*
import org.isoron.androidbase.utils.InterfaceUtils.*
import org.isoron.uhabits.*
import org.isoron.uhabits.core.models.*
import org.isoron.uhabits.utils.*

abstract class HabitCard(
        context: Context,
        protected val habit: Habit
) : LinearLayout(context), ModelObservable.Listener {

    init {
        setPadding(dp(16f), dp(16f), dp(4f), dp(16f))
        setBackgroundColor(sres.getColor(R.attr.cardBackgroundColor))
        if (SDK_INT >= LOLLIPOP) elevation = dpToPixels(context, 1f)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshData()
        habit.observable.addListener(this)
        habit.repetitions.observable.addListener(this)
    }

    override fun onDetachedFromWindow() {
        habit.repetitions.observable.removeListener(this)
        habit.observable.removeListener(this)
        super.onDetachedFromWindow()
    }

    override fun onModelChange() {
        post { refreshData() }
    }

    protected abstract fun refreshData()
}
