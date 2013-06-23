package okosama.app.action;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;
import android.os.Message;
import android.view.View;

/**
 * タブを選択した時に実行するアクション
 * @author 25689
 *
 */
public final class TabSelectAction implements IViewAction {

	public static final int MSG_ID_TAB_SELECT = 101;
	Tab tabRoot;
	int tabId = TabPage.TABPAGE_ID_UNKNOWN;

	public TabSelectAction(Tab tabRoot, int tabId) {
		super();
		this.tabRoot = tabRoot;
		this.tabId = tabId;
	}

	/**
	 * 
	 */
	@Override
	public int doAction( View v ) {
		if( tabRoot != null && tabId != TabPage.TABPAGE_ID_UNKNOWN )
		{
			// 案外高コストかもしれない
			// System.gc();
			tabRoot.setCurrentTab(tabId, true);
			// handlerに通知する
			Message msg = Message.obtain();
			msg.arg1 = MSG_ID_TAB_SELECT;			
			OkosamaMediaPlayerActivity.getHandler().sendMessage( msg );
		}
		return 0;
	}

}
