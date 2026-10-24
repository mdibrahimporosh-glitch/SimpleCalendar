package com.porosh.simplecalendar

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : Activity() {
    private val green = Color.rgb(35, 100, 70)
    private val paleGreen = Color.rgb(226, 241, 232)
    private val ink = Color.rgb(32, 42, 36)
    private val muted = Color.rgb(112, 124, 116)
    private val prefs by lazy { getSharedPreferences("calendar_notes", MODE_PRIVATE) }
    private val monthCal = Calendar.getInstance()
    private var selectedDate = Calendar.getInstance()
    private lateinit var monthTitle: TextView
    private lateinit var selectedTitle: TextView
    private lateinit var noteText: TextView
    private lateinit var grid: GridLayout
    private lateinit var dateKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        monthCal.timeInMillis = selectedDate.timeInMillis
        buildScreen()
        renderCalendar()
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun rounded(bg: Int, radius: Int = 14, stroke: Int? = null): GradientDrawable {
        return GradientDrawable().apply {
            setColor(bg)
            cornerRadius = dp(radius).toFloat()
            if (stroke != null) setStroke(dp(1), stroke)
        }
    }

    private fun label(text: String, size: Float, color: Int, bold: Boolean = false): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER_VERTICAL
        }
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(20), dp(18), dp(16))
            setBackgroundColor(Color.rgb(250, 249, 245))
        }

        val appName = label("Simple Calendar", 25f, ink, true)
        root.addView(appName, LinearLayout.LayoutParams(-1, dp(42)))
        val subtitle = label("Keep track of your days", 14f, muted)
        root.addView(subtitle, LinearLayout.LayoutParams(-1, dp(28)))

        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(4), dp(8), dp(4))
            background = rounded(Color.WHITE, 16)
        }
        val prev = button("‹")
        prev.textSize = 30f
        prev.setOnClickListener {
            monthCal.add(Calendar.MONTH, -1)
            renderCalendar()
        }
        nav.addView(prev, LinearLayout.LayoutParams(dp(48), dp(48)))
        monthTitle = label("", 18f, ink, true).apply { gravity = Gravity.CENTER }
        nav.addView(monthTitle, LinearLayout.LayoutParams(0, dp(48), 1f))
        val next = button("›")
        next.textSize = 30f
        next.setOnClickListener {
            monthCal.add(Calendar.MONTH, 1)
            renderCalendar()
        }
        nav.addView(next, LinearLayout.LayoutParams(dp(48), dp(48)))
        root.addView(nav, LinearLayout.LayoutParams(-1, dp(58)).apply {
            topMargin = dp(18)
        })

        val todayButton = TextView(this).apply {
            text = "Go to today"
            textSize = 14f
            setTextColor(green)
            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
            setOnClickListener {
                selectedDate = Calendar.getInstance()
                monthCal.timeInMillis = selectedDate.timeInMillis
                renderCalendar()
            }
        }
        root.addView(todayButton, LinearLayout.LayoutParams(-1, dp(36)))

        val calendarCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(Color.WHITE, 18)
            setPadding(dp(8), dp(12), dp(8), dp(12))
        }
        val weekdays = GridLayout(this).apply { columnCount = 7 }
        val weekdayNames = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        weekdayNames.forEach { day ->
            val t = label(day, 12f, muted, true).apply { gravity = Gravity.CENTER }
            weekdays.addView(t, GridLayout.LayoutParams().apply {
                width = 0; height = dp(32); columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            })
        }
        calendarCard.addView(weekdays)
        grid = GridLayout(this).apply { columnCount = 7 }
        calendarCard.addView(grid)
        root.addView(calendarCard, LinearLayout.LayoutParams(-1, dp(310)).apply {
            topMargin = dp(8)
        })

        val details = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(Color.WHITE, 18)
            setPadding(dp(16), dp(14), dp(16), dp(14))
        }
        selectedTitle = label("", 17f, ink, true)
        details.addView(selectedTitle, LinearLayout.LayoutParams(-1, dp(30)))
        noteText = label("", 14f, muted)
        noteText.maxLines = 3
        details.addView(noteText, LinearLayout.LayoutParams(-1, dp(48)))
        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val addNote = TextView(this).apply {
            text = "+ Add / edit note"
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
            background = rounded(green, 12)
            setPadding(dp(16), 0, dp(16), 0)
            setOnClickListener { showNoteDialog() }
        }
        actions.addView(addNote, LinearLayout.LayoutParams(-2, dp(42)))
        val clearNote = TextView(this).apply {
            text = "Clear"
            textSize = 14f
            setTextColor(green)
            gravity = Gravity.CENTER
            setPadding(dp(16), 0, dp(10), 0)
            setOnClickListener {
                prefs.edit().remove(dateKey).apply()
                updateSelectedDetails()
                renderCalendar()
            }
        }
        actions.addView(clearNote, LinearLayout.LayoutParams(-2, dp(42)))
        details.addView(actions, LinearLayout.LayoutParams(-1, dp(46)))
        root.addView(details, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(14)
        })

        val footer = label("Your notes stay on this device.", 12f, muted).apply {
            gravity = Gravity.CENTER
        }
        root.addView(footer, LinearLayout.LayoutParams(-1, dp(32)))

        setContentView(root)
    }

    private fun button(textValue: String): TextView = TextView(this).apply {
        text = textValue
        textSize = 18f
        setTextColor(green)
        gravity = Gravity.CENTER
        background = rounded(paleGreen, 12)
    }

    private fun renderCalendar() {
        val fmt = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
        monthTitle.text = fmt.format(monthCal.time)
        grid.removeAllViews()

        val first = (monthCal.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val offset = first.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
        val days = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val total = ((offset + days + 6) / 7) * 7
        val today = Calendar.getInstance()

        for (cell in 0 until total) {
            val day = cell - offset + 1
            val cellView = FrameLayout(this)
            val lp = GridLayout.LayoutParams().apply {
                width = 0
                height = dp(38)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(dp(1), dp(1), dp(1), dp(1))
            }
            if (day in 1..days) {
                val c = (monthCal.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) }
                val key = keyFor(c)
                val isSelected = key == keyFor(selectedDate)
                val isToday = c.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    c.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                val number = TextView(this).apply {
                    text = day.toString()
                    textSize = 14f
                    gravity = Gravity.CENTER
                    setTextColor(if (isSelected) Color.WHITE else ink)
                    if (isSelected) {
                        background = rounded(green, 12)
                    } else if (isToday) {
                        background = rounded(paleGreen, 12, green)
                    }
                }
                cellView.addView(number, FrameLayout.LayoutParams(-1, -1))
                if (prefs.contains(key)) {
                    val dot = View(this).apply {
                        background = rounded(if (isSelected) Color.WHITE else green, 4)
                    }
                    cellView.addView(dot, FrameLayout.LayoutParams(dp(5), dp(5), Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL).apply {
                        bottomMargin = dp(3)
                    })
                }
                cellView.setOnClickListener {
                    selectedDate = c
                    updateSelectedDetails()
                    renderCalendar()
                }
            }
            grid.addView(cellView, lp)
        }
        updateSelectedDetails()
    }

    private fun keyFor(c: Calendar): String = String.format(
        Locale.US, "%04d-%02d-%02d",
        c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH)
    )

    private fun updateSelectedDetails() {
        dateKey = keyFor(selectedDate)
        selectedTitle.text = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH).format(selectedDate.time)
        val note = prefs.getString(dateKey, null)
        noteText.text = note?.takeIf { it.isNotBlank() } ?: "No note for this day yet."
    }

    private fun showNoteDialog() {
        val edit = EditText(this).apply {
            hint = "e.g. Chemistry exam at 10 AM"
            setText(prefs.getString(dateKey, ""))
            minLines = 2
            maxLines = 5
            setPadding(dp(12), dp(10), dp(12), dp(10))
        }
        AlertDialog.Builder(this)
            .setTitle("Note for ${SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(selectedDate.time)}")
            .setView(edit)
            .setPositiveButton("Save") { _, _ ->
                val note = edit.text.toString().trim()
                if (note.isEmpty()) prefs.edit().remove(dateKey).apply()
                else prefs.edit().putString(dateKey, note).apply()
                updateSelectedDetails()
                renderCalendar()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
