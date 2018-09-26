package com.rudiworks.habittrainer.db

import android.provider.BaseColumns

val DATABASE_NAME = "habitrainer.db"
val DATABASE_VERSION = 1

object HabitEntry : BaseColumns {
    val TABLE_NAME = "habit"
    var _ID = "id"
    val TITLE_COL = "title"
    val DESC_COL = "description"
    val IMAGE_COL = "image"

}