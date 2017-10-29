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

import dagger.*
import org.isoron.androidbase.activities.*
import org.isoron.uhabits.*
import org.isoron.uhabits.activities.common.dialogs.*
import org.isoron.uhabits.activities.habits.edit.*
import org.isoron.uhabits.core.models.*
import org.isoron.uhabits.core.ui.screens.habits.show.*
import javax.inject.*

@ActivityScope
class ShowHabitScreen @Inject constructor(
        activity: BaseActivity,
        rootView: ShowHabitRootView,
        menu: ShowHabitsMenu,
        private val habit: Habit,
        private val editHabitDialogFactory: EditHabitDialogFactory,
        private val behavior: Lazy<ShowHabitBehavior>
) : BaseScreen(activity),
    ShowHabitMenuBehavior.Screen,
    ShowHabitBehavior.Screen,
    HistoryEditorDialog.Controller,
    ShowHabitRootView.Controller {

    init {
        setMenu(menu)
        setRootView(rootView)
        rootView.setController(this)
    }

    override fun onEditHistoryButtonClick() {
        behavior.get().onEditHistory()
    }

    override fun onToggleCheckmark(timestamp: Timestamp) {
        behavior.get().onToggleCheckmark(timestamp)
    }

    override fun onToolbarChanged() {
        invalidateToolbar()
    }

    override fun reattachDialogs() {
        super.reattachDialogs()
        val historyEditor = activity
                .supportFragmentManager
                .findFragmentByTag("historyEditor") as HistoryEditorDialog?
        historyEditor?.setController(this)
    }

    override fun showEditHabitScreen(habit: Habit) {
        activity.showDialog(editHabitDialogFactory.edit(habit), "editHabit")
    }

    override fun showEditHistoryScreen() {
        val dialog = HistoryEditorDialog()
        dialog.setHabit(habit)
        dialog.setController(this)
        dialog.show(activity.supportFragmentManager, "historyEditor")
    }

    override fun showMessage(m: ShowHabitMenuBehavior.Message) {
        when (m) {
            ShowHabitMenuBehavior.Message.COULD_NOT_EXPORT -> showMessage(R.string.could_not_export)
        }
    }
}
