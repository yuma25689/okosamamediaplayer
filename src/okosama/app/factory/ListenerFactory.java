package okosama.app.factory;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.state.IDisplayState;
import okosama.app.state.absDisplayState;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class ListenerFactory {
	/////////////////////// ƒŠƒXƒi
	public static BroadcastReceiver createMediaChangeListener(IDisplayState state) 
	{
		final IDisplayState state2 = state;
		return new BroadcastReceiver() {
			IDisplayState mState = state2;
			@Override
	        public void onReceive(Context context, Intent intent) {
	            //getListView().invalidateViews();
	            //MusicUtils.updateNowPlaying(AlbumBrowserActivity.this);
	        	//OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(
	        	//		ControlIDs.ID_NOT_SPECIFIED, false);
				mState.updateStatus();
	        }
	    };
	}
	private static Handler mReScanHandler = null;
	public static BroadcastReceiver createScanListener(Handler reScanHandler)
	{
		mReScanHandler = reScanHandler;
		return new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            //MusicUtils.setSpinnerState(AlbumBrowserActivity.this);
	        	mReScanHandler.sendEmptyMessage(0);
	            if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
	                MediaPlayerUtil.clearAlbumArtCache();
	            }
	        }
		};
	}
	public static Handler createRescanHandler()
	{
		return new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	        	OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(
	        			ControlIDs.ID_NOT_SPECIFIED,false);
	        	
	//            if (mAdapter != null) {
	//                getAlbumCursor(mAdapter.getQueryHandler(), null);
	//            }
	        }
		};
    };
}
