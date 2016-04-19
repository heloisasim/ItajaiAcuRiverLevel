package br.com.metragemrio.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import br.com.metragemrio.model.Dam;
import br.com.metragemrio.model.Meterage;

public class DamDataSource {

    // Database fields
    private SQLiteDatabase database;
    private Database dbHelper;
    private String[] allColumns = {Dam.METERAGE_ID, Dam.OPEN, Dam.CLOSED, Dam.TOTAL, Dam.CAPACITY, Dam.NAME};

    public DamDataSource() {
        dbHelper = Database.getInstance();
    }

    public void open() throws SQLException {
        database = dbHelper.getMyWritableDatabase();
    }

    public void close() {
//        dbHelper.close();
    }

    public long create(Dam dam, String name, long timestamp) {
        try {
            open();
            ContentValues values = new ContentValues();
            values.put(Dam.CAPACITY, dam.getCapacity());
            values.put(Dam.CLOSED, dam.getClosed());
            values.put(Dam.OPEN, dam.getOpen());
            values.put(Dam.TOTAL, dam.getTotal());
            values.put(Dam.METERAGE_ID, timestamp);
            values.put(Dam.NAME, name);
            long id = database.insertWithOnConflict(Dam.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//            close();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Dam get(long meterage_id, String name) {
        try {
            open();
            Cursor cursor = database.query(Dam.TABLE_NAME, allColumns, Dam.METERAGE_ID + "=? AND "+Dam.NAME+" =?", new String[]{String.valueOf(meterage_id), name}, null, null, null);
            if (cursor.moveToFirst()) {
                Dam dam = cursorToObject(cursor);
//                cursor.close();
                return dam;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Dam cursorToObject(Cursor cursor) {
        Dam dam = new Dam();
        dam.setCapacity(cursor.getString(cursor.getColumnIndex(Dam.CAPACITY)));
        dam.setClosed(cursor.getInt(cursor.getColumnIndex(Dam.CLOSED)));
        dam.setOpen(cursor.getInt(cursor.getColumnIndex(Dam.OPEN)));
        dam.setMeterage_id(cursor.getLong(cursor.getColumnIndex(Dam.METERAGE_ID)));
        dam.setName(cursor.getString(cursor.getColumnIndex(Dam.NAME)));
        dam.setTotal(cursor.getFloat(cursor.getColumnIndex(Dam.TOTAL)));
        return dam;
    }

}

