package okosama.app.state;

import okosama.app.AppStatus;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateNone extends absDisplayState {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// 未選択へ切り替え
		tab.setCurrentTab(TabPage.TABPAGE_ID_NONE, true);		
//		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_NONE );
//		action.doAction(null);
		return TabPage.TABPAGE_ID_NONE;
	}
	@Override
	public int registerReceivers(int status) {
		// このタブは、リスナ登録対象外？
		// 怪しいので、後で確認すること
		return 1;
	}
	@Override
	public long updateDisplay() {
		// TODO Auto-generated method stub
		return AppStatus.NO_REFRESH;
	}
}
