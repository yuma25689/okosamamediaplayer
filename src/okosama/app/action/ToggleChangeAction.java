package okosama.app.action;

import okosama.app.OkosamaMediaPlayerActivity;
import android.view.View;

/**
 * トグルボタンの値が変更された時に実行するアクション
 * @author 25689
 *
 */
public final class ToggleChangeAction implements IViewAction {

	boolean bVal;
	int toggleId = 0;
	public static final int TOGGLE_ID_EXTERNAL = 1;
	public static final int TOGGLE_ID_INTERNAL = 2;

	public ToggleChangeAction(int toggleId, boolean b) {
		super();
		this.bVal = b;
		this.toggleId = toggleId;
	}

	/**
	 * 
	 */
	@Override
	public int doAction( View v ) {
		switch( toggleId )
		{
		case TOGGLE_ID_INTERNAL:
			OkosamaMediaPlayerActivity.setInternalRef( bVal );
			break;
		case TOGGLE_ID_EXTERNAL:
			OkosamaMediaPlayerActivity.setExternalRef( bVal );
			break;
		}
		return 0;
	}

}
