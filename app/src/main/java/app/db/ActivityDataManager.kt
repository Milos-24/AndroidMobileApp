package app.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import app.model.ActivityItem

class ActivityDataManager(context: Context) {

    private val dbHelper: MyDatabaseHelper = MyDatabaseHelper(context)

    fun insertActivity(
        title: String,
        description: String,
        date: String,
        time: String,
        langLen: String,
        url1: String,
        url2: String,
        url3: String,
        type: String
    ): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(MyDatabaseHelper.ACTIVITY_COLUMN_TITLE, title)
            put(MyDatabaseHelper.ACTIVITY_COLUMN_DESCRIPTION, description)
            put(MyDatabaseHelper.ACTIVITY_COLUMN_DATE, date)
            put(MyDatabaseHelper.ACTIVITY_COLUMN_TIME, time)
            put(MyDatabaseHelper.ACTIVITY_COLUMN_LANG_LEN, langLen)
            put(MyDatabaseHelper.ACTIVITY_COLUMN_URL1, url1)
            put(MyDatabaseHelper.ACTIVITY_COLUMN_URL2, url2)
            put(MyDatabaseHelper.ACTIVITY_COLUMN_URL3, url3)
            put(MyDatabaseHelper.ACTIVITY_COLUMN_TYPE, type)
        }
        return db.insert(MyDatabaseHelper.TABLE_ACTIVITY, null, values)
    }

    fun readActivities(): List<ActivityItem> {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            MyDatabaseHelper.ACTIVITY_COLUMN_TITLE,
            MyDatabaseHelper.ACTIVITY_COLUMN_DESCRIPTION,
            MyDatabaseHelper.ACTIVITY_COLUMN_DATE,
            MyDatabaseHelper.ACTIVITY_COLUMN_TIME,
            MyDatabaseHelper.ACTIVITY_COLUMN_LANG_LEN,
            MyDatabaseHelper.ACTIVITY_COLUMN_URL1,
            MyDatabaseHelper.ACTIVITY_COLUMN_URL2,
            MyDatabaseHelper.ACTIVITY_COLUMN_URL3,
            MyDatabaseHelper.ACTIVITY_COLUMN_TYPE
        )
        val cursor: Cursor = db.query(
            MyDatabaseHelper.TABLE_ACTIVITY,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val activities = mutableListOf<ActivityItem>()

        with(cursor) {
            while (moveToNext()) {
                val title = getString(getColumnIndexOrThrow(MyDatabaseHelper.ACTIVITY_COLUMN_TITLE))
                val description = getString(getColumnIndexOrThrow(MyDatabaseHelper.ACTIVITY_COLUMN_DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(MyDatabaseHelper.ACTIVITY_COLUMN_DATE))
                val time = getString(getColumnIndexOrThrow(MyDatabaseHelper.ACTIVITY_COLUMN_TIME))
                val langLen = getString(getColumnIndexOrThrow(MyDatabaseHelper.ACTIVITY_COLUMN_LANG_LEN))
                val url1 = getString(getColumnIndexOrThrow(MyDatabaseHelper.ACTIVITY_COLUMN_URL1))
                val url2 = getString(getColumnIndexOrThrow(MyDatabaseHelper.ACTIVITY_COLUMN_URL2))
                val url3 = getString(getColumnIndexOrThrow(MyDatabaseHelper.ACTIVITY_COLUMN_URL3))
                val type = getString(getColumnIndexOrThrow(MyDatabaseHelper.ACTIVITY_COLUMN_TYPE))

                activities.add(
                    ActivityItem(
                        title,
                        description,
                        date,
                        time,
                        langLen,
                        url1,
                        url2,
                        url3,
                        type
                    )
                )
            }
        }

        return activities
    }

    fun deleteActivity(title: String): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            MyDatabaseHelper.TABLE_ACTIVITY,
            "${MyDatabaseHelper.ACTIVITY_COLUMN_TITLE} = ?",
            arrayOf(title)
        )
    }

    // Additional methods for updating other activity details as needed
}
