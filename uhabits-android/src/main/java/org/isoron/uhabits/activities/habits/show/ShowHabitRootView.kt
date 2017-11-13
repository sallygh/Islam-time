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

import android.content.*
import android.os.*
import android.support.v7.widget.Toolbar
import android.view.ViewGroup.LayoutParams.*
import android.widget.*
import org.isoron.androidbase.activities.*
import org.isoron.androidbase.utils.*
import org.isoron.uhabits.*
import org.isoron.uhabits.activities.habits.show.views.*
import org.isoron.uhabits.core.models.*
import org.isoron.uhabits.core.preferences.*
import org.isoron.uhabits.core.tasks.*
import org.isoron.uhabits.utils.*
import org.isoron.uhabits.widgets.*
import javax.inject.*

@ActivityScope
class ShowHabitRootView @Inject constructor(
        @ActivityContext context: Context,
        taskRunner: TaskRunner,
        prefs: Preferences,
        widgetUpdater: WidgetUpdater,
        private val habit: Habit
) : BaseRootView(context),
    ModelObservable.Listener {

    private val frequencyCard = FrequencyCard(context, habit, taskRunner)
    private val streakCard = StreakCard(context, habit, taskRunner)
    private val subtitleCard = SubtitleCard(context, habit)
    private val overviewCard = OverviewCard(context, habit)
    private val scoreCard = ScoreCard(context, habit, prefs, taskRunner, widgetUpdater)
    private val historyCard = HistoryCard(context, habit)
    private val barCard = BarCard(context, habit, prefs, taskRunner)
    private val tbar = buildToolbar()
    private var controller: Controller

    init {
        addView(RelativeLayout(context).apply {
            addAtTop(tbar)
            addBelow(ScrollView(context).apply {
                setBackgroundColor(sres.getColor(R.attr.windowBackgroundColor))
                addView(LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    val params = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    params.setMargins(dp(3.0f).toInt(), 0, dp(3.0f).toInt(), dp(3.0f).toInt())

                    addView(subtitleCard)
                    addView(overviewCard, params)
                    addView(scoreCard, params)
                    addView(barCard, params)
                    addView(historyCard, params)
                    addView(streakCard, params)
                    addView(frequencyCard, params)
                })
            }, tbar)
        }, MATCH_PARENT, MATCH_PARENT)

        controller = object : Controller {}
        displayHomeAsUp = true
        initToolbar()
    }

    override fun getToolbar(): Toolbar {
        return tbar
    }

    override fun getToolbarColor(): Int {
        val res = StyledResources(context)
        if (!res.getBoolean(R.attr.useHabitColorAsPrimary)) return super.getToolbarColor()
        else return PaletteUtils.getColor(context, habit.color)
    }

    override fun onModelChange() {
        Handler(Looper.getMainLooper()).post { toolbar.title = habit.name }
        controller.onToolbarChanged()
    }

    fun setController(controller: Controller) {
        this.controller = controller
        historyCard.setController(controller)
    }

    override fun initToolbar() {
        super.initToolbar()
        toolbar.title = habit.name
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        habit.observable.addListener(this)
    }

    override fun onDetachedFromWindow() {
        habit.observable.removeListener(this)
        super.onDetachedFromWindow()
    }

    interface Controller : HistoryCard.Controller {
        fun onToolbarChanged() {}
    }
}
