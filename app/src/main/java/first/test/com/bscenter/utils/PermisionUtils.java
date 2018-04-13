package first.test.com.bscenter.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by Admin on 2018/3/12.
 */


public class PermisionUtils {

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA};
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CAMERA = 2;

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     */
    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            showMessageDialog(REQUEST_EXTERNAL_STORAGE,activity);
            return false;
        }
        return true;
    }
    public static boolean verifyCameraPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            showMessageDialog(REQUEST_CAMERA,activity);
            return false;
        }
        return true;
    }
    private static void showMessageDialog(final int code, final Activity activity){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("权限");
        switch (code){
            case REQUEST_EXTERNAL_STORAGE:
                builder.setMessage("统计文件需要获取文件权限");
                break;
            case REQUEST_CAMERA:
                builder.setMessage("人脸识别需要相机权限");
                break;
        }
        builder.setPositiveButton("允许", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code == REQUEST_EXTERNAL_STORAGE){
                    ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE);
                }else if (code == REQUEST_CAMERA){
                    ActivityCompat.requestPermissions(activity, PERMISSIONS_CAMERA,
                            REQUEST_CAMERA);
                }

            }
        });
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code == REQUEST_EXTERNAL_STORAGE){
                    Toast.makeText(activity, "您的软件将不能升级,请在[设置]-[授权管理]中打开", Toast.LENGTH_SHORT).show();
                }else if (code == REQUEST_CAMERA){
                    Toast.makeText(activity, "您的软件将无法进行人脸识别,请在[设置]-[授权管理]中打开", Toast.LENGTH_SHORT).show();
                }

            }
        });
//        builder.setCancelable(false);
         AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}