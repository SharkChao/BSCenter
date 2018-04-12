package first.test.com.bscenter.greendao;

/**
 * Created by Administrator on 2017/3/22.
 */

import android.content.Context;


/**
 *  greenDao管理类
 */
public class GreenDaoManager {
    private static GreenDaoManager mInstance;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private GreenDaoManager(Context context) {
        DBHelper devOpenHelper =new DBHelper(context,null);
        devOpenHelper.onUpgrade(devOpenHelper.getWritableDatabase(),1,1);
        mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public static GreenDaoManager getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new GreenDaoManager(context);
        }
        return mInstance;
    }
}