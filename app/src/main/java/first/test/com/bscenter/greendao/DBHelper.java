package first.test.com.bscenter.greendao;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Administrator on 2017/3/20.
 */

public class DBHelper extends DaoMaster.OpenHelper {
    public static final String DBNAME = "winning_HIAP.db";

    public DBHelper(Context context, String dbName) {
        super(context, DBNAME, null);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
//        MigrationHelper dbhelper = new MigrationHelper();
//        dbhelper.migrate(db, UserDao.class);
    }
}