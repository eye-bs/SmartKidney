package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import java.io.ByteArrayOutputStream

@SuppressLint("Recycle")
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, TABLE_NAME, null, 1) {

    /**
     * Returns all the data from database
     * @return
     */
    val data: Cursor?
        get() {
             try {
                val db = this.writableDatabase
                val query = "SELECT $COL2 FROM $TABLE_NAME"
                 return db.rawQuery(query, null)
            } catch (e: Exception) {
                 return null
            }

        }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable =
            "CREATE TABLE $TABLE_NAME (ID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT , img blob)"
        db.execSQL(createTable)

    }


    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {
        db.execSQL("DROP IF TABLE EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addData(name: String, img: ByteArray): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL2, name)
        contentValues.put(COL3, img)
        Log.d(TAG, "addData: Adding $name to $TABLE_NAME")

        val result = db.insert(TABLE_NAME, null, contentValues)


        //if date as inserted incorrectly it will return -1
        return result != (-1).toLong()
    }

    fun getNameData(name: String): Cursor {
        val db = this.writableDatabase
        val query = "SELECT " + COL2 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + name + "'"
        return db.rawQuery(query, null)
    }

    fun getImgData(name: String): Cursor? {
         try {
            val db = this.writableDatabase
            val query = "SELECT " + COL3 + " FROM " + TABLE_NAME +
                    " WHERE " + COL2 + " = '" + name + "'"
             return db.rawQuery(query, null)
        } catch (e: Exception) {
             return  null
        }

    }

    /**
     * Returns only the ID that matches the name passed in
     * @param name
     * @return
     */
    fun getItemID(name: String): Cursor {
        val db = this.writableDatabase
        val query = "SELECT " + COL1 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + name + "'"
        return db.rawQuery(query, null)
    }

    /**
     * Updates the name field
     * @param newName
     * @param id
     * @param oldName
     */
    fun updateImage(newImage: ByteArray, name: String) {
        Log.d(TAG, "updating Image...")
        try {
            val db = this.writableDatabase
            val query = "UPDATE " + TABLE_NAME + " SET " + COL3 +
                    " = '" + newImage + "' WHERE " + COL2 + " = '" + name + "'"
            Log.d(TAG, "updateName: query: $query")
            Log.d(TAG, "updateImage: Setting name to newImage")
            db.execSQL(query)
        }catch (e:Exception){
            Log.wtf(Constant.TAG,"Update image Error $e")
        }
    }

    /**
     * Delete from database
     * @param id
     * @param name
     */
    fun deleteName(name: String) {
        val db = this.writableDatabase
        val query = ("DELETE FROM $TABLE_NAME WHERE $COL2 = '$name'")
        Log.d(TAG, "deleteName: query: $query")
        Log.d(TAG, "deleteName: Deleting $name from database.")
        db.execSQL(query)
    }

    companion object {
        private const val TAG = "DatabaseHelper"

        private const val TABLE_NAME = "people_table_1"
        private const val COL1 = "ID"
        private const val COL2 = "name"
        private const val COL3 = "img"
    }

}
