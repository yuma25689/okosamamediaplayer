package okosama.app.state;

import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateMedia extends absDisplayState {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// メディア選択画面への切り替え
		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_MEDIA );
		action.doAction(null);

		// サブ画面があるので、1を返却する
		return TabPage.TABPAGE_ID_MEDIA;
	}
	@Override
	public int registerReceivers(int status) {
		// このタブは、対象外
		return 1;
	}
}
