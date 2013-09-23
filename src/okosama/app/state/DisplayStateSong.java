package okosama.app.state;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateSong extends absDisplayStateMediaTab {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// ã»ëIëâÊñ Ç÷ÇÃêÿÇËë÷Ç¶
		tab.setCurrentTab(TabPage.TABPAGE_ID_SONG, true);		
//		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_SONG );
//		action.doAction(null);
		return 0;
	}
	@Override
	public long updateDisplay() {
		long ret =  OkosamaMediaPlayerActivity.NO_REFRESH;
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(OkosamaMediaPlayerActivity.tabNameMain, false);
		return ret;
	}	
//	@Override
//	public int registerReceivers(int status) {
//		// TODO Auto-generated method stub
//		
//		return 0;
//	}
}
