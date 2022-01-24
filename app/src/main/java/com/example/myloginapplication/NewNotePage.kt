package com.example.myloginapplication

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NewNotePage : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private lateinit var createNewTitle: EditText
    private lateinit var createNewNotes: EditText
    private lateinit var saveFab: FloatingActionButton
    private lateinit var backButton: ImageButton
    private lateinit var reminder: ImageButton
    private lateinit var chooseReminderDate: Button
    private lateinit var chooseTime: Button
    private lateinit var close: ImageButton
    private lateinit var save: ImageButton
    var year = 0
    var month = 0
    var day = 0
    var hour = 0
    var minute = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_new_note_page, container, false)
        saveFab = view.findViewById(R.id.save)
        createNewTitle = view.findViewById(R.id.createTitle)
        createNewNotes = view.findViewById(R.id.createNote)
        backButton = view.findViewById(R.id.backButton)
        reminder = view.findViewById(R.id.reminderLayout)
        (activity as AppCompatActivity?)!!.supportActionBar?.hide()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        saveDataToFireStore()
        backToHomeNoteActivity()
        reminderOnClick()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun backToHomeNoteActivity() {
        backButton.setOnClickListener {
            startActivity(Intent(context, NoteHomeActivity::class.java))
        }
    }

    private fun reminderOnClick() {
        reminder.setOnClickListener {
            if (createNewNotes.text.toString() == "" && createNewTitle.text.toString() == "") {
                Toast.makeText(context, "Title & content is not added", Toast.LENGTH_SHORT).show()
            } else {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.reminder_layout)
                chooseReminderDate = dialog.findViewById(R.id.chooseDate)
                chooseTime = dialog.findViewById(R.id.chooseTime)
                close = dialog.findViewById(R.id.closeReminder)
                save = dialog.findViewById(R.id.saveReminder)
                chooseReminderDate.setOnClickListener {
                    selectDate()
                }
                chooseTime.setOnClickListener {
                    selectTime()
                }
                close.setOnClickListener {
                    dialog.dismiss()
                }
                save.setOnClickListener {
                    val delay = calculateTimeDifference(hour, minute)
                    val request = OneTimeWorkRequestBuilder<MyWorker>().setInitialDelay(
                        delay.toLong(),
                        TimeUnit.MINUTES
                    ).setInputData(
                        workDataOf(
                            "Title" to createNewTitle.text.toString(),
                            "Content" to createNewNotes.text.toString()
                        )
                    ).build()
                    WorkManager.getInstance(requireContext()).enqueue(request)
                    WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(request.id)
                        .observe(viewLifecycleOwner, Observer<WorkInfo>() {
                            Log.d("Notification status: ", it.state.name)
                        })
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
    }

    private fun saveDataToFireStore() {
        saveFab.setOnClickListener {
            val noteServices = NoteServices()
            val title = createNewTitle.text.toString()
            val noteContent = createNewNotes.text.toString()
            val archive: Boolean = false
            if (title.isEmpty() || noteContent.isEmpty()) {
                Toast.makeText(context, "Title and Notes must be entered", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val context = requireContext()
                val sdf = SimpleDateFormat("dd MMMM,hh.mm a", Locale.getDefault())
                val currentDateandTime: String = sdf.format(Date())
                noteServices.addNote(title, noteContent, currentDateandTime, archive, context)
                startActivity(Intent(activity, NoteHomeActivity::class.java))
            }
//            Toast.makeText(
//                context,
//                "The Reminder is set on ${chooseReminderDate.text} in ${chooseTime.text}",
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }

    private fun calculateTimeDifference(hour: Int, minute: Int): Int {
        val today = Calendar.getInstance()
        val today_hr = today.get(Calendar.HOUR_OF_DAY)
        val today_min = today.get(Calendar.MINUTE)
        if (hour == today_hr) {
            if (minute > today_min)
                return minute - today_min
            else
                return 0
        } else if (hour == today_hr + 1) {
            if (minute > today_min) {
                return minute - today_min + 60
            } else if (minute == today_min) {
                return 60
            } else {
                return (60 - today_min) + minute
            }
        } else {
            var timeDiff: Int = 0
            var tempHr = hour
            while (tempHr > today_hr + 1) {
                timeDiff++
                tempHr--
            }
            if (minute > today_min) {
                return ((minute - today_min) + (timeDiff * 60))
            } else {
                return ((60 - today_min + minute) + (timeDiff * 60))
            }
        }
    }

    private fun selectTime() {
        val calender = Calendar.getInstance()
        val hr = calender.get(Calendar.HOUR_OF_DAY)
        val min = calender.get(Calendar.MINUTE)
        TimePickerDialog(
            requireContext(), this, hr, min,
            android.text.format.DateFormat.is24HourFormat(requireContext())
        ).show()
    }

    private fun selectDate() {
        val calender = Calendar.getInstance()
        val yr = calender.get(Calendar.YEAR)
        val monthOfYr = calender.get(Calendar.MONTH)
        val dayOfYr = calender.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(requireContext(), this, yr, monthOfYr, dayOfYr).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(p0: DatePicker?, years: Int, months: Int, days: Int) {
        Calendar.getInstance().let {
            it.set(years, months, days)
            chooseReminderDate.text = days.toString().padStart(2, '0') + "-" +
                    (months + 1).toString().padStart(2, '0') + "-" + years
            this.year = years
            this.month = months + 1
            this.day = days
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minuteOfDay: Int) {
        var hour = hourOfDay
        var median = "AM"
        if (hour > 12) {
            hour = hourOfDay % 12
            median = "PM"
        }
        val time: String = "$hour:$minuteOfDay $median"
        chooseTime.text = time
        this.hour = hourOfDay
        this.minute = minuteOfDay
    }
}