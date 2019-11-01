package th.ac.kku.smartkidney

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.text.TextUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import kotlinx.android.synthetic.main.activity_add_alarm.*
import th.ac.kku.smartkidney.ReadCalendar.calendarIdUser
import java.text.SimpleDateFormat
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION",
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class AddAlarmActivity : AppCompatActivity() {

    lateinit var startDate: Date
    lateinit var endDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)

        val callbackId = 42
        checkPermissions(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)

        val timeFormat = SimpleDateFormat("E dd MMM HH:mm", Locale.getDefault())
        val c = Calendar.getInstance()
        c.add(Calendar.HOUR, 1)
        startDate = c.time
        startDateTextview.text = timeFormat.format(startDate)
        c.add(Calendar.HOUR, 1)
        endDate = c.time
        endDateTextview.text = timeFormat.format(c.time)

        //-----------setOnClick-------------------------
        startDateLayout.setOnClickListener {
            setDate(startDateTextview, endDateTextview)
        }
        endDateLayout.setOnClickListener {
            setDate(endDateTextview, startDateTextview)
        }
        addAlarmBackBt.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
            finish()
        }
        addAlarmBt.setOnClickListener {
            if (TextUtils.isEmpty(titleEditText.text)) {
                titleEditText.error = getString(R.string.checkFill)
            } else {
                setAlarm()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        //---------------------------------------------------
    }

    private fun setDate(textView1: TextView, textView2: TextView) {
        val defaultDate: Date = if (textView1 == startDateTextview) {
            startDate
        } else {
            endDate
        }
        SingleDateAndTimePickerDialog.Builder(this)
                .bottomSheet()
                .curved()
                .mustBeOnFuture()
                .defaultDate(defaultDate)
                .displayAmPm(false)
                .listener { date ->
                    val timeFormat = SimpleDateFormat("E dd MMM HH:mm", Locale.getDefault())
                    val convertDate = timeFormat.format(date)
                    textView1.text = convertDate
                    if (textView1 == startDateTextview) {
                        startDate = date
                        if (startDate > endDate) {
                            date.hours = date.hours + 1
                            endDate = date
                            textView2.text = timeFormat.format(endDate)
                        }

                    } else if(textView1 == endDateTextview) {
                        endDate = date
                        if (endDate < startDate) {
                            date.hours = date.hours - 1
                            startDate = date
                            textView2.text = timeFormat.format(startDate)
                        }
                    }
                }
                .display()
    }

    @SuppressLint("ObsoleteSdkInt")
    fun setAlarm() {

        var calID: Long = calendarIdUser().toLong()
        val tz = TimeZone.getDefault()

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startDate.time)
            put(CalendarContract.Events.DTEND,  endDate.time)
            put(CalendarContract.Events.TITLE, titleEditText.text.toString())
            put(CalendarContract.Events.DESCRIPTION, contentEditText.text.toString() + "\n#SmartKidney")
            put(CalendarContract.Events.EVENT_LOCATION, locationEditText.text.toString())
            put(CalendarContract.Events.CALENDAR_ID, calID)
            put(CalendarContract.Events.EVENT_TIMEZONE, tz.id)
        }
        val eventUri: Uri = if (Build.VERSION.SDK_INT >= 8) {
            Uri.parse("content://com.android.calendar/events")
        } else {
            Uri.parse("content://calendar/events")
        }
        val uri: Uri = contentResolver.insert(eventUri, values)

        val eventID: Long = uri.lastPathSegment.toLong()

        val values2 = ContentValues().apply {
            put(CalendarContract.Reminders.MINUTES, 1440)
            put(CalendarContract.Reminders.EVENT_ID, eventID)
            put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }
        contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, values2)

    }

    private fun checkPermissions(callbackId: Int, vararg permissionsId: String) {
        var permissions = true
        for (p in permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED
        }
        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId)
    }

}
