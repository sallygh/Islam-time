/*
 * Copyright (C) 2015-2017 Álinson Santos Xavier <isoron@gmail.com>
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

package org.isoron.uhabits.activities.common.views

import android.content.*
import android.support.v7.widget.*
import android.util.*
import android.view.*
import android.widget.*
import org.isoron.uhabits.*
import org.isoron.uhabits.core.preferences.*
import org.isoron.uhabits.core.utils.*

class TimeBucketSizeSpinner(context: Context,
                            prefs: Preferences) : AppCompatSpinner(context) {

    var onBucketSizeSelected: ((size: Int) -> Unit) = {}
    var bucketSize = BUCKET_SIZES[prefs.defaultScoreSpinnerPosition]
    val truncateField: DateUtils.TruncateField
        get() = bucketSizeToTruncateField(bucketSize)

    init {
        adapter = ArrayAdapter<String>(
                ContextThemeWrapper(context, R.style.SmallSpinner),
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.strengthIntervalNames))
        setSelection(prefs.defaultScoreSpinnerPosition)
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long) {
                prefs.defaultScoreSpinnerPosition = position
                bucketSize = BUCKET_SIZES[position]
                onBucketSizeSelected(bucketSize)
            }
        }
    }

    companion object {
        val BUCKET_SIZES = intArrayOf(1, 7, 31, 92, 365)
        fun bucketSizeToTruncateField(size: Int): DateUtils.TruncateField {
            if (size == 7) return DateUtils.TruncateField.WEEK_NUMBER
            if (size == 31) return DateUtils.TruncateField.MONTH
            if (size == 92) return DateUtils.TruncateField.QUARTER
            if (size == 365) return DateUtils.TruncateField.YEAR
            Log.e("ScoreCard", String.format("Unknown bucket size: %d", size))
            return DateUtils.TruncateField.MONTH
        }
    }
}