package okosama.app.state;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateArtist extends absDisplayStateMediaTab {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// アーティスト別選択画面への切り替え
		tab.setCurrentTab(TabPage.TABPAGE_ID_ARTIST, true);
//		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_ARTIST );
//		action.doAction(null);
		return 0;
	}
	@Override
	public long updateDisplay() {
		long ret =  OkosamaMediaPlayerActivity.NO_REFRESH;
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(ControlIDs.TAB_ID_MEDIA, false);
		return ret;
	}
//	@Override
//	public int registerReceivers(int status) {
//		// TODO Auto-generated method stub
//		
//		
//		return 0;
//	}
}
