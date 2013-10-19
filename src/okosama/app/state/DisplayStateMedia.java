package okosama.app.state;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateMedia extends absDisplayState {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// メディア選択画面への切り替え
		tab.setCurrentTab(TabPage.TABPAGE_ID_MEDIA, true);
//		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_MEDIA );
//		action.doAction(null);

		// サブ画面があるので、1を返却する
		return TabPage.TABPAGE_ID_MEDIA;
	}
	@Override
	public int registerReceivers(int status) {
		// このタブは、対象外
		return 1;
	}
	@Override
	public long updateDisplay() {
		// TODO Auto-generated method stub
		return OkosamaMediaPlayerActivity.NO_REFRESH;
	}
}
