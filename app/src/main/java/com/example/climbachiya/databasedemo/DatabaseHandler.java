package com.example.climbachiya.databasedemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by C.limbachiya on 6/3/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    // Contacts table name
    public static final String TABLE_CONTACTS = "contacts";

    private static DatabaseHandler mInstance;
    private static SQLiteDatabase sqLiteDatabase;

    // Contacts Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_INTEREST = "interest";
    public static final String KEY_COURSE = "course";

    public static DatabaseHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHandler(context);
        }
        return mInstance;
    }

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " VARCHAR(50),"
                + KEY_EMAIL + " VARCHAR(30), "
                + KEY_GENDER + " VARCHAR(6), "
                + KEY_INTEREST + " VARCHAR(30), "
                + KEY_COURSE + " VARCHAR(50) "
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        // Create tables again
        onCreate(db);
    }

    //Insert into tables
    public long insertQuery(ContentValues values, String TABLE) {

        long returnVal = 0;

        try {
            sqLiteDatabase = getDatabaseMode("write");
            // Inserting Row
            returnVal = sqLiteDatabase.insert(TABLE, null, values);
        } catch (Exception e) {
            returnVal = 0;
            e.printStackTrace();
        }
        return returnVal;
    }

    //Update into tables
    public int updateQuery(String TABLE, ContentValues values, String whereClause, String[] whereArgs) {

        int returnVal = 0;

        try {
            sqLiteDatabase = getDatabaseMode("write");
            // Inserting Row
            returnVal = sqLiteDatabase.update(TABLE, values, whereClause, whereArgs);

        } catch (Exception e) {
            returnVal = 0;
            e.printStackTrace();
        }
        return returnVal;
    }

    //Get data by specific records using where clause in query
    public Cursor getDataByCustomQuery(String QUERY, String[] args) {

        Cursor cursor = null;
        try {

            sqLiteDatabase = getDatabaseMode("read");
            cursor = sqLiteDatabase.rawQuery(QUERY, args);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }

    //Delete all data of table
    public void truncateTable(String TABLE) {
        try {
            sqLiteDatabase = getDatabaseMode("write");
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE); //delete all rows in a table
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Delete record
    public int deleteRecord(String table, String whereClause, String[] whereArgs) {

        int returnVal = 0;
        try {
            sqLiteDatabase = getDatabaseMode("write");
            returnVal = sqLiteDatabase.delete(table, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            returnVal = 0;
        }

        return returnVal;
    }

    /**
     * Returns a writable database instance in order not to open and close many
     * SQLiteDatabase objects simultaneously
     *
     * @return a writable instance to SQLiteDatabase
     */
    public SQLiteDatabase getDatabaseMode(String mode) {
        if ((sqLiteDatabase == null) || (!sqLiteDatabase.isOpen())) {
            if (mode.equalsIgnoreCase("read"))
                sqLiteDatabase = this.getReadableDatabase();
            else
                sqLiteDatabase = this.getWritableDatabase();
        }

        return sqLiteDatabase;
    }

    @Override
    public void close() {
        super.close();
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
            sqLiteDatabase = null;
        }
    }
}
