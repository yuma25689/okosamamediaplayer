package okosama.app.action;
import okosama.app.service.MediaPlayer;
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
		MediaPlayer.prev();
        return 0;
	}

}
