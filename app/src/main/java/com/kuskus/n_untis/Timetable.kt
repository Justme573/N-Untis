package com.kuskus.n_untis

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.DayOfWeek

class Timetable : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private val repository =
        WebUntisRepository()

    private val sessionStore by lazy {
        SessionStore(this)
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.timetable
        )

        recyclerView =
            findViewById(
                R.id.timetableRecycler
            )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        loadTimetable()
    }

    private fun loadTimetable() {

        lifecycleScope.launch {

            try {

                val storedSession =
                    withContext(Dispatchers.IO) {
                        sessionStore.getSession()
                    }

                if (storedSession == null) {
                    return@launch
                }

                repository.restoreSession(
                    storedSession
                )
                repository.restoreSession(storedSession)

                val statusData =
                    withContext(Dispatchers.IO) {
                        repository.getStatusData()
                    }

                println("STATUS DATA:")
                println(statusData)
                val subjects =
                    withContext(Dispatchers.IO) {
                        repository.getSubjects()
                    }

                println("SUBJECTS:")
                println(subjects)

                val date = get_week_date()

                val lessons =
                    withContext(Dispatchers.IO) {
                        repository.timetable(
                            startDate = date,
                            endDate = date
                        )
                    }.sortedBy {
                        it.startTime
                    }

                recyclerView.adapter =
                    TimetableAdapter(lessons)

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
    private fun get_week_date(): String {
        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        val weekdays = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
        val weekend = listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        val today = LocalDate.now()
        val today_weekday = LocalDate.now().dayOfWeek
        var date = today.format(formatter)

        if (weekdays.contains(today_weekday)) {
            date = today.format(formatter)
        } else if (weekend.contains(today_weekday)) {
            if (today_weekday == DayOfWeek.SATURDAY) {
                date = today.plusDays(2).format(formatter)
            } else {
                date = today.plusDays(1).format(formatter)
            }
        }
        return date
    }
}