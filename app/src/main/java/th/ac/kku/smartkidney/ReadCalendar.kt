package th.ac.kku.smartkidney

import java.util.Date
import java.util.HashSet
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.text.format.DateUtils

object ReadCalendar {
    private var cursor: Cursor? = null
    var calendarIdUser:String = ""
    val hashMapEvent:ArrayList<HashMap<String,String>> = ArrayList<HashMap<String,String>>()

    fun calendarIdUser():String{
        return calendarIdUser
    }
    fun getEventHashMapArr():ArrayList<HashMap<String,String>>{
        return hashMapEvent
    }

    fun readCalendar(context: Context):ArrayList<HashMap<String,String>> {

        val contentResolver = context.contentResolver

        cursor = contentResolver.query(
            Uri.parse("content://com.android.calendar/calendars"),
            arrayOf("_id", "calendar_displayName", "visible"), null, null, null)

        val calendarIds = HashSet<String>()

        try {
            println("Count=" + cursor!!.count)
            if (cursor!!.count > 0) {
                println("the control is just inside of the cursor.count loop")
                while (cursor!!.moveToNext()) {

                    val _id = cursor!!.getString(0)
                    val displayName = cursor!!.getString(1)
                    val selected = cursor!!.getString(2) != "0"

                    if (displayName.contains("@")){
                        calendarIdUser = _id
                        break
                    }

                    println("Id: $_id Display Name: $displayName Selected: $selected")
                    calendarIds.add(_id)
                }
            }
        } catch (ex: AssertionError) {
            ex.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // For each calendar, display all the events from the previous week to the end of next week.
        for (id in calendarIds) {
            val builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon()
            //Uri.Builder builder = Uri.parse("content://com.android.calendar/calendars").buildUpon();
            val now = Date().time

            ContentUris.appendId(builder, now - DateUtils.DAY_IN_MILLIS * 10000)
            ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS * 10000)

            val eventCursor = contentResolver.query(
                Uri.parse("content://com.android.calendar/events"),
                arrayOf("calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"),
                    "calendar_id=$calendarIdUser", null, "dtstart")

            println("eventCursor count=" + eventCursor!!.count)
            println("ecalendarIdUser = $calendarIdUser")
            if (eventCursor.count > 0) {

                if (eventCursor.moveToFirst()) {
                    do {
                        val hashMap:HashMap<String,String> = HashMap<String,String>()

                        val title = eventCursor.getString(1)
                        val description = eventCursor.getString(2)
                        val begin = Date(eventCursor.getLong(3))
                        val end = Date(eventCursor.getLong(4))
                        val location = eventCursor.getString(5)

                        if (description.contains("#SmartKidney")){
                            hashMap["title"] = title
                            hashMap["begin"] = begin.time.toString()
                            hashMap["end"] = end.time.toString()

                            hashMapEvent.add(hashMap)
                        }
                    } while (eventCursor.moveToNext())
                }
            }
            break
        }
        return hashMapEvent
    }
}