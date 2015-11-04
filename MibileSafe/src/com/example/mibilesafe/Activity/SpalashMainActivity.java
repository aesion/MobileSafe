package com.example.mibilesafe.Activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.mibilesafe.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;


public class SpalashMainActivity extends Activity {
	
	protected static final int CODE_UPDATE = 0;
	protected static final int no_UPDATE = 1;
	Map<String, Object> codeMap;
	private TextView tv_Spalash;
	private String description;
	private String downloadUrl;
	
	 Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case CODE_UPDATE:
				choseUpdate();
				break;
			case no_UPDATE:
				startHomeActivity();
				break;

			default:
				break;
			}
			
			
			
		}
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spalash_layout);
        tv_Spalash = (TextView) findViewById(R.id.tv_Spalash);
        getcode();
        tv_Spalash.setText("版本："+codeMap.get("NversionName"));
        checkVerson();
        
    }
    
    private void startHomeActivity() {
    	
    	Intent intent = new Intent(this,HomeActivity.class);
    	startActivity(intent);
    	
	}

	private void checkVerson() {
		// TODO Auto-generated method stub
		final long startTime  = System.currentTimeMillis();
    	new Thread(new Runnable() {
			
			
    		
			

			@Override
			public void run() {
				Message msg=handler.obtainMessage();
				HttpURLConnection openConnection = null;
				try {
					URL url = new URL("http://192.168.1.101:8080/josn/update.json");
					openConnection = (HttpURLConnection) url.openConnection();
					openConnection.setRequestMethod("GET");
					openConnection.setConnectTimeout(10000);
					openConnection.setReadTimeout(10000);
					openConnection.connect();
					int responseCode = openConnection.getResponseCode();
					if (responseCode == 200) {
						InputStream inputStream = openConnection.getInputStream();
						String updateInfo = getUpdateInfo(inputStream);
						System.out.println(updateInfo);
						
						JSONObject jsonObject = new JSONObject(updateInfo);
						description = jsonObject.getString("description");
						int versionCode = jsonObject.getInt("versionCode");
						downloadUrl = jsonObject.getString("downloadUrl");
						System.out.println(downloadUrl);
						System.out.println(description);
						int Nversioncode = (Integer) codeMap.get("Nversioncode");
						System.out.println(versionCode+"......"+Nversioncode);
						if (versionCode > Nversioncode) {
							msg.what = CODE_UPDATE;
							
							
							
						}
						else {
							
							msg.what = no_UPDATE;
							
						}
						
						
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// TODO Auto-generated method stub
				catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally{
					
					long EndTime  = System.currentTimeMillis();
					long useTime = EndTime - startTime;
					System.out.println(useTime);
					if (useTime < 1000) {
						try {
							Thread.sleep(1000 - useTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						handler.sendMessage(msg);
					}
						
					if (openConnection!=null) {
						
						openConnection.disconnect();
						
					}
				}
				
			}

			
		}).start();
    	
		
	}
	public void choseUpdate() {
		final ProgressDialog progress = new ProgressDialog(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("更新提示！");
		builder.setMessage(description);
		builder.setNegativeButton("立即更新", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			
					
					String path = Environment.getExternalStorageDirectory()+"/upDate.apk";
					HttpUtils http = new HttpUtils();
					HttpHandler handler = http.download(downloadUrl, path,true,true,new RequestCallBack<File>() {
						
						private Intent intent;
						@Override
						public void onSuccess(ResponseInfo<File> arg0) {
							// TODO Auto-generated method stub
							System.out.println("下载成功");
							intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.fromFile(arg0.result), "application/vnd.android.package-archive");
							startActivityForResult(intent, 0);
						}
						
						
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							System.out.println("下载失败");
							
						}
						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
							// TODO Auto-generated method stub
							progress.setMessage("Downloading...");
							progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
							progress.setIndeterminate(false);
							
							progress.setMax((int) total);
							progress.setProgress((int) current);
//							progress.setButton("安装", new Bt1DialogListener());
							progress.show();
							System.out.println(total+"..."+current);
							super.onLoading(total, current, isUploading);
						}
					});
					
					
				} 
			
//		    class Bt1DialogListener implements OnClickListener {     
//		        @Override    
//		        public void onClick(DialogInterface dialog, int which) {     
//		            // 点击“确定”按钮取消对话框     
//		            dialog.cancel();     
//		        }     
//		    } 
				
				
			
		});
		builder.setPositiveButton("取消更新", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				startHomeActivity();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				startHomeActivity();
			}
		});
		
		builder.show();
		
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		startHomeActivity();
		super.onActivityResult(requestCode, resultCode, data);
	}
	private String getUpdateInfo(InputStream in) throws IOException {
		
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len;
		while ((len =in.read(buf)) != -1) {
			
			arrayOutputStream.write(buf, 0, len);
			
		}
		in.close();
		arrayOutputStream.close();
		return arrayOutputStream.toString();
		
		
		
	};

	public void getcode(){
    	
    	PackageManager packageManager = getPackageManager();
    	try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			int code =packageInfo.versionCode;
			String versionName =packageInfo.versionName;
			int versioncode = packageInfo.versionCode;
			codeMap = new HashMap<String, Object>();
			codeMap.put("NversionName", versionName);
			codeMap.put("Nversioncode", versioncode);
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	
    	
    }
    
    


}
