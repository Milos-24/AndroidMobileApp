package app.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import app.model.Settings

class SettingsDataManager(context: Context) {

    private val dbHelper: MyDatabaseHelper = MyDatabaseHelper(context)

    fun insertSettings(languages: String, notificationTime: String, notifications: Boolean): Long {
        val db = dbHelper.writableDatabase

        // Delete existing settings
        deleteAllSettings()

        // Insert new settings
        val values = ContentValues().apply {
            put(MyDatabaseHelper.SETTINGS_COLUMN_LANGUAGES, languages)
            put(MyDatabaseHelper.SETTINGS_COLUMN_NOTIFICATION_TIME, notificationTime)
            put(MyDatabaseHelper.SETTINGS_COLUMN_NOTIFICATIONS, notifications.toInt())
        }

        return db.insert(MyDatabaseHelper.TABLE_SETTINGS, null, values)
    }

    fun readSettings(): Settings? {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            MyDatabaseHelper.SETTINGS_COLUMN_LANGUAGES,
            MyDatabaseHelper.SETTINGS_COLUMN_NOTIFICATION_TIME,
            MyDatabaseHelper.SETTINGS_COLUMN_NOTIFICATIONS
        )
        val cursor: Cursor = db.query(
            MyDatabaseHelper.TABLE_SETTINGS,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            var languages = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.SETTINGS_COLUMN_LANGUAGES))
            var notificationTime = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.SETTINGS_COLUMN_NOTIFICATION_TIME))
            var notifications = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.SETTINGS_COLUMN_NOTIFICATIONS)) == 1

            if(languages ==null)
                languages="English"
            if(notificationTime == null)
                notificationTime="7 Days"
            if(notifications==null)
                notifications=false

            Settings(languages, notificationTime, notifications)
        } else {
            null
        }
    }

    fun deleteAllSettings() {
        val db = dbHelper.writableDatabase
        db.delete(MyDatabaseHelper.TABLE_SETTINGS, null, null)
    }

    private fun Boolean.toInt() = if (this) 1 else 0

}
