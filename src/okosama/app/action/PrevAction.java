package okosama.app.action;
import okosama.app.service.MediaPlayer;
import android.view.View;

/**
 * ���̋Ȃֈړ��A�N�V����
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
