package okosama.app.state;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.factory.ListenerFactory;
import okosama.app.service.MediaPlaybackService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

public class absDisplayStateMediaTab extends absDisplayState {
	@Override
	public int registerReceivers(int status) {
		// TODO: èàóùì‡óeå©íºÇµ
		if( handlers == null 
		|| receivers == null )
		{
			return -1;
		}
		IntentFilter f = null;
		
		switch( status )
		{
		case STATUS_ON_CREATE:
	        Handler reScanHdr = ListenerFactory.createRescanHandler();
	        handlers.put( HDLER_NAME_RESCAN, reScanHdr );

	        f = new IntentFilter();
	        f.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
	        f.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
	        f.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
	        f.addDataScheme("file");
	        BroadcastReceiver scanListener = ListenerFactory.createScanListener( reScanHdr );
	        receivers.put( LSNER_NAME_SCAN, scanListener );
	        OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().registerReceiver(scanListener, f);

			break;
		case STATUS_ON_RESUME:
			f = new IntentFilter();
	        f.addAction(MediaPlaybackService.META_CHANGED);
	        f.addAction(MediaPlaybackService.QUEUE_CHANGED);
	        BroadcastReceiver mediaChangeListener = ListenerFactory.createMediaChangeListener(this);
	        receivers.put( LSNER_NAME_MEDIACHG, mediaChangeListener );
	        OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().registerReceiver(mediaChangeListener, f);
	        mediaChangeListener.onReceive(null, null);
	
	        // MusicUtils.setSpinnerState(this);
	        break;
		}
		return 0;
	}
	@Override
	public void unregisterReceivers(int status) {
		switch( status )
		{
		case STATUS_ON_PAUSE:
			// TODO: ä÷êîâª
			if( receivers.containsKey( LSNER_NAME_MEDIACHG ))
			{
				BroadcastReceiver brTrack = receivers.get( LSNER_NAME_MEDIACHG );
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().unregisterReceiver(brTrack);
		        receivers.remove(LSNER_NAME_MEDIACHG);
			}
			if( handlers.containsKey( HDLER_NAME_RESCAN ))
			{
		        Handler hdrRescan = handlers.get( HDLER_NAME_RESCAN );
		        hdrRescan.removeCallbacksAndMessages(null);
		        // ÉGÉìÉgÉäÇÕè¡Ç≥Ç»Ç¢
			}
			break;
		case STATUS_ON_DESTROY:
			clearReceivers();
			break;
		}
		
	}
	@Override
	public long updateDisplay() {
		// TODO Auto-generated method stub
		return OkosamaMediaPlayerActivity.NO_REFRESH;
	}	
}
