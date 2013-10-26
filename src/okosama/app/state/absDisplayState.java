package okosama.app.state;

import java.util.HashMap;
import java.util.Map.Entry;


import android.content.BroadcastReceiver;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.Tab;

public abstract class absDisplayState implements IDisplayState {
	
	public static final int MENU_UPDATE = 333;
	
	// TODO: �q�N���X����̃A�N�Z�T
	double prevAzimuth = 0;
	double prevPitch = 0;
	double prevRoll = 0;
	double azimuth = 0;
	double pitch = 0;
	double roll = 0;
	
	protected HashMap< String, BroadcastReceiver > receivers;
	protected HashMap< String, Handler > handlers;
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
		// �S�Ă�BroadcastReceiver��o�^��������
		for( Entry<String, BroadcastReceiver > e : receivers.entrySet() )
		{
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().unregisterReceiver(e.getValue());
		}
		receivers.clear();
		// �S�Ẵn���h���̃L���[�̃��b�Z�[�W���N���A����H
		// �����̂��ȁE�E�E�H
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
		return MENU_OK;		
	}
	@Override
	public int onOptionsItemSelected(MenuItem menu)
	{
		return MENU_OK;
	}
	
	@Override
	public int ChangeMotion()
	{
		// TODO:���̃N���X�ł͕s�v��������Ȃ�
//		OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver.
//		azimuth = OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver.getAzimuth();
//		pitch = OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver.getPitch();
//		roll = OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver.getRoll();
		return 0;
	}

}
