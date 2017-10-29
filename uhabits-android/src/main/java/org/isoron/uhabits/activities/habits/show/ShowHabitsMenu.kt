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

package org.isoron.uhabits.activities.habits.show

import android.view.MenuItem
import dagger.Lazy
import org.isoron.androidbase.activities.ActivityScope
import org.isoron.androidbase.activities.BaseActivity
import org.isoron.androidbase.activities.BaseMenu
import org.isoron.uhabits.R
import org.isoron.uhabits.core.ui.screens.habits.show.ShowHabitMenuBehavior
import javax.inject.Inject

@ActivityScope
class ShowHabitsMenu @Inject constructor(
        activity: BaseActivity,
        private val behavior: Lazy<ShowHabitMenuBehavior>
) : BaseMenu(activity) {

    override fun onItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit_habit -> {
                behavior.get().onEditHabit()
                return true
            }

            R.id.export -> {
                behavior.get().onExportCSV()
                return true
            }

            else -> return false
        }
    }

    override fun getMenuResourceId() = R.menu.show_habit
}
