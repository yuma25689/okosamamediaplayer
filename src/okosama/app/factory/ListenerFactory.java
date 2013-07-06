package okosama.app.factory;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.service.MediaPlayerUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class ListenerFactory {
	/////////////////////// ƒŠƒXƒi
	public static BroadcastReceiver createTrackListener() 
	{
		return new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            //getListView().invalidateViews();
	            //MusicUtils.updateNowPlaying(AlbumBrowserActivity.this);
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
	        	((OkosamaMediaPlayerActivity)OkosamaMediaPlayerActivity.getResourceAccessor().getActivity()).reScanMedia(null,false);
	        	
	//            if (mAdapter != null) {
	//                getAlbumCursor(mAdapter.getQueryHandler(), null);
	//            }
	        }
		};
    };
}
