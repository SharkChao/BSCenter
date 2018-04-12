package first.test.com.bscenter.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import first.test.com.bscenter.R;
import first.test.com.bscenter.utils.SharedPreferenceUtil;


public class StartUI extends Activity {
    boolean mIsFirstTimeUse = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.start);
        mIsFirstTimeUse = SharedPreferenceUtil.getFirstTimeUse();
        
        if (mIsFirstTimeUse) {
            Intent intent = new Intent(StartUI.this, WelcomActivity.class);
            startActivity(intent);
            finish();
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent mainIntent = new Intent(StartUI.this, MainActivity.class);
                    startActivity(mainIntent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                    finish();
                }
            }, 1200);
        }

    }
}
