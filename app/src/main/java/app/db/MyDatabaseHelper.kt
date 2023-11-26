package app.db
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "mydatabase"
        const val DATABASE_VERSION = 2

        // Settings table
        const val TABLE_SETTINGS = "settings"
        const val SETTINGS_COLUMN_LANGUAGES = "languages"
        const val SETTINGS_COLUMN_NOTIFICATION_TIME = "notification_time"
        const val SETTINGS_COLUMN_NOTIFICATIONS = "notifications"

        // Activity table
        const val TABLE_ACTIVITY = "activity"
        const val ACTIVITY_COLUMN_TITLE = "title"
        const val ACTIVITY_COLUMN_DESCRIPTION = "description"
        const val ACTIVITY_COLUMN_DATE = "date"
        const val ACTIVITY_COLUMN_TIME = "time"
        const val ACTIVITY_COLUMN_LANG_LEN = "lang_len"
        const val ACTIVITY_COLUMN_URL1 = "url1"
        const val ACTIVITY_COLUMN_URL2 = "url2"
        const val ACTIVITY_COLUMN_URL3 = "url3"
        const val ACTIVITY_COLUMN_TYPE = "type"
    }

    // Define the create statements for both tables
    private val CREATE_SETTINGS_TABLE = """
        CREATE TABLE IF NOT EXISTS $TABLE_SETTINGS (
            $SETTINGS_COLUMN_LANGUAGES TEXT,
            $SETTINGS_COLUMN_NOTIFICATION_TIME TEXT,
            $SETTINGS_COLUMN_NOTIFICATIONS BOOLEAN
        );
    """

    private val CREATE_ACTIVITY_TABLE = """
        CREATE TABLE IF NOT EXISTS $TABLE_ACTIVITY (
            $ACTIVITY_COLUMN_TITLE TEXT,
            $ACTIVITY_COLUMN_DESCRIPTION TEXT,
            $ACTIVITY_COLUMN_DATE TEXT,
            $ACTIVITY_COLUMN_TIME TEXT,
            $ACTIVITY_COLUMN_LANG_LEN TEXT,
            $ACTIVITY_COLUMN_URL1 TEXT,
            $ACTIVITY_COLUMN_URL2 TEXT,
            $ACTIVITY_COLUMN_URL3 TEXT,
            $ACTIVITY_COLUMN_TYPE TEXT
        );
    """

    override fun onCreate(db: SQLiteDatabase) {
        // Create both tables when the database is created
        db.execSQL(CREATE_SETTINGS_TABLE)
        db.execSQL(CREATE_ACTIVITY_TABLE)
    }

    fun deleteAllData() {
        val db = writableDatabase

        // Delete all data from the Settings table
        db.delete(TABLE_SETTINGS, null, null)

        // Delete all data from the Activity table
        db.delete(TABLE_ACTIVITY, null, null)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {


    }
}
