package br.com.metragemrio.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.com.metragemrio.AppApplication;
import br.com.metragemrio.model.Dam;
import br.com.metragemrio.model.Meterage;

public class Database extends SQLiteOpenHelper {

    private static Database sInstance;

    private static final String DATABASE_NAME = "metragemrio.db";
    private static final int DATABASE_VERSION = 1;

    private static SQLiteDatabase myWritableDb;

    public static synchronized Database getInstance() {
        if (sInstance == null) {
            sInstance = new Database();
        }
        return sInstance;
    }

    public Database() {
        super(AppApplication.getContext().getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Returns a writable database instance in order not to open and close many
     * SQLiteDatabase objects simultaneously
     *
     * @return a writable instance to SQLiteDatabase
     */
    public SQLiteDatabase getMyWritableDatabase() {
        if ((myWritableDb == null) || (!myWritableDb.isOpen())) {
            myWritableDb = this.getWritableDatabase();
        }

        return myWritableDb;
    }

    @Override
    public void close() {
        super.close();
        if (myWritableDb != null) {
            myWritableDb.close();
            myWritableDb = null;
        }
    }

    private static final String METERAGE_TABLE_CREATE = "create table if not exists "
            + Meterage.TABLE_NAME + "(" //
            + Meterage.TIMESTAMP + " integer primary key, " //
            + Meterage.LEVEL + " text not null, " //
            + Meterage.STATUS + " text not null" //
            + ");";

    private static final String DAM_TABLE_CREATE = "create table if not exists "
            + Dam.TABLE_NAME + "(" //
            + Dam.CAPACITY + " integer not null, " //
            + Dam.CLOSED + " integer not null, " //
            + Dam.OPEN + " integer not null, " //
            + Dam.TOTAL + " text not null, " //
            + Dam.NAME + " text not null, " //
            + Dam.METERAGE_ID + " integer not null, " //
            + " PRIMARY KEY (" + Dam.METERAGE_ID + ", " + Dam.NAME + "))";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(METERAGE_TABLE_CREATE);
        sqLiteDatabase.execSQL(DAM_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
