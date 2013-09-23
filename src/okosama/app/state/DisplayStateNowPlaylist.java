package okosama.app.state;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateNowPlaylist extends absDisplayStateMediaTab {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// �ȑI����ʂւ̐؂�ւ�
		// �������^�u�I�������̖��[�H�����ςȍ�肾���E�E�E
		tab.setCurrentTab(TabPage.TABPAGE_ID_NOW_PLAYLIST, true);
		//IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_NOW_PLAYLIST );
		//action.doAction(null);
		return 0;
	}
	@Override
	public long updateDisplay() {
		long ret =  OkosamaMediaPlayerActivity.NO_REFRESH;
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(OkosamaMediaPlayerActivity.tabNameMain, false);
		return ret;
	}
	
}
