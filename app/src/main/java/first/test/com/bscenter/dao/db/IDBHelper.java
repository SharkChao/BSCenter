package first.test.com.bscenter.dao.db;

import android.database.sqlite.SQLiteDatabase;

public interface IDBHelper {
    
    public SQLiteDatabase openDatabase();
    public void closeDatabase();

}
