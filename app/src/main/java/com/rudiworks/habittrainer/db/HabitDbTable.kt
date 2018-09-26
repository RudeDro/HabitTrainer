package com.rudiworks.habittrainer.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.rudiworks.habittrainer.Habit
import com.rudiworks.habittrainer.db.HabitEntry.TABLE_NAME
import com.rudiworks.habittrainer.db.HabitEntry._ID
import com.rudiworks.habittrainer.db.HabitEntry.TITLE_COL
import com.rudiworks.habittrainer.db.HabitEntry.DESC_COL
import com.rudiworks.habittrainer.db.HabitEntry.IMAGE_COL
import java.io.ByteArrayOutputStream



class HabitDbTable(context: Context) {

    private val dbHelper = HabitTrainerDb(context)

    fun store(habit:Habit): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        with (values) {
            put(TITLE_COL, habit.title)
            put(DESC_COL, habit.description)
            put(IMAGE_COL, toByteArray(habit.image))
        }

        return db.transaction {
            insert(TABLE_NAME, null, values)
        }
    }

    fun readAllHabits() : List<Habit> {
        val columns = arrayOf(_ID, TITLE_COL, DESC_COL, IMAGE_COL)
        val order = "$_ID ASC"

        val db = dbHelper.readableDatabase
        val cursor = db.doQuery(TABLE_NAME, columns, orderBy =  order)

        return parseHabitsFrom(cursor)
    }

    private fun parseHabitsFrom(cursor: Cursor): MutableList<Habit> {
        val habits = mutableListOf<Habit>()
        while (cursor.moveToNext()) {
            with(cursor) {
                val title = getString(TITLE_COL)
                val desc = getString(DESC_COL)
                val bitmap = getBitmap(IMAGE_COL)
                habits.add(Habit(title, desc, bitmap))
            }
        }
        cursor.close()
        return habits
    }

    private fun toByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        return stream.toByteArray()
    }
}

private fun SQLiteDatabase.doQuery(table: String,
                                          columns: Array<String>,
                                          selection: String? = null,
                                          selectionsArgs: Array<String>? = null,
                                          groupBy: String? = null,
                                          having: String? = null,
                                          orderBy: String? = null): Cursor{
    return query(table, columns, selection, selectionsArgs, groupBy, having, orderBy)
}

fun Cursor.getString(column: String): String = getString(getColumnIndex(column))

fun Cursor.getBitmap(column: String): Bitmap {
    val byteArray = getBlob(getColumnIndex(column))
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}

private inline fun <T> SQLiteDatabase.transaction(function: SQLiteDatabase.() -> T): T {
    beginTransaction()
    val result = try {
        val returnValue = function()
        setTransactionSuccessful()
        returnValue
    } finally {
        endTransaction()
    }
    close()
    return result
}
