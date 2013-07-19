package okosama.app.action;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.service.MediaPlayerUtil;
import android.os.Handler;
import android.os.Message;
import android.view.View;

/**
 * ŽŸ‚Ì‹È‚ÖˆÚ“®ƒAƒNƒVƒ‡ƒ“
 * @author 25689
 *
 */
public final class PrevAction implements IViewAction {

	/**
	 * 
	 */
	@Override
	public int doAction( View v ) {
		MediaPlayerUtil.prev();
        Handler handler = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getHandler();
        if( handler == null )
        {
        	return -1;
        }
        Message msg = handler.obtainMessage(OkosamaMediaPlayerActivity.REFRESH);
        handler.removeMessages(OkosamaMediaPlayerActivity.REFRESH);
        handler.sendMessageDelayed(msg, 1);
		
        return 0;
	}

}
