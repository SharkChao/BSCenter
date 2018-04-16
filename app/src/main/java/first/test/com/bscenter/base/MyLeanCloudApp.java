package first.test.com.bscenter.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.iflytek.cloud.SpeechUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import first.test.com.bscenter.core.common.FileType;
import first.test.com.bscenter.core.engine.ResourceManager;
import first.test.com.bscenter.dao.DaoFactory;
import first.test.com.bscenter.dao.db.APPNameDbHelper;
import first.test.com.bscenter.dao.db.GDEDbHelper;
import first.test.com.bscenter.dao.impl.FavoriteDao;
import first.test.com.bscenter.dao.impl.FileAppNameDao;
import first.test.com.bscenter.dao.impl.FileTypeDao;
import first.test.com.bscenter.model.Favorite;
import first.test.com.bscenter.presenter.MainPresenter;
import first.test.com.bscenter.utils.PermisionUtils;
import first.test.com.bscenter.utils.PermissionsManager;

//import android.support.multidex.MultiDex;

/**
 */


public class MyLeanCloudApp extends Application{
    private static MyLeanCloudApp mLeanCloudApp;
    MainPresenter mMainPresenter;

    private static final String TAG = "DeploymentOperation";
    private static final String DATABASE_NAME = "appdirname.db";
    public static Map<String, int[]> mExtensionTypeMap = null;
    public static Map<String, String> mAppNameMap = null;
    private static MyLeanCloudApp mAppSeft = null;
    public static String PACKAGE_NAME = "";
    @Override
    public void onCreate() {
        super.onCreate();
        mLeanCloudApp = this;
        mMainPresenter = MainPresenter.getInstance();
//        Bmob.initialize(this, "9429c5e5ab59f91a8fe65c5205d1a5e1");
        mAppSeft = this;
        PACKAGE_NAME = getPackageName();

//        deployeDataBase(getApplicationContext(), false);
        SpeechUtility.createUtility(this, "appid=" + "5ad40bbd");
        initResource();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public static MyLeanCloudApp getInstance(){
        return mLeanCloudApp;
    }
    public MainPresenter getMainPresenter(){
        return mMainPresenter;
    }


    public static void initialFavoriteDatabase() {
        PermisionUtils.verifyStoragePermissions(BaseActivity.currentActivity);
        File file = new File("/");
        int size = file.listFiles().length;
        Favorite favoriteRoot = new Favorite("/", "Root目录", "根目录", FileType.TYPE_FOLDER, System.currentTimeMillis(), size, "安装时加入");
        FavoriteDao dao = DaoFactory.getFavoriteDao(getAppContext());
        dao.insertFavorite(favoriteRoot);

        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            file = Environment.getExternalStorageDirectory();
            size = file.list().length;
            try {
                Favorite favoriteStorage = new Favorite(file.getCanonicalPath(), "存储卡", "存储卡", FileType.TYPE_FOLDER, System.currentTimeMillis(),
                        size, "外部存储卡");
                dao.insertFavorite(favoriteStorage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Context getAppContext() {
        if (mAppSeft != null) {
            return mAppSeft.getApplicationContext();
        }
        return null;
    }

    private void initResource() {
        loadExtesionTypeMap();
        loadAppNameMap();
        loadResourceManager(getApplicationContext());
    }

    // 加载文件路径对应的应用名
    private void loadAppNameMap() {
        if (mAppNameMap != null) {
            mAppNameMap.clear();
        }
        FileAppNameDao dao = DaoFactory.getFileAppNameDao(getApplicationContext());
        mAppNameMap = dao.findAllAppName();
    }

    private void loadResourceManager(Context applicationContext) {

    }

    /**
     * 加载扩展名对应文件数据类型
     */
    private void loadExtesionTypeMap() {
        if (mExtensionTypeMap != null) {
            mExtensionTypeMap.clear();
        }
        FileTypeDao dao = DaoFactory.getFileTypeDao(getApplicationContext());
        mExtensionTypeMap = dao.getAllExtensionFileTypeMap();
        // showMap(mExtensionTypeMap);
    }

    private void showMap(Map<String, int[]> map) {
        for (String key : map.keySet()) {
            System.out.println("扩展名：" + key + " 类型： " + map.get(key)[0] + ", 类别：" + map.get(key)[1]);
        }
    }

    /**
     * 强制要求重新加载
     *
     * @param context
     * @param isForced
     */
    private void deployeDataBase(Context context, boolean isForced) {

        File dir = new File(ResourceManager.DATABASES_DIR);
        if (!dir.exists() || isForced) {
            try {
                dir.mkdir();
            } catch (Exception e) {
                Log.e(TAG, "--->创建数据库目录失败");
                e.printStackTrace();
            }
        }

        File dest = new File(dir, DATABASE_NAME);
        if (dest.exists() && !isForced) {
            return;
        }

        FileOutputStream out = null;
        InputStream in = null;

        try {
            if (dest.exists()) {
                dest.delete();
            }

            dest.createNewFile();
            in = context.getAssets().open(DATABASE_NAME, Context.MODE_PRIVATE);
            int size = in.available();
            byte buf[] = new byte[size];
            in.read(buf);
            out = new FileOutputStream(dest);
            out.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "--->拷贝数据库成功！");

        initialFavoriteDatabase();
    }

    public void testFindByPath() {

        FileAppNameDao dao = DaoFactory.getFileAppNameDao(getApplicationContext());
        System.out.println("--->appName:" + dao.findAppName("/baidu/AppSearch"));
        //
        // FavoriteDao dao = DaoFactory.getFavoriteDao(getApplicationContext());
        // Favorite favorite = dao.findFavoriteByFullPath("/sdcard/360/a.txt");
        // System.out.println("--->" + favorite);
        //
        // dao.insertFavorite(favorite);
        //
        // List<Favorite> favorites = dao.findAllFavorite();
        // for (Favorite favorite2 : favorites) {
        // System.out.println("--->" + favorite2);
        // }
        //
        // dao.insertFavorites(favorites);
        //
        // favorites = dao.findAllFavorite();
        //
        // System.out.println("===>" + favorites.size());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        APPNameDbHelper.getInstance().closeDatabase();
        GDEDbHelper.getInstance().closeDatabase();

    }

}
