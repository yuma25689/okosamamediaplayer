package okosama.app.state;

import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStatePlaylist extends absDisplayStateMediaTab {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// プレイ選択画面への切り替え
		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_PLAYLIST );
		action.doAction(null);
		return 0;
	}
//	@Override
//	public int registerReceivers(int status) {
//		// TODO Auto-generated method stub
//	
//		return 0;
//	}
}
