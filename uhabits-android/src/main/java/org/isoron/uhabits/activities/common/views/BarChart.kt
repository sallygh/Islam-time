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

package org.isoron.uhabits.activities.common.views

import android.content.*
import android.graphics.*
import org.isoron.androidbase.utils.InterfaceUtils.*
import org.isoron.uhabits.*
import org.isoron.uhabits.activities.habits.list.views.*
import org.isoron.uhabits.core.models.*
import org.isoron.uhabits.core.utils.*
import org.isoron.uhabits.utils.*
import java.util.*

class BarChart(context: Context) : ScrollableChart(context) {

    private val style = Style()
    private val renderer = Renderer()
    private var checkmarks = Collections.emptyList<Checkmark>()

    private var bucketSize = 7
    private var maxValue: Double = 0.toDouble()
    private var target: Double = 0.toDouble()

    fun setBucketSize(bucketSize: Int) {
        this.bucketSize = bucketSize
        postInvalidate()
    }

    fun setCheckmarks(checkmarks: List<Checkmark>) {
        this.checkmarks = checkmarks

        maxValue = 1.0
        for (c in checkmarks) maxValue = Math.max(maxValue, c.value.toDouble())
        maxValue = Math.ceil(maxValue / 1000 * 1.05) * 1000

        postInvalidate()
    }

    fun setColor(primaryColor: Int) {
        this.style.primaryColor = primaryColor
        postInvalidate()
    }

    fun setIsTransparencyEnabled(enabled: Boolean) {
        this.style.isTransparencyEnabled = enabled
        postInvalidate()
    }

    fun setTarget(target: Double) {
        this.target = target
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        renderer.draw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        renderer.setSize(width, maxOf(height, 200))
    }

    inner class Style(
            var textColor: Int = sres.getColor(R.attr.mediumContrastTextColor),
            var gridColor: Int = sres.getColor(R.attr.lowContrastTextColor),
            var backgroundColor: Int = sres.getColor(R.attr.cardBackgroundColor),
            var primaryColor: Int = Color.BLACK,
            var isTransparencyEnabled: Boolean = false
    )

    inner class Renderer {
        private var em = 0f
        private val dfMonth = AndroidDateFormats.fromSkeleton("MMM")
        private val dfDay = AndroidDateFormats.fromSkeleton("d")
        private val dfYear = AndroidDateFormats.fromSkeleton("yyyy")
        private var baseSize = 0f
        private var paddingTop = 0f
        private var columnWidth = 0f
        private var columnHeight = 0f
        private var nColumns = 0
        private var skipYear = 0
        private var previousYearText: String? = null
        private var previousMonthText: String? = null
        private var transparentCache: Bitmap? = null
        private var transparentCacheCanvas: Canvas? = null
        private val rect = RectF()
        private val prevRect = RectF()
        private val pGrid = Paint().apply { isAntiAlias = true }
        private val pText = Paint().apply { isAntiAlias = true; textAlign = Paint.Align.CENTER }
        private val pGraph = Paint().apply { isAntiAlias = true; textAlign = Paint.Align.CENTER }

        fun draw(canvas: Canvas) {
            if (style.isTransparencyEnabled) {
                if (transparentCache == null) {
                    transparentCache = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    transparentCacheCanvas = Canvas(transparentCache)
                }
                transparentCache!!.eraseColor(Color.TRANSPARENT)
                innerDraw(transparentCacheCanvas!!)
                canvas.drawBitmap(transparentCache!!, 0f, 0f, null)
            } else {
                innerDraw(canvas)
            }
        }

        private fun innerDraw(canvas: Canvas) {
            rect.set(0f, 0f, nColumns * columnWidth, columnHeight)
            rect.offset(0f, paddingTop)
            drawGrid(canvas, rect)

            pText.color = style.textColor
            pGraph.color = style.primaryColor
            prevRect.setEmpty()

            previousMonthText = ""
            previousYearText = ""
            skipYear = 0

            for (k in 0 until nColumns) {
                val offset = nColumns - k - 1 + dataOffset
                if (offset >= checkmarks.size) continue

                val value = checkmarks[offset].value.toDouble()
                val timestamp = checkmarks[offset].timestamp
                val height = (columnHeight * value / maxValue).toInt()

                rect.set(0f, 0f, baseSize, height.toFloat())
                rect.offset(k * columnWidth + (columnWidth - baseSize) / 2,
                        (paddingTop + columnHeight - height))

                drawValue(canvas, rect, value)
                drawBar(canvas, rect, value)

                prevRect.set(rect)
                rect.set(0f, 0f, columnWidth, columnHeight)
                rect.offset(k * columnWidth, paddingTop)

                drawFooter(canvas, rect, timestamp)
            }
        }

        private fun drawFooter(canvas: Canvas, rect: RectF, currentDate: Timestamp) {
            val yearText = dfYear.format(currentDate.toJavaDate())
            val monthText = dfMonth.format(currentDate.toJavaDate())
            val dayText = dfDay.format(currentDate.toJavaDate())
            pText.color = style.textColor

            var shouldPrintYear = true
            if (yearText == previousYearText) shouldPrintYear = false

            if (shouldPrintYear) {
                previousYearText = yearText
                previousMonthText = ""
                pText.textAlign = Paint.Align.CENTER

                if(bucketSize < 365) {
                    canvas.drawText(yearText, rect.centerX(), rect.bottom + em * 2.2f, pText)
                } else {
                    canvas.save()
                    canvas.translate(rect.centerX(), rect.bottom + em * 1.7f)
                    canvas.rotate(-45f)
                    canvas.drawText(yearText, 0f, 0f, pText)
                    canvas.restore()
                }
            }

            if (bucketSize < 365) {
                val text: String
                if (monthText != previousMonthText) {
                    previousMonthText = monthText
                    text = monthText
                } else {
                    text = dayText
                }
                canvas.drawText(text, rect.centerX(), rect.bottom + em * 1.2f, pText)
            }
        }

        private fun drawGrid(canvas: Canvas, rGrid: RectF) {
            val nRows = 5
            val rowHeight = rGrid.height() / nRows

            pText.color = style.textColor
            pGrid.color = style.gridColor

            for (i in 0 until nRows) {
                canvas.drawLine(rGrid.left, rGrid.top, rGrid.right, rGrid.top,
                        pGrid)
                rGrid.offset(0f, rowHeight)
            }

            canvas.drawLine(rGrid.left, rGrid.top, rGrid.right, rGrid.top, pGrid)
        }

        private fun drawValue(canvas: Canvas, rect: RectF, value: Double) {
            if (value == 0.0) return

            var activeColor = style.textColor
            if (value / 1000 >= target)
                activeColor = style.primaryColor

            val label = (value / 1000).toShortString()
            val rText = Rect()
            pText.getTextBounds(label, 0, label.length, rText)

            val offset = 0.5f * em
            val x = rect.centerX()
            val y = rect.top - offset
            val cap = (-0.1f * em).toInt()

            rText.offset(x.toInt(), y.toInt())
            rText.offset(-rText.width() / 2, 0)
            rText.inset(3 * cap, cap)

            setModeOrColor(pText, XFERMODE_CLEAR, style.backgroundColor)
            canvas.drawRect(rText, pText)

            setModeOrColor(pText, XFERMODE_SRC, activeColor)
            canvas.drawText(label, x, y, pText)
        }

        private fun drawBar(canvas: Canvas, rect: RectF, value: Double) {
            val margin = baseSize * 0.225f

            var color = style.textColor
            if (value / 1000 >= target) color = style.primaryColor

            rect.inset(-margin, 0f)
            setModeOrColor(pGraph, XFERMODE_CLEAR, style.backgroundColor)
            canvas.drawRect(rect, pGraph)

            rect.inset(margin, 0f)
            setModeOrColor(pGraph, XFERMODE_SRC, color)
            canvas.drawRect(rect, pGraph)

            if (style.isTransparencyEnabled) pGraph.xfermode = XFERMODE_SRC
        }

        private fun setModeOrColor(p: Paint, mode: PorterDuffXfermode, color: Int) {
            if (style.isTransparencyEnabled) p.xfermode = mode
            else p.color = color
        }

        fun setSize(width: Int, height: Int) {

            fun getMaxDayWidth(): Float {
                var maxDayWidth = 0f
                val day = DateUtils.getStartOfTodayCalendar()

                for (i in 0..27) {
                    day.set(Calendar.DAY_OF_MONTH, i)
                    val monthWidth = pText.measureText(dfMonth.format(day.time))
                    maxDayWidth = Math.max(maxDayWidth, monthWidth)
                }

                return maxDayWidth
            }

            fun getMaxMonthWidth(): Float {
                var maxMonthWidth = 0f
                val day = DateUtils.getStartOfTodayCalendar()

                for (i in 0..11) {
                    day.set(Calendar.MONTH, i)
                    val monthWidth = pText.measureText(dfMonth.format(day.time))
                    maxMonthWidth = Math.max(maxMonthWidth, monthWidth)
                }

                return maxMonthWidth
            }

            val maxTextSize = getDimension(context, R.dimen.tinyTextSize)
            val textSize = height * 0.06f
            pText.textSize = Math.min(textSize, maxTextSize)
            em = pText.fontSpacing

            val footerHeight = (3 * em).toInt()
            paddingTop = em

            baseSize = (height - footerHeight - paddingTop) / 12
            columnWidth = baseSize
            columnWidth = Math.max(columnWidth, getMaxDayWidth() * 1.5f)
            columnWidth = Math.max(columnWidth, getMaxMonthWidth() * 1.2f)

            nColumns = (width / columnWidth).toInt()
            columnWidth = width.toFloat() / nColumns
            setScrollerBucketSize(columnWidth.toInt())

            columnHeight = 12f * baseSize

            val minStrokeWidth = dpToPixels(context, 1f)
            pGraph.textSize = baseSize * 0.5f
            pGraph.strokeWidth = baseSize * 0.1f
            pGrid.strokeWidth = Math.min(minStrokeWidth, baseSize * 0.05f)

            transparentCache?.recycle()
            transparentCache = null
        }
    }

    companion object {
        private val XFERMODE_CLEAR = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        private val XFERMODE_SRC = PorterDuffXfermode(PorterDuff.Mode.SRC)
    }
}
