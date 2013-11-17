package okosama.app.state;

import java.util.HashMap;
import java.util.Map.Entry;

import okosama.app.MusicSettingsActivity;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.tab.Tab;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.media.audiofx.AudioEffect;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public abstract class absDisplayState implements IDisplayState {
	
	public static final int MENU_SETTING = 335;
	public static final int MENU_EFFECTOR = 334;
	public static final int MENU_UPDATE = 333;
	
	// TODO: 子クラスからのアクセサ
	double prevAzimuth = 0;
	double prevPitch = 0;
	double prevRoll = 0;
	double azimuth = 0;
	double pitch = 0;
	double roll = 0;
	
	protected HashMap< String, BroadcastReceiver > receivers = new HashMap< String, BroadcastReceiver >();
	protected HashMap< String, Handler > handlers = new HashMap< String, Handler >();
//	protected OkosamaMediaPlayerActivity act;
//	void setActivity( OkosamaMediaPlayerActivity _act )
//	{
//		act = _act;
//	}

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int registerReceivers(int status) {
		// TODO Auto-generated method stub
		return 1;
	}

	public void clearReceivers()
	{
		// 全てのBroadcastReceiverを登録解除する
		for( Entry<String, BroadcastReceiver > e : receivers.entrySet() )
		{
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().unregisterReceiver(e.getValue());
		}
		receivers.clear();
		// 全てのハンドラのキューのメッセージをクリアする？
		// いいのかな・・・？
		for( Entry<String, Handler > e : handlers.entrySet() )
		{
			e.getValue().removeCallbacksAndMessages(null);
		}
		handlers.clear();
	}
	
	@Override
	public void unregisterReceivers(int status) {
		if( OkosamaMediaPlayerActivity.getResourceAccessor().getActivity() == null )
		{
			Log.e("unregisterReceivers", "activity not set");
			return;
		}
		clearReceivers();
	}
	
	@Override
	public int onCreateOptionsMenu(Menu menu)
	{
		return MENU_OK;
	}
	@Override
	public int onPrepareOptionsMenu(Menu menu)
	{
		menu.clear();	// TODO: 微妙
		MenuItem item = null;
		item = menu.add(Menu.NONE, MENU_UPDATE, Menu.NONE, R.string.update_menu);
		// TODO: アイコンを更新に ic_menu_refreshが本当はあるはず？
		item.setIcon(android.R.drawable.ic_popup_sync );
		item = menu.add(Menu.NONE, MENU_SETTING, Menu.NONE, R.string.setting_menu);
		item.setIcon(android.R.drawable.ic_menu_preferences );
        Intent i = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
        if (OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getPackageManager().resolveActivity(i, 0) != null) {
            item = menu.add(Menu.NONE, MENU_EFFECTOR, Menu.NONE, R.string.effect_menu);
    		item.setIcon(android.R.drawable.ic_lock_silent_mode_off );
    	}		
		
		return MENU_OK;		
	}
	@Override
	public int onOptionsItemSelected(MenuItem menu)
	{
		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		switch (menu.getItemId())
		{
		case MENU_SETTING:
            Intent intent = new Intent();
            intent.setClass(act, MusicSettingsActivity.class);
            // startActivityForResult(intent, SETTINGS);
            act.startActivity(intent);
			break;
		case MENU_EFFECTOR:
            Intent i = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
            try {
				i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, 
						MediaPlayerUtil.sService.getAudioSessionId());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(act,
						"Effector get Error!", Toast.LENGTH_LONG).show();
			}
            act.startActivity(i);			
			break;
		}
		return MENU_OK;
	}
	
	@Override
	public int ChangeMotion()
	{
		// TODO:このクラスでは不要かもしれない
//		OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver.
//		azimuth = OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver.getAzimuth();
//		pitch = OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver.getPitch();
//		roll = OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver.getRoll();
		return 0;
	}
	@Override
	public int updateStatus() {
		// OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getAlbumAdp().updateStatus();
		return 0;
	}

}
