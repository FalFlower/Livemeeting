package cn.edu.sdnu.i.livemeeting.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
;

import cn.edu.sdnu.i.livemeeting.R;

public class EditProfileActivity extends AppCompatActivity {

    public static EditProfileActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance=this;
        setContentView(R.layout.activity_edit_profile);
        Button coomBtn=findViewById(R.id.complete);
        coomBtn.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
