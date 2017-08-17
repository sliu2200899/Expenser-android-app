package cmu.edu.expenser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dandanshi on 29/07/2017.
 */

public class ItemDAO {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {
            SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_USERID,
            SQLiteHelper.COLUMN_TOTAL,
            SQLiteHelper.COLUMN_DATE,
            SQLiteHelper.COLUMN_CATEGORY,
            SQLiteHelper.COLUMN_PEOPLE,
            SQLiteHelper.COLUMN_AVERAGE};

    public ItemDAO(Context context) {
        dbHelper = new SQLiteHelper (context,
                SQLiteHelper.DATABASE_NAME, null, SQLiteHelper.DATABASE_VERSION);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertItem(String userId, double total, String date, String category, int people,
                           String photoUri) {
        ContentValues newItem = new ContentValues();
        newItem.put(SQLiteHelper.COLUMN_USERID, userId);
        newItem.put(SQLiteHelper.COLUMN_TOTAL, total);
        newItem.put(SQLiteHelper.COLUMN_DATE, date);
        newItem.put(SQLiteHelper.COLUMN_CATEGORY, category);
        newItem.put(SQLiteHelper.COLUMN_PEOPLE, people);
        newItem.put(SQLiteHelper.COLUMN_PHOTOURI, photoUri);

        double average = total / people;
        newItem.put(SQLiteHelper.COLUMN_AVERAGE, average);

        open();
        database.insert(SQLiteHelper.TABLE_NAME, null, newItem);
        close();
    }

    public void updateItem(long id, double total, String date, String category,
                           int people, String photoUri)  {
        ContentValues editItem = new ContentValues();
        editItem.put(SQLiteHelper.COLUMN_TOTAL, total);
        editItem.put(SQLiteHelper.COLUMN_DATE, date);
        editItem.put(SQLiteHelper.COLUMN_CATEGORY, category);
        editItem.put(SQLiteHelper.COLUMN_PEOPLE, people);
        editItem.put(SQLiteHelper.COLUMN_PHOTOURI, photoUri);

        double average = total / people;
        editItem.put(SQLiteHelper.COLUMN_AVERAGE, average);

        open();
        database.update(SQLiteHelper.TABLE_NAME, editItem,
                "_id=" + id, null); close();
    }

    public void deleteItem(String userId, String total, String date, String category,
                           String people, String photoUri) {
        open();
        String whereClause = "userId = ? and total = ? and date = ? and category = ? "
                + "and people = ? and photoUri = ?";
        String[] whereArgs = new String[] {userId, total, date, category,
                people, photoUri};
        database.delete(SQLiteHelper.TABLE_NAME, whereClause, whereArgs);
        close();
    }

    public Cursor getAllItems(String userId) {
        database = dbHelper.getReadableDatabase();
        String[] tableColumns = new String[] {"_id", "userID", "total",
                "date", "category", "people", "average", "photoUri"};
        String whereClause = "userID = ?";
        String[] whereArgs = new String[] {userId};
        String orderBy = "date";

        Cursor cursor = database.query(
                SQLiteHelper.TABLE_NAME, tableColumns, whereClause, whereArgs, null, null, orderBy);
        return cursor;
    }

    public Cursor getMonthItems(String userId, String month) {
        database = dbHelper.getReadableDatabase();
        String[] tableColumns = new String[] { "userID", "category", "SUM(average)"};
        String whereClause = "userID = ? AND date LIKE ?";
        String[] whereArgs = new String[] {userId, month + "%"};
        String groupBy = "category";
        // String having = "";

        Cursor cursor = database.query(
                SQLiteHelper.TABLE_NAME, tableColumns, whereClause, whereArgs, groupBy, null, null);
        return cursor;
    }

    public List<Item> getAllItemsLists(String userId) {
        List<Item> events = new ArrayList<Item>();
        String[] tableColumns = new String[] {"_id", "userID", "total",
                "date", "category", "people", "average", "photoUri"};
        String whereClause = "userID = ?";
        String[] whereArgs = new String[] {userId};
        String orderBy = "date";

        Cursor cursor = database.query(
                SQLiteHelper.TABLE_NAME, tableColumns, whereClause, whereArgs, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = cursorToItem(cursor);
            events.add(item);
            cursor.moveToNext();
        } // make sure to close the cursor cursor.close();
        return events;
    }

    public Item cursorToItem(Cursor cursor) {
        String userId = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_USERID));
        int total = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_TOTAL));

        String date = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_DATE));
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date transactionDate = new Date();
        try {
            transactionDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String category = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_CATEGORY));
        int people = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_PEOPLE));
        String photoUri = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_PHOTOURI));
        Item item = new Item(userId, total, transactionDate, category, people, photoUri);
        return item;
    }

//    public Cursor getOneEvent(long id) {
//        database = dbHelper.getReadableDatabase();
//        return database.query(SQLiteHelper.TABLE_NAME, null, "_id=" + id,
//                null, null, null, null);
//    }
//
//    public void deleteEvent(long id) {
//        open();
//        database.delete(SQLiteHelper.TABLE_NAME, "_id=" + id, null);
//        close();
//    }
}
