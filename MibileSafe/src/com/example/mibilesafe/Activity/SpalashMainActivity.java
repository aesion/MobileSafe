package com.example.mibilesafe.Activity;

import com.example.mibilesafe.R;
import com.example.mibilesafe.R.layout;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class SpalashMainActivity extends Activity {

	private TextView tv_Spalash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_Spalash = (TextView) findViewById(R.id.tv_Spalash);
        tv_Spalash.setText("°æ±¾£º"+getcode());
    }
    
    public String getcode(){
    	
    	PackageManager packageManager = getPackageManager();
    	try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			int code =packageInfo.versionCode;
			String versioncode =packageInfo.versionName;
			return versioncode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    	
    	
    }


}
