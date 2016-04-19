package br.com.metragemrio.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.metragemrio.model.Meterage;

public class MeterageDataSource {

    // Database fields
    private SQLiteDatabase database;
    private Database dbHelper;
    private String[] allColumns = {Meterage.TIMESTAMP, Meterage.LEVEL, Meterage.STATUS};

    public MeterageDataSource() {
        dbHelper = Database.getInstance();
    }

    public void open() throws SQLException {
        database = dbHelper.getMyWritableDatabase();
    }

    public void close() {
//        dbHelper.close();
    }

    public long create(Meterage meterage) {
        try {
            open();
            ContentValues values = new ContentValues();
            values.put(Meterage.TIMESTAMP, meterage.getTimestamp());
            values.put(Meterage.STATUS, meterage.getStatus());
            values.put(Meterage.LEVEL, meterage.getLevel());
            long id = database.insertWithOnConflict(Meterage.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//            close();
            return id;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Meterage> getAll() {
        List<Meterage> meterages = new ArrayList<>();
        try {
            open();

            String query = "SELECT * FROM " + Meterage.TABLE_NAME + " ORDER BY " + Meterage.TIMESTAMP + " DESC LIMIT 300";
            Cursor cursor = database.rawQuery(query, null);
//            Cursor cursor = database.query(Meterage.TABLE_NAME, allColumns, null, null, null, null, Meterage.TIMESTAMP + " DESC ");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Meterage meterage = cursorToObject(cursor);
                meterages.add(meterage);
                cursor.moveToNext();
            }
            // make sure to close the cursor
//            cursor.close();
//            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meterages;
    }

    public List<Meterage> get30() {
        List<Meterage> meterages = new ArrayList<>();
        try {
            open();
            String query = "SELECT * FROM " + Meterage.TABLE_NAME + " ORDER BY " + Meterage.TIMESTAMP + " DESC LIMIT 30";
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                Meterage meterage = cursorToObject(cursor);
                meterages.add(meterage);
                cursor.moveToNext();
            }
            // make sure to close the cursor
//            cursor.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meterages;
    }

    public Float[] getMaxMin30() {
        try {
            open();
            String query = "SELECT max("+Meterage.LEVEL+"), min("+Meterage.LEVEL+") FROM " + Meterage.TABLE_NAME + " LIMIT 30";
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            DatabaseUtils.dumpCursor(cursor);
            float maxFloat = cursor.getFloat(cursor.getColumnIndex("max(" + Meterage.LEVEL + ")"));
            float minFloat = cursor.getFloat(cursor.getColumnIndex("min(" + Meterage.LEVEL + ")"));
            // make sure to close the cursor
//            cursor.close();
            return new Float[]{maxFloat, minFloat};
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Meterage cursorToObject(Cursor cursor) {
        Meterage meterage = new Meterage();
        meterage.setTimestamp(cursor.getLong(cursor.getColumnIndex(Meterage.TIMESTAMP)));
        meterage.setLevel(cursor.getFloat(cursor.getColumnIndex(Meterage.LEVEL)));
        meterage.setStatus(cursor.getString(cursor.getColumnIndex(Meterage.STATUS)));
        return meterage;
    }

}

