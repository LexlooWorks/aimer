package com.nvapp.comic;

import java.io.File;

import com.nvapp.comic.cache.ACache;
import com.nvapp.comic.common.DoubleClickExitHelper;
import com.nvapp.comic.fragment.HomeFragment;
import com.nvapp.comic.lib.dialog.SweetAlertDialog;
import com.nvapp.comic.lib.drawer.ActionBarDrawerToggle;
import com.nvapp.comic.lib.drawer.DrawerArrowDrawable;
import com.nvapp.comic.lib.view.RoundedImageView;
import com.nvapp.comic.toast.Crouton;
import com.nvapp.comic.toast.Style;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	private DoubleClickExitHelper mDoubleClickExitHelper;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	RelativeLayout rl;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerArrowDrawable drawerArrow;
	public static FragmentManager fm;
	Boolean openOrClose = false;
	int vc;// 获取当前版本号
	ACache mCache;
	RoundedImageView iv_main_left_head;
	TextView user_name;
	RelativeLayout toprl;
	ImageView login_tv;
	LinearLayout animll_id;
	private boolean isLogin = false;
	protected static final int UPDATE_TEXT = 0;
	File sdcardDir;
	String path;
	File f;
	File[] fl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
		}

		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.actionbar_color);// 通知栏所需颜色

		mCache = ACache.get(this);
		mDoubleClickExitHelper = new DoubleClickExitHelper(this);
		toprl = (RelativeLayout) findViewById(R.id.toprl);
		animll_id = (LinearLayout) findViewById(R.id.animll_id);
		login_tv = (ImageView) findViewById(R.id.login_tv);
		user_name = (TextView) findViewById(R.id.user_name);
		iv_main_left_head = (RoundedImageView) findViewById(R.id.iv_main_left_head);
		createSDCardDir();

		vc = getVersionCode(this);
		chekedVersionCode();

		final ActionBar ab = getActionBar();

		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		init();
		fm = this.getSupportFragmentManager();
		rl = (RelativeLayout) findViewById(R.id.rl);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.navdrawer);

		drawerArrow = new DrawerArrowDrawable(this) {
			@Override
			public boolean isLayoutRtl() {
				return false;
			}
		};
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, drawerArrow, R.string.drawer_open,
				R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				invalidateOptionsMenu();
				openOrClose = false;
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu();
				openOrClose = true;
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();
		String[] values = new String[] { "每日一句", "精选美文", "精美卡片", "推荐应用" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_text, values);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					// ComponentName cn = new ComponentName("com.nvapp.comic",
					// "com.nvapp.mupdf.ChoosePDFActivity");
					// Intent intent = new Intent();
					// intent.setComponent(cn);
					// startActivity(intent);
					// initFragment(new EveryDayEnglishFragment());

					// setTitle("每日一句");
					// ab.setBackgroundDrawable(new ColorDrawable(Color.RED));
					break;
				case 1:
					// initFragment(new OtherFragment());
					setTitle("精选美文");
					break;
				case 2:
					initFragment(new HomeFragment());
					setTitle("精美卡片");

					break;
				case 3:
					// initFragment(new AppTuiFragment());
					setTitle("推荐应用");

					break;

				}
				mDrawerLayout.closeDrawers();
				openOrClose = false;
			}
		});
		toprl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (isLogin == true) {
					// Intent it = new Intent(getApplicationContext(),
					// SettingActivity.class);
					// startActivityForResult(it, 1);
					// overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				} else {
				}

			}
		});
		clearCache();
	}

	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	private void clearCache() {
		sdcardDir = Environment.getExternalStorageDirectory();
		path = sdcardDir.getPath() + "/comic";
		f = new File(path);
		fl = f.listFiles();

		if (fl == null) {
			return;
		}
		Log.e("fl.length==", fl.length + "");
		if (fl.length == 0) {

		} else {
			for (int i = 0; i < fl.length; i++) {
				if (fl[i].toString().endsWith(".mp3") || fl[i].toString().endsWith(".MP3")) {
					fl[i].delete();
				}
			}
		}
	}

	/**
	 * 显示ShortToast
	 */
	public void showCustomToast(String pMsg, int view_position) {
		Crouton.makeText(this, pMsg, Style.CONFIRM, view_position).show();
	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// 获取软件版本号
			versionCode = context.getPackageManager().getPackageInfo(getPackageName(), 1).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String avatar = mCache.getAsString("avatar");
		String name = mCache.getAsString("name");
		try {
			if (avatar.equals("")) {
				login_tv.setVisibility(View.VISIBLE);
				// login_tv.setText("登录");
			} else {
				isLogin = true;
				login_tv.setVisibility(View.GONE);
				// Ion.with(MainActivity.this).load(avatar).asBitmap().setCallback(new
				// FutureCallback<Bitmap>() {
				//
				// @Override
				// public void onCompleted(Exception e, Bitmap bitmap) {
				// // ivHead.setImageBitmap(bitmap);
				// iv_main_left_head.setImageBitmap(bitmap);
				// }
				// });

			}

			if (!name.equals("")) {
				isLogin = true;
				user_name.setText(name);
				// login_tv.setText("");
				login_tv.setVisibility(View.GONE);
			} else {
				// login_tv.setText("登录");
				login_tv.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(rl)) {
				mDrawerLayout.closeDrawer(rl);
				openOrClose = false;
			} else {
				mDrawerLayout.openDrawer(rl);
				openOrClose = true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private void init() {
		fm = getSupportFragmentManager();
		// initFragment(new EveryDayEnglishFragment());
	}

	// 切換Fragment
	public void changeFragment(Fragment f) {
		changeFragment(f, false);
	}

	public void initFragment(Fragment f) {
		changeFragment(f, true);
	}

	private void changeFragment(Fragment f, boolean init) {
		FragmentTransaction ft = fm.beginTransaction().setCustomAnimations(R.anim.comic_fb_slide_in_from_left,
				R.anim.comic_fb_slide_out_from_left, R.anim.comic_fb_slide_in_from_right,
				R.anim.comic_fb_slide_out_from_right);
		ft.replace(R.id.fragment_layout, f);
		if (!init)
			ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

			if (openOrClose == false) {
				showCustomToast(getString(R.string.back_exit_tips), R.id.fragment_layout);
				return mDoubleClickExitHelper.onKeyDown(keyCode, event);
			} else {
				mDrawerLayout.closeDrawers();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void createSDCardDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			// 创建一个文件夹对象，赋值为外部存储器的目录
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String path = sdcardDir.getPath() + "/comic";
			File path1 = new File(path);
			if (!path1.exists()) {
				// 若不存在，创建目录，可以在应用启动的时候创建
				path1.mkdirs();
				System.out.println("paht ok,path:" + path);
			}
		} else {
			System.out.println("false");
			return;
		}
	}

	public void chekedVersionCode() {
		// Ion.with(this, Conf.VERSION_CODE).asJsonObject().setCallback(new
		// FutureCallback<JsonObject>() {
		//
		// @Override
		// public void onCompleted(Exception e, JsonObject result) {
		// if (e != null) {
		// return;
		// }
		// String code = result.get("code").getAsString();
		// int jsonCode = Integer.parseInt(code);
		// // 比较开源中国返回的code跟当前版本code是否一致
		// if (jsonCode == vc) {
		// return;
		// } else if (jsonCode > vc) {
		//
		// CountDownTimer timer = new CountDownTimer(12 * 100, 100) {
		//
		// @Override
		// public void onTick(long millisUntilFinished) {
		// long a = millisUntilFinished / 100;
		//
		// if (a == 1) {
		// new SweetAlertDialog(MainActivity.this,
		// SweetAlertDialog.WARNING_TYPE)
		// .setTitleText("版本检测").setContentText("发现新版本，是否更新？").setCancelText("暂不更新")
		// .setConfirmText("马上更新").showCancelButton(true)
		// .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
		// @Override
		// public void onClick(SweetAlertDialog sDialog) {
		// sDialog.dismiss();
		// }
		// }).setConfirmClickListener(new
		// SweetAlertDialog.OnSweetClickListener() {
		// @Override
		// public void onClick(SweetAlertDialog sDialog) {
		// Intent updateIntent = new Intent(MainActivity.this,
		// AppUpdateService.class);
		// updateIntent.putExtra("titleId", R.string.app_name);
		// startService(updateIntent);
		// sDialog.dismiss();
		//
		// }
		// }).show();
		// } else {
		//
		// }
		// }
		//
		// @Override
		// public void onFinish() {
		//
		// }
		// };
		//
		// timer.start();
		// }
		//
		// }
		// });
	}

	public PendingIntent getDefalutIntent(int flags) {
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
		return pendingIntent;
	}
}