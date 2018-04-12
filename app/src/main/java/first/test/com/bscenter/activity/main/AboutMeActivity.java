package first.test.com.bscenter.activity.main;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import first.test.com.bscenter.R;

public class AboutMeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_me);

    }

    public void back(View view) {
        finish();
    }
}
