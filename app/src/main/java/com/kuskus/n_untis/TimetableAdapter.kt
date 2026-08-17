package com.kuskus.n_untis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimetableAdapter(
    private val lessons: List<Lesson>
) : RecyclerView.Adapter<TimetableAdapter.LessonViewHolder>() {

    class LessonViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        val Layout: LinearLayout =
            view.findViewById(R.id.lesson_layout)
        val timeText: TextView =
            view.findViewById(R.id.timeText)

        val subjectText: TextView =
            view.findViewById(R.id.subjectText)

        val teacherText: TextView =
            view.findViewById(R.id.teacherText)

        val roomText: TextView =
            view.findViewById(R.id.roomText)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LessonViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.lesson_item,
                    parent,
                    false
                )

        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: LessonViewHolder,
        position: Int
    ) {

        val lesson =
            lessons[position]

        holder.timeText.text =
            "${formatTime(lesson.startTime)} - " +
                    formatTime(lesson.endTime)

        holder.subjectText.text =
            lesson.su
                ?.firstOrNull()
                ?.name
                ?: "Unbekannt"

        holder.teacherText.text =
            lesson.te
                ?.firstOrNull()
                ?.name
                ?: "Kein Lehrer"

        holder.roomText.text =
            lesson.ro
                ?.firstOrNull()
                ?.name
                ?: "Kein Raum"

        if (lesson.code == "cancelled") {
            holder.Layout.setBackgroundResource(
                R.drawable.background_lesson_canceled
            )
        } else {
            holder.Layout.setBackgroundResource(
                R.drawable.background_lesson
            )
        }
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    private fun formatTime(
        time: Int?
    ): String {

        if (time == null) {
            return "--:--"
        }

        val hours = time / 100
        val minutes = time % 100

        return "%02d:%02d".format(
            hours,
            minutes
        )
    }
}