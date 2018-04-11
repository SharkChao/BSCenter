package first.test.com.bscenter.utils;


import first.test.com.bscenter.base.MyLeanCloudApp;

/**
 */
public class StringFetcher {
    public static String getString(int id) {
        return MyLeanCloudApp.getInstance().getString(id);
    }
}
